package com.abdul.taskmaster.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.abdul.taskmaster.R;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserSetting extends AppCompatActivity {
    // setup shared preferences for user
    SharedPreferences preferences;
    //set up share preference for team
    SharedPreferences teampreferences;
    // preference tag
    public static final  String USER_NAME_TAG = "userName";
    public static final String TAG = "UserSettingsActivity";
    public static final String TEAM = "CHOOSE TEAM";


    // Arrays for the team
    CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();
    ArrayList<String> teamName = new ArrayList<>();
    ArrayList<Team> team = new ArrayList<>();
    ActivityResultLauncher<Intent> activityResultLauncher;

    ByteArrayOutputStream byteArrayOutputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        saveAndSetUsernameandTeam();
        saveButton();
        setupTeam();

        activityResultLauncher = getImagePickingActivityResultLauncher();
        setUpAddimageBtn();

    }
    public void setUpAddimageBtn()
    {
        Button addImageBtn = findViewById(R.id.userImageButton);
        addImageBtn.setOnClickListener(v -> {
            launchImageSelectionIntent();
        });
    }

    public void launchImageSelectionIntent()
    {
        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/png"});

        activityResultLauncher.launch(imageFilePickingIntent);
    }

    public ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher()
    {
        ImageView userImage = findViewById(R.id.userImageView);
        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                Uri pickedImageUri = result.getData().getData();
                                try {
                                    InputStream pickedImageInputstream = getContentResolver().openInputStream(pickedImageUri);
                                    String pickedImageFileName = getFileNameFromUri(pickedImageUri);
                                    Bitmap bitmap = BitmapFactory.decodeStream(pickedImageInputstream);
                                    userImage.setImageBitmap(bitmap);

                                    Log.i(TAG, "Succeeded in getting input stream from a file on our phone");
                                } catch (FileNotFoundException fnfe)
                                {
                                    Log.e(TAG, "Could not get file from phone: " + fnfe.getMessage(), fnfe);
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

    public void uploadImage(InputStream data, String fileName)
    {

        Amplify.Storage.uploadInputStream(
                fileName,
                data,
                good -> Log.i(TAG,"S# uploaded file" + good.getKey()),
                bad -> Log.e(TAG,"failed to upload to S3" + bad.getMessage())
        );



    }

    public void setUpUserImage()
    {
        ImageView userImage = findViewById(R.id.userImageView);
    }




    public void saveAndSetUsernameandTeam(){
        // create the shared preferrence instant for username
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String userName = preferences.getString(USER_NAME_TAG,"");
        if(!userName.isEmpty()){
            EditText userNameEdited = findViewById(R.id.userNameInput);
            userNameEdited.setText(userName);
        }

        // create the shared preferrence instant for team spinner
        teampreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String teamName = preferences.getString(TEAM,"No Team Selected");
        if(!teamName.isEmpty()){
            Spinner teamSpinner = findViewById(R.id.settingsTeamAddspinner);
            teamSpinner.setSelected(false);

        }

//        uploadImage();


    }


    public void saveButton(){
        // get the button
        Button userSaveButton = findViewById(R.id.saveUserNameButton);
        // set an on click event
        userSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting up the editor
                SharedPreferences.Editor preferenceEditor = preferences.edit();
                // grab the edit text for the username
                EditText userNameText = findViewById(R.id.userNameInput);
                // change it into a string
                String userNameString = userNameText.getText().toString();
                // update the USER_NAME_TAG to the string got from the user.
                preferenceEditor.putString(USER_NAME_TAG,userNameString);
                // NOTHINGS SAVES UNLESS apply the changes!!!!!
                preferenceEditor.apply();

                // Shared Prefernce for the team spinner
                SharedPreferences.Editor teamPreferenceEditor = preferences.edit();
                Spinner teamSpinner = findViewById(R.id.settingsTeamAddspinner);
                // turn the selected item into a string
                String teamNameSelected = teamSpinner.getSelectedItem().toString();
                // update the final string TEAM to hold the inputed spinner
                teamPreferenceEditor.putString(TEAM,teamNameSelected);
                // NOTHINGS SAVES UNLESS apply the changes!!!!!
                teamPreferenceEditor.apply();


                // make a notification to know the button works
                Toast.makeText(UserSetting.this,"Setting Saved",Toast.LENGTH_SHORT).show();

                finish();

            }
        });
    }


    public void setupTeam() {
        Spinner addteamSpinner = findViewById(R.id.settingsTeamAddspinner);
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG,"team spinner created");

                    for (Team teams :success.getData()) {
                        team.add(teams);
                        teamName.add(teams.getTeamName());

                    }

                    teamFuture.complete(team);

                    runOnUiThread(() -> {
                        addteamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                teamName
                        ));
                    });

                },
                failure -> {
                    teamFuture.complete(null);
                    Log.e(TAG,"failed to set up Team Spinner due to:" + failure);
                }

        );
    }


}