package com.largerlife.learndroid.myyamba.apitype;

import android.content.SharedPreferences;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

/**
 * Created by LargerLife on 26/04/15.
 */
public enum APIType {
    TWITTER("twitter_", new TwitterAPIInfo());

    public static final String API_TOKEN = "token";

    public static final String API_TOKEN_SECRET = "tokenSecret";
    public static final String API_USERNAME = "username";
    public static final String API_ROOT_URL = "apiRoot";
    private final String prefix;

    private final IAPITypeInfo apiTypeInfo;

    private CommonsHttpOAuthConsumer mConsumer;

    private String token;

    private String tokenSecret;

    private OAuthProvider mProvider;

    APIType(String prefix, IAPITypeInfo apiTypeInfo) {
        this.prefix = prefix;
        this.apiTypeInfo = apiTypeInfo;
        this.mConsumer = new CommonsHttpOAuthConsumer(
                apiTypeInfo.getAuthKey(),
                apiTypeInfo.getAuthSecret());
        this.mProvider = new DefaultOAuthProvider(
                apiTypeInfo.getRequestTokenEndPointUrl(),
                apiTypeInfo.getAccesTokenEndPointUrl(),
                apiTypeInfo.getAuthorizationWebsiteUrl());
    }

    public String getPrefix() {
        return prefix;
    }

    public IAPITypeInfo getInfo() {
        return apiTypeInfo;
    }

    public CommonsHttpOAuthConsumer getOAuthConsumer() {
        return mConsumer;
    }

    public OAuthProvider getOAuthProvider() {
        return mProvider;
    }

    public String getUserName(SharedPreferences prefs) {
        return prefs.getString(getPrefix() + API_USERNAME, "");
    }

    public String getAccessToken(SharedPreferences prefs) {
        return prefs.getString(getPrefix() + API_TOKEN, null);
    }

    public String getAccessTokenSecret(SharedPreferences prefs) {
        return prefs.getString(getPrefix() + API_TOKEN_SECRET, null);
    }

    public String getAPIRootUrl(SharedPreferences prefs) {
        return prefs.getString(getPrefix() + API_ROOT_URL, "");
    }
}
