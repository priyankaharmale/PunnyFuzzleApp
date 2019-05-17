package com.hnweb.punnyfuzzleiap.punnyfuzzle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.MusicManager;


/**

 * Created by Priyanka Harmale on 02/01/2019

 */
public class InfoActivity extends AppCompatActivity {

    private Boolean continueMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        try {
            Toolbar toolbar = (Toolbar ) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);


        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(InfoActivity):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnBack_onClick(View view) {
        try {
            InfoActivity.this.onBackPressed();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnBack_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnVisitSite_onClick(View view) {
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.visit_url)));
            startActivity(myIntent);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnVisitSite_onClick):" + ex.toString());
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
