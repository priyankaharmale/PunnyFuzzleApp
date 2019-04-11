package com.hnweb.punny;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.hnweb.punny.singleplayer.SinglePlayerPuzzlePurchase;
import com.hnweb.punny.utilities.AlertUtility;
import com.hnweb.punny.utilities.App;
import com.hnweb.punny.utilities.AppConstant;
import com.hnweb.punny.utilities.AppUtils;
import com.hnweb.punny.utilities.MusicManager;
import com.hnweb.punny.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private Activity activity;

    private Boolean continueMusic;

    // Animation
    private Animation animFadein;
    private ImageView img_pie;
    Dialog dialog;
    String multiplayer, singleplayer, sixtrysecchallenge;
    private ProgressDialog pDialog3, pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            activity = this;

            Toolbar toolbar = ( Toolbar ) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            /*Check Internet Connectivity*/
            if (!Utilities.isInternetAvailable()) {
                showAlertDialog();
            } else {
                if (!Utilities.getPurchasedInfo().equals("")) {
                    postPaymentData();
                }
            }

            img_pie = ( ImageView ) findViewById(R.id.img_pie);
            // load the animation
            animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);
            img_pie.startAnimation(animFadein);

            pDialog3 = new ProgressDialog(this);
            pDialog3.setMessage("Logging out...");
            pDialog3.setCancelable(false);
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);

            getCreditcount();

        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(MainActivity):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            if (Utilities.getNOsend())
                postPuzzleDataLogout();
            else {
                Utilities.setLoginSuccess(false, "", "", "");
                activity.invalidateOptionsMenu();
                Utilities.showAlertDailog(activity, "PunnyFuzzles", "Logout Successfully", "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                activity.invalidateOptionsMenu();
                            }
                        }, true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAlertDialog() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Can't connect");
            alertDialogBuilder.setMessage("Check your network and try again.");
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(showAlertDialog):" + ex.toString());
        }
    }

    public void btnInfo_onClick(View view) {
        try {
            Intent intent = new Intent(activity, InfoActivity.class);
            startActivity(intent);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnTheRules_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnTheRules_onClick(View view) {
        try {
            Intent intent = new Intent(activity, TheRulesActivity.class);
            startActivity(intent);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnTheRules_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnSinglePlayer_onClick(View view) {
        try {
           /* Intent intent = new Intent(activity, SinglePlayerActivity.class);
            startActivity(intent);*/
            Log.e("AppConstant.LOGIN_ID", AppConstant.LOGIN_ID);
            if (AppConstant.LOGIN_ID == null || AppConstant.LOGIN_ID.equalsIgnoreCase("")) {
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
            } else {
                dialogChoosePlayer();

            }
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnSinglePlayer_onClick):" + ex.toString());
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
        activity.invalidateOptionsMenu();
        if (AppConstant.IS_MUSIC_ON == true) {
            continueMusic = false;
            MusicManager.start(this, MusicManager.MUSIC_MENU);
        }
    }

    @Override
    protected void onStop() {
        try {
            App.getInstance().getRequestQueue().cancelAll(TAG);
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
            hideProgressDialog3();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    private void showProgressDialog3() {
        if (!pDialog3.isShowing())
            pDialog3.show();
    }

    private void hideProgressDialog3() {
        if (pDialog3.isShowing())
            pDialog3.hide();
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
                                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();

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

    private void postPaymentData() {
        String url = AppConstant.POST_PAYMENT_DATA;
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response1) {
                        try {
                            JSONObject response = new JSONObject(response1);
                            JSONObject objresponse = response.getJSONObject("response");
                            if (objresponse.getInt("flag") == 1) {
                                Utilities.setPurchasedInfo("");
                            }
                        } catch (Exception e) {
                            Utilities.setNOsend(true);
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params1 = new HashMap<String, String>();
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    String[] strData = Utilities.getPurchasedInfo().split("|");
                    params.put("user_id", strData[0]);
                    params.put("firstname", strData[1]);
                    params.put("payment_status", strData[2]);
                    params.put("total_order_price", strData[3]);
                    params.put("date_order_placed", strData[4]);
                    params.put("total_puzzle_purchase", strData[5]);
                    params1 = params;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params1;
            }
        };

        // Adding request to request queue
        App.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    public void dialogChoosePlayer() {

        //getCreditcount();

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_playerselection);
        TextView tv_sinngleplayer = ( TextView ) dialog.findViewById(R.id.tv_sinngleplayer);
        TextView tv_multiplayer = ( TextView ) dialog.findViewById(R.id.tv_multiplayer);
        TextView tv_sinngleplayercredit = dialog.findViewById(R.id.tv_sinngleplayercredit);
        TextView tv_multiplayercredit = dialog.findViewById(R.id.tv_multiplayercredit);
        TextView tv_60Seccredit = dialog.findViewById(R.id.tv_60Seccredit);

        tv_sinngleplayercredit.setText("(" + singleplayer + ")");
        tv_multiplayercredit.setText("(" + multiplayer + ")");
        tv_60Seccredit.setText("(" + sixtrysecchallenge + ")");

        dialog.show();

        dialog.setCancelable(true);
        tv_sinngleplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SinglePlayerPuzzlePurchase.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        tv_multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MultiplayerPuzzlePurchase.class);
                startActivity(intent);
                dialog.dismiss();

            }
        });

    }

    public void getCreditcount() {
        showProgressDialog2();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_CREDIT_COUNT,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            final JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            hideProgressDialog2();

                            if (message_code == 1) {
                                multiplayer = j.getString("multiplayer");
                                singleplayer = j.getString("singleplayer");
                                sixtrysecchallenge = j.getString("60secchallenge");

                            } else {
                                message = j.getString("message");
                               /* AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();*/
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(MainActivity.this, error);
                        AlertUtility.showAlert(MainActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("user_id", AppConstant.LOGIN_ID);


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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void showProgressDialog2() {
        pDialog.show();
    }

    private void hideProgressDialog2() {
        if (pDialog.isShowing())
            pDialog.hide();
    }


}
