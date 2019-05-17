package com.ethernet.app.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ethernet.app.R;
import com.ethernet.app.global.PreferenceManager;
import com.ethernet.app.mainscreen.activity.MainActivity;
import com.ethernet.app.registerdevicescreen.activity.DeviceActivity;
import com.ethernet.app.settingscreen.activity.SettingActivity;
import com.ethernet.app.utility.Constant;

public class SplashActivity extends AppCompatActivity {

    private String deviceId;
    private String startSettingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final TextView deviceIdTextView = findViewById(R.id.tv_deviceid);

        deviceId = PreferenceManager.getStringForKey(this, Constant.DEVICE_ID, Constant.IS_EMPTY);
        if (!deviceId.isEmpty()) {
            deviceIdTextView.setText(deviceId);
        }
        Bundle intent = getIntent().getExtras();
        if(intent !=null){
            startSettingActivity  = intent.getString("startSettingActivity");
        }

        Handler delayHandler = new Handler();
        delayHandler.postDelayed(() -> {
            /*if(startSettingActivity.equalsIgnoreCase("SettingActivity")){
                startActivity(new Intent(SplashActivity.this, SettingActivity.class));
            }else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            finish();*/


           if(deviceId.isEmpty()){
               Intent i = new Intent(SplashActivity.this, DeviceActivity.class);
               startActivity(i);
               finish();
           }else {
               Intent i = new Intent(SplashActivity.this, MainActivity.class);
               startActivity(i);
               finish();
           }

        }, 5000);
    }
}
