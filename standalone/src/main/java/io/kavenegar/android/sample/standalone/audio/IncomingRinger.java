package io.kavenegar.android.sample.standalone.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;

import io.kavenegar.sdk.call.audio.KavenegarAudioManager;


class IncomingRinger {

    private static final String TAG = IncomingRinger.class.getSimpleName();

    private static final long[] VIBRATE_PATTERN = {0, 1000, 1000};

    private final Context context;
    private final Vibrator vibrator;

    private MediaPlayer player;

    IncomingRinger(Context context) {
        this.context = context.getApplicationContext();
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }


    void start(Uri uri, boolean vibrate) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (player != null) player.release();
        if (uri != null) player = createPlayer(uri);

        int ringerMode = audioManager.getRingerMode();

        if (shouldVibrate(context, player, ringerMode, vibrate)) {
            vibrator.vibrate(VIBRATE_PATTERN, 1);
        }

        if (player != null && ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            try {
                if (!player.isPlaying()) {
                    player.prepare();
                    player.start();
                } else {
                }
            } catch (IllegalStateException | IOException e) {
                Log.e(TAG, "", e);
                player = null;
            }
        } else {
            Log.w(TAG, "Not ringing, mode: " + ringerMode);
        }
    }

    void stop() {
        if (player != null) {
            Log.w(TAG, "Stopping ringer");
            player.release();
            player = null;
        }

        Log.w(TAG, "Cancelling vibrator");
        vibrator.cancel();
    }

    private boolean shouldVibrate(Context context, MediaPlayer player, int ringerMode, boolean vibrate) {
        if (player == null) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return shouldVibrateNew(context, ringerMode, vibrate);
        } else {
            return shouldVibrateOld(context, vibrate);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean shouldVibrateNew(Context context, int ringerMode, boolean vibrate) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator == null || !vibrator.hasVibrator()) {
            return false;
        }

        if (vibrate) {
            return ringerMode != AudioManager.RINGER_MODE_SILENT;
        } else {
            return ringerMode == AudioManager.RINGER_MODE_VIBRATE;
        }
    }

    private boolean shouldVibrateOld(Context context, boolean vibrate) {
        AudioManager audioManager = KavenegarAudioManager.getAudioManager(context);
        return vibrate && audioManager.shouldVibrate(AudioManager.VIBRATE_TYPE_RINGER);
    }

    private MediaPlayer createPlayer(Uri ringtoneUri) {
        try {

            MediaPlayer mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnErrorListener(new MediaPlayerErrorListener());
            mediaPlayer.setDataSource(context, ringtoneUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);

            return mediaPlayer;
        } catch (Exception e) {
            Log.e(TAG, "FAILED to create player for incoming call ringer");
            return null;
        }
    }


    private class MediaPlayerErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.w(TAG, "onError(" + mp + ", " + what + ", " + extra);
            player = null;
            return false;
        }
    }

}