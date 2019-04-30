package com.hnweb.punny.multiplayer.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hnweb.punny.multiplayer.activity.MultiPlayerActivity;
import com.hnweb.punny.R;
import com.hnweb.punny.bo.Puzzle;
import com.hnweb.punny.interfaces.OnCallBack;
import com.hnweb.punny.utilities.AlertUtility;
import com.hnweb.punny.utilities.App;
import com.hnweb.punny.utilities.AppConstant;
import com.hnweb.punny.utilities.AppUtils;
import com.hnweb.punny.utilities.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiplayerPuzzlesAdapter extends RecyclerView.Adapter<MultiplayerPuzzlesAdapter.MyViewHolder> {

    private List<Puzzle> puzzleList;
    private Activity activity;
    String pid;
    private ProgressDialog pDialog;
    OnCallBack onCallBack;
    private TextView txtScore;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout list_item_view;
        public TextView txtPuzzleNo, txtPrimaryWord, lbloptions;
        public ImageButton btnGiveAnswer;

        public ImageView img_puzzle;

        public MyViewHolder(View view, final List<Puzzle> puzzleList, final Activity activity) {
            super(view);

            list_item_view = ( RelativeLayout ) view.findViewById(R.id.list_item_view);

            txtPuzzleNo = ( TextView ) view.findViewById(R.id.txt_puzzle_no);
            txtPrimaryWord = ( TextView ) view.findViewById(R.id.txt_primary_word);
            lbloptions = ( TextView ) view.findViewById(R.id.lbloptions);

            btnGiveAnswer = ( ImageButton ) view.findViewById(R.id.btn_give_answer);


            img_puzzle = view.findViewById(R.id.img_puzzle);


        }
    }


    public MultiplayerPuzzlesAdapter(List<Puzzle> puzzleList, Activity activity, String pid, OnCallBack onCallBack) {
        this.puzzleList = puzzleList;
        this.activity = activity;
        this.pid = pid;
        this.onCallBack = onCallBack;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multi_puzzle_list_row, parent, false);
        pDialog = new ProgressDialog(activity);
        //pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
        return new MyViewHolder(itemView, puzzleList, activity);
    }

    @SuppressLint({"NewApi", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            Puzzle puzzle = puzzleList.get(position);
            holder.txtPuzzleNo.setText(position +1+ " of " + puzzleList.size());
            holder.txtPrimaryWord.setText(puzzle.getPrimary_word());

            /*a) Dish\n\nb) Rare\n\nc) Boot*/
            String strOptions = "a) " + puzzle.getOption1() + "\n\n" +
                    "b) " + puzzle.getOption2() + "\n\n" +
                    "c) " + puzzle.getOption3();

            holder.lbloptions.setText(strOptions);

            holder.btnGiveAnswer.setTag(position);
           // holder.img_puzzle.setTag(position);

            Glide.with(activity).load("http://viyra.com/viyra.com/johnvij/spoonerism/admin" + puzzleList.get(position).getImage_url()).into(holder.img_puzzle);

            holder.img_puzzle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setMessage(puzzleList.get(position).getImage_caption());

                        alertDialogBuilder.setPositiveButton("Ok", null);
                        alertDialogBuilder.setTitle("Clue");
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } catch (Exception ex) {
                        Log.e(App.TAG, "Error(img_puzzle):" + ex.toString());
                    }
                }
            });
            if (puzzle.getPlayed_status().equalsIgnoreCase("Played")) {
                if (!puzzle.getFree()) {
                    enableDisableView(holder.list_item_view, false);
                    holder.list_item_view.setAlpha(.5f);
                    holder.list_item_view.setBackground(App.getContext().getResources().getDrawable(R.drawable.lock));
                } else {
                    enableDisableView(holder.list_item_view, true);
                    holder.list_item_view.setAlpha(1f);
                    holder.list_item_view.setBackgroundColor(App.getContext().getResources().getColor(android.R.color.transparent));
                }
            } else {
                enableDisableView(holder.list_item_view, true);
                holder.list_item_view.setAlpha(1f);
                holder.list_item_view.setBackgroundColor(App.getContext().getResources().getColor(android.R.color.transparent));
            }
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(onBindViewHolder):" + ex.toString());
        }


        holder.btnGiveAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (puzzleList.get(position).getAttempted()) {
                        Utilities.showAlertDailog(activity, "PunnyFuzzles", "You have already played this puzzle.", "Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }, true);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Choose an Option");
                        String[] options = {"a) " + puzzleList.get(position).getOption1(), "b) " + puzzleList.get(position).getOption2(), "c) " + puzzleList.get(position).getOption3()};
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new CheckAnswer(puzzleList.get(position), which).execute();
                            }
                        });
                        builder.setNegativeButton("Cancel", null);
                        AlertDialog actions = builder.create();

                        actions.show();
                    }
                } catch (Exception ex) {
                    Log.e(AppConstant.TAG, "Error(btnGiveAnswer):" + ex.toString());
                }
            }
        });
    }

    public void enableDisableView(View view, boolean enabled) {
        try {
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                ViewGroup group = ( ViewGroup ) view;

                for (int idx = 0; idx < group.getChildCount(); idx++) {
                    enableDisableView(group.getChildAt(idx), enabled);
                }
            }
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(enableDisableView):" + ex.toString());
        }
    }

    @Override
    public int getItemCount() {
        return puzzleList.size();

    }

    public void refreshAdapter() {
        MultiplayerPuzzlesAdapter.this.notifyDataSetChanged();
    }

    private class CheckAnswer extends AsyncTask<String, Void, Boolean> {
        private int givenAnswer = -1;
        private Puzzle p;

        public CheckAnswer(Puzzle p, int givenAnswer) {
            this.p = p;
            this.givenAnswer = givenAnswer;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Checking answer...");
            pDialog.setCancelable(true);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean isSpoonerism = false;
            try {
                isSpoonerism = Utilities.isSpoonnerism(p.getPrimary_word(), getOptionText(p, givenAnswer), p.getSolution());
//                Log.d(AppConstant.TAG, "isSpoonerism:" + isSpoonerism);
            } catch (Exception ex) {
                Log.e(AppConstant.TAG, "Error(PuzzlesAdapter):" + ex.toString());
            }
            return isSpoonerism;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            try {

                if (pDialog.isShowing())
                    pDialog.dismiss();

                Utilities.setNOsend(true);
                if (!AppConstant.ATTEMPTED_QUE.contains(p.getPnumber())) {
                    AppConstant.ATTEMPTED_QUE += p.getPnumber() + ",";
                }
                Utilities.setAttemptedQuestions(AppConstant.ATTEMPTED_QUE);
                p.setAttempted(true);
                if (result) {
                    p.setRight(true);
                    Utilities.setScore((AppConstant.SCORE + 1));
                    //  lbl_current_score.setText(AppConstant.SCORE + "");
                    AlertDialog.Builder alertadd = new AlertDialog.Builder(activity);
                    LayoutInflater factory = LayoutInflater.from(activity);
                    final View view = factory.inflate(R.layout.dialog_correct_answer, null);
                    alertadd.setTitle("Correct Answer");
                    alertadd.setView(view);
                    alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {
                            getMultiPuzzleList(p.getId(), "Yes");
                        }
                    });
                    alertadd.show();
                } else {
                    p.setRight(false);
                    AlertDialog.Builder alertadd = new AlertDialog.Builder(activity);
                    LayoutInflater factory = LayoutInflater.from(activity);
                    final View view = factory.inflate(R.layout.dialog_incorrect_answer, null);
                    alertadd.setTitle("Incorrect Answer");
                    alertadd.setView(view);
                    alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {
                            getMultiPuzzleList(p.getId(), "No");

                        }
                    });
                    alertadd.show();
                }
                refreshAdapter();
            } catch (Exception ex) {
                Log.e(AppConstant.TAG, "Error(PuzzlesAdapter):" + ex.toString());
            }
        }

        private String getOptionText(Puzzle p, int givenAnswer) {
            String option = "";
            try {
                switch (givenAnswer) {
                    case 0:
                        option = p.getOption1();
                        break;
                    case 1:
                        option = p.getOption2();
                        break;
                    case 2:
                        option = p.getOption3();
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                Log.e(AppConstant.TAG, "Error(PuzzlesAdapter-getOptionText):" + ex.toString());
            }
            return option;
        }

    }

    private void getMultiPuzzleList(final String id, final String answer) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GIVE_MULTI_ANSWER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("GIVE_MULTI_ANSWER" + response);
                        hideProgressDialog();
                        Log.i("Response", "MessagesResponse= " + response);
                        refresh();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(activity, error);
                        AlertUtility.showAlert(activity, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    final String time = MultiPlayerActivity.timerValue.getText().toString();

                    params.put("pid", pid);
                    params.put("user_id", AppConstant.LOGIN_ID);
                    params.put("played_puzzel", id);
                    params.put("is_ans_correct", answer);
                    params.put("time", time);
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
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);

    }

    private void showProgressDialog() {

        pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    private void refresh() {
        onCallBack.callbackrefresh();

    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
