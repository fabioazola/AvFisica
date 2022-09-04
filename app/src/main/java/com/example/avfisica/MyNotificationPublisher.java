package com.example.avfisica;
import android.app.Notification ;
import android.app.NotificationChannel ;
import android.app.NotificationManager ;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver ;
import android.content.Context ;
import android.content.Intent ;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.avfisica.MainActivity.NOTIFICATION_CHANNEL_ID;
import static com.example.avfisica.MainActivity.flag_notify;

public class MyNotificationPublisher extends BroadcastReceiver {

    private static final String CHANNEL_ID = "CHANNEL_SAMPLE";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Get id & message
        int notificationId = intent.getIntExtra("notificationId", 0);
        String message = intent.getStringExtra("message");

        // Call MainActivity when notification is tapped.
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        // NotificationManager
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API 26 and above
            CharSequence channelName = "My Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        if (notificationId==1) {
            // Prepare Notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("WIFIT")
                    .setContentText("Acompanhe seus resultados!!")
                    .setSubText("WIFIT")
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            notificationManager.notify(notificationId, builder.build());          // Notify

        }else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("WIFIT")
                    .setContentText("Bora treinar e verificar seus Resultados!!")
                    .setSubText("WIFIT")
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            notificationManager.notify(notificationId, builder.build());          // Notify

        }


    }
}