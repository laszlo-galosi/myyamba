package com.largerlife.learndroid.myyamba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.largerlife.learndroid.myyamba.apitype.APIType;
import com.largerlife.learndroid.myyamba.apitype.OAuthAuthorizeTask;


public class SettingsActivity extends ActionBarActivity implements OnSharedPreferenceChangeListener {
    static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        YambaApp app = ((YambaApp) getApplication());
        app.getPrefs().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        APIType apiType = APIType.getAPITypeByPreferenceKey(key);
        Log.d(TAG, key + " settings changed.");
        YambaApp app = ((YambaApp) getApplication());
        if (apiType == APIType.TWITTER) {
            if (key.equals(apiType.getPrefix() + APIType.API_USERNAME)) {
                Log.d(TAG, key + " settings changed, need reauthorize API access");
                Toast.makeText(SettingsActivity.this, "Settings changed. Please, authorize Twitter access.", Toast.LENGTH_LONG).show();
                app.twitter = null;
                new OAuthAuthorizeTask(apiType, this).execute();
            }
        }
    }
}
