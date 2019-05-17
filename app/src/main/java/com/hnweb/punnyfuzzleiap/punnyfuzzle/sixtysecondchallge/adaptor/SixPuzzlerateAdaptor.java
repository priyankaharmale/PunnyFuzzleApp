package com.hnweb.punnyfuzzleiap.punnyfuzzle.sixtysecondchallge.adaptor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.hnweb.punnyfuzzleiap.punnyfuzzle.R;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.PuzzleRate;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Priyanka Harmale on 30/04/2019.
 */

public class SixPuzzlerateAdaptor extends RecyclerView.Adapter<SixPuzzlerateAdaptor.ViewHolder> {
    private Context context;
    private List<PuzzleRate> PuzzleRatesList;
    private LayoutInflater inflater;
    Drawable drawable;
    private List<PuzzleRate> mFilteredList;
    String str_startDate;
    String callFrom;
    String puzzleid, puzzleamout, totalpuzzle;

    public SixPuzzlerateAdaptor(Context context, List<PuzzleRate> data) {
        this.context = context;
        this.PuzzleRatesList = data;
        this.mFilteredList = new ArrayList<PuzzleRate>();
        this.callFrom = callFrom;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public SixPuzzlerateAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_multiplayerpuzzle, parent, false);
        SixPuzzlerateAdaptor.ViewHolder vh = new SixPuzzlerateAdaptor.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final SixPuzzlerateAdaptor.ViewHolder holder, final int i) {
        final PuzzleRate details = PuzzleRatesList.get(i);


        Log.e("Data", details.toString());
        String PuzzleRateTitle = PuzzleRatesList.get(i).getTitle();

        if (PuzzleRateTitle.equals("")) {
            holder.tv_puzzlerate.setText("No Name");
        } else {
            holder.tv_puzzlerate.setText(PuzzleRatesList.get(i).getTitle());
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  Intent intent = new Intent(context, inappNew.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", details.getTid());
                intent.putExtra("amount", details.getAmount());
                intent.putExtra("totalpuzzle", details.getPuzzle());
                context.startActivity(intent);*/
                addPaymentInfo("10", PuzzleRatesList.get(i).getTid(), PuzzleRatesList.get(i).getAmount(), PuzzleRatesList.get(i).getPuzzle(), PuzzleRatesList.get(i).getCredit());
            }
        });
    }


    @Override
    public int getItemCount() {
        return PuzzleRatesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_puzzlerate;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_puzzlerate = itemView.findViewById(R.id.tv_puzzlerate);

        }
    }

    private void addPaymentInfo(final String order_id, final String puzzleid, final String puzzleamout, final String totalpuzzle, final String credit) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.PURCHASE_PLAN_60SEC

                ,
                new Response.Listener<String>() {

                    public void onResponse(String response) {
                        System.out.println("respnsePaymnet= " + response);
                        try {
                            JSONObject j = new JSONObject(response);

                            System.out.println("test" + response.toString() + response.toString());

                            int message_code = j.getInt("message_code");
                            if (message_code == 1) {
                                String res = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(res)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                String res = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(res)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        } catch (JSONException e) {
                            System.out.println("jsonexeption1111" + e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //selected_views_count
                    params.put("tid", puzzleid);
                    params.put("pay_amt", puzzleamout);
                    params.put("total_puzzel", totalpuzzle);
                    params.put("trans_id", order_id);
                    params.put("user_id", AppConstant.LOGIN_ID);
                    params.put("credit", credit);

                } catch (Exception e) {
                    System.out.println("test" + e.toString());
                }


                System.out.println("myparams" + params);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
