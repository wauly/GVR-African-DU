package com.ethernet.app.mainscreen.asynctask;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.ethernet.app.mainscreen.model.ContentDataModel;
import com.ethernet.app.utility.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SaveContentUrlAsyncTask extends AsyncTask<String,String,String> {

    private static final String TAG = SaveContentUrlAsyncTask.class.getSimpleName();

    private SaveContentUrlListener listener;

    public interface SaveContentUrlListener{
        void didReceivedSaveContentUrlDate(String status);
    }
    private static final String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
    private static final File folder = new File(extStorageDirectory, "waulypod-resource");
    private static final int MEGABYTE = 1024 * 1024;

    private ArrayList<ContentDataModel> listOfContent;

    public SaveContentUrlAsyncTask(SaveContentUrlListener listener, ArrayList<ContentDataModel> list){
        this.listOfContent = list;
        this.listener = listener;
    }


    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        File file;
        File downloadfile;

        if (!folder.exists()) {
            folder.mkdir();
        }

        for (int i = 0; i < listOfContent.size(); i++) {

            if (isCancelled()) {
                Log.i(TAG, "FetchResource - isCancelled Called");
                break;
            }

            String fileURL = listOfContent.get(i).url;
            String fileName = getFileName(fileURL);

            if (!fileName.equalsIgnoreCase("")) {
                file = new File(folder, fileName);
                downloadfile = new File(folder, fileName + ".download");

                if (!file.exists()) {
                    try {
                        //Log.d(TAG, "FetchResource - download : " + fileName);

                        String fileUrl = fileURL;
                        URL url = new URL(fileUrl);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.connect();
                        InputStream inputStream = urlConnection.getInputStream();

                        //file.createNewFile();
                        downloadfile.createNewFile();

                        FileOutputStream fileOutputStream = new FileOutputStream(downloadfile);
                        byte[] buffer = new byte[MEGABYTE];
                        int bufferLength = 0;
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, bufferLength);
                        }
                        fileOutputStream.close();
                        if (downloadfile.exists()) {
                            downloadfile.renameTo(file);
                        }
                        result = Constant.SUCCESS;

                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.getMessage());
                        result = "FileDownLoadError";
                    } catch (MalformedURLException e) {
                        Log.e(TAG, e.getMessage());
                        result = "FileDownLoadError";
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                        result = "FileDownLoadError";
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        result = "FileDownLoadError";
                    }

                    //Delete file if not success
                    if (!result.equalsIgnoreCase(Constant.SUCCESS)) {
                        //file.delete();
                        downloadfile.delete();
                    }

                }

            }

        }
        return result;
    }
    public String getFileName(String url) {
        String fileName = "";
        fileName = url.substring(url.lastIndexOf('/') + 1);
        return fileName;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.didReceivedSaveContentUrlDate(s);

    }
}
