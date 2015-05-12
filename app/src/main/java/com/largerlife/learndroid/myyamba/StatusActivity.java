package com.largerlife.learndroid.myyamba;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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

import oauth.signpost.OAuth;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;


public class StatusActivity extends ActionBarActivity {

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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            actionBar.setHomeButtonEnabled(true);
        }
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

        app = ((YambaApp) getApplication());
        APIType apiType = APIType.TWITTER;

        if (app.getOrCreateAPI(apiType) == null) {
            Toast.makeText(getApplicationContext(), String.format("Please, authorize %s access.",
                            apiType.name().toLowerCase()),
                    Toast.LENGTH_LONG).show();
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
            new RetrieveAccessTokenTask(apiType, this, app.prefs) {
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
        Twitter twitter = app.twitter;
        if (twitter == null) {
            Toast.makeText(this, "Authenticate first", Toast.LENGTH_LONG).show();
            return;
        }
        EditText status = (EditText) findViewById(R.id.et_status);
        new PostStatusTask().execute(status.getText().toString());
        Log.d(TAG, "onClick with text:" + status);
    }

    public void onClickGetStatus(View view) {
        Twitter twitter = app.twitter;
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

    /* Responsible for getting Twitter status */
    class GetStatusTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return app.twitter.getStatus().text;
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
                app.twitter.setStatus(params[0]);
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
