package com.appzoro.BP_n_ME.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Appzoro_ 5 on 9/6/2017.
 */

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent intent2 = new Intent(context,ReminderService.class);
            int type = intent2.getIntExtra("type", 0);
            intent2.putExtra("type", type);
            context.startService(intent2);
        }
    }
}
