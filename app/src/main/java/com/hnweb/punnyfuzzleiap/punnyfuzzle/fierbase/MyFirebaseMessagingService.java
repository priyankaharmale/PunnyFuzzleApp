package com.hnweb.punnyfuzzleiap.punnyfuzzle.fierbase;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.hnweb.punnyfuzzleiap.punnyfuzzle.R;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.NotificationUpdateModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hnwebmarketing on 1/7/2017.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String CurrentString = remoteMessage.getData().toString();

        Log.d(TAG, "separated String a= : " + CurrentString);
        sendNotification(remoteMessage);
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(RemoteMessage remoteMessage) {

        Log.d(TAG, "messageBody: " + remoteMessage.getData().toString());
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationOREO(remoteMessage);
            } else {
                showNotification(remoteMessage);
            }
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotificationOREO(RemoteMessage remoteMessage) {

        try {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());

            Log.d("JsonResponse", jsonObject.toString());
            String type = jsonObject.getString("type");
            String message = jsonObject.getString("msg");
            Log.e("JsonResponse", type + " :: " + message);
            Intent resultIntent = new Intent();
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle("Punny Fuzzle")
                    .setContentText(message)
                    .setAutoCancel(false)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent);

            mNotificationManager = ( NotificationManager ) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert mNotificationManager != null;
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
            NotificationUpdateModel.getInstance().changeState(true);


        } catch (JSONException e) {
            Log.e("JSONException", e.toString());
            e.printStackTrace();
        }


    }

    public void showNotification(RemoteMessage remoteMessage) {

        try {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());

            Log.d("JsonResponse", jsonObject.toString());
            String type = jsonObject.getString("type");
            String message = jsonObject.getString("msg");
            Log.e("JsonResponse", type + " :: " + message);
            Intent intent = new Intent();
            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};

                @SuppressLint({"NewApi", "LocalSuppress"})
                Notification noti = new Notification.Builder(getApplicationContext())
                        .setContentTitle("Punny Fuzzle")
                        .setContentText(message)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent)
                        .setSound(alarmSound)
                        .setAutoCancel(false)
                        .setVibrate(pattern)
                        .build();
                NotificationManager notificationManager = ( NotificationManager ) getSystemService(NOTIFICATION_SERVICE);
                // hide the notification after its selected
                noti.flags |= Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(0, noti);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}