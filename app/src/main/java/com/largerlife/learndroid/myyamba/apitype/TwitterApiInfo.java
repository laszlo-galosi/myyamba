package com.largerlife.learndroid.myyamba.apitype;

/**
 * Create Twitter API key at https://dev.twitter.com/apps
 * Created by LargerLife on 26/04/15.
 */
public class TwitterAPIInfo implements IAPITypeInfo {

    private static final String AUTH_KEY = "iYQ6Wv46bTlBgrofZ8HLli4LI";
    private static final String AUTH_SECRET = "OwuKvmoUThpBrkI3BKkZuD13jZND7yuJM1vqtH9BziQK4w3YHf";
    private static final String CALLBACK_SCHEME = "x-largerlife-oauth-twitter";

    private static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
    private static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
    private static final String AUTH_WEBSITE_URL = "https://api.twitter.com/oauth/authorize";

    @Override
    public APIType getAPIType() {
        return APIType.TWITTER;
    }

    @Override
    public String getAuthKey() {
        return AUTH_KEY;
    }

    @Override
    public String getAuthSecret() {
        return AUTH_SECRET;
    }

    @Override
    public String getCallbackScheme() {
        return CALLBACK_SCHEME;
    }

    @Override
    public String getCallbackUrl() {
        return CALLBACK_SCHEME + "://callback";
    }

    @Override
    public String getRequestTokenEndPointUrl() {
        return REQUEST_URL;
    }

    @Override
    public String getAccesTokenEndPointUrl() {
        return ACCESS_URL;
    }

    @Override
    public String getAuthorizationWebsiteUrl() {
        return AUTH_WEBSITE_URL;
    }
}
