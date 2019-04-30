package com.hnweb.punny.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.punny.R;
import com.hnweb.punny.bo.InviteFriend;
import com.hnweb.punny.utilities.AlertUtility;
import com.hnweb.punny.utilities.AppConstant;
import com.hnweb.punny.utilities.AppUtils;
import com.hnweb.punny.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteFriendAdaptor extends RecyclerView.Adapter<InviteFriendAdaptor.ViewHolder> implements Filterable {
    private Context context;
    private List<InviteFriend> PuzzleRatesList;
    private LayoutInflater inflater;
    String callFrom;
    ProgressDialog pDialog;
    private List<InviteFriend> mFilteredList;
    public MyFilter mFilter;

    public InviteFriendAdaptor(Context context, List<InviteFriend> data) {
        this.context = context;
        this.mFilteredList = new ArrayList<InviteFriend>();
        this.PuzzleRatesList = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public InviteFriendAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_invitepalyer, parent, false);
        InviteFriendAdaptor.ViewHolder vh = new InviteFriendAdaptor.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final InviteFriendAdaptor.ViewHolder holder, final int i) {
        final InviteFriend details = PuzzleRatesList.get(i);
        holder.tv_rank.setText(i + 1 + "");
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
        Log.e("Data", details.toString());
        String PuzzleRateTitle = PuzzleRatesList.get(i).getUsername();

        if (PuzzleRateTitle.equals("")) {
            holder.tv_name.setText("No Name");
        } else {
            holder.tv_name.setText(PuzzleRatesList.get(i).getUsername());
        }
        holder.btn_invitefrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invitefrnd(PuzzleRatesList.get(i).getId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return PuzzleRatesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_rank, tv_name;
        Button btn_invitefrnd;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_rank = itemView.findViewById(R.id.tv_rank);
            tv_name = itemView.findViewById(R.id.tv_name);
            btn_invitefrnd = itemView.findViewById(R.id.btn_invitefrnd);

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

        private final InviteFriendAdaptor myAdapter;
        private final List<InviteFriend> originalList;
        private final List<InviteFriend> filteredList;

        private MyFilter(InviteFriendAdaptor myAdapter, List<InviteFriend> originalList) {
            this.myAdapter = myAdapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<InviteFriend>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (InviteFriend leadsModel : originalList) {
                    if (leadsModel.getUsername().toLowerCase().contains(filterPattern) || leadsModel.getEmail().toLowerCase().contains(filterPattern)) {
                        filteredList.add(leadsModel);
                        Log.d("FilterData", filteredList.toString());
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            myAdapter.PuzzleRatesList.clear();
            myAdapter.PuzzleRatesList.addAll(( ArrayList<InviteFriend> ) filterResults.values);
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
