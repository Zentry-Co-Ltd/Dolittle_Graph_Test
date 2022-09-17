package kr.zentry.devdolittle_graph_hr_file_4.common;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.kakao.sdk.common.KakaoSdk;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import kr.zentry.devdolittle_graph_hr_file_4.R;

public class GlobalApplication extends Application {
    private String DBName = "Dolittle";
    private static String CHANNEL_FCM_ID = "FCMchannel";
    private static String CHANNEL_FCM_NAME = "FCMchannel";

    private static String CHANNEL_DAILY_ID = "DAILYchannel";
    private static String CHANNEL_DAILY_NAME = "DAILYchannel";

    private static GlobalApplication mInstance;
    private NotificationManager notificationManager;
    private NotificationChannel channel;

    public static GlobalApplication getGlobalApplicationContext() {
        if (mInstance == null) {
            throw new IllegalStateException("This Application does not GlobalAuthHelper");
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        createFCMChannel(mInstance.getApplicationContext());
        KakaoSdk.init(this, getString(R.string.kakao_app_key));
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .name(DBName)
                .deleteRealmIfMigrationNeeded() // 개발 단계 Migration 설정
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mInstance = null;
    }

    private void createFCMChannel(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                channel = new NotificationChannel(CHANNEL_FCM_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
                channel.setVibrationPattern(new long[]{200, 100, 200});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
            }

        } catch (NullPointerException nullException) {
            Utils.ShowToast(getApplicationContext(), "푸시 알림 채널 생성에 실패했습니다. 앱을 재실행하거나 재설치해주세요.", false);
            nullException.printStackTrace();
        }
    }

    private void createDailyChannel(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                channel = new NotificationChannel(CHANNEL_DAILY_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
                channel.setVibrationPattern(new long[]{200, 100, 200});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
            }

        } catch (NullPointerException nullException) {
            Utils.ShowToast(getApplicationContext(), "푸시 알림 채널 생성에 실패했습니다. 앱을 재실행하거나 재설치해주세요.", false);
            nullException.printStackTrace();
        }
    }
}
