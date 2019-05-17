package com.hnweb.punnyfuzzleiap.punnyfuzzle.multiplayer.adaptor;

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

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;

import com.hnweb.punnyfuzzleiap.punnyfuzzle.R;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.Puzzle;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.App;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.Utilities;

import java.util.List;

public class PuzzlesAdapter extends RecyclerView.Adapter<PuzzlesAdapter.MyViewHolder> {

    private List<Puzzle> puzzleList;
    private Activity activity;
    private TextView lbl_current_score;

    private Puzzle selectedPuzzle = null;
    private ProgressDialog pDialog;

    private TextView txtScore;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout list_item_view;
        public TextView txtPuzzleNo, txtPrimaryWord, lbloptions;
        public ImageButton btnGiveAnswer;

        public ImageView img_puzzle;

        public MyViewHolder(View view, final List<Puzzle> puzzleList, final Activity activity) {
            super(view);

            list_item_view = (RelativeLayout ) view.findViewById(R.id.list_item_view);

            txtPuzzleNo = (TextView ) view.findViewById(R.id.txt_puzzle_no);
            txtPrimaryWord = (TextView ) view.findViewById(R.id.txt_primary_word);
            lbloptions = (TextView ) view.findViewById(R.id.lbloptions);

            btnGiveAnswer = (ImageButton ) view.findViewById(R.id.btn_give_answer);
            btnGiveAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = Integer.parseInt(v.getTag().toString());
                        selectedPuzzle = puzzleList.get(pos);

                        if (selectedPuzzle.getAttempted()) {
                            Utilities.showAlertDailog(activity, "PunnyFuzzles", "You have already played this puzzle.", "Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }, true);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Choose an Option");
                            String[] options = {"a) " + selectedPuzzle.getOption1(), "b) " + selectedPuzzle.getOption2(), "c) " + selectedPuzzle.getOption3()};
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new CheckAnswer(selectedPuzzle, which).execute();
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

            img_puzzle = view.findViewById(R.id.img_puzzle);


        }
    }


    public PuzzlesAdapter(List<Puzzle> puzzleList, Activity activity, TextView lbl_current_score) {
        this.puzzleList = puzzleList;
        this.activity = activity;
        this.lbl_current_score = lbl_current_score;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.puzzle_list_row, parent, false);

        return new MyViewHolder(itemView, puzzleList, activity);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            Puzzle puzzle = puzzleList.get(position);
            holder.txtPuzzleNo.setText(puzzle.getId() + " of " + puzzleList.size());
            holder.txtPrimaryWord.setText(puzzle.getPrimary_word());

            /*a) Dish\n\nb) Rare\n\nc) Boot*/
            String strOptions = "a) " + puzzle.getOption1() + "\n\n" +
                    "b) " + puzzle.getOption2() + "\n\n" +
                    "c) " + puzzle.getOption3();

            holder.lbloptions.setText(strOptions);

            holder.btnGiveAnswer.setTag(position);

            ImageLoader imageLoader = App.getInstance().getImageLoader();
            // If you are using NetworkImageView
            // holder.img_puzzle.setImageUrl("http://viyra.com/viyra.com/johnvij/spoonerism/admin" + puzzle.getImage_url(), imageLoader);
            Glide.with(activity).load("http://viyra.com/viyra.com/johnvij/spoonerism/admin" + puzzle.getImage_url()).into(holder.img_puzzle);
            holder.img_puzzle.setTag(position);

            holder.img_puzzle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = Integer.parseInt(v.getTag().toString());
                        Puzzle p = puzzleList.get(pos);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setMessage(p.getImage_caption());

                        alertDialogBuilder.setPositiveButton("Ok", null);
                        alertDialogBuilder.setTitle("Clue");
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } catch (Exception ex) {
                        Log.e(App.TAG, "Error(img_puzzle):" + ex.toString());
                    }
                }
            });
            if (puzzle.getAttempted()) {
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
    }

    public void enableDisableView(View view, boolean enabled) {
        try {
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup ) view;

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
        int size = 0;
        try {
            size = puzzleList.size();
//            Log.d(AppConstant.TAG, "Isize:" + size);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(getItemCount):" + ex.toString());
        }
        return size;
    }

    public void refreshAdapter() {
        PuzzlesAdapter.this.notifyDataSetChanged();
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
                    lbl_current_score.setText(AppConstant.SCORE + "");
                    AlertDialog.Builder alertadd = new AlertDialog.Builder(activity);
                    LayoutInflater factory = LayoutInflater.from(activity);
                    final View view = factory.inflate(R.layout.dialog_correct_answer, null);
                    alertadd.setTitle("Correct Answer");
                    alertadd.setView(view);
                    alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {

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
}
