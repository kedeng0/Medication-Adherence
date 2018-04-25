package com.appzoro.BP_n_ME.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.fragment.SMAPFragment;

/**
 * Created by kedeng on 23/4/2018.
 */

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context, MainActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notify)
                .setContentTitle("Take Medicine Now")
                .setContentText("Time to take your medicine, refer to your schedule for which medicine to take now.")
                .setAutoCancel(true)
                .setSound(alarmSound);

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
//        boolean isScreeonOn = pm.isScreenOn();
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyLock");
        wl.acquire(10000);
//            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
//            wl_cpu.acquire(10000);
        wl.release();



        notificationManager.notify(100, builder.build());
    }
}