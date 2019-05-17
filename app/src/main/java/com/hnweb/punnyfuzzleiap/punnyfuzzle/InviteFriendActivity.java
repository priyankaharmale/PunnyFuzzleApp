package com.hnweb.punnyfuzzleiap.punnyfuzzle;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.adapters.InviteFriendAdaptor;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.InviteFriend;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AlertUtility;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppConstant;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.AppUtils;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.MusicManager;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InviteFriendActivity extends AppCompatActivity {
    RecyclerView recycler_view;
    ProgressDialog pDialog;
    ArrayList<InviteFriend> inviteFriends;
    InviteFriendAdaptor inviteFriendAdaptor;
    TextView tv_userName;
    ImageView iv_sound;
    private Boolean continueMusic = true;
    Boolean isplayed = true;
    SearchView searchView;
    TextView textView_empty_list;
    ImageView mCloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitefrnd);
        tv_userName = findViewById(R.id.tv_userName);
        iv_sound = findViewById(R.id.switch_sound);
        textView_empty_list = findViewById(R.id.textView_empty_list);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
        recycler_view = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recycler_view.setLayoutManager(layoutManager);

        tv_userName.setText(AppConstant.LOGIN_NAME);


        getfriendList();

        if (AppConstant.IS_MUSIC_ON == true) {
            if (continueMusic == true) {
                AppConstant.IS_MUSIC_ON = true;
                Utilities.setMusicChecked(true);
                continueMusic = false;
                MusicManager.start(this, MusicManager.MUSIC_MENU);
                isplayed = false;
                iv_sound.setImageResource(R.drawable.volume_up_indicator);


            } else {
                iv_sound.setImageResource(R.drawable.volume_off_indicatornew);

                Utilities.setMusicChecked(false);
                AppConstant.IS_MUSIC_ON = false;
                if (!continueMusic) {
                    MusicManager.pause();

                }
                continueMusic = true;

                isplayed = false;
                iv_sound.setImageResource(R.drawable.volume_off_indicatornew);
            }
        }
        iv_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (continueMusic == true) {
                    AppConstant.IS_MUSIC_ON = true;
                    Utilities.setMusicChecked(true);
                    continueMusic = false;
                    MusicManager.start(InviteFriendActivity.this, MusicManager.MUSIC_MENU);
                    isplayed = false;
                    iv_sound.setImageResource(R.drawable.volume_up_indicator);


                } else {
                    Utilities.setMusicChecked(false);
                    AppConstant.IS_MUSIC_ON = false;
                    if (!continueMusic) {
                        MusicManager.pause();

                    }
                    continueMusic = true;

                    isplayed = false;
                    iv_sound.setImageResource(R.drawable.volume_off_indicatornew);
                }
            }
        });

        SearchManager searchManager = ( SearchManager ) getSystemService(Context.SEARCH_SERVICE);
        searchView = findViewById(R.id.searchView_my_task);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // Add Text Change Listener to EditText
        mCloseButton = searchView.findViewById(R.id.search_close_btn);
        mCloseButton.setVisibility(View.VISIBLE);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // linearLayout.setVisibility(View.VISIBLE);
              //  searchView.setVisibility(View.GONE);
                searchView.setQuery("", true);

                getfriendList();

            }
        });
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.toString().trim().length() == 0) {
                    ///linearLayout.setVisibility(View.VISIBLE);
                    //searchView.setVisibility(View.GONE);
                    // mCloseButton.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                    mCloseButton.setVisibility(View.VISIBLE);
                    mCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //    linearLayout.setVisibility(View.VISIBLE);
                         //   searchView.setVisibility(View.GONE);
                            searchView.setQuery("", true);
                            getfriendList();

                        }
                    });
                }
                try {
                    if (inviteFriends.size() == 0) {
                        textView_empty_list.setVisibility(View.VISIBLE);
                        recycler_view.setVisibility(View.GONE);

                    } else {
                        inviteFriendAdaptor.getFilter().filter(query.toString());

                    }

                } catch (NullPointerException e) {

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(), "New Text "+newText, Toast.LENGTH_SHORT).show();
                if (newText.toString().trim().length() == 0) {
                    //linearLayout.setVisibility(View.VISIBLE);
                    //searchView.setVisibility(View.GONE);
                    mCloseButton.setVisibility(View.VISIBLE);
                    mCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /// linearLayout.setVisibility(View.VISIBLE);
                            //searchView.setVisibility(View.GONE);
                            searchView.setQuery("", true);
                            getfriendList();


                        }
                    });
                }

                try {
                    if (inviteFriends.size() == 0) {
                        // Toast.makeText(getActivity(), "No match Found", Toast.LENGTH_SHORT).show();
                        textView_empty_list.setVisibility(View.VISIBLE);
                        recycler_view.setVisibility(View.GONE);

                    } else {
                        inviteFriendAdaptor.getFilter().filter(newText.toString());
                    }

                } catch (NullPointerException e) {
                }

                return true;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //   linearLayout.setVisibility(View.VISIBLE);
               // searchView.setVisibility(View.GONE);
                return false;
            }
        });


    }

    private void getfriendList() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.GET_FRIEND_LIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("GET_MULTI_CREDIT_PLAN" + response);
                        hideProgressDialog();
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);
                            if (message_code == 1) {
                                textView_empty_list.setVisibility(View.GONE);
                                recycler_view.setVisibility(View.VISIBLE);
                                JSONArray userdetails = jobj.getJSONArray("listing");
                                inviteFriends = new ArrayList<InviteFriend>();
                                Log.d("ArrayLengthNails", String.valueOf(userdetails.length()));

                                for (int j = 0; j < userdetails.length(); j++) {
                                    JSONObject jsonObject = userdetails.getJSONObject(j);

                                    InviteFriend puzzleRate = new InviteFriend();
                                    puzzleRate.setId(jsonObject.getString("id"));
                                    puzzleRate.setEmail(jsonObject.getString("email"));
                                    puzzleRate.setUsername(jsonObject.getString("username"));
                                    puzzleRate.setDate_time(jsonObject.getString("date_time"));

                                    inviteFriends.add(puzzleRate);
                                    Log.d("ArraySize", String.valueOf(inviteFriends.size()));
                                }
                                inviteFriendAdaptor = new InviteFriendAdaptor(InviteFriendActivity.this, inviteFriends);
                                recycler_view.setAdapter(inviteFriendAdaptor);
                            } else {

                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(InviteFriendActivity.this);
                                builder.setMessage(msg)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                textView_empty_list.setVisibility(View.VISIBLE);
                                                recycler_view.setVisibility(View.GONE);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(InviteFriendActivity.this, error);
                        AlertUtility.showAlert(InviteFriendActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("user_id", AppConstant.LOGIN_ID);
                    Log.e("userId", AppConstant.LOGIN_ID);
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
        RequestQueue requestQueue = Volley.newRequestQueue(InviteFriendActivity.this);
        requestQueue.add(stringRequest);

    }

    private void showProgressDialog() {

        pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }
}
