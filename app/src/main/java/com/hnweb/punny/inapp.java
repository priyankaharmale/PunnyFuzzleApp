/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hnweb.punny;
/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import com.hnweb.punny.util.IabBroadcastReceiver;
import com.hnweb.punny.util.IabHelper;
import com.hnweb.punny.util.IabResult;
import com.hnweb.punny.util.Inventory;
import com.hnweb.punny.util.Purchase;
import com.hnweb.punny.utilities.App;
import com.hnweb.punny.utilities.AppConstant;
import com.hnweb.punny.utilities.MusicManager;
import com.hnweb.punny.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example game using in-app billing version 3.
 *
 * Before attempting to run this sample, please read the README file. It
 * contains important information on how to set up this project.
 *
 * All the game-specific logic is implemented here in MainActivity, while the
 * general-purpose boilerplate that can be reused in any app is provided in the
 * classes in the util/ subdirectory. When implementing your own application,
 * you can copy over util/*.java to make use of those utility classes.
 *
 * This game is a simple "driving" game where the player can buy gas
 * and drive. The car has a tank which stores gas. When the player purchases
 * gas, the tank fills up (1/4 tank at a time). When the player drives, the gas
 * in the tank diminishes (also 1/4 tank at a time).
 *
 * The user can also purchase a "premium upgrade" that gives them a red car
 * instead of the standard blue one (exciting!).
 *
 * The user can also purchase a subscription ("infinite gas") that allows them
 * to drive without using up any gas while that subscription is active.
 *
 * It's important to note the consumption mechanics for each item.
 *
 * PREMIUM: the item is purchased and NEVER consumed. So, after the original
 * purchase, the player will always own that item. The application knows to
 * display the red car instead of the blue one because it queries whether
 * the premium "item" is owned or not.
 *
 * INFINITE GAS: this is a subscription, and subscriptions can't be consumed.
 *
 * GAS: when gas is purchased, the "gas" item is then owned. We consume it
 * when we apply that item's effects to our app's world, which to us means
 * filling up 1/4 of the tank. This happens immediately after purchase!
 * It's at this point (and not when the user drives) that the "gas"
 * item is CONSUMED. Consumption should always happen when your game
 * world was safely updated to apply the effect of the purchase. So,
 * in an example scenario:
 *
 * BEFORE:      tank at 1/2
 * ON PURCHASE: tank at 1/2, "gas" item is owned
 * IMMEDIATELY: "gas" is consumed, tank goes to 3/4
 * AFTER:       tank at 3/4, "gas" item NOT owned any more
 *
 * Another important point to notice is that it may so happen that
 * the application crashed (or anything else happened) after the user
 * purchased the "gas" item, but before it was consumed. That's why,
 * on startup, we check if we own the "gas" item, and, if so,
 * we have to apply its effects to our world and consume it. This
 * is also very important!
 */
