package io.kavenegar.android.sample.standalone.audio;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

class OutgoingRinger {

    private static final String TAG = OutgoingRinger.class.getSimpleName();

    private final Context context;

    private MediaPlayer mediaPlayer;

    OutgoingRinger(Context context) {
        this.context = context;
    }

    void start(Uri uri) {

        if (mediaPlayer != null) {
            stop();
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);

        try {
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepare();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            Log.w(TAG, e);
        }
    }

    void stop() {
        if (mediaPlayer == null) return;
        mediaPlayer.release();
        mediaPlayer = null;
    }
}