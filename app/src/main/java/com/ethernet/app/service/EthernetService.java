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
    private String IP_ADDRESS = "192.168.0.100";
    private int PORT = 16224;
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
            Log.i("INFO", "SOCKET BACKGROUND SERVICE IS RUNNING");

            try {
                if (_socket == null) {
                    _socket = new Socket(IP_ADDRESS, PORT);
                    _socket.setKeepAlive(true);
                }
                int bytesRead;
                InputStream inputStream = _socket.getInputStream();
                byte[] buffer = new byte[1024];

                new Thread(() -> {
                    while(true) {
                        try {
                            Thread.sleep(10);
                        }catch(InterruptedException e){

                        }
                        if (realtimeDataParser.hasMessage()) {
                            Msg m = realtimeDataParser.getNextMessage();
                            if (m != null) {
                                iMessage msg = _mf.getMessage(m);

                                if (msg != null) {
                                    sendDataInSideActivity(msg.toJson());
                                    //GlobalBus.getBus().post(new EventMessage(msg.toJson()));
                                }
                            }
                        }
                    }
                }).start();


                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    char[] ch_str = new char[bytesRead];

                    for(int i=0;i<bytesRead;i++){
                        ch_str[i] = (char)(0x00FF & buffer[i]);
                    }
                    realtimeDataParser.add(ch_str);

                    for(int i=0;i<bytesRead;i++){
                        buffer[i]=0;
                    }
                }

                if(_socket.isClosed()){
                    sendToastMessageInSideActivity("closed 1");
                }

            } catch (IOException e) {
                if(_socket.isClosed()){
                    sendToastMessageInSideActivity("closed 2");
                }

                e.printStackTrace();
            }catch (Exception e){
                if(_socket.isClosed()){

                }
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }

        return START_STICKY;
    }
    private void sendDataInSideActivity(String data)
    {
        try
        {
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(MainActivity.BROADCAST_ACTION);
            broadCastIntent.putExtra(Constant.DATA, data);
            sendBroadcast(broadCastIntent);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    private void sendToastMessageInSideActivity(String data)
    {
        try
        {
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(MainActivity.BROADCAST_ACTION_TOAST);
            broadCastIntent.putExtra(Constant.DATA, data);
            sendBroadcast(broadCastIntent);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
