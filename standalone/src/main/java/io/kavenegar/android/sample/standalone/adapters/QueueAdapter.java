package io.kavenegar.android.sample.standalone.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import io.kavenegar.sdk.call.enums.CallDirection;
import io.kavenegar.sdk.call.enums.CallFinishedReason;
import io.kavenegar.android.sample.standalone.MyApplication;
 import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.android.sample.standalone.models.CallSession;
import io.kavenegar.android.sample.standalone.network.Callback;
import io.kavenegar.android.sample.standalone.utils.RoundedTransformation;

import static org.threeten.bp.format.DateTimeFormatter.ofPattern;


public class QueueAdapter extends ArrayAdapter<CallSession> {

    private Context context;
    private List<CallSession> calls;
    private ImageView avatarImage;
    private TextView usernameText;
    private ImageView directionImage;
    private TextView datetimeText;
    private TextView durationText;
    private TextView finishReasonText;
    private Callback<CallSession> onCall;


    public QueueAdapter(Context context, List<CallSession> list, Callback<CallSession> onCall) {
        super(context, 0, list);
        this.context = context;
        calls = list;
        this.onCall = onCall;
    }

    public void setCalls(List<CallSession> calls) {
        this.calls = calls;
    }

    @Override
    public int getCount() {
        return calls.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.row_queue, parent, false);

        CallSession call = calls.get(position);

        this.avatarImage = listItem.findViewById(R.id.avatar_image);
        this.usernameText = listItem.findViewById(R.id.textView3);
        this.directionImage = listItem.findViewById(R.id.direction_image);
        this.datetimeText = listItem.findViewById(R.id.datetime_text);
        this.durationText = listItem.findViewById(R.id.duration_text);
        this.finishReasonText = listItem.findViewById(R.id.finish_reason_text);

        ImageButton callButton = listItem.findViewById(R.id.call_button);


        CallDirection direction = call.getCaller().getId().equals(MyApplication.getUser().getId()) ? CallDirection.OUTBOUND : CallDirection.INBOUND;

        callButton.setOnClickListener(view -> {
            onCall.accept(call);
        });


        if (direction == CallDirection.INBOUND) {
            usernameText.setText(call.getCaller().getUsername());
            loadAvatar("https://api.adorable.io/avatars/128/" + call.getCaller().getUsername());
        } else {
            usernameText.setText(call.getReceptor().getUsername());
            loadAvatar("https://api.adorable.io/avatars/128/" + call.getReceptor().getUsername());
        }
        directionImage.setImageResource(getDirectionImage(direction, call));

        LocalDateTime createdAt = call.getCreatedAt();
        if (createdAt != null) {
            datetimeText.setText(createdAt.format(ofPattern("MMM dd ")) + " at " + createdAt.format(ofPattern("h:mm")));
        }
        if (call.getEndType() != null && call.getEndType() != CallFinishedReason.HANGUP) {
            finishReasonText.setText(call.getEndType().toString());
            finishReasonText.setTextColor(ContextCompat.getColor(context, R.color.logItemDanger));
        } else {
            finishReasonText.setText(call.getEndType().toString());
            finishReasonText.setTextColor(ContextCompat.getColor(context, R.color.logItemDefault));
        }

        if (call.getDuration() != null && call.getDuration() != 0) {
            LocalTime t = LocalTime.MIDNIGHT.plusSeconds(call.getDuration() / 1000);
            String formatted = DateTimeFormatter.ofPattern("m:ss").format(t);
            durationText.setText("(" + formatted + ")");
        } else {
            durationText.setText("");
        }


        return listItem;
    }


    void loadAvatar(String url) {
        Picasso.get().load(url).transform(new RoundedTransformation(20,0))
                .placeholder(R.drawable.ic_user)
                .resize(200, 200)
                .error(R.drawable.ic_user).into(avatarImage);
    }


    int getDirectionImage(CallDirection direction, CallSession call) {
        if (direction == CallDirection.INBOUND) {
            if (call.getDuration() == 0) {
                return R.drawable.ic_call_received_red_18dp;
            } else {
                return R.drawable.ic_call_received_green_18dp;
            }
        } else {
            if (call.getDuration() == 0) {
                return R.drawable.ic_call_made_red_18dp;
            } else {
                return R.drawable.ic_call_made_green_18dp;
            }
        }
    }

    @Override
    public CallSession getItem(int position) {
        return calls.get(position);
    }
}

