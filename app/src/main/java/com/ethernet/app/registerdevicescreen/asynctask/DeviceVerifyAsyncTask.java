package com.ethernet.app.registerdevicescreen.asynctask;

import android.os.AsyncTask;

import com.ethernet.app.utility.Constant;

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

public class DeviceVerifyAsyncTask extends AsyncTask<String, Void,String> {

    private GetDeviceVerifyListener listener;

    public interface GetDeviceVerifyListener{

        void didReceivedDeviceVerifyResult(String result);
    }

    public DeviceVerifyAsyncTask(GetDeviceVerifyListener listener){
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... deviceId) {
        String ProcessResult;

        try {
            URL url = new URL(Constant.BASE_URL + "/DEVICE_VERIFY");
            String postData = "";
            postData += "deviceid=" + URLEncoder.encode(deviceId[0], "UTF-8") + "&";
            postData += "uuid=" + URLEncoder.encode(Constant.IS_EMPTY, "UTF-8");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
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
                String Result = sb.toString();
                if (Result.lastIndexOf(Constant.SUCCESS) > -1) {
                    ProcessResult = Constant.SUCCESS;
                } else {
                    ProcessResult = Constant.FAIL;
                }

            } else {
                ProcessResult = Constant.RESPONSE_FAIL;
            }

        } catch (IOException e) {
            ProcessResult = Constant.PROCESS_ERROR;
        } catch (Exception e) {
            ProcessResult = Constant.PROCESS_ERROR;
        }
        return ProcessResult;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(listener != null){
            listener.didReceivedDeviceVerifyResult(result);
        }
    }
}