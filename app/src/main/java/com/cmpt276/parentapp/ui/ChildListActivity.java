package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChildListActivity extends AppCompatActivity {

    ChildManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_list);

        manager = ChildManager.getInstance();

        setUpToolbar();
        setUpNewGameButton();
        populateListView();
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateUI();
    }
    private void updateUI(){
        populateListView();
    }

    private void populateListView(){
        // Build the adapter
        ArrayAdapter<Child> adapter = new MyListAdapter();

        // Configure the list view
        ListView childList = findViewById(R.id.child_list_view);
        childList.setAdapter(adapter);
    }

    // MyListAdapter that will help make the complex list view
    private class MyListAdapter extends  ArrayAdapter<Child>{
        public MyListAdapter(){
            super(ChildListActivity.this, R.layout.child_name_view, manager.getAllChildren());
        }
        public View getView(int position, View convertView, ViewGroup parent){

            // make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.child_name_view, parent, false);
            }

            // Find the child to work with
            Child currentChild = manager.retrieveChildByIndex(position);

            // Fill the view
            TextView childNameText = itemView.findViewById(R.id.child_name_text_view);
            childNameText.setText(currentChild.getChildName());

            return itemView;
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getText(R.string.child_list_activity_toolbar_label));
    }

    private void setUpNewGameButton() {
        FloatingActionButton addNewChildFabButton = findViewById(R.id.add_child_fab);
        addNewChildFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChildListActivity.this, "This button will let you add a new child", Toast.LENGTH_SHORT).show();
                startActivity(AddChildActivity.getIntent(ChildListActivity.this));
            }
        });
    }
}