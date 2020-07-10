package io.kavenegar.android.sample.standalone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.SettingsService;
import io.kavenegar.android.sample.standalone.MyApplication;

import io.kavenegar.android.sample.standalone.core.BaseActivity;

public class IntroActivity extends BaseActivity {

    ProgressBar progressBar;
    Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("exit", false)) {
            finish();
        }


        setContentView(R.layout.activity_intro);

        retryButton = findViewById(R.id.retry_button);
        progressBar = findViewById(R.id.loading_progress);

        retryButton.setOnClickListener(v -> {
            hello();
        });

        this.check();
    }


    void check() {
        if (SettingsService.isAuthenticated(getBaseContext())) {
            this.hello();
        } else {
            this.showLogin();
        }
    }

    void hello() {
        progressBar.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.GONE);


        if (SettingsService.getSelectedEndpoint(this) == 0) {
            showSelectEndpoint();
            return;
        }

        Integer endpointId = SettingsService.getSelectedEndpoint(this);


        MyApplication.getApiClient().getEndpoint(endpointId, response -> {
            MyApplication.setUser(response);
            showMain();
        }, error -> {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });


    }


    void showMain() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        finish();
    }

    void showLogin() {
        this.startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    void showSelectEndpoint() {
        this.startActivity(new Intent(this, ChooseEndpointActivity.class));
        finish();
    }

}
