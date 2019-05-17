package com.hnweb.punnyfuzzleiap.punnyfuzzle;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.adapters.ScoreboardListAdaptor;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.Score;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AlertUtility;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardListActivity extends AppCompatActivity {
    ImageView imageView3;
    RecyclerView recycler_view;
    ArrayList<Score> scoreArrayList;
    ProgressDialog pDialog;
    ScoreboardListAdaptor scoreboardListAdaptor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboardlist);
        imageView3 = findViewById(R.id.imageView3);
        recycler_view = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(mLayoutManager);
        // Glide.with(this).load(R.raw.you_won).asGif().into(imageView3);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
       // Glide.with(this).load(R.raw.scoreboard_gif).asGif().into(imageView3);
        Glide.with(this)
                .load(R.raw.scoreboard_gif)
                .into(new GlideDrawableImageViewTarget(
                        imageView3));
        getScoreBoardList();
    }


    private void getScoreBoardList() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_SCORE_MULTI,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        scoreArrayList = new ArrayList<Score>();

                        System.out.println("GET_MULTI_PUZZLES_LIST" + response);
                        hideProgressDialog();
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String message = jobj.getString("message");

                            if (message_code == 1) {

                                scoreArrayList.clear();
                                JSONArray jsonArray = jobj.getJSONArray("listing");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jObject = jsonArray.getJSONObject(i);
                                    String id = /*(i + 1) + "";*/ jObject.getString("pid");
                                    String title = jObject.getString("title");
                                    String score = jObject.getString("score");
                                    String total_puzzel = jObject.getString("total_puzzel");
                                    String play_time = jObject.getString("play_time");
                                    String username = jObject.getString("username");
                                    String played_status = jObject.getString("played_status");


                                    Score p = new Score();
                                    p.setPid(id);
                                    p.setTitle(title);
                                    p.setScore(score);
                                    p.setTotal_puzzel(total_puzzel);
                                    p.setPlay_time(play_time);
                                    p.setPlayed_status(played_status);
                                    p.setUsername(username);


                                    scoreArrayList.add(p);

                                }

                            }

                            scoreboardListAdaptor = new ScoreboardListAdaptor(ScoreboardListActivity.this, scoreArrayList);
                            Log.e("puzleList", String.valueOf(scoreArrayList.size()));
                            recycler_view.setAdapter(scoreboardListAdaptor);
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(ScoreboardListActivity.this, error);
                        AlertUtility.showAlert(ScoreboardListActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("user_id", AppConstant.LOGIN_ID);
                    Log.e("user_id", AppConstant.LOGIN_ID);
                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(ScoreboardListActivity.this);
        requestQueue.add(stringRequest);

    }

    private void showProgressDialog() {

        pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }
}
