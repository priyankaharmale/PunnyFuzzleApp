package com.hnweb.punnyfuzzleiap.punnyfuzzle.singleplayer;

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
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.hnweb.punnyfuzzleiap.punnyfuzzle.R;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.PuzzleRate;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.singleplayer.adaptor.SinglePuzzlerateAdaptor;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AlertUtility;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SinglePlayerPuzzlePurchase extends AppCompatActivity {
    RecyclerView recycler_view;
    ProgressDialog pDialog;
    SinglePuzzlerateAdaptor puzzleRateAdaptor;
    ArrayList<PuzzleRate> puzzleRates;
    Button btn_credithead;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayerpuzzle);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
        recycler_view = findViewById(R.id.recycler_view);
        btn_credithead = findViewById(R.id.btn_credithead);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recycler_view.setLayoutManager(layoutManager);

        btn_credithead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SinglePlayerCreditListActivity.class);
                startActivity(intent);
            }
        });
        getPuzzleRate();
    }


    private void getPuzzleRate() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_SINGLE_PUZLLE_TIER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("MessagesResponse" + response);
                        hideProgressDialog();
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);
                            if (message_code == 1) {

                                recycler_view.setVisibility(View.VISIBLE);
                                JSONArray userdetails = jobj.getJSONArray("detail");
                                puzzleRates = new ArrayList<PuzzleRate>();
                                Log.d("ArrayLengthNails", String.valueOf(userdetails.length()));

                                for (int j = 0; j < userdetails.length(); j++) {
                                    JSONObject jsonObject = userdetails.getJSONObject(j);
                                    PuzzleRate puzzleRate = new PuzzleRate();
                                    puzzleRate.setTid(jsonObject.getString("tid"));
                                    puzzleRate.setPid(jsonObject.getString("pid"));
                                    puzzleRate.setAmount(jsonObject.getString("amount"));
                                    puzzleRate.setPuzzle(jsonObject.getString("puzzle"));
                                    puzzleRate.setTitle(jsonObject.getString("title"));
                                    puzzleRate.setUpdated_dt(jsonObject.getString("updated_dt"));
                                    puzzleRate.setDeleted(jsonObject.getString("deleted"));
                                    puzzleRate.setCredit(jsonObject.getString("credit"));
                                    puzzleRate.setPlayed_status(jsonObject.getString("played_status"));


                                    puzzleRates.add(puzzleRate);
                                    Log.d("ArraySize", String.valueOf(puzzleRates.size()));


                                }
                                puzzleRateAdaptor = new SinglePuzzlerateAdaptor(SinglePlayerPuzzlePurchase.this, puzzleRates);
                                recycler_view.setAdapter(puzzleRateAdaptor);
                            } else {

                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(SinglePlayerPuzzlePurchase.this);
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
                        String reason = AppUtils.getVolleyError(SinglePlayerPuzzlePurchase.this, error);
                        AlertUtility.showAlert(SinglePlayerPuzzlePurchase.this, reason);
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
        RequestQueue requestQueue = Volley.newRequestQueue(SinglePlayerPuzzlePurchase.this);
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
