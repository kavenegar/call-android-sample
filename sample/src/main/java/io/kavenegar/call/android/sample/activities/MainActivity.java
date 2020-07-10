package io.kavenegar.call.android.sample.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.body.UrlEncodedFormBody;

import org.json.JSONObject;

import java.util.Arrays;

import io.kavenegar.call.android.sample.MyApplication;
import io.kavenegar.call.android.sample.R;


public class MainActivity extends Activity {

    static final String TAG = "MainActivity";

    Button dialButton;
    EditText dialUsernameText;

    private TextView toolbarUserNumberText;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);

            dialButton = findViewById(R.id.dial_button);
            dialUsernameText = findViewById(R.id.dial_username_text);


            dialButton.setOnClickListener(MainActivity.this::dialClick);

            checkGooglePlayServicesAvailable();



            toolbar = findViewById(R.id.tool_bar);

            toolbar.findViewById(R.id.signout_button).setOnClickListener(v -> {
                saveAppApiToken(null);
                this.finish();
            });

            toolbarUserNumberText = toolbar.findViewById(R.id.user_number_text);

            toolbarUserNumberText.setText(getMobileNumber());


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private boolean checkGooglePlayServicesAvailable() {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (status == ConnectionResult.SUCCESS) {
            return true;
        }

        if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
            final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            if (errorDialog != null) {
                errorDialog.show();
            }
        }

        return false;
    }


    // Backend Methods =====================================================================================

    public void saveAppApiToken(String apiToken) {
        SharedPreferences.Editor editor = getSharedPreferences("Avanegar", Context.MODE_PRIVATE).edit();
        editor.putString("AppApiToken", apiToken);
        editor.apply();
    }

    public String getAppApiToken() {
        SharedPreferences editor = getSharedPreferences("Avanegar", Context.MODE_PRIVATE);
        return editor.getString("AppApiToken", null);
    }

    public String getMobileNumber() {
        SharedPreferences editor = getSharedPreferences("Avanegar", Context.MODE_PRIVATE);
        return editor.getString("MobileNumber", "");

    }




    /* UI events ======================================================================================== */

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void dialClick(View view) {
        try {
            this.call(dialUsernameText.getText().toString());
        } catch (Exception ex) {
            Log.e(TAG, "Error", ex);
        }
    }

    public void call(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.isEmpty()) {
            Toast.makeText(this, "Receptor number is empty", Toast.LENGTH_LONG).show();
            return;
        }

        ProgressDialog loadingDialog = ProgressDialog.show(this, "", "Starting call ...");


        AsyncHttpPost request = new AsyncHttpPost(MyApplication.BACKEND_URL + "/calls");
        request.setHeader("Authorization", getAppApiToken());
        request.setBody(new UrlEncodedFormBody(Arrays.asList(
                new BasicNameValuePair("receptor", mobileNumber)
        )));
        AsyncHttpClient.getDefaultInstance().executeJSONObject(request, new AsyncHttpClient.JSONObjectCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, JSONObject result) {
                MainActivity.this.runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    try {
                        if (e != null) {
                            Log.e(TAG, "Error in Authorization result ", e);
                            Toast.makeText(MainActivity.this, "Call failed : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (source.code() != 200) {
                            Toast.makeText(MainActivity.this, "Call failed  : " + (result == null ? source.message() : result.toString()), Toast.LENGTH_LONG).show();
                            return;
                        }

                        MainActivity.this.startCall(result);
                    } catch (Exception e1) {
                        Log.e(TAG, "Error in Authorization result ", e1);
                    }
                });
            }
        });
    }

    public void startCall(JSONObject payload) {
        try {
            String accessToken = payload.getString("accessToken");
            String callId = payload.getString("callId");
            Intent intent = new Intent(this, CallActivity.class);
            intent.putExtra("call_id", callId);
            intent.putExtra("access_token", accessToken);
            this.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, "StartCall", ex);
        }
    }



}