package io.kavenegar.android.sample.standalone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.models.MoreMenu;


public class MoreMenuAdapter extends ArrayAdapter<MoreMenu> {

    private Context context;
    private List<MoreMenu> contacts;

    public MoreMenuAdapter(Context context, List<MoreMenu> contacts) {
        super(context, 0, contacts);
        this.context = context;
        this.contacts = contacts;
    }


    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.row_menu, parent, false);

        MoreMenu contact = contacts.get(position);

        TextView labelText = listItem.findViewById(R.id.label_text);

        labelText.setText(contact.label);


        ImageView icon = listItem.findViewById(R.id.icon_img);
        icon.setImageResource(contact.icon);
        icon.setVisibility(View.GONE);

        return listItem;
    }


}


