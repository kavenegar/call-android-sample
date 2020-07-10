package io.kavenegar.call.android.sample.fcm;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import io.kavenegar.call.android.sample.activities.CallActivity;
import io.kavenegar.sdk.call.Call;
import io.kavenegar.sdk.call.KavenegarCall;


public class MyFirebaseService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Push notification message data payload: " + remoteMessage.getData());
            if (remoteMessage.getData().get("action").equals("call")) {
                String payloadRaw = remoteMessage.getData().get("payload");
                try {
                    if (KavenegarCall.getInstance().getActiveCall() != null) {
                        Toast.makeText(this, "New call received but there is active call in queue  ", Toast.LENGTH_LONG).show();
                        Log.v(TAG, "New call received but there is active call in queue : " + payloadRaw);
                        return;
                    }
                    JSONObject payload = new JSONObject(payloadRaw);
                    String accessToken = payload.getString("accessToken");
                    String callId = payload.getString("callId");
                    Intent intent = new Intent(this, CallActivity.class);
                    intent.putExtra("call_id", callId);
                    intent.putExtra("access_token", accessToken);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(intent);
                } catch (Exception ex) {

                }
            }
        }
    }

    @Override
    public void onNewToken(String s) {
        Log.v(TAG, "onNewToken :" + s);
        super.onNewToken(s);
    }
}
