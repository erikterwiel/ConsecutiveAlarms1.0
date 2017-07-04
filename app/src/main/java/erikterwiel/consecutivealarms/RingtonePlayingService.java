package erikterwiel.consecutivealarms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Erik on 3/8/2017.
 */

public class RingtonePlayingService extends Service {
    private MediaPlayer mPlayer;
    private NotificationManager mNM;
    private PowerManager.WakeLock screenOn;
    private Vibrator vibrator;
    private CountDownTimer timer;
    private CountDownTimer screenTimer;
    private long[] vibratePattern = {0,1000,1000};

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("LocalService", "Recieved start id  " + startId + ": " + intent);
        String alarmString = intent.getStringExtra("alarmTone");
        int alarmNumber = intent.getIntExtra("alarmNumber", 0);
        int alarmID = intent.getIntExtra("alarmID", -1);
        boolean alarmVibrate = intent.getBooleanExtra("alarmVibrate", false);
        Uri alarm = Uri.parse(alarmString);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (alarmNumber == 0) {
            try {
                Log.e("Alarm type playing: ", "Snooze alarms");
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(this, alarm);
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
                mPlayer.prepare();
                mPlayer.start();
                int requestID = (int) System.currentTimeMillis();
                Intent stopAlarmIntent = new Intent(this, NotificationReceiver.class);
                stopAlarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                stopAlarmIntent.putExtra("startCode", 0);
                PendingIntent stopAlarmPendingIntent = PendingIntent.getBroadcast
                        (this, requestID, stopAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification alarmNotification = new Notification.Builder(this)
                        .setCategory(Notification.CATEGORY_ALARM)
                        .setSmallIcon(R.drawable.ic_notifications_white_48dp)
                        .setContentIntent(stopAlarmPendingIntent)
                        .setContentTitle("Consecutive alarm is going off")
                        .setContentText("Press to stop the alarm")
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVibrate(new long[0])
                        .build();
                mNM.notify(100, alarmNotification);
                screenOn = ((PowerManager) getSystemService(POWER_SERVICE))
                        .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "examples");
                screenOn.acquire();
                if (alarmVibrate) {
                    Log.e("RingtonePlayingService", "Phone vibration activated");
                    vibrator.vibrate(vibratePattern, 0);
                }
                SharedPreferences sharedPreferences = getSharedPreferences("AlarmDatabase", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("alarmIDToDelete", 1000001);
                editor.commit();
                screenTimer = new CountDownTimer(10000, 10000) {
                    @Override
                    public void onTick(long millisUntilFinished) {}

                    @Override
                    public void onFinish() {
                        screenOn.release();
                    }
                };
                timer = new CountDownTimer(30000, 30000) {
                    @Override
                    public void onTick(long millisUntilFinished) {}

                    @Override
                    public void onFinish() {
                        mPlayer.stop();
                        mPlayer.release();
                        vibrator.cancel();
                        mNM.cancel(100);
                    }
                };
                timer.start();
                screenTimer.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                Log.e("Alarm type playing: ", "Final");
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(this, alarm);
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
                mPlayer.prepare();
                mPlayer.start();

                int requestID = (int) System.currentTimeMillis();
                Intent stopAlarmIntent = new Intent(this, NotificationReceiver.class);
                stopAlarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent stopAlarmPendingIntent = PendingIntent.getBroadcast
                        (this, requestID, stopAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification alarmNotification = new Notification.Builder(this)
                        .setCategory(Notification.CATEGORY_ALARM)
                        .setSmallIcon(R.drawable.ic_notifications_white_48dp)
                        .setContentIntent(stopAlarmPendingIntent)
                        .setContentTitle("Final alarm is going off")
                        .setContentText("Press to stop the alarm")
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVibrate(new long[0])
                        .build();
                mNM.notify(requestID, alarmNotification);
                screenOn = ((PowerManager) getSystemService(POWER_SERVICE))
                        .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "examples");
                screenOn.acquire();
                if (alarmVibrate) {
                    Log.e("RingtonePlayingService", "Phone vibration activated");
                    vibrator.vibrate(vibratePattern, 0);
                }
                Log.e("RingtonePlayingService", "Alarm ID " + alarmID + " playing");
                SharedPreferences sharedPreferences = getSharedPreferences("AlarmDatabase", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("alarmIDToDelete", alarmID);
                editor.commit();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("RingtonePlayingService", "onDestroy() called");
        mPlayer.stop();
        mPlayer.release();
        screenOn.release();
        vibrator.cancel();
        if (timer != null) {
            timer.cancel();
            screenTimer.cancel();
        }
    }
}
