package com.ethernet.app.mainscreen.asynctask;

import android.os.AsyncTask;
import android.util.Log;


import com.ethernet.app.mainscreen.model.DevicePingModel;
import com.ethernet.app.utility.Constant;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;

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

public class OnLineLoopContentPingProcessTask extends AsyncTask<String, String, String> {

    private static final String TAG = OnLineLoopContentPingProcessTask.class.getSimpleName();
    private DevicePingModel model;

    public OnLineLoopContentPingProcessTask(DevicePingModel devicePingModel){
        this.model = devicePingModel;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        return PingProcess();
    }

    private String PingProcess() {
        String responseString;
        try {
            //String ServiceUrl = getResources().getString(R.string.service_url);
            String callURL = Constant.BASE_URL + "/LOOP_CONTENT_PING";
            //String callURL = ServiceUrl + "/CONTENT_PING";
            String postData = "";
            postData += "deviceid=" + URLEncoder.encode(model.deviceId, "UTF-8") + "&";
            postData += "deviceip=" + URLEncoder.encode(model.deviceIp, "UTF-8") + "&";
            postData += "uuid=" + URLEncoder.encode(model.uuId, "UTF-8") + "&";
            postData += "date=" + URLEncoder.encode(model.date, "UTF-8") + "&";
            postData += "nowplaying=" + URLEncoder.encode("" + model.nowPlaying, "UTF-8") + "&";
            postData += "viewcontentcount=" + URLEncoder.encode("" + 1, "UTF-8") + "&";
            postData += "viewloopcount=" + URLEncoder.encode("" + model.viewLoopCount, "UTF-8") + "&";
            postData += "IsOffline=" + URLEncoder.encode("" + model.isOffline, "UTF-8");

            Log.d(TAG, "postDataForLoopContentPing: " + postData);

            URL url = new URL(callURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);/* milliseconds */
            conn.setConnectTimeout(3000); /* milliseconds */
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
                String json = sb.toString();

                Log.d(TAG, "PingINLoopContent : " + json);

                JSONArray jsonarray = new JSONArray(json);
                if (jsonarray.length() > 0) {
                   /* JSONObject jObj = jsonarray.getJSONObject(0);
                    flg_Active = jObj.getString("ACTIVE");
                    flg_ContentUpdate = jObj.getString("CONTENTUPDATE");*/
                }
                responseString = "success";

            } else {
                responseString = "fail";
            }
        } catch (ClientProtocolException e) {
            responseString = "fail";
            Log.e(TAG, "Ping Failed ClientProtocolException : " + e.getMessage());
        } catch (IOException e) {
            responseString = "fail";
            Log.e(TAG, "Ping Failed IOException : " + e.getMessage());
        } catch (Exception e) {
            responseString = "fail";
            Log.e(TAG, "postDataForLoopContentPing Exception" + e.getMessage());
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
