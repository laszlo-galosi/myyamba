package com.largerlife.learndroid.myyamba;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.largerlife.learndroid.myyamba.apitype.APIType;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;

/**
 * Created by LargerLife on 26/04/15.
 */
public class YambaApp extends Application {
    static final String TAG = "YambaApp";
    Twitter twitter;
    SharedPreferences prefs;
    private OAuthSignpostClient oauthClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        APIType apiType = APIType.TWITTER;
        if (readAPIAccessTokenFromPrefs(apiType)) {
            createAPI(apiType);
        }
    }

    public void createAPI(APIType apiType) throws UnsupportedOperationException {
        if (apiType == APIType.TWITTER) {
            String userName = apiType.getUserName(prefs);
            String token = apiType.getAccessToken(prefs);
            String tokenSecret = apiType.getAccessTokenSecret(prefs);
            Log.d(TAG, apiType.getPrefix() + APIType.API_USERNAME + ":" + userName);
            String apiRoot = prefs.getString(apiType.getPrefix() + APIType.API_ROOT_URL, "");
            // We have token, use it
            apiType.getOAuthConsumer().setTokenWithSecret(token, tokenSecret);
            oauthClient = new OAuthSignpostClient(
                    apiType.getInfo().getAuthKey(),
                    apiType.getInfo().getAuthSecret(),
                    token,
                    tokenSecret);
            twitter = new Twitter(userName, oauthClient);
            if (!apiRoot.isEmpty()) {
                Log.d(TAG, apiType.getPrefix() + APIType.API_ROOT_URL + ":" + apiRoot);
                twitter.setAPIRootUrl(apiRoot);
            }
        } else {
            throw new UnsupportedOperationException(apiType.name() + " API is not supported yet.");
        }
    }

    /**
     * Read the settings to see if we have token•
     *
     * @param apiType the {@link com.largerlife.learndroid.myyamba.apitype.APIType}
     * @return true if the authentication token found in the Preferences.
     */
    private boolean readAPIAccessTokenFromPrefs(APIType apiType) {
        Log.d(TAG, "Checking settings for auth " + apiType.getPrefix() + APIType.API_TOKEN);
        String token = apiType.getAccessToken(prefs);
        String tokenSecret = apiType.getAccessTokenSecret(prefs);
        if (token != null && tokenSecret != null) {
            Log.d(TAG, "Found token.");
            return prefs.contains(apiType.getPrefix() + "username");
        }
        return false;
    }
}
