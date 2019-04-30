package com.hnweb.punny;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.hnweb.punny.utilities.AppConstant;
import com.hnweb.punny.utilities.MusicManager;
import com.hnweb.punny.utilities.PostDataTask;
import com.hnweb.punny.utilities.SharedPrefManager;
import com.hnweb.punny.utilities.Utilities;

import okhttp3.MediaType;

public class SplashScreen extends Activity {
    private Boolean continueMusic;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
   // public static final String URL = "https://docs.google.com/forms/d/e/1FAIpQLSf2LNSEcFhHnNG6w94c1XG0QeFBQD1Ni2sy1srsfLOlATLXZg/formResponse";
  //  public static final String EMAIL_KEY = "entry.2044416652";
  //  public static final MediaType FORM_DATA_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getdeviceToken();
        sharedpreferences = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        AppConstant.IS_MUSIC_ON = sharedpreferences.getBoolean(AppConstant.SHARE_PREF_MUSIC_ON, true);
        AppConstant.IS_LOGIN = sharedpreferences.getBoolean(AppConstant.SHARE_PREF_LOGIN, false);
        AppConstant.LOGIN_ID = sharedpreferences.getString(AppConstant.SHARE_PREF_USERID, "");
        AppConstant.LOGIN_NAME = sharedpreferences.getString(AppConstant.SHARE_PREF_USERNAME, "");
        AppConstant.LOGIN_EMAIL = sharedpreferences.getString(AppConstant.SHARE_PREF_USEREMAIL, "");
        AppConstant.TOTAL_PUZZLES = 5;

      //  PostDataTask postDataTask = new PostDataTask();
       // postDataTask.execute(URL, deviceInfo());

        if (!AppConstant.IS_LOGIN) {
            Utilities.setAttemptedQuestions("");
            Utilities.setScore(0);
        } else {
            if (!Utilities.getNOsend()) {
                Utilities.setAttemptedQuestions("");
                Utilities.setScore(0);
            } else {
                AppConstant.ATTEMPTED_QUE = sharedpreferences.getString(AppConstant.SHARE_PREF_ATTEMPTED_QUE, "");
                AppConstant.SCORE = sharedpreferences.getInt(AppConstant.SHARE_PREF_SCORE, 0);
            }
        }

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main_login activity
                if (!AppConstant.IS_LOGIN) {
                    Utilities.setAttemptedQuestions("");
                    Utilities.setScore(0);
                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    if (!Utilities.getNOsend()) {
                        Utilities.setAttemptedQuestions("");
                        Utilities.setScore(0);
                    } else {
                        AppConstant.ATTEMPTED_QUE = sharedpreferences.getString(AppConstant.SHARE_PREF_ATTEMPTED_QUE, "");
                        AppConstant.SCORE = sharedpreferences.getInt(AppConstant.SHARE_PREF_SCORE, 0);
                    }
                }


                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
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

    public String deviceInfo() {

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        String s = "Debug-infos:";
        s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
        s += "\n Device: " + android.os.Build.DEVICE;
        s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";

        s += "\n RELEASE: " + android.os.Build.VERSION.RELEASE;
        s += "\n BRAND: " + android.os.Build.BRAND;
        s += "\n DISPLAY: " + android.os.Build.DISPLAY;
        s += "\n CPU_ABI: " + android.os.Build.CPU_ABI;
        s += "\n CPU_ABI2: " + android.os.Build.CPU_ABI2;
        s += "\n UNKNOWN: " + android.os.Build.UNKNOWN;
        s += "\n HARDWARE: " + android.os.Build.HARDWARE;
        s += "\n Build ID: " + android.os.Build.ID;
        s += "\n MANUFACTURER: " + android.os.Build.MANUFACTURER;
        s += "\n SERIAL: " + android.os.Build.SERIAL;
        s += "\n USER: " + android.os.Build.USER;
        s += "\n HOST: " + android.os.Build.HOST;
        s += "\n APK Version: " + version;

        return s;
    }
    private void getdeviceToken() {
        try {
            String deviceToken = SharedPrefManager.getInstance(this).getDeviceToken();

            if(!TextUtils.isEmpty(deviceToken))
            {
                Log.d("TokanisEmpty", deviceToken);

            }else {
                Log.d("TokanisEmpty", "Not Register");

            }


        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}
