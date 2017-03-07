/**
 * The custom content provider for the Schedule Planner app.
 * @author Jimmy nguyen
 * @version 3/6/2017
 */
package com.example.studentplanner.studentplanner;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

@SuppressWarnings("ConstantConditions")
public class ScheduleProvider extends ContentProvider {

    // set the authority string
    private static final String AUTHORITY =
            "com.example.studentplanner.studentplanner.scheduleprovider";

    // sets the path constants and creates the URI for each table
    private static final String TERMS_PATH = "terms";
    public static final Uri CONTENT_TERMS_URI =
            Uri.parse("content://" + AUTHORITY + "/" + TERMS_PATH);
    private static final String COURSE_PATH = "courses";
    public static final Uri CONTENT_COURSES_URI =
            Uri.parse("content://" + AUTHORITY + "/" + COURSE_PATH);
    private static final String MENTOR_PATH = "mentors";
    public static final Uri CONTENT_MENTORS_URI =
            Uri.parse("content://" + AUTHORITY + "/" + MENTOR_PATH);
    private static final String ASSESSMENT_PATH = "assessments";
    public static final Uri CONTENT_ASSESSMENTS_URI =
            Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENT_PATH);

    // sets the constants for each URI case
    private static final int TERMS_BASE = 1;
    private static final int TERMS_ID = 2;
    private static final int COURSE_BASE = 3;
    private static final int COURSE_ID = 4;
    private static final int MENTOR_BASE = 5;
    private static final int MENTOR_ID = 6;
    private static final int ASSESSMENT_BASE = 7;
    private static final int ASSESSMENT_ID = 8;

    // the constant names that will hold specific URI paths
    public static final String CONTENT_TERM_TYPE = "term";
    public static final String CONTENT_COURSE_TYPE = "course";
    public static final String CONTENT_MENTOR_TYPE = "mentor";
    public static final String CONTENT_ASSESSMENT_TYPE = "assessment";

    // a new UriMatcher
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // matching the URI case to the constants above
    static {
        uriMatcher.addURI(AUTHORITY, TERMS_PATH, TERMS_BASE);
        uriMatcher.addURI(AUTHORITY, TERMS_PATH + "/#", TERMS_ID);
        uriMatcher.addURI(AUTHORITY, COURSE_PATH, COURSE_BASE);
        uriMatcher.addURI(AUTHORITY, COURSE_PATH + "/#", COURSE_ID);
        uriMatcher.addURI(AUTHORITY, MENTOR_PATH, MENTOR_BASE);
        uriMatcher.addURI(AUTHORITY, MENTOR_PATH + "/#", MENTOR_ID);
        uriMatcher.addURI(AUTHORITY, ASSESSMENT_PATH, ASSESSMENT_BASE);
        uriMatcher.addURI(AUTHORITY, ASSESSMENT_PATH + "/#", ASSESSMENT_ID);
    }

    // the database
    private SQLiteDatabase database;

    /**
     * Creates and gets the database.
     * @return Does not matter what to return
     */
    @Override
    public boolean onCreate() {
        // Creates the helper and gets the DB
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return false;
    }

    /**
     * Queries the depending on the URI table and returns the results with the specific filter.
     * @param uri the uri to query
     * @param projection projection of the columns in the table queried
     * @param selection filters and which rows to return
     * @param selectionArgs arguments
     * @param sortOrder order when the query is returned
     * @return the cursor (sql results)
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // queries the actual db
        Cursor cursor = database.query(getTable(uri), getColumns(uri), getSelection(uri, selection),
                null, null, null,  getTableSort(uri)+ " ASC");
        // wants to know if anything changes in the table
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     *  Returns the type, but right now does not need that
     * @param uri the inputted uri
     * @return no return right now
     */
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Inserts the information into their respective columns.
     * @param uri the inputted uri
     * @param values the values to insert
     * @return returns the uri of the specific path
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // notifies that something has changed
        getContext().getContentResolver().notifyChange(uri, null);
        long id = database.insert(getTable(uri), null, values);
        //returns the specific URI created
        return createUri(uri, id);
    }

    /**
     * Deletes specific rows in the table.
     * @param uri the inputted uri
     * @param selection the filter
     * @param selectionArgs none
     * @return how many rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // notifies that something has changed
        getContext().getContentResolver().notifyChange(uri, null);
        // returns the number of deleted rows
        return database.delete(getTable(uri), selection, selectionArgs);
    }

    /**
     * Updates the selected rows in the table.
     * @param uri the inputted uri
     * @param values updated values
     * @param selection filter
     * @param selectionArgs none
     * @return how many rows updated
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        // notifies that something has changed
        getContext().getContentResolver().notifyChange(uri, null);
        // returns how many rows were updated
        return database.update(getTable(uri), values, selection, selectionArgs);
    }

    /**
     * Returns which table to use with the URI.
     * @param uri the inputted uri
     * @return returns which table to use
     */
    private String getTable(Uri uri){
        // creates the uriMatcher to determine which table to use
        int uriMatch = uriMatcher.match(uri);
        String returnTable = "";
        // sets the table to return depending on the URI
        switch (uriMatch) {
            case TERMS_BASE:
                returnTable = DBOpenHelper.TABLE_TERMS;
                break;
            case TERMS_ID:
                returnTable = DBOpenHelper.TABLE_TERMS;
                break;
            case COURSE_BASE:
                returnTable = DBOpenHelper.TABLE_COURSES;
                break;
            case COURSE_ID:
                returnTable = DBOpenHelper.TABLE_COURSES;
                break;
            case MENTOR_BASE:
                returnTable = DBOpenHelper.TABLE_MENTORS;
                break;
            case MENTOR_ID:
                returnTable = DBOpenHelper.TABLE_MENTORS;
                break;
            case ASSESSMENT_BASE:
                returnTable = DBOpenHelper.TABLE_ASSESSMENTS;
                break;
            case ASSESSMENT_ID:
                returnTable = DBOpenHelper.TABLE_ASSESSMENTS;
                break;
        }
        // returns the table
        return returnTable;
    }

    /**
     * Returns which columns to use with the URI.
     * @param uri the inputted uri
     * @return returns which columns to use
     */
    private String[] getColumns(Uri uri){
        // creates the uriMatcher to determine which column to use
        int uriMatch = uriMatcher.match(uri);
        String[] returnColumns = {};
        // sets the columns to return depending on the URI
        switch (uriMatch) {
            case TERMS_ID:
                returnColumns = DBOpenHelper.TERMS_COLUMNS;
                break;
            case COURSE_ID:
                returnColumns = DBOpenHelper.COURSES_COLUMNS;
                break;
            case MENTOR_ID:
                returnColumns = DBOpenHelper.MENTOR_COLUMNS;
                break;
            case ASSESSMENT_ID:
                returnColumns = DBOpenHelper.ASSESSMENT_COLUMNS;
                break;
        }
        // returns the columns
        return returnColumns;
    }

    /**
     * Returns which specific path to use with the URI.
     * @param uri the inputted uri
     * @return returns which specific path to use
     */
    private Uri createUri(Uri uri, long id){
        // creates the uriMatcher to determine which path to use
        int uriMatch = uriMatcher.match(uri);
        Uri returnUri = null;
        // sets the specific path to return depending on the URI
        switch (uriMatch) {
            case TERMS_BASE:
                returnUri = Uri.parse(TERMS_PATH + "/" + id);
                break;
            case COURSE_BASE:
                returnUri = Uri.parse(COURSE_PATH + "/" + id);
                break;
            case MENTOR_BASE:
                returnUri = Uri.parse(MENTOR_PATH + "/" + id);
                break;
            case ASSESSMENT_BASE:
                returnUri = Uri.parse(ASSESSMENT_PATH + "/" + id);
                break;
        }
        // returns the specific path
        return returnUri;
    }

    /**
     * Returns which filter to use with the URI.
     * @param uri the inputted uri
     * @param selection filter
     * @return returns which filter to use
     */
    private String getSelection(Uri uri, String selection){
        // creates the uriMatcher to determine which selection to use
        int uriMatch = uriMatcher.match(uri);
        // sets the filter to return depending on the URI
        switch(uriMatch)
        {
            case TERMS_ID:
                selection = DBOpenHelper.TERM_ID + "=" + uri.getLastPathSegment();
                break;
            case COURSE_ID:
                selection = DBOpenHelper.COURSE_ID + "=" + uri.getLastPathSegment();
                break;
            case MENTOR_ID:
                selection = DBOpenHelper.MENTOR_ID + "=" + uri.getLastPathSegment();
                break;
            case ASSESSMENT_ID:
                selection = DBOpenHelper.ASSESSMENT_ID + "=" + uri.getLastPathSegment();
                break;
        }
        // returns the selection
        return selection;
    }

    /**
     * Returns which sort to use with the URI.
     * @param uri the inputted uri
     * @return returns which sort to use
     */
    private String getTableSort(Uri uri) {
        // creates the uriMatcher to determine which sort to use
        int uriMatch = uriMatcher.match(uri);
        String returnTableCreated = "";
        // sets the sort to return depending on the URI
        switch (uriMatch) {
            case TERMS_BASE:
                returnTableCreated = DBOpenHelper.TERM_NUMBER;
                break;
            case TERMS_ID:
                returnTableCreated = DBOpenHelper.TERM_NUMBER;
                break;
            case COURSE_BASE:
                returnTableCreated = DBOpenHelper.COURSE_NAME;
                break;
            case COURSE_ID:
                returnTableCreated = DBOpenHelper.COURSE_NAME;
                break;
            case MENTOR_BASE:
                returnTableCreated = DBOpenHelper.MENTOR_NAME;
                break;
            case MENTOR_ID:
                returnTableCreated = DBOpenHelper.MENTOR_NAME;
                break;
            case ASSESSMENT_BASE:
                returnTableCreated = DBOpenHelper.ASSESSMENT_COURSE;
                break;
            case ASSESSMENT_ID:
                returnTableCreated = DBOpenHelper.ASSESSMENT_COURSE;
                break;
        }
        // returns the sort
        return returnTableCreated;
    }
}
