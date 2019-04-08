package com.hnweb.punny;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hnweb.punny.utilities.App;
import com.hnweb.punny.utilities.AppConstant;
import com.hnweb.punny.utilities.MusicManager;
import com.hnweb.punny.utilities.SharedPrefManager;
import com.hnweb.punny.utilities.Utilities;


import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by Priyanka Harmale on 29/12/2018.
 */
public class LoginActivity extends AppCompatActivity {

    private String TAG = LoginActivity.class.getSimpleName();

    private Boolean continueMusic;

    private ProgressDialog pDialog;
    private ProgressDialog pDialog2;
    private ProgressDialog pDialog3;
    private EditText txt_username;

    String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getdeviceToken();
        try {
            Toolbar toolbar = ( Toolbar ) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            /*Init*/
            txt_username = ( EditText ) findViewById(R.id.txt_username);
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Logging...");
            pDialog.setCancelable(true);

            pDialog2 = new ProgressDialog(this);
            pDialog2.setMessage("Fetching User Data...");
            pDialog2.setCancelable(false);

            pDialog3 = new ProgressDialog(this);
            pDialog3.setMessage("Fetching User Data...");
            pDialog3.setCancelable(false);

        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(LoginActivity):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnBack_onClick(View view) {
        try {
            LoginActivity.this.onBackPressed();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnBack_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnLogin_onClick(View view) {
        try {
            String strUsername = txt_username.getText().toString().trim();
            CheckLoginDet(strUsername);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnLogin_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnRegister_onClick(View view) {
        try {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnRegister_onClick):" + ex.toString());
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

        pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    private void showProgressDialog2() {
        pDialog2.show();
    }

    private void hideProgressDialog2() {
        if (pDialog2.isShowing())
            pDialog2.hide();
    }

    private void showProgressDialog3() {
        pDialog3.show();
    }

    private void hideProgressDialog3() {
        if (pDialog3.isShowing())
            pDialog3.hide();
    }

    private void CheckLoginDet(String username) {
        if (username.equals("")) {
            Utilities.showAlertDailog(LoginActivity.this, "PunnyFuzzles", "Please Enter Username!!!", "Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    }, true);
        } else {
            String url = AppConstant.USER_LOGIN + "?username=" + username + "&device_type=Android" + "&token=" + deviceToken;
            ;
            Log.e("url", url);
            showProgressDialog();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, new JSONObject(),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e("response", response.toString());
                                JSONObject jsonObject = response.getJSONObject("response");
                                if (jsonObject.getInt("flag") == 1) {
                                    JSONObject jsonUserDet = jsonObject.getJSONObject("user_details");
                                    Utilities.setLoginSuccess(true, jsonUserDet.getString("id"), jsonUserDet.getString("username"),
                                            jsonUserDet.getString("email"));
                                    Utilities.setNOsend(false);
                                    Utilities.showAlertDailog(LoginActivity.this, "PunnyFuzzles", "Login Successfully.", "Ok",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    if (AppConstant.REFRESH1) {
                                                        LoginActivity.this.onBackPressed();
                                                    } else {
                                                        if (!AppConstant.REFRESH) {
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {

                                                            getPuzzleData();
//                                                        LoginActivity.this.onBackPressed();
                                                        }
                                                    }
                                                }
                                            }, false);

                                } else {
                                    Utilities.showAlertDailog(LoginActivity.this, "PunnyFuzzles", "Incorrect Username, Please Try Again.", "Ok",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {

                                                }
                                            }, true);
                                }
                            } catch (Exception e) {
                                Log.e(AppConstant.TAG, "Error(CheckLoginDet):" + e.toString());
                            }
                            hideProgressDialog();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    hideProgressDialog();
                    Utilities.showAlertDailog(LoginActivity.this, "PunnyFuzzles", "Check your network and please try again.", "Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            }, true);
                }
            });

            // Adding request to request queue
            App.getInstance().addToRequestQueue(jsonObjReq, TAG);
        }
    }


    private void getPuzzleData() {
        showProgressDialog2();
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
                                    //fetch_Attempted + AppConstant.ATTEMPTED_QUE
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
                                getPuzzleList();
                            } else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                alertDialogBuilder.setTitle("PunnyFuzzles");
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setMessage("Network issue,Please try again.");
                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getPuzzleData();
                                    }
                                });
                                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AppConstant.REFRESH = false;
                                        LoginActivity.this.onBackPressed();
                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        hideProgressDialog2();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog2();
            }
        });

        // Adding request to request queue
        App.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    private void getPuzzleList() {
        showProgressDialog3();
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

                            if (AppConstant.IS_LOGIN) {
                                if (AppConstant.TOTAL_PUZZLES <= jsonArray.length()) {
                                    AppConstant.TOTAL_PUZZLES_AVAILABLE = jsonArray.length() - AppConstant.TOTAL_PUZZLES;
                                } else {
                                    AppConstant.TOTAL_PUZZLES_AVAILABLE = 0;
                                }
                            } else {
                                AppConstant.TOTAL_PUZZLES_AVAILABLE = jsonArray.length() - 5;
                            }

                            LoginActivity.this.onBackPressed();

                        } catch (Exception e) {
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

    private void getdeviceToken() {
        try {
            deviceToken = SharedPrefManager.getInstance(this).getDeviceToken();

            if (!TextUtils.isEmpty(deviceToken)) {
                Log.d("TokanisEmpty", deviceToken);

            } else {
                Log.d("TokanisEmpty", "Not Register");

            }


        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}
