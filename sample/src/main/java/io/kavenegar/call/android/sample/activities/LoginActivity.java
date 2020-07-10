package io.kavenegar.call.android.sample.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.body.UrlEncodedFormBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.kavenegar.call.android.sample.MyApplication;
import io.kavenegar.call.android.sample.R;

public class LoginActivity extends Activity {

    static final String TAG = "LoginActivity";

    Button loginButton;

    EditText usernameText;
    EditText fullnameText;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button);
        usernameText = findViewById(R.id.username_text);
        fullnameText = findViewById(R.id.fullname_text);

        usernameText.setText(getMobileNumber());
        loginButton.setOnClickListener(this::authorizeClick);

        if (getAppApiToken() != null) {
            this.start();
        }

        super.onCreate(savedInstanceState);
    }


    public void authorizeClick(View view) {
        ProgressDialog loadingDialog = ProgressDialog.show(this, "", "Authorizing ...");


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, result -> {

            UrlEncodedFormBody formBody = new UrlEncodedFormBody(Arrays.asList(
                    new BasicNameValuePair("username", usernameText.getText().toString()),
                    new BasicNameValuePair("displayName", fullnameText.getText().toString()),
                    new BasicNameValuePair("platform", "android"),
                    new BasicNameValuePair("notificationToken", result.getToken()),
                    new BasicNameValuePair("deviceId", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID))
            ));

            AsyncHttpPost request = new AsyncHttpPost(MyApplication.BACKEND_URL + "/authorize");
            request.setBody(formBody);

            AsyncHttpClient.getDefaultInstance().executeJSONObject(request, new AsyncHttpClient.JSONObjectCallback() {
                @Override
                public void onCompleted(Exception e, AsyncHttpResponse source, JSONObject result) {
                    LoginActivity.this.runOnUiThread(() ->
                    {
                        loadingDialog.dismiss();
                        try {
                            if (e != null) {
                                Log.e(TAG, "Error in Authorization result ", e);
                                Toast.makeText(LoginActivity.this, "Login Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (source.code() != 200) {
                                Toast.makeText(LoginActivity.this, "Login Error : " + (result == null ? source.message() : result.toString()), Toast.LENGTH_LONG).show();
                                return;
                            }

                            LoginActivity.this.saveAppApiToken(result.getString("apiToken"));
                            LoginActivity.this.saveMobileNumber(usernameText.getText().toString());
                            LoginActivity.this.start();
                        } catch (JSONException e1) {
                            Log.e(TAG, "Error in Authorization result ", e1);
                        }
                    });
                }
            });
        });

    }

    public void saveAppApiToken(String apiToken) {
        SharedPreferences.Editor editor = getSharedPreferences("Avanegar", Context.MODE_PRIVATE).edit();
        editor.putString("AppApiToken", apiToken);
        editor.apply();
    }


    public String getMobileNumber() {
        SharedPreferences editor = getSharedPreferences("Avanegar", Context.MODE_PRIVATE);
        return editor.getString("MobileNumber", "");

    }

    public void saveMobileNumber(String number) {
        SharedPreferences.Editor editor = getSharedPreferences("Avanegar", Context.MODE_PRIVATE).edit();
        editor.putString("MobileNumber", number);
        editor.apply();
    }


    public String getAppApiToken() {
        SharedPreferences editor = getSharedPreferences("Avanegar", Context.MODE_PRIVATE);
        return editor.getString("AppApiToken", null);
    }

    public void start() {
        Intent intent = new Intent(this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
    }

}
