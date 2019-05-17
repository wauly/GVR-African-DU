package com.ethernet.app.mainscreen.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ethernet.app.global.DatabaseHandler;
import com.ethernet.app.global.PreferenceManager;
import com.ethernet.app.mainscreen.model.ContentDataModel;
import com.ethernet.app.mainscreen.model.GetContentModel;
import com.ethernet.app.utility.Constant;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class GetContentAsyncTask extends AsyncTask<String, String, String> {

    private static final String TAG = GetContentAsyncTask.class.getSimpleName();

    private DatabaseHandler database;
    private GetContentDateListener listener;
    private Context mtContext;
    private int CONTENT_DATA_NOT_AVAILABLE = 1;
    //private ArrayList<Integer> storeLoopIdList = new ArrayList<>();

    public interface GetContentDateListener {
        void didReceivedGetContentDate(String status, int isContentAvailable);
    }

    public GetContentAsyncTask(GetContentDateListener listener, DatabaseHandler db, Context context) {
        this.listener = listener;
        this.database = db;
        this.mtContext = context;
    }


    @Override
    protected String doInBackground(String... deviceId) {
        String responseString = "";
        try {
            //String ServiceUrl = getResources().getString(R.string.service_url);
            String callURL = Constant.BASE_URL + "/GET_CONTENT";
            String postData = "";
            Log.d("CallUrlForFirstime==>", callURL);
            postData += "deviceid=" + URLEncoder.encode(deviceId[0], "UTF-8");

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
                final String jsonData = sb.toString();

                collectAllJsonData(jsonData);

                responseString = Constant.SUCCESS;

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

    private void collectAllJsonData(String json) {

        if (!database.isTableEmpty()) {
            database.updateDeleteFlagContent("1");
        }
        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(json);
            for (int i = 0; jsonarray.length() > i; i++) {

                JSONObject jsonObject = jsonarray.getJSONObject(i);
                GetContentModel model = new GetContentModel();
                model.active = jsonObject.getBoolean(Constant.API_TAG.ACTIVE);
                model.appPinkCallInterval = jsonObject.getString(Constant.API_TAG.APP_PING_CALL_INTERVAL);
                PreferenceManager.saveStringForKey(mtContext, Constant.APP_PING_CALL_INTERVAL, model.appPinkCallInterval);
                model.deviceId = jsonObject.getString(Constant.API_TAG.DEVICE_ID);
                PreferenceManager.saveStringForKey(mtContext, Constant.DEVICE_ID, model.deviceId);
                model.orientation = jsonObject.getString(Constant.API_TAG.ORIENTATION);
                PreferenceManager.saveStringForKey(mtContext, Constant.API_TAG.ORIENTATION, model.orientation);
                model.uuid = jsonObject.getString(Constant.API_TAG.UUID);
                PreferenceManager.saveStringForKey(mtContext, Constant.UUID, model.uuid);

                JSONArray jsonArrayContent = jsonObject.getJSONArray(Constant.API_TAG.CONTENT);
                int length = jsonArrayContent.length();
                if (length != 0) {
                    for (int j = 0; j < jsonArrayContent.length(); j++) {
                        JSONObject contentObject = jsonArrayContent.getJSONObject(j);
                        ContentDataModel contentModel = new ContentDataModel();
                        contentModel.clickUrl = contentObject.getString(Constant.API_TAG.CLICKURL);
                        contentModel.duration = contentObject.getString(Constant.API_TAG.DURATION);
                        contentModel.isDaily = contentObject.getString(Constant.API_TAG.IS_DAILY);
                        contentModel.isSchedule = contentObject.getString(Constant.API_TAG.IS_SCHEDULE);
                        contentModel.loopContent = contentObject.getString(Constant.API_TAG.LOOP_CONTENT);
                        contentModel.loopEndDate = contentObject.getString(Constant.API_TAG.LOOP_END_DATE);
                        contentModel.loopEndTime = contentObject.getString(Constant.API_TAG.LOOP_END_TIME);
                        contentModel.loopStartDate = contentObject.getString(Constant.API_TAG.LOOP_START_DATE);
                        contentModel.loopStartTime = contentObject.getString(Constant.API_TAG.LOOP_START_TIME);
                        contentModel.txtLoopName = contentObject.getString(Constant.API_TAG.TXT_LOOPNAME);
                        contentModel.type = contentObject.getString(Constant.API_TAG.TYPE);
                        contentModel.url = contentObject.getString(Constant.API_TAG.URL);
                        contentModel.deleteFlag = "0";

                        String[] screenType = contentModel.txtLoopName.split("_");
                        switch (screenType[0]) {
                            case Constant.ScreenType.VERTICAL_ADD:
                                contentModel.screenType = Constant.ScreenType.VERTICAL_ADD;
                                break;
                            case Constant.ScreenType.HORIZONTAL_ADD:
                                contentModel.screenType = Constant.ScreenType.HORIZONTAL_ADD;
                                break;
                            case Constant.ScreenType.PAYMENT_ADD:
                                contentModel.screenType = Constant.ScreenType.PAYMENT_ADD;
                                break;
                            default:
                                contentModel.screenType = Constant.ScreenType.VERTICAL_ADD;
                                break;
                        }

                        database.insertContent(contentModel);
                    }
                } else {
                    CONTENT_DATA_NOT_AVAILABLE = Constant.NO_CONTENT_AVAILABLE;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s.equals(Constant.SUCCESS)) {
            database.deleteContent("1");
            listener.didReceivedGetContentDate(s, CONTENT_DATA_NOT_AVAILABLE);
        } else {
            listener.didReceivedGetContentDate(s, CONTENT_DATA_NOT_AVAILABLE);
        }
    }
}

