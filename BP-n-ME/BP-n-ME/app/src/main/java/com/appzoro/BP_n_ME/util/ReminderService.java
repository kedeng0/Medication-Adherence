package com.appzoro.BP_n_ME.util;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Appzoro_ 5 on 8/1/2017.
 */

public class ReminderService extends IntentService {
    public static final String MED_FILENAME = "med_file";
    public static final String FREQ_FILENAME = "freq_file";
    public static final String PREFS_UID = "MyPrefsFile";
    ArrayList<String> medicationList = new ArrayList<>();
    ArrayList<String> freqList = new ArrayList<>();
    Context ctx;
    String USER_FILENAME = "user_file";
    String printedMeds;
    String tenAmMeds="";
    String twoPmMeds="";
    String eightPmMeds="";
    ArrayList<String> mList = new ArrayList<>();
    String dischargeDate = "";
    MedasolPrefs prefs;
    private DatabaseReference mDatabase;
    String UID;
    String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    String time = util.getTime();

    public ReminderService() {
        super("ReminderService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */


    //@Override
    protected void onHandleIntent(Intent intent) {
        prefs = new MedasolPrefs(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        UID = prefs.getUID();

        try {
            FileInputStream fin = openFileInput(MED_FILENAME);
            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            //Log.e("Login Attempt", temp);
            if (temp.length()>1){
                //Log.e("temp",temp);
                while(temp.indexOf("\n")!=-1){
                    medicationList.add(temp.substring(0,temp.indexOf("\n")));
                    temp = temp.substring(temp.indexOf("\n")+1,temp.length());
                }
                medicationList.add(temp.substring(0,temp.length()));
                //Log.e("index",Integer.toString(temp.indexOf("\n")));
                //Log.e("medList",medicationList.toString());
            }
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileInputStream finfreq = openFileInput(FREQ_FILENAME);
            int c;
            String temp2="";
            while( (c = finfreq.read()) != -1){
                temp2 = temp2 + Character.toString((char)c);
            }
            //Log.e("Login Attempt", temp);
            if (temp2.length()>1){
                //Log.e("temp2",temp2);
                while(temp2.indexOf("\n")!=-1){
                    freqList.add(temp2.substring(0,temp2.indexOf("\n")));
                    temp2 = temp2.substring(temp2.indexOf("\n")+1,temp2.length());
                }
                freqList.add(temp2.substring(0,temp2.length()));
                //Log.e("index",Integer.toString(temp2.indexOf("\n")));
                //Log.e("freqList",freqList.toString());
            }
            finfreq.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("medArrayFinal",medicationList.toString());
        Log.e("medFreqFinal",freqList.toString());

        //first notification at 10 AM next day
        Calendar cur_cal = new GregorianCalendar();
        cur_cal.setTimeInMillis(System.currentTimeMillis());  //set the current time and date for this calendar

        for (int i=0;i<freqList.size();i++){
            if(freqList.get(i).equals("Daily")){
                if(tenAmMeds=="") {
                    tenAmMeds = tenAmMeds.concat(medicationList.get(i));
                }
                else{
                    tenAmMeds = tenAmMeds.concat(", ").concat(medicationList.get(i));
                }
            }
            else if(freqList.get(i).equals("Twice daily")){
                if(tenAmMeds=="") {
                    tenAmMeds = tenAmMeds.concat(medicationList.get(i));
                }
                else{
                    tenAmMeds = tenAmMeds.concat(", ").concat(medicationList.get(i));
                }
                if(eightPmMeds=="") {
                    eightPmMeds = eightPmMeds.concat(medicationList.get(i));
                }
                else{
                    eightPmMeds = eightPmMeds.concat(", ").concat(medicationList.get(i));
                }
            }
            else{
                if(tenAmMeds=="") {
                    tenAmMeds = tenAmMeds.concat(medicationList.get(i));
                }
                else{
                    tenAmMeds = tenAmMeds.concat(", ").concat(medicationList.get(i));
                }
                if(eightPmMeds=="") {
                    eightPmMeds = eightPmMeds.concat(medicationList.get(i));
                }
                else{
                    eightPmMeds = eightPmMeds.concat(", ").concat(medicationList.get(i));
                }
                if(twoPmMeds=="") {
                    twoPmMeds = twoPmMeds.concat(medicationList.get(i));
                }
                else{
                    twoPmMeds = twoPmMeds.concat(", ").concat(medicationList.get(i));
                }
            }
        }

        if(MainActivity.notification_lafda==1) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(System.currentTimeMillis());

            Intent alarmIntent = new Intent(this, MyAlarmReceiver.class);
            alarmIntent.putExtra("type", 1);
            alarmIntent.putExtra("message",tenAmMeds);
            final int _id = (int) cal.getTimeInMillis();
            //Log.e("Alarm10","Set");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1691882881, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

            cal.set(Calendar.HOUR_OF_DAY, 10); //10:00 AM
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

            //Set repeating every 15 minutes
            //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),1000*60*10, pendingIntent);


            if(twoPmMeds!="") {
                //second notification at 2 PM next day
                cal.set(Calendar.HOUR_OF_DAY, 14); //02:00 PM
                cal.set(Calendar.MINUTE, 0);
                alarmIntent = new Intent(this, MyAlarmReceiver.class);
                alarmIntent.putExtra("type", 2);
                alarmIntent.putExtra("message",twoPmMeds);
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                final int _id1 = (int) cal.getTimeInMillis();
                pendingIntent = PendingIntent.getBroadcast(this, 2, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                //Set repeating every 10 minutes
                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),1000*60*10, pendingIntent);
                //Log.e("Alarm2","Set");
            }
            if (eightPmMeds!="") {
                //third notification at 8 PM next day
                cal.set(Calendar.HOUR_OF_DAY, 20); //08:00 PM
                cal.set(Calendar.MINUTE, 0);
                alarmIntent = new Intent(this, MyAlarmReceiver.class);
                alarmIntent.putExtra("type", 3);
                alarmIntent.putExtra("message",eightPmMeds);
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                final int _id2 = (int) cal.getTimeInMillis();
                pendingIntent = PendingIntent.getBroadcast(this, 3, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                //Set repeating every 10 minutes
                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),1000*60*10, pendingIntent3);
                //Log.e("Alarm8","Set");
            }

            // notification at 8:15PM every night describing How Many doses were taken or Not

            //*************** 8:15 pm **************************//

            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(System.currentTimeMillis());

            dischargeDate = MedasolPrefs.getInstance(getApplicationContext()).getDischargeDate();
            Log.e("dischargeDate", "Date" + dischargeDate);


            cal1.set(Calendar.HOUR_OF_DAY, 20); // 08:15 PM
            cal1.set(Calendar.MINUTE, 15);
            cal1.set(Calendar.SECOND, 0);
            alarmIntent = new Intent(this, MyAlarmReceiver.class);
            alarmIntent.putExtra("type", 7);
            alarmIntent.putExtra("message", "mList");
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            final int _id3 = (int) cal1.getTimeInMillis();
            pendingIntent = PendingIntent.getBroadcast(this, 4, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(), pendingIntent);


            //*************** After 7 days  **************************//

            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(System.currentTimeMillis());

            SimpleDateFormat curFormater = new SimpleDateFormat("MM/dd/yyyy");
            Date dateObj = null;
            try {
                dateObj = curFormater.parse(dischargeDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cal2.setTime(dateObj);
            cal2.add(Calendar.DATE, 7);              //10:00 AM
            cal2.set(Calendar.HOUR_OF_DAY, 10);
            cal2.set(Calendar.MINUTE, 0);
            alarmIntent = new Intent(this, MyAlarmReceiver.class);
            alarmIntent.putExtra("type", 8);
            alarmIntent.putExtra("message", "ask for appointment!");
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            final int _id4 = (int) cal2.getTimeInMillis();
            pendingIntent = PendingIntent.getBroadcast(this, 5, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), pendingIntent);


            //*************** After 28 days  **************************//
            Calendar cal3 = Calendar.getInstance();
            cal3.setTimeInMillis(System.currentTimeMillis());

            SimpleDateFormat curFormater1 = new SimpleDateFormat("MM/dd/yyyy");
            Date dateObj1 = null;
            try {
                dateObj1 = curFormater1.parse(dischargeDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cal3.setTime(dateObj1);
            cal3.add(Calendar.DATE, 28);           //10:00 AM
            cal3.set(Calendar.HOUR_OF_DAY, 10);
            cal3.set(Calendar.MINUTE, 0);
            alarmIntent = new Intent(this, MyAlarmReceiver.class);
            alarmIntent.putExtra("type", 9);
            alarmIntent.putExtra("message", "say thankx!");
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            final int _id5 = (int) cal3.getTimeInMillis();
            pendingIntent = PendingIntent.getBroadcast(this, 6, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal3.getTimeInMillis(), pendingIntent);




           /*int typeOfNotification =  intent.getIntExtra("type", 0);
            //Log.e("Notification type ", String.valueOf(typeOfNotification));
            SharedPreferences settings = getSharedPreferences(PREFS_UID, 0);
            //there should be a second settings for the medications list
            String UIDstored = settings.getString("UID", "Default");
            //Log. d("UID", UIDstored);
            if(typeOfNotification==1){
                //Log.e("Print","10AM");
                printedMeds = tenAmMeds;
            }
            else if(typeOfNotification==2){
                //Log.e("Print","2PM");
                printedMeds = twoPmMeds;
            }
            else if (typeOfNotification==3){
                //Log.e("Print","8PM");
                printedMeds = eightPmMeds;
            }
            else if(typeOfNotification==4){
                //Log.e("canceled","alarm4");
                alarmManager.cancel(pendingIntent);
            }
            else if(typeOfNotification==5){
                //Log.e("canceled","alarm5");
                alarmManager.cancel(pendingIntent);
            }
            else if(typeOfNotification==6){
                //Log.e("canceled","alarm6");
                alarmManager.cancel(pendingIntent);
            }
            else if (typeOfNotification == 7){
                Log.e("Print","8:15PM");
                mList =  new ArrayList<>(Arrays.asList(MedasolPrefs.getInstance(getApplicationContext())
                        .getMedsLog().replace("[","").replace("]","").replace(" ","").split(",")));
                Log.e("mList Size", ">>" + mList.size());
            }
            else if (typeOfNotification == 8){
                Log.e("Print","7 days");
            }
            else if (typeOfNotification == 9){
                Log.e("Print","28 days");
            }

            //Set device default notification sound
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //if(MainActivity.notification_lafda==1) {
            if (typeOfNotification==1|typeOfNotification==2|typeOfNotification==3) {
                if (printedMeds.equals("")){
                    alarmManager.cancel(pendingIntent);
                } else {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setAutoCancel(true)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon))
                            .setSmallIcon(R.drawable.amc_notification)
                            .setSound(alarmSound)
                            .setContentTitle("AMC-n-ME")
                            .setContentText("Please take your medication(s): " + printedMeds);

                    mDatabase.child("app").child("users").child(UID).child("Notification").child(todayDate).child(time+"_1")
                            .setValue("Please take your medication(s): " + printedMeds);

                    Intent resultIntent = new Intent(this, MainActivity.class);
                    resultIntent.putExtra("notification", "1");

                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    this,
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);
                    int mNotificationId = typeOfNotification+4;

                    Notification notification = mBuilder.build();

                    // Inflate and set the layout for the expanded notification view
                    RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_custom_layout);
                    expandedView.setTextViewText(R.id.noti_text, "Please take your medication(s): " + printedMeds);
                    notification.bigContentView = expandedView;

                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(mNotificationId, notification);
                }
            }
            stopService(intent);

            if (typeOfNotification == 7){
                if (!mList.get(0).equals("")){
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setAutoCancel(true)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon))
                            .setSmallIcon(R.drawable.amc_notification)
                            .setSound(alarmSound)
                            .setContentTitle("AMC-n-ME")
                            .setContentText("Today's taken medication(s): " + mList);

                    mDatabase.child("app").child("users").child(UID).child("Notification").child(todayDate).child(time+"_2")
                            .setValue("Today's taken medication(s): " + mList);

                    Intent resultIntent = new Intent(this, MainActivity.class);
                    resultIntent.putExtra("notification", "1");

                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    this,
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);
                    int mNotificationId = typeOfNotification+4;

                    Notification notification = mBuilder.build();

                    // Inflate and set the layout for the expanded notification view
                    RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_custom_layout);
                    expandedView.setTextViewText(R.id.noti_text, "Today's taken medication(s): " + mList);
                    notification.bigContentView = expandedView;

                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(mNotificationId, notification);
                } else {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setAutoCancel(true)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon))
                            .setSmallIcon(R.drawable.amc_notification)
                            .setSound(alarmSound)
                            .setContentTitle("AMC-n-ME")
                            .setContentText("Please take your medication(s) precisely & follow your doctor's instructions or call your pharmacist for what to do if you miss a dose");

                    mDatabase.child("app").child("users").child(UID).child("Notification").child(todayDate).child(time+"_2")
                            .setValue("Please take your medication(s) precisely & follow your doctor's instructions or call your pharmacist for what to do if you miss a dose");

                    Intent resultIntent = new Intent(this, MainActivity.class);
                    resultIntent.putExtra("notification", "1");

                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    this,
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);
                    int mNotificationId = typeOfNotification+4;

                    Notification notification = mBuilder.build();

                    // Inflate and set the layout for the expanded notification view
                    RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_custom_layout);
                    expandedView.setTextViewText(R.id.noti_text, "Please take your medication(s) precisely & follow your doctor's instructions or call your pharmacist for what to do if you miss a dose.");
                    notification.bigContentView = expandedView;

                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(mNotificationId, notification);
                }
            }
            stopService(intent);

            if (typeOfNotification == 8){
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon))
                        .setSmallIcon(R.drawable.amc_notification)
                        //.setSound(alarmSound)
                        .setContentTitle("AMC-n-ME")
                        .setContentText("Have you been to your doctor's appointment to follow-up on your heart failure or COPD since being discharged from the hospital?");

                mDatabase.child("app").child("users").child(UID).child("Notification").child(todayDate).child(time+"_3")
                        .setValue("Have you been to your doctor's appointment to follow-up on your heart failure or COPD since being discharged from the hospital?");

                Intent resultIntent = new Intent(this, MainActivity.class);
                resultIntent.putExtra("notification", "1");

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);
                int mNotificationId = typeOfNotification+4;

                Notification notification = mBuilder.build();

                // Inflate and set the layout for the expanded notification view
                RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_custom_layout);
                expandedView.setTextViewText(R.id.noti_text, "Have you been to your doctor's appointment to follow-up on your heart failure or COPD since being discharged from the hospital?");
                notification.bigContentView = expandedView;

                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, notification);
            }
            stopService(intent);

            if (typeOfNotification == 9){
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon))
                        .setSmallIcon(R.drawable.amc_notification)
                        //.setSound(alarmSound)
                        .setContentTitle("AMC-n-ME")
                        .setContentText("Thanking you for participating in the study. Have you complete the mobile app satisfaction and medication adherence surveys.");

                mDatabase.child("app").child("users").child(UID).child("Notification").child(todayDate).child(time+"_4")
                        .setValue("Thanking you for participating in the study. Have you complete the mobile app satisfaction and medication adherence surveys.");

                Intent resultIntent = new Intent(this, MainActivity.class);
                resultIntent.putExtra("notification", "1");

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);
                int mNotificationId = typeOfNotification+4;

                Notification notification = mBuilder.build();

                // Inflate and set the layout for the expanded notification view
                RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_custom_layout);
                expandedView.setTextViewText(R.id.noti_text, "Thanking you for participating in the study. Have you complete the mobile app satisfaction and medication adherence surveys.");
                notification.bigContentView = expandedView;

                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, notification);
            }
            stopService(intent);*/
        }
    }
}
