package com.hnweb.punnyfuzzleiap.punnyfuzzle.sixtysecondchallge;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.hnweb.punnyfuzzleiap.punnyfuzzle.R;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.adapters.NotificationAdaptor;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.Notification;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.NotificationUpdateModel;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.PuzzleRate;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.sixtysecondchallge.adaptor.SixtyCreditsAdaptor;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AlertUtility;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppUtils;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.MusicManager;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by Priyanka Harmale on 30/04/2019.
 */

public class SixtySecondCreditListActivity extends AppCompatActivity implements NotificationUpdateModel.OnCustomStateListener {
    RecyclerView recycler_view;
    ProgressDialog pDialog;
    SixtyCreditsAdaptor puzzleRateAdaptor;
    ArrayList<PuzzleRate> puzzleRates;
    TextView tv_credit;
    ImageButton btn_back;
    TextView cart_badge;
    String mCartItemCount = "";
    Boolean isplayed = true;
    private Boolean continueMusic = true;
    ImageView iv_invite, iv_sound;
    ArrayList<Notification> notifications;
    TextView textView_empty_list;
    NotificationAdaptor notificationAdaptor;
    AlertDialog b;
    ImageView iv_close;
    Button btn_scoreboard;

    FrameLayout notificationframe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditpurchase_listing);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
        recycler_view = findViewById(R.id.recycler_view);
        tv_credit = findViewById(R.id.tv_credit);
        btn_back = findViewById(R.id.btn_back);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recycler_view.setLayoutManager(layoutManager);
        cart_badge = findViewById(R.id.cart_badge);
        iv_invite = findViewById(R.id.iv_invite);
        iv_sound = findViewById(R.id.switch_sound);
        notificationframe = findViewById(R.id.frame);
        btn_scoreboard = findViewById(R.id.btn_scoreboard);
        btn_scoreboard.setVisibility(View.GONE);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // getPuzzleRate();

        iv_invite.setVisibility(View.GONE);
        iv_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent = new Intent(SixtySecondCreditListActivity.this, InviteFriendActivity.class);
                startActivity(intent);*/
            }
        });
        notificationframe.setVisibility(View.GONE);

        notificationframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // dialogNotificationDetails();


            }
        });

        btn_scoreboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent(SixtySecondCreditListActivity.this, ScoreboardListActivity.class);
                startActivity(intent);*/

            }
        });


        if (AppConstant.IS_MUSIC_ON == true) {
            if (continueMusic == true) {
                AppConstant.IS_MUSIC_ON = true;
                Utilities.setMusicChecked(true);
                continueMusic = false;
                MusicManager.start(this, MusicManager.MUSIC_MENU);
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
                    MusicManager.start(SixtySecondCreditListActivity.this, MusicManager.MUSIC_MENU);
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
        getPuzzleRate();
    }


    private void getPuzzleRate() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_60SEC_CREDIT_PLAN,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("GET_MULTI_CREDIT_PLAN" + response);
                        hideProgressDialog();
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);
                            if (message_code == 1) {

                                recycler_view.setVisibility(View.VISIBLE);
                                JSONArray userdetails = jobj.getJSONArray("credit_plan");
                                puzzleRates = new ArrayList<PuzzleRate>();
                                Log.d("ArrayLengthNails", String.valueOf(userdetails.length()));

                                for (int j = 0; j < userdetails.length(); j++) {
                                    JSONObject jsonObject = userdetails.getJSONObject(j);
                                    PuzzleRate puzzleRate = new PuzzleRate();

                                    puzzleRate.setPid(jsonObject.getString("pid"));
                                    puzzleRate.setPuzzle(jsonObject.getString("puzzle"));
                                    puzzleRate.setTitle(jsonObject.getString("title"));
                                    puzzleRate.setUpdated_dt(jsonObject.getString("updated_dt"));


                                    puzzleRates.add(puzzleRate);
                                    Log.d("ArraySize", String.valueOf(puzzleRates.size()));

                                }
                                tv_credit.setText(String.valueOf(puzzleRates.size()));
                                puzzleRateAdaptor = new SixtyCreditsAdaptor(SixtySecondCreditListActivity.this, puzzleRates);
                                recycler_view.setAdapter(puzzleRateAdaptor);
                            } else {

                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(SixtySecondCreditListActivity.this);
                                builder.setMessage(msg)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
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
                        String reason = AppUtils.getVolleyError(SixtySecondCreditListActivity.this, error);
                        AlertUtility.showAlert(SixtySecondCreditListActivity.this, reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(SixtySecondCreditListActivity.this);
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


        final AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(SixtySecondCreditListActivity.this);

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
                                notificationAdaptor = new NotificationAdaptor(SixtySecondCreditListActivity.this, notifications);
                                recycler_view.setAdapter(notificationAdaptor);
                            } else {

                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(SixtySecondCreditListActivity.this);
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
                        String reason = AppUtils.getVolleyError(SixtySecondCreditListActivity.this, error);
                        AlertUtility.showAlert(SixtySecondCreditListActivity.this, reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(SixtySecondCreditListActivity.this);
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

}
