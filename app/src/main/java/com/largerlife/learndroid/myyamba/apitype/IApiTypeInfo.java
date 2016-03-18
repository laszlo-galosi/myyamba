package com.largerlife.learndroid.myyamba.apitype;

/**
 * Created by LargerLife on 26/04/15.
 */
public interface IApiTypeInfo {

    public APIType getAPIType();

    public String getAuthKey();

    public String getAuthSecret();

    public String getCallbackScheme();

    public String getCallbackUrl();

    public String getRequestTokenEndPointUrl();

    public String getAccesTokenEndPointUrl();

    public String getAuthorizationWebsiteUrl();
}
