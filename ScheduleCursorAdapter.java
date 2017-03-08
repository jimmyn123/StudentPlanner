/**
 *A cursor adapter for the student planner app. Inflates and binds to a custom view.
 * @author Jimmy Nguyen
 * @version 3/8/2017
 */
package com.example.studentplanner.studentplanner;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressWarnings("WeakerAccess")
public class ScheduleCursorAdapter extends CursorAdapter {

    // constants to identify which activity is using the adapter
    private static final int termsActivity = 1;
    private static final int coursesActivity = 2;
    private static final int mentorsActivity = 3;
    private static final int assessmentsActivity = 4;
    private final int activity;

    /**
     * Constructs the adapter.
     *
     * @param context input context
     * @param c input cursor
     * @param flags flag number
     * @param activity identifies which activity is using the adapter
     */
    public ScheduleCursorAdapter(Context context, Cursor c, int flags, int activity) {
        super(context, c, flags);
        // set the activity state
        this.activity = activity;
    }

    /**
     * Inflates the new view for the adapter
     * @param context input context
     * @param cursor input cursor
     * @param parent the ViewGroup that this is contained in.
     * @return returns the created layout
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * Gets the text to display to the ListView.
     * @param view the view to bind
     * @param context input context
     * @param cursor the information cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find the display
        TextView tv = (TextView) view.findViewById(R.id.item_display);
        // Returns what text to display depending on which activity it is
        switch(activity) {
            case termsActivity:
                String termNumber = cursor
                        .getString(cursor.getColumnIndex(DBOpenHelper.TERM_NUMBER));
                String title = "Term #" + termNumber;
                tv.setText(title);
                break;
            case coursesActivity:
                String courseName = cursor
                        .getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NAME));
                tv.setText(courseName);
                break;
            case mentorsActivity:
                String mentorName = cursor
                        .getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_NAME));
                tv.setText(mentorName);
                break;
            case assessmentsActivity:
                String assessmentName = cursor
                        .getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NAME));
                tv.setText(assessmentName);
                break;
        }
    }
}
