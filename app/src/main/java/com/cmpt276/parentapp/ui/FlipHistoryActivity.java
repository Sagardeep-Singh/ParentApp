package com.cmpt276.parentapp.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.model.ChildManager;
import com.cmpt276.parentapp.model.FlipHistoryManager;
import com.cmpt276.parentapp.model.CoinFlip;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Activity to print list of all flips that have happened
 */

public class FlipHistoryActivity extends AppCompatActivity {

    FlipHistoryManager flipHistoryManager;
    ChildManager childManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_history);

        flipHistoryManager = FlipHistoryManager.getInstance(FlipHistoryActivity.this);
        childManager = ChildManager.getInstance(FlipHistoryActivity.this);
        populateListView();
        setUpToolbar();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, FlipHistoryActivity.class);
    }

    private void populateListView() {
        ArrayAdapter<CoinFlip> adapter = new MyListAdapter();
        ListView historyList = findViewById(R.id.flip_history_list);
        historyList.setAdapter(adapter);
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.flip_history_activity_toolbar_label);

        // set up "UP" button on toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private class MyListAdapter extends ArrayAdapter<CoinFlip> {
        public MyListAdapter() {
            super(
                    FlipHistoryActivity.this,
                    R.layout.flip_history_view,
                    flipHistoryManager.getFullHistory()
            );
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(
                        R.layout.flip_history_view, parent, false
                );
            }
            final String DATE_FORMAT = "MMM - dd @ KK:mma";

            CoinFlip currentFlip = flipHistoryManager.getFlip(position);

            TextView dateText = itemView.findViewById(R.id.game_date_flip_information_tv);
            String date = DateTimeFormatter.ofPattern(DATE_FORMAT).format(currentFlip.getFlipTime());
            dateText.setText(date);

            TextView childNameText = itemView.findViewById(R.id.child_name_flip_information_tv);
            if (currentFlip.getChild() == null) {
                childNameText.setText("");
            } else {
                childNameText.setText(currentFlip.getChild().getChildName());
            }

            TextView childChoiceText = itemView.findViewById(R.id.child_choice_flip_information_tv);
            childChoiceText.setText(getString(R.string.child_choice_for_flip, currentFlip.getChoice(),
                    currentFlip.isWinner() ? getString(R.string.child_won) : getString(R.string.child_lost)));

            ImageView guessImage = itemView.findViewById(R.id.child_guess_image_flip_information_iv);
            if (currentFlip.isWinner()) {
                guessImage.setImageResource(R.drawable.child_guess_correct);
            } else {
                guessImage.setImageResource(R.drawable.child_guess_wrong);
            }
            return itemView;
        }
    }

}