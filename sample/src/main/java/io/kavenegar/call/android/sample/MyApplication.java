package io.kavenegar.call.android.sample;

import android.app.Application;

import com.google.firebase.FirebaseApp;

import io.kavenegar.sdk.call.KavenegarCall;
import io.kavenegar.sdk.call.enums.Environment;


public class MyApplication extends Application {


    public static final String BACKEND_URL = "{YOUR_BACKEND_IP_HERE:PORT}";


    @Override
    public void onCreate() {
        KavenegarCall.initialize(this, Environment.PRODUCTION);
        FirebaseApp.initializeApp(getBaseContext());
        super.onCreate();
    }


}
