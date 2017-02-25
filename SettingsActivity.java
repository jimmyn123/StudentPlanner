/**
 * The settings activity class. It provides the option to send notification messages for
 * upcoming course due dates.
 * @author Jimmy Nguyen
 * @version 2/24/2017
 */
package com.example.studentplanner.studentplanner;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    /**
     * Creates the layout and opens the fragment containing the SharedPreferences.
     * @param savedInstanceState to load again if app crashes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Code to set the view as well as the actionbar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * Anonymous class that goes to the previous activity in the backstack.
             * @param view current view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Sets the back button on the ActionBar
        if(getSupportActionBar() != null ) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sets the title of the activity
        setTitle("Settings");

        // Opens the SharePreferences fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.content_settings, new SettingsFragment()).commit();
    }

}
