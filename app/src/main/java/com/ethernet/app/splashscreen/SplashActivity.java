package com.ethernet.app.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ethernet.app.R;
import com.ethernet.app.global.PreferenceManager;
import com.ethernet.app.mainscreen.activity.MainActivity;
import com.ethernet.app.registerdevicescreen.activity.DeviceActivity;
import com.ethernet.app.settingscreen.activity.SettingActivity;
import com.ethernet.app.utility.Constant;

public class SplashActivity extends AppCompatActivity {

    private final static String TAG = SplashActivity.class.getSimpleName();
    private String deviceId = Constant.IS_EMPTY;
    private String ipAddress = Constant.IS_EMPTY;
    private String startSettingActivity = Constant.IS_EMPTY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final TextView deviceIdTextView = findViewById(R.id.tv_deviceid);

        ipAddress =  PreferenceManager.getStringForKey(this, Constant.DU_Setting.IP_ADDRESS, Constant.IS_EMPTY);

        deviceId = PreferenceManager.getStringForKey(this, Constant.DEVICE_ID, Constant.IS_EMPTY);
        Log.e(TAG, "DEVICE ID : " + deviceId);
        if (!deviceId.isEmpty()) {
            deviceIdTextView.setText(deviceId);
        }

        Bundle intent = getIntent().getExtras();
        if(intent !=null){
            Log.e("SplashActivity","intent:" +"Not null");
            startSettingActivity  = intent.getString("startSettingActivity");
            Log.e("SplashActivity","Activity:" + startSettingActivity);
        }

        Handler delayHandler = new Handler();
        delayHandler.postDelayed(() -> {
            if("SettingActivity".equals(startSettingActivity)){
                startActivity(new Intent(SplashActivity.this, SettingActivity.class));
                finish();
            }else {
                if(!ipAddress.isEmpty()) {
                    if(deviceId.isEmpty()){
                        Intent i = new Intent(SplashActivity.this, DeviceActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"DU Setting not available",Toast.LENGTH_LONG).show();
                }
            }

        }, 5000);
    }
}
