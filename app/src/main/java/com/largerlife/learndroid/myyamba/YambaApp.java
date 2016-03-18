package com.largerlife.learndroid.myyamba;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.largerlife.learndroid.myyamba.apitype.APIType;
import java.util.ArrayList;
import java.util.List;
import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;

/**
 * Created by LargerLife on 26/04/15.
 */
public class YambaApp extends Application {
    static final String TAG = "YambaApp";
    final List<Status> mTimeLineStatuses = new ArrayList<>(40);
    Twitter twitter;
    SharedPreferences prefs;
    private RecyclerView.AdapterDataObserver mTimeLineObserver;
    private OAuthSignpostClient oauthClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public Object getOrCreateAPI(APIType apiType) {
        if (apiType == APIType.TWITTER) {
            if (twitter == null && readAPIAccessTokenFromPrefs(apiType)) {
                createAPI(apiType);
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
        Log.d(TAG, apiType.name() + " token not found.");
        return false;
    }

    public void createAPI(final APIType apiType) throws UnsupportedOperationException {
        if (apiType == APIType.TWITTER) {
            String userName = apiType.getUserName(prefs);
            String token = apiType.getAccessToken(prefs);
            String tokenSecret = apiType.getAccessTokenSecret(prefs);
            Log.d(TAG, String.format("Creating %s API with %s %s and     %s %s.",
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

    public void addStatus(final Status status) {
        Log.d("addStatus", String.format("@%s at %s %s", status.user.name, status.getCreatedAt(),
                                         status.getDisplayText()));
        mTimeLineStatuses.add(status);
        if (mTimeLineObserver != null) {
            mTimeLineObserver.onItemRangeInserted(0, 1);
        }
    }

    public void setTimeLine(List<Status> statusList) {
        mTimeLineStatuses.addAll(statusList);
        if (mTimeLineObserver != null) {
            mTimeLineObserver.onItemRangeInserted(0, statusList.size());
        }
    }

    public void clearTimeLine() {
        int size = mTimeLineStatuses.size();
        mTimeLineStatuses.clear();
        if (mTimeLineObserver != null) {
            mTimeLineObserver.onItemRangeRemoved(0, size);
        }
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public List<Status> getTimeLine() {
        return mTimeLineStatuses;
    }

    public YambaApp setTimeLineObserver(final RecyclerView.AdapterDataObserver timeLineObserver) {
        mTimeLineObserver = timeLineObserver;
        return this;
    }
}
