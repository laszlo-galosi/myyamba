package com.largerlife.learndroid.myyamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by LargerLife on 05/05/15.
 */
public class BootReceiver extends BroadcastReceiver {

    static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        context.startService(new Intent(context, UpdaterService.class));
    }
}
