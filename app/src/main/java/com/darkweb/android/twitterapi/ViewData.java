package com.darkweb.android.twitterapi;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ViewData extends AppCompatActivity {

   private static final String TWITTER_KEY = "";
   private static final String TWITTER_SECRET = "";

   // Pls type your Twitter KEy



    final static String ScreenName = "android";
    final static String LOG_TAG = "rnc";
    ListView lv_list;
    ArrayList<String> al_text = new ArrayList<>();
    Adapter obj_adapter;
    String results = null;

    EditText searchdata;
    ImageButton search;

    int count = 15;
    String type = "popular";

    private List<ApplicationInfo> mAppList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewdata);
        lv_list = findViewById(R.id.lv_list);

        mAppList = getPackageManager().getInstalledApplications(0);

        searchdata = findViewById(R.id.search_data);
        search = findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadTweets();
            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv_list,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    al_text.remove(position);
                                    obj_adapter.notifyDataSetChanged();

                                    new DownloadTwitterTask().execute(ScreenName, String.valueOf(count+5),type="recent");

                                }

                            }
                        });
        lv_list.setOnTouchListener(touchListener);


    }

    // download twitter timeline after first checking to see if there is a network connection
    public void downloadTweets() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadTwitterTask().execute(ScreenName);
            Toast.makeText(this, "Network Connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    // Uses an AsyncTask to download a Twitter user's timeline
    private class DownloadTwitterTask extends AsyncTask<String, Void, String> {

        //type your twitter data

        final static String CONSUMER_KEY = "";
        final static String CONSUMER_SECRET = "";
        final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
        final static String TwitterStreamURL = "https://api.twitter.com/1.1/search/tweets.json?q=%23";
        final ProgressDialog dialog = new ProgressDialog(ViewData.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Loading");
            dialog.setMessage("Please wait");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... statuses) {
            String result = null;

            if (statuses.length > 0) {
                result = getTwitterStream(statuses[0]);
            }
            return result;
        }

        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
        @Override
        protected void onPostExecute(String result) {
            Log.e("result",results);
            dialog.dismiss();

            try {
                JSONObject json = new JSONObject(results);
                JSONArray articles = json.getJSONArray("statuses");

//              JSONArray  jsonarray = jsonobject.getJSONArray("items");

                al_text.clear();
                for (int i=0; i<articles.length();i++){


                    al_text.add(articles.getJSONObject(i).getString("text") );


                }
            }catch (Exception e){
                e.printStackTrace();
            }



            obj_adapter= new Adapter(getApplicationContext(), al_text);
            lv_list.setAdapter(obj_adapter);











        }



        // convert a JSON authentication object into an Authenticated object
        private Authenticated jsonToAuthenticated(String rawAuthorization) {
            Authenticated auth = null;
            if (rawAuthorization != null && rawAuthorization.length() > 0) {
                try {
                    Gson gson = new Gson();
                    auth = gson.fromJson(rawAuthorization, Authenticated.class);
                } catch (IllegalStateException ex) {
                    // just eat the exception
                }
            }
            return auth;
        }

        private String getResponseBody(HttpRequestBase request) {
            StringBuilder sb = new StringBuilder();
            try {

                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpResponse response = httpClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                String reason = response.getStatusLine().getReasonPhrase();

                if (statusCode == 200) {

                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();

                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    sb.append(reason);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (ClientProtocolException ex1) {
            } catch (IOException ex2) {
            }
            return sb.toString();
        }

        private String getTwitterStream(String screenName) {


            // Step 1: Encode consumer key and secret
            try {
                // URL encode the consumer key and secret
                String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
                String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

                // Concatenate the encoded consumer key, a colon character, and the
                // encoded consumer secret
                String combined = urlApiKey + ":" + urlApiSecret;

                // Base64 encode the string
                String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

                // Step 2: Obtain a bearer token
                HttpPost httpPost = new HttpPost(TwitterTokenURL);
                httpPost.setHeader("Authorization", "Basic " + base64Encoded);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
                String rawAuthorization = getResponseBody(httpPost);
                Authenticated auth = jsonToAuthenticated(rawAuthorization);

                // Applications should verify that the value associated with the
                // token_type key of the returned object is bearer
                if (auth != null && auth.token_type.equals("bearer")) {

                    // Step 3: Authenticate API requests with bearer token
                    HttpGet httpGet = new HttpGet(TwitterStreamURL + searchdata.getText().toString() + "&count="+count +"&result_type="+type);

                    Log.e("url",httpGet.toString());

                    // construct a normal HTTPS request and include an Authorization
                    // header with the value of Bearer <>
                    httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
                    httpGet.setHeader("Content-Type", "application/json");
                    // update the results with the body of the response
                    results = getResponseBody(httpGet);

                    Log.e("response",results.toString());
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (IllegalStateException ex1) {
            }
            return results;
        }
    }
}
