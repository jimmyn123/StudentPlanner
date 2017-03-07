/**
 * Terms activity. Displays the added terms and loads it from the database.
 *
 * @author Jimmy Nguyen
 * @version 3/6/2017
 */

package com.example.studentplanner.studentplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class TermsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    // the adapter between the cursor and the database
    private CursorAdapter cursorAdapter;

    /**
     * Loads the terms previously saved when the activity is created.
     *
     * @param savedInstanceState to reload if something breaks
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Code to set the view as well as the actionbar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Sets the  floating action button to open the addTerms activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * Anonymous class implementation of what happens when you click the FAB
             * @param view current view
             */
            @Override
            public void onClick(View view) {
                openAddTerms();
            }
        });

        // Sets the back button on the ActionBar
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sets the title of the activity
        setTitle(getString(R.string.term_title));

        // Creates a new adapter
        cursorAdapter = new ScheduleCursorAdapter(this, null, 0, 1);

        // Finds the ListView in the activity to display the terms and sets the adapter
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        // Sets what happens when you click each item
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Anonymous class implementation of what happens when you click on each item
             * @param parent parent view
             * @param view current view
             * @param position position of the item clicked
             * @param id id number of item clicked
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Opens a new activity window with the specific id of the item
                Intent intent = new Intent(TermsActivity.this, AddTermsActivity.class);
                Uri uri = Uri.parse(ScheduleProvider.CONTENT_TERMS_URI + "/" + id);
                intent.putExtra(ScheduleProvider.CONTENT_TERM_TYPE, uri);
                startActivity(intent);
            }
        });

        // Initialize the loader for LoaderManager
        getSupportLoaderManager().initLoader(0, null, this);

    }

    /**
     * Sets the text of each menu item appropriately.
     * @param menu the menu on the activity
     * @return returns true
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        menu.getItem(0).setTitle("Add Terms");
        menu.getItem(1).setTitle("Delete All Terms");
        return true;
    }

    /**
     * Provides the navigation/actions to each menu item.
     * @param item the selected item in the menu
     * @return returns true
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Calls different actions to open activities
        switch(item.getItemId()) {
            case R.id.action_add:
                openAddTerms();
                break;
            case R.id.action_delete:
                deleteAll();
                break;
            default:
                onBackPressed();
        }
        return true;
    }

    /**
     * Opens the addTerms activity
     */
    private void openAddTerms() {
        Intent intent = new Intent(TermsActivity.this, AddTermsActivity.class);
        startActivity(intent);
    }

    /**
     * Deletes every term
     */
    private void deleteAll(){
        // Creates a new listener for dialog interfaces.
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
            /**
             * Anonymous class implementation of what to do when the user OKs the delete.
             * @param dialog the dialog interface
             * @param which which button is pressed
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Deletes everything if it is confirmed
                if(which == DialogInterface.BUTTON_POSITIVE){
                    getContentResolver().delete(ScheduleProvider.CONTENT_TERMS_URI, null, null);
                    getContentResolver().delete(ScheduleProvider.CONTENT_COURSES_URI, null, null);
                    getContentResolver().delete(ScheduleProvider.CONTENT_MENTORS_URI, null, null);
                    getContentResolver().delete(ScheduleProvider.CONTENT_ASSESSMENTS_URI,
                            null, null);
                }
            }
        };
        // The pop up dialogue verifying
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Warning: This will delete all courses," +
                " assessments, and mentors as well.")
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.cancel), dialogClickListener).show();
    }

    /**
     * Goes back to the latest action from the BackStack.
     */
    @Override
    public void onBackPressed() {
        finish();
    }

//    Old code to reload the cursor.
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == TERMS_REQUEST_CODE && resultCode == RESULT_OK){
//            restartLoader();
//        }
//    }
//
//    private void restartLoader() {
//        getSupportLoaderManager().restartLoader(0, null, this);
//    }

    /**
     * Creates the cursor loader.
     * @param id id number of the loader
     * @param args extra arguments
     * @return returns a new CursorLoader
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ScheduleProvider.CONTENT_TERMS_URI, null, null, null, null);
    }

    /**
     * The callback method for when a cursor is loaded. It reloads the adapter to reflect changes.
     * @param loader The LoaderManager
     * @param data the new data/most recent
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    /**
     * Displays nothing when the loader is reset
     * @param loader the LoaderManager
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

}
