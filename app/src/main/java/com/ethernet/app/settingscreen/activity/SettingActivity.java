package com.ethernet.app.settingscreen.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ethernet.app.R;
import com.ethernet.app.global.BaseAppCompatActivity;
import com.ethernet.app.global.PreferenceManager;
import com.ethernet.app.utility.Constant;


public class SettingActivity extends BaseAppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    private static final String TAG = SettingActivity.class.getSimpleName();

    final String[] fuel_type = new String[]{"Select fuel type", "PMS", "AGO"};
    final String[] fp_type = new String[]{"Select fp type", "1", "2"};
    final String[] screen_sleep_time = new String[]{"Select sleep time", "30000", "20000"};

    private Spinner fuelTypeSpinner,fpTypeSpinner,sleepTimeSpinner;

    private ArrayAdapter<String> adapterFuelType,adapterFpType,adapterScreenSleepTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        setListener();
        //Fuel Type
        adapterFuelType = new ArrayAdapter<>(SettingActivity.this, android.R.layout.simple_spinner_item, fuel_type);
        adapterFuelType.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        fuelTypeSpinner.setAdapter(adapterFuelType);

        // FP type
        adapterFpType = new ArrayAdapter<>(SettingActivity.this, android.R.layout.simple_spinner_item, fp_type);
        adapterFpType.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        fpTypeSpinner.setAdapter(adapterFpType);

        // Screen sleep time
        adapterScreenSleepTime = new ArrayAdapter<>(SettingActivity.this, android.R.layout.simple_spinner_item, screen_sleep_time);
        adapterScreenSleepTime.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sleepTimeSpinner.setAdapter(adapterScreenSleepTime);



    }
    @Override
    public void initView() {
        fuelTypeSpinner = findViewById(R.id.fuel_type_spinner);
        fpTypeSpinner = findViewById(R.id.fp_type_spinner);
        sleepTimeSpinner = findViewById(R.id.sleep_time_spinner);

    }

    @Override
    public void setListener() {
        fuelTypeSpinner.setOnItemSelectedListener(this);
        fpTypeSpinner.setOnItemSelectedListener(this);
        sleepTimeSpinner.setOnItemSelectedListener(this);
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        final String fuel_type = fuelTypeSpinner.getSelectedItem().toString();
        final String fp_type = fpTypeSpinner.getSelectedItem().toString();
        final String sleep_time = sleepTimeSpinner.getSelectedItem().toString();


        int sp_id = parent.getId();
        switch (sp_id) {
            case R.id.fuel_type_spinner:
                if (!fuel_type.equals("Select fuel type")) {
                    fuelTypeSpinner.setSelection(position, false);
                    PreferenceManager.saveStringForKey(this, Constant.DU_Setting.FUEL_TYPE, parent.getItemAtPosition(position).toString().trim());
                    adapterFuelType.notifyDataSetChanged();

                }
                break;
            case R.id.fp_type_spinner:
                if (!fp_type.equals("Select fp type")) {
                    fpTypeSpinner.setSelection(position, false);
                    PreferenceManager.saveStringForKey(this, Constant.DU_Setting.FP_TYPE, parent.getItemAtPosition(position).toString().trim());
                    adapterFpType.notifyDataSetChanged();
                }

                break;
            case R.id.sleep_time_spinner:
                if (!sleep_time.equals("Select sleep time")) {
                    sleepTimeSpinner.setSelection(position, false);
                    final String sleepTime = parent.getItemAtPosition(position).toString().trim();
                    PreferenceManager.saveIntForKey(this, Constant.DU_Setting.SLEEP_TIME,Integer.parseInt(sleepTime));
                    adapterScreenSleepTime.notifyDataSetChanged();
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}



















