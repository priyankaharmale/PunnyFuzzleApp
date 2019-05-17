package com.hnweb.punnyfuzzleiap.punnyfuzzle.multiplayer.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.hnweb.punnyfuzzleiap.punnyfuzzle.InviteFriendActivity;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.LoginActivity;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.MainActivity;

import com.hnweb.punnyfuzzleiap.punnyfuzzle.R;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.ScoreActivity;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.ScoreboardListActivity;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.ThrowtowelActivity;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.multiplayer.adaptor.MultiplayerPuzzlesAdapter;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.adapters.NotificationAdaptor;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.Notification;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.NotificationUpdateModel;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.Puzzle;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.interfaces.OnCallBack;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.multiplayer.adaptor.MultiplayerPuzzlesNewAdapter;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AlertUtility;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.App;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppUtils;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.MusicManager;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.TextProgressBar;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Priyanka Harmale on 02/01/2018.
 */

public class MultiPlayerActivity extends AppCompatActivity implements OnCallBack, NotificationUpdateModel.OnCustomStateListener {

    private String TAG = MultiPlayerActivity.class.getSimpleName();
    private Activity activity;
    ImageView iv_close;
    private Boolean continueMusic = true;
    ImageView iv_invite;
    static public Chronometer chronometer;
    TextView textView_empty_list;
    // private SwitchCompat switch_sound;
    NotificationAdaptor notificationAdaptor;
    private TextView lbl_current_score;
    private RecyclerView recyclerView;
    private MultiplayerPuzzlesNewAdapter mAdapter;
    private ProgressDialog pDialog1;
    private ProgressDialog pDialog2;
    private ProgressDialog pDialog3;
    private ProgressDialog pDialog4;
    String play_time;
    private LinearLayout layout_no_internet;
    static public TextView timerValue;
    OnCallBack onCallBack;
    private long startTime = 0L;
    ProgressDialog pDialog;
    private Handler customHandler = new Handler();
    ArrayList<Puzzle> puzzlelist;
    ArrayList<Notification> notifications;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    TextView tv_userName;
    ImageView iv_sound;
    String pid;
    AlertDialog b;
    Boolean isplayed = true;
    ImageButton btn_submit;
    FrameLayout notificationframe;
    int myProgress = 0;
    private MyNewCountDownTimer myNewCountDownTimer;

