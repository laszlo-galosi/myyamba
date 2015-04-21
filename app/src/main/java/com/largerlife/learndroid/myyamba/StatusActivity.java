package com.largerlife.learndroid.myyamba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;


public class StatusActivity extends ActionBarActivity implements View.OnClickListener {

    static final String TAG = "StatusActivity";
    private static final String OAUTH_KEY = "iYQ6Wv46bTlBgrofZ8HLli4LI";
    private static final String OAUTH_SECRET = "OwuKvmoUThpBrkI3BKkZuD13jZND7yuJM1vqtH9BziQK4w3YHf";
    private static final String OAUTH_CALLBACK_SCHEME = "x-largerlife   -oauth-twitter";
    private static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://callback";
    private static final String TWITTER_USER = "laszlo_galosi";

    private OAuthSignpostClient oauthClient;
    private OAuthConsumer mConsumer;
    private OAuthProvider mProvider;
    private Twitter twitter;
    SharedPreferences prefs;
    private EditText etStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

/*        mConsumer = new CommonsHttpOAuthConsumer(OAUTH_KEY, OAUTH_SECRET);
        mProvider = new DefaultOAuthProvider(
                "https://api.twitter.com/oauth/request_token",
                "https://api.twitter.com/oauth/access_token",
                "https://api.twitter.com/oauth/authorize");

        // Read the prefs to see if we have token
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs.getString("token", null);
        String tokenSecret = prefs.getString("tokenSecret", null);
        if (token != null && tokenSecret != null) {
            // We have token, use it
            mConsumer.setTokenWithSecret(token, tokenSecret);
            // Make a Twitter object
            oauthClient = new OAuthSignpostClient(OAUTH_KEY, OAUTH_SECRET, token,
                    tokenSecret);
            twitter = new Twitter(TWITTER_USER, oauthClient);
        }*/
        etStatus = (EditText) findViewById(R.id.et_status);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    /* Callback once we are done with the authorization of this app with Twitter. */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "intent: " + intent);

        // Check if this is a callback from OAuth
        Uri uri = intent.getData();
        if (uri != null && uri.getScheme().equals(OAUTH_CALLBACK_SCHEME)) {
            Log.d(TAG, "callback: " + uri.getPath());

            String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
            Log.d(TAG, "verifier: " + verifier);

            new RetrieveAccessTokenTask().execute(verifier);
        }

    }

    @Override
    public void onClick(View v) {
        String statusText = etStatus.getText().toString();
        Twitter twitter = new Twitter(
                Log.d(TAG, "onClick with text:" + statusText);
    }


    /* Responsible for starting the Twitter authorization */
   /* class OAuthAuthorizeTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String authUrl;
            String message = null;
            try {
                authUrl = mProvider.retrieveRequestToken(mConsumer, OAUTH_CALLBACK_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
                startActivity(intent);
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
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
            } catch (OAuthNotAuthorizedException e) {
                e.printStackTrace();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }
    }

    *//* Responsible for retrieving access tokens from twitter *//*
    class RetrieveAccessTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String message = null;
            String verifier = params[0];
            try {
                // Get the token
                Log.d(TAG, "mConsumer: " + mConsumer);
                Log.d(TAG, "mProvider: " + mProvider);
                mProvider.retrieveAccessToken(mConsumer, verifier);
                String token = mConsumer.getToken();
                String tokenSecret = mConsumer.getTokenSecret();
                mConsumer.setTokenWithSecret(token, tokenSecret);

                Log.d(TAG, String.format("verifier: %s, token: %s, tokenSecret: %s",
                        verifier, token, tokenSecret));

                // Store token in prefs
                prefs.edit().putString("token", token).putString("tokenSecret",
                        tokenSecret).commit();

                // Make a Twitter object
                oauthClient = new OAuthSignpostClient(OAUTH_KEY, OAUTH_SECRET, token,
                        tokenSecret);
                twitter = new Twitter("MarkoGargenta", oauthClient);

                Log.d(TAG, "token: " + token);
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
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }
    }*/
}
