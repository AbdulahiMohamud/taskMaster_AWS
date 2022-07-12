package com.abdul.taskmaster.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abdul.taskmaster.R;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddTask extends AppCompatActivity {

    public static final String TAG = "ADDTASKActivity";

    // Arrays for the team
    CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();
    ArrayList<String> teamName = new ArrayList<>();
    List<Team> team = new ArrayList<>();
    ActivityResultLauncher<Intent> activityResultLauncher;
    String imageS3Key = "";
    FusedLocationProviderClient locationProviderClient = null;


    // spinners


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tasks);

        activityResultLauncher = getImagePickingActivityResultLauncher();

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationProviderClient.flushLocations();

        setUpAddimageBtn();
        setUpAddButton();
        setUpSpinner();


    }

    public void setUpAddimageBtn() {
        Button addImageBtn = findViewById(R.id.taskAddImageBtn);
        addImageBtn.setOnClickListener(v -> {
            launchImageSelectionIntent();
        });
    }

    public void launchImageSelectionIntent() {
        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/png"});

        activityResultLauncher.launch(imageFilePickingIntent);
    }

    public ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher() {
        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (result.getResultCode() == Activity.RESULT_OK) {
                                    if (result.getData() != null) {
                                        Uri pickedImageFileUri = result.getData().getData();
                                        try {
                                            InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                            String pickedImageFilename = getFileNameFromUri(pickedImageFileUri);

                                            Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
                                            // Part 3: Use our InputStream to upload file to S3
                                            uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename, pickedImageFileUri);
                                        } catch (FileNotFoundException fnfe) {
                                            Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "Activity result error in ActivityResultLauncher.onActivityResult");
                                }
                            }
                        }
                );

        return imagePickingActivityResultLauncher;
    }


    // Taken from https://stackoverflow.com/a/25005243/16889809
    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadInputStreamToS3(InputStream inputStream, String filename, Uri uri) {
        Amplify.Storage.uploadInputStream(
                filename,
                inputStream,
                success ->
                {
                    imageS3Key = success.getKey();
                    setUpAddButton();
                    ImageView taskImageView = findViewById(R.id.taskImageView);

                    Log.i(TAG, "Succeeding in getting file uploaded to S3. key is: " + success.getKey());
                    InputStream copyInputStream = null;
                    try {
                        copyInputStream = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException fnfe) {
                        Log.e(TAG, "Could not get file from Uri. " + fnfe.getMessage(), fnfe);
                    }
                    taskImageView.setImageBitmap(BitmapFactory.decodeStream(copyInputStream));

                },
                failure ->
                {
                    Log.i(TAG, "Failure in uploading file to S3. Filename: " + filename + "with error: " + failure.getMessage());
                }
        );
    }


    private void setUpSpinner() {
        Spinner statusSpinner = findViewById(R.id.taskStateSpinner);
        Spinner teamSpinner = findViewById(R.id.teamAddSpinner);


        // amplify Api qury to read it for the team spinner
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "Read team Successfully");


                    for (Team teams : success.getData()) {
                        team.add(teams);
                        teamName.add(teams.getTeamName());

                    }

                    teamFuture.complete(team);

                    runOnUiThread(() -> {
                        teamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                teamName
                        ));
                    });

                },
                failure -> {
                    teamFuture.complete(null);
                    Log.e(TAG, "failed to set up Team Spinner due to:" + failure);
                }
        );

        statusSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                StateEnum.values()
        ));


    }

    private void setUpAddButton() {
        Spinner statusSpinner = findViewById(R.id.taskStateSpinner);
        Button addTask = findViewById(R.id.addTaskOnAddTaskPageButton);
        Spinner teamSpinner = findViewById(R.id.teamAddSpinner);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView headerChange = AddTask.this.findViewById(R.id.submittedText);
                String name = ((EditText) findViewById(R.id.taskTitleInput)).getText().toString();
                String description = ((EditText) findViewById(R.id.taskDescriptionInput)).getText().toString();
                String currentDate = com.amazonaws.util.DateUtils.formatISO8601Date(new Date());

                String selectedTeamString = teamSpinner.getSelectedItem().toString();

                try {
                    team = teamFuture.get();
                } catch (InterruptedException ie) {
                    Log.e(TAG, "InterruptedException while getting team");
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ee) {
                    Log.e(TAG, "ExecutionException while getting team");
                }

                Team selectedTeam = team.stream().filter(t -> t.getTeamName().equals(selectedTeamString))
                        .findAny().orElseThrow(RuntimeException::new);

                if (ActivityCompat.checkSelfPermission(AddTask.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(AddTask.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location == null) {
                        Log.e(TAG, "Location callback was null");
                    }
                    String currLat = Double.toString(location.getLatitude());
                    String currLong = Double.toString(location.getLongitude());

                    Log.i(TAG, "Long and lat  = " + currLong + currLat);

                    TaskModel newTask = TaskModel.builder()
                            .name(name)
                            .description(description)
                            .state((StateEnum) statusSpinner.getSelectedItem())
                            .dateCreated(new Temporal.DateTime(currentDate))
                            .team(selectedTeam)
                            .taskImageS3Key(imageS3Key)
                            .latitude(currLat)
                            .longitude(currLong)
                            .build();

                    Amplify.API.mutate(
                            ModelMutation.create(newTask),
                            successResponce -> Log.i(TAG, "AddTaskActivity.onClick: made a Task"),
                            failureResponse -> Log.e(TAG, "AddTaskActivity.onClick: failed" + failureResponse)
                    );
                }).addOnCanceledListener(() -> {
                    Log.e(TAG, "Location request was cancelled");
                }).addOnFailureListener(failure -> {
                    Log.e(TAG, "Location request failed Error was: " + failure.getMessage(), failure.getCause());
                }).addOnCompleteListener(complete -> {
                    Log.e(TAG, "Location request Completed");
                });


                Toast.makeText(AddTask.this,"Task Saved",Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }


}