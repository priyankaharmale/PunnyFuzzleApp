package com.hnweb.punnyfuzzleiap.punnyfuzzle.multiplayer.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.hnweb.punnyfuzzleiap.punnyfuzzle.InviteFriendActivity;

import com.hnweb.punnyfuzzleiap.punnyfuzzle.R;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.ScoreboardListActivity;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.multiplayer.adaptor.CreditsAdaptor;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.adapters.NotificationAdaptor;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.Notification;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.NotificationUpdateModel;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.PuzzleRate;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AlertUtility;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreditListActivity extends AppCompatActivity implements NotificationUpdateModel.OnCustomStateListener {
    RecyclerView recycler_view;
    ProgressDialog pDialog;
    CreditsAdaptor puzzleRateAdaptor;
    ArrayList<PuzzleRate> puzzleRates;
    TextView tv_credit;
    ImageButton btn_back;
    TextView cart_badge;
    String mCartItemCount = "";

    ImageView iv_invite;
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
        notificationframe = findViewById(R.id.frame);
        btn_scoreboard = findViewById(R.id.btn_scoreboard);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getPuzzleRate();

        iv_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreditListActivity.this, InviteFriendActivity.class);
                startActivity(intent);
            }
        });
        notificationframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogNotificationDetails();


            }
        });

        btn_scoreboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreditListActivity.this, ScoreboardListActivity.class);
                startActivity(intent);

            }
        });
    }


    private void getPuzzleRate() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_MULTI_CREDIT_PLAN,
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
                                puzzleRateAdaptor = new CreditsAdaptor(CreditListActivity.this, puzzleRates);
                                recycler_view.setAdapter(puzzleRateAdaptor);
                            } else {

                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(CreditListActivity.this);
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
                        String reason = AppUtils.getVolleyError(CreditListActivity.this, error);
                        AlertUtility.showAlert(CreditListActivity.this, reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(CreditListActivity.this);
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


        final AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(CreditListActivity.this);

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
                                notificationAdaptor = new NotificationAdaptor(CreditListActivity.this, notifications);
                                recycler_view.setAdapter(notificationAdaptor);
                            } else {

                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(CreditListActivity.this);
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
                        String reason = AppUtils.getVolleyError(CreditListActivity.this, error);
                        AlertUtility.showAlert(CreditListActivity.this, reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(CreditListActivity.this);
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
