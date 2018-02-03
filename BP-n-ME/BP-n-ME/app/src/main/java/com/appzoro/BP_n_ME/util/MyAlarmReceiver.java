package com.appzoro.BP_n_ME.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Appzoro_ 5 on 8/1/2017.
 */

public class MyAlarmReceiver extends BroadcastReceiver {
    String printedMeds;
    MedasolPrefs prefs;
    private DatabaseReference mDatabase;
    String UID;
    String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    String time = util.getTime();
    ArrayList<String> mList = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(MainActivity.notification_lafda==1) {
            int typeOfNotification = intent.getIntExtra("type", 0);
            String message = intent.getStringExtra("message");
            Log.d("Alarm Recieved!", Integer.toString(typeOfNotification));
            //Intent i = new Intent(context, ReminderService.class);
            //i.putExtra("type", type);
            //context.startService(i);

            prefs = new MedasolPrefs(context.getApplicationContext());
            mDatabase = FirebaseDatabase.getInstance().getReference();
            UID = prefs.getUID();
            Log.e("time", time);

            //Set device default notification sound
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            //if(MainActivity.notification_lafda==1) {
            if (typeOfNotification==1|typeOfNotification==2|typeOfNotification==3) {
                printedMeds = message;
                if (printedMeds.equals("")){
                    //alarmManager.cancel(pendingIntent);
                } else {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setAutoCancel(true)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon))
                            .setSmallIcon(R.drawable.amc_notification)
                            .setSound(alarmSound)
                            .setContentTitle("BP-n-ME")
                            .setContentText("Please take your medication(s): " + printedMeds);

                    mDatabase.child("app").child("users").child(UID).child("Notification").child(todayDate).child(time+"_1")
                            .setValue("Please take your medication(s): " + printedMeds);

                    Intent resultIntent = new Intent(context, MainActivity.class);
                    resultIntent.putExtra("notification", "1");

                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    context,
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);
                    int mNotificationId = typeOfNotification+4;

                    Notification notification = mBuilder.build();

                    // Inflate and set the layout for the expanded notification view
                    RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.notification_custom_layout);
                    expandedView.setTextViewText(R.id.noti_text, "Please take your medication(s): " + printedMeds);
                    notification.bigContentView = expandedView;

                    NotificationManager mNotifyMgr =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(mNotificationId, notification);
                }
            }
            context.stopService(intent);

            if (typeOfNotification == 7){
                mList =  new ArrayList<>(Arrays.asList(MedasolPrefs.getInstance(context.getApplicationContext())
                        .getMedsLog().replace("[","").replace("]","").replace(" ","").split(",")));
                Log.e("mList Size", ">>" + mList.size());
                if (!mList.get(0).equals("")){
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setAutoCancel(true)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon))
                            .setSmallIcon(R.drawable.amc_notification)
                            .setSound(alarmSound)
                            .setContentTitle("BP-n-ME")
                            .setContentText("Today's taken medication(s): " + mList);

                    mDatabase.child("app").child("users").child(UID).child("Notification").child(todayDate).child(time+"_2")
                            .setValue("Today's taken medication(s): " + mList);

                    Intent resultIntent = new Intent(context, MainActivity.class);
                    resultIntent.putExtra("notification", "1");

                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    context,
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);
                    int mNotificationId = typeOfNotification+4;

                    Notification notification = mBuilder.build();

                    // Inflate and set the layout for the expanded notification view
                    RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.notification_custom_layout);
                    expandedView.setTextViewText(R.id.noti_text, "Today's taken medication(s): " + mList);
                    notification.bigContentView = expandedView;

                    NotificationManager mNotifyMgr =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(mNotificationId, notification);
                } else {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setAutoCancel(true)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon))
                            .setSmallIcon(R.drawable.amc_notification)
                            .setSound(alarmSound)
                            .setContentTitle("BP-n-ME")
                            .setContentText("Please take your medication(s) precisely & follow your doctor's instructions or call your pharmacist for what to do if you miss a dose");

                    mDatabase.child("app").child("users").child(UID).child("Notification").child(todayDate).child(time+"_2")
                            .setValue("Please take your medication(s) precisely & follow your doctor's instructions or call your pharmacist for what to do if you miss a dose");

                    Intent resultIntent = new Intent(context, MainActivity.class);
                    resultIntent.putExtra("notification", "1");

                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    context,
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);
                    int mNotificationId = typeOfNotification+4;

                    Notification notification = mBuilder.build();

                    // Inflate and set the layout for the expanded notification view
                    RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.notification_custom_layout);
                    expandedView.setTextViewText(R.id.noti_text, "Please take your medication(s) precisely & follow your doctor's instructions or call your pharmacist for what to do if you miss a dose.");
                    notification.bigContentView = expandedView;

                    NotificationManager mNotifyMgr =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(mNotificationId, notification);
                }
            }
            context.stopService(intent);

            if (typeOfNotification == 8){
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon))
                        .setSmallIcon(R.drawable.amc_notification)
                        .setSound(alarmSound)
                        .setContentTitle("BP-n-ME")
                        .setContentText("Have you been to your doctor's appointment to follow-up on your heart failure or COPD since being discharged from the hospital?");

                mDatabase.child("app").child("users").child(UID).child("Notification").child(todayDate).child(time+"_3")
                        .setValue("Have you been to your doctor's appointment to follow-up on your heart failure or COPD since being discharged from the hospital?");

                Intent resultIntent = new Intent(context, MainActivity.class);
                resultIntent.putExtra("notification", "1");

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);
                int mNotificationId = typeOfNotification+4;

                Notification notification = mBuilder.build();

                // Inflate and set the layout for the expanded notification view
                RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.notification_custom_layout);
                expandedView.setTextViewText(R.id.noti_text, "Have you been to your doctor's appointment to follow-up on your heart failure or COPD since being discharged from the hospital?");
                notification.bigContentView = expandedView;

                NotificationManager mNotifyMgr =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, notification);
            }
            context.stopService(intent);

            if (typeOfNotification == 9){
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon))
                        .setSmallIcon(R.drawable.amc_notification)
                        .setSound(alarmSound)
                        .setContentTitle("BP-n-ME")
                        .setContentText("Thank you for participating in this study. You may continue to use the APP for free to thank you for participating. Please complete the mobile APP satisfaction survey and the drug-specific medication adherence surveys. The information that you input into the application from now on will not be monitored by your pharmacist. We wish you the very best of health and wellness!");

                mDatabase.child("app").child("users").child(UID).child("Notification").child(todayDate).child(time+"_4")
                        .setValue("Thank you for participating in this study. You may continue to use the APP for free to thank you for participating. Please complete the mobile APP satisfaction survey and the drug-specific medication adherence surveys. The information that you input into the application from now on will not be monitored by your pharmacist. We wish you the very best of health and wellness!");

                Intent resultIntent = new Intent(context, MainActivity.class);
                resultIntent.putExtra("notification", "1");

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);
                int mNotificationId = typeOfNotification+4;

                Notification notification = mBuilder.build();

                // Inflate and set the layout for the expanded notification view
                RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.notification_custom_layout);
                expandedView.setTextViewText(R.id.noti_text, "Thank you for participating in this study. You may continue to use the APP for free to thank you for participating. Please complete the mobile APP satisfaction survey and the drug-specific medication adherence surveys. The information that you input into the application from now on will not be monitored by your pharmacist. We wish you the very best of health and wellness!");
                notification.bigContentView = expandedView;

                NotificationManager mNotifyMgr =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, notification);
            }
            context.stopService(intent);

        }
    }
}
