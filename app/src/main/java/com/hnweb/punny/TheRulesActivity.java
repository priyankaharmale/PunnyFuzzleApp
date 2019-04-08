package com.hnweb.punny;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hnweb.punny.utilities.AppConstant;
import com.hnweb.punny.utilities.MusicManager;


/**
 * Created by .
 */
public class TheRulesActivity extends AppCompatActivity {

    private Boolean continueMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therules);
        try {
            Toolbar toolbar = (Toolbar ) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);


        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(TheRulesActivity):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnBack_onClick(View view) {
        try {
            TheRulesActivity.this.onBackPressed();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnBack_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnImage_OnClick(View view) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Urban Legend");

            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });
            alertDialogBuilder.setTitle("Clue");
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btn1_5Doller_onClick):" + ex.toString());
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
}
