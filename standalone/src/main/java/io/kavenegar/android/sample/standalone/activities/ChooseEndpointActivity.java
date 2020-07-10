package io.kavenegar.android.sample.standalone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.kavenegar.android.sample.standalone.MyApplication;
 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.SettingsService;
import io.kavenegar.android.sample.standalone.adapters.EndpointsAdapter;
import io.kavenegar.android.sample.standalone.core.BaseActivity;
import io.kavenegar.android.sample.standalone.models.Endpoint;

public class ChooseEndpointActivity extends BaseActivity {


    Button saveButton;

    ListView endpointsList;

    EndpointsAdapter endpointsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("انتخاب مخاطب");

        saveButton = findViewById(R.id.save_button);

        endpointsList = findViewById(R.id.endpoints_list);
        this.endpointsAdapter = new EndpointsAdapter(this, new ArrayList<>(), this::next);

        endpointsList.setAdapter(endpointsAdapter);

        MyApplication.getApiClient().getEndpoints(response -> {
            endpointsAdapter.addAll(response);
            endpointsAdapter.notifyDataSetChanged();
        }, error -> {

        });
    }

    private void next(Endpoint endpoint) {
        SettingsService.setSelectedEndpoint(this, endpoint.getId());
        MyApplication.setUser(endpoint);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

}
