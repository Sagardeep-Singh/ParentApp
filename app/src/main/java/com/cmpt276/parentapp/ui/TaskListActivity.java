package com.cmpt276.parentapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityTaskListBinding;
import com.cmpt276.parentapp.model.ParentAppDatabase;
import com.cmpt276.parentapp.model.Task;
import com.cmpt276.parentapp.model.TaskDao;

import java.util.List;

/**
 * +
 * Activity lists all created tasks
 * Redirects to create new task screen and confirm child for a task when appropriate
 */

public class TaskListActivity extends AppCompatActivity {

    private ActivityTaskListBinding binding;

    public static Intent getIntent(Context context) {
        return new Intent(context, TaskListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskListBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());
        populateTaskRecyclerView();
        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btn_add_task) {
            startActivity(
                    TaskActivity.getIntentForNewTask(this)
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateTaskRecyclerView() {
        new Thread(() -> {
            TaskDao TaskDao = ParentAppDatabase.getInstance(this).taskDao();

            List<Task> list = TaskDao.getAll().blockingGet();

            TaskListAdapter adapter = new TaskListAdapter(list);
            runOnUiThread(() -> binding.rvTaskList.setAdapter(adapter));
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateTaskRecyclerView();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    class TaskListAdapter extends ArrayAdapter<Task> {
        List<Task> taskList;

        public TaskListAdapter(List<Task> taskList) {
            super(TaskListActivity.this,
                    R.layout.task_list_item,
                    taskList);
            this.taskList = taskList;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater()
                        .inflate(
                                R.layout.task_list_item,
                                parent,
                                false
                        );
            }
            Task task = taskList.get(position);

            TextView taskNameText = itemView.findViewById(R.id.tv_task_name);
            taskNameText.setText(task.getName());

            itemView.setOnClickListener(v -> {
                Intent i = TaskActivity.getIntentForExistingTask(TaskListActivity.this, task.getTaskId());
                startActivity(i);
            });
            return itemView;
        }
    }
}