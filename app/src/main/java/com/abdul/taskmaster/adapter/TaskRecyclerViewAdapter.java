package com.abdul.taskmaster.adapter;

import android.content.*;
import android.view.*;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdul.taskmaster.R;
import com.abdul.taskmaster.activities.MainActivity;
import com.abdul.taskmaster.activities.TaskDetails;
import com.amplifyframework.datastore.generated.model.TaskModel;

import java.util.List;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> {

    // had in the data using a constructor
    List<TaskModel> tasks;
    // hands over the activity context
    Context callingActivity;

    public TaskRecyclerViewAdapter(List<TaskModel> _tasks, Context _callingActivity) {
        this.tasks = _tasks;
        this.callingActivity = _callingActivity;

    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // creates a view from the xml in the parent
        View taskFragment = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_fragments,parent,false);
        // attach fragment to view holder
        return new TaskViewHolder(taskFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        // bind data items to fragments inside of View holder, get the textview of a fragment
        TextView taskFragmentTextView = holder.itemView.findViewById(R.id.taskFragmentsTextView);
        // get the name from the list
        // position is the index of when the data lies in the recycler List and is given.
        TaskModel taskList = tasks.get(position);
        // set the fragment text view to  the task name
        taskFragmentTextView.setText((position + 1) +" :"+ taskList.getName() + "\n" + taskList.getCreatedAt()+
                "\n" + taskList.getState());

        // bind the onclick listener
        View taskViewHolder = holder.itemView;
        taskViewHolder.setOnClickListener(v -> {
            Intent goToTaskDetails = new Intent(callingActivity, TaskDetails.class);
            goToTaskDetails.putExtra(MainActivity.TASK_TITLE,taskList.getName());
            goToTaskDetails.putExtra(MainActivity.TASK_DESCRIPTION,taskList.getDescription());
            goToTaskDetails.putExtra(MainActivity.TASK_STATUS,taskList.getState().toString());
            goToTaskDetails.putExtra(MainActivity.TASK_ID,taskList.getId());
            goToTaskDetails.putExtra(MainActivity.TASK_IMAGE_TAG,taskList.getTaskImageS3Key());


            callingActivity.startActivity(goToTaskDetails);
        });



    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    // make a view holder class to hold a fragment
    public static class TaskViewHolder extends RecyclerView.ViewHolder{

        public TaskViewHolder(View fragmentItemView) {
            super(fragmentItemView);
        }
    }
}
