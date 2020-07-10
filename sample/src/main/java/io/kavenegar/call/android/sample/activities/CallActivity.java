package io.kavenegar.call.android.sample.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.kavenegar.call.android.sample.R;
import io.kavenegar.sdk.call.Call;
import io.kavenegar.sdk.call.CallEventListener;
import io.kavenegar.sdk.call.KavenegarCall;
import io.kavenegar.sdk.call.audio.KavenegarAudioManager;
import io.kavenegar.sdk.call.core.KavenegarException;
import io.kavenegar.sdk.call.enums.CallDirection;
import io.kavenegar.sdk.call.enums.CallFinishedReason;
import io.kavenegar.sdk.call.enums.CallStatus;
import io.kavenegar.sdk.call.enums.JoinStatus;
import io.kavenegar.sdk.call.enums.MediaState;
import io.kavenegar.sdk.call.enums.MessagingState;
import io.kavenegar.sdk.call.log.DefaultLogger;
import io.kavenegar.sdk.call.log.Logger;
import io.kavenegar.sdk.call.messaging.MediaStateChangedEvent;
import io.kavenegar.sdk.call.messaging.MessagingStateChangedEvent;
import io.kavenegar.sdk.call.webrtc.models.LocalMediaStateChangedEvent;


public class CallActivity extends Activity implements CallEventListener {

    static final String TAG = "CallActivity";

    TextView callStatusText;
    TextView callerUserNameText;
    TextView messagingStateText;
    TextView mediaStateText;

    ImageButton acceptButton;
    ImageButton rejectButton;
    ImageButton hangupButton;
    ImageButton speakerButton;
    ImageButton muteButton;

    KavenegarAudioManager audioManager;


