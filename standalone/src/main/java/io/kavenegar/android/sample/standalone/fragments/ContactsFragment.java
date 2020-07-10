package io.kavenegar.android.sample.standalone.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import io.kavenegar.android.sample.standalone.MyApplication;
 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.activities.MainActivity;
import io.kavenegar.android.sample.standalone.adapters.EndpointsAdapter;
import io.kavenegar.android.sample.standalone.models.Endpoint;

public class ContactsFragment extends Fragment {

    static final String TAG = "Contacts";

    ImageView callButton;

    ListView contactsList;
    EndpointsAdapter contactsAdapter;

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contacts, null);

        rootView.findViewById(R.id.sync_button).setVisibility(View.GONE);



        MyApplication.getApiClient().getEndpoints(response -> {
            rootView.findViewById(R.id.loading_layout).setVisibility(View.GONE);
            contactsAdapter.setContacts(response);
            contactsAdapter.notifyDataSetChanged();
            contactsList.setEmptyView(rootView.findViewById(R.id.contacts_empty));
        }, error -> {

        });


        this.contactsAdapter = new EndpointsAdapter(this.getContext(), new ArrayList<>(), this::makeCall);
        contactsList = rootView.findViewById(R.id.contacts_list);
        contactsList.setAdapter(contactsAdapter);

        rootView.findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);

        return rootView;
    }



    void makeCall(Endpoint user) {

        ProgressDialog dialog = ProgressDialog.show(getContext(), "", getString(R.string.calling_to) + " " + user.getFullName(), true, false);
        MyApplication.getApiClient().call(MyApplication.getUser().getUsername(), user.getUsername(), response -> {
            MyApplication.startCall(getActivity(), response.getId(), response.getCaller().getAccessToken(), response);
            dialog.dismiss();
        }, error -> {
            dialog.dismiss();
            Toast.makeText(getContext(), "ApiError :" + error.toString(), Toast.LENGTH_LONG).show();
        });

    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }


}