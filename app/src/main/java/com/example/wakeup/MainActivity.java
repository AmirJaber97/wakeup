package com.example.wakeup;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener proximitySensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startWakeUp();
    }

    private void startWakeUp() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor == null) {
            Toast.makeText(this, "Proximity Sensor not available", Toast.LENGTH_LONG).show();
            finish();
        }

        proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.values[0] < proximitySensor.getMaximumRange()) {
                    // WAKE UP
                    // CHECK IF SCREEN IS ON OR OFF TO NOT CALL THE FUNCTION ALL THE TIME
                    // MAKE WAKEUP A SERVICE https://examples.javacodegeeks.com/android/core/service/android-service-example/
                    // MAKE IT LOAD IN A SMALL INTERFACE, ALLOW ADDING THE WIDGET & ENABLE/DISABLE SERVICE
                    PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
                        boolean isScreenOn = powerManager.isInteractive();
                        if (!isScreenOn) {
                            Toast.makeText(getApplicationContext(), "Waking Screen Up", Toast.LENGTH_LONG).show();
                            wakeupScreen();
                        } else {
                            Log.d(TAG, "onSensorChanged: Screen is Already On");
                        }
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

    @SuppressLint("InvalidWakeLockTag")
    private void wakeupScreen() {
        PowerManager.WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();

        screenLock.release();
    }


}
