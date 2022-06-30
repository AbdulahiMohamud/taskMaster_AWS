package com.abdul.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.abdul.taskmaster.R;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.*;


import java.util.Date;

public class AddTask extends AppCompatActivity {

    public static final String TAG = "ADD TASK";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tasks);

        setUpSpinner();
        setUpAddButton();


    }



    private void setUpSpinner(){
        Spinner statusSpinner = findViewById(R.id.taskStateSpinner);
        statusSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                StateEnum.values()
        ));


    }

    private void setUpAddButton(){
        Spinner statusSpinner = findViewById(R.id.taskStateSpinner);
        Button addTask = findViewById(R.id.addTaskOnAddTaskPageButton);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView headerChange = AddTask.this.findViewById(R.id.submittedText);
                String name = ((EditText)findViewById(R.id.taskTitleInput)).getText().toString();
                String description = ((EditText)findViewById(R.id.taskDescriptionInput)).getText().toString();
                String currentDate = com.amazonaws.util.DateUtils.formatISO8601Date(new Date());
                headerChange.setText("Submitted!");

                TaskModel newTask = TaskModel.builder()
                        .name(name)
                        .description(description)
                        .state((StateEnum)statusSpinner.getSelectedItem())
                        .dateCreated(new Temporal.DateTime(currentDate))
                        .build();

                Amplify.API.mutate(
                        ModelMutation.create(newTask),
                        successResponce -> Log.i(TAG,"AddTaskActivity.onClick: made a Task"),
                        failureResponse -> Log.i(TAG, "AddTaskActivity.onClick: failed" + failureResponse)
                );

//                database.taskDao().insert(newTask);
//                Intent goHome = new Intent(AddTask.this,MainActivity.class);
//                startActivity(goHome);
            }
        });
    }
}