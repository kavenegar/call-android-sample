package io.kavenegar.android.sample.standalone.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.models.Endpoint;
import io.kavenegar.android.sample.standalone.network.Callback;
import io.kavenegar.android.sample.standalone.utils.RoundedTransformation;

public class EndpointsAdapter extends ArrayAdapter<Endpoint> {


    private Context context;
    private List<Endpoint> contacts;
    private Callback<Endpoint> onCall;

    public EndpointsAdapter(@NonNull Context context, List<Endpoint> list, Callback<Endpoint> onCall) {
        super(context, 0, list);
        this.context = context;
        contacts = list;
        this.onCall = onCall;
    }

    public void setContacts(List<Endpoint> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.row_contact, parent, false);


        Endpoint contact = contacts.get(position);

        TextView usernameText = listItem.findViewById(R.id.username_text);
        usernameText.setText(contact.getFullName() == null ? "No Name !" : contact.getFullName());


        ImageView avatarImage = listItem.findViewById(R.id.avatar_image);

        loadAvatar("https://api.adorable.io/avatars/128/" + contact.getUsername(), avatarImage);


        TextView mobileNumberText = listItem.findViewById(R.id.mobile_number_text);
        mobileNumberText.setText(contact.getUsername());

        ImageButton callButton = listItem.findViewById(R.id.call_button);

        callButton.setOnClickListener(view -> {
            onCall.accept(contact);
        });


        return listItem;
    }


    void loadAvatar(String url, ImageView avatarImage) {
        Picasso.get().load(url).transform(new RoundedTransformation(20,0))
                .placeholder(R.drawable.ic_user)
                .resize(200, 200)
                .error(R.drawable.ic_user).into(avatarImage);
    }


    @Override
    public int getCount() {
        return contacts.size();
    }


    @Override
    public Endpoint getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return contacts.get(position).getId();
    }
}
