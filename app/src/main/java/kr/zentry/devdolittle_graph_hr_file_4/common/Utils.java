package kr.zentry.devdolittle_graph_hr_file_4.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kr.zentry.devdolittle_graph_hr_file_4.R;

public class Utils {
    public static final String TAG = "DOLITTLE";
    private static final String PREFS_DOLITTLE = "PREFS_DOLITTLE";
    private static final String INSTANCE_ACCESS_VALUE = "SESSION_VALUE"; // string
    private static final String INSTANCE_REFRESH_VALUE = "REFRESH_VALUE"; // string
    private static final String INSTANCE_OWNER_VALUE = "OWNER_VALUE"; // string
    private static final String INSTANCE_MEASURE_TIME = "MEASURE_TIME"; // int
    private static final String INSTANCE_EVENT_PUSH_AGREE = "EVENT_PUSH_AGREE"; // bool
    private static final String INSTANCE_ANIMAL_VALUE_CNT = "ANIMAL_VALUE_CNT"; // int
    private static final String INSTANCE_SELECTED_ANIMAL = "SELECTED_ANIMAL"; // string
    private static final String INSTANCE_SELECTED_ANIMAL_ID = "SELECTED_ANIMAL_ID"; // string
    private static final String INSTANCE_USER_NICKNAME = "USER_NICKNAME"; // string
    private static final String INSTANCE_SCHEDULE_VALUE_CNT = "SCHEDULE_VALUE_CNT"; // int
    private static final String INSTANCE_USER_EMAIL = "USER_EMAIL"; // string


    public static final String ENVIRONMENT_FRAGMENT = "ENVIRONMENT_FRAGMENT";
    public static final String MOTION_FRAGMENT = "MOTION_FRAGMENT";
    public static final String UI_FRAGMENT = "UI_FRAGMENT";
    public static final String SOUND_FRAGMENT = "SOUND_FRAGMENT";
    public static final String CLOUD_FRAGMENT = "CLOUD_FRAGMENT";
    public static final String PROGRESS_DIALOG_TAG = "PROG_DIALOG";
    public static final String NFC_DIALOG_TAG = "NFC_DIALOG";

    //Notification constants
    public static final String ACTION_DISCONNECT = "ACTION_DISCONNECT";
    public static final int DISCONNECT_REQ = 501;
    public static final String THINGY_GROUP_ID = "THINGY_GROUP_ID";
    public static final int NOTIFICATION_ID = 502;
    public static final int OPEN_ACTIVITY_REQ = 503;

    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    public static final String EXTRA_DEVICE_NAME = "EXTRA_DEVICE_NAME";

    public static final String START_RECORDING = "START_RECORDING";
    public static final String STOP_RECORDING = "STOP_RECORDING";
    public static final String EXTRA_DATA_AUDIO_RECORD = "EXTRA_DATA_AUDIO_RECORD";
    public static final String ERROR_AUDIO_RECORD = "ERROR";

    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String EXTRA_DATA_TYPE = "EXTRA_DATA_TYPE";
    public static final String EXTRA_DATA_URL = "EXTRA_DATA_URL";


    public static final int REQUEST_ENABLE_BT = 1020;
    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1021;
    public static final int REQUEST_ACCESS_FINE_LOCATION = 1022;
    public static final int REQ_PERMISSION_WRITE_EXTERNAL_STORAGE = 1023;
    public static final int REQ_PERMISSION_READ_EXTERNAL_STORAGE = 1024;
    public static final int REQ_PERMISSION_RECORD_AUDIO = 1024;

    public static final int DOUBLE_CLICK_DURATION = 250;

    public static final int GPS_ENABLE_REQUEST_CODE = 2001;
    public static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final char HANGUL_BEGIN_UNICODE = 44032;
    private static final char HANGUL_LAST_UNICODE = 55203;
    private static final char HANGUL_BASE_UNIT = 588;
    private static final char[] INITIAL_SOUND = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

    public static final float CHART_LINE_WIDTH = 1.5f;
    public static final float CHART_CANDIDATE_LINE_WIDTH = 2.0f;
    public static final float CHART_FINAL_LINE_WIDTH = 1.0f;
    public static final float CHART_VALUE_TEXT_SIZE = 6.5f;

    public static String getTime() {
        String ret = "";
        Timestamp tt = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("mmssHH");
        ret = sdf.format(tt);

        return ret;
    }

    private static boolean isInitialSound(char searchar) {
        for (char c : INITIAL_SOUND) {
            if (c == searchar) {
                return true;
            }
        }
        return false;
    }

    private static char getInitialSound(char c) {
        int hanBegin = (c - HANGUL_BEGIN_UNICODE);
        int index = hanBegin / HANGUL_BASE_UNIT;
        return INITIAL_SOUND[index];
    }

    /**
     * 해당 문자가 한글인지 검사 * @param c 문자 하나 * @return
     */
    private static boolean isHangul(char c) {
        return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE;
    }

    public static boolean matchString(String value, String search) {
        int t = 0;
        int seof = value.length() - search.length();
        int slen = search.length();
        if (seof < 0) return false;
        for (int i = 0; i <= seof; i++) {
            t = 0;
            while (t < slen) {
                if (isInitialSound(search.charAt(t)) == true && isHangul(value.charAt(i + t))) {
                    if (getInitialSound(value.charAt(i + t)) == search.charAt(t))
                        t++;
                    else
                        break;
                } else {
                    if (value.charAt(i + t) == search.charAt(t))
                        t++;
                    else
                        break;
                }
            }
            if (t == slen)
                return true;
        }
        return false;
    }

