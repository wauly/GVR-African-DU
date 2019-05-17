package com.ethernet.app.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.ethernet.app.BuildConfig;
import com.ethernet.app.mainscreen.model.ContentDataModel;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;


public class Constant {

    private static final String TAG = Constant.class.getSimpleName();

    public static final String BASE_URL = BuildConfig.BASE_URL;
    public static final int HTTP_OK = 200;

    public static final String DATA = "data";


    public interface Operation{
        String CURRENT_DISPENSE = "CurrentDispense";
    }

    public final static String ALLOW = "allow";
    public final static String DENY = "deny";
    public final static String IS_EMPTY = "";


    public final static String DEVICE_ID = "deviceid";
    public final static String UUID = "uuid";
    public final static String APP_PING_CALL_INTERVAL = "APPPINGCALLINTERVAL";
    public final static String SUCCESS = "SUCCESS";
    public final static String FAIL = "FAIL";
    public final static String RESPONSE_FAIL = "RESPONSEFAIL";
    public final static String PROCESS_ERROR = "PROCESSERROR";
    public final static String VERIFY = "Verify";
    public final static String NOT_HTTP_CONNECTION = "NOTHTTPCONNECTION";
    public final static String HORIZONTAL = "Horizontal";
    public final static String VERTICAL = "Vertical";

    public final static String IMAGE = "Image";
    public final static String VIDEO = "Video";
    public final static String CONTENT_UPDATE = "CONTENTUPDATE";
    public final static int NO_CONTENT_AVAILABLE = 0;
    public final static String SAVE_OFF_LINE_TIME = "saveOfLineTime";
    public final static String OFFLINE_PING_ID = "id";
    public final static String STOP_FUELING = "stop";


    public interface API_TAG {
        String URL = "URL";
        String TYPE = "TYPE";
        String DURATION = "DURATION";
        String LOOP_CONTENT = "LOOPCONTENT";
        String IS_DAILY = "IsDaily";
        String IS_SCHEDULE = "IsSchedule";
        String LOOP_END_DATE = "LoopEndDate";
        String LOOP_END_TIME = "LoopEndTime";
        String LOOP_START_DATE = "LoopStartDate";
        String LOOP_START_TIME = "LoopStartTime";
        String ACTIVE = "ACTIVE";
        String APP_PING_CALL_INTERVAL = "APPPINGCALLINTERVAL";
        String DEVICE_ID = "DEVICE_ID";
        String ORIENTATION = "ORIENTATION";
        String UUID = "UUID";
        String CONTENT = "CONTENT";
        String CLICKURL = "CLICKURL";
        String TXT_LOOPNAME = "TXT_LOOPNAME";

    }

    public interface SCRREN_TYPE {
        String RIGHT = "RIGHT";
        String BOTTOM = "BOTTOM";
        String LEFT = "LEFT";
    }

    public interface BOOLEAN {
        String TRUE = "True";
        String FALSE = "False";
    }
    public interface ScreenType{
        String VERTICAL_ADD = "Vertical";
        String HORIZONTAL_ADD = "Horizontal";
        String PAYMENT_ADD = "payment";
    }
    public interface DU_Setting{
        String FUEL_TYPE = "fuelType";
        String FP_TYPE = "fpType";
        String SLEEP_TIME = "sleepTime";
    }


    public static void logError(String tag, String message) {
        Log.e(tag, message);
    }

    /**
     * Shifted  getIpAddress method from MainActivity to separate class for simplification
     * Modification added in code for Ethernet to return  IP address , Previously returning only wifi IP address
     */

