package com.pricealert.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ScraperReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(ScraperReceiver.class.getSimpleName(), "In receiver, starting new scraper...");
        Uri uri = intent.getData();

        Intent newIntent = new Intent(context, ScraperReceiver.class);
        long scTime = 60*1000;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + scTime, pendingIntent);

        Intent serviceIntent = new Intent(context, ScraperService.class);
        serviceIntent.setData(uri);
        context.startService(serviceIntent);
    }
}
