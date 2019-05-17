package com.hnweb.punnyfuzzleiap.punnyfuzzle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.multiplayer.activity.CreditListActivity;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.App;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.MusicManager;

/**
 * Created by Priyanka Harmale on 03/01/2019.
 */

public class ScoreActivity extends AppCompatActivity {
    String TAG = ScoreActivity.class.getSimpleName();
    Button btn_scoreboard;
    Button button2;
    public static String EXTRA_SCORE = "extra_score";


    private Boolean continueMusic;

    private ProgressDialog pDialog;
    ImageView trophy_ranking_scoreboard;

    private TextView lblscore, tv_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        try {
            Toolbar toolbar = ( Toolbar ) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            lblscore = ( TextView ) findViewById(R.id.lblscore);
            btn_scoreboard = findViewById(R.id.btn_scoreboard);
            button2 = findViewById(R.id.button2);
            tv_username = findViewById(R.id.tv_username);
            trophy_ranking_scoreboard = findViewById(R.id.trophy_ranking_scoreboard);
            // lblMotivationText = ( TextView ) findViewById(R.id.lbl_motivation_text);
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Getting Score...");
            pDialog.setCancelable(true);

//            if (AppConstant.IS_LOGIN) {
//                getScore();
//            } else {
            Bundle bundle = getIntent().getExtras();
            int score = bundle.getInt(EXTRA_SCORE);
            lblscore.setText(score + "");
            tv_username.setText(AppConstant.LOGIN_NAME);
            //lblMotivationText.setText(getMotivationText(score));
//            }

        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(ScoreActivity):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
        Glide.with(this).load(R.raw.scoreboard_gif).asGif().into(trophy_ranking_scoreboard);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreActivity.this, CreditListActivity.class);
                startActivity(intent);
            }
        });
    }

    public void btnBack_onClick(View view) {
        try {
            ScoreActivity.this.onBackPressed();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnBack_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

   /* public void btnVisitSite_onClick(View view) {
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.visit_url)));
            startActivity(myIntent);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnVisitSite_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }*/

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

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Getting Score...");
        pDialog.setCancelable(true);

        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    private String getMotivationText(int score) {
        String strRet = "";
        try {
            if (score < 3) {
                strRet = "Puzzled and Color me disappointed";
            } else if (score < 5) {
                strRet = "Novice Puzzler";
            } else if (score < 7) {
                strRet = "Puzzler of Distinguished Honor";
            } else if (score < 9) {
                strRet = "Puzzle Master 1st Degree";
            } else if (score < 16) {
                strRet = "Puzzle Master 2nd Degree";
            } else if (score < 31) {
                strRet = "Puzzle Master 3rd Degree";
            } else if (score < 51) {
                strRet = "Puzzle Master 4th Degree";
            } else {
                strRet = "Puzzle Master 5th Degree";
            }
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(getMotivationText):" + ex.toString());
        }
        return strRet;
    }
}

