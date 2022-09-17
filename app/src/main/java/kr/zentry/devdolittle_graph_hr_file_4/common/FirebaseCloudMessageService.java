package kr.zentry.devdolittle_graph_hr_file_4.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import kr.zentry.devdolittle_graph_hr_file_4.R;
import kr.zentry.devdolittle_graph_hr_file_4.SplashActivity;

public class FirebaseCloudMessageService extends FirebaseMessagingService {
    private static final String TAG = "FCM";
    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID = "FCMchannel";
    private static String CHANNEL_NAME = "FCMchannel";
    private NotificationCompat.Builder builder;
    private static final String[] topics = {"/topics/event"};

    public FirebaseCloudMessageService() {
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e(TAG, s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        makeNotification(remoteMessage);
    }

    private void makeNotification(RemoteMessage remoteMessage) {
        try {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            String topic = remoteMessage.getFrom();

            Context mContext = getApplicationContext();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
            } else {
                builder = new NotificationCompat.Builder(mContext);
            }

            builder.setSmallIcon(R.mipmap.ic_launcher_round)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setVibrate(new long[]{200, 100, 200});

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            int notificationId = -1;

            Intent intent = new Intent(this, SplashActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            if (topic.equals(topics[0])){
                notificationId = 0;
            }

            if (notificationId >= 0) {
                PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(pendingIntent);
                notificationManager.notify(notificationId, builder.build());
            }
        } catch (NullPointerException nullPointerException) {
            Log.e("error Notify", nullPointerException.toString());
        }
    }
}

