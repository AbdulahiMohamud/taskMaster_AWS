package com.abdul.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.abdul.taskmaster.R;
import com.abdul.taskmaster.adapter.TaskRecyclerViewAdapter;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.datastore.generated.model.TaskModel;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.protobuf.AbstractMessageLite;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TASK_TITLE = "task title";
    public static final String TASK_DESCRIPTION = "task description";
    public static final String TASK_STATUS = "task status";
    public static final String TASK_CREATION = "task date";
    public static final String TAG = "MainActivity";
    public static final String TASK_ID = "task Id";

    SharedPreferences preferences;


    TaskRecyclerViewAdapter adapter;
    List<TaskModel> taskModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize shared preference
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // reroutes to other activities
        goToAddTaskBtn();
        goToAllTaskBtn();
        goToUSerSettingBtn();

        // make a Team

//        makeTeam();

        // tasks hard coded in task that re-route to task details activity
//        taskClean();
//        taskGym();
//        taskStudy();




        setUpTaskRecyclerView();



    }

    @Override
    protected void onResume(){
        super.onResume();

        updateUsername();

//        setUpFromDB();
        teamTaskList();

        // updating the recyclerview by clearing the lists and readding them from the database



    }


    public void goToAddTaskBtn(){
        // get button element by id
        Button addTaskButton = MainActivity.this.findViewById(R.id.addTaskButton);

        // set the event listener
        addTaskButton.setOnClickListener(v -> {
            Intent goToAddTaskFromIntent = new Intent(MainActivity.this, AddTask.class);
            startActivity(goToAddTaskFromIntent);
        });
    }


    public void goToAllTaskBtn(){
        // get button element by id
        Button allTaskButton = MainActivity.this.findViewById(R.id.allTaskButton);

        // set the event listener
        allTaskButton.setOnClickListener(v -> {
            Intent goToAllTaskFromIntent = new Intent(MainActivity.this, AllTasks.class);
            startActivity(goToAllTaskFromIntent);
        });
    }

    public void goToUSerSettingBtn() {
        // get button element by id
        ImageButton userSettingsButton = MainActivity.this.findViewById(R.id.goToUserSettingButton);

        // set the event listener
        userSettingsButton.setOnClickListener(v -> {
            Intent goToUserSetting = new Intent(MainActivity.this, UserSetting.class);
            startActivity(goToUserSetting);
        });
    }


//    public void taskClean(){
//        Button cleanTaskBtn = findViewById(R.id.cleanTaskButton);
//
//        cleanTaskBtn.setOnClickListener(v -> {
//            Intent goToTaskDetails = new Intent(MainActivity.this, TaskDetails.class);
//            // include an extra with the event
//            goToTaskDetails.putExtra(TASK_TITLE,cleanTaskBtn.getText().toString());
//            // start the activity
//            startActivity(goToTaskDetails);
//
//        });
//    }

//    public void taskGym(){
//        Button gymTaskBtn = findViewById(R.id.gymTaskButton);
//
//        gymTaskBtn.setOnClickListener(v -> {
//            Intent goToTaskDetails = new Intent(MainActivity.this, TaskDetails.class);
//            // include an extra with the event
//            goToTaskDetails.putExtra(TASK_TITLE,gymTaskBtn.getText().toString());
//            // start the activity
//            startActivity(goToTaskDetails);
//
//        });
//    }

//    public void taskStudy(){
//        Button studyTaskBtn = findViewById(R.id.studyTaskButton);
//
//        studyTaskBtn.setOnClickListener(v -> {
//            Intent goToTaskDetails = new Intent(MainActivity.this, TaskDetails.class);
//            // include an extra with the event
//            goToTaskDetails.putExtra(TASK_TITLE,studyTaskBtn.getText().toString());
//            // start the activity
//            startActivity(goToTaskDetails);
//
//        });
//    }

    public void updateUsername(){
        // get userName
        String userName = preferences.getString(UserSetting.USER_NAME_TAG,"No UserName");
        // formated the title
        String formatedUserNameTitle = String.format("%s's Tasks",userName);
        // set UserName to view
        TextView userNameText = findViewById(R.id.homeTaskTitle);
        userNameText.setText(formatedUserNameTitle);
    }

    public void setUpTaskRecyclerView() {
        // grab the recycler view by its id
        RecyclerView taskRecyclerView = findViewById(R.id.recyclerView);
        // set the layout manager of the recyclerview to a linearLayoutManeger
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        // set the layout manager
        taskRecyclerView.setLayoutManager(layoutManager);


        // create the data using the model

        // make the data


        //give  context to are adapter to reroute when Recycler view is clicked

        // create and attach the adapter
        adapter = new TaskRecyclerViewAdapter(taskModelList,this);
        //set the adapter to the recyclerview
        taskRecyclerView.setAdapter(adapter);


    }


    public void setUpFromDB(){
        Amplify.API.query(
                ModelQuery.list(TaskModel.class),
                success -> {
                    Log.i(TAG,"Task successFully created");
                    taskModelList.clear();
                    for (TaskModel databaseTask : success.getData()) {
                        taskModelList.add(databaseTask);
                    }
                    runOnUiThread(() ->{
                        adapter.notifyDataSetChanged();
                    });
                },
                fail -> Log.e(TAG,"Failed to Create Task")
        );

        setUpTaskRecyclerView();
    }


    public void makeTeam () {
        Team team1 = Team.builder()
                .teamName("java")
                .build();
        Amplify.API.mutate(
                ModelMutation.create(team1) ,
                success -> Log.i(TAG,"Created Team"),
                failure -> Log.e(TAG,"Failed to create Team due to:" + failure)
        );


        Team team2 = Team.builder()
                .teamName("python")
                .build();
        Amplify.API.mutate(
                ModelMutation.create(team2) ,
                success -> Log.i(TAG,"Created Team"),
                failure -> Log.e(TAG,"Failed to create Team due to:" + failure)
        );

        Team team3 = Team.builder()
                .teamName("C++")
                .build();
        Amplify.API.mutate(
                ModelMutation.create(team3) ,
                success -> Log.i(TAG,"Created Team"),
                failure -> Log.e(TAG,"Failed to create Team due to:" + failure)
        );

    }

    private void teamTaskList () {
        String currentTeam = preferences.getString(UserSetting.TEAM,"No Team");
        taskModelList.clear();
        Amplify.API.query(
                ModelQuery.list(TaskModel.class),
                success -> {
                    Log.i(TAG,"Read team tasks");
                    for (TaskModel taskModel : success.getData()) {
                        if (taskModel.getTeam().getTeamName().equals(currentTeam))
                            taskModelList.add(taskModel);
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                },
                failure -> Log.e(TAG,"Failed to read team tasks")

        );

        setUpTaskRecyclerView();
    }




}