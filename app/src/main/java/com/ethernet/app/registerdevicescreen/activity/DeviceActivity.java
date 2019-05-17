package com.ethernet.app.registerdevicescreen.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ethernet.app.R;
import com.ethernet.app.global.BaseAppCompatActivity;
import com.ethernet.app.global.PreferenceManager;
import com.ethernet.app.mainscreen.activity.MainActivity;
import com.ethernet.app.registerdevicescreen.asynctask.DeviceVerifyAsyncTask;
import com.ethernet.app.utility.Constant;
import com.ethernet.app.utility.NetworkUtil;

public class DeviceActivity extends BaseAppCompatActivity implements
        View.OnClickListener, DeviceVerifyAsyncTask.GetDeviceVerifyListener {

    public static final String TAG = DeviceActivity.class.getSimpleName();
    private static long back_pressed;
    private ProgressDialog progressDialog;

    // TODO views
    private EditText deviceIdEditText;
    private TextView successResultTextView;
    private TextView errorResultTextView;
    private Button verifyButton;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initView();
        setListener();

    }

    //BaseAppCompatActivity
    @Override
    public void initView() {
        deviceIdEditText = findViewById(R.id.device_id_edit_text);
        successResultTextView = findViewById(R.id.success_result_text_view);
        successResultTextView.setText("");
        errorResultTextView = findViewById(R.id.error_result_text_view);
        errorResultTextView.setText("");
        verifyButton = findViewById(R.id.verify_button);
        continueButton = findViewById(R.id.continue_button);
        continueButton.setVisibility(View.GONE);


    }

    @Override
    public void setListener() {
        verifyButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
    }


    // TODO Function to check internet connection
    public Boolean checkInternetConnection() {
        int status = NetworkUtil.getConnectivityStatusString(getApplicationContext());
        return status != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED;

    }

    // TODO device verify  Functionality
    private void deviceVerify() {
        errorResultTextView.setText("");
        successResultTextView.setText("");
        continueButton.setVisibility(View.GONE);
        if (!validate()) {
            verifyButton.setEnabled(true);
        } else {
            verifyButton.setEnabled(false);
            verifyButton.setText(Constant.VERIFY);
            showProgressBar();
            new DeviceVerifyAsyncTask(this).execute(deviceIdEditText.getText().toString());
        }
    }

    //View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verify_button:
                if (checkInternetConnection()) {
                    deviceVerify();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_network_msg, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.continue_button:
                navigateToMainActivity();
                break;
        }
    }

    //DeviceVerifyAsyncTask.GetDeviceVerifyListener
    @Override
    public void didReceivedDeviceVerifyResult(String result) {
        hideProgressBar();
        if (result.equals(Constant.NOT_HTTP_CONNECTION)) {
            verifyButton.setEnabled(true);
        } else if (result.equals(Constant.SUCCESS)) {
            PreferenceManager.saveStringForKey(myContext, Constant.DEVICE_ID, deviceIdEditText.getText().toString());
            deviceIdEditText.setVisibility(View.GONE);
            successResultTextView.setText(R.string.id_verified_successfully);
            successResultTextView.setVisibility(View.VISIBLE);
            continueButton.setVisibility(View.VISIBLE);
            verifyButton.setVisibility(View.GONE);
        } else if (result.equals(Constant.FAIL)) {
            verifyButton.setEnabled(true);
            errorResultTextView.setText(R.string.id_verified_fail);
        } else if (result.equals(Constant.RESPONSE_FAIL)) {
            verifyButton.setEnabled(true);
            errorResultTextView.setText(R.string.id_verified_unable);
        } else if (result.equals(Constant.PROCESS_ERROR)) {
            verifyButton.setEnabled(true);
            errorResultTextView.setText(R.string.id_verified_some_error);
        }


    }

    private void showProgressBar() {
        progressDialog = new ProgressDialog(DeviceActivity.this, R.style.MyMaterialThemeDialog);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Verifying...:");
        progressDialog.show();
    }

    private void hideProgressBar() {
        progressDialog.dismiss();
    }

    protected void navigateToMainActivity() {
        errorResultTextView.setText("");
        Intent intent = new Intent(myContext, MainActivity.class);
        startActivity(intent);
        finish();
    }


    //Validation Logic
    public boolean validate() {
        if (deviceIdEditText.getText().toString().isEmpty()) {
            deviceIdEditText.setError("Please Enter Device ID");
            return false;
        } else if (deviceIdEditText.getText().toString().length() < 6 || deviceIdEditText.getText().toString().length() > 6) {
            deviceIdEditText.setError("Please Enter Valid Device ID");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }
}
