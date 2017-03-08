/**
 * A DatePicker fragment that extends from a DialogFragment.
 * Creates a new dialogue to select a date.
 * @author Jimmy Nguyen
 * @version 3/8/2017
 */

package com.example.studentplanner.studentplanner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment {
    // constants that represent which activity is using the fragment
    private static final int termsActivity = 1;
    private static final int coursesActivity = 2;
    private static final int assessmentActivity = 3;

    /**
     * Constructs a new dialogue for picking dates.
     * @param savedInstanceState data used to restore
     * @return a new DatePicker dialogue with today's date as default
     */
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // create a calendar to get today's date.
        final Calendar c = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener listener = null;
        // gets the arguments to find out which activity
        Bundle args = getArguments();
        // determines which activity to set the listener for
        switch (args.getInt("activity")){
            case termsActivity:
                listener = (AddTermsActivity)getActivity();
                break;
            case coursesActivity:
                listener = (AddCoursesActivity)getActivity();
                break;
            case assessmentActivity:
                listener = (AddAssessmentsActivity)getActivity();
        }
        // returns today's date as the default
        return new DatePickerDialog(getActivity(), listener,
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }
}
