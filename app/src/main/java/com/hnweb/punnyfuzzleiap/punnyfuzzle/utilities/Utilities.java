package com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;


/* Created by Priyanka Harmale on 29/12/2018. */

public class Utilities {

    public static void setMusicChecked(Boolean isMusicOn) {
        try {
           SharedPreferences sharedpreferences = App.getContext().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(AppConstant.SHARE_PREF_MUSIC_ON, isMusicOn);
            editor.commit();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(setMusicChecked):" + ex.toString());
        }
    }

    public static void setLoginSuccess(Boolean isLogin, String userid, String username, String email) {
        try {
            setNOsend(false);
            SharedPreferences sharedpreferences = App.getContext().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            AppConstant.LOGIN_ID = userid;
            AppConstant.LOGIN_NAME = username;
            AppConstant.LOGIN_EMAIL = email;
            AppConstant.TOTAL_PUZZLES = 5;
            AppConstant.TOTAL_PUZZLES_AVAILABLE = 0;

            if (isLogin) {
                editor.putBoolean(AppConstant.SHARE_PREF_LOGIN, true);
                editor.putString(AppConstant.SHARE_PREF_USERID, userid);
                editor.putString(AppConstant.SHARE_PREF_USERNAME, username);
                editor.putString(AppConstant.SHARE_PREF_USEREMAIL, email);
                AppConstant.IS_LOGIN = true;
            } else {
                editor.putBoolean(AppConstant.SHARE_PREF_LOGIN, false);
                editor.putString(AppConstant.SHARE_PREF_USERID, "");
                editor.putString(AppConstant.SHARE_PREF_USERNAME, "");
                editor.putString(AppConstant.SHARE_PREF_USEREMAIL, "");
                AppConstant.IS_LOGIN = false;
            }
            editor.commit();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(setMusicChecked):" + ex.toString());
        }
    }

    public static void setAttemptedQuestions(String strAttemptedQue) {
        try {
            SharedPreferences sharedpreferences = App.getContext().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(AppConstant.SHARE_PREF_ATTEMPTED_QUE, strAttemptedQue);
            editor.commit();
            AppConstant.ATTEMPTED_QUE = strAttemptedQue;
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(setAttemptedQuestions):" + ex.toString());
        }
    }

    public static String getAttemptedQuestions() {
        String strRet = "";
        try {
            SharedPreferences sharedpreferences = App.getContext().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
            strRet = sharedpreferences.getString(AppConstant.SHARE_PREF_ATTEMPTED_QUE, "");
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(setAttemptedQuestions):" + ex.toString());
        }
        return strRet;
    }


    public static void setScore(int intScore) {
        try {
            SharedPreferences sharedpreferences = App.getContext().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(AppConstant.SHARE_PREF_SCORE, intScore);
            editor.commit();
            AppConstant.SCORE = intScore;
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(setScore):" + ex.toString());
        }
    }

    public static int getScore() {
        int intScore = 0;
        try {
            SharedPreferences sharedpreferences = App.getContext().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
            intScore = sharedpreferences.getInt(AppConstant.SHARE_PREF_SCORE, 0);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(setScore):" + ex.toString());
        }
        return intScore;
    }

    public static void setNOsend(Boolean isSend) {
        try {
            SharedPreferences sharedpreferences = App.getContext().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(AppConstant.SHARE_PREF_NOT_SEND, isSend);
            editor.commit();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(setNOsend):" + ex.toString());
        }
    }

    public static Boolean getNOsend() {
        Boolean isSend = false;
        try {
            SharedPreferences sharedpreferences = App.getContext().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
            isSend = sharedpreferences.getBoolean(AppConstant.SHARE_PREF_NOT_SEND, false);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(setNOsend):" + ex.toString());
        }
        return isSend;
    }

    public static void setPurchasedInfo(String strPurchased) {
        try {
            SharedPreferences sharedpreferences = App.getContext().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(AppConstant.SHARE_PREF_PURCHASED, strPurchased);
            editor.commit();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(setPurchasedInfo):" + ex.toString());
        }
    }

    public static String getPurchasedInfo() {
        String strRet = "";
        try {
            SharedPreferences sharedpreferences = App.getContext().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
            strRet = sharedpreferences.getString(AppConstant.SHARE_PREF_PURCHASED, "");
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(getPurchasedInfo):" + ex.toString());
        }
        return strRet;
    }

    public static boolean isInternetAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager ) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    public static void showAlertDailog(Context context, String title, String message,
                                       String positivebtn, DialogInterface.OnClickListener positive_onclicklistner, Boolean cancelable) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setCancelable(cancelable);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton(positivebtn, positive_onclicklistner);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(showAlertDailog):" + ex.toString());
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isSpoonnerism(String strPrimaryWord, String strOption, String solution) {
        boolean isDone = false;
        strPrimaryWord = strPrimaryWord.toUpperCase();
        strOption = strOption.toUpperCase();
        solution = solution.toUpperCase().trim();
        try {
            for (int i = 0; i < strPrimaryWord.length() && isDone == false; i++) {
                String primchars = strPrimaryWord.substring(0, i + 1);

                for (int j = 0; j < strOption.length() && isDone == false; j++) {
                    String optionchars = strOption.substring(0, j + 1);

                    String _strPrimaryWord = Utilities.SwapWord(strPrimaryWord, optionchars, i + 1);
                    String _strOption = Utilities.SwapWord(strOption, primchars, j + 1);

                    String _solution = _strPrimaryWord + " " + _strOption;
                    if (_solution.equals(solution)) {
                        isDone = true;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(isSpoonnerism):" + ex.toString());
        }
        return isDone;
    }

    public static String SwapWord(String strWord, String strPut, int placesfill) {
        String strRet = strWord;
        try {
            strRet = strPut + strWord.substring(placesfill);
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(SwapWord):" + ex.toString());
        }
        return strRet;
    }
}
