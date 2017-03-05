/**
 * The main activity class. Provides navigation to the other activities.
 * @author Jimmy nguyen
 * @version 3/2/2017
 */

package com.example.studentplanner.studentplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
        menu.add(Menu.NONE, 4, Menu.NONE, "Settings");
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
