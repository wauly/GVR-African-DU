package com.ethernet.app.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkUtil {

    public static final String TAG = NetworkUtil.class.getSimpleName();

    private static int TYPE_WIFI = 1;
    private static int TYPE_MOBILE = 2;
    private static int TYPE_ETHERNET = 9;
    private static int TYPE_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_NOT_CONNECTED=0,NETWORK_STAUS_WIFI=1,NETWORK_STATUS_MOBILE=2,NETWORK_STATUS_ETHERNET=9;

    private static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
            /*if(activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET)
                return TYPE_ETHERNET;*/
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        int status = 0;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = NETWORK_STAUS_WIFI;
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status =NETWORK_STATUS_MOBILE;
        }/* else if (conn == NetworkUtil.TYPE_ETHERNET) {
            status = NETWORK_STATUS_ETHERNET;
        }*/ else if (conn == TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }

    public   static Network findWlanNetwork(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        Network etherNetwork = null;

        for (Network network : connectivityManager.getAllNetworks()) {

            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                return   network;

            }

        }

        return null;

    }

}


