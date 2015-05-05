package com.largerlife.learndroid.myyamba;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.util.Log;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class SettingsFragment extends PreferenceFragment {
    static final String TAG = "SettingsFragment";
    private SharedPreferences mSharedPreference;

    public SettingsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        this.mSharedPreference = ((YambaApp) getActivity().getApplication()).prefs;
        addPreferencesFromResource(R.xml.settings);
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                    bindPreferenceSummaryToValue(preferenceGroup.getPreference(j));
                }
            } else {
                bindPreferenceSummaryToValue(preference);
            }
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();
                    //display the original summary if not set
                    if (stringValue.isEmpty()) {
                        return true;
                    }
                    if (preference instanceof ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.
                        ListPreference listPreference = (ListPreference) preference;
                        int index = listPreference.findIndexOfValue(stringValue);
                        // Set the summary to reflect the new value.
                        preference.setSummary(
                                index >= 0
                                        ? listPreference.getEntries()[index]
                                        : null);
                    } else {
                        // For all other preferences, set the summary to the value's
                        // simple string representation.
                        preference.setSummary(stringValue);
                    }
                    Log.d(TAG, "pref changed : " + preference.getKey() + " " + value);
                    return true;
                }
            };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryListener
     */

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        Log.d(TAG, "bindPreferenceSummaryToValue:" + preference.getKey());
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryListener.onPreferenceChange(preference,
                mSharedPreference.getString(preference.getKey(), ""));
    }
}
