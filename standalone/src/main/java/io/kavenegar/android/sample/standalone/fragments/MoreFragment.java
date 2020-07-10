package io.kavenegar.android.sample.standalone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;

import io.kavenegar.android.sample.standalone.BuildConfig;
import io.kavenegar.android.sample.standalone.MyApplication;
 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.activities.ContactActivity;
import io.kavenegar.android.sample.standalone.activities.WebViewActivity;
import io.kavenegar.android.sample.standalone.adapters.MoreMenuAdapter;
import io.kavenegar.android.sample.standalone.models.MoreMenu;

public class MoreFragment extends Fragment {
    private TextView appVersionText;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_more, null);

        List<MoreMenu> list = Arrays.asList(
//                new MoreMenu(R.string.about_us, R.drawable.ic_info_black_24dp, "about.html"),
                new MoreMenu(R.string.contact_us, R.drawable.ic_email_black_24dp, "contact.html"),
                new MoreMenu(R.string.faq, R.drawable.ic_help_black_24dp, "faq.html")
        );

        MoreMenuAdapter itemsAdapter = new MoreMenuAdapter(this.getContext(), list);

        ListView logsList = rootView.findViewById(R.id.more_list);
        logsList.setAdapter(itemsAdapter);

        this.appVersionText = rootView.findViewById(R.id.app_version_text);


        appVersionText.setText(BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE);

        logsList.setOnItemClickListener((adapterView, view, i, l) -> {

            if (i == 0) {
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                startActivity(intent);
                return;
            }
            Intent intent = new Intent(this.getActivity(), WebViewActivity.class);
            MoreMenu menu = list.get(i);

            intent.putExtra("title", getResources().getString(menu.label));
            intent.putExtra("url", MyApplication.backendURL + "templates/" + menu.name);

            this.startActivity(intent);
        });

        return rootView;
    }
}