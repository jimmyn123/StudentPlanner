/**
 * The SharePreferences class that extends a PreferenceFragment. Saves the SharedPreferences.
 * @author Jimmy Nguyen
 * @version 3/6/2017
 */
package com.example.studentplanner.studentplanner;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    /**
     * Creates the preferences from the xml file preferences.
     * @param savedInstanceState data to load if app crashes
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Adds the preferences
        addPreferencesFromResource(R.xml.preferences);
    }
}
