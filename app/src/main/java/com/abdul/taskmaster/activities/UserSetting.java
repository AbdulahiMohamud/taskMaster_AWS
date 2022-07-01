package com.abdul.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.abdul.taskmaster.R;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        saveAndSetUsernameandTeam();
        saveButton();
        setupTeam();

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