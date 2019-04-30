package com.hnweb.punny;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.punny.multiplayer.adaptor.PuzzlesAdapter;
import com.hnweb.punny.bo.Puzzle;
import com.hnweb.punny.utilities.AlertUtility;
import com.hnweb.punny.utilities.App;
import com.hnweb.punny.utilities.AppConstant;
import com.hnweb.punny.utilities.AppUtils;
import com.hnweb.punny.utilities.MusicManager;
import com.hnweb.punny.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Priyanka Harmale on 02/01/2018.
 */
public class SinglePlayerActivity extends AppCompatActivity {

    private String TAG = SinglePlayerActivity.class.getSimpleName();

    private Activity activity;

    private Boolean continueMusic = true;

    // private SwitchCompat switch_sound;

    private TextView lbl_current_score;
    //    private int fetch_score = 0;
    /*PuzzleList*/
    private RecyclerView recyclerView;
    private PuzzlesAdapter mAdapter;
    private ProgressDialog pDialog1;
    private ProgressDialog pDialog2;
    private ProgressDialog pDialog3;
    private ProgressDialog pDialog4;
    private LinearLayout layout_no_internet;
    private TextView timerValue;

    private long startTime = 0L;
    ArrayList<Puzzle> puzzlelist;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    TextView tv_userName;
    ImageView iv_sound;
    String id;
    Boolean isplayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);

        Intent intent = getIntent();
        id = intent.getStringExtra("pid");

        try {
            activity = this;
            Toolbar toolbar = ( Toolbar ) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            /*Init*/
            recyclerView = ( RecyclerView ) findViewById(R.id.recycler_view);
            iv_sound = findViewById(R.id.switch_sound);
            layout_no_internet = ( LinearLayout ) findViewById(R.id.layout_no_internet);
            lbl_current_score = ( TextView ) findViewById(R.id.lbl_current_score);
            tv_userName = findViewById(R.id.tv_userName);
            timerValue = findViewById(R.id.timerValue);
            pDialog1 = new ProgressDialog(this);
            pDialog1.setMessage("Fetching Data...");
            pDialog1.setCancelable(true);

            pDialog2 = new ProgressDialog(this);
            pDialog2.setMessage("Fetching Data...");
            pDialog2.setCancelable(true);

            pDialog3 = new ProgressDialog(this);
            pDialog3.setMessage("Logging out...");
            pDialog3.setCancelable(false);

            pDialog4 = new ProgressDialog(this);
            pDialog4.setMessage("Submitting Score...");
            pDialog4.setCancelable(true);

            if (AppConstant.IS_MUSIC_ON == true) {
                if (continueMusic == true) {
                    AppConstant.IS_MUSIC_ON = true;
                    Utilities.setMusicChecked(true);
                    continueMusic = false;
                    MusicManager.start(activity, MusicManager.MUSIC_MENU);
                    isplayed = false;
                    iv_sound.setImageResource(R.drawable.volume_up_indicator);


                } else {
                    iv_sound.setImageResource(R.drawable.volume_off_indicatornew);

                    Utilities.setMusicChecked(false);
                    AppConstant.IS_MUSIC_ON = false;
                    if (!continueMusic) {
                        MusicManager.pause();

                    }
                    continueMusic = true;

                    isplayed = false;
                    iv_sound.setImageResource(R.drawable.volume_off_indicatornew);
                }
            }
            iv_sound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (continueMusic == true) {
                        AppConstant.IS_MUSIC_ON = true;
                        Utilities.setMusicChecked(true);
                        continueMusic = false;
                        MusicManager.start(activity, MusicManager.MUSIC_MENU);
                        isplayed = false;
                        iv_sound.setImageResource(R.drawable.volume_up_indicator);


                    } else {
                        Utilities.setMusicChecked(false);
                        AppConstant.IS_MUSIC_ON = false;
                        if (!continueMusic) {
                            MusicManager.pause();

                        }
                        continueMusic = true;

                        isplayed = false;
                        iv_sound.setImageResource(R.drawable.volume_off_indicatornew);
                    }
                }
            });

           /* switch_sound.setChecked(AppConstant.IS_MUSIC_ON);
            switch_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        AppConstant.IS_MUSIC_ON = true;
                        Utilities.setMusicChecked(true);
                        continueMusic = false;
                        MusicManager.start(activity, MusicManager.MUSIC_MENU);
                    } else {
                        Utilities.setMusicChecked(false);
                        AppConstant.IS_MUSIC_ON = false;
                        if (!continueMusic) {
                            MusicManager.pause();
                        }
                    }
                }
            });*/

            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            tv_userName.setText(AppConstant.LOGIN_NAME);
            recyclerView.setItemAnimator(new DefaultItemAnimator());


            /*Check Internet Connectivity*/
            if (!Utilities.isInternetAvailable()) {
                layout_no_internet.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                if (AppConstant.IS_LOGIN)
                    getPuzzleData();
                else
                    getPuzzleList();
            }

        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(SinglePlayerActivity):" + ex.toString());
            Toast.makeText(this, "Error(SinglePlayerActivity):" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (AppConstant.IS_LOGIN)
            getMenuInflater().inflate(R.menu.main_logout, menu);
        else
            getMenuInflater().inflate(R.menu.main_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            AppConstant.REFRESH = true;
            AppConstant.REFRESH1 = true;
            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            activity.invalidateOptionsMenu();
            if (Utilities.getNOsend())
                postPuzzleDataLogout();
            else {
                Utilities.setLoginSuccess(false, "", "", "");
                activity.invalidateOptionsMenu();
                Utilities.showAlertDailog(activity, "PunnyFuzzles", "Logout Successfully", "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }, true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void btnBack_onClick(View view) {
        try {
            activity.onBackPressed();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnBack_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnLogin_onClick(View view) {
        try {
            Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnLogin_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnSubmit_onClick(View view) {
        try {
            if (AppConstant.IS_LOGIN) {
                if (Utilities.getNOsend())
                    postPuzzleData();
                else
                    Toast.makeText(this, "Already Submitted, Play Game and Submit", Toast.LENGTH_LONG).show();
            } else {
                int score = Integer.parseInt(lbl_current_score.getText().toString());
                Intent intent = new Intent(activity, SinglePalyerScoreActivity.class);
                intent.putExtra(SinglePalyerScoreActivity.EXTRA_SCORE, score);
                startActivity(intent);
            }
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnSubmit_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnGetMorePuzzle_onClick(View view) {
        try {
            Intent intent = new Intent(activity, inapp.class);
            startActivity(intent);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnGetMorePuzzle_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnRetry_onClick(View view) {
        try {
            if (AppConstant.IS_LOGIN)
                getPuzzleData();
            else {
                Utilities.setScore(0);
                Utilities.setAttemptedQuestions("");
                getPuzzleList();
            }
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnRetry_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void showProgressDialog1() {
        pDialog1.show();//this, "", "Fetching Data...");
    }

    private void hideProgressDialog1() {
        if (pDialog1.isShowing())
            pDialog1.hide();
    }

    private void showProgressDialog2() {
        pDialog2.show();//this, "", "Fetching Data...");
    }

    private void hideProgressDialog2() {
        if (pDialog2.isShowing())
            pDialog2.hide();
    }

    private void showProgressDialog3() {
        pDialog3.show();//(this, "", "Submitting Score...");
    }

    private void hideProgressDialog3() {
        if (pDialog3.isShowing())
            pDialog3.hide();
    }

    private void showProgressDialog4() {
        pDialog4.show();
    }

    private void hideProgressDialog4() {
        if (pDialog4.isShowing())
            pDialog4.hide();
    }

    /*private void getPuzzleList() {
        showProgressDialog2();
        String url;
        if (AppConstant.IS_LOGIN) {
            url = AppConstant.GET_PUZZLES_LIST + "?user_id=" + AppConstant.LOGIN_ID;
        } else {
            url = AppConstant.GET_PUZZLES_LIST + "?user_id=";//4
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            AppConstant.puzzleList.clear();
                            JSONArray jsonArray = response.getJSONObject("response").getJSONArray("puzzle_list");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jObject = jsonArray.getJSONObject(i);

                                String id = (i + 1) + "";//jObject.getString("id");
                                String pnumber = jObject.getString("pnumber");
                                String section = jObject.getString("section");
                                String primary_word = jObject.getString("primary_word");
                                String image_caption = jObject.getString("image_caption");
                                String image_url = jObject.getString("image_url");
                                String full_image_path = jObject.getString("full_image_path");
                                String solution = jObject.getString("solution");
                                String added_date = jObject.getString("added_date");
                                String status = jObject.getString("status");
                                String options = jObject.getString("options");
                                String spoonerism = jObject.getString("spoonerism");

                                Puzzle p = new Puzzle();
                                p.setId(id);
                                p.setPnumber(pnumber);
                                p.setSection(section);
                                p.setPrimary_word(primary_word);
                                p.setImage_caption(image_caption);
                                p.setImage_url(image_url);
                                p.setFull_image_path(full_image_path);
                                p.setSolution(solution);
                                p.setAdded_date(added_date);
                                p.setStatus(status);
                                p.setOptions(options);
                                p.setSpoonerism(spoonerism);

                                if (i >= 5 && AppConstant.IS_LOGIN) {
                                    if (AppConstant.ATTEMPTED_QUE.contains(pnumber))
                                        p.setAttempted(true);
                                }

                                if (i < 5) {
                                    p.setFree(true);
                                }

                                AppConstant.puzzleList.add(p);
                            }
                            if (AppConstant.IS_LOGIN) {
                                if (AppConstant.TOTAL_PUZZLES <= AppConstant.puzzleList.size()) {
                                    mAdapter = new PuzzlesAdapter(AppConstant.puzzleList.subList(0, AppConstant.TOTAL_PUZZLES), activity, lbl_current_score);
                                    AppConstant.TOTAL_PUZZLES_AVAILABLE = jsonArray.length() - AppConstant.TOTAL_PUZZLES;
                                } else {
                                    mAdapter = new PuzzlesAdapter(AppConstant.puzzleList, activity, lbl_current_score);
                                    AppConstant.TOTAL_PUZZLES_AVAILABLE = 0;
                                }
                            } else {
                                mAdapter = new PuzzlesAdapter(AppConstant.puzzleList.subList(0, 5), activity, lbl_current_score);
                                AppConstant.TOTAL_PUZZLES_AVAILABLE = jsonArray.length() - 5;
                            }
                            recyclerView.setAdapter(mAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);
                        hideProgressDialog2();
                        layout_no_internet.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog2();
                layout_no_internet.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        // Adding request to request queue
        App.getInstance().addToRequestQueue(jsonObjReq, TAG);

    }*/

    private void getPuzzleList() {
        showProgressDialog2();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_SINGLE_PUZZLES_LIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        puzzlelist = new ArrayList<Puzzle>();

                        System.out.println("GET_Single_PUZZLES_LIST" + response);
                        hideProgressDialog2();
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            if (message_code == 1) {
                                String status1 = jobj.getString("status");
                                Log.i("status1", status1);

                                // play_time = jobj.getString("play_time");
                                // Log.i("play_time", play_time);

                                String played_puzzel_cnt = jobj.getString("played_puzzel_cnt");
                                String total_puzzel = jobj.getString("total_puzzel");
                                String score = jobj.getString("score");
                                puzzlelist.clear();
                                JSONArray jsonArray = jobj.getJSONArray("puzzle_list");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jObject = jsonArray.getJSONObject(i);
                                    String id = /*(i + 1) + "";*/ jObject.getString("id");
                                    String pnumber = jObject.getString("pnumber");
                                    String section = jObject.getString("section");
                                    String primary_word = jObject.getString("primary_word");
                                    String image_caption = jObject.getString("image_caption");
                                    String image_url = jObject.getString("image_url");
                                    String full_image_path = jObject.getString("full_image_path");
                                    String solution = jObject.getString("solution");
                                    String added_date = jObject.getString("added_date");
                                    String status = jObject.getString("status");
                                    String options = jObject.getString("options");
                                    String spoonerism = jObject.getString("spoonerism");
                                    String played_status = jObject.getString("played_status");


                                    Puzzle p = new Puzzle();
                                    p.setId(id);
                                    p.setPnumber(pnumber);
                                    p.setSection(section);
                                    p.setPrimary_word(primary_word);
                                    p.setImage_caption(image_caption);
                                    p.setImage_url(image_url);
                                    p.setFull_image_path(full_image_path);
                                    p.setSolution(solution);
                                    p.setAdded_date(added_date);
                                    p.setStatus(status);
                                    p.setOptions(options);
                                    p.setSpoonerism(spoonerism);
                                    p.setPlayed_status(played_status);

                                    puzzlelist.add(p);

                                }
                               /* pb.setMax(Integer.parseInt(total_puzzel));
                                pb.setProgress(Integer.parseInt(played_puzzel_cnt));
                                lbl_current_score.setText(score);
                                pb.setText(played_puzzel_cnt + "/" + total_puzzel);
                                if (status1.equalsIgnoreCase("complete")) {
                                    btn_submit.setVisibility(View.VISIBLE);
                                } else {
                                    btn_submit.setVisibility(View.GONE);
                                }*/
                              /*  if (play_time.equalsIgnoreCase("") || play_time == null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        c.setCountDown(true);
                                    }
                                    long dayInMilli = 60 * 60 * 24 * 1000;
                                    chronometer.setBase(SystemClock.elapsedRealtime());
                                    chronometer.start();
                                } else {

                                    long total = 0;
                                    String array[] = play_time.split(":");
                                    if (array.length == 2) {
                                        total = Integer.parseInt(array[0]) * 60 * 1000 + Integer.parseInt(array[1]) * 1000;
                                    } else if (array.length == 3) {
                                        total =
                                                Integer.parseInt(array[0]) * 60 * 60 * 1000 + Integer.parseInt(array[1]) * 60 * 1000
                                                        + Integer.parseInt(array[2]) * 1000;
                                    }

                                    chronometer.setBase(SystemClock.elapsedRealtime() - total);

                                    if (status1.equalsIgnoreCase("complete")) {
                                        chronometer.stop();
                                        Utilities.showAlertDailog(MultiPlayerActivity.this, "PunnyFuzzles", "Puzzle Completed Successfully..Please submit it", "Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {


                                                    }
                                                }, false);
                                    } else {

                                        chronometer.start();
                                    }


                                }*/
                            }

                            mAdapter = new PuzzlesAdapter(puzzlelist, activity, lbl_current_score);
                            Log.e("puzleList", String.valueOf(puzzlelist.size()));
                            recyclerView.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(SinglePlayerActivity.this, error);
                        AlertUtility.showAlert(SinglePlayerActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("pid", id);
                    Log.e("pid", id);
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
        RequestQueue requestQueue = Volley.newRequestQueue(SinglePlayerActivity.this);
        requestQueue.add(stringRequest);

    }


    private void getPuzzleData() {
        showProgressDialog1();
        String url;
        url = AppConstant.GET_ATTEMPTED_DATA + "?user_id=" + AppConstant.LOGIN_ID;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject objresponse = response.getJSONObject("response");
                            if (objresponse.getInt("flag") == 1) {

                                //This following variable contains the attempted question ids comma seperated.
                                JSONObject jobj = objresponse.getJSONArray("scoretotal").getJSONObject(0);
                                int fetch_score = Integer.parseInt(jobj.getString("total_score"));
                                String fetch_Attempted = jobj.getString("played_puzzle");
                                if (!fetch_Attempted.equals("")) {
                                    char c = fetch_Attempted.charAt(fetch_Attempted.length() - 1);
                                    if (c != ',') {
                                        fetch_Attempted += ",";
                                    }
                                }
                                if (!jobj.getString("total_puzzle").equals("")) {
                                    if (jobj.getString("total_puzzle").equals("0")) {
                                        AppConstant.TOTAL_PUZZLES = 5;
                                    } else {
                                        AppConstant.TOTAL_PUZZLES = Integer.parseInt(jobj.getString("total_puzzle").trim());
                                    }
                                }

                                if (Utilities.getNOsend()) {
                                    String[] str = fetch_Attempted.split(",");
                                    String strFinal = AppConstant.ATTEMPTED_QUE + "";
                                    for (String strPId : str) {
                                        if (!AppConstant.ATTEMPTED_QUE.contains(strPId)) {
                                            strFinal += strPId + ",";
                                        }
                                    }
                                    Utilities.setAttemptedQuestions(strFinal);
                                    Utilities.setScore(AppConstant.SCORE);
                                } else {
                                    Utilities.setAttemptedQuestions(fetch_Attempted);
                                    Utilities.setScore(fetch_score);
                                }
                                lbl_current_score.setText(AppConstant.SCORE + "");
                                getPuzzleList();
                            } else {
                                layout_no_internet.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        hideProgressDialog1();

                        layout_no_internet.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog1();
                layout_no_internet.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        // Adding request to request queue
        App.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    private void postPuzzleData() {
        showProgressDialog4();
        String url;
        if (AppConstant.IS_LOGIN) {
            url = AppConstant.POST_PUZZLE_DATA + "?user_id=" + AppConstant.LOGIN_ID +
                    "&section=0" +
                    "&total_score=" + AppConstant.SCORE +
                    "&played_puzzle=" + AppConstant.ATTEMPTED_QUE +
                    "&total_puzzle=" + AppConstant.TOTAL_PUZZLES;

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, new JSONObject(),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONObject objresponse = response.getJSONObject("response");
                                if (objresponse.getInt("flag") == 1) {
                                    int score = AppConstant.SCORE + 0;
                                    Utilities.setNOsend(false);
                                    Intent intent = new Intent(activity, SinglePalyerScoreActivity.class);
                                    intent.putExtra(SinglePalyerScoreActivity.EXTRA_SCORE, score);
                                    startActivity(intent);
                                } else {
                                    Utilities.setNOsend(true);
                                    Toast.makeText(activity, "Score is not submitted, Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Utilities.setNOsend(true);
                                e.printStackTrace();
                            }
                            hideProgressDialog4();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Utilities.setNOsend(true);
                    hideProgressDialog4();
                    Toast.makeText(activity, "Score is not submitted, Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

            // Adding request to request queue
            App.getInstance().addToRequestQueue(jsonObjReq, TAG);
        } else {
            int score = Integer.parseInt(lbl_current_score.getText().toString());
            Intent intent = new Intent(activity, SinglePalyerScoreActivity.class);
            intent.putExtra(SinglePalyerScoreActivity.EXTRA_SCORE, score);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (AppConstant.IS_MUSIC_ON) {
                if (!continueMusic) {
                    MusicManager.pause();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            activity.invalidateOptionsMenu();
            if (AppConstant.IS_MUSIC_ON) {
                continueMusic = false;
                MusicManager.start(this, MusicManager.MUSIC_MENU);
            }
            if (AppConstant.REFRESH) {
                AppConstant.REFRESH = false;
                AppConstant.REFRESH1 = false;
                if (AppConstant.IS_LOGIN)
                    getPuzzleData();
                else {
                    Utilities.setScore(0);
                    Utilities.setAttemptedQuestions("");
                    getPuzzleList();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        try {
            App.getInstance().getRequestQueue().cancelAll(TAG);
            hideProgressDialog1();
            hideProgressDialog2();
            hideProgressDialog3();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            App.getInstance().cancelPendingRequests(TAG);
            if (!AppConstant.IS_LOGIN) {
                Utilities.setScore(0);
                Utilities.setAttemptedQuestions("");
            }
            hideProgressDialog1();
            hideProgressDialog2();
            hideProgressDialog3();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void postPuzzleDataLogout() {
        showProgressDialog3();
        String url;
        url = AppConstant.POST_PUZZLE_DATA + "?user_id=" + AppConstant.LOGIN_ID +
                "&section=0" +
                "&total_score=" + AppConstant.SCORE +
                "&played_puzzle=" + AppConstant.ATTEMPTED_QUE +
                "&total_puzzle=" + AppConstant.TOTAL_PUZZLES;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject objresponse = response.getJSONObject("response");
                            if (objresponse.getInt("flag") == 1) {
                                Utilities.setLoginSuccess(false, "", "", "");
                                activity.invalidateOptionsMenu();
                                Utilities.setNOsend(false);
                                Utilities.showAlertDailog(activity, "PunnyFuzzles", "Logout Successfully", "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                Intent intent = new Intent(SinglePlayerActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }
                                        }, true);
                            }
                        } catch (Exception e) {
                            Utilities.setNOsend(true);
                            e.printStackTrace();
                        }
                        hideProgressDialog3();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog3();
            }
        });

        // Adding request to request queue
        App.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = ( int ) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = ( int ) (updatedTime % 1000);
            timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };

    ///////////////////////////inapp needed///////////////////////
}
