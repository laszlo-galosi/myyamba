package com.largerlife.learndroid.myyamba;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.largerlife.learndroid.myyamba.apitype.APIType;

import java.util.List;

import winterwell.jtwitter.Status;
import winterwell.jtwitter.TwitterException;

public class UpdaterService extends Service {

    static final String TAG = "UpdaterService";
    private static final long DEFAULT_DELAY = 30;
    private static final String DELAY_SETTING_NAME = "refreshRate";
    private boolean running = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, ".onCreate");
        this.running = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final YambaApp app = ((YambaApp) getApplication());

        if (app.twitter == null) {
            Log.e(TAG, "Cannot start Updater service No API created");
            Toast.makeText(this, "Cannot start updater service", Toast.LENGTH_LONG).show();
            return super.onStartCommand(intent, flags, startId);
        }
        try {
            new Thread() {
                public void run() {
                    running = true;
                    while (running) {
                        List<Status> timeline = app.twitter.getHomeTimeline();
                        try {
                            for (Status status : timeline) {
                                Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
                            }
                            long delay = Integer.parseInt(
                                    app.prefs.getString(APIType.TWITTER.getPrefix() + DELAY_SETTING_NAME,
                                            "" + DEFAULT_DELAY));
                            Log.d(TAG, String.format("Waiting for %d seconds ...", delay));
                            Thread.sleep(delay * 1000);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Network error while retriveing timeline:", e);
                            running = false;
                        }
                    }
                }
            }.start();
        } catch (TwitterException e) {
            Log.e(TAG, "Cannot retrieve user timeline:" + app.twitter.getSelf().getName(), e);
        }
        Log.d(TAG, ".onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, ".onDestroy");
        this.running = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
