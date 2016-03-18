package com.largerlife.learndroid.myyamba;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import com.largerlife.learndroid.myyamba.apitype.APIType;
import com.largerlife.learndroid.myyamba.apitype.DownloadProfileImageTask;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

public class StatusActivity extends BaseActivity {

    static final String TAG = "StatusActivity";
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mActionFab;
    private ImageButton mProfileButton;

    @Override public String getScreenTag() {
        return TAG;
    }

    @Override public String getToolbarTitle() {
        return getString(R.string.app_name);
    }

    @Override public void onInitView() {
        //mToolbar.setLogo(R.mipmap.ic_launcher);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return onOptionsItemSelected(item);
            }
        });

        // Inflate a menu to be displayed in the toolbar
        mToolbar.inflateMenu(R.menu.menu_main);
        //toolbar.setLogo(R.mipmap.ic_launcher);
        mActionFab = (FloatingActionButton) findViewById(R.id.actionFab);
        mActionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendClicked(v);
            }
        });

        mProfileButton = (ImageButton) findViewById(R.id.profileFab);

        final Context selfContext = this;
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(selfContext, TimelineActivity.class);
                startActivity(intent);
            }
        });

        mApplication = ((YambaApp) getApplication());
        APIType apiType = APIType.TWITTER;

        if (mApplication.getOrCreateAPI(apiType) == null) {
            makeConnectSnackbar();
        }
    }

    @Override protected void onPause() {
        super.onPause();
        closeSoftKeyboard();
    }

    @Override public void onProfileImageDownloaded(final Drawable drawable) {
        Log.d(TAG, "setProfileImage:" + drawable);
        if (drawable == null) {
            makeConnectSnackbar();
            mProfileButton.setImageDrawable(
                  getResources().getDrawable(R.drawable.ic_person_white_48dp));
            mActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
        } else {
            mProfileButton.setImageDrawable(drawable);
            mActionFab.setImageDrawable(
                  getResources().getDrawable(R.drawable.ic_twitter_white_48dp));
            makeConfirmSnackBar(
                  String.format("%s %s %s", getString(R.string.logged_in_as),
                                APIType.TWITTER.name(),
                                mApplication.twitter.getSelf().getScreenName())

            );
        }
    }

    @Override public int getScreenLayout() {
        return R.layout.activity_status;
    }

    public void onSendClicked(View v) {
        closeSoftKeyboard();
        Twitter twitter = mApplication.twitter;
        if (twitter == null) {
            makeConnectSnackbar();
            return;
        }
        EditText statusField = (EditText) findViewById(R.id.et_status);
        if (TextUtils.isEmpty(statusField.getText())) {
            makeConfirmSnackBar(getString(R.string.say_something));
        } else {
            Log.d(TAG, "onClick with text:" + statusField.getText());
            new PostStatusTask().execute(statusField.getText().toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        Log.d(TAG, "Menu Inflate.");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        //        menuProfile = menu.findItem(R.id.menu_profile);
        new DownloadProfileImageTask(APIType.TWITTER, mApplication) {
            @Override
            protected void onPostExecute(Drawable result) {
                super.onPostExecute(result);
                onProfileImageDownloaded(result);
            }
        }.execute();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MENU", "" + item.getTitle());
        Log.d("MENU", "" + item.getTitle());
        int i = item.getItemId();
        Intent intentUpdater = new Intent(this, UpdaterService.class);
        Intent intentRefresher = new Intent(this, RefreshService.class);
        switch (i) {
            case R.id.menu_authorize:
                onAuthorizeClicked(this);
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

    public void onClickGetStatus(View view) {
        Twitter twitter = mApplication.twitter;
        if (twitter == null) {
            makeConnectSnackbar();
            return;
        }
        new GetStatusTask().execute();
    }

    /* Responsible for getting Twitter status */
    class GetStatusTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return mApplication.twitter.getStatus().text;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            makeConfirmSnackBar(result);
        }
    }

    /* Responsible for posting new status to Twitter */
    class PostStatusTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                mApplication.twitter.setStatus(params[0]);
                return "Successfully posted: " + params[0];
            } catch (TwitterException e) {
                return "Error connecting to server.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            makeConfirmSnackBar(result);
        }
    }

    public void closeSoftKeyboard() {
        InputMethodManager imm =
              (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token
        // from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
