package com.largerlife.learndroid.myyamba;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.largerlife.learndroid.myyamba.apitype.APIType;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;

/**
 * Created by LargerLife on 26/04/15.
 */
public class YambaApp extends Application {
    static final String TAG = "YambaApp";
    Twitter twitter;

    public SharedPreferences getPrefs() {
        return prefs;
    }

    SharedPreferences prefs;
    private OAuthSignpostClient oauthClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void createAPI(final APIType apiType) throws UnsupportedOperationException {
        if (apiType == APIType.TWITTER) {
            String userName = apiType.getUserName(prefs);
            String token = apiType.getAccessToken(prefs);
            String tokenSecret = apiType.getAccessTokenSecret(prefs);
            Log.d(TAG, String.format("Creating %s API with %s %s and %s %s.",
                    apiType.name().toLowerCase(),
                    APIType.API_USERNAME, userName,
                    APIType.API_TOKEN, token));
            String apiRoot = prefs.getString(apiType.getPrefix() + APIType.API_ROOT_URL, "");
            // We have token, use it
            apiType.getOAuthConsumer().setTokenWithSecret(token, tokenSecret);
            oauthClient = new OAuthSignpostClient(
                    apiType.getInfo().getAuthKey(),
                    apiType.getInfo().getAuthSecret(),
                    token,
                    tokenSecret);
            twitter = new Twitter(userName, oauthClient);
            if (apiRoot != null && !apiRoot.isEmpty()) {
                Log.d(TAG, apiType.getPrefix() + APIType.API_ROOT_URL + ":" + apiRoot);
                twitter.setAPIRootUrl(apiRoot);
            }
        } else {
            throw new UnsupportedOperationException(apiType.name() + " API is not supported yet.");
        }
    }

    public Object getOrCreateAPI(APIType apiType) {
        if (apiType == APIType.TWITTER) {
            if (twitter == null) {

            }
            Log.d(TAG, "Get API " + apiType.name());
            return twitter;
        }
        return null;
    }

    /**
     * Read the settings to see if we have token•
     *
     * @param apiType the {@link com.largerlife.learndroid.myyamba.apitype.APIType}
     * @return true if the authentication token found in the Preferences.
     */
    public boolean readAPIAccessTokenFromPrefs(APIType apiType) {
        Log.d(TAG, "Checking settings for auth " + apiType.getPrefix() + APIType.API_TOKEN);
        String token = apiType.getAccessToken(prefs);
        String tokenSecret = apiType.getAccessTokenSecret(prefs);
        if (token != null && tokenSecret != null) {
            Log.d(TAG, apiType.name() + " token found.");
            return prefs.contains(apiType.getPrefix() + "username");
        }
        Toast.makeText(getApplicationContext(), String.format("Please, authorize %s access.",
                        apiType.name().toLowerCase()),
                Toast.LENGTH_LONG).show();
        Log.d(TAG, apiType.name() + " token not found.");
        return false;
    }
}
