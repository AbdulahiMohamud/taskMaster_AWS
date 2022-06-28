package com.abdul.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.abdul.taskmaster.R;
import com.abdul.taskmaster.model.StateEnum;
import com.abdul.taskmaster.model.TaskModel;

import java.util.Date;

public class AddTask extends AppCompatActivity {



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
                headerChange.setText("Submitted!");

                TaskModel newTask = new TaskModel(
                        ((EditText)findViewById(R.id.taskTitleInput)).getText().toString(),
                        ((EditText)findViewById(R.id.taskDescriptionInput)).getText().toString(),
                        new Date(),
                        StateEnum.fromString(statusSpinner.getSelectedItem().toString())

                );
//                database.taskDao().insert(newTask);
//                Intent goHome = new Intent(AddTask.this,MainActivity.class);
//                startActivity(goHome);
            }
        });
    }
}