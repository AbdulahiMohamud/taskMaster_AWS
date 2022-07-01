package com.abdul.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

    // spinners



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tasks);

        setUpSpinner();
        setUpAddButton();


    }



    private void setUpSpinner(){
        Spinner statusSpinner = findViewById(R.id.taskStateSpinner);
        Spinner teamSpinner = findViewById(R.id.teamAddSpinner);


        // amplify Api qury to read it for the team spinner
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG,"Read team Successfully");


                    for (Team teams :success.getData()) {
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
                    Log.e(TAG,"failed to set up Team Spinner due to:" + failure);
                }
        );

        statusSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                StateEnum.values()
        ));


    }

    private void setUpAddButton(){
        Spinner statusSpinner = findViewById(R.id.taskStateSpinner);
        Button addTask = findViewById(R.id.addTaskOnAddTaskPageButton);
        Spinner teamSpinner = findViewById(R.id.teamAddSpinner);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView headerChange = AddTask.this.findViewById(R.id.submittedText);
                String name = ((EditText)findViewById(R.id.taskTitleInput)).getText().toString();
                String description = ((EditText)findViewById(R.id.taskDescriptionInput)).getText().toString();
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
                        .findAny().orElseThrow(RuntimeException ::new);


                TaskModel newTask = TaskModel.builder()
                        .name(name)
                        .description(description)
                        .state((StateEnum)statusSpinner.getSelectedItem())
                        .dateCreated(new Temporal.DateTime(currentDate))
                        .team(selectedTeam)
                        .build();

                Amplify.API.mutate(
                        ModelMutation.create(newTask),
                        successResponce -> Log.i(TAG,"AddTaskActivity.onClick: made a Task"),
                        failureResponse -> Log.e(TAG, "AddTaskActivity.onClick: failed" + failureResponse)
                );


//                database.taskDao().insert(newTask);
//                Intent goHome = new Intent(AddTask.this,MainActivity.class);
//                startActivity(goHome);
                Toast.makeText(AddTask.this,"Task Saved",Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }
}