public class inapp extends Activity implements IabBroadcastReceiver.IabBroadcastListener,
        DialogInterface.OnClickListener {
    // Debug tag, for logging
  //  static final String TAG = "TrivialDrive";

    // Does the user have the premium upgrade?
    boolean mIsPremium = false;

    // Does the user have an active subscription to the infinite gas plan?
//    boolean mSubscribedToInfiniteGas = false;

    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;

    // Tracks the currently owned infinite gas SKU, and the options in the Manage dialog
    String mInfiniteGasSku = "";
    String mFirstChoiceSku = "";
    String mSecondChoiceSku = "";

    // Used to select between purchasing gas on a monthly or yearly basis
    String mSelectedSubscriptionPeriod = "";

    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    static final String SKU_PREMIUM = "40puzzle";
    static final String SKU_GAS = "100puzzle";

    // SKU for our subscription (infinite gas)
  //  static final String SKU_INFINITE_GAS_MONTHLY = "infinite_gas_monthly";
 //   static final String SKU_INFINITE_GAS_YEARLY = "infinite_gas_yearly";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // Graphics for the gas gauge
    static int[] TANK_RES_IDS = { R.drawable.gas0, R.drawable.gas1, R.drawable.gas2,
            R.drawable.gas3, R.drawable.gas4 };

    // How many units (1/4 tank is our unit) fill in the tank.
    static final int TANK_MAX = 4;

    // Current amount of gas in tank, in units
    int mTank;

    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;


    ////punnyfuzzle



    private String TAG = inapp.class.getSimpleName();

    private Activity activity;

    private Boolean continueMusic;

    /*StartPaypal*/

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "ARdoaX1Z7r_bU1GERLz0fkWU9xI4hl8H4JaPJNprmOWaQkQ3Uqc9SHKI5rh2rPO6ilXYU8tz9fnupJoN";

    private static final int REQUEST_CODE_PAYMENT_40 = 2;
    private static final int REQUEST_CODE_PAYMENT_100 = 3;


    /*EndPaypal*/

    private ProgressDialog pDialog1;
    private ProgressDialog pDialog2;
    private ProgressDialog pDialog3;


    //////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inapp);

        // load game data
        loadData();

        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgNspap5ostykSJ/2oxQSzG/h5Rnw8HBvcyIcJe7pahuFahfrzCIThSe4o94BVCxTpB8PI1s6qYPh5Z79G/l9tQOS61CSdLnd3FQBxryAIW4A2+dfbMtvMuQuxEDjvQHCcc8O7o8RAfaMROxxNxnie4x83WQcXGZoSf10XYTYkuTvDG/umrw/GYumRQSZeRguMhQeGzqzdyZBWgcpV1HYQaKItn5U9t3kYqgEhjUZZRJWtzHN64QXa+4gjq7C7KD5h8oZja/thVRvF8Hc4PeMyXg3DB6pUkunDkekiUD4jn0Xows4IIjj9H5ZFbYeWXKg+AWjygpmOZq03Hmg6tMhZwIDAQAB";

        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please put your app's public key in MainActivity.java. See README.");
        }
        if (getPackageName().startsWith("com.example")) {
            throw new RuntimeException("Please change the sample's package name! See README.");
        }

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(inapp.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
        consumeProducts();
    }

    private void consumeProducts() {




    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

          /*  // First find out which subscription is auto renewing
            Purchase gasMonthly = inventory.getPurchase(SKU_INFINITE_GAS_MONTHLY);
            Purchase gasYearly = inventory.getPurchase(SKU_INFINITE_GAS_YEARLY);
            if (gasMonthly != null && gasMonthly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_MONTHLY;
                mAutoRenewEnabled = true;
            } else if (gasYearly != null && gasYearly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_YEARLY;
                mAutoRenewEnabled = true;
            } else {
                mInfiniteGasSku = "";
                mAutoRenewEnabled = false;
            }
*/
            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
         /*   mSubscribedToInfiniteGas = (gasMonthly != null && verifyDeveloperPayload(gasMonthly))
                    || (gasYearly != null && verifyDeveloperPayload(gasYearly));
            Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
                    + " infinite gas subscription.");
            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;
*/
            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
            Purchase prePurchase = inventory.getPurchase(SKU_PREMIUM);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                Log.d(TAG, "We have gas. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), new IabHelper.OnConsumeFinishedListener() {
                        @Override
                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                            Log.d("InApp Purchase","onConsumeFinished= "+result.getMessage());
                        }
                    });
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming gas. Another async operation in progress.");
                    Log.d("InApp Purchase","IabAsyncInProgressException");
                }
                return;
            }
            if (prePurchase != null && verifyDeveloperPayload(prePurchase)) {
                Log.d(TAG, "We have gas. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_PREMIUM), new IabHelper.OnConsumeFinishedListener() {
                        @Override
                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                            Log.d("InApp Purchase","onConsumeFinished= "+result.getMessage());
                        }
                    });
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming gas. Another async operation in progress.");
                    Log.d("InApp Purchase","IabAsyncInProgressException");
                }
                return;
            }
         //   updateUi();
         //   setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

  /*  public void btnBack_onClick(View view) {
        try {
            inapp.this.onBackPressed();
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnBack_onClick):" + ex.toString());
            Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }*/

    public void btnFree_onClick(View view) {
        try {
            AppConstant.REFRESH = true;
           // inapp.this.onBackPressed();
            finish();

            if (mBroadcastReceiver != null) {
                unregisterReceiver(mBroadcastReceiver);
            }

            // very important:
            Log.d(TAG, "Destroying helper.");
            if (mHelper != null) {
                mHelper.disposeWhenFinished();
                mHelper = null;
            }
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btnFree_onClick):" + ex.toString());
        }
    }

    // User clicked the "Buy Gas" button
    public void onBuyGasButtonClicked(View arg0) {
        Log.d(TAG, "Buy gas button clicked.");

        try {
            if (!AppConstant.IS_LOGIN && AppConstant.LOGIN_ID.equals("")) {
                AppConstant.REFRESH = true;
                Intent intent = new Intent(inapp.this, LoginActivity.class);
                startActivity(intent);
            } else {
                if (AppConstant.TOTAL_PUZZLES_AVAILABLE < 40) {
                    Utilities.showAlertDailog(inapp.this, "No More Puzzle!!!", AppConstant.TOTAL_PUZZLES_AVAILABLE + " Puzzles Available.\nPlease Select Other Options", "Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            }, true);
                } else{
                    String payload = "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ";

                try {
                   // mHelper.launchPurchaseFlow(this, SKU_GAS, RC_REQUEST,
                  //          mPurchaseFinishedListener, payload);

                    mHelper.launchPurchaseFlow(this, SKU_GAS, RC_REQUEST,
                                    mPurchaseFinishedListener, payload);

                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Service Not Available , Please Try Again Later..");
                    //  setWaitScreen(false);
                }
                   // Do_Transaction(new BigDecimal("0.99"), "40 Puzzles", REQUEST_CODE_PAYMENT_40);
            }}
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btn40_onClick):" + ex.toString());
        }

       /* if (mSubscribedToInfiniteGas) {
            complain("No need! You're subscribed to infinite gas. Isn't that awesome?");
            return;
        }

        if (mTank >= TANK_MAX) {
            complain("Your tank is full. Drive around a bit!");
            return;
        }*/

        // launch the gas purchase UI flow.
        // We will be notified of completion via mPurchaseFinishedListener
       // setWaitScreen(true);
        Log.d(TAG, "Launching purchase flow for gas.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */

    }

    // User clicked the "Upgrade to Premium" button.
    public void onUpgradeAppButtonClicked(View arg0) {
        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");
      //  setWaitScreen(true);
        try {
            if (!AppConstant.IS_LOGIN && AppConstant.LOGIN_ID.equals("")) {
                AppConstant.REFRESH = true;
                Intent intent = new Intent(inapp.this, LoginActivity.class);
                startActivity(intent);
            } else {
                if (AppConstant.TOTAL_PUZZLES_AVAILABLE < 100) {
                    Utilities.showAlertDailog(inapp.this, "No More Puzzle!!!", AppConstant.TOTAL_PUZZLES_AVAILABLE + " Puzzles Available.\nPlease Select Other Options", "Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            }, true);
                } else


                try {
                    String payload = "";
                    mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
                            mPurchaseFinishedListener, payload);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Service Not Available , Please Try Again Later..");
                    //setWaitScreen(false);
                }
            }
        } catch (Exception ex) {
            Log.e(AppConstant.TAG, "Error(btn100_onClick):" + ex.toString());
        }
        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */

    }
/*
    // "Subscribe to infinite gas" button clicked. Explain to user, then start purchase
    // flow for subscription.
    public void onInfiniteGasButtonClicked(View arg0) {
        if (!mHelper.subscriptionsSupported()) {
            complain("Subscriptions not supported on your device yet. Sorry!");
            return;
        }

        CharSequence[] options;
        if (!mSubscribedToInfiniteGas || !mAutoRenewEnabled) {
            // Both subscription options should be available
            options = new CharSequence[2];
            options[0] = getString(R.string.subscription_period_monthly);
            options[1] = getString(R.string.subscription_period_yearly);
            mFirstChoiceSku = SKU_INFINITE_GAS_MONTHLY;
            mSecondChoiceSku = SKU_INFINITE_GAS_YEARLY;
        } else {
            // This is the subscription upgrade/downgrade path, so only one option is valid
            options = new CharSequence[1];
            if (mInfiniteGasSku.equals(SKU_INFINITE_GAS_MONTHLY)) {
                // Give the option to upgrade to yearly
                options[0] = getString(R.string.subscription_period_yearly);
                mFirstChoiceSku = SKU_INFINITE_GAS_YEARLY;
            } else {
                // Give the option to downgrade to monthly
                options[0] = getString(R.string.subscription_period_monthly);
                mFirstChoiceSku = SKU_INFINITE_GAS_MONTHLY;
            }
            mSecondChoiceSku = "";
        }

        int titleResId;
        if (!mSubscribedToInfiniteGas) {
            titleResId = R.string.subscription_period_prompt;
        } else if (!mAutoRenewEnabled) {
            titleResId = R.string.subscription_resignup_prompt;
        } else {
            titleResId = R.string.subscription_update_prompt;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleResId)
                .setSingleChoiceItems(options, 0 *//* checkedItem *//*, this)
                .setPositiveButton(R.string.subscription_prompt_continue, this)
                .setNegativeButton(R.string.subscription_prompt_cancel, this);
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/

    @Override
    public void onClick(DialogInterface dialog, int id) {
        if (id == 0 /* First choice item */) {
            mSelectedSubscriptionPeriod = mFirstChoiceSku;
        } else if (id == 1 /* Second choice item */) {
            mSelectedSubscriptionPeriod = mSecondChoiceSku;
        } else if (id == DialogInterface.BUTTON_POSITIVE /* continue button */) {
            /* TODO: for security, generate your payload here for verification. See the comments on
             *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
             *        an empty string, but on a production app you should carefully generate
             *        this. */
            String payload = "";

            if (TextUtils.isEmpty(mSelectedSubscriptionPeriod)) {
                // The user has not changed from the default selection
                mSelectedSubscriptionPeriod = mFirstChoiceSku;
            }

            List<String> oldSkus = null;
            if (!TextUtils.isEmpty(mInfiniteGasSku)
                    && !mInfiniteGasSku.equals(mSelectedSubscriptionPeriod)) {
                // The user currently has a valid subscription, any purchase action is going to
                // replace that subscription
                oldSkus = new ArrayList<String>();
                oldSkus.add(mInfiniteGasSku);
            }

          //  setWaitScreen(true);
            Log.d(TAG, "Launching purchase flow for gas subscription.");
            try {
                mHelper.launchPurchaseFlow(this, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                        oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain("Service Not Available , Please Try Again Later..");
            //    setWaitScreen(false);
            }
            // Reset the dialog options
            mSelectedSubscriptionPeriod = "";
            mFirstChoiceSku = "";
            mSecondChoiceSku = "";
        } else if (id != DialogInterface.BUTTON_NEGATIVE) {
            // There are only four buttons, this should not happen
            Log.e(TAG, "Unknown button clicked in subscription dialog: " + id);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_REQUEST) {


            if (resultCode == RESULT_OK) {
                try {
                    int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                    String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                    String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                    Log.d("InAPPPrchase","dataSignature= "+dataSignature);
                    Log.d("InAPPPrchase","purchaseData"+purchaseData);
                    Log.d("InAPPPrchase", "responseCode"+ String.valueOf(responseCode));

                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
              //      Toast.makeText(getApplicationContext(),"sku"+sku,Toast.LENGTH_LONG).show();
                    System.out.println("op"+sku);

                    if(sku.equals("40puzzle"))
                    {
                     //   Toast.makeText(getApplicationContext(),"40 Puzzles called",Toast.LENGTH_LONG).show();
                        Utilities.setPurchasedInfo(AppConstant.LOGIN_ID + "|" +
                                AppConstant.LOGIN_NAME + "|" +
                                "Success|" +
                                "0.99|" +
                                "date" + "|" +
                                "40");
                        Utilities.showAlertDailog(inapp.this, "PunnyFuzzles", "PayPal Payment Successfully Done.", "Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        postPaymentData("Success", "0.99", "40", "date");
                                    }
                                }, false);

                    }
                    else
                    {
                    //    Toast.makeText(getApplicationContext(),"100 Puzzles called",Toast.LENGTH_LONG).show();
                        Utilities.setPurchasedInfo(AppConstant.LOGIN_ID + "|" +
                                AppConstant.LOGIN_NAME + "|" +
                                "Success|" +
                                "1.99|" +
                                "date" + "|" +
                                "100");
                        Utilities.showAlertDailog(inapp.this, "PunnyFuzzles", "PayPal Payment Successfully Done.", "Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        postPaymentData("Success", "1.99", "100", "date");
                                    }
                                }, false);

                    }
                  //  alert("You have bought the " + sku + ". Excellent choice, adventurer!");
                  //  Toast.makeText(getApplicationContext(),"40 puzzles done",Toast.LENGTH_LONG).show();
                ///    Toast.makeText(getApplicationContext(),"data"+AppConstant.LOGIN_NAME+AppConstant.LOGIN_ID,Toast.LENGTH_LONG).show();

                  /*  Utilities.setPurchasedInfo(AppConstant.LOGIN_ID + "|" +
                            AppConstant.LOGIN_NAME + "|" +
                            "Success|" +
                            "1.99|" +
                            "date" + "|" +
                            "100");
                    Utilities.showAlertDailog(inapp.this, "PunnyFuzzles", "PayPal Payment Successfully Done.", "Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    postPaymentData("Success", "1.99", "100", "date");
                                }
                            }, false);
*/

                }
                catch (JSONException e) {
                    alert("Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }*/

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
             //   setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
              //  setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_GAS)) {

                ///punny fuzzle
//.makeText(getApplicationContext(),"40 puzzles done",Toast.LENGTH_LONG).show();
                Utilities.setPurchasedInfo("m17" + "|" +
                        "name"+ "|" +
                        "Success|" +
                        "1.99|" +
                        "date" + "|" +
                        "100");
                Utilities.showAlertDailog(activity, "PunnyFuzzles", "PayPal Payment Successfully Done.", "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                postPaymentData("Success", "1.99", "100", "date");
                            }
                        }, false);

                ///
                // bought 1/4 tank of gas. So consume it.
               /* Log.d(TAG, "Purchase is gas. Starting gas consumption.");
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabAsyncInProgressException e) {
                    complain("Error consuming gas. Another async operation in progress.");
                 //   setWaitScreen(false);
                    return;
                }*/
            }
            else if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
            /*    Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                mIsPremium = true;*/


                    AppConstant.TOTAL_PUZZLES_AVAILABLE = AppConstant.TOTAL_PUZZLES_AVAILABLE - 100;
                    Utilities.setPurchasedInfo(AppConstant.LOGIN_ID + "|" +
                            AppConstant.LOGIN_NAME + "|" +
                            "Success|" +
                            "1.99|" +
                            "datetime" + "|" +
                            "100");
                    Utilities.showAlertDailog(inapp.this, "PunnyFuzzles", "PayPal Payment Successfully Done.", "Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    postPaymentData("Success", "1.99", "100", "demodatetime");
                                }
                            }, false);
              //  updateUi();
              //  setWaitScreen(false);
            }
           /* else if (purchase.getSku().equals(SKU_INFINITE_GAS_MONTHLY)
                    || purchase.getSku().equals(SKU_INFINITE_GAS_YEARLY)) {
                // bought the infinite gas subscription
                Log.d(TAG, "Infinite gas subscription purchased.");
                alert("Thank you for subscribing to infinite gas!");
                mSubscribedToInfiniteGas = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mInfiniteGasSku = purchase.getSku();
                mTank = TANK_MAX;
              //  updateUi();
               // setWaitScreen(false);
            }*/
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
              /*  Log.d(TAG, "Consumption successful. Provisioning.");
                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
                saveData();
                alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");*/
                AppConstant.TOTAL_PUZZLES_AVAILABLE = AppConstant.TOTAL_PUZZLES_AVAILABLE - 100;
             //   Toast.makeText(getApplicationContext(),"data"+AppConstant.LOGIN_NAME+AppConstant.LOGIN_ID,Toast.LENGTH_LONG).show();
                Utilities.setPurchasedInfo(AppConstant.LOGIN_ID + "|" +
                        AppConstant.LOGIN_NAME + "|" +
                        "Success|" +
                        "1.99|" +
                        "datetime" + "|" +
                        "100");
                Utilities.showAlertDailog(inapp.this, "PunnyFuzzles", "PayPal Payment Successfully Done.", "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                postPaymentData("Success", "1.99", "100", "demodatetime");
                            }
                        }, false);
            }
            else {
                complain("Error while consuming: " + result);
            }
           // updateUi();
           // setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };
