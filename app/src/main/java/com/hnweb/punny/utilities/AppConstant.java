package com.hnweb.punny.utilities;

import com.hnweb.punny.bo.Puzzle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Priyanka Harmale on 29/12/2018.
 */
public class AppConstant {
    public static String TAG = "DebugPunny";

    public static String SHARE_PREF_MUSIC_ON = "isMusicOn";
    public static Boolean IS_MUSIC_ON = true;
    public static String BASEURL = "http://viyra.com/viyra.com/johnaks/punnyfuzzle/webservice/";


    public static String SHARE_PREF_LOGIN = "isLogin";
    public static String SHARE_PREF_USERID = "userid";
    public static String SHARE_PREF_USERNAME = "username";
    public static String SHARE_PREF_USEREMAIL = "useremail";
    public static Boolean IS_LOGIN = false;
    public static String LOGIN_ID = "";
    public static String LOGIN_NAME = "";
    public static String LOGIN_EMAIL = "";


    public static String SHARE_PREF_NOT_SEND = "not_send";
    public static String SHARE_PREF_ATTEMPTED_QUE = "attempted_que";
    public static String SHARE_PREF_SCORE = "score";
    public static String ATTEMPTED_QUE = "";
    public static int SCORE = 0;
    public static int TOTAL_PUZZLES = 5;
    public static int TOTAL_PUZZLES_AVAILABLE = 0;
    public static boolean REFRESH = false;
    public static boolean REFRESH1 = false;

    public static String SHARE_PREF_PURCHASED = "purchase_det";

    /*Fetching Data From Server*/
    public static String GET_PUZZLES_LIST = BASEURL + "list_puzzles.php";
    public static List<Puzzle> puzzleList = new ArrayList<>();

    public static String USER_LOGIN = BASEURL + "login.php";

    public static String USER_REGISTRATION = BASEURL + "register_android.php";

    public static String POST_PUZZLE_DATA = BASEURL + "post_attempted_puzzles_data_android.php";

    public static String GET_ATTEMPTED_DATA = BASEURL + "get_attempted_puzzles_data_android.php";

    public static String POST_PAYMENT_DATA = BASEURL + "payment_android.php";

    public static String GET_CREDIT_COUNT = BASEURL + "mul_get_credit_count.php";

    public static String GET_PUZLLE_TIER = BASEURL + "mul_get_tier.php";

    public static String MULTI_PURCHASE_PLAN = BASEURL + "mul_purchase_plan.php";

    public static String GET_MULTI_CREDIT_PLAN = BASEURL + "mul_get_credit_plans.php";

    public static String GET_MULTI_PUZZLES_LIST = BASEURL + "mul_get_puzzel.php";

    public static String GIVE_MULTI_ANSWER = BASEURL + "mul_give_ans.php";

    public static String GET_FRIEND_LIST = BASEURL + "mul_get_friends.php";

    public static String SUBMIT_PUZZLE = BASEURL + "mul_submit_puzzel.php";

    public static String INVITE_FRIEND = BASEURL + "mul_send_invitation.php";

    public static String ACCEPT_INVITE = BASEURL + "mul_accpet_invite.php";

    public static String GET_NOTIFICATION_LIST = BASEURL + "mul_get_notification_list.php";

    public static String GET_NOTIFICATION_COUNT = BASEURL + "notification_count.php";

    public static String GET_SCORE_MULTI = BASEURL + "mul_score_board.php";

    public static String QUITE_PUZZLE = BASEURL + "mul_quite_puzzel.php";


    /*SingleAplyer*/

    public static String GET_SINGLE_PUZLLE_TIER = BASEURL + "single_get_tier.php";

    public static String SINGLE_PURCHASE_PLAN = BASEURL + "single_purchase_plan.php";

    public static String GET_SINGLE_CREDIT_PLAN = BASEURL + "single_get_credit_plans.php";

    public static String GET_SINGLE_PUZZLES_LIST = BASEURL + "single_get_puzzel.php";

    public static String GIVE_SINGLE_ANSWER = BASEURL + "single_give_ans.php";

    public static String SINGLE_SUBMIT_PUZZLE = BASEURL + "single_submit_puzzel.php";





}
