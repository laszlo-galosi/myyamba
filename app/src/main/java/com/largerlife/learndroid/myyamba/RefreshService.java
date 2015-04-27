package com.largerlife.learndroid.myyamba;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

/**
 * Created by LargerLife on 26/04/15.
 */
public class RefreshService extends IntentService {

    static final String TAG = "RefreshService";

    public RefreshService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, ".onHandleIntent");
        Twitter twitter = ((YambaApp) getApplication()).twitter;
        if (twitter == null) {
            Log.e(TAG, "Cannot start Updater service No API created");
//            Toast.makeText(this, "Cannot start updater service", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            List<Status> timeline = twitter.getHomeTimeline();
            for (Status status : timeline) {
                Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
            }
        } catch (TwitterException e) {
            Log.e(TAG, "Cannot retrieve user timeline:" + twitter.getSelf().getName(), e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, ".onDestroy");
    }
}