    TextProgressBar pb;
    String mCartItemCount = "";
    TextView cart_badge;
    Button btn_scoreboard;
    RecyclerView recycler_view;
    public int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        pb = new TextProgressBar(this, 10);
        pb = (TextProgressBar) findViewById(R.id.pb);
        try {
            activity = this;
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Intent intent = getIntent();
            pid = intent.getStringExtra("id");
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(true);
            /*Init*/

            onCallBack = this;
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            btn_submit = findViewById(R.id.btn_submit);
            cart_badge = findViewById(R.id.cart_badge);
            iv_invite = findViewById(R.id.iv_invite);
            layout_no_internet = (LinearLayout) findViewById(R.id.layout_no_internet);
            lbl_current_score = (TextView) findViewById(R.id.lbl_current_score);
            tv_userName = findViewById(R.id.tv_userName);
            iv_sound = findViewById(R.id.switch_sound);
            btn_scoreboard = findViewById(R.id.btn_scoreboard);
            notificationframe = findViewById(R.id.frame);
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


            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitpuzzle();
                }
            });
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
            iv_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, InviteFriendActivity.class);
                    startActivity(intent);
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

            //recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            tv_userName.setText(AppConstant.LOGIN_NAME);
            //recyclerView.setItemAnimator(new DefaultItemAnimator());


            /*Check Internet Connectivity*/
            if (!Utilities.isInternetAvailable()) {
                layout_no_internet.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                getMultiPuzzleList();
            }

        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(SinglePlayerActivity):" + ex.toString());
            Toast.makeText(this, "Error(SinglePlayerActivity):" + ex.toString(), Toast.LENGTH_LONG).show();
        }
       /* startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);*/
     /*   final long startTime = 10 * 60 * 1000;
        final long interval = 1 * 1000;
        final long startNewTime = 1 * 60 * 1000;
      //  myNewCountDownTimer.cancel();
        myNewCountDownTimer = new MyNewCountDownTimer(startNewTime, interval, timerValue);
        myNewCountDownTimer.start();*/
        notificationframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogNotificationDetails();


            }
        });
        btn_scoreboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ScoreboardListActivity.class);
                startActivity(intent);

            }
        });
        chronometer = findViewById(R.id.chronometer);

       /* chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometerChanged) {
                chronometer = chronometerChanged;
            }
        });*/

        chronometer.setVisibility(View.GONE);
        CountDownTimer cT = new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {


                String v = String.format("%02d", millisUntilFinished / 60000);
                int va = (int) ((millisUntilFinished % 60000) / 1000);

                long updatemilisec = 300000 - millisUntilFinished;

                String v1 = String.format("%02d", updatemilisec / 60000);
                int va2 = (int) ((updatemilisec % 60000) / 1000);

                Log.e(AppConstant.TAG, v1 + ":" + String.format("%02d", va2));

                timerValue.setText(v1 + ":" + String.format("%02d", va2));
            }

            public void onFinish() {
                timerValue.setText("5:00");

                submitpuzzle();
            }
        };
        cT.start();


        getNotificationCount();

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
                    //postPuzzleData();

                    Toast.makeText(this, "Already Submitted, Play Game and Submit", Toast.LENGTH_LONG).show();
            } else {
                int score = Integer.parseInt(lbl_current_score.getText().toString());
                Intent intent = new Intent(activity, ScoreActivity.class);
                intent.putExtra(ScoreActivity.EXTRA_SCORE, score);
                startActivity(intent);
            }
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnSubmit_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /*public void btnGetMorePuzzle_onClick(View view) {
        try {
            Intent intent = new Intent(activity, inapp.class);
            startActivity(intent);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnGetMorePuzzle_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }
*/
    public void btnRetry_onClick(View view) {
        try {


            Utilities.setScore(0);
            Utilities.setAttemptedQuestions("");
            getMultiPuzzleList();

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
            getNotificationCount();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            activity.invalidateOptionsMenu();
            if (AppConstant.IS_MUSIC_ON) {
                continueMusic = false;
                MusicManager.start(this, MusicManager.MUSIC_MENU);
            }
            if (AppConstant.REFRESH) {
                AppConstant.REFRESH = false;
                AppConstant.REFRESH1 = false;

                Utilities.setScore(0);
                Utilities.setAttemptedQuestions("");
                getMultiPuzzleList();

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
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);

            /*if(play_time.equalsIgnoreCase("") || play_time==null || play_time.equalsIgnoreCase(null))
            {
                timerValue.setText("" + mins + ":"
                        + String.format("%02d", secs));
            }else {*/

            //  }
            timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };

    ///////////////////////////inapp needed///////////////////////


    private void getMultiPuzzleList() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_MULTI_PUZZLES_LIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        puzzlelist = new ArrayList<Puzzle>();

                        System.out.println("GET_MULTI_PUZZLES_LIST" + response);
                        hideProgressDialog();
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            if (message_code == 1) {
                                String status1 = jobj.getString("status");
                                Log.i("status1", status1);

                                play_time = jobj.getString("play_time");
                                Log.i("play_time", play_time);

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
                                pb.setMax(Integer.parseInt(total_puzzel));
                                pb.setProgress(Integer.parseInt(played_puzzel_cnt));
                                lbl_current_score.setText(score);
                                pb.setText(played_puzzel_cnt + "/" + total_puzzel);
                                if (status1.equalsIgnoreCase("complete")) {
                                    btn_submit.setVisibility(View.VISIBLE);
                                } else {
                                    btn_submit.setVisibility(View.GONE);
                                }
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

                            mAdapter = new MultiplayerPuzzlesNewAdapter(puzzlelist, activity, pid, onCallBack);
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
                        String reason = AppUtils.getVolleyError(MultiPlayerActivity.this, error);
                        AlertUtility.showAlert(MultiPlayerActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("pid", pid);
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
        RequestQueue requestQueue = Volley.newRequestQueue(MultiPlayerActivity.this);
        requestQueue.add(stringRequest);

    }

    private void submitpuzzle() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.SUBMIT_PUZZLE,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        System.out.println("GET_MULTI_PUZZLES_LIST" + response);
                        hideProgressDialog();
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String message = jobj.getString("message");

                            if (message_code == 1) {
                                Utilities.showAlertDailog(activity, "PunnyFuzzles", message, "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                Intent intent = new Intent(getApplicationContext(), MultiplayerScoreActivity.class);
                                                intent.putExtra("pid", pid);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }, true);
                            } else {
                                Utilities.showAlertDailog(activity, "PunnyFuzzles", message, "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                Intent intent = new Intent(getApplicationContext(), CreditListActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
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
                        String reason = AppUtils.getVolleyError(MultiPlayerActivity.this, error);
                        AlertUtility.showAlert(MultiPlayerActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("pid", pid);
                    params.put("user_id", AppConstant.LOGIN_ID);
                    params.put("time", timerValue.getText().toString());
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
        RequestQueue requestQueue = Volley.newRequestQueue(MultiPlayerActivity.this);
        requestQueue.add(stringRequest);

    }

    private void quitepuzzle() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.QUITE_PUZZLE,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        System.out.println("GET_MULTI_PUZZLES_LIST" + response);
                        hideProgressDialog();
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String message = jobj.getString("message");

                            if (message_code == 1) {
                                Utilities.showAlertDailog(activity, "PunnyFuzzles", "Puzzle Quite Successfully", "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                Intent intent = new Intent(getApplicationContext(), ThrowtowelActivity.class);
                                                intent.putExtra("callfrom", "2");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }, true);
                            } else {
                                Utilities.showAlertDailog(activity, "PunnyFuzzles", message, "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                Intent intent = new Intent(getApplicationContext(), CreditListActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
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
                        String reason = AppUtils.getVolleyError(MultiPlayerActivity.this, error);
                        AlertUtility.showAlert(MultiPlayerActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("pid", pid);
                    params.put("user_id", AppConstant.LOGIN_ID);
                    params.put("time", timerValue.getText().toString());
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
        RequestQueue requestQueue = Volley.newRequestQueue(MultiPlayerActivity.this);
        requestQueue.add(stringRequest);

    }

    private void showProgressDialog() {

        pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    @Override
    public void callbackrefresh() {
        getMultiPuzzleList();
    }

    @Override
    public void notificationStateChanged() {
        getNotificationCount();
    }


    private void getNotificationCount() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.GET_NOTIFICATION_COUNT,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("NotificationResponse", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int message_code = jsonObject.getInt("message_code");
                            String msg = jsonObject.getString("message");
                            String notification_count = jsonObject.getString("ncount");
                            Log.d("Notication:-", String.valueOf(message_code));
                            if (message_code == 1) {
                                //  Utils.AlertDialog(MainActivityUser.this, msg);
                                mCartItemCount = "";
                                if (notification_count.equals("0")) {
                                    mCartItemCount = "0";
                                    setupBadge();
                                } else {
                                    mCartItemCount = notification_count;
                                    setupBadge();
                                }

                            } else {
                                mCartItemCount = "0";
                                setupBadge();
                                // Utils.AlertDialog(MainActivityUser.this, msg);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyLog.d("VolleyResponse", "Error: " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        ) {
            @SuppressLint("LongLogTag")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("user_id", AppConstant.LOGIN_ID);
                Log.e("NotificationCount ", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    public void dialogNotificationDetails() {


        final AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(MultiPlayerActivity.this);

        LayoutInflater inflater1 = getLayoutInflater();
        final View dialogView1 = inflater1.inflate(R.layout.dialog_notificationlist, null);

        recycler_view = dialogView1.findViewById(R.id.recycler_view);
        textView_empty_list = dialogView1.findViewById(R.id.textView_empty_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recycler_view.setLayoutManager(layoutManager);
        iv_close = dialogView1.findViewById(R.id.iv_close);
        getnotificationList();
        dialogBuilder1.setView(dialogView1);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });
        b = dialogBuilder1.create();
        b.setCancelable(false);
        b.show();
    }

    private void getnotificationList() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_NOTIFICATION_LIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("GET_NOTIFICATION_LIST" + response);
                        hideProgressDialog();
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);
                            if (message_code == 1) {
                                textView_empty_list.setVisibility(View.GONE);
                                recycler_view.setVisibility(View.VISIBLE);
                                JSONArray userdetails = jobj.getJSONArray("detail");
                                notifications = new ArrayList<Notification>();
                                Log.d("ArrayLengthNails", String.valueOf(userdetails.length()));

                                for (int j = 0; j < userdetails.length(); j++) {
                                    JSONObject jsonObject = userdetails.getJSONObject(j);

                                    Notification puzzleRate = new Notification();
                                    puzzleRate.setNid(jsonObject.getString("nid"));
                                    puzzleRate.setEmail(jsonObject.getString("email"));
                                    puzzleRate.setUsername(jsonObject.getString("username"));
                                    puzzleRate.setUpdated_dt(jsonObject.getString("updated_dt"));
                                    puzzleRate.setFrom_user_id(jsonObject.getString("from_user_id"));
                                    puzzleRate.setTo_user_id(jsonObject.getString("to_user_id"));
                                    puzzleRate.setView_by_to(jsonObject.getString("view_by_to"));

                                    notifications.add(puzzleRate);
                                    Log.d("ArraySize", String.valueOf(notifications.size()));
                                }
                                notificationAdaptor = new NotificationAdaptor(MultiPlayerActivity.this, notifications);
                                recycler_view.setAdapter(notificationAdaptor);
                            } else {

                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(MultiPlayerActivity.this);
                                builder.setMessage(msg)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                textView_empty_list.setVisibility(View.VISIBLE);
                                                recycler_view.setVisibility(View.GONE);
                                                b.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(MultiPlayerActivity.this, error);
                        AlertUtility.showAlert(MultiPlayerActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("user_id", AppConstant.LOGIN_ID);
                    Log.e("userId", AppConstant.LOGIN_ID);
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
        RequestQueue requestQueue = Volley.newRequestQueue(MultiPlayerActivity.this);
        requestQueue.add(stringRequest);

    }


    private void setupBadge() {

        if (cart_badge != null) {
            if (mCartItemCount.equals("0")) {
                if (cart_badge.getVisibility() != View.GONE) {
                    cart_badge.setVisibility(View.GONE);
                }
            } else {
                cart_badge.setText(String.valueOf(Math.min(Integer.parseInt(mCartItemCount), 99)));
                if (cart_badge.getVisibility() != View.VISIBLE) {
                    cart_badge.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    public class MyNewCountDownTimer extends CountDownTimer {
        TextView tv;
        long startingTime;
        int postionnews;

        public MyNewCountDownTimer(long startTime, long interval, TextView textView) {
            super(startTime, interval);
            tv = textView;
            startingTime = startTime;
            this.postionnews = postionnews;
        }

        @Override
        public void onFinish() {

            String finalDowntime = tv.getText().toString();
            Log.e("finaltime", finalDowntime);

            tv.setText("Time's up!");
            tv.setText("0:00");
            Log.d("TimerEnd0:0", "Time's up!");
            myNewCountDownTimer.cancel();

        }

        @Override
        public void onTick(long millisUntilFinished) {

            final String mnewstop;

            tv.setText((millisUntilFinished / 60000) + ":" + (millisUntilFinished % 60000 / 1000));

        }
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();

        AlertDialog.Builder builder = new AlertDialog.Builder(MultiPlayerActivity.this);
        builder.setMessage("Are you sure you want to Quite the Puzzle?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        quitepuzzle();
                    }
                });


        AlertDialog alert = builder.create();
        alert.show();
    }
}
