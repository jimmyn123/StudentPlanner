/**
 * The settings activity class. It provides the option to send notification messages for
 * upcoming course due dates.
 *
 * @author Jimmy Nguyen
 * @version 3/6/2017
 */
package com.example.studentplanner.studentplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Calendar;

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
             * Anonymous class that goes to the previous activity in the BackStack.
             * @param view current view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Sets the back button on the ActionBar
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sets the title of the activity
        setTitle("Settings");

        // Opens the SharePreferences fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.content_settings, new SettingsFragment()).commit();
    }

    /**
     * When the activity stops, make sure to set the alarms and cancels alarms depending on the
     * set SharedPreferences.
     */
    @Override
    protected void onStop() {
        // Gets the SharedPreferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notifications = sp.getBoolean("notifications", true);

        // Queries the database for the courses and end dates
        Cursor cursor = getContentResolver().query(
                ScheduleProvider.CONTENT_COURSES_URI, null, null, null, null);

        // This only runs if there is a result
        if (cursor != null) {
            try { // Runs only if there is something in cursor
                int alarmID = 0;
                while (cursor.moveToNext()) {
                    // Gets today's date
                    Calendar today = Calendar.getInstance();

                    // Creates a calendar to hold current instance
                    Calendar alarm = Calendar.getInstance();

                    // Gets the end date to set reminder
                    String[] date = cursor.getString(
                            cursor.getColumnIndex(DBOpenHelper.COURSE_END)).split("/");
                    // Sets the date of the reminder
                    alarm.set(Integer.parseInt(date[2]),
                            (Integer.parseInt(date[0]) - 1), Integer.parseInt(date[1]));

                    // New intent and to get a PendingIntent
                    Intent intent = new Intent(this, AlarmReceiver.class);
                    // Adds the name nad alarm ID into the extras
                    intent.putExtra("course",
                            cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NAME)));
                    intent.putExtra("alarmID", alarmID);
                    // Gets a PendingIntent that sends a broadcast
                    PendingIntent pi = PendingIntent.getBroadcast(this, alarmID, intent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

                    // Gets an alarm manager and cancels every set alarm
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    am.cancel(pi);
                    // Sets alarm if the date has not passed yet.
                    if (alarm.after(today)) {
                        // Only sets a new alarm if notifications is set to true in preferences
                        alarm.add(Calendar.DAY_OF_MONTH, -1);
                        if (notifications) am.set(AlarmManager.RTC, alarm.getTimeInMillis(), pi);
                    }
                    // Increments for the alarmID
                    alarmID += 1;
                }
            } finally {
                // Closes the cursor
                cursor.close();
            }
        }
        super.onStop();
    }
}
