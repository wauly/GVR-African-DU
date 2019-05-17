package com.ethernet.app.mainscreen.asynctask;

import android.os.AsyncTask;

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

public class UpdateContentFlgTask extends AsyncTask<String, String, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String DeviceID = params[0];
        return UpdateContentFlgProcess(DeviceID);
    }

    private String UpdateContentFlgProcess(String DeviceID) {
        String responseString = "";
        try {
            String callURL = Constant.BASE_URL + "/DEVICE_CONTENTFLG_UPDATE";
            String postData = "";
            postData += "deviceid=" + URLEncoder.encode(DeviceID, "UTF-8");

            //MDM_NEW SSL SERVICE CONNECTION
            URL url = new URL(callURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(15000);/* milliseconds */
//                conn.setConnectTimeout(15000); /* milliseconds */
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
                //Log.d(TAG,"UpdateContentFlgProcess : " + json);
                JSONArray jsonarray = new JSONArray(json);
                if (jsonarray.length() > 0) {

                }
                responseString = "success";
            } else {
                responseString = "fail";
            }
        } catch (ClientProtocolException e) {
            responseString = "fail";
        } catch (IOException e) {
            responseString = "fail";
        } catch (Exception e) {
            responseString = "fail";
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {

    }


}