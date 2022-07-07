package com.abdul.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.abdul.taskmaster.LoginActivity;
import com.abdul.taskmaster.R;
import com.abdul.taskmaster.SignupActivity;
import com.abdul.taskmaster.adapter.TaskRecyclerViewAdapter;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskModel;
import com.amplifyframework.datastore.generated.model.Team;

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

        setLoginBtn();
        setLogoutBtn();


        // make a Team

//        makeTeam();


        setUpTaskRecyclerView();

//        createUser();
//        verifyUser();


    }

    @Override
    protected void onResume() {
        super.onResume();

//        updateUsername();

        fetchUserName();
        teamTaskList();
        setLoginBtn();
        setLogoutBtn();


        // updating the recyclerview by clearing the lists and readding them from the database


    }


    public void goToAddTaskBtn() {
        // get button element by id
        Button addTaskButton = MainActivity.this.findViewById(R.id.addTaskButton);

        // set the event listener
        addTaskButton.setOnClickListener(v -> {
            Intent goToAddTaskFromIntent = new Intent(MainActivity.this, AddTask.class);
            startActivity(goToAddTaskFromIntent);
        });
    }


    public void goToAllTaskBtn() {
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


    public void updateUsername() {
        // get userName
        String userName = preferences.getString(UserSetting.USER_NAME_TAG, "No UserName");
        // formated the title
        String formatedUserNameTitle = String.format("%s's Tasks", userName);
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
        adapter = new TaskRecyclerViewAdapter(taskModelList, this);
        //set the adapter to the recyclerview
        taskRecyclerView.setAdapter(adapter);


    }


    public void makeTeam() {
        Team team1 = Team.builder()
                .teamName("java")
                .build();
        Amplify.API.mutate(
                ModelMutation.create(team1),
                success -> Log.i(TAG, "Created Team"),
                failure -> Log.e(TAG, "Failed to create Team due to:" + failure)
        );


        Team team2 = Team.builder()
                .teamName("python")
                .build();
        Amplify.API.mutate(
                ModelMutation.create(team2),
                success -> Log.i(TAG, "Created Team"),
                failure -> Log.e(TAG, "Failed to create Team due to:" + failure)
        );

        Team team3 = Team.builder()
                .teamName("C++")
                .build();
        Amplify.API.mutate(
                ModelMutation.create(team3),
                success -> Log.i(TAG, "Created Team"),
                failure -> Log.e(TAG, "Failed to create Team due to:" + failure)
        );

    }

    private void teamTaskList() {
        String currentTeam = preferences.getString(UserSetting.TEAM, "No Team");
        taskModelList.clear();
        Amplify.API.query(
                ModelQuery.list(TaskModel.class),
                success -> {
                    Log.i(TAG, "Read team tasks");
                    for (TaskModel taskModel : success.getData()) {
                        if (taskModel.getTeam().getTeamName().equals(currentTeam))
                            taskModelList.add(taskModel);
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                },
                failure -> Log.e(TAG, "Failed to read team tasks")

        );

        setUpTaskRecyclerView();
    }

    public void createUser() {
        Amplify.Auth.signUp(
                "abdulahimohamud22@gmail.com",
                "password",
                AuthSignUpOptions.builder()
                        .userAttribute(AuthUserAttributeKey.email(), "abdulahimohamud22@gmail.com")
                        .userAttribute(AuthUserAttributeKey.preferredUsername(), "Ballout")
                        .build(),
                success -> {
                    Log.i(TAG, "Signup succeeded " + success.toString());
                },
                failure -> {
                    Log.i(TAG, "Signup failed with message: " + failure.toString());
                }
        );
    }

    public void verifyUser() {
        Amplify.Auth.confirmSignUp(
                "abdulahimohamud22@gmail.com",
                "779161",

                success -> {
                    Log.i(TAG, "Verification succeeded: " + success.toString());
                },
                failure -> {
                    Log.i(TAG, "Verification failed: " + failure.toString());
                }


        );
    }


    public void fetchUserName() {
        AuthUser currentUser = Amplify.Auth.getCurrentUser();
        String preferredUserName = "";
        Button loginButton = findViewById(R.id.HomeLoginButton);
        Button logoutButton = findViewById(R.id.homeLogOutButton);

        if (currentUser == null) {
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.INVISIBLE);
        } else {
            Log.i(TAG, "onCreate: Username:  " + preferredUserName);
            loginButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.VISIBLE);

            Amplify.Auth.fetchUserAttributes(
                    success -> {
                        Log.i(TAG, "Fetch user attributes success! " + success.toString());
                        for (AuthUserAttribute authUserAttribute : success) {
                            if (authUserAttribute.getKey().getKeyString().equals("nickname")) {
                                String userNickname = authUserAttribute.getValue();
                                runOnUiThread(() -> {
                                    ((TextView) findViewById(R.id.homeTaskTitle)).setText(userNickname);

                                });
                            }

                        }

                    },
                    failure -> {
                        Log.i(TAG, "Fetch user atts failed: " + failure.toString());
                    }
            );
        }


    }

    public void setLoginBtn()
    {
        Button loginBtn = findViewById(R.id.HomeLoginButton);

        loginBtn.setOnClickListener(v -> {
            Intent goToLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(goToLoginActivity);
        });

    }

    public void setLogoutBtn()
    {
        Button logoutBtn = findViewById(R.id.homeLogOutButton);
        logoutBtn.setOnClickListener(v -> {
            Amplify.Auth.signOut(
                    () -> {
                        Log.i(TAG, "Logout succeeded!");
                    },
                    failure ->
                    {
                        Log.i(TAG, "Logout failed: " + failure.toString());
                    }
            );

            finish();

        });
    }


}