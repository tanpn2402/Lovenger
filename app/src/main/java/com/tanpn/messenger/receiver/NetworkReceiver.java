package com.tanpn.messenger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by phamt_000 on 11/21/16.
 */
public class NetworkReceiver extends BroadcastReceiver {

    public interface OnNetworkReceiver{
        void onReceiver(boolean isConnected);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                OnNetworkReceiver onNetworkReceiver = (OnNetworkReceiver) context;
                onNetworkReceiver.onReceiver(true);
            } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                OnNetworkReceiver onNetworkReceiver = (OnNetworkReceiver) context;
                onNetworkReceiver.onReceiver(false);
            }
        }
    }
}
