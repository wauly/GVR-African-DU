package com.ethernet.app.mainscreen.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ethernet.app.R;
import com.ethernet.app.global.BaseAppCompatActivity;
import com.ethernet.app.global.DatabaseHandler;
import com.ethernet.app.global.EventMessage;
import com.ethernet.app.global.GlobalBus;
import com.ethernet.app.global.PreferenceManager;
import com.ethernet.app.mainscreen.asynctask.DeleteImageAndVideoAsyncTask;
import com.ethernet.app.mainscreen.asynctask.GetContentAsyncTask;
import com.ethernet.app.mainscreen.asynctask.SaveContentUrlAsyncTask;
import com.ethernet.app.mainscreen.asynctask.UpdateContentFlgTask;
import com.ethernet.app.mainscreen.fragmnet.HorizontalPaymentFuelFragment;
import com.ethernet.app.mainscreen.fragmnet.VerticalAddFragment;
import com.ethernet.app.permission.PermissionsHelper;
import com.ethernet.app.service.EthernetService;
import com.ethernet.app.utility.Constant;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

public class MainActivity extends BaseAppCompatActivity implements
        PermissionsHelper.GetPermissionResultListener,
        GetContentAsyncTask.GetContentDateListener,
        SaveContentUrlAsyncTask.SaveContentUrlListener {

    private final String TAG = MainActivity.class.getSimpleName();
    //classes
    private PermissionsHelper permissionsHelper;
    private DatabaseHandler database;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    //views
    private ImageView logoImageView;
    private FrameLayout frameLayout;
    //variables
    private int offlineIconDelayTime = 5000; //in Sec
    private String deviceId = "5L2RWK";
    // temporary variable
    private boolean isFirstTime;
    public static final String BROADCAST_ACTION = "SEND_DATA";
    public static final String BROADCAST_ACTION_TOAST = "SEND_TOAST";

    private MyBroadCastReceiver myBroadCastReceiver;
    private ToastBroadCastReceiver toastBroadCastReceiver;
    private boolean stopToSendingData = true;


    @Override
    protected void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DatabaseHandler(myContext);

        initView();
        setListener();
        isPermissionsGranted();
        startMyService();
        registerMyReceiver();
        registerToastReceiver();

    }

    @Override
    public void initView() {
        myBroadCastReceiver = new MyBroadCastReceiver();
        toastBroadCastReceiver = new ToastBroadCastReceiver();
        frameLayout = findViewById(R.id.frame_layout);
        logoImageView = findViewById(R.id.logo_image_view);
    }

    @Override
    public void setListener() {

    }

    private void startMyService() {
        try {
            Intent myServiceIntent = new Intent(this, EthernetService.class);
            startService(myServiceIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Check all requires permission here
    private void checkPermissions() {
        permissionsHelper = new PermissionsHelper(this);
        permissionsHelper.checkAndRequestPermissions(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void isPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                callGetContentApi();
            } else {
                checkPermissions();
            }
        } else {
            callGetContentApi();
        }
    }

    //get data from server method name is  :- GET_CONTENT
    private void callGetContentApi() {
        isFirstTime = PreferenceManager.getBooleanForKey(getApplicationContext(),
                "isFirstTime", false);
        if (!isFirstTime) {
            GetContentAsyncTask task = new GetContentAsyncTask(this, database, myContext);
            task.execute(deviceId);
        } else {
            logoImageView.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            addFragment(new VerticalAddFragment(), Constant.ScreenType.VERTICAL_ADD);
        }

    }

    private void saveUrlInLocal() {
        SaveContentUrlAsyncTask task = new SaveContentUrlAsyncTask(this, database.getAllContent());
        task.execute();
    }

    // TODO Call Device CONTENT FLG UPDATE Webservice
    public void updateContentFlg() {
        if (!deviceId.isEmpty()) {
            //Log.e(TAG, "updateContentFlg");
            new UpdateContentFlgTask().execute(deviceId);
        }
    }

    private void deletedImageAndVideoFromLocal() {

        new DeleteImageAndVideoAsyncTask().execute(database);
    }

    //load fragment
    private void addFragment(Fragment fragment, String tag) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();

    }

    public void clearStack() {
        //Here we are clearing back stack fragment entries
        int backStackEntry = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                getSupportFragmentManager().popBackStackImmediate();
            }
        }

        //Here we are removing all the fragment that are shown here
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
                Fragment mFragment = getSupportFragmentManager().getFragments().get(i);
                if (mFragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    // PermissionsHelper.GetPermissionResultListener
    @Override
    public void didReceivedPermission(String status) {
        if (status.equals(Constant.ALLOW)) {
            callGetContentApi();
        } else {
            // if user deny then this code run
            checkPermissions();
        }
    }

    //GetContentAsyncTask.GetContentDateListener
    @Override
    public void didReceivedGetContentDate(String status, int isContentAvailable) {
        if (status.equals(Constant.SUCCESS)) {
            if (isContentAvailable == Constant.NO_CONTENT_AVAILABLE) {
                logoImageView.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.GONE);
            } else {
                saveUrlInLocal();
            }

        }
    }

    //SaveContentUrlAsyncTask.SaveContentUrlListener
    @Override
    public void didReceivedSaveContentUrlDate(String status) {
        PreferenceManager.saveBooleanForKey(getApplicationContext(), "isFirstTime", true);
        if (status.equals(Constant.SUCCESS) || status.equals(Constant.IS_EMPTY)) {
            updateContentFlg();
            addFragment(new VerticalAddFragment(), Constant.ScreenType.VERTICAL_ADD);
            logoImageView.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
        }
        deletedImageAndVideoFromLocal();
    }


    @Subscribe
    public void getEventMessage(EventMessage message) {
        String data = message.getEventMessage();
        if (data.equalsIgnoreCase(Constant.STOP_FUELING)) {
            if(stopToSendingData){
                clearStack();
                addFragment(new VerticalAddFragment(), Constant.ScreenType.VERTICAL_ADD);
                stopToSendingData = false;
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister(this);
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        } else if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * This method is responsible to register an action to BroadCastReceiver
     */
    private void registerMyReceiver() {

        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BROADCAST_ACTION);
            registerReceiver(myBroadCastReceiver, intentFilter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    /**
     * MyBroadCastReceiver is responsible to receive broadCast from register action
     */
    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, "onReceive() called");
                String data = intent.getStringExtra(Constant.DATA);
                //Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
                if (!data.isEmpty() && data != null) {
                    JSONObject jObj = new JSONObject(data);
                    if (jObj.has("newState")) {
                        String firstTimeCall = jObj.getString("newState");
                        if (firstTimeCall.equalsIgnoreCase("calling")) {
                            stopToSendingData = true;
                            clearStack();
                            addFragment(new HorizontalPaymentFuelFragment(), Constant.ScreenType.HORIZONTAL_ADD);
                        }
                    }

                    GlobalBus.getBus().post(new EventMessage(data));

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class ToastBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, "onReceive() called");
                String data = intent.getStringExtra(Constant.DATA);
                Toast.makeText(context, data, Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void registerToastReceiver() {

        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BROADCAST_ACTION_TOAST);
            registerReceiver(toastBroadCastReceiver, intentFilter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


}
