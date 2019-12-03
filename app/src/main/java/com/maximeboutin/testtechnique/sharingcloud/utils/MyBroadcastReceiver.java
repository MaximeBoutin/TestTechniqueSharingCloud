package com.maximeboutin.testtechnique.sharingcloud.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "TwitterAPI";

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();

        TwitterAPI twitterAPI = new TwitterAPI("M3m67FdpWsADQXzcUrDRlwSf0Pl4Payf94peJhxFyWr1LYrfnI","bfBB8M3sRV8uSxvXSN3brVQIp");
        String rep = twitterAPI.requestBearerToken();

        Log.i("TwitterAPI", rep);

        Intent serviceIntent = new Intent();

        NotifService.enqueueWork(context, NotifService.class, 1000, serviceIntent);
    }
}
