package io.kavenegar.android.sample.standalone.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.kavenegar.android.sample.standalone.MyApplication;

 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.core.BaseActivity;

public class ContactActivity extends BaseActivity {
    Button saveButton;
    EditText messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.contact_us);

        saveButton = findViewById(R.id.save_button);

        this.messageText = findViewById(R.id.message_text);

        findViewById(R.id.close_button).setOnClickListener(v -> this.finish());

        saveButton.setOnClickListener(this::handleSave);
        saveButton.setTypeface(null, Typeface.NORMAL);

        messageText.requestFocus();
    }

    void handleSave(View view) {
        String message = messageText.getText().toString();
        if(message.isEmpty()){
            Toast.makeText(this,R.string.ticket_message_is_empty, Toast.LENGTH_LONG).show();
            return;
        }
    }

}
