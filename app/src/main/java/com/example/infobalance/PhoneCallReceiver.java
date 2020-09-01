package com.example.infobalance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

public class PhoneCallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;

    private boolean isInitialized;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!isInitialized) {
            isInitialized = true;
            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            assert telephony != null;
            telephony.listen(new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    onCustomCallStateChanged(context, state, phoneNumber);
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Log.d("suru","incoming");
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d("suru","incoming");

    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d("suru","incoming");

    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d("suru","incoming");

    }

    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d("suru","incoming");

    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    private void onCustomCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, number, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, number, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, number, callStartTime, new Date());
                } else {
                    onOutgoingCallEnded(context, number, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }
}