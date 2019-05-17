package com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities;

import android.os.AsyncTask;


public class PostDataTask extends AsyncTask<String, Void, Boolean>

{

    @Override
    protected Boolean doInBackground(String... contactData) {
        Boolean result = true;
        String url = contactData[0];
        String email = contactData[1];

        String postBody = "";

     /*   try {

           // postBody = EMAIL_KEY + "=" + URLEncoder.encode(email, "UTF-8");

        } catch (UnsupportedEncodingException ex) {
            result = false;
        }*/

     /*   try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException exception) {
            result = false;
        }*/
        return result;
    }


    @Override
    protected void onPostExecute(Boolean result) {
    }


}
