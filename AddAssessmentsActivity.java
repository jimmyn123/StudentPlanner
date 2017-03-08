/**
 * The Activity for adding and displaying the details of an assessment to the DB
 *
 * @author Jimmy Nguyen
 * @version 3/8/2017
 */
package com.example.studentplanner.studentplanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddAssessmentsActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    // sets fields that will be used in multiple methods
    private static final int CAMERA_REQUEST_CODE = 777;
    private EditText assessmentNotesEditor, goalEditor;
    private ImageView iv;
    private String action, filter, assessmentNotes, imagePath, savedEndDate, endDate;
    private int assessmentCourseID, assessmentTypeID;
    private Spinner courseName, assessmentType;
    private ArrayAdapter<String> adapterCourse, adapterType;
    private Drawable editTextBackground;

    // Regex for date
    private final String DATE_PATTERN = "^[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}";

    /**
     * Shows the add form if it is adding a new entry.
     * Shows the saved details if it is loading an entry.
     * @param savedInstanceState used to reload if the app breaks
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Code to set the view as well as the actionbar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessments);
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
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sets the title of the activity
        setTitle("Add and save your assessments");

        // Finds all of the views and assigns it to a variable for easier access
        assessmentNotesEditor = (EditText) findViewById(R.id.notesText);
        goalEditor = (EditText) findViewById(R.id.endDateAssessments);
        iv = (ImageView) findViewById(R.id.imageView);

        // Gets the editText default background
        editTextBackground = goalEditor.getBackground();

        // Get the contents of the courses table and put it in a cursor
        Cursor cursor = getContentResolver().query(ScheduleProvider.CONTENT_COURSES_URI,
                null, null, null, null);
        // Create and populate an array of course names using the cursor
        List<String> courseNames = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    courseNames.add(cursor.getString(
                            cursor.getColumnIndex(DBOpenHelper.COURSE_NAME)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        // If there are no courses, prompt the user to add a courses
        if (courseNames.size() == 0) {
            // Creates a new listener for dialog interfaces.
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        /**
                         * Anonymous class implementation of what to do when the user OKs the delete.
                         * @param dialog the dialog interface
                         * @param which which button is pressed
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Goes to the AddCoursesActivity if the user wants to add
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                Intent intent = new Intent(AddAssessmentsActivity.this, AddCoursesActivity.class);
                                // Creates the BackStack and sets parent to Terms activity
                                TaskStackBuilder stackBuilder =
                                        TaskStackBuilder.create(AddAssessmentsActivity.this);
                                stackBuilder.addNextIntentWithParentStack(intent);
                                stackBuilder.startActivities();
                            } else {
                                // Goes back to Assessments activity if the user doesn't want to add term
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

        // Create an adapterCourse for the data and place it in a pre-defined layout
        adapterCourse = new ArrayAdapter<>(this, R.layout.spinner_item, courseNames);
        adapterCourse.setDropDownViewResource(R.layout.spinner_item);

        // Finds the courseName view and sets the adapterCourse to display the data
        courseName = (Spinner) findViewById(R.id.courseName);
        courseName.setAdapter(adapterCourse);

        // Create and populate an array of course types
        List<String> courseTypes = new ArrayList<>();
        courseTypes.add("Objective Assessment");
        courseTypes.add("Performance Assessment");

        // Create an adapterCourse for the data and place it in a pre-defined layout
        adapterType = new ArrayAdapter<>(this, R.layout.spinner_item, courseTypes);

        // Finds the assessment type view and sets the adapterCourse to display the data
        assessmentType = (Spinner) findViewById(R.id.assessmentType);
        assessmentType.setAdapter(adapterType);

        // Finds the button that adds a picture
        Button addPicture = (Button) findViewById(R.id.button_Photo_Note);
        addPicture.setOnClickListener(new View.OnClickListener() {
            /**
             * Anonymous class for the button that deals with what happens when you click it
             * @param v the view
             */
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Check manifest for permission to use camera
                    if (checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        // If permission has not been set, manually ask for permission
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST_CODE);
                    } else {
                        openCamera();
                    }
                } else {
                    // All other SDKs already have permission when the app installs
                    openCamera();
                }
            }
        });

        // Disables the button if the phone doesn't have a camera
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            addPicture.setEnabled(false);
        }

        // Gets the intent from the previous activity
        Intent intent = getIntent();
        // Gets the extra content to determine if this is a specific item
        Uri uri = intent.getParcelableExtra(ScheduleProvider.CONTENT_ASSESSMENT_TYPE);
        if (uri == null) {
            // Not a specific item, so not editing but inserting
            action = Intent.ACTION_INSERT;
            setTitle("Add New Assessment");
        } else {
            // This loads the saved information to edit
            action = Intent.ACTION_EDIT;
            setTitle("Edit Assessment");

            // The filter is which row of data to load from
            filter = DBOpenHelper.ASSESSMENT_ID + "=" + uri.getLastPathSegment();
            cursor = getContentResolver().query(uri, DBOpenHelper.ASSESSMENT_COLUMNS,
                    filter, null, null);
            // Make sure there is data
            if (cursor != null) {
                // Move to the beginning and load all of the data from the DB to the views
                cursor.moveToFirst();

                assessmentCourseID = cursor.getInt(
                        cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_COURSE_ID));
                courseName.setSelection(assessmentCourseID);
                assessmentTypeID = cursor.getInt(
                        cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE));
                assessmentType.setSelection(assessmentTypeID);
                assessmentNotes = cursor.getString(
                        cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTES));
                assessmentNotesEditor.setText(assessmentNotes);
                savedEndDate = cursor.getString(
                        cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DUE_DATE));
                endDate = savedEndDate;
                goalEditor.setText(endDate);

                imagePath = cursor.getString(
                        cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_PICTURE));

                if (imagePath != null) setImage();

                // Closing the resource
                cursor.close();
            }
        }
    }

    /**
     * Opens the camera if the user grants permission to access it.
     * @param requestCode the request code for permissions
     * @param permissions the permissions
     * @param grantResults results of the permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            // Checks to see if it is a response to the camera permissions request
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Opens the camera if permission is granted
                openCamera();
            } else {
                // Display asks user to grant access
                Toast.makeText(this, "Please allow the camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Helper function that opens the camera using intent.
     */
    private void openCamera() {
        // New intent to open capture image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = null;
        try {
            // Tries to create an image file
            imageFile = createImageFile();
        } catch (IOException e) {
            // Something happened in IO
            Toast.makeText(this, "Couldn't create file", Toast.LENGTH_SHORT).show();
        }

        if (imageFile != null) {
            // If the image was created save to that location
            Uri imageURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider", imageFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    /**
     * Creates the file with where to store and how to name
     * @return returns the image file
     * @throws IOException File can throw IO error
     */
    private File createImageFile() throws IOException {
        // Create a timestamp to add to the custom image name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String namePref = "image_assessments" + timeStamp;

        //Create the file with preset name and storage location
        File storeDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storeDir, namePref);

        // sets where to find the image for later use
        imagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    /**
     * When the camera closes and the image is captured, set it to display in the ImageView
     * @param requestCode request code of the camera
     * @param resultCode whether the user hit cancel or ok
     * @param data would contain a thumbnail but picture was stored at different location
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Sets the image
            setImage();
        }
    }

    /**
     * Helper function that sets the image into the ImageView.
     */
    private void setImage() {
        // Creates the bitmap options
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // Scales down the size to save memory when displaying
        options.inSampleSize = 4;

        // Decode the bitmap using the set options
        Bitmap imageBitmap;
        imageBitmap = BitmapFactory.decodeFile(imagePath, options);

        // Rotates the image depending on how it was saved
        imageBitmap = rotateImage(imageBitmap);
        iv.setImageBitmap(imageBitmap);
    }

    /**
     * Rotates the image according to the Exif of the image.
     * @param imageBitmap the image to rotate
     * @return returns the rotated image
     */
    private Bitmap rotateImage(Bitmap imageBitmap) {
        // New ExifInterface with the orientation properties
        ExifInterface ei;
        Bitmap returnBitmap = null;
        try {
            ei = new ExifInterface(imagePath);
            // Gets the orientation of how the image was taken
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // Depending on how it was taken, rotates the image to match the original
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    returnBitmap = rotate(imageBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    returnBitmap = rotate(imageBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    returnBitmap = rotate(imageBitmap, 270);
                    break;
                default:
                    returnBitmap = rotate(imageBitmap, 0);
            }
        } catch (IOException e) {
            // Issue creating ei
            Toast.makeText(this, "IO error!", Toast.LENGTH_SHORT).show();
        }
        return returnBitmap;
    }

    /**
     * Rotates the image in degrees.
     * @param in inputted image
     * @param degree the degrees to rotate
     * @return returns the rotated image
     */
    private Bitmap rotate(Bitmap in, int degree) {
        // Creates a new matrix and applies the rotated matrix to the image
        Matrix mtx = new Matrix();
        mtx.postRotate(degree);
        return Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), mtx, true);
    }

    /**
     * Sets the text of each menu item appropriately.
     * @param menu the menu on the activity
     * @return returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        menu.getItem(0).setTitle("Save Assessment");
        menu.getItem(1).setTitle("Delete Assessment");
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
                deleteAssessment();
                break;
            default:
                finishEditing();
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
        String fromEditor = courseName.getSelectedItem().toString();
        switch (action) {
            case Intent.ACTION_INSERT:
                // If the action is insert and the assessment isn't blank, add assessment
                if (fromEditor.length() != 0) {
                    insertAssessments();
                } else {
                    finish();
                }
                break;
            case Intent.ACTION_EDIT:
                // If the action is edit and the assessment is blank, delete, else update
                if (fromEditor.length() == 0) {
                    deleteAssessment();
                } else {
                    updateAssessment();
                }
                break;
        }
    }

    /**
     * Helper function that inserts the values from the screen using the set ContentResolver.
     */
    private void insertAssessments() {
        if (validate()) {
            ContentValues cv = getContentValues();
            // Update the alarms
            updateAlarms();
            getContentResolver().insert(ScheduleProvider.CONTENT_ASSESSMENTS_URI, cv);
            finish();
        }
    }

    /**
     * Helper function that updates the selection from the screen using the set ContentResolver.
     */
    private void updateAssessment() {
        if (validate()) {
            ContentValues cv = getContentValues();
            // Only updates the alarm if the end dates are different
            if (!savedEndDate.equals(endDate)) updateAlarms();
            getContentResolver().update(ScheduleProvider.CONTENT_ASSESSMENTS_URI, cv, filter, null);
            finish();
        }
    }

    /**
     * Helper function that deletes the selection from the screen using the set ContentResolver.
     */
    private void deleteAssessment() {
        getContentResolver().delete(ScheduleProvider.CONTENT_ASSESSMENTS_URI, filter, null);
        updateAlarms();
        finish();
    }

    /**
     * Helper function that gets the contents of the form and returns it in a ContentValue.
     * @return ContentValue object of all the values
     */
    private ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        // Puts in the values in the ContentValues object
        String name = courseName.getSelectedItem().toString() + " - " +
                assessmentType.getSelectedItem().toString();
        cv.put(DBOpenHelper.ASSESSMENT_NAME, name);
        cv.put(DBOpenHelper.ASSESSMENT_COURSE_ID, courseName.getSelectedItemPosition());
        cv.put(DBOpenHelper.ASSESSMENT_TYPE, assessmentType.getSelectedItemPosition());
        cv.put(DBOpenHelper.ASSESSMENT_DUE_DATE, endDate);
        cv.put(DBOpenHelper.ASSESSMENT_NOTES, assessmentNotesEditor.getText().toString());
        if (imagePath != null && imagePath.length() != 0) {
            cv.put(DBOpenHelper.ASSESSMENT_PICTURE, imagePath);
        }
        return cv;
    }


    /**
     * Helper function that returns whether a field is valid or not.
     * @return returns whether fields are validated
     */
    private boolean validate() {
        // Resets the borders
        resetBorders();

        // Default everything is right
        Boolean matches = true;

        // Compiles the name pattern
        Pattern pattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = pattern.matcher(goalEditor.getText().toString());

        // Checks to see if start date matches the pattern
        if (!matcher.matches()) {
            matches = false;
            // Highlight the editor red
            goalEditor.setBackgroundResource(R.drawable.invalid_border);
            Toast.makeText(this,
                    "Invalid start date, please try again.", Toast.LENGTH_SHORT).show();
        }

        return matches;
    }

    /**
     * Helper function that resets the background to the original.
     */
    private void resetBorders() {
        // Changes all of the backgrounds of the borders back
        goalEditor.setBackground(editTextBackground);
    }

    /**
     * Helper function that resets the alarms if an item was modified or deleted.
     */
    private void updateAlarms() {
        // Alarm ID
        int alarmID = 0;

        // Gets the SharedPreferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notifications = sp.getBoolean("notificationsCourses", true);

        // Queries the database for the courses and end dates
        Cursor cursor = getContentResolver().query(
                ScheduleProvider.CONTENT_COURSES_URI, null, null, null, null);

        alarmID = createNotifications(notifications, cursor, DBOpenHelper.COURSE_NAME,
                DBOpenHelper.COURSE_END, alarmID, true);

        notifications = sp.getBoolean("notificationsAssessment", true);
        // Queries the database for the assessments and end dates
        cursor = getContentResolver().query(
                ScheduleProvider.CONTENT_ASSESSMENTS_URI, null, null, null, null);
        createNotifications(notifications, cursor, DBOpenHelper.ASSESSMENT_NAME,
                DBOpenHelper.ASSESSMENT_DUE_DATE, alarmID, false);
    }

    /**
     * Helper function that takes in parameters, sets a notifcation and returns an alarmID
     * @param notify True or false if the alarm should be set
     * @param c the cursor
     * @param name name to put in the alarm
     * @param endString end date
     * @param alarmID alarmID to return/save
     * @return returns the alarmID that it last ended with
     */
    private int createNotifications(Boolean notify, Cursor c, String name,
                                    String endString, int alarmID, boolean courseBool) {
        // This only runs if there is a cursor
        if (c != null) {
            try { // Runs only if there is something in cursor
                while (c.moveToNext()) {
                    // Gets today's date
                    Calendar today = Calendar.getInstance();

                    // Creates a calendar to hold current instance
                    Calendar alarm = Calendar.getInstance();

                    // Gets the end date to set reminder
                    String[] date = c.getString(
                            c.getColumnIndex(endString)).split("/");
                    // Sets the date of the reminder
                    alarm.set(Integer.parseInt(date[2]),
                            (Integer.parseInt(date[0]) - 1), Integer.parseInt(date[1]));

                    // New intent and to get a PendingIntent
                    Intent intent = new Intent(this, AlarmReceiver.class);
                    // Adds the name nad alarm ID into the extras
                    intent.putExtra("name", c.getString(c.getColumnIndex(name)));
                    intent.putExtra("alarmID", alarmID);
                    intent.putExtra("course", courseBool);

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
                        if (notify) am.set(AlarmManager.RTC, alarm.getTimeInMillis(), pi);
                    }
                    // Increments for the alarmID
                    alarmID += 1;
                }
            } finally {
                // Closes the cursor
                c.close();
            }
        }
        return alarmID;
    }

    /**
     * Opens a DatePicker each time a specific button is clicked.
     * @param view input view
     */
    public void openDateCourses(View view) {
        // Finds the name of the clicked button and parses it
        String name = getResources()
                .getResourceEntryName(view.getId()).substring(0, 3).trim().toLowerCase();

        // Creates a new DatePicker and adds an argument to tell which activity is calling it
        DatePickerFragment dpf = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("activity", 3);
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

        endDate = date;

        // Displays the date on the textView
        goalEditor.setText(date);
    }
}