/*
    // Drive button clicked. Burn gas!
    public void onDriveButtonClicked(View arg0) {
        Log.d(TAG, "Drive button clicked.");
        if (!mSubscribedToInfiniteGas && mTank <= 0) alert("Oh, no! You are out of gas! Try buying some!");
        else {
            if (!mSubscribedToInfiniteGas) --mTank;
            saveData();
            alert("Vroooom, you drove a few miles.");
           // updateUi();
            Log.d(TAG, "Vrooom. Tank is now " + mTank);
        }
    }*/

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            try {
                unregisterReceiver(mBroadcastReceiver);
            }
            catch(Exception e)
            {

            }
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    // updates UI to reflect model
/*    public void updateUi() {
        // update the car color to reflect premium status or lack thereof
        ((ImageView)findViewById(R.id.free_or_premium)).setImageResource(mIsPremium ? R.drawable.premium : R.drawable.free);

        // "Upgrade" button is only visible if the user is not premium
        findViewById(R.id.upgrade_button).setVisibility(mIsPremium ? View.GONE : View.VISIBLE);

        ImageView infiniteGasButton = (ImageView) findViewById(R.id.infinite_gas_button);
        if (mSubscribedToInfiniteGas) {
            // If subscription is active, show "Manage Infinite Gas"
            infiniteGasButton.setImageResource(R.drawable.manage_infinite_gas);
        } else {
            // The user does not have infinite gas, show "Get Infinite Gas"
            infiniteGasButton.setImageResource(R.drawable.get_infinite_gas);
        }

        // update gas gauge to reflect tank status
        if (mSubscribedToInfiniteGas) {
            ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(R.drawable.gas_inf);
        }
        else {
            int index = mTank >= TANK_RES_IDS.length ? TANK_RES_IDS.length - 1 : mTank;
            ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(TANK_RES_IDS[index]);
        }
    }*/

    // Enables or disables the "please wait" screen.
   /* void setWaitScreen(boolean set) {
        findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }*/
