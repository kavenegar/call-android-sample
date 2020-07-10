package io.kavenegar.android.sample.standalone.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.webrtc.PeerConnection;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import io.kavenegar.android.sample.standalone.R;
import io.kavenegar.sdk.call.enums.Environment;
import io.kavenegar.sdk.call.log.DefaultLogger;
import io.kavenegar.android.sample.standalone.MyApplication;

import io.kavenegar.android.sample.standalone.audio.MyAudioManager;
import io.kavenegar.android.sample.standalone.models.CallSession;
import io.kavenegar.android.sample.standalone.models.Endpoint;
import io.kavenegar.android.sample.standalone.utils.RoundedTransformation;
import io.kavenegar.android.sample.standalone.utils.StringUtils;
import io.kavenegar.android.sample.standalone.utils.TimeUtils;
import io.kavenegar.sdk.call.Call;
import io.kavenegar.sdk.call.CallEventListener;
import io.kavenegar.sdk.call.KavenegarCall;
import io.kavenegar.sdk.call.core.JoinCallback;
import io.kavenegar.sdk.call.core.KavenegarException;
import io.kavenegar.sdk.call.enums.CallDirection;
import io.kavenegar.sdk.call.enums.CallFinishedReason;
import io.kavenegar.sdk.call.enums.CallStatus;
import io.kavenegar.sdk.call.enums.JoinStatus;
import io.kavenegar.sdk.call.enums.MessagingState;
import io.kavenegar.sdk.call.log.Logger;
import io.kavenegar.sdk.call.messaging.MediaStateChangedEvent;
import io.kavenegar.sdk.call.messaging.MessagingStateChangedEvent;
import io.kavenegar.sdk.call.webrtc.models.LocalMediaStateChangedEvent;

/**
 * Created by mohsen on 2/27/2018 AD.
 */

public class CallActivity extends Activity implements CallEventListener, JoinCallback {

    static final String TAG = "CallActivity";

    TextView statusText;
    TextView usernameText;
    ImageButton acceptButton;
    ImageButton rejectButton;
    ImageButton hangupButton;
    ImageButton speakerButton;
    ImageButton muteButton;
    ImageView avatarImage;

    MyAudioManager audioManager;

    Call nativeCall;
    io.kavenegar.android.sample.standalone.models.CallSession callSession;

    Logger logger;

    Timer timer = new Timer();

    CallFinishedReason endType;

    TextView mediaStatusText;
    TextView remoteCallerMediaStatusText;
    TextView remoteReceptorMediaStatusText;
    TextView messagingStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

            setContentView(R.layout.activity_call);


            hangupButton = findViewById(R.id.hangup_button);
            statusText = findViewById(R.id.status_text);
            usernameText = findViewById(R.id.username_text);
            acceptButton = findViewById(R.id.accept_button);
            rejectButton = findViewById(R.id.reject_button);
            speakerButton = findViewById(R.id.speaker_button);
            muteButton = findViewById(R.id.mute_button);
            avatarImage = findViewById(R.id.avatar_image);
            mediaStatusText = findViewById(R.id.media_status_text);
            messagingStatusText = findViewById(R.id.messaging_status_text);
            remoteCallerMediaStatusText = findViewById(R.id.remote_caller_media_status_text);
            remoteReceptorMediaStatusText = findViewById(R.id.remote_receptor_media_status_text);

            acceptButton.setOnClickListener(view -> acceptClick());
            rejectButton.setOnClickListener(view -> rejectClick());
            hangupButton.setOnClickListener(view -> hangupClick());

            speakerButton.setEnabled(false);
            muteButton.setEnabled(false);

            speakerButton.setOnClickListener(view -> speakerClick());

            muteButton.setOnClickListener(view -> muteClick());

            audioManager = new MyAudioManager(this, R.raw.connected, R.raw.disconnected);

            this.setInitUI();

            if (!hasAudioPermission()) {
                requestAudioPermission();
            } else {
                this.start();
            }

