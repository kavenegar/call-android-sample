package io.kavenegar.android.sample.standalone.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;
 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.core.BaseActivity;

public class WebViewActivity extends BaseActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate( Bundle savedInstanceState) {

        setContentView(R.layout.activity_webview);


        TextView toolbarTitle = findViewById(R.id.toolbar_title);

        toolbarTitle.setText(getIntent().getStringExtra("title"));


        ImageView closeButton = findViewById(R.id.close_button);

        closeButton.setOnClickListener(view -> {
            this.finish();
        });


        WebView webView = findViewById(R.id.webview);

        webView.loadUrl(getIntent().getStringExtra("url"));

        super.onCreate(savedInstanceState);

    }


}
