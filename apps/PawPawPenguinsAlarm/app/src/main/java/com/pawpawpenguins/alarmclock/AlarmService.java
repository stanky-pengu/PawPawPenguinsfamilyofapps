package com.pawpawpenguins.alarmclock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

public class AlarmService extends Service {
    static final String ACTION_START = "com.pawpawpenguins.alarmclock.START";
    static final String ACTION_STOP = "com.pawpawpenguins.alarmclock.STOP";
    private static final String CHANNEL = "pawpaw_alarm_channel";
    private Ringtone ringtone;
    private MediaPlayer customPlayer;

    @Override public void onCreate() { super.onCreate(); createChannel(); }
    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) { stopSelf(); return START_NOT_STICKY; }
        Intent open = new Intent(this, AlarmActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent openPi = PendingIntent.getActivity(this, 810, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Intent stop = new Intent(this, AlarmService.class).setAction(ACTION_STOP);
        PendingIntent stopPi = PendingIntent.getService(this, 811, stop, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder = new Notification.Builder(this, CHANNEL)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("PawPawPenguins Alarm")
                .setContentText("Time to wake up!").setContentIntent(openPi).setOngoing(true)
                .addAction(new Notification.Action.Builder(android.R.drawable.ic_lock_idle_alarm, "Dismiss", stopPi).build());
        startForeground(811, builder.build());
        playSelectedRingtone();
        return START_STICKY;
    }
    private void playSelectedRingtone() {
        stopRingtone();
        AlarmStore store = new AlarmStore(this);
        String picked = store.ringtoneUri();
        if (picked != null) {
            try {
                customPlayer = new MediaPlayer();
                customPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
                customPlayer.setDataSource(this, Uri.parse(picked));
                customPlayer.setLooping(true);
                customPlayer.setOnPreparedListener(MediaPlayer::start);
                customPlayer.setOnErrorListener((player, what, extra) -> { startDefaultRingtone(); return true; });
                customPlayer.prepareAsync();
                return;
            } catch (Exception ignored) { releaseCustomPlayer(); }
        }
        startDefaultRingtone();
    }
    private void startDefaultRingtone() {
        stopRingtone();
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            ringtone = RingtoneManager.getRingtone(this, uri);
            if (ringtone == null) throw new IllegalStateException("No ringtone player");
            if (Build.VERSION.SDK_INT >= 21) ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
            if (Build.VERSION.SDK_INT >= 28) ringtone.setLooping(true);
            ringtone.play();
        } catch (Exception e) {
            Uri fallback = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            try { ringtone = RingtoneManager.getRingtone(this, fallback); if (ringtone != null) ringtone.play(); } catch (Exception ignored) { }
        }
    }
    private void stopRingtone() { if (ringtone != null) { try { ringtone.stop(); } catch (Exception ignored) { } ringtone = null; } releaseCustomPlayer(); }
    private void releaseCustomPlayer() { if (customPlayer != null) { try { if (customPlayer.isPlaying()) customPlayer.stop(); } catch (Exception ignored) { } try { customPlayer.release(); } catch (Exception ignored) { } customPlayer = null; } }
    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL, "Alarm notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Alarm alerts and dismissal controls");
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
    }
    @Override public void onDestroy() { stopRingtone(); stopForeground(STOP_FOREGROUND_REMOVE); super.onDestroy(); }
    @Override public IBinder onBind(Intent intent) { return null; }
}
