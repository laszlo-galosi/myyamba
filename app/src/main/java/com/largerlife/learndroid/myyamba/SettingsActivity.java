package com.largerlife.learndroid.myyamba;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import com.largerlife.learndroid.myyamba.apitype.APIType;
import com.largerlife.learndroid.myyamba.apitype.OAuthAuthorizeTask;


public class SettingsActivity extends BaseActivity implements OnSharedPreferenceChangeListener {
    static final String TAG = "SettingsActivity";
    SharedPreferences prefs;

    @Override public String getScreenTag() {
        return TAG;
    }

    @Override public String getToolbarTitle() {
        return getString(R.string.action_settings);
    }

    @Override public void onInitView() {
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getFragmentManager().beginTransaction()
                            .replace(R.id.main_content, new SettingsFragment())
                            .commit();
        this.prefs = ((YambaApp) getApplication()).prefs;
    }

    @Override public void onProfileImageDownloaded(final Drawable result) {
    }

    @Override public int getScreenLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        // Unregister the listener whenever a key changes
        this.prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        this.prefs.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        APIType apiType = APIType.getAPITypeByPreferenceKey(key);
        Log.d(TAG, key + " settings changed.");
        YambaApp app = ((YambaApp) getApplication());
        if (apiType == APIType.TWITTER) {
            if (key.equals(apiType.getPrefix() + APIType.API_USERNAME)) {
                Log.d(TAG, key + " settings changed, need reauthorize API access");
                makeConfirmSnackBar(getString(R.string.settings_changed));
                app.twitter = null;
                new OAuthAuthorizeTask(apiType, this).execute();
            }
        }
    }
}
