package com.ethernet.app.mainscreen.asynctask;

import android.content.Context;
import android.os.AsyncTask;


public class CheckInternetAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    public CheckInternetWorksListener listener;

    public interface CheckInternetWorksListener{
        void didInternetWorking(boolean status);
    }

    public CheckInternetAsyncTask(Context context,CheckInternetWorksListener listener) {
        this.context = context;
        this.listener = listener;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        /*ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        try
                        {
                            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                            urlc.setRequestProperty("User-Agent", "Test");
                            urlc.setRequestProperty("Connection", "close");
                            urlc.setConnectTimeout(100); //choose your own timeframe
                            urlc.setReadTimeout(100); //choose your own timeframe
                            urlc.connect();
                            int networkcode2 = urlc.getResponseCode();
                            return (urlc.getResponseCode() == 200);
                        } catch (IOException e)
                        {
                            return (false);  //connectivity exists, but no internet.
                        }
                    }

        }
        return false;*/
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
     if(listener !=null){
         listener.didInternetWorking(result);
     }
    }
}