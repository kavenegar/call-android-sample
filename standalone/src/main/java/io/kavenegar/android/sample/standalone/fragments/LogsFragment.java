package io.kavenegar.android.sample.standalone.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import io.kavenegar.sdk.call.enums.CallDirection;
import io.kavenegar.android.sample.standalone.MyApplication;

 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.activities.MainActivity;
import io.kavenegar.android.sample.standalone.adapters.LogsAdapter;
import io.kavenegar.android.sample.standalone.models.Call;
import io.kavenegar.android.sample.standalone.models.Endpoint;

public class LogsFragment extends Fragment {


    LogsAdapter logsAdapter;

    public LogsFragment() {
    }

    boolean isInRemoteMode = false;

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logs, null);

        try {
            View emptyView = rootView.findViewById(R.id.logs_empty);
            ListView logsList = rootView.findViewById(R.id.logs_list);


            MyApplication.getApiClient().getCalls(calls -> {
                rootView.findViewById(R.id.loading_layout).setVisibility(View.GONE);
                logsList.setEmptyView(emptyView);
                logsAdapter.setCalls(calls);
                logsAdapter.notifyDataSetChanged();
            }, error -> {

            });

            this.logsAdapter = new LogsAdapter(this.getContext(), new ArrayList<>(), this::makeCall);

            rootView.findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);

            logsList.setAdapter(logsAdapter);
            logsList.setOnItemClickListener((parent, view, position, id) -> {
                handleClick(logsAdapter.getItem(position));
            });
            return rootView;
        } catch (Exception ex) {
            ex.printStackTrace();
            return rootView;
        }
    }






    void handleClick(Call call) {
        String[] colors = {getString(R.string.remove)};
        CallDirection direction = call.getCaller().getId().equals(MyApplication.getUser().getId()) ? CallDirection.OUTBOUND : CallDirection.INBOUND;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View titleView = getLayoutInflater().inflate(R.layout.call_log_detail_title, null);
        TextView titleText = titleView.findViewById(R.id.title_text);
        TextView descText = titleView.findViewById(R.id.desc_text);
        titleText.setText(direction == CallDirection.INBOUND ? call.getCaller().getFullName() : call.getReceptor().getFullName());
        descText.setText(call.getEndType().toString().toUpperCase());
        builder.setCustomTitle(titleView);
        builder.setItems(colors, (dialog, which) -> {
            getMainActivity().reloadFragment();
        });
        builder.show();
    }


    void makeCall(Endpoint user) {
        ProgressDialog dialog = ProgressDialog.show(getContext(), "", getString(R.string.calling_to) + " " + user.getFullName(), false, false);
        MyApplication.getApiClient().call(MyApplication.getUser().getUsername(), user.getUsername(), response -> {
            MyApplication.startCall(getActivity(), response.getId(), response.getCaller().getAccessToken(), response);
            dialog.dismiss();
        }, error -> {
            dialog.dismiss();
            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}