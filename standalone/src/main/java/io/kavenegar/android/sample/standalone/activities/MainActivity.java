package io.kavenegar.android.sample.standalone.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.kavenegar.android.sample.standalone.MyApplication;
 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.core.BaseActivity;
import io.kavenegar.android.sample.standalone.fragments.ContactsFragment;
import io.kavenegar.android.sample.standalone.fragments.LogsFragment;
import io.kavenegar.android.sample.standalone.fragments.MoreFragment;
import io.kavenegar.android.sample.standalone.fragments.ProfileFragment;
import io.kavenegar.android.sample.standalone.fragments.QueueFragment;
import io.kavenegar.sdk.call.enums.CallFinishedReason;

public class MainActivity extends BaseActivity {

    static final String TAG = "MainActivity";

    BottomNavigationView navigationView;

    public LogsFragment logsFragment;
    public ContactsFragment contactsFragment;
    public MoreFragment moreFragment;
    public ProfileFragment profileFragment;
    public QueueFragment queueFragment;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.navigation);


        navigationView.setOnNavigationItemSelectedListener(this::handleNavigationSelected);

        this.contactsFragment = new ContactsFragment();
        this.logsFragment = new LogsFragment();
        this.profileFragment = new ProfileFragment();
        this.moreFragment = new MoreFragment();
        this.queueFragment = new QueueFragment();

        navigationView.setSelectedItemId(R.id.navigation_call_queue);

        this.applyBottomNavFont(this, navigationView);
    }



    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            currentFragment = fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public boolean reloadFragment() {
        if (currentFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .detach(currentFragment)
                    .attach(currentFragment)
                    .replace(R.id.fragment_container, currentFragment)
                    .commit();
            return true;
        }
        return false;
    }

    private boolean handleNavigationSelected(MenuItem item) {

        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_logs:
                fragment = logsFragment;
                break;
            case R.id.navigation_contacts:
                fragment = contactsFragment;
                break;
            case R.id.navigation_profile:
                fragment = profileFragment;
                break;
            case R.id.navigation_call_queue:
                fragment = queueFragment;
                break;
            case R.id.navigation_more:
                fragment = moreFragment;
                break;
        }
        return loadFragment(fragment);

    }

    public View getToolbar() {
        View rootView = getLayoutInflater().inflate(R.layout.toolbar_main, null);
        return rootView;
    }


    public static void applyBottomNavFont(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    applyBottomNavFont(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Shabnam-Bold.ttf"));
            }
        } catch (Exception e) {
        }
    }


    // Get Permissions ===================================================================================================================
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MyApplication.CALL_ACTIVITY_REQUEST_CODE && resultCode == 200) {
            CallFinishedReason endType = (CallFinishedReason) data.getExtras().get("endType");

        }
        super.onActivityResult(requestCode, resultCode, data);
        reloadFragment();
    }


    Dialog makeFeedBackDialog(Integer callId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_feedback, null);

        EditText valueText = dialogView.findViewById(R.id.value_text);
        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        builder.setView(dialogView);
        builder.setTitle(R.string.rate);
        builder.setMessage(R.string.rate_call_dialog_title);

        builder.setPositiveButton(R.string.submit, (dialog, which) -> {
        });
        builder.setNegativeButton(R.string.close, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(view -> {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(this, R.string.call_rating_is_required, Toast.LENGTH_LONG).show();
                    return;
                }
                String message = valueText.getText().toString();
                Integer rating = (int) ratingBar.getRating();
                MyApplication.getApiClient().reportCall(callId, message, rating, "", result -> {
                    Toast.makeText(this, R.string.thanks_for_your_feedback, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }, error -> {
                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                });
            });
        });

        return dialog;
    }


}
