package com.ethernet.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ethernet.app.mainscreen.activity.MainActivity;
import com.ethernet.app.utility.Constant;
import com.gvr.displaydataparser.MessageFactory;
import com.gvr.displaydataparser.Msg;
import com.gvr.displaydataparser.RealtimeDataParser;
import com.gvr.displaydataparser.iMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class EthernetService extends Service {

    private String TAG = EthernetService.class.getSimpleName();
    private RealtimeDataParser realtimeDataParser = new RealtimeDataParser();
    private MessageFactory _mf = new MessageFactory();
    private String IP_ADDRESS = Constant.IS_EMPTY;
    private int PORT;
    private Socket _socket;

    private boolean isRunning;
    private Thread backgroundThread;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }


    private Runnable myTask = new Runnable() {
        @Override
        public void run() {

           // Log.e("INFO", "SOCKET BACKGROUND SERVICE IS RUNNING");
            try {
                //sendDataInSideActivity("I am in side urn method :" + IP_ADDRESS + " " + PORT);

                if (_socket == null) {//|| _socket.isClosed()
                    //Log.e(TAG, " START CONNECTING SOCKET");
                    _socket = new Socket(IP_ADDRESS, PORT);
                    //_socket.setKeepAlive(true);
                    sendDataInSideActivity("Socket connected");
                    Log.e(TAG, " CONNECTING SOCKET SUCCESSFULLY");
                }

                int bytesRead;
                InputStream inputStream = _socket.getInputStream();
                //sendDataInSideActivity("below of input stream");
                byte[] buffer = new byte[1024];

                new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {

                        }
                        if (realtimeDataParser.hasMessage()) {
                            Msg m = realtimeDataParser.getNextMessage();
                            if (m != null) {
                                iMessage msg = _mf.getMessage(m);

                                if (msg != null) {
                                    sendDataInSideActivity(msg.toJson());
                                }
                            }
                        }
                    }
                }).start();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    char[] ch_str = new char[bytesRead];

                    for (int i = 0; i < bytesRead; i++) {
                        ch_str[i] = (char) (0x00FF & buffer[i]);
                    }
                    realtimeDataParser.add(ch_str);

                    for (int i = 0; i < bytesRead; i++) {
                        buffer[i] = 0;
                    }
                    //try {
                    //    Thread.sleep(10);
                    //} catch (InterruptedException e) {

                    //}
                }
            } catch (IOException e) {
                //sendToastMessageInSideActivity("IOException :" + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                //sendToastMessageInSideActivity("Exception :" + e.getMessage());
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroy() {
        if (_socket != null) {
            try {
                _socket.close();
                _socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            IP_ADDRESS = intent.getStringExtra("ip");
            PORT = Integer.valueOf(intent.getStringExtra("port"));
            Log.e(TAG, "IP : " + IP_ADDRESS + "::" + "PORT : " + PORT);

        }
        if (!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }

        return START_STICKY;
    }

    private void sendDataInSideActivity(String data) {
        try {
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(MainActivity.BROADCAST_ACTION);
            broadCastIntent.putExtra(Constant.DATA, data);
            sendBroadcast(broadCastIntent);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*private void sendToastMessageInSideActivity(String data) {
        try {
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(MainActivity.BROADCAST_ACTION_TOAST);
            broadCastIntent.putExtra(Constant.DATA, data);
            sendBroadcast(broadCastIntent);

        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
    //}
}
