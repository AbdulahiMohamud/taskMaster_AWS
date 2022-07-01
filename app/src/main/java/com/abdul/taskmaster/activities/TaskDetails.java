package com.abdul.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.abdul.taskmaster.R;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskModel;

public class TaskDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        setUpTaskTitle();
        setUpTaskDate();
        setUpTaskDescription();
        setUpTaskStatus();
        setUpdelete();
    }

    private void setUpTaskTitle(){

        Intent callingIntent = getIntent();
        String taskTitleString = null;
        if(callingIntent != null){
            taskTitleString = callingIntent.getStringExtra(MainActivity.TASK_TITLE);
        }
        TextView taskTitle = findViewById(R.id.taskTitleText);
        if(taskTitle != null){
            taskTitle.setText(taskTitleString);
        }
        else {
            taskTitle.setText(R.string.no_task_title);
        }


    }
    private void setUpTaskDescription(){

        Intent callingIntent = getIntent();
        String taskDescriptionString = null;
        if(callingIntent != null){
            taskDescriptionString = callingIntent.getStringExtra(MainActivity.TASK_DESCRIPTION);
        }
        TextView taskDescription = findViewById(R.id.tasksDescripOnDetails);
        if(taskDescription != null){
            taskDescription.setText(taskDescriptionString);
        }
        else {

            taskDescription.setText(R.string.no_task_title);

        }


    }

    private void setUpTaskStatus(){

        Intent callingIntent = getIntent();
        String taskStatus = null;
        if(callingIntent != null){
            taskStatus = callingIntent.getStringExtra(MainActivity.TASK_STATUS);
        }
        TextView staus = findViewById(R.id.taskDetailStatus);
        if(staus != null){
            staus.setText(taskStatus);
        }
        else {

            staus.setText(R.string.no_task_title);

        }


    }

    private void setUpTaskDate(){

        Intent callingIntent = getIntent();
        String taskDateString = null;
        if(callingIntent != null){
            taskDateString = callingIntent.getStringExtra(MainActivity.TASK_CREATION);
        }
        TextView taskDate = findViewById(R.id.taskDateMadeOnDeatils);
        if(taskDate != null){
            taskDate.setText(taskDateString);
        }
        else {
            taskDate.setText(R.string.no_task_title);
        }


    }

    private void setUpdelete(){
        Intent callingIntent = getIntent();
        Button deleteBtn = findViewById(R.id.deleteButton);
        String taskId = null;
        if(callingIntent != null){
            taskId = callingIntent.getStringExtra(MainActivity.TASK_ID);
        }
        String finalTaskId = taskId;
        deleteBtn.setOnClickListener(v -> {
            Amplify.API.query(
                    ModelQuery.get(TaskModel.class, finalTaskId),
                    res -> {
                        Log.i("TaskDetailsAvtivity","successfully got task");
                        Amplify.API.mutate(
                                ModelMutation.delete(res.getData()),
                                success -> Log.i("TaskDetailsAvtivity","successfully deleted task"),
                                fail -> Log.e("TaskDetailsAvtivity","failed deleted task")

                        );
                    },
                    fail -> Log.e("TaskDetailsAvtivity","failed deleted task")
            );
            Toast.makeText(TaskDetails.this,"Task Deleted",Toast.LENGTH_SHORT).show();

            finish();
        });
    }


}