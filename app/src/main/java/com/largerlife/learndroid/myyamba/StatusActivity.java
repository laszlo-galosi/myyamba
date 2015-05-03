package com.largerlife.learndroid.myyamba;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.largerlife.learndroid.myyamba.apitype.APIType;
import com.largerlife.learndroid.myyamba.apitype.DownloadProfileImageTask;
import com.largerlife.learndroid.myyamba.apitype.OAuthAuthorizeTask;
import com.largerlife.learndroid.myyamba.apitype.RetrieveAccessTokenTask;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import oauth.signpost.OAuth;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;


public class StatusActivity extends ActionBarActivity implements PreferenceChangeListener {

    static final String TAG = "StatusActivity";
    private EditText etStatus;
    private MenuItem menuProfile;
    private YambaApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.yamba);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

        app = ((YambaApp) getApplication());
        APIType apiType = APIType.TWITTER;
        if (app.readAPIAccessTokenFromPrefs(apiType)) {
            app.createAPI(apiType);
        }
        etStatus = (EditText) findViewById(R.id.et_status);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        Log.d(TAG, "Menu Inflate.");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menuProfile = menu.findItem(R.id.menu_profile);
        new DownloadProfileImageTask(APIType.TWITTER, app) {
            @Override
            protected void onPostExecute(Drawable result) {
                super.onPostExecute(result);
                setProfileImage(result);
            }
        }.execute();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MENU", "" + item.getTitle());
        int i = item.getItemId();
        Intent intentUpdater = new Intent(this, UpdaterService.class);
        Intent intentRefresher = new Intent(this, RefreshService.class);
        switch (i) {
            case R.id.menu_authorize:
                onClickAuthorize(getCurrentFocus());
                break;
            case R.id.menu_profile:
                onClickGetStatus(getCurrentFocus());
                break;
            case R.id.menu_start_service:
                startService(intentUpdater);
                break;
            case R.id.menu_stop_service:
                stopService(intentUpdater);
                break;
            case R.id.menu_refresh:
                startService(intentRefresher);
                break;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                Log.d(TAG, "Invalid menu action");
                return false;
        }
        return true;
    }

    /* Callback once we are done with the authorization of this app with Twitter. */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "OnNewIntent: " + intent);

        // Check if this is a callback from OAuth
        final APIType apiType = APIType.TWITTER;
        Uri uri = intent.getData();
        if (uri != null && uri.getScheme().equals(apiType.getInfo().getCallbackScheme())) {
            Log.d(TAG, "callback: " + uri.getPath());
            final String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
            Log.d(TAG, "verifier: " + verifier);
            new RetrieveAccessTokenTask(apiType, this, ((YambaApp) getApplication()).prefs) {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if (result != null) {
                        //Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
                        new DownloadProfileImageTask(apiType, app) {
                            @Override
                            protected void onPostExecute(Drawable result) {
                                super.onPostExecute(result);
                                setProfileImage(result);
                            }
                        }.execute();
                    }
                }
            }.execute(verifier);
        }
    }

    public void onClickAuthorize(View view) {
        new OAuthAuthorizeTask(APIType.TWITTER, this).execute();
    }

    public void onClickTweet(View v) {
        Twitter twitter = ((YambaApp) getApplication()).twitter;
        if (twitter == null) {
            Toast.makeText(this, "Authenticate first", Toast.LENGTH_LONG).show();
            return;
        }
        EditText status = (EditText) findViewById(R.id.et_status);
        new PostStatusTask().execute(status.getText().toString());
        Log.d(TAG, "onClick with text:" + status);
    }

    public void onClickGetStatus(View view) {
        Twitter twitter = ((YambaApp) getApplication()).twitter;
        if (twitter == null) {
            Toast.makeText(this, "Authenticate first", Toast.LENGTH_LONG).show();
            return;
        }
        new GetStatusTask().execute();
    }

    private void setProfileImage(Drawable drawable) {
        Log.d(TAG, "setProfileImage:" + drawable);
        if (drawable == null) {
            Toast.makeText(StatusActivity.this, "Please, authorize Twitter access.", Toast.LENGTH_LONG).show();
            drawable = getResources().getDrawable(R.drawable.default_profile);
        } else {
            Toast.makeText(StatusActivity.this, "Logged in as " + app.twitter.getSelf().getScreenName(), Toast.LENGTH_LONG).show();
        }
        menuProfile.setIcon(drawable);
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent pce) {
        APIType apiType = APIType.getAPITypeByPreferenceKey(pce.getKey());
        Log.d(TAG, pce.getKey() + " settings changed.");
        if (apiType == APIType.TWITTER) {
            if (pce.getKey().equals(apiType.getPrefix() + APIType.API_USERNAME)) {
                Log.d(TAG, pce.getKey() + " settings changed, need reauthorize API access");
                Toast.makeText(StatusActivity.this, "Settings changed. Please, authorize Twitter access.", Toast.LENGTH_LONG).show();
                ((YambaApp) getApplication()).twitter = null;
                new OAuthAuthorizeTask(apiType, this).execute();
            }
        }
    }

    /* Responsible for getting Twitter status */
    class GetStatusTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return ((YambaApp) getApplication()).twitter.getStatus().text;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }

    /* Responsible for posting new status to Twitter */
    class PostStatusTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                ((YambaApp) getApplication()).twitter.setStatus(params[0]);
                return "Successfully posted: " + params[0];
            } catch (TwitterException e) {
                return "Error connecting to server.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
}
