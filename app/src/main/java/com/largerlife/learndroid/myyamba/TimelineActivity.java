package com.largerlife.learndroid.myyamba;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.largerlife.learndroid.myyamba.apitype.APIType;
import com.largerlife.learndroid.myyamba.apitype.DownloadProfileImageTask;
import com.largerlife.learndroid.myyamba.apitype.OAuthAuthorizeTask;
import winterwell.jtwitter.Twitter;

public class TimelineActivity extends BaseActivity {

    private static final String TAG = "TimelineActivity";
    private ImageView mProfileImage;
    private TextView mProfileNameView;
    private FloatingActionButton mActionFab;

    @Override public String getScreenTag() {
        return TAG;
    }

    @Override public String getToolbarTitle() {
        return getString(R.string.title_activity_timeline);
    }

    @Override public void onInitView() {
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return onOptionsItemSelected(item);
            }
        });
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mProfileNameView = (TextView) findViewById(R.id.profileName);
        mActionFab = (FloatingActionButton) findViewById(R.id.actionFab);

        mActionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRefresher = new Intent(TimelineActivity.this, RefreshService.class);
                startService(intentRefresher);
            }
        });

        mApplication = ((YambaApp) getApplication());
        APIType apiType = APIType.TWITTER;

        Twitter twitter = (Twitter) mApplication.getOrCreateAPI(apiType);
        if (twitter == null) {
            makeConnectSnackbar();
        } else {
            mProfileNameView.setText(twitter.getSelf().getScreenName());
            new DownloadProfileImageTask(apiType, mApplication) {
                @Override
                protected void onPostExecute(Drawable result) {
                    super.onPostExecute(result);
                    onProfileImageDownloaded(result);
                }
            }.execute();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        mApplication.clearTimeLine();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "" + item.getTitle());
        int i = item.getItemId();
        Intent intentRefresher = new Intent(this, RefreshService.class);
        switch (i) {
            case R.id.menu_authorize:
                new OAuthAuthorizeTask(APIType.TWITTER, this).execute();
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
        return super.onOptionsItemSelected(item);
    }

    @Override public void onProfileImageDownloaded(Drawable drawable) {
        Log.d(TAG, "onProfileImageDownloaded:" + drawable);
        if (drawable == null) {
            makeConnectSnackbar();
            mProfileImage.setImageDrawable(
                  getResources().getDrawable(R.drawable.ic_person_white_48dp));
            mActionFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
        } else {
            mProfileImage.setImageDrawable(drawable);
            mActionFab.setImageDrawable(
                  getResources().getDrawable(R.drawable.ic_refresh_white_48dp));
        }
    }

    @Override public int getScreenLayout() {
        return R.layout.activity_timeline;
    }
}
