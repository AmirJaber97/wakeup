package com.example.wakeup;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class WakeupService extends Service {

    /*
    TODO: Change Button text from Service intent (Especially when clicked from Notification)
     */

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";


    private static final String TAG = "WakeupService";
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener proximitySensorListener;

    public WakeupService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStart: Intent received");

        if (intent != null) {
            String action = intent.getAction();

            if (action != null) {
                switch (action) {
                    case ACTION_START_FOREGROUND_SERVICE:
                        startWakeUp();
                        break;
                    case ACTION_STOP_FOREGROUND_SERVICE:
                        killWakeUp();
                        break;
                }
            }
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Intent restartService = new Intent("RestartService");
        sendBroadcast(restartService);
        Log.d(TAG, "onDestroy: Service stopped successfully");
    }

    private void startWakeUp() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            showNotification();
        }

        if (proximitySensor == null) {
            ToastUtils.longToast(this, "Proximity Sensor not available");
            killWakeUp();
        }

        proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                if (sensorEvent.values[0] < proximitySensor.getMaximumRange()) {
                    if (powerManager != null) {
                        Log.d(TAG, "onSensorChanged: Is Screen ON?: " + powerManager.isScreenOn());
                        if (!powerManager.isScreenOn()) {
                            turnScreenOn();
                        }
                    } else {
                        turnScreenOn();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(proximitySensorListener, proximitySensor,
                2 * 1000 * 1000);

    }

    private void showNotification() {
        String NOTIFICATION_CHANNEL_ID = "com.jeee.wakeup";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        Intent stopIntent = new Intent(this, WakeupService.class);
        stopIntent.setAction(ACTION_STOP_FOREGROUND_SERVICE);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, 0);
        NotificationCompat.Action stopAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Turn off", pendingStopIntent);

        Intent myIntent = new Intent(this, MainActivity.class);
        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                myIntent,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Wave to Wake up is now Active")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .addAction(stopAction)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    private void killWakeUp() {
        if (proximitySensorListener != null) {
            sensorManager.unregisterListener(proximitySensorListener);
            sensorManager = null;
        }
        stopForeground(true);
        stopSelf();
    }

    @SuppressLint("StaticFieldLeak")
    private void turnScreenOn() {
        new AsyncTask<Void, Void, Exception>() {
            @SuppressLint({"InvalidWakeLockTag", "WakelockTimeout"})
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock fullWakeLock = null;
                    if (powerManager != null) {
                        fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
                    }
                    if (fullWakeLock != null) {
                        fullWakeLock.acquire();
                        fullWakeLock.release();
                    }

                } catch (Exception e) {
                    return e;
                }
                return null;
            }
        }.execute();
    }


}