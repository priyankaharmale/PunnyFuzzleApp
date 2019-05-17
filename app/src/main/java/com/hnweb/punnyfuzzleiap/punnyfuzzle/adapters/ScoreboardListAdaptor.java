package com.hnweb.punnyfuzzleiap.punnyfuzzle.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.hnweb.punnyfuzzleiap.punnyfuzzle.R;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.Score;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AlertUtility;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppUtils;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardListAdaptor extends RecyclerView.Adapter<ScoreboardListAdaptor.ViewHolder> implements Filterable {
    private Context context;
    private List<Score> PuzzleRatesList;
    private LayoutInflater inflater;
    String callFrom;
    ProgressDialog pDialog;
    ImageView iv_throwtowel;
    private List<Score> mFilteredList;
    public MyFilter mFilter;

    public ScoreboardListAdaptor(Context context, List<Score> data) {
        this.context = context;
        this.mFilteredList = new ArrayList<Score>();
        this.PuzzleRatesList = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ScoreboardListAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_multiplayerscore, parent, false);
        ScoreboardListAdaptor.ViewHolder vh = new ScoreboardListAdaptor.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ScoreboardListAdaptor.ViewHolder holder, final int i) {
        final Score details = PuzzleRatesList.get(i);
        holder.tv_rank.setText(i + 1 + "");
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
        Log.e("Data", details.toString());
        String username = PuzzleRatesList.get(i).getUsername();
        String puzzleTime = PuzzleRatesList.get(i).getPlay_time();
        String totalsolved = PuzzleRatesList.get(i).getScore();
        String totalpuzzle = PuzzleRatesList.get(i).getTotal_puzzel();
        String played_status = PuzzleRatesList.get(i).getPlayed_status();

        if (username.equals("")) {
            holder.tv_username.setText("No Name");
        } else {
            holder.tv_username.setText(PuzzleRatesList.get(i).getUsername());
        }
        if (puzzleTime.equals("")) {
            holder.tv_time.setText("No Time");
        } else {
            holder.tv_time.setText(PuzzleRatesList.get(i).getPlay_time());
        }

        if (played_status.equalsIgnoreCase("Quit")) {
            holder.iv_throwtowel.setVisibility(View.VISIBLE);
            holder.tv_score.setVisibility(View.GONE);
        } else {
            holder.iv_throwtowel.setVisibility(View.GONE);
            holder.tv_score.setVisibility(View.VISIBLE);

            if (totalsolved.equals("")) {
                holder.tv_score.setText("-");
            } else {
                holder.tv_score.setText(PuzzleRatesList.get(i).getScore());
            }
        }

        if (totalsolved.equals("")) {
            holder.tv_totalsolved.setText("-");
        } else {
            holder.tv_totalsolved.setText(PuzzleRatesList.get(i).getScore());
        }
        if (totalpuzzle.equals("")) {
            holder.tv_totalpuzzle.setText("-");
        } else {
            holder.tv_totalpuzzle.setText(PuzzleRatesList.get(i).getTotal_puzzel());
        }


    }


    @Override
    public int getItemCount() {
        return PuzzleRatesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_totalsolved, tv_username, tv_totalpuzzle, tv_time, tv_score, tv_rank;
        ImageView iv_throwtowel;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_totalsolved = itemView.findViewById(R.id.tv_totalsolved);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_totalpuzzle = itemView.findViewById(R.id.tv_totalpuzzle);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_score = itemView.findViewById(R.id.tv_score);
            tv_rank = itemView.findViewById(R.id.tv_rank);
            iv_throwtowel = itemView.findViewById(R.id.iv_throwtowel);

        }
    }

    @Override
    public Filter getFilter() {

        if (mFilter == null) {
            mFilteredList.clear();
            mFilteredList.addAll(this.PuzzleRatesList);
            mFilter = new MyFilter(this, mFilteredList);
        }
        return mFilter;

    }

    private static class MyFilter extends Filter {

        private final ScoreboardListAdaptor myAdapter;
        private final List<Score> originalList;
        private final List<Score> filteredList;

        private MyFilter(ScoreboardListAdaptor myAdapter, List<Score> originalList) {
            this.myAdapter = myAdapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<Score>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Score leadsModel : originalList) {
                    /*if (leadsModel.getUsername().toLowerCase().contains(filterPattern) || leadsModel.getEmail().toLowerCase().contains(filterPattern)) {
                        filteredList.add(leadsModel);
                        Log.d("FilterData", filteredList.toString());
                    }*/
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            myAdapter.PuzzleRatesList.clear();
            myAdapter.PuzzleRatesList.addAll(( ArrayList<Score> ) filterResults.values);
            myAdapter.notifyDataSetChanged();

        }
    }

    private void invitefrnd(final String id) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.INVITE_FRIEND,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        System.out.println("INVITE_FRIEND" + response);
                        hideProgressDialog();
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            final JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String message = jobj.getString("message");


                            if (message_code == 1) {

                                Utilities.showAlertDailog(context, "PunnyFuzzles", message, "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                /*try {
                                                    JSONObject jsonObject = jobj.getJSONObject("score");

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    System.out.println("jsonexeption" + e.toString());
                                                }
*/

                                            }
                                        }, true);
                            } else {
                                Utilities.showAlertDailog(context, "PunnyFuzzles", message, "Ok",
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
                        String reason = AppUtils.getVolleyError(context, error);
                        AlertUtility.showAlert(context, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("to_user_id", id);
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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    private void showProgressDialog() {

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);

        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

}
