package io.kavenegar.android.sample.standalone.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import io.kavenegar.android.sample.standalone.SettingsService;
import io.kavenegar.android.sample.standalone.MyApplication;
 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.core.BaseActivity;
import io.kavenegar.android.sample.standalone.models.Endpoint;

public class LoginActivity extends BaseActivity {

    Button loginButton;
    EditText usernameText;
    Endpoint user;
    View rootView;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rootView = findViewById(R.id.login_layout);

        this.usernameText = findViewById(R.id.mobile_number_text);
        this.loginButton = findViewById(R.id.login_button);
        this.toolbarTitle = findViewById(R.id.toolbar_title);

        toolbarTitle.setText(R.string.app_name);


        loginButton.setOnClickListener(v -> handleLogin());

        loginButton.setTypeface(null, Typeface.NORMAL);
    }


    void handleLogin() {


        String mobileNumber = usernameText.getText().toString();
        MyApplication.getApiClient().setAccessToken(mobileNumber);

        ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.connecting));
        MyApplication.getApiClient().getEndpoints(response -> {
            dialog.dismiss();
            SettingsService.authenticate(mobileNumber, getBaseContext());
            next();
        }, error -> {
            dialog.dismiss();
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        });
    }


    void next() {
        Intent intent = new Intent(this, ChooseEndpointActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }


}
