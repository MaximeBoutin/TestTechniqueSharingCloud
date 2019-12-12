package com.maximeboutin.testtechnique.sharingcloud.utils;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.maximeboutin.testtechnique.sharingcloud.DisplayTwitterFeedActivity;
import com.maximeboutin.testtechnique.sharingcloud.R;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * @brief Classe permettant de gérer le service de notification.
 */
public class NotifService extends JobIntentService {
    public static final int NOTIF_ID = 1000;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // Create an explicit intent for an Activity in your app
        while (true)
        {
            try {
                //TODO A enlever, utile pour débug.
                Thread.sleep(10000);

                TwitterAPI twitterAPI = new TwitterAPI();
                String trendig = twitterAPI.requestTrending();

                Intent intent2 = new Intent(this, DisplayTwitterFeedActivity.class);
                intent.putExtra("trendingHashtag", trendig);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "idChannel")
                        .setSmallIcon(R.drawable.ic_notif_twitter)
                        .setContentTitle("Un nouveau Hashtag est Trendig")
                        .setContentText(trendig)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);


                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(NOTIF_ID, builder.build());

                Log.i("TwitterAPI", "notif has been sent");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
