package com.example.wakeup;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

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
                    Toast.makeText(getApplicationContext(), "Waking Screen Up", Toast.LENGTH_LONG).show();
                    wakeupScreen();
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
        PowerManager.WakeLock screenLock = null;
        if ((getSystemService(POWER_SERVICE)) != null) {
            screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            screenLock.acquire(10 * 60 * 1000L /*10 minutes*/);


            screenLock.release();
        }
    }


}
