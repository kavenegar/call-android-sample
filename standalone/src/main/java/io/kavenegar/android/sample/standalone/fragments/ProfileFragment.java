package io.kavenegar.android.sample.standalone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import io.kavenegar.android.sample.standalone.MyApplication;
import io.kavenegar.android.sample.standalone.SettingsService;
import io.kavenegar.android.sample.standalone.activities.ChooseEndpointActivity;
import io.kavenegar.android.sample.standalone.activities.LoginActivity;
import io.kavenegar.android.sample.standalone.activities.MainActivity;
import io.kavenegar.android.sample.standalone.models.Endpoint;
 import io.kavenegar.android.sample.standalone.R;

public class ProfileFragment extends Fragment {


    ImageView avatarImage;
    TextView usernameText;
    TextView fullNameText;
    TextView mobileNumberText;


    View rootView;
    private TextView headerFullNameText;

    public ProfileFragment() {

    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_profile, null);
        Endpoint user = MyApplication.getUser();

        this.usernameText = rootView.findViewById(R.id.username_text);
        this.mobileNumberText = rootView.findViewById(R.id.header_mobile_number_text);
        this.headerFullNameText = rootView.findViewById(R.id.header_fullname_text);
        this.fullNameText = rootView.findViewById(R.id.message_text);
        this.avatarImage = rootView.findViewById(R.id.avatar_image);



        fullNameText.setText(user.getFullName() == null ? "---" : user.getFullName());
        headerFullNameText.setText(user.getFullName() == null ? "---" : user.getFullName());
        usernameText.setText(user.getUsername() == null ? "---" : user.getUsername());
        mobileNumberText.setText(user.getUsername());

        avatarImage.setOnClickListener(v -> {


        });


        Toolbar toolbar = rootView.findViewById(R.id.toolbar_top);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_more_vert_black_24dp));
        toolbar.inflateMenu(R.menu.profile_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.navigation_choose_endpoint) {
                Intent intent = new Intent(getActivity(), ChooseEndpointActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            } else {
                SettingsService.signOut(getActivity());
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }
        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.navigation, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }




}