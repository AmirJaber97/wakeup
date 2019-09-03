package com.example.wakeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public Button startStopButton;
    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.wakeup.action.close")) {
                finish();
            }
        }
    };
    private boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startStopButton = findViewById(R.id.startStopButton);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.wakeup.action.close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    public void startStopWakeupService(View view) {
        String startText = getResources().getString(R.string.start_wakeup);
        String stopText = getResources().getString(R.string.stop_wakeup);

        if (startStopButton.getText().equals(startText)) {
            Intent intent = new Intent(this, WakeupService.class);
            intent.setAction(WakeupService.ACTION_START_FOREGROUND_SERVICE);
            startService(intent);
            Log.d(TAG, "startStopWakeupService: Started Wakeup Service");
            startStopButton.setText(stopText);
        } else {
            Intent intent = new Intent(this, WakeupService.class);
            intent.setAction(WakeupService.ACTION_STOP_FOREGROUND_SERVICE);
            startService(intent);
            Log.d(TAG, "stopWakeupService: Stopped Wakeup Service");
            startStopButton.setText(startText);
        }

    }

    public void stopWakeupService(View view) {

    }

    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }
}
