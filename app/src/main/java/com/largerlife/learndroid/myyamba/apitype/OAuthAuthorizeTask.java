package com.largerlife.learndroid.myyamba.apitype;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * Responsible for starting the API authorization
 * Created by LargerLife on 27/04/15.
 */
public class OAuthAuthorizeTask extends AsyncTask<Void, Void, String> {

    private final APIType apiType;
    private final Activity activity;

    public OAuthAuthorizeTask(APIType apiType, Activity activity) {
        this.apiType = apiType;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {
        String authUrl;
        String message = null;
        try {
            CommonsHttpOAuthConsumer consumer = apiType.getOAuthConsumer();
            OAuthProvider provider = apiType.getOAuthProvider();
            authUrl = provider.retrieveRequestToken(consumer, apiType.getInfo().getCallbackUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
            this.activity.startActivity(intent);
        } catch (OAuthMessageSignerException e) {
            message = "OAuthMessageSignerException";
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            message = "OAuthNotAuthorizedException";
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            message = "OAuthExpectationFailedException";
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            message = "OAuthCommunicationException";
            e.printStackTrace();
        }
        return message;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null) {
            Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
        }
    }
}
