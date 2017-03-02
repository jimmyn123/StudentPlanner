/**
 * The main activity class. Provides navigation to the other activities.
 * @author Jimmy nguyen
 * @version 3/1/2017
 */

package com.example.studentplanner.studentplanner;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    /**
     * Creates the user interface for the main activity.
     * @param savedInstanceState to load again if app crashes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Sets the title for the activity
        setTitle(getString(R.string.main_title));

        //Finds all of the buttons and sets what happens when you click them
        Button term = (Button) findViewById(R.id.button_Terms);
        term.setOnClickListener(new View.OnClickListener() {
            /**
             * Anonymous class that opens the terms activity.
             * @param v the view
             */
            @Override
            public void onClick(View v) {
                openTerms();
            }
        });

        Button courses = (Button) findViewById(R.id.button_Courses);
        courses.setOnClickListener(new View.OnClickListener() {
            /**
             * Anonymous class that opens the courses activity.
             * @param v the view
             */
            @Override
            public void onClick(View v) {
                openCourses();
            }
        });

        Button mentors = (Button) findViewById(R.id.button_Mentors);
        mentors.setOnClickListener(new View.OnClickListener(){
            /**
             * Anonymous class that opens the mentors activity.
             * @param v the view
             */
            @Override
            public void onClick(View v) {
                openMentors();
            }
        });

        Button assessments = (Button) findViewById(R.id.button_Assessments);
        assessments.setOnClickListener(new View.OnClickListener(){
            /**
             * Anonymous class that opens the assessments activity.
             * @param v the view
             */
            @Override
            public void onClick(View v) {
                openAssessments();
            }
        });

    }

    /**
     * When the activity starts, make sure to set the alarms and cancels alarms depending on the
     * set SharedPreferences.
     */
    @Override
    protected void onStart() {
        // Calls the super for onStart first
        super.onStart();

        // Gets the SharedPreferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notifications = sp.getBoolean("notifications",true);

        // Queries the database for the courses and end dates
        Cursor cursor = getContentResolver().query(
                ScheduleProvider.CONTENT_COURSES_URI, null, null, null, null);

        // This only runs if there is a result
        if (cursor != null) {
            try { // Runs only if there is something in cursor
                int alarmID = 0;
                while (cursor.moveToNext()) {
                    // Creates a calendar to hold current instance
                    Calendar c = Calendar.getInstance();

                    // Gets the end date to set reminder
                    String[] date = cursor.getString(
                            cursor.getColumnIndex(DBOpenHelper.COURSE_END)).split("/");
                    // Sets the date of the reminder
                    c.set(Integer.parseInt(date[2]),
                            (Integer.parseInt(date[1]) - 1), Integer.parseInt(date[0]));

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
                    // Only sets a new alarm if notifications is set to true in preferences
                    if(notifications) am.set(AlarmManager.RTC, c.getTimeInMillis(), pi);
                    // Increments for the alarmID
                    alarmID += 1;
                }
            } finally {
                // Closes the cursor
                cursor.close();
            }
        }
    }

    /**
     * Creates the menu on the top right hand corner.
     * @param menu the menu for the activity
     * @return returns true after the menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu and sets the text for each menu item
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(Menu.NONE, 0, Menu.NONE, "View Terms");
        menu.add(Menu.NONE, 1, Menu.NONE, "View Courses");
        menu.add(Menu.NONE, 2, Menu.NONE, "View Mentors");
        menu.add(Menu.NONE, 3, Menu.NONE, "View Assessments");
        menu.add(Menu.NONE, 4, Menu.NONE, "View Settings");
        return true;
    }

    /**
     * Directs to the correct activity when the menu item is clicked.
     * @param item each menu item
     * @return returns the boolean from super.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch & case using the item id to open the appropriate activities
        switch(item.getItemId()){
            case 0:
                openTerms();
                break;
            case 1:
                openCourses();
                break;
            case 2:
                openMentors();
                break;
            case 3:
                openAssessments();
                break;
            case 4:
                openSettings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper function to open terms.
     */
    private void openTerms() {
        Intent intent = new Intent(MainActivity.this, TermsActivity.class);
        startActivity(intent);
    }

    /**
     * Helper function to open courses activity.
     */
    private void openCourses() {
        Intent intent = new Intent(MainActivity.this, CoursesActivity.class);
        startActivity(intent);
    }

    /**
     * Helper function to open mentors activity.
     */
    private void openMentors() {
        Intent intent = new Intent(MainActivity.this, MentorsActivity.class);
        startActivity(intent);
    }

    /**
     * Helper function to open assessments activity.
     */
    private void openAssessments() {
        Intent intent = new Intent(MainActivity.this, AssessmentsActivity.class);
        startActivity(intent);
    }

    /**
     * Helper function to open assessments activity.
     */
    private void openSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
