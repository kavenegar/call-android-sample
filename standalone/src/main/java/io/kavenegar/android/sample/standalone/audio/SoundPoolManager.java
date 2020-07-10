package io.kavenegar.android.sample.standalone.audio;


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;


import io.kavenegar.android.sample.standalone.R;

import static android.content.Context.AUDIO_SERVICE;

public class SoundPoolManager {

    private boolean playing = false;
    private boolean loaded = false;
    private boolean playingCalled = false;
    private float actualVolume;
    private float maxVolume;
    private float volume;
    private AudioManager audioManager;
    private SoundPool soundPool;
    private int ringingSoundId;
    private int ringingStreamId;

    private int disconnectSoundId;

    private int connectedSoundId;

    private int reconnectingSoundId;
    private int reconnectingStreamId;


    private static SoundPoolManager instance;

    public SoundPoolManager(Context context) {
        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actualVolume / maxVolume;

        // Load the sounds
        int maxStreams = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .build();
        } else {
            soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        }

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            loaded = true;
            if (playingCalled) {
                playRinging();
                playingCalled = false;
            }
        });
        ringingSoundId = soundPool.load(context, R.raw.outring, 1);
        reconnectingSoundId = soundPool.load(context, R.raw.reconnecting, 1);
        connectedSoundId = soundPool.load(context, R.raw.connected, 1);
        disconnectSoundId = soundPool.load(context, R.raw.disconnected, 1);
    }

    public static SoundPoolManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundPoolManager(context);
        }
        return instance;
    }

    public void playRinging() {
        if (loaded && !playing) {
            ringingStreamId = soundPool.play(ringingSoundId, volume, volume, 1, -1, 1f);
            playing = true;
        } else {
            playingCalled = true;
        }
    }

    public void playReconnecting() {
        if (loaded && !playing) {
            reconnectingStreamId = soundPool.play(reconnectingSoundId, volume, volume, 1, -1, 0.5f);
            playing = true;
        }
    }

    public void stopReconnecting() {
        if (playing) {
            soundPool.stop(reconnectingStreamId);
            playing = false;
        }
    }

    public void stopRinging() {
        if (playing) {
            soundPool.stop(ringingStreamId);
            playing = false;
        }
    }

    public void playDisconnect() {
        if (loaded && !playing) {
            soundPool.play(disconnectSoundId, volume, volume, 1, 1, 1f);
            playing = false;
        }
    }

    public void playConnected() {
        if (loaded && !playing) {
            soundPool.play(disconnectSoundId, volume, volume, 1, 1, 1f);
            playing = false;
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.unload(ringingSoundId);
            soundPool.unload(disconnectSoundId);
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }

}
