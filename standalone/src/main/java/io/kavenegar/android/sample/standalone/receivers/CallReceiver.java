package io.kavenegar.android.sample.standalone.receivers;

import android.content.Context;
import android.util.Log;

import java.util.Date;

import io.kavenegar.android.sample.standalone.core.PhonecallReceiver;
import io.kavenegar.sdk.call.KavenegarCall;
import io.kavenegar.sdk.call.core.KavenegarException;
import io.kavenegar.sdk.call.enums.CallFinishedReason;

public class CallReceiver extends PhonecallReceiver {


    static final String TAG = "CallReceiver";

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Log.i(TAG,"onIncomingCallStarted");

        if (KavenegarCall.getInstance().getActiveCall() != null) {
            try {
                KavenegarCall.getInstance().getActiveCall().hangup(CallFinishedReason.CALL_RECEIVED);
            } catch (KavenegarException e) {
               Log.e(TAG,"onIncomingCallStarted",e);
            }
        }
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.i(TAG,"onOutgoingCallStarted");

        if (KavenegarCall.getInstance().getActiveCall() != null) {
            try {
                KavenegarCall.getInstance().getActiveCall().hangup(CallFinishedReason.CALL_RECEIVED);
            } catch (KavenegarException e) {
                Log.e(TAG,"onIncomingCallStarted",e);
            }
        }
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.i(TAG,"onIncomingCallEnded");

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.i(TAG,"onOutgoingCallEnded");

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {

    }

}