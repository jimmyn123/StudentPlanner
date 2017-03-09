/**
 * The helper class used to create and access the internal database.
 * @author Jimmy nguyen
 * @version 3/8/2017
 */
package com.example.studentplanner.studentplanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Most access modifiers are public in case others need to use the ContentProvider to access the DB.
@SuppressWarnings("WeakerAccess")
public class DBOpenHelper extends SQLiteOpenHelper {
    // Database name and version constants
    private static final String DATABASE_NAME = "schedule.db";
    private static final int DATABASE_VERSION = 9;

    // Column names for the terms table
    public static final String TABLE_TERMS = "terms";
    public static final String TERM_ID = "_id";
    public static final String TERM_NUMBER = "termNumber";
    public static final String TERM_START = "termStart";
    public static final String TERM_END = "termEnd";
    public static final String TERM_HAS_COURSE = "hasCourse";
    public static final String TERM_CREATED = "termCreated";

    // Array of all the columns in terms
    public static final String[] TERMS_COLUMNS = {TERM_ID, TERM_NUMBER, TERM_START,
            TERM_END, TERM_HAS_COURSE, TERM_CREATED};

    // The sql statement to create the terms table
    private static final String TERMS_CREATE = "CREATE TABLE " + TABLE_TERMS +
            " (" + TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TERM_NUMBER + " INTEGER, " + TERM_START + " TEXT, " +
            TERM_END + " TEXT, " + TERM_HAS_COURSE + " INTEGER DEFAULT 0, " +
            TERM_CREATED + " TEXT default CURRENT_TIMESTAMP" + ")";

    // Column names for the courses table
    public static final String TABLE_COURSES = "courses";
    public static final String COURSE_ID = "_id";
    public static final String COURSE_TERM_ID = "termID";
    public static final String COURSE_NAME = "courseName";
    public static final String COURSE_START = "courseStart";
    public static final String COURSE_END = "courseEnd";
    public static final String COURSE_STATUS = "courseStatus";
    public static final String COURSE_NOTES = "courseNotes";
    public static final String COURSE_PICTURE = "coursePicture";
    public static final String COURSE_CREATED = "courseCreated";

    // Array of all the columns in terms
    static final String[] COURSES_COLUMNS = {COURSE_ID, COURSE_TERM_ID, COURSE_NAME,
            COURSE_START, COURSE_END, COURSE_STATUS, COURSE_NOTES, COURSE_PICTURE, COURSE_CREATED};

    // The sql statement to create the courses table
    private static final String COURSES_CREATE = "CREATE TABLE " + TABLE_COURSES + " (" +
            COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COURSE_TERM_ID + " INTEGER, " + COURSE_NAME + " TEXT, " + COURSE_START + " TEXT, " +
            COURSE_END + " TEXT, " + COURSE_NOTES + " TEXT, " + COURSE_STATUS + " INTEGER, " +
            COURSE_PICTURE + " TEXT, " + COURSE_CREATED + " TEXT default CURRENT_TIMESTAMP" + ")";

    // Column names for the mentors table
    public static final String TABLE_MENTORS = "mentors";
    public static final String MENTOR_ID = "_id";
    public static final String MENTOR_NAME = "mentorName";
    public static final String MENTOR_COURSES = "mentorCourses";
    public static final String MENTOR_NUMBER = "mentorNumber";
    public static final String MENTOR_EMAIL = "mentorEmail";
    public static final String MENTOR_CREATED = "mentorCreated";

    // Array of all the columns in mentors
    public static final String[] MENTOR_COLUMNS = {MENTOR_ID, MENTOR_NAME, MENTOR_COURSES,
            MENTOR_NUMBER, MENTOR_EMAIL, MENTOR_CREATED};

    // The sql statement to create the mentors table
    private static final String MENTORS_CREATE = "CREATE TABLE " + TABLE_MENTORS +
            " (" + MENTOR_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MENTOR_NAME + " TEXT, " + MENTOR_COURSES + " TEXT, " +
            MENTOR_NUMBER + " TEXT, " + MENTOR_EMAIL + " TEXT, " +
            MENTOR_CREATED + " TEXT default CURRENT_TIMESTAMP" + ")";

    // Column names for the assessments table
    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String ASSESSMENT_ID = "_id";
    public static final String ASSESSMENT_NAME = "assessmentName";
    public static final String ASSESSMENT_COURSE_ID = "assessmentCourseID";
    public static final String ASSESSMENT_TYPE = "assessmentType";
    public static final String ASSESSMENT_DUE_DATE = "assessmentDueDate";
    public static final String ASSESSMENT_NOTES = "assessmentNotes";
    public static final String ASSESSMENT_PICTURE = "assessmentPicture";
    public static final String ASSESSMENT_CREATED = "assessmentCreated";

    // Array of all the columns in assessments
    public static final String[] ASSESSMENT_COLUMNS = {ASSESSMENT_ID, ASSESSMENT_NAME,
            ASSESSMENT_COURSE_ID, ASSESSMENT_TYPE, ASSESSMENT_DUE_DATE, ASSESSMENT_NOTES,
            ASSESSMENT_PICTURE, ASSESSMENT_CREATED};

    // The sql statement to create the assessments table
    public static final String ASSESSMENTS_CREATE = "CREATE TABLE " + TABLE_ASSESSMENTS +
            " (" + ASSESSMENT_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ASSESSMENT_NAME + " TEXT, " + ASSESSMENT_COURSE_ID + " INTEGER, " +
            ASSESSMENT_TYPE + " INTEGER, " + ASSESSMENT_DUE_DATE + " TEXT, " +
            ASSESSMENT_NOTES + " TEXT, " + ASSESSMENT_PICTURE + " TEXT, " +
            ASSESSMENT_CREATED + " TEXT default CURRENT_TIMESTAMP" + ")";

    /**
     * Constructor for the OpenHelper.
     * @param context the context of the app
     */
    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Executes the create SQL statements for all of the tables.
     * @param db the database for the context
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TERMS_CREATE);
        db.execSQL(COURSES_CREATE);
        db.execSQL(MENTORS_CREATE);
        db.execSQL(ASSESSMENTS_CREATE);
    }

    /**
     * Deletes the old data and updates the database.
     * Should change to keep the data in later versions.
     * @param db the db for the context
     * @param oldVersion old DB version
     * @param newVersion new DB version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        onCreate(db);
    }
}
