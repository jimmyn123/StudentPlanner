/**
 * The Activity for adding and displaying the details of a course to the DB
 * @author Jimmy Nguyen
 * @version 3/1/2017
 */
package com.example.studentplanner.studentplanner;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddCoursesActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    // sets fields that will be used in multiple methods
    private EditText courseNameEditor, startEditor, endEditor, statusEditor, dateDisplay;
    private String action, filter, startDate, endDate;
    private boolean start;

    /**
     * Shows the add form if it is adding a new entry.
     * Shows the saved details if it is loading an entry.
     * @param savedInstanceState used to reload if the app breaks
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Code to set the view as well as the actionbar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_courses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Sets the  floating action button to open the addTerms activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * Anonymouse class implementation of what happens when you click the FAB
             * @param view current view
             */
            @Override
            public void onClick(View view) {
                finishEditing();
            }
        });

        // Sets the back button on the ActionBar
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sets the title of the activity
        setTitle("Add and save your courses");

        // Finds all of the views and assigns it to a variable for easier access
        courseNameEditor = (EditText) findViewById(R.id.courseName);
        startEditor = (EditText) findViewById(R.id.startDateCourses);
        endEditor = (EditText) findViewById(R.id.endDateCourses);
        statusEditor = (EditText) findViewById(R.id.courseStatus);

        // Finds the mentors button and opens the mentors activity when clicked
        Button mentors = (Button) findViewById(R.id.button_mentors);
        mentors.setOnClickListener(new View.OnClickListener() {
            /**
             * Anonymous class that opens the mentors activity.
             * @param v the view
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCoursesActivity.this, MentorsActivity.class);
                startActivity(intent);
            }
        });

        // Finds the assessments button and opens the assessments activity when clicked
        Button assessments = (Button) findViewById(R.id.button_assessments);
        assessments.setOnClickListener(new View.OnClickListener() {
            /**
             * Anonymous class that opens the assessments activity.
             * @param v the view
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCoursesActivity.this, AssessmentsActivity.class);
                startActivity(intent);
            }
        });

        // Get the contents of the terms table and put it in a cursor
        Cursor cursor = getContentResolver().query(ScheduleProvider.CONTENT_TERMS_URI,
                null, null, null, null);
        // Create and populate an array of terms using the cursor
        List<Integer> terms = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    terms.add(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_NUMBER)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        // Create an adapter for the data and place it in a pre-defined layout
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, terms);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        // Finds the spinner view and sets the adapter to display the data
        Spinner spinner = (Spinner) findViewById(R.id.spinnerTerms);
        spinner.setAdapter(adapter);

        // Gets the intent from the previous activity
        Intent intent = getIntent();
        // Gets the extra content to determine if this is a specific item
        Uri uri = intent.getParcelableExtra(ScheduleProvider.CONTENT_COURSE_TYPE);
        if (uri == null) {
            // Not a specific item, so not editing but inserting
            action = Intent.ACTION_INSERT;
            setTitle("Add A New Course");
        } else {
            // This loads the saved information to edit
            action = Intent.ACTION_EDIT;
            setTitle("Edit Course");

            // The filter is which row of data to load from
            filter = DBOpenHelper.COURSE_ID + "=" + uri.getLastPathSegment();
            cursor = getContentResolver().query(uri, DBOpenHelper.COURSES_COLUMNS,
                    filter, null, null);
            // Make sure there is data
            if(cursor!=null) {
                // Move to the beginning and load all of the data from the DB to the views
                cursor.moveToFirst();

                String courseName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NAME));
                courseNameEditor.setText(courseName);

                startDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START));
                startEditor.setText(startDate);

                endDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));
                endEditor.setText(endDate);

                String courseStatus = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_STATUS));
                statusEditor.setText(courseStatus);

                // This sets the spinner to the specific term
                spinner.setSelection(adapter.getPosition(cursor.getInt(
                        cursor.getColumnIndex(DBOpenHelper.COURSE_TERM_ID))));

                // Closing the resource
                cursor.close();
            }
        }
    }

    /**
     * Sets the text of each menu item appropriately.
     * @param menu the menu on the activity
     * @return returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        menu.getItem(0).setTitle("Save Course");
        menu.getItem(1).setTitle("Delete Course");
        return true;
    }

    /**
     * Provides the navigation/actions to each menu item.
     * @param item the selected item in the menu
     * @return returns true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Calls different actions to open activities
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteCourse();
                break;
            default:
                onBackPressed();
        }
        return true;
    }

    /**
     * Finishes the editing and saves/deletes.
     */
    @Override
    public void onBackPressed() {
        finishEditing();
    }

    /**
     * Helper method that will insert/delete or update the data.
     */
    private void finishEditing() {
        // Gets the course from the editor
        String fromEditor = courseNameEditor.getText().toString();
        switch (action) {
            case Intent.ACTION_INSERT:
                // If the action is insert and the course isn't blank, add course
                if (fromEditor.length() != 0) insertCourse();
                break;
            case Intent.ACTION_EDIT:
                // If the action is edit and the course is blank, delete, else update
                if (fromEditor.length() == 0) {
                    deleteCourse();
                } else {
                    updateCourse();
                }
                break;
        }
        finish();
    }

    /**
     * Helper function that inserts the values from the screen using the set ContentResolver.
     */
    private void insertCourse() {
        ContentValues cv = getValues();
        getContentResolver().insert(ScheduleProvider.CONTENT_COURSES_URI, cv);
    }

    /**
     * Helper function that updates the selection from the screen using the set ContentResolver.
     */
    private void updateCourse() {
        ContentValues cv = getValues();
        getContentResolver().update(ScheduleProvider.CONTENT_COURSES_URI, cv, filter, null);
    }

    /**
     * Helper function that deletes the selection from the screen using the set ContentResolver.
     */
    private void deleteCourse() {
        getContentResolver().delete(ScheduleProvider.CONTENT_COURSES_URI, filter, null);
        updateAlarms();
        finish();
    }

    /**
     * Helper function that gets the contents of the form and returns it in a ContentValue.
     * @return ContentValue object of all the values
     */
    private ContentValues getValues() {
        ContentValues cv = new ContentValues();
        // Puts in the values in the ContentValues object
        Spinner terms = (Spinner) findViewById(R.id.spinnerTerms);
        cv.put(DBOpenHelper.COURSE_NAME, courseNameEditor.getText().toString());
        cv.put(DBOpenHelper.COURSE_TERM_ID, Integer.parseInt(terms.getSelectedItem().toString()));
        cv.put(DBOpenHelper.COURSE_START, startDate);
        cv.put(DBOpenHelper.COURSE_END, endDate);
        cv.put(DBOpenHelper.COURSE_STATUS, statusEditor.getText().toString());
        return cv;
    }

    /**
     * Helper function that resets the alarms if an item was modified or deleted.
     */
    private void updateAlarms() {
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
                    if(notifications) am.set(AlarmManager.RTC, c.getTimeInMillis() + 10000, pi);
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
     * Opens a DatePicker each time a specific button is clicked.
     * @param view input view
     */
    public void openDateCourses(View view) {
        // Finds the name of the clicked button and parses it
        String name = getResources()
                .getResourceEntryName(view.getId()).substring(0, 3).trim().toLowerCase();
        // Sets which text editor to display the date in after selected
        if (name.equals("end")) {
            dateDisplay = endEditor;
            start = false;
        } else {
            dateDisplay = startEditor;
            start = true;
        }

        // Creates a new DatePicker and adds an argument to tell which activity is calling it
        DatePickerFragment dpf = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("activity", 2);
        dpf.setArguments(args);
        dpf.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * The onDateSet listener. Responds when a date has been set on the dialogue.
     * @param view The DatePicker view
     * @param year year selected
     * @param month month selected
     * @param dayOfMonth day selected
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Creates a new format that can be saved into SQLiteDatabase
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar d = Calendar.getInstance();
        d.set(year, month, dayOfMonth);
        // Gets the selected date and formats
        String date = sdf.format(d.getTime());
        // Sets the start or end date appropriately
        if (start) {
            startDate = date;
        } else {
            endDate = date;
        }
        // Displays the date on the textView
        dateDisplay.setText(date);
    }
}
