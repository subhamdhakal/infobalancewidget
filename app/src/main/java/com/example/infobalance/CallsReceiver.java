package com.example.infobalance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.telephony.TelephonyManager;


import java.util.Date;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.TELEPHONY_SERVICE;

public class CallsReceiver extends PhoneCallReceiver {


    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Log.d(this.getClass().getName(), "onIncomingCallStarted number = " + number);
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d(this.getClass().getName(), "onOutgoingCallStarted number = " + number);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(this.getClass().getName(), "onIncomingCallEnded number = " + number);
    }

    @Override
    protected void onOutgoingCallEnded(final Context ctx, String number, Date start, Date end) {
        Log.d(this.getClass().getName(), "onOutgoingCallEnded number = " + number);
        Log.d(this.getClass().getName(), "ho aba ussd code run garne");
        runUssdCode(ctx);


    }

    @SuppressLint("MissingPermission")
    public void runUssdCode(final Context ctx) {

        Log.d(this.getClass().getName(), "code run garne");



        TelephonyManager manager = (TelephonyManager) ctx.getSystemService(TELEPHONY_SERVICE);
            Log.d("networkprovider",manager.getNetworkOperatorName());
            manager.sendUssdRequest("*400#", new TelephonyManager.UssdResponseCallback() {
                @Override
                public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                    super.onReceiveUssdResponse(telephonyManager, request, response);
                    Log.d(this.getClass().getName(), "response" + response.toString());
                    String string = response.toString();
                    String[] parts = string.split("E");
                    String part1 = parts[0]; // 004
                    String part2 = parts[1]; // 034556

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ctx);
                    RemoteViews remoteViews = new RemoteViews(ctx.getPackageName(), R.layout.info_balance_widger);
                    ComponentName thisWidget = new ComponentName(ctx, InfoBalanceWidger.class);
                    remoteViews.setTextViewText(R.id.appwidget_text, part1);
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);

                }

                @Override
                public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                    Log.d(this.getClass().getName(), "response" + failureCode);


                }
            }, new Handler());





    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d(this.getClass().getName(), "onMissedCall number = " + number);
    }

}