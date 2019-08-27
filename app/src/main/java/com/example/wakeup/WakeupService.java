package com.example.wakeup;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Objects;

public class WakeupService extends Service {

    //TODO: FIX BUG - onDestroy Called Automatically, Killing my service.

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
        Log.d(TAG, "onCreate: New Service Has been created");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStart: Starting Wakeup Service");
        startWakeUp();
        Log.d(TAG, "onStart: Wakeup Service has Been started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Killing Wakeup Service");
        killWakeUp();
        Log.d(TAG, "onDestroy: Wakeup Service successfully killed");
    }

    private void killWakeUp() {
        if (proximitySensorListener != null) {
            sensorManager.unregisterListener(proximitySensorListener);
            sensorManager = null;
        }
    }

    private void startWakeUp() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        if (proximitySensor == null) {
            ToastUtils.longToast(this, "Proximity Sensor not available");
            sendIntentToCloseApp();
        }

        proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                if (sensorEvent.values[0] < proximitySensor.getMaximumRange()) {
                    if (powerManager != null) {
                        Log.d(TAG, "onSensorChanged: Is Screen ON?: " + powerManager.isScreenOn());
                        if (!powerManager.isScreenOn()) {
//                            wakeupScreen();
                            turnScreenOn();
                        }
                    } else {
//                        wakeupScreen();
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

    private void sendIntentToCloseApp() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(WakeupService.this);
        localBroadcastManager.sendBroadcast(new Intent(
                "com.wakeup.action.close"
        ));
    }

    @SuppressLint("InvalidWakeLockTag")
    private void wakeupScreen() {
        PowerManager.WakeLock screenLock = ((PowerManager) Objects.requireNonNull(getSystemService(POWER_SERVICE))).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Wake Lock");
        screenLock.acquire();

        screenLock.release();
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
                        fullWakeLock.acquire(); // turn on
                        try {
                            Thread.sleep(20000); // turn on duration
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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