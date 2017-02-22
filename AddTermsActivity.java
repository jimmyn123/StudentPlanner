/**
 * The Activity for adding and displaying the details of a term to the DB
 * @author Jimmy Nguyen
 * @version 2/21/2017
 */
package com.example.studentplanner.studentplanner;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddTermsActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener{

    // sets fields that will be used in multiple methods
    private EditText termEditor, startEditor, endEditor, dateDisplay;
    private String action, termFilter, startDate, endDate;
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
        setContentView(R.layout.activity_add_terms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * Anonymous class implementation of what happens when you click the FAB.
             * @param view current view
             */
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Sets the back button on the ActionBar
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sets the title of the activity
        setTitle("Add and save your terms");

        // Finds all of the views and assigns it to a variable for easier access
        termEditor = (EditText) findViewById(R.id.editText);
        startEditor = (EditText) findViewById(R.id.startDateText);
        endEditor = (EditText) findViewById(R.id.endDateText);

        // Gets the intent from the previous activity
        Intent intent = getIntent();
        // Gets the extra content to determine if this is a specific item
        Uri uri = intent.getParcelableExtra(ScheduleProvider.CONTENT_TERM_TYPE);
        if(uri == null){
            // Not a specific item, so not editting but inserting
            action = Intent.ACTION_INSERT;
            setTitle("Add New Term");
        }
        else {
            // This loads the saved information to edit
            action = Intent.ACTION_EDIT;
            setTitle("Edit Existing Term");

            // The filter is which row of data to load from
            termFilter = DBOpenHelper.TERM_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.TERMS_COLUMNS,
                    termFilter, null, null);
            // Make sure there is data
            if(cursor != null){
                // Move to the beginning and load all of the data from the DB to the views
                cursor.moveToFirst();

                String oldTerm = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NUMBER));
                termEditor.setText(oldTerm);

                startDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
                startEditor.setText(startDate);

                endDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));
                endEditor.setText(endDate);

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
        menu.getItem(0).setTitle("Save term");
        menu.getItem(1).setTitle("Delete term");
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
        switch (item.getItemId()){
            case R.id.action_delete:
                deleteTerm();
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
        // Gets the term from the editor
        String fromEditor = termEditor.getText().toString().trim();
        switch(action) {
            case Intent.ACTION_INSERT:
                // If the action is insert and the term isn't blank, add term
                if(fromEditor.length() != 0){
                    int termNumber = Integer.parseInt(fromEditor);
                    insertTerm(termNumber);
                }
                break;
            case Intent.ACTION_EDIT:
                // If the action is edit and the term is blank, delete, else update
                if(fromEditor.length() == 0){
                    deleteTerm();
                } else {
                    int termNumber = Integer.parseInt(fromEditor);
                    updateTerm(termNumber);
                }
        }
        finish();
    }

    /**
     * Helper function that inserts the values from the screen using the set ContentResolver.
     */
    private void insertTerm(Integer termNumber){
        ContentValues values = getValues(termNumber);
        getContentResolver().insert(ScheduleProvider.CONTENT_TERMS_URI, values);
    }
    /**
     * Helper function that updates the selection from the screen using the set ContentResolver.
     */
    private void updateTerm(Integer termNumber){
        ContentValues values = getValues(termNumber);
        getContentResolver().update(ScheduleProvider.CONTENT_TERMS_URI, values, termFilter, null);
    }

    /**
     * Helper function that deletes the selection from the screen using the set ContentResolver.
     */
    private void deleteTerm(){
        getContentResolver().delete(ScheduleProvider.CONTENT_TERMS_URI, termFilter, null);
        finish();
    }

    /**
     * Helper function that gets the contents of the form and returns it in a ContentValue.
     * @return ContentValue object of all the values
     */
    private ContentValues getValues(Integer termNumber) {
        ContentValues values = new ContentValues();
        // Puts in the values in the ContentValues object
        values.put(DBOpenHelper.TERM_NUMBER, termNumber);
        values.put(DBOpenHelper.TERM_START, startDate);
        values.put(DBOpenHelper.TERM_END, endDate);
        return values;
    }

    /**
     * Opens a DatePicker each time a specific button is clicked.
     * @param view input view
     */
    public void openDateDialogue(View view) {
        // Finds the name of the clicked button and parses it
        String name = getResources()
                .getResourceEntryName(view.getId()).substring(0, 3).trim().toLowerCase();
        // Sets which text editor to display the date in after selected
        if (name.equals("end")){
            dateDisplay = endEditor;
            start = false;
        } else {
            dateDisplay = startEditor;
            start = true;
        }

        // Creates a new DatePicker and adds an argument to tell which activity is calling it
        DatePickerFragment dpf = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("activity", 1);
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
        if(start) {
            startDate = date;
        } else {
            endDate = date;
        }
        // Displays the date on the textView
        dateDisplay.setText(date);
    }

}
