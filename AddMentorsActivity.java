/**
 * The Activity for adding and displaying the details of a mentor to the DB
 * @author Jimmy Nguyen
 * @version 3/5/2017
 */
package com.example.studentplanner.studentplanner;

import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddMentorsActivity extends AppCompatActivity {

    // sets fields that will be used in multiple methods
    private String action, termFilter, coursesMentored;
    private EditText nameEditor, numberEditor, emailEditor;
    private TextView coursesEditor;
    private Button addCourses;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private Boolean resetButton = false;
    private Drawable editTextBackground;

    // Regex for name pattern
    private final String NAME_PATTERN =
            "[_A-Za-z-\'\\s\\+]+";

    // Regex for phone pattern
    private final String PHONE_PATTERN = "[0-9]{10}";

    // Regex for email pattern
    private final String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

/*            "^[_A-Za-z-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";*/

    /**
     * Shows the add form if it is adding a new entry.
     * Shows the saved details if it is loading an entry.
     * @param savedInstanceState used to reload if the app breaks
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Code to set the view as well as the actionbar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mentors);
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
                finishEditing();
            }
        });

        // Sets the back button on the ActionBar
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sets the title of the activity
        setTitle("Add and save mentors");

        // Finds all of the views and assigns it to a variable for easier access
        nameEditor = (EditText) findViewById(R.id.mentorName);
        emailEditor = (EditText) findViewById(R.id.mentorEmail);
        numberEditor = (EditText) findViewById(R.id.mentorPhone);
        coursesEditor = (TextView) findViewById(R.id.coursesMentored);
        addCourses = (Button) findViewById(R.id.button_Add_Courses);

        // Instantiate courses string
        coursesMentored = "";

        // Gets the original background
        editTextBackground = nameEditor.getBackground();

        // Get the contents of the courses table and put it in a cursor
        Cursor cursor = getContentResolver().query(ScheduleProvider.CONTENT_COURSES_URI,
                null, null, null, null);
        // Create and populate an array of course names using the cursor
        List<String> courseNames = new ArrayList<>();
        if(cursor != null){
            if(cursor.moveToFirst()) {
                do {
                    courseNames.add(cursor.getString(
                            cursor.getColumnIndex(DBOpenHelper.COURSE_NAME)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        // If there are no courses, prompt the user to add a courses
        if(courseNames.size() == 0) {
            // Creates a new listener for dialog interfaces.
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener(){
                        /**
                         * Anonymous class implementation of what to do when the user OKs the delete.
                         * @param dialog the dialog interface
                         * @param which which button is pressed
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Goes to the AddCoursesActivity if the user wants to add
                            if(which == DialogInterface.BUTTON_POSITIVE){
                                Intent intent = new Intent(AddMentorsActivity.this, AddCoursesActivity.class);
                                // Creates the backstack and sets parent to Terms activity
                                TaskStackBuilder stackBuilder =
                                        TaskStackBuilder.create(AddMentorsActivity.this);
                                stackBuilder.addNextIntentWithParentStack(intent);
                                stackBuilder.startActivities();
                            }
                            else {
                                // Goes back to Mentors activity if the user doesn't want to add term
                                finish();
                            }
                        }
                    };
            // The pop up dialogue verifying
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No courses added, add course?")
                    .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(android.R.string.cancel), dialogClickListener)
                    .show();
        }

        // Create an adapter for the data and place it in a pre-defined layout
        adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, courseNames);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        // Finds the spinner view and sets the adapter to display the data
        spinner = (Spinner) findViewById(R.id.spinnerCourses);
        spinner.setAdapter(adapter);

        // Gets the intent from the previous activity
        Intent intent = getIntent();
        // Gets the extra content to determine if this is a specific item
        Uri uri = intent.getParcelableExtra(ScheduleProvider.CONTENT_MENTOR_TYPE);
        if(uri == null){
            // Not a specific item, so not editing but inserting
            action = Intent.ACTION_INSERT;
            setTitle("Add A New Mentor");
        }
        else {
            // This loads the saved information to edit
            action = Intent.ACTION_EDIT;
            setTitle("Edit Mentor");

            // The filter is which row of data to load from
            termFilter = DBOpenHelper.MENTOR_ID + "=" + uri.getLastPathSegment();
            cursor = getContentResolver().query(uri, DBOpenHelper.MENTOR_COLUMNS,
                    termFilter, null, null);
            // Make sure there is data
            if(cursor != null) {
                // Move to the beginning and load all of the data from the DB to the views
                cursor.moveToFirst();

                String mentorName = cursor.getString(
                        cursor.getColumnIndex(DBOpenHelper.MENTOR_NAME));
                nameEditor.setText(mentorName);

                String mentorNumber = cursor.getString(
                        cursor.getColumnIndex(DBOpenHelper.MENTOR_NUMBER));
                numberEditor.setText(mentorNumber);

                String mentorEmail = cursor.getString(
                        cursor.getColumnIndex(DBOpenHelper.MENTOR_EMAIL));
                emailEditor.setText(mentorEmail);

                coursesMentored = cursor.getString(
                        cursor.getColumnIndex(DBOpenHelper.MENTOR_COURSES));
                coursesEditor.setText(coursesMentored);

                // Closing the resource
                cursor.close();

                // Parses the courses being mentored by this person into a string array
                String toSplit = coursesMentored.replace("\t - ", "");
                String stringArray[] = toSplit.split("\\r?\\n");

                // Removes each course from the adapter so it does not appear again to be added
                for(String s: stringArray){
                    adapter.remove(s);
                }
            }
        }

        // Checks the adapter count to reset the button
        checkAdapter();

        // Sets the onClick listener for the button
        addCourses.setOnClickListener(new View.OnClickListener() {
            /**
             * Anonymous class that adds from the spinner into the TextView or resets the view.
             * and adds into the spinner.
             * @param v view
             */
            @Override
            public void onClick(View v) {
                if (resetButton){
                    // If the reset button is active, change the text from TextView to spinner
                    addCourses.setText(R.string.add_course);
                    resetButton = false;
                    // Parsing the text from the TextView and put into an array using REGEX
                    String toSplit = coursesMentored.replace("\t - ", "");
                    String stringArray[] = toSplit.split("\\r?\\n");
                    // Adds every string back into the adapter to display
                    for (String s : stringArray) {
                        adapter.add(s);
                    }
                    // Reset the text of the courses as well as the TextView
                    coursesMentored = "";
                    coursesEditor.setText("");
                }
                else {
                    // If it's the add button, run add the courses to the TextView
                    addCourses.setText(R.string.add_course);
                    addCourse();
                }
            }
        });

        // Sets the onclick for the button to email the mentor
        emailButton();
    }

    /**
     * Helper function that adds the courses from the adapter to the TextView.
     */
    private void addCourse() {
        // Finds the previously added courses
        String newText = coursesMentored;
        // Adds new line if there is old data
        if (coursesMentored.length() != 0) {
            newText += "\n";
        }
        // Gets the object to add and adds
        String object = spinner.getSelectedItem().toString();
        newText += "\t - " + object;
        coursesEditor.setText(newText);
        coursesMentored = coursesEditor.getText().toString();

        // Removes the course from display
        adapter.remove(object);

        // Checks to change the button again
        checkAdapter();
    }

    /**
     * Helper function that checks to switch the text and functions of the button.
     */
    private void checkAdapter() {
        // If there is nothing left to add from the spinner, gives the option to reset
        if(spinner.getAdapter().getCount() == 0){
            addCourses.setText(R.string.reset_courses_mentoring);
            resetButton = true;
        }
    }

    /**
     * Helper function that emails the mentor
     */
    private void emailButton() {
        Button email = (Button) findViewById(R.id.emailLabel);
        email.setOnClickListener(new View.OnClickListener() {
            /**
             * Anonymous class that opens the package manager to handle emailing.
             * @param v the view
             */
            @Override
            public void onClick(View v) {
                if(validate()) {
                    // Creates an array of emails
                    String[] emails = {emailEditor.getText().toString()};
                    // Create the intent to email
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, emails);

                    // If there is a manager to handle emails then start activity
                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }
                }
            }
        });
    }

    /**
     * Sets the text of each menu item appropriately.
     * @param menu the menu on the activity
     * @return returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        menu.getItem(0).setTitle("Save Mentor");
        menu.getItem(1).setTitle("Delete Mentor");
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
        switch(item.getItemId()){
            case R.id.action_delete:
                deleteMentor();
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
        // Gets the mentor from the editor
        String fromEditor = nameEditor.getText().toString();
        switch(action){
            // If the action is insert and the mentor isn't blank, add mentor
            case Intent.ACTION_INSERT:
                if(fromEditor.length() != 0){
                    insertMentor();
                } else {
                    finish();
                }
                break;
            case Intent.ACTION_EDIT:
                // If the action is edit and the mentor is blank, delete, else update
                if(fromEditor.length() == 0){
                    deleteMentor();
                }
                else {
                    updateMentor();
                }
                break;
        }
    }

    /**
     * Helper function that inserts the values from the screen using the set ContentResolver.
     */
    private void insertMentor() {
        if(validate()) {
            ContentValues cv = getValues();
            getContentResolver().insert(ScheduleProvider.CONTENT_MENTORS_URI, cv);
            finish();
        }
    }

    /**
     * Helper function that updates the selection from the screen using the set ContentResolver.
     */
    private void updateMentor() {
        if(validate()) {
            ContentValues cv = getValues();
            getContentResolver().update(ScheduleProvider.CONTENT_MENTORS_URI, cv, termFilter, null);
            finish();
        }
    }

    /**
     * Helper function that deletes the selection from the screen using the set ContentResolver.
     */
    private void deleteMentor() {
        getContentResolver().delete(ScheduleProvider.CONTENT_MENTORS_URI, termFilter, null);
        finish();
    }

    /**
     * Helper function that gets the contents of the form and returns it in a ContentValue.
     * @return ContentValue object of all the values
     */
    private ContentValues getValues() {
        ContentValues cv = new ContentValues();
        // Puts in the values in the ContentValues object
        cv.put(DBOpenHelper.MENTOR_NAME, nameEditor.getText().toString());
        cv.put(DBOpenHelper.MENTOR_EMAIL, emailEditor.getText().toString());
        cv.put(DBOpenHelper.MENTOR_NUMBER, numberEditor.getText().toString());
        cv.put(DBOpenHelper.MENTOR_COURSES, coursesEditor.getText().toString());
        return cv;
    }

    /**
     * Helper function that returns whether a field is valid or not.
      * @return if something is not properly validated
     */
    private boolean validate() {
        // Resets the borders
        resetBorders();

        // Default everything is right
        Boolean matches = true;

        // Compiles the name pattern
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(nameEditor.getText().toString());

        // Checks to see if name matches the pattern
        if(!matcher.matches()){
            matches = false;
            // Highlight the editor red
            nameEditor.setBackgroundResource(R.drawable.invalid_border);
            Toast.makeText(this,
                    "Invalid name format, please try again.", Toast.LENGTH_SHORT).show();
        }

        // Compiles the phone pattern
        pattern = Pattern.compile(PHONE_PATTERN);
        matcher = pattern.matcher(numberEditor.getText().toString());

        // Checks to see if phone matches the pattern
        if(!matcher.matches()){
            matches = false;
            // Highlight the editor red
            numberEditor.setBackgroundResource(R.drawable.invalid_border);
            Toast.makeText(this,
                    "Invalid phone number format, please enter a 10 digit number.",
                    Toast.LENGTH_SHORT).show();
        }

        // Compiles the email pattern
        pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(emailEditor.getText().toString());

        // Checks to see if email matches the pattern
        if(!matcher.matches()){
            matches = false;
            // Highlight the editor red
            emailEditor.setBackgroundResource(R.drawable.invalid_border);
            Toast.makeText(this,
                    "Invalid email format, please try again.", Toast.LENGTH_SHORT).show();
        }

        // Returns the boolean
        return matches;
    }

    /**
     * Helper function that resets the background to the original.
     */
    private void resetBorders() {
        // Changes all of the backgrounds of the borders back
        nameEditor.setBackground(editTextBackground);
        numberEditor.setBackground(editTextBackground);
        emailEditor.setBackground(editTextBackground);
    }
}
