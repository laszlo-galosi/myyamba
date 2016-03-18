package com.largerlife.learndroid.myyamba;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.largerlife.learndroid.myyamba.apitype.APIType;
import com.largerlife.learndroid.myyamba.apitype.DownloadProfileImageTask;
import com.largerlife.learndroid.myyamba.apitype.OAuthAuthorizeTask;
import com.largerlife.learndroid.myyamba.apitype.RetrieveAccessTokenTask;
import oauth.signpost.OAuth;

/**
 * Created by László Gálosi on 17/03/16
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected YambaApp mApplication;
    protected CoordinatorLayout mCoordinatorLayout;
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getScreenTag(), "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(getScreenLayout());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //toolbar.setLogo(R.mipmap.ic_launcher);
        mToolbar.setTitle(getToolbarTitle());

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainLayout);
        onInitView();
        //        etStatus = (EditText) findViewById(R.id.et_status);
    }

    /**
     * Callback once we are done with the authorization of this mApplication with Twitter.
     */
    @Override public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(getScreenTag(), "OnNewIntent: " + intent);

        // Check if this is a callback from OAuth
        final APIType apiType = APIType.TWITTER;
        Uri uri = intent.getData();
        if (uri != null && uri.getScheme().equals(apiType.getInfo().getCallbackScheme())) {
            Log.d(getScreenTag(), "callback: " + uri.getPath());
            final String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
            Log.d(getScreenTag(), "verifier: " + verifier);
            new RetrieveAccessTokenTask(apiType, this, mApplication.prefs) {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if (result != null) {
                        new DownloadProfileImageTask(apiType, mApplication) {
                            @Override
                            protected void onPostExecute(Drawable result) {
                                super.onPostExecute(result);
                                onProfileImageDownloaded(result);
                            }
                        }.execute();
                    }
                }
            }.execute(verifier);
        }
    }

    protected void makeConfirmSnackBar(final String message) {
        SnackbarMaker.getInstance().setMessage(message)
                     .positiveAction(R.string.snackbar_action_ok)
                     .make(this, mCoordinatorLayout);
    }

    protected void makeConnectSnackbar() {
        final Activity selfActivity = this;
        SnackbarMaker.getInstance().setMessage(getString(R.string.login_twitter))
                     .positiveAction(R.string.login)
                     .positiveActionClicked(new View.OnClickListener() {
                         @Override public void onClick(View v) {
                             onAuthorizeClicked(selfActivity);
                         }
                     }).make(this, mCoordinatorLayout);
    }

    protected void onAuthorizeClicked(final Activity activity) {
        new OAuthAuthorizeTask(APIType.TWITTER, activity).execute();
    }

    public YambaApp getYambaApp() {
        return (YambaApp) getApplication();
    }

    public abstract String getScreenTag();

    public abstract String getToolbarTitle();

    public abstract void onInitView();

    public abstract void onProfileImageDownloaded(final Drawable result);

    public abstract int getScreenLayout();
}