            if (!hasReadPhoneStatePermission()) {
                requestReadPhoneStatePermission();
            }


//            if (KavenegarCall.getInstance().getEnvironment() == Environment.PRODUCTION) {
//                mediaStatusText.setVisibility(View.GONE);
//                messagingStatusText.setVisibility(View.GONE);
//                remoteMediaStatusText.setVisibility(View.GONE);
//            }

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (nativeCall != null && nativeCall.getStatus() == CallStatus.CONVERSATION) {
                        CallActivity.this.updateDuration();
                    }
                }
            }, 0, 1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void start() {
        try {
            String accessToken = getIntent().getStringExtra("access_token");
            String callId = getIntent().getStringExtra("call_id");
            this.callSession = (CallSession) getIntent().getSerializableExtra("call");
            MyApplication.setCurrentCallId(callId);
            this.logger = KavenegarCall.getInstance().getEnvironment() == Environment.PRODUCTION ? new DefaultLogger() : new DefaultLogger();
            KavenegarCall.getInstance().initCall(callId, accessToken, logger, this, this);


        } catch (Exception ex) {
            logger.error(TAG, "Messaging start has exception ", ex);
        }
    }

    void loadAvatar(String url, ImageView avatarImage) {
        Picasso.get().load(url).transform(new RoundedTransformation(20, 0))
                .placeholder(R.drawable.ic_user)
                .resize(200, 200)
                .error(R.drawable.ic_user).into(avatarImage);
    }


    @Override
    public void onResult(JoinStatus status, Call call) {
        runOnUiThread(() -> {
            try {
                if (status == JoinStatus.SUCCESS) {
                    try {
                        this.nativeCall = call;
                        call.setMessagingStateChangedHandler(this::messagingSateChanged);
                        call.setMediaStateChangedEventHandler(this::peerConnectionStateChanged);
                        audioManager.initializeAudioForCall();
                        if (nativeCall.getDirection() == CallDirection.OUTBOUND) {
                            logger.info(TAG, "Call to " + nativeCall.getReceptor() + ", id : " + nativeCall.getId() + "===============================================================");
                            initOutgoingCall();
                        } else {
                            logger.info(TAG, "Receive call from : " + nativeCall.getCaller() + " , id : " + nativeCall.getId() + " ===============================================================");
                            initIncomingCall();
                        }
                    } catch (Exception ex) {
                        logger.error(TAG, "Messaging start callback", ex);
                        Toast.makeText(CallActivity.this, "Messaging start has error :  " + ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        this.endType = CallFinishedReason.UNKNOWN;
                        this.finish();
                    }
                } else {
                    Toast.makeText(CallActivity.this, "Messaging start result : " + status, Toast.LENGTH_LONG).show();
                    logger.warn(TAG, "Messaging start result is not success : " + status.toString());
                    this.endType = CallFinishedReason.UNKNOWN;
                    this.finish();
                }
            } catch (Exception ex) {
                logger.error(TAG, "Messaging start has exception ", ex);
            }
        });
    }


    private void initIncomingCall() throws KavenegarException {
        try {
            Endpoint endpoint = callSession.getCaller();
            statusText.setText("Incoming");
            if (this.nativeCall.getStatus() == CallStatus.TRYING) {
                this.nativeCall.ringing();
            }
            String name = String.format("%s (%s)", endpoint.getFullName() == null ? "No Name " : endpoint.getFullName(), endpoint.getUsername());
            usernameText.setText(name);
            loadAvatar("https://api.adorable.io/avatars/128/" + endpoint.getUsername(), avatarImage);

            Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.startIncomingRinger(ringtone, false);
        } catch (Exception ex) {
            logger.error(TAG, "", ex);
        }
        setAnswerUI();
    }


    private void initOutgoingCall() {
        try {
            Endpoint endpoint = callSession.getReceptor();
            statusText.setText("Trying");
            String name = String.format("%s (%s)", endpoint.getFullName() == null ? "No Name " : endpoint.getFullName(), endpoint.getUsername());
            usernameText.setText(name);
            loadAvatar("https://api.adorable.io/avatars/128/" + endpoint.getUsername(), avatarImage);
            setCallUI();

            Uri dataUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.outring);
            audioManager.startOutgoingRinger(dataUri, false);

        } catch (Exception ex) {
            logger.error(TAG, "", ex);
        }
    }


    void updateDuration() {
        runOnUiThread(() -> {
            LocalTime t = LocalTime.MIDNIGHT.plusSeconds(this.nativeCall.getDuration() / 1000);
            String formatted = DateTimeFormatter.ofPattern("m:ss").format(t);
            statusText.setText(formatted);
        });
    }


    // Kavenegar Call State Change Events ====================================================================

    private void peerConnectionStateChanged(LocalMediaStateChangedEvent event) {
        this.runOnUiThread(() -> {
            PeerConnection.IceConnectionState old = event.getOldState();
            PeerConnection.IceConnectionState current = event.getNewState();
            if ((old == PeerConnection.IceConnectionState.COMPLETED || old == PeerConnection.IceConnectionState.CONNECTED) && current == PeerConnection.IceConnectionState.DISCONNECTED) {
                nativeCall.setStatus(CallStatus.PAUSED, true);
            }

            mediaStatusText.setText(StringUtils.capitalize(current.name()));
        });
    }

    private void messagingSateChanged(MessagingStateChangedEvent event) {
        this.runOnUiThread(() -> {
            MessagingState old = event.getOldState();
            MessagingState current = event.getNewState();
            messagingStatusText.setText(StringUtils.capitalize(current.name()));
        });
    }

    // UI Methods ============================================================================================


    public void setInitUI() {
        acceptButton.setVisibility(View.GONE);
        rejectButton.setVisibility(View.GONE);
        hangupButton.setVisibility(View.GONE);
        speakerButton.setVisibility(View.GONE);
        muteButton.setVisibility(View.GONE);
    }

    public void setCallUI() {
        speakerButton.setEnabled(true);
        muteButton.setEnabled(true);
        acceptButton.setVisibility(View.GONE);
        rejectButton.setVisibility(View.GONE);
        hangupButton.setVisibility(View.VISIBLE);

        speakerButton.setVisibility(View.VISIBLE);
        muteButton.setVisibility(View.VISIBLE);
    }

    public void setAnswerUI() {
        speakerButton.setEnabled(true);
        muteButton.setEnabled(true);
        hangupButton.setVisibility(View.GONE);
        acceptButton.setVisibility(View.VISIBLE);
        rejectButton.setVisibility(View.VISIBLE);

        speakerButton.setVisibility(View.VISIBLE);
        muteButton.setVisibility(View.VISIBLE);
    }

    private void setCommunicationUI() {
        speakerButton.setEnabled(true);
        muteButton.setEnabled(true);
        acceptButton.setVisibility(View.GONE);
        rejectButton.setVisibility(View.GONE);
        hangupButton.setVisibility(View.VISIBLE);
    }

    // UI Events =================================================================================================

    public void acceptClick() {
        try {
            this.nativeCall.accept();
            setCommunicationUI();
        } catch (Exception ex) {
            logger.error(TAG, "", ex);
        }
    }

    public void rejectClick() {
        try {
            this.nativeCall.reject();
        } catch (Exception ex) {
            logger.error(TAG, "", ex);
        }
    }

    public void hangupClick() {
        try {
            this.nativeCall.hangup();
        } catch (Exception ex) {
            logger.error(TAG, "", ex);
        }
    }

    public void muteClick() {
        Uri dataUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.reconnecting);
        if (audioManager.isMicrophoneMute()) {
            audioManager.setMicrophoneMute(false);
            muteButton.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_with_border));
        } else {
            audioManager.setMicrophoneMute(true);
            muteButton.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_background));
        }
    }

    public void speakerClick() {
        if (audioManager.isSpeakerOn()) {
            audioManager.setSpeakerOn(false);
            speakerButton.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_with_border));
        } else {
            audioManager.setSpeakerOn(true);
            speakerButton.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_background));
        }
    }

    // ============================================================================================================== //


    @Override
    public void finish() {
        if (this.nativeCall != null) {
            try {
                this.nativeCall.dispose();
                audioManager.stop(false);
            } catch (Exception ex) {
                logger.error(KavenegarCall.TAG, "Exception in dispose call", ex);
            }
            logger.info(TAG, "Finish call with id : " + nativeCall.getId());

            Intent returnIntent = new Intent();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

            returnIntent.putExtra("createdAt", TimeUtils.toISOFormat(callSession.getCreatedAt()));
            returnIntent.putExtra("startedAt", nativeCall.getStartedAt() != null ? dateFormat.format(nativeCall.getStartedAt()) : "");
            returnIntent.putExtra("finishedAt", nativeCall.getFinishedAt() != null ? dateFormat.format(nativeCall.getFinishedAt()) : "");
            returnIntent.putExtra("endType", endType);
            returnIntent.putExtra("remoteId", nativeCall.getId());
            returnIntent.putExtra("callId", callSession.getId());
            returnIntent.putExtra("duration", nativeCall.getDuration());
            setResult(200, returnIntent);
            MyApplication.setCurrentCallId(null);
            super.finish();
        } else {
            super.finish();
        }
    }


    // Call events =============================================================================================== //

    @Override
    public void onMediaStateChanged(MediaStateChangedEvent event) {
        runOnUiThread(() -> {
            remoteCallerMediaStatusText.setText(StringUtils.capitalize(nativeCall.getCallerMediaState().toString()));
            remoteReceptorMediaStatusText.setText(StringUtils.capitalize(nativeCall.getReceptorMediaState().toString()));
        });
    }


    @Override
    public void onCallFinished(CallFinishedReason reason) {
        logger.info(TAG, "Call finished with reason :" + reason.name());
        this.endType = reason;
        this.finish();
    }


    @Override
    public void onCallStateChanged(CallStatus state, boolean isLocalChange) {
        if (nativeCall != null) {
            //nativeCall.getLogger().info("CallActivity", "Call state changed event listener executed with :" + state + " , is local:" + isLocalChange + ", last status : " + nativeCall.getStatus());
        }
        runOnUiThread(() -> {
            setCallStatus(state);
            switch (state) {
                case NEW: {

                    break;
                }
                case TRYING: {

                    break;
                }
                case RINGING: {

                    break;
                }
                case ACCEPTED: {
                    audioManager.stop(false);
                    break;
                }
                case CONVERSATION: {
                    setCommunicationUI();
                    audioManager.startCommunication(false);

                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(200);
                    }

                    break;
                }
                case PAUSED: {
                    Uri dataUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.reconnecting);
                    audioManager.startBeepRinger(dataUri, false);
                }
                case FINISHED: {
                    break;
                }
            }
        });
    }


    void setCallStatus(CallStatus status) {
        String text = "";
        switch (status) {
            case TRYING: {
                text = "Trying";
                break;
            }
            case RINGING: {
                text = "Ringing";
                break;
            }
            case ACCEPTED: {
                text = "Accepted";
                break;
            }
            case CONVERSATION: {
                text = "Conversation";
                break;
            }
            case PAUSED: {
                text = "Paused";
                break;
            }
            case FINISHED: {
                text = "Finished";
                break;
            }
        }
        statusText.setText(text);
    }


    // Permissions =============================================================================================== //

    boolean hasAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    boolean hasReadPhoneStatePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    void requestReadPhoneStatePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
    }

    void requestAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.start();
                } else {
                    Toast.makeText(this, "Failed to access the  microphone.\nclick allow when asked for permission.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


}