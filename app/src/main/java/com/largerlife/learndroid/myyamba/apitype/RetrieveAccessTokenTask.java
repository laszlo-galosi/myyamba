package com.largerlife.learndroid.myyamba.apitype;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.largerlife.learndroid.myyamba.YambaApp;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * Created by LargerLife on 27/04/15.
 */
public class RetrieveAccessTokenTask extends AsyncTask<String, Void, String> {
    private final APIType apiType;
    private final Activity activity;
    private final SharedPreferences prefs;

    private String tag;

    public RetrieveAccessTokenTask(APIType apiType, Activity activity, SharedPreferences prefs) {
        this.apiType = apiType;
        this.activity = activity;
        this.prefs = prefs;
        this.tag = apiType.name() + ".RetrieveAccessToken";
    }

    @Override
    protected String doInBackground(String... params) {
        String message = null;
        String verifier = params[0];
        String errorMessage = "Authenthorization error.";
        try {
            // Get the token
            CommonsHttpOAuthConsumer consumer = apiType.getOAuthConsumer();
            OAuthProvider provider = apiType.getOAuthProvider();
            Log.d(tag, "consumer: " + consumer);
            Log.d(tag, "mProvider: " + provider);
            provider.retrieveAccessToken(consumer, verifier);
            String token = consumer.getToken();
            String tokenSecret = consumer.getTokenSecret();
            Log.d(tag, String.format("verifier: %s, token: %s, tokenSecret: %s",
                    verifier, token, tokenSecret));
            // Store token in settings
            prefs.edit().putString(apiType.getPrefix() + APIType.API_TOKEN, token)
                    .putString(apiType.getPrefix() + APIType.API_TOKEN_SECRET, tokenSecret)
                    .commit();
            Log.d(tag, apiType.getPrefix() + APIType.API_TOKEN + ":" + apiType.getAccessToken(prefs));
            // Make an API object
            ((YambaApp) activity.getApplication()).createAPI(apiType);
            message = "Authorization succesful";
        } catch (OAuthMessageSignerException e) {
            message = errorMessage;
            Log.e(tag, message, e);
        } catch (OAuthNotAuthorizedException e) {
            message = errorMessage;
            Log.e(tag, message, e);
        } catch (OAuthExpectationFailedException e) {
            message = errorMessage;
            Log.e(tag, message, e);
        } catch (OAuthCommunicationException e) {
            message = errorMessage;
            Log.e(tag, message, e);
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
