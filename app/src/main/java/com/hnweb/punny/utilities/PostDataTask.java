package com.hnweb.punny.utilities;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.hnweb.punny.SplashScreen.EMAIL_KEY;
import static com.hnweb.punny.SplashScreen.FORM_DATA_TYPE;

public class PostDataTask extends AsyncTask<String, Void, Boolean>

{

    @Override
    protected Boolean doInBackground(String... contactData) {
        Boolean result = true;
        String url = contactData[0];
        String email = contactData[1];

        String postBody = "";

        try {

            postBody = EMAIL_KEY + "=" + URLEncoder.encode(email, "UTF-8");

        } catch (UnsupportedEncodingException ex) {
            result = false;
        }

        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException exception) {
            result = false;
        }
        return result;
    }


    @Override
    protected void onPostExecute(Boolean result) {
    }


}
