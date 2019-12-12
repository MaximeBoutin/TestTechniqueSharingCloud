package com.maximeboutin.testtechnique.sharingcloud;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import com.maximeboutin.testtechnique.sharingcloud.utils.MyBroadcastReceiver;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // A faire en premier d'après la documentation
        createNotificationChannel();

        BroadcastReceiver br = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        this.registerReceiver(br, filter);

//
//        TwitterAPI twitterAPI = new TwitterAPI("M3m67FdpWsADQXzcUrDRlwSf0Pl4Payf94peJhxFyWr1LYrfnI","bfBB8M3sRV8uSxvXSN3brVQIp");
//        twitterAPI.requestBearerToken();
//
//        Intent serviceIntent = new Intent(this, DisplayTwitterFeedActivity.class);
//        startActivity(serviceIntent);


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ChannelName";
            String description = "ChannelDescription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("idChannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



}

