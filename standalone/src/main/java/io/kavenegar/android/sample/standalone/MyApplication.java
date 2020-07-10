package io.kavenegar.android.sample.standalone;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;


import com.jakewharton.threetenabp.AndroidThreeTen;


import java.lang.ref.WeakReference;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.kavenegar.android.sample.standalone.activities.CallActivity;
import io.kavenegar.android.sample.standalone.models.CallSession;
import io.kavenegar.android.sample.standalone.models.Endpoint;
import io.kavenegar.android.sample.standalone.network.AvaApiClient;
import io.kavenegar.sdk.call.KavenegarCall;
import io.kavenegar.sdk.call.enums.Environment;

public class MyApplication extends Application {

    public static final int CALL_ACTIVITY_REQUEST_CODE = 103;

    static WeakReference<Context> context;

    static AvaApiClient apiClient;

    static Endpoint user;

   //  public static final String backendURL = "http://192.168.1.10:8081/voice/v1";
    public static final String backendURL = "https://api.kavenegar.io/voice/v1";

    //public static final String backendURL = "http://dev.kavenegar.io:9090";

    public static String currentCallId;

    public static void setCurrentCallId(String currentCallId) {
        MyApplication.currentCallId = currentCallId;
    }

    public static String getCurrentCallId() {
        return currentCallId;
    }

    public static Endpoint getUser() {
        return user;
    }

    public static void setUser(Endpoint user) {
        MyApplication.user = user;
    }

    public static AvaApiClient getApiClient() {
        return apiClient;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        context = new WeakReference<>(getApplicationContext());
        apiClient = new AvaApiClient(backendURL, getApplicationContext());

        if (SettingsService.isAuthenticated(getApplicationContext())) {
            apiClient.setAccessToken(SettingsService.getAccessToken(getApplicationContext()));
        }

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Shabnam.ttf")
                                .build()))
                .build());

        KavenegarCall.initialize(getApplicationContext(), Environment.PRODUCTION);
    }

    public static void startCall(Activity context, String callId, String accessToken, CallSession call) {
        try {
            Intent intent = new Intent(context, CallActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("call_id", callId);
            intent.putExtra("access_token", accessToken);
            intent.putExtra("call",call);
            context.startActivityForResult(intent, CALL_ACTIVITY_REQUEST_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
