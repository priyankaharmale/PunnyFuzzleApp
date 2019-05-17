package com.hnweb.punnyfuzzleiap.punnyfuzzle;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.App;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.MusicManager;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.Utilities;

import org.json.JSONObject;

/**
 * Created by Priyanka Harmale on 29/12/2018.
         */
public class RegisterActivity extends AppCompatActivity {

    private String TAG = RegisterActivity.class.getSimpleName();

    private Boolean continueMusic;

    private ProgressDialog pDialog;
    private EditText txt_username, txt_emailid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        try {
            Toolbar toolbar = (Toolbar ) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            /*Init*/
            txt_username = (EditText ) findViewById(R.id.txt_username);
            txt_emailid = (EditText ) findViewById(R.id.txt_emailid);
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Registering...");
            pDialog.setCancelable(true);

        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(RegisterActivity):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnBack_onClick(View view) {
        try {
            RegisterActivity.this.onBackPressed();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnBack_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnRegister_onClick(View view) {
        try {
            String strUsername = txt_username.getText().toString().trim();
            String strEmailid = txt_emailid.getText().toString().trim();
            RegisterRequest(strUsername, strEmailid);
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
        if (AppConstant.IS_MUSIC_ON) {
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
        pDialog.setMessage("Registering...");
        pDialog.setCancelable(true);

        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    private Boolean isValidate(String username, String emailid) {
        Boolean isValid = false;
        try {
            if (username.equals("") || emailid.equals("")) {
                Utilities.showAlertDailog(RegisterActivity.this, "PunnyFuzzles", "Username and Email Id are mandetory!!!", "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        }, true);
            } else if (!Utilities.isValidEmail(emailid.trim())) {
                Utilities.showAlertDailog(RegisterActivity.this, "PunnyFuzzles", "Please enter proper email id.", "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        }, true);
            } else {
                isValid = true;
            }
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(isValidate):" + ex.toString());
        }
        return isValid;
    }

    private void RegisterRequest(String username, String emailid) {
        try {
            if (isValidate(username, emailid)) {
                String url = AppConstant.USER_REGISTRATION + "?username=" + username + "&email=" + emailid;
                showProgressDialog();
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        url, new JSONObject(),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println("responsejson  "+response);
                                try {
                                    JSONObject jsonObject = response.getJSONObject("response");
                                    if (jsonObject.getInt("flag") == 1) {
                                        String strMessage = jsonObject.getString("response");
                                        Utilities.showAlertDailog(RegisterActivity.this, "PunnyFuzzles", "Registration successful", "Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        RegisterActivity.this.onBackPressed();
                                                    }
                                                }, true);
                                    } else {
                                        String strMessage = jsonObject.getString("msg");
                                        Utilities.showAlertDailog(RegisterActivity.this, "PunnyFuzzles", strMessage, "Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {

                                                    }
                                                }, true);
                                    }
                                } catch (Exception e) {
                                    Utilities.showAlertDailog(RegisterActivity.this, "PunnyFuzzles", "Error while registering, please try again.", "Ok",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {

                                                }
                                            }, true);
                                    Log.e(AppConstant.TAG, "Error(RegisterRequest):" + e.toString());
                                }
                                hideProgressDialog();
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Errorresponse  : " + error.getMessage());
                        hideProgressDialog();
                        Utilities.showAlertDailog(RegisterActivity.this, "PunnyFuzzles", "Check your network and please try again.", "Ok",
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
        } catch (Exception e) {
            Log.e(AppConstant.TAG, "Error(RegisterRequest):" + e.toString());
        }
    }
}
