package com.example.owner.myapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ImageService extends Service {

    private BroadcastReceiver broadcastReceiver;
    public ImageService() { }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();


    }

    public int onStartCommand(Intent intent, int flag, int startId) {

        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        theFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            TcpConnection tcpConnection = new TcpConnection();
                            tcpConnection.startConnection();
                        }
                    }
                }
            }
        };
        this.registerReceiver(this.broadcastReceiver, theFilter);
        Toast.makeText(this, "Service starting...", Toast.LENGTH_SHORT).show();
        return START_STICKY;


    }

    public void onDestroy() {
        Toast.makeText(this, "Service ending...", Toast.LENGTH_SHORT).show();
    }


}
