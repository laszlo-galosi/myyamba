package com.largerlife.learndroid.myyamba.apitype;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.largerlife.learndroid.myyamba.YambaApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import winterwell.jtwitter.Twitter;

/**
 * Responsible for downloading Twitter profile image
 * Created by lgalosi on 27/04/15.
 */
public class DownloadProfileImageTask extends AsyncTask<Void, Void, Drawable> {

    private final APIType apiType;
    private final YambaApp app;
    private String tag;

    public DownloadProfileImageTask(APIType apiType, YambaApp app) {
        this.apiType = apiType;
        this.app = app;
        this.tag = apiType.name() + ".DownloadProfileImageTask";
    }

    @Override
    protected Drawable doInBackground(Void... params) {
        Drawable profileImage = null;
        if (apiType == APIType.TWITTER) {
            Twitter twitter = (Twitter) app.getOrCreateAPI(apiType);
            try {
                android.net.Uri profileImageURI = android.net.Uri.parse(twitter.getSelf().getProfileImageUrl().toString());
                Log.d(tag, "Downloading profile image for:" + twitter.account().toString() + " from url:" + profileImageURI.toString());
                profileImage = drawableFromURL(profileImageURI.toString());
            } catch (Exception e) {
                Log.e(tag, "Cannot retrieve profile image for " + apiType.getUserName(app.getPrefs()), e);
            }
        }
        return profileImage;
    }

    /**
     * Creates a drawable bitmap from the specified url, by downloading it.
     *
     * @param url the url to download.
     * @return a {@link BitmapDrawable}
     * @throws java.net.MalformedURLException
     * @throws java.io.IOException
     */
    BitmapDrawable drawableFromURL(String url) throws MalformedURLException, IOException {
        Bitmap bitmap;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-agent", "Mozilla/4.0");

        connection.connect();
        InputStream input = connection.getInputStream();

        bitmap = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(bitmap);
    }
}
