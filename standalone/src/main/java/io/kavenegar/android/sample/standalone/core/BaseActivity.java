package io.kavenegar.android.sample.standalone.core;

import android.content.Context;


import androidx.appcompat.app.AppCompatActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));

    }
}