    public static String getIpAddress(Context context) {
        String actualConnectedToNetwork = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                actualConnectedToNetwork = getWifiIp(context);  //TODO : Get IP in case of WIFI only

            }
        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = getNetworkInterfaceIpAddress();//TODO:GET IP in case of Ethernet
        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = "127.0.0.1";
        }
        return actualConnectedToNetwork;
    }

    /**
     * Get IP in case of WIFI only
     */
    @Nullable
    private static String getWifiIp(Context context) {
        final WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
            int ip = mWifiManager.getConnectionInfo().getIpAddress();
            return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                    + ((ip >> 24) & 0xFF);
        }
        return null;
    }

    /**
     * Get IP in case of Ethernet only
     */
    private static String getNetworkInterfaceIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String host = inetAddress.getHostAddress();
                        if (!TextUtils.isEmpty(host)) {
                            return host;
                        }
                    }
                }

            }
        } catch (Exception ex) {
            Log.e("IP Address", "getLocalIpAddress", ex);
        }
        return null;
    }

    public static String getCurrentDate() {
        Date d = new Date();
        return DateFormat.format("MM-dd-yyyy HH:mm:ss", d.getTime()).toString();
    }

    public static String getFileName(String url) {
        String fileName = "";
        fileName = url.substring(url.lastIndexOf('/') + 1);
        return fileName;
    }

    public static boolean someLogicBeforeStartSlider(ContentDataModel model) {
        if (model.isDaily.equalsIgnoreCase(Constant.BOOLEAN.FALSE) &&
                model.isSchedule.equalsIgnoreCase(Constant.BOOLEAN.FALSE)) {
            /** If isDaily and isSchedule both are false this means add play always*/
            return true;
        } else if (model.isDaily.equalsIgnoreCase(Constant.BOOLEAN.TRUE) &&
                model.isSchedule.equalsIgnoreCase(Constant.BOOLEAN.FALSE)) {
            /** If isDaily is true and isSchedule is false this means add play
             * In between given time the variable name is loopStartTime and loopEndTime*/
            return isTimeBetweenTwoTime(model);
        } else if (model.isDaily.equalsIgnoreCase(BOOLEAN.FALSE) &&
                model.isSchedule.equalsIgnoreCase(BOOLEAN.TRUE)) {
            /** If isDaily is false and isSchedule is true this means add play
             * In between given date the variable name is loopStartDate and loopEndDate*/
            return isDateBetweenTwoTime(model);
        }

        return false;
    }


    private static boolean isTimeBetweenTwoTime(ContentDataModel model) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

            Date starTime = simpleDateFormat.parse(model.loopStartTime);
            //Log.e(TAG,"START_TIME :" + starTime);
            Date endTime = simpleDateFormat.parse(model.loopEndTime);
            //Log.e(TAG,"END_TIME :" + endTime);

            //current time
            String getCurrentTime = simpleDateFormat.format(Calendar.getInstance().getTime());
            Date currentTime = simpleDateFormat.parse(getCurrentTime);
            //Log.e(TAG,"CURRENT_TIME :" + currentTime);

            return currentTime.after(starTime) && currentTime.before(endTime) || currentTime.equals(starTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isDateBetweenTwoTime(ContentDataModel model) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            Date starDate = simpleDateFormat.parse(model.loopStartDate);
            //Log.e(TAG,"START_DATE :" + starDate);
            Date endDate = simpleDateFormat.parse(model.loopEndDate);
            // Log.e(TAG,"END_DATE :" + endDate);

            //current time
            String getCurrentDate = simpleDateFormat.format(Calendar.getInstance().getTime());
            Date currentDate = simpleDateFormat.parse(getCurrentDate);
            //Log.e(TAG,"CURRENT_DATE :" + currentDate);

            return currentDate.after(starDate) && currentDate.before(endDate) || currentDate.equals(starDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Long offLineTimeDifference(String offLineTime) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

        try {
            Date date1 = simpleDateFormat.parse(offLineTime);
            Date date2 = simpleDateFormat.parse(getCurrentDate());

            return getDifferenceOffLineTime(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0L;
    }

    //TODO difference between two dates
    private static Long getDifferenceOffLineTime(Date startDate, Date endDate) {

        //Log.e(TAG, "StartDate :" + startDate);
        //Log.e(TAG, "EndDate :" + endDate);
        //in milliseconds
        long diff = endDate.getTime() - startDate.getTime();
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diff);
        //Log.e(TAG, "DiffInSec " + diffInSec);

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        //Log.e(TAG, "Difference : " + diffDays + "," + diffHours + "," + diffMinutes + "," + diffSeconds);

        return diffInSec;
    }

}
