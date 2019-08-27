package com.example.wakeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.wakeup.action.close")) {
                finish();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.wakeup.action.close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);

    }

    public void startNewService(View view) {
        Log.d(TAG, "startNewService: Start Wakeup Service Called");
        startService(new Intent(this, WakeupService.class));
        Log.d(TAG, "startNewService: Started Wakeup Service");
    }

    public void stopNewService(View view) {
        Log.d(TAG, "stopNewService: Stop Wakeup Service Called");
        stopService(new Intent(this, WakeupService.class));
        Log.d(TAG, "stopNewService: Stopped Wakeup Service");
    }

    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }
}
