package io.kavenegar.android.sample.standalone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.kavenegar.android.sample.standalone.MyApplication;
import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.activities.MainActivity;
import io.kavenegar.android.sample.standalone.adapters.QueueAdapter;
import io.kavenegar.android.sample.standalone.models.CallSession;

public class QueueFragment extends Fragment {


    QueueAdapter queueAdapter;
    Timer timer = new Timer();

    public QueueFragment() {

    }


    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_queue, null);

        try {

            ListView logsList = rootView.findViewById(R.id.logs_list);


            this.queueAdapter = new QueueAdapter(this.getContext(), new ArrayList<>(), this::handleClick);

            rootView.findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);


            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (QueueFragment.this != null) {
                            QueueFragment.this.update(rootView, logsList);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, 0, 3000);

            logsList.setAdapter(queueAdapter);
            logsList.setOnItemClickListener((parent, view, position, id) -> {
                handleClick(queueAdapter.getItem(position));
            });


            return rootView;
        } catch (Exception ex) {
            ex.printStackTrace();
            return rootView;
        }
    }

    void update(View rootView, ListView logsList) {
        View emptyView = rootView.findViewById(R.id.logs_empty);
        MyApplication.getApiClient().getSessions(MyApplication.getUser().getUsername(), calls -> {
            rootView.findViewById(R.id.loading_layout).setVisibility(View.GONE);
            logsList.setEmptyView(emptyView);
            queueAdapter.setCalls(calls);
            queueAdapter.notifyDataSetChanged();
        }, error -> {

        });

    }


    void handleClick(CallSession call) {
        MyApplication.startCall(getActivity(), call.getId(), call.getReceptor().getAccessToken(), call);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}