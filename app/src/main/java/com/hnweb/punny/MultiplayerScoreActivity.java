package com.hnweb.punny;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.punny.adapters.MultiplayerPuzzlesAdapter;
import com.hnweb.punny.bo.Puzzle;
import com.hnweb.punny.utilities.AlertUtility;
import com.hnweb.punny.utilities.App;
import com.hnweb.punny.utilities.AppConstant;
import com.hnweb.punny.utilities.AppUtils;
import com.hnweb.punny.utilities.MusicManager;
import com.hnweb.punny.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Priyanka Harmale on 03/01/2018.
 */

public class MultiplayerScoreActivity extends AppCompatActivity {
    String TAG = MultiplayerScoreActivity.class.getSimpleName();
    public static String EXTRA_SCORE = "extra_score";

    private Boolean continueMusic;

    private ProgressDialog pDialog;
    ImageButton btn_submit;
    private TextView lblscore, tv_username, tv_time, tv_scoremulti, tv_total;
    String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayerscoreboard);
        try {
            Toolbar toolbar = ( Toolbar ) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            lblscore = ( TextView ) findViewById(R.id.lblscore);
            btn_submit = findViewById(R.id.btn_submit);
            tv_username = findViewById(R.id.tv_username);
            tv_time = findViewById(R.id.tv_time);
            tv_scoremulti = findViewById(R.id.tv_scoremulti);
            tv_total = findViewById(R.id.tv_total);
            // lblMotivationText = ( TextView ) findViewById(R.id.lbl_motivation_text);
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Getting Score...");
            pDialog.setCancelable(true);


            Bundle bundle = getIntent().getExtras();
            pid = bundle.getString("pid");
            tv_username.setText(AppConstant.LOGIN_NAME);
            //lblMotivationText.setText(getMotivationText(score));
//            }

            submitpuzzle();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(ScoreActivity):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MultiplayerScoreActivity.this, CreditListActivity.class);
                startActivity(intent);
            }
        });
    }

    public void btnBack_onClick(View view) {
        try {
            MultiplayerScoreActivity.this.onBackPressed();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnBack_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (AppConstant.IS_MUSIC_ON == true) {
            if (!continueMusic) {
                MusicManager.pause();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConstant.IS_MUSIC_ON == true) {
            continueMusic = false;
            MusicManager.start(this, MusicManager.MUSIC_MENU);
        }
    }

    @Override
    protected void onStop() {
        try {
            App.getInstance().getRequestQueue().cancelAll(TAG);

            if (pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            App.getInstance().cancelPendingRequests(TAG);

            if (pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void showProgressDialog() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Getting Score...");
        pDialog.setCancelable(true);

        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }


    private void submitpuzzle() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.SUBMIT_PUZZLE,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        System.out.println("GET_MULTI_SCORE" + response);
                        hideProgressDialog();
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            final JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String message = jobj.getString("message");


                            if (message_code == 1) {

                                Utilities.showAlertDailog(MultiplayerScoreActivity.this, "PunnyFuzzles", message, "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                try {
                                                    JSONObject jsonObject = jobj.getJSONObject("score");
                                                    String score = jsonObject.getString("score");
                                                    String total_puzzel = jsonObject.getString("total_puzzel");
                                                    String play_time = jsonObject.getString("play_time");
                                                    lblscore.setText(score);
                                                    tv_scoremulti.setText(score);
                                                    tv_total.setText(total_puzzel);
                                                    tv_time.setText(play_time);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    System.out.println("jsonexeption" + e.toString());
                                                }


                                            }
                                        }, true);
                            } else {
                                Utilities.showAlertDailog(MultiplayerScoreActivity.this, "PunnyFuzzles", message, "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        }, true);
                            }


                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(MultiplayerScoreActivity.this, error);
                        AlertUtility.showAlert(MultiplayerScoreActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("pid", pid);
                    params.put("user_id", AppConstant.LOGIN_ID);
                    Log.e("pid", pid);
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
        RequestQueue requestQueue = Volley.newRequestQueue(MultiplayerScoreActivity.this);
        requestQueue.add(stringRequest);

    }
}

