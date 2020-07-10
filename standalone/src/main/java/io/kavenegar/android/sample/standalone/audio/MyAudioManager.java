package io.kavenegar.android.sample.standalone.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class MyAudioManager implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "AudioManager";

    private final Context context;
    private final IncomingRinger incomingRinger;
    private final OutgoingRinger outgoingRinger;
    private final OutgoingRinger beepRinger;

    private final SoundPool soundPool;
    private final int connectedSoundId;
    private final int disconnectedSoundId;

    public MyAudioManager(Context context, Integer connectedSound, Integer disconnectedSound) {
        this.context = context.getApplicationContext();
        this.incomingRinger = new IncomingRinger(context);
        this.outgoingRinger = new OutgoingRinger(context);
        this.beepRinger = new OutgoingRinger(context);
        this.soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        this.connectedSoundId = this.soundPool.load(context, connectedSound, 1);
        this.disconnectedSoundId = this.soundPool.load(context, disconnectedSound, 1);
    }


    public static AudioManager getAudioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }


    public void initializeAudioForCall() {
        AudioManager audioManager = getAudioManager(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            audioManager.requestAudioFocus(this, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);

        } else {
            audioManager.requestAudioFocus(this, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);
        }
    }


    public void startIncomingRinger(Uri ringtoneUri, boolean vibrate) {
        AudioManager audioManager = getAudioManager(context);
        boolean speaker = !audioManager.isWiredHeadsetOn() && !audioManager.isBluetoothScoOn();

        audioManager.setMode(AudioManager.MODE_RINGTONE);
        audioManager.setMicrophoneMute(false);
        audioManager.setSpeakerphoneOn(speaker);

        incomingRinger.start(ringtoneUri, vibrate);
    }

    public void startOutgoingRinger(Uri type, boolean speakerOff) {
        AudioManager audioManager = getAudioManager(context);
        audioManager.setMicrophoneMute(false);

        if (speakerOff) {
            audioManager.setSpeakerphoneOn(false);
        }

        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        outgoingRinger.start(type);
    }

    public void startBeepRinger(Uri type, boolean speakerOff) {

        outgoingRinger.stop();
        incomingRinger.stop();

        AudioManager audioManager = getAudioManager(context);
        audioManager.setMicrophoneMute(false);

        if (speakerOff) {
            audioManager.setSpeakerphoneOn(false);
        }

        audioManager.setMode(AudioManager.MODE_RINGTONE);

        beepRinger.start(type);
    }

    public void silenceIncomingRinger() {
        incomingRinger.stop();
    }

    public void startCommunication(boolean preserveSpeakerphone) {
        AudioManager audioManager = getAudioManager(context);

        incomingRinger.stop();
        outgoingRinger.stop();
        beepRinger.stop();

        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        if (!preserveSpeakerphone) {
            audioManager.setSpeakerphoneOn(false);
        }

        soundPool.play(connectedSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void stop(boolean playDisconnected) {
        AudioManager audioManager = getAudioManager(context);

        incomingRinger.stop();
        outgoingRinger.stop();
        beepRinger.stop();

        if (playDisconnected) {
            soundPool.play(disconnectedSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }

        if (audioManager.isBluetoothScoOn()) {
            audioManager.setBluetoothScoOn(false);
            audioManager.stopBluetoothSco();
        }

        audioManager.setSpeakerphoneOn(false);
        audioManager.setMicrophoneMute(false);
        audioManager.setMode(AudioManager.MODE_NORMAL);

        audioManager.abandonAudioFocus(null);
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.i(TAG, "Audio Focus Changed , " + focusChange);
    }

    public boolean isSpeakerOn() {
        AudioManager audioManager = getAudioManager(context);
        return audioManager.isSpeakerphoneOn();
    }

    public void setSpeakerOn(boolean status) {
        AudioManager audioManager = getAudioManager(context);
        audioManager.setSpeakerphoneOn(status);

        if (status && audioManager.isBluetoothScoOn()) {
            audioManager.stopBluetoothSco();
            audioManager.setBluetoothScoOn(false);
        }
    }


    public void setMicrophoneMute(boolean on) {
        AudioManager audioManager = getAudioManager(context);
        audioManager.setMicrophoneMute(on);
    }

    public boolean isMicrophoneMute() {
        AudioManager audioManager = getAudioManager(context);
        return audioManager.isMicrophoneMute();
    }

}