    Call call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_call);

        hangupButton = findViewById(R.id.hangup_button);
        callStatusText = findViewById(R.id.call_status_text);
        callerUserNameText = findViewById(R.id.caller_username_text);
        acceptButton = findViewById(R.id.accept_button);
        rejectButton = findViewById(R.id.reject_button);
        speakerButton = findViewById(R.id.speaker_button);
        muteButton = findViewById(R.id.mute_button);
        messagingStateText = findViewById(R.id.messaging_state_text);
        mediaStateText = findViewById(R.id.media_state_text);

        acceptButton.setOnClickListener(view -> acceptClick());
        rejectButton.setOnClickListener(view -> rejectClick());
        hangupButton.setOnClickListener(view -> hangupClick());

        muteButton.setEnabled(false);
        speakerButton.setEnabled(false);

        speakerButton.setOnClickListener(view -> speakerClick());

        muteButton.setOnClickListener(view -> muteClick());

        audioManager = new KavenegarAudioManager(this, R.raw.webrtc_completed, R.raw.webrtc_disconnected);

        this.setInitUI();

        if (!hasAudioPermission()) {
            requestAudioPermission();
        } else {
            this.start();
        }
    }

    public void start() {
        try {
            String accessToken = getIntent().getStringExtra("access_token");
            String callId = getIntent().getStringExtra("call_id");

            KavenegarCall.getInstance().initCall(callId, accessToken, new DefaultLogger(), this, this::handleJoinResult);

        } catch (Exception ex) {
            call.getLogger().error(TAG, "Messaging start has exception ", ex);
        }
    }

    private void handleJoinResult(JoinStatus status, Call call) {
        runOnUiThread(() -> {
            try {
                if (status != JoinStatus.SUCCESS) {
                    Toast.makeText(CallActivity.this, "Messaging start result : " + status, Toast.LENGTH_LONG).show();
                    call.getLogger().warn(TAG, "Messaging start result is not success : " + status.toString());
                    this.finish();
                    return;
                }
                audioManager.initializeAudioForCall();

                this.call = call;

                this.call.setMediaStateChangedEventHandler(this::peerConnectionStateChanged);
                this.call.setMessagingStateChangedHandler(this::messagingSateChanged);

                if (call.getDirection() == CallDirection.OUTBOUND) {
                    initOutgoingCall();
                } else {
                    initIncomingCall();
                }

            } catch (Exception ex) {
                call.getLogger().error(TAG, "Messaging start callback", ex);
                Toast.makeText(CallActivity.this, "Messaging start has error :  " + ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                this.finish();
            }
        });
    }


    private void initIncomingCall() throws KavenegarException {
        runOnUiThread(() -> {
            try {
                this.call.ringing();
                callerUserNameText.setText(call.getCaller().getUsername());
                callStatusText.setText("Incoming call");
                Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                audioManager.startIncomingRinger(ringtone, false);
            } catch (Exception ex) {
                Log.e(TAG, "", ex);
            }
            setAnswerUI();
        });
    }

    private void initOutgoingCall() {
        runOnUiThread(() -> {
            callerUserNameText.setText(this.call.getReceptor().getUsername());
            setCallUI();
        });
    }

    // Kavenegar Call State Change Events ====================================================================

    private void peerConnectionStateChanged(LocalMediaStateChangedEvent event) {
        this.runOnUiThread(() -> {
            mediaStateText.setText("Media: " + event.getOldState().name().toLowerCase() + " => " + event.getNewState().name().toLowerCase());
        });
    }


    private void messagingSateChanged(MessagingStateChangedEvent event) {
        this.runOnUiThread(() -> {
            MessagingState old = event.getOldState();
            MessagingState current = event.getNewState();
            messagingStateText.setText("Messaging: " + old.name().toLowerCase() + " => " + current.name().toLowerCase());
        });
    }

    // UI Methods ============================================================================================


    public void setInitUI() {
        acceptButton.setVisibility(View.GONE);
        rejectButton.setVisibility(View.GONE);
        hangupButton.setVisibility(View.GONE);
    }

    public void setCallUI() {
        acceptButton.setVisibility(View.GONE);
        rejectButton.setVisibility(View.GONE);
        hangupButton.setVisibility(View.VISIBLE);
    }

    public void setAnswerUI() {
        hangupButton.setVisibility(View.GONE);
        acceptButton.setVisibility(View.VISIBLE);
        rejectButton.setVisibility(View.VISIBLE);
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
            this.call.accept();
        } catch (Exception ex) {
            Log.e(TAG, "", ex);
        }
    }

    public void rejectClick() {
        try {
            this.call.reject();
        } catch (Exception ex) {
            Log.e(TAG, "", ex);
        }
    }

    public void hangupClick() {
        try {
            this.call.hangup();
        } catch (Exception ex) {
            Log.e(TAG, "", ex);
        }
    }

    public void muteClick() {
        if (audioManager.isMicrophoneMute()) {
            audioManager.setMicrophoneMute(false);
            muteButton.setBackground(null);
        } else {
            audioManager.setMicrophoneMute(true);
            muteButton.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_background));
        }
    }

    public void speakerClick() {
        if (audioManager.isSpeakerOn()) {
            audioManager.setSpeakerOn(false);
            speakerButton.setBackground(null);
        } else {
            audioManager.setSpeakerOn(true);
            speakerButton.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_button_background));
        }
    }


    @Override
    public void finish() {
        try {
            this.call.dispose();
            audioManager.stop(false);
        } catch (Exception ex) {
            Log.e(KavenegarCall.TAG, "Exception in dispose call", ex);
        }
        Log.i(TAG, "Finish call with id : " + call.getId());
        super.finish();
    }


    // Call events =============================================================================================== //

    @Override
    public void onMediaStateChanged(MediaStateChangedEvent event) {
        this.runOnUiThread(() -> {
            Toast.makeText(this, event.toString(), Toast.LENGTH_LONG).show();

            if (call.getCallerMediaState() == MediaState.CONNECTED && call.getReceptorMediaState() == MediaState.CONNECTED) {
                audioManager.startCommunication(false);
            }
        });
    }


    @Override
    public void onCallFinished(CallFinishedReason reason) {
        Log.i(TAG, "Call finished with reason :" + reason.name());
        CallActivity.this.finish();
    }

    @Override
    public void onCallStateChanged(CallStatus state, boolean isLocalChange) {
        runOnUiThread(() -> {
            callStatusText.setText(state.name());
            switch (state) {
                case TRYING: {
                    String packageName = CallActivity.this.getPackageName();
                    Uri dataUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.redphone_sonarping);
                    audioManager.startOutgoingRinger(dataUri, false);
                    break;
                }
                case RINGING: {
                    audioManager.stop(false);
                    break;
                }
                case ACCEPTED: {
                    setCommunicationUI();
                    break;
                }
                case CONVERSATION: {
                    audioManager.stop(false);
                    setCommunicationUI();
                    break;
                }
                case FINISHED: {
                    break;
                }
            }
        });
    }

    // Permissions =============================================================================================== //

    boolean hasAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
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
                    Toast.makeText(this, "FAILED to access the   microphone.\nclick allow when asked for permission.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}