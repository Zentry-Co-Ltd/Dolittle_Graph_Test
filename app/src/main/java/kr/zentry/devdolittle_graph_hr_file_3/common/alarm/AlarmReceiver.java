package kr.zentry.devdolittle_graph_hr_file_3.common.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kr.zentry.devdolittle_graph_hr_file_3.R;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_ALARM_ID = "alarmID";


    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(EXTRA_MESSAGE ) + "일정 알람!!!";
        int alarm_id = intent.getIntExtra(EXTRA_ALARM_ID, -1);
        if (alarm_id > -1){
            showAlarmNotification(context, alarm_id, "일정 알람", message);
//            cancelAlarm(context, alarm_id);
        }
    }

    public void setOneTimeAlarm(Context context, int alarmID, long time, String message){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Date date = new Date(time);
        Log.d("times", date + " !!" );
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int tmp_alarmID = Integer.valueOf (String.valueOf(alarmID) + "0000000" + "0");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, tmp_alarmID, new Intent(context, AlarmReceiver.class).putExtra(EXTRA_ALARM_ID, alarmID).putExtra(EXTRA_MESSAGE, message), 0);
//        Calendar calendar1 = Calendar.getInstance();
//        calendar1.setTime(date);
//        calendar1.add(Calendar.MINUTE, 1);
//        calendar1.set(Calendar.SECOND, 0);
//        calendar1.set(Calendar.MILLISECOND, 0);
//        alarmID = Integer.valueOf (String.valueOf(alarmID) + "0000000" + "1");
//        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, alarmID, new Intent(context, AlarmReceiver.class).putExtra(EXTRA_ALARM_ID, alarmID).putExtra(EXTRA_MESSAGE, message), 0);

        if(alarmManager != null){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);
        }
    }

    public void setRepeatDaysAlarm(Context context, int alarmID, long time, String message, ArrayList<Boolean> weekOnDays){
        ArrayList<PendingIntent> pendingIntents = new ArrayList<>();
        ArrayList<Calendar> calendars = new ArrayList<>();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Date date = new Date(time);
        int idx = 0;
        for (int i=0; i<weekOnDays.size(); i++){
            if (weekOnDays.get(i)){
                int week = 0;
                if (i == 6) {
                    week = 1;
                } else {
                    week = i+2;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.DAY_OF_WEEK, week);
                calendars.add(calendar);
                int tmp_alarmID = Integer.valueOf (String.valueOf(alarmID) + "0000000" + String.valueOf(idx));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, tmp_alarmID, new Intent(context, AlarmReceiver.class).putExtra(EXTRA_ALARM_ID, alarmID).putExtra(EXTRA_MESSAGE, message), 0);
                pendingIntents.add(pendingIntent);
                idx+=1;
                Log.d("times", calendar.getTime() + " !!!!!!!!! 예약");
            }
        }

        for (int i=0; i<pendingIntents.size(); i++){
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendars.get(i).getTimeInMillis(),AlarmManager.INTERVAL_DAY*7, pendingIntents.get(i));
        }
    }

    public void cancelAlarm(Context context, int alarmID){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        for (int i=0; i<7; i++){
            int tmp_alarmID = Integer.valueOf (String.valueOf(alarmID) + "0000000" + String.valueOf(i));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, tmp_alarmID, intent, PendingIntent.FLAG_NO_CREATE);

            if (alarmManager != null) {
                if (pendingIntent != null){
                    pendingIntent.cancel();
                    alarmManager.cancel(pendingIntent);
                    Log.d("times", tmp_alarmID + "당신이 !!!!!!!!! 죽였어!!!!!!!");
                }
            }
        }

    }

    private void showAlarmNotification(Context context, int animalID, String title, String message){
        String CHANNEL_ID = "AlarmChannel";
        String CHANNEL_NAME = "AlarmManagerChannel";

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.img_dolittle_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});

            builder.setChannelId(CHANNEL_ID);

            if (notificationManagerCompat != null){
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }

        Notification notification = builder.build();

        if(notificationManagerCompat != null){
            notificationManagerCompat.notify(animalID, notification);
        }
    }
}