    public static void ShowToast(Context context, String text, boolean isLong) {
        if (isLong) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public static String getEncrypt(String s1, String s2) {
        return getEncMD5(s1 + s2);
    }

    public static String getEncMD5(String str) {
        String MD5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            MD5 = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            MD5 = null;
        }
        return MD5;
    }

    public static Date convertServerToLocal(Context context, String s1) throws ParseException {
        SimpleDateFormat serverFormat = new SimpleDateFormat(context.getString(R.string.msg_date_mili_format));
        SimpleDateFormat localFormat = new SimpleDateFormat(context.getString(R.string.msg_date_sec_format));
        Date date = serverFormat.parse(s1);
        String convert = localFormat.format(date);
        Date ret = localFormat.parse(convert);
        return ret;
    }

    public static String getDateFormat(Calendar calendar) {
        SimpleDateFormat dateSDF = new SimpleDateFormat("yyyy - MM - dd (E)");
        return dateSDF.format(calendar.getTimeInMillis());
    }

    public static String getDatePointFormat(Calendar calendar) {
        SimpleDateFormat dateSDF = new SimpleDateFormat("yyyy.MM.dd");
        return dateSDF.format(calendar.getTimeInMillis());
    }

    public static String getDatePointFormat(long time) {
        SimpleDateFormat dateSDF = new SimpleDateFormat("yyyy.MM.dd");
        return dateSDF.format(time);
    }

    public static String getTimeFormat(Calendar calendar) {
        SimpleDateFormat timeSDF = new SimpleDateFormat("a hh : mm");
        return timeSDF.format(calendar.getTimeInMillis());
    }

    public static boolean setAccessValue(Context context, String session) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(INSTANCE_ACCESS_VALUE, session);
        return editor.commit();
    }

    public static boolean clearPreferences(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        return editor.commit();
    }

    public static boolean setRefreshValue(Context context, String session) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(INSTANCE_REFRESH_VALUE, session);
        return editor.commit();
    }

    public static boolean setOwnerValue(Context context, String owner) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(INSTANCE_OWNER_VALUE, owner);
        return editor.commit();
    }

    public static boolean setMeasureTime(Context context, int measureTime) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putInt(INSTANCE_MEASURE_TIME, measureTime);
        return editor.commit();
    }

    public static boolean setEventPushAgree(Context context, boolean agree) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(INSTANCE_EVENT_PUSH_AGREE, agree);
        return editor.commit();
    }

    public static boolean setAnimalCount(Context context, int cnt) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putInt(INSTANCE_ANIMAL_VALUE_CNT, cnt);
        return editor.commit();
    }

    public static boolean setScheduleCount(Context context, int cnt) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putInt(INSTANCE_SCHEDULE_VALUE_CNT, cnt);
        return editor.commit();
    }

    public static boolean setSelectedAnimal(Context context, String name) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(INSTANCE_SELECTED_ANIMAL, name);
        return editor.commit();
    }

    public static boolean setSelectedAnimalID(Context context, String animalId) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(INSTANCE_SELECTED_ANIMAL_ID, animalId);
        return editor.commit();
    }

    public static boolean setUserName(Context context, String nickname) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(INSTANCE_USER_NICKNAME, nickname);
        return editor.commit();
    }

    public static boolean setUserEmail(Context context, String eMail) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(INSTANCE_USER_EMAIL, eMail);
        return editor.commit();
    }

    public static String getAccessValue(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        return sp.getString(INSTANCE_ACCESS_VALUE, "");
    }

    public static String getRefreshValue(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        return sp.getString(INSTANCE_REFRESH_VALUE, "");
    }

    public static String getOwnerValue(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        return sp.getString(INSTANCE_OWNER_VALUE, "");
    }

    public static int getMeasureTime(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        return sp.getInt(INSTANCE_MEASURE_TIME, 60);
    }

    public static boolean getEventPushAgree(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        return sp.getBoolean(INSTANCE_EVENT_PUSH_AGREE, false);
    }

    public static int getAnimalCount(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        return sp.getInt(INSTANCE_ANIMAL_VALUE_CNT, 0);
    }

    public static int getScheduleCount(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        return sp.getInt(INSTANCE_SCHEDULE_VALUE_CNT, 0);
    }

    public static String getSelectedAnimal(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        return sp.getString(INSTANCE_SELECTED_ANIMAL, null);
    }

    public static String getSelectedAnimalID(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        return sp.getString(INSTANCE_SELECTED_ANIMAL_ID, null);
    }

    public static String getUserName(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        return sp.getString(INSTANCE_USER_NICKNAME, null);
    }

    public static String getUserEmail(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(PREFS_DOLITTLE, Context.MODE_PRIVATE);
        return sp.getString(INSTANCE_USER_EMAIL, null);
    }


    public static void subscribeToTopic(String topic, Boolean isSubscribe) {
        if (isSubscribe) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
        }
    }

    public static class DecimalValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    }

    public static boolean checkIfVersionIsOreoOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static BluetoothDevice getBluetoothDevice(final Context context, final String address) {
        final BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter ba = bm.getAdapter();
        if (ba != null /*&& ba.isEnabled()*/) {
            try {
                return ba.getRemoteDevice(address);
            } catch (Exception ex) {
                return null;
            }
        }
        return null; //ideally shouldn't go here
    }
}