///////////////////punny fuzzle payment ////////////////////////////
    private void postPaymentData(final String strStatus, final String strOrderPrice, final String strPuzzlePurchased,
                                 final String currentDateTimeString) {
      //  Toast.makeText(getApplicationContext(),"inside post payment data".toString(),Toast.LENGTH_LONG).show();

        //  showProgressDialog1();
        String url;
        url = AppConstant.POST_PAYMENT_DATA;

        AppConstant.REFRESH = true;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response1) {
                        try {
                            JSONObject response = new JSONObject(response1);
                            JSONObject objresponse = response.getJSONObject("response");
                      //      Toast.makeText(getApplicationContext(),"response"+objresponse.toString(),Toast.LENGTH_LONG).show();
                            if (objresponse.getInt("flag") == 1) {
                                Utilities.setPurchasedInfo("");
///my code
                            /*    if (mHelper != null) {
                                    mHelper.disposeWhenFinished();
                                    mHelper = null;
                                }*/
                              /*  Utilities.showAlertDailog(inapp.this, "PunnyFuzzles", "Payment Successfully Done.", "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        }, true);*/
                            }
                        } catch (Exception e) {
                            Utilities.setNOsend(true);
                            e.printStackTrace();
                        }
                 //       hideProgressDialog1();
                    }


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            //    hideProgressDialog1();
            }
        })
        {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", AppConstant.LOGIN_ID);
                params.put("firstname", AppConstant.LOGIN_NAME);
                params.put("payment_status", strStatus);
                params.put("total_order_price", strOrderPrice);
                params.put("date_order_placed", currentDateTimeString);
                params.put("total_puzzle_purchase", strPuzzlePurchased);

                return params;
            }

        };

        // Adding request to request queue
        App.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    //////////////////////////////

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
        inapp.this.invalidateOptionsMenu();
        if (AppConstant.IS_MUSIC_ON == true) {
            continueMusic = false;
            MusicManager.start(this, MusicManager.MUSIC_MENU);
        }
    }

    @Override
    protected void onStop() {
        try {
            App.getInstance().getRequestQueue().cancelAll(TAG);
            hideProgressDialog3();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

   /* @Override
    protected void onDestroy() {
        try {
            App.getInstance().cancelPendingRequests(TAG);
            stopService(new Intent(this, PayPalService.class));
            hideProgressDialog3();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }*/


    private void showProgressDialog1() {
        pDialog1.show();
    }

    private void hideProgressDialog1() {
        if (pDialog1.isShowing())
            pDialog1.hide();
    }


    private void showProgressDialog3() {
        pDialog3.show();
    }

    private void hideProgressDialog3() {
        if (pDialog3.isShowing())
            pDialog3.hide();
    }

    private void postPuzzleDataLogout() {
        showProgressDialog3();
        String url;
        url = AppConstant.POST_PUZZLE_DATA + "?user_id=" + AppConstant.LOGIN_ID +
                "&section=0" +
                "&total_score=" + AppConstant.SCORE +
                "&played_puzzle=" + AppConstant.ATTEMPTED_QUE +
                "&total_puzzle=" + AppConstant.TOTAL_PUZZLES;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject objresponse = response.getJSONObject("response");
                            if (objresponse.getInt("flag") == 1) {
                                Utilities.setLoginSuccess(false, "", "", "");
                                inapp.this.invalidateOptionsMenu();
                                Utilities.setNOsend(false);
                                Utilities.showAlertDailog(inapp.this, "PunnyFuzzles", "Logout Successfully", "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }
                                        }, true);
                            }
                        } catch (Exception e) {
                            Utilities.setNOsend(true);
                            e.printStackTrace();
                        }
                        hideProgressDialog3();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog3();
            }
        });

        // Adding request to request queue
        App.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    ///////////////////////////////////////////////

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("" + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        finish();
                    }
                });
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }
   /* @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub
        switch(which){
            case DialogInterface.BUTTON_POSITIVE: // yes

                break;
            case DialogInterface.BUTTON_NEGATIVE: // no

                break;
            case DialogInterface.BUTTON_NEUTRAL: // neutral

                break;
            default:
                // nothing
                break;
        }
    }*/
    void saveData() {

        /*
         * WARNING: on a real application, we recommend you save data in a secure way to
         * prevent tampering. For simplicity in this sample, we simply store the data using a
         * SharedPreferences.
         */

        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
        spe.putInt("tank", mTank);
        spe.apply();
        Log.d(TAG, "Saved data: tank = " + String.valueOf(mTank));
    }

    void loadData() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        mTank = sp.getInt("tank", 2);
        Log.d(TAG, "Loaded data: tank = " + String.valueOf(mTank));
    }
}
