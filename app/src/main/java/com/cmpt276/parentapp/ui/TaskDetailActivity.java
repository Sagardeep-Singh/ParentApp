package com.cmpt276.parentapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityTaskDetailBinding;
import com.cmpt276.parentapp.model.ParentAppDatabase;
import com.cmpt276.parentapp.model.TaskDao;
import com.cmpt276.parentapp.model.TaskWithChild;

public class TaskDetailActivity extends AppCompatActivity {

    public static final String TASK_ID_EXTRA = "TASK_ID_EXTRA";
    public static final int DEFAULT_VALUE = -1;
    public static final int MIN_ORDER = 0;

    private ActivityTaskDetailBinding binding;
    private TaskDao taskDao;
    private TaskWithChild taskWithChild;

    public static Intent getIntent(Context context, int taskId) {
        Intent i = new Intent(context, TaskDetailActivity.class);
        i.putExtra(TASK_ID_EXTRA, taskId);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskDetailBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        int id = getIntent().getIntExtra(TASK_ID_EXTRA, DEFAULT_VALUE);
        taskDao = ParentAppDatabase.getInstance(this).taskDao();

        setupToolbar();
        setupConfirmButton();
        setupTask(id);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btn_task_edit) {
            showTaskActivity();
            return true;
        } else if (item.getItemId() == R.id.btn_task_delete) {
            showDeleteTaskDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupConfirmButton() {
        binding.btnConfirmTask.setOnClickListener((v) -> new Thread(() -> {
            int taskId = taskWithChild.task.getTaskId();
            int order = taskDao.getNextOrder(taskId).blockingGet();

            taskDao.updateOrder(taskId, taskWithChild.child.getChildId(), order).blockingAwait();
            taskDao.decrementOrder(taskId, MIN_ORDER).blockingAwait();

            setupTask(taskId);
        }).start());
    }

    private void setupTask(int id) {
        new Thread(() -> {
            taskWithChild = taskDao
                    .getTaskWithNextChild(id)
                    .blockingGet();

            runOnUiThread(this::updateUI);
        }).start();
    }

    private void showTaskActivity() {
        Intent i = TaskActivity.getIntentForExistingTask(
                this,
                taskWithChild.task.getTaskId()
        );
        startActivity(i);
    }

    private void showDeleteTaskDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.warning_message)
                .setNegativeButton(R.string.no, null)
                .setMessage(getString(
                        R.string.confirm_delete_child_dialog_box_message,
                        taskWithChild.task.getName()
                ))
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> deleteTask())
                .create()
                .show();
    }

    private void deleteTask() {
        new Thread(() -> {
            try {
                taskDao.delete(this.taskWithChild.task).blockingAwait();

                Toast.makeText(this, "Deleted.", Toast.LENGTH_SHORT).show();
                runOnUiThread(this::finish);
            } catch (Exception e) {
                Log.i("Task Activity deletion", e.getMessage());
            }
        }).start();
    }


    private void updateUI() {
        if (taskWithChild != null) {
            binding.tvChildName.setText(taskWithChild.child.getName());
        }
    }
}