/**
 * The SharePreferences class that extends a PreferenceFragment. Saves the SharedPreferences.
 * @author Jimmy Nguyen
 * @version 2/25/2017
 */
package com.example.studentplanner.studentplanner;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    /**
     * Creates the prefernces from the xml file preferences.
     * @param savedInstanceState data to load if app crashes
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Adds the preferences
        addPreferencesFromResource(R.xml.preferences);
    }
}
