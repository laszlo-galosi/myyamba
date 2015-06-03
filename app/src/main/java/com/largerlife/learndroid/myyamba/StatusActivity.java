package com.largerlife.learndroid.myyamba;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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


public class StatusActivity extends AppCompatActivity {

    static final String TAG = "StatusActivity";
    private EditText etStatus;
    private MenuItem menuProfile;
    private YambaApp app;
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mActionFab;
    private FloatingActionButton mProfileFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return onOptionsItemSelected(item);
            }
        });

        // Inflate a menu to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.menu_main);
        //toolbar.setLogo(R.mipmap.ic_launcher);

        mActionFab = (FloatingActionButton) findViewById(R.id.actionFab);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainLayout);

        mActionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdate(v);
            }
        });

        mProfileFab = (FloatingActionButton) findViewById(R.id.profileFab);

        final Context selfContext = this;
        mProfileFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(selfContext, TimelineActivity.class));
            }
        });


        app = ((YambaApp) getApplication());
        APIType apiType = APIType.TWITTER;

        if (app.getOrCreateAPI(apiType) == null) {
            makeConnectSnackBar();
        }
//        etStatus = (EditText) findViewById(R.id.et_status);
    }

    private void makeConnectSnackBar() {
        Snackbar.make(mCoordinatorLayout,
                getString(R.string.login_twitter), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.login), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickAuthorize(v);
                    }
                }).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        Log.d(TAG, "Menu Inflate.");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
//        menuProfile = menu.findItem(R.id.menu_profile);
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
/*            case R.id.menu_profile:
                onClickGetStatus(getCurrentFocus());
                startActivity(new Intent(this, TimelineActivity.class));
                break;*/
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

    public void onClickUpdate(View v) {
        Twitter twitter = app.twitter;
        if (twitter == null) {
            makeConnectSnackBar();
            return;
        }
        EditText status = (EditText) findViewById(R.id.et_status);
        new PostStatusTask().execute(status.getText().toString());
        Log.d(TAG, "onClick with text:" + status);
    }

    public void onClickGetStatus(View view) {
        Twitter twitter = app.twitter;
        if (twitter == null) {
            makeConnectSnackBar();
            return;
        }
        new GetStatusTask().execute();
    }

    private void setProfileImage(Drawable drawable) {
        Log.d(TAG, "setProfileImage:" + drawable);
        if (drawable == null) {
            makeConnectSnackBar();
            drawable = getResources().getDrawable(R.drawable.ic_person_white_48dp);
        } else {
//            Toast.makeText(StatusActivity.this, "Logged in as " + app.twitter.getSelf().getScreenName(), Toast.LENGTH_LONG).show();
            Snackbar.make(mCoordinatorLayout,
                    getString(R.string.logged_in_as)
                            + " " + APIType.TWITTER.name()
                            + " " + app.twitter.getSelf().getScreenName()
                    , Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.btn_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClickAuthorize(v);
                        }
                    }).show();
        }
//        menuProfile.setIcon(drawable);
        mProfileFab.setImageDrawable(drawable);
        mActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_create_white_48dp));
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
