package com.ethernet.app.mainscreen.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.ethernet.app.mainscreen.model.DevicePingModel;
import com.ethernet.app.utility.Constant;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class OffLineDevicePingAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = OffLineDevicePingAsyncTask.class.getSimpleName();
    private DevicePingModel model;
    private String jsonResult = Constant.IS_EMPTY;
    private Long offlineTime;
    private String _id;

    private GetOffLineDevicePingListener listener;

    public interface GetOffLineDevicePingListener {
        void didReceivedOffLineDevicePingResult(String status, String json,String id);
    }


    public OffLineDevicePingAsyncTask(GetOffLineDevicePingListener listener,
                                      DevicePingModel devicePingModel,Long offlineTimeDifference,
                                      String id) {
        this.listener = listener;
        this.model = devicePingModel;
        this.offlineTime = offlineTimeDifference;
        this._id = id;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String responseString;
        int PingCallSocketTimeout = 5000;
        int PingCallConnTimeout = 3000; //in Sec
        try {
            String callURL = Constant.BASE_URL + "/DEVICE_PING";
            String postData = "";
            postData += "deviceid=" + URLEncoder.encode(model.deviceId, "UTF-8") + "&";
            postData += "deviceip=" + URLEncoder.encode(model.deviceIp, "UTF-8") + "&";
            postData += "uuid=" + URLEncoder.encode(model.uuId, "UTF-8") + "&";
            postData += "date=" + URLEncoder.encode(model.date, "UTF-8") + "&";
            postData += "lat=" + URLEncoder.encode("" + model.latitude, "UTF-8") + "&";
            postData += "long=" + URLEncoder.encode("" + model.longitude, "UTF-8") + "&";
            postData += "nowplaying=" + URLEncoder.encode("" + model.nowPlaying, "UTF-8") + "&";
            postData += "IsOffline=" + URLEncoder.encode("" + model.isOffline, "UTF-8") + "&";
            postData += "OfflineTime=" + URLEncoder.encode("" + offlineTime, "UTF-8");

            Log.d(TAG, "PingProcessTaskPostData: " + postData);
            Log.d(TAG, "CallURlWithPostData" + callURL + postData);
            //MDM_NEW SSL SERVICE CONNECTION

            URL url = new URL(callURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(PingCallSocketTimeout);/* milliseconds */
            conn.setConnectTimeout(PingCallConnTimeout); /* milliseconds */
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(postData);

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == Constant.HTTP_OK) {
                // Server response
                InputStream is = conn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                jsonResult = sb.toString();


                Log.d(TAG, "Ping : " + jsonResult);

                /*JSONArray jsonarray = new JSONArray(json);
                if (jsonarray.length() > 0) {
                    JSONObject jObj = jsonarray.getJSONObject(0);
                    flg_Active = jObj.getString("ACTIVE");
                    flg_ContentUpdate = jObj.getString("CONTENTUPDATE");
                    onDemandTime = jObj.getString("OnDemandTime");
                }*/
                responseString = Constant.SUCCESS;
            } else {
                responseString = Constant.FAIL;
            }
        } catch (ClientProtocolException e) {
            responseString = Constant.FAIL;
            Log.e(TAG, "Ping Failed ClientProtocolException : " + e.getMessage());
        } catch (IOException e) {
            responseString = Constant.FAIL;
            Log.e(TAG, "Ping Failed IOException : " + e.getMessage());
        } catch (Exception e) {
            responseString = Constant.FAIL;
            Log.e(TAG, "Ping Failed Exception : " + e.getMessage());
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String status) {
        super.onPostExecute(status);
        if(listener != null){
            listener.didReceivedOffLineDevicePingResult(status,jsonResult,_id);
        }

    }
}
