package com.ethernet.app.mainscreen.fragmnet;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ethernet.app.R;
import com.ethernet.app.global.BaseFragment;
import com.ethernet.app.global.DatabaseHandler;
import com.ethernet.app.global.EventMessage;
import com.ethernet.app.global.FullScreenVideoView;
import com.ethernet.app.global.GPSTracker;
import com.ethernet.app.global.GlobalBus;
import com.ethernet.app.global.PreferenceManager;
import com.ethernet.app.mainscreen.asynctask.OffLineDevicePingAsyncTask;
import com.ethernet.app.mainscreen.asynctask.OffLineLoopContentPingProcessTask;
import com.ethernet.app.mainscreen.asynctask.OnLineDevicePingAsyncTask;
import com.ethernet.app.mainscreen.asynctask.OnLineLoopContentPingProcessTask;
import com.ethernet.app.mainscreen.model.ContentDataModel;
import com.ethernet.app.mainscreen.model.DevicePingModel;
import com.ethernet.app.utility.Constant;
import com.ethernet.app.utility.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class VerticalAddFragment extends BaseFragment implements
        OnLineDevicePingAsyncTask.GetOnLineDevicePingListener,
        OffLineDevicePingAsyncTask.GetOffLineDevicePingListener{

    private static final String TAG = VerticalAddFragment.class.getSimpleName();

    public static final String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
    public static final File folder = new File(extStorageDirectory, "waulypod-resource");

    //views
    private ImageView offLineIconImageView;
    private ImageView logoImageView;
    private ImageView sliderImageView;
    private FullScreenVideoView sliderVideoView;
    // Classes
    private DatabaseHandler database;
    private ArrayList<ContentDataModel> listOfContent;
    private ArrayList<Integer> storeLoopIdList = new ArrayList<>();
    private DevicePingModel devicePingModel;
    private GPSTracker gps;
    private Handler offLineIconHandler;

    //variable
    private String deviceId;
    private int position;
    private String nowPlaying = ""; // This parameter take LOOP_CONTENT # DURATION;
    private long isOffline = 0;
    private long offlineTime = 0;
    private int viewLoopCount = 0;
    private int pingCallDelay = 4000;
    private int offlineIconDelayTime = 5000; //in Sec
    private int storeLoopId = 0;
    private boolean isDateSaved = false;
    private int contentCounter = 0; //  this Counter used in case of one loop count

    private Context thisContext;


    public VerticalAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advertisement, container, false);

        thisContext = getContext();
        database = new DatabaseHandler(thisContext);
        gps = new GPSTracker(thisContext);

        initView(view);
        setListener(view);

        loadContentData();

        return view;
    }

    @Override
    public void initView(View view) {
        //deviceId = PreferenceManager.getStringForKey(thisContext, Constant.DEVICE_ID, Constant.IS_EMPTY);
        deviceId = "5L2RWK";
        offLineIconImageView = view.findViewById(R.id.off_line_icon_image_view);
        logoImageView = view.findViewById(R.id.logo_image_view);
        sliderImageView = view.findViewById(R.id.slider_image_view);
        sliderVideoView = view.findViewById(R.id.slider_video_view);
    }

    @Override
    public void setListener(View view) {

    }

    @Override
    public void onResume() {
        super.onResume();
        showOfflineIcon();
        Log.e(TAG,"onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        removeAllHandler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy()");

    }
    private void showOfflineIcon() {
        offLineIconHandler = new Handler();
        offLineIconHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkInterNetConnection()) {
                    /*isDateSaved = false;
                    offLineIconImageView.setVisibility(View.GONE);
                    String offLineTimeDate = PreferenceManager.getStringForKey(thisContext, Constant.SAVE_OFF_LINE_TIME, Constant.IS_EMPTY);
                    if (!offLineTimeDate.isEmpty()) {
                        sendOfflinePing(Constant.offLineTimeDifference(offLineTimeDate));
                        sendOfflineLoopContent();
                    }
                    PreferenceManager.saveStringForKey(thisContext, Constant.SAVE_OFF_LINE_TIME, Constant.IS_EMPTY);
                } else {
                    offLineIconImageView.setVisibility(View.VISIBLE);
                    if (!isDateSaved) {
                        PreferenceManager.saveStringForKey(thisContext, Constant.SAVE_OFF_LINE_TIME, Constant.getCurrentDate());
                        isDateSaved = true;
                    }*/
                }
                offLineIconHandler.postDelayed(this, offlineIconDelayTime);
            }
        }, offlineIconDelayTime);
    }
    private void removeAllHandler(){
        offLineIconHandler.removeCallbacks(null);
        offLineIconHandler.removeCallbacksAndMessages(null);
        pingCallHandler.removeCallbacks(null);
        pingCallHandler.removeCallbacksAndMessages(null);
        handlerSlider.removeCallbacks(null);
        handlerSlider.removeCallbacksAndMessages(null);
    }

    public Handler handlerSlider = new Handler();
    public Runnable runnableSlider = new Runnable() {
        public void run() {
            position = position + 1;
            //Log.e(TAG, "ListPosition " + position);
            if (position >= listOfContent.size()) {
                position = 0;
            }
            startSlideContent(listOfContent.get(position));
        }
    };
    public Handler pingCallHandler = new Handler();
    public Runnable pingCallRunnable = this::callPing;

    private void collectDevicePinkData() {
        devicePingModel = new DevicePingModel();
        //devicePingModel.deviceId = PreferenceManager.getStringForKey(thisContext, Constant.DEVICE_ID, Constant.IS_EMPTY);
        deviceId = "5L2RWK";
        devicePingModel.uuId = PreferenceManager.getStringForKey(thisContext, Constant.UUID, Constant.IS_EMPTY);
        devicePingModel.deviceIp = Constant.getIpAddress(thisContext);
        if (gps.canGetLocation) {
            devicePingModel.latitude = gps.latitude;
            devicePingModel.longitude = gps.longitude;
        } else {
            devicePingModel.latitude = 0.0;
            devicePingModel.longitude = 0.0;
        }
        devicePingModel.nowPlaying = nowPlaying; // send LOOP_CONTENT and add # duration
        devicePingModel.isOffline = isOffline; //  if You want to send offline pink 1 or online 0
        devicePingModel.offlineTime = offlineTime;
        devicePingModel.date = Constant.getCurrentDate();
        devicePingModel.viewContentCount = 1;
        devicePingModel.viewLoopCount = viewLoopCount;
    }

    //Function to check internet connection
    public Boolean checkInterNetConnection() {
        int status = NetworkUtil.getConnectivityStatusString(thisContext);
        return status != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED;
    }
    private void setOrientation() {
        final String orientation = PreferenceManager.getStringForKey(thisContext, Constant.API_TAG.ORIENTATION, Constant.IS_EMPTY);
        if (orientation.equalsIgnoreCase(Constant.HORIZONTAL)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation.equalsIgnoreCase(Constant.VERTICAL)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void callLoopContentPingProcessTask() {
        collectDevicePinkData();
        final OnLineLoopContentPingProcessTask task = new OnLineLoopContentPingProcessTask(devicePingModel);
        task.execute();

    }
    //  Call Device ping Webservice
    private void callDevicePinkAsyncTask() {
        collectDevicePinkData();
        OnLineDevicePingAsyncTask asyncTask = new OnLineDevicePingAsyncTask(this, devicePingModel);
        asyncTask.execute();
    }
    // collect ping data in case of offline
    private void collectAllLoopContentDataForOffline() {
        isOffline = 1;
        collectDevicePinkData();
        database.insertAllOffLineLoopContent(devicePingModel);
    }
    private void sendOfflineLoopContent() {
        ArrayList<DevicePingModel> listOfOffLineLoopContent = database.getAllOffLineLoopContent();
        if (listOfOffLineLoopContent.size() > 0) {
            for (int i = 0; i < listOfOffLineLoopContent.size(); i++) {
                //Log.e(TAG,"OFFLINE_PNK : " + i);
                OffLineLoopContentPingProcessTask asyncTask = new OffLineLoopContentPingProcessTask(listOfOffLineLoopContent.get(i), database);
                asyncTask.execute();
            }

        }
    }

    private void loadContentData() {

        if (listOfContent == null) {
            listOfContent = new ArrayList<>();
        }
        if (listOfContent.size() != 0) {
            listOfContent.clear();
        }
        listOfContent = database.getAllVerticalContent();
        for (int i = 0; i < listOfContent.size(); i++) {

            String[] separated = listOfContent.get(i).loopContent.split("#");
            if (!storeLoopIdList.contains(Integer.parseInt(separated[1]))) {
                storeLoopIdList.add(Integer.parseInt(separated[1]));
            }

            String fileName = Constant.getFileName(listOfContent.get(i).url);
            final String path = folder.toString() + "/" + fileName;
            listOfContent.get(i).url = path;
            File file = new File(folder, fileName);
            if (file.exists()) {

            }
        }
        //Log.e(TAG, "pingCallDelay TI ME :" + pingCallDelay);
        //pingCallHandler.postDelayed(pingCallRunnable, pingCallDelay);

        startSlideContent(listOfContent.get(position));
    }

    private void startSlideContent(ContentDataModel model) {
        logoImageView.setVisibility(View.GONE);
        if (Constant.someLogicBeforeStartSlider(model)) {
            String[] separated = model.loopContent.split("#");
            nowPlaying = model.loopContent + '#' + model.duration;

            if(storeLoopIdList.size() == 1){
                if(contentCounter == 0){
                    viewLoopCount = 1;
                    contentCounter++;
                }else {
                    contentCounter++;
                    viewLoopCount = 0;
                    if(contentCounter == listOfContent.size()){
                        contentCounter = 0;
                    }
                }

            }else {
                if (storeLoopId == Integer.parseInt(separated[1])) {
                    viewLoopCount = 0;
                } else {
                    viewLoopCount = 1;
                    storeLoopId = Integer.parseInt(separated[1]);
                }

            }

            /*if (checkInterNetConnection()) {
                callLoopContentPingProcessTask();
            } else {
                collectAllLoopContentDataForOffline();
            }*/

            if (model.type.equalsIgnoreCase(Constant.IMAGE)) {
                playImage(model);
            } else if (model.type.equalsIgnoreCase(Constant.VIDEO)) {
                playVideo(model);
            }
        } else {
            handlerSlider.postDelayed(runnableSlider, 0);
        }

    }

    private void playImage(ContentDataModel model) {
        sliderVideoView.setVisibility(View.GONE);
        sliderImageView.setVisibility(View.VISIBLE);
        sliderImageView.setImageURI(Uri.parse(model.url));
        handlerSlider.postDelayed(runnableSlider, 10000);
    }

    private void playVideo(ContentDataModel model) {
        sliderVideoView.stopPlayback();
        sliderVideoView.setVideoURI(Uri.parse(model.url));
        Log.e(TAG,"VIDEO :"+Uri.parse(model.url));
        sliderVideoView.seekTo(0);
        sliderVideoView.setVisibility(View.VISIBLE);
        sliderImageView.setVisibility(View.GONE);
        sliderVideoView.setOnPreparedListener(mp -> {
            try {
                sliderVideoView.start();

            } catch (Exception e) {
                Log.e(TAG, "setOnPreparedListener Error : " + e.getMessage());
            }
        });
        sliderVideoView.setOnCompletionListener(mediaPlayer -> {
            sliderVideoView.pause();
            sliderVideoView.stopPlayback();

            handlerSlider.postDelayed(runnableSlider, 0);
        });
    }
    public void callPing() {
        if (checkInterNetConnection()) {
            Log.e(TAG, "PingCallRunnable" + Constant.getCurrentDate());
            if (!PreferenceManager.getStringForKey(thisContext, Constant.DEVICE_ID, Constant.IS_EMPTY).isEmpty()) {
                isOffline = 0;
                offlineTime = 0;
                callDevicePinkAsyncTask();
            }
        }
    }
    private void sendOfflinePing(Long offlineTime) {
        isOffline = 1;
        collectDevicePinkData();
        OffLineDevicePingAsyncTask asyncTask = new OffLineDevicePingAsyncTask(this,
                devicePingModel, offlineTime);
        asyncTask.execute();


    }
    //OnLineDevicePingAsyncTask.GetOnLineDevicePingListener,
    @Override
    public void didReceivedOneLineDevicePingResult(String status, String json) {
        if (status.equals(Constant.SUCCESS)) {
            try {
                if (json.length() > 0) {
                    JSONArray jsonarray = new JSONArray(json);
                    JSONObject jObj = jsonarray.getJSONObject(0);
                    final boolean contentUpdate = jObj.getBoolean(Constant.CONTENT_UPDATE);
                    Log.e(TAG, "contentUpdate : " + contentUpdate);
                    String pingCall = PreferenceManager.getStringForKey(thisContext, Constant.APP_PING_CALL_INTERVAL, Constant.IS_EMPTY);
                    if(!pingCall.isEmpty()){
                        pingCallDelay = Integer.parseInt(pingCall);
                    }
                    if (contentUpdate) {
                        handlerSlider.removeCallbacks(runnableSlider);
                        //pingCallHandler.removeCallbacks(pingCallRunnable);
                        //callGetContentApi();
                    } else {
                        //pingCallHandler.postDelayed(pingCallRunnable, pingCallDelay);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //pingCallHandler.postDelayed(pingCallRunnable, pingCallDelay);
        }
    }
    //OffLineDevicePingAsyncTask.GetOffLineDevicePingListener
    @Override
    public void didReceivedOffLineDevicePingResult(String status, String json) {
        isOffline = 0;
        //pingCallHandler.postDelayed(pingCallRunnable, pingCallDelay);
        if (status.equals(Constant.SUCCESS)) {

        }
    }
}
