/*
package kr.zentry.devdolittle;

import static kr.zentry.devdolittle.MainBaseActivity.threadFlag_row;
import static kr.zentry.devdolittle.common.Utils.REQUEST_ENABLE_BT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import kr.zentry.devdolittle.BluetoothScanner.ScannerListener;
import kr.zentry.devdolittle.R;
import kr.zentry.devdolittle.Realm.AnimalInfo;
import kr.zentry.devdolittle.Realm.DolittleData;
import kr.zentry.devdolittle.common.DolittleAPI;
import kr.zentry.devdolittle.common.Mmath;
import kr.zentry.devdolittle.common.Models.auto_register_md;
import kr.zentry.devdolittle.common.MovingAverageFilter;
import kr.zentry.devdolittle.common.Utils;
import kr.zentry.devdolittle.database.DatabaseHelper;
import kr.zentry.devdolittle.thingy.Thingy;
import kr.zentry.devdolittle.thingy.ThingyService;
import kr.zentry.devdolittle.thingylib.ThingyListener;
import kr.zentry.devdolittle.thingylib.ThingyListenerHelper;
import kr.zentry.devdolittle.thingylib.ThingySdkManager;
import kr.zentry.devdolittle.thingylib.utils.ThingyUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GraphTestActivity extends AppCompatActivity implements ThingySdkManager.ServiceConnectionListener {
    private Button btn_real_time_start;

    private ImageButton img_bluetooth;
    private ImageButton img_reserve;
    private ImageButton img_set;

    private AppCompatButton btn_rr_device;
    private AppCompatButton btn_rr_app;
    private AppCompatButton btn_hr_device;
    private AppCompatButton btn_hr_app;


    private ImageView img_heartRate;
    private ImageView img_RespiratoryRate;

    private TextView tv_battery;
    private TextView tv_RespiratoryRateBPM;
    private TextView tv_heartRateBPM;
    private TextView tv_counting_heartRateBPM;
    private TextView tv_device_respiratoryRateBPM;


    private LineChart lc_RespiratoryChart;
    private LineChart lc_HeartChart;

    private BluetoothDevice mDevice = null;
    private DatabaseHelper mDatabaseHelper;
    private ThingySdkManager mThingySdkManager = null;
    private ThingyService.ThingyBinder mBinder;

    private ConnectingPopupDialog connectingPopupDialog;
    private DeviceConnectingPopup deviceConnectingPopup;

    private Mmath heart_math = new Mmath();
    private Mmath respiratory_math = new Mmath();

    private MovingAverageFilter press_ha;
    private MovingAverageFilter press_avg;
    private MovingAverageFilter sound_ha;
    private MovingAverageFilter sound_avg;
    private MovingAverageFilter sound_avg_min;
    private MovingAverageFilter sound_avg_max;

    private Realm realm;
    private Retrofit retrofit_dolittle;
    private DolittleAPI.DolittleAPIs dolittleAPIs;
    private Gson gson = new Gson();

    private Queue<Double> heart_median = new LinkedList<>();
    private Queue<Double> rest_median = new LinkedList<>();

    private String respiratory_old_timestamp = "";
    private String heart_old_timestamp = "";

    private int respiratory_cnt = 0;
    private int heart_cnt = 0;
    private int respiratory_sw_cnt = 0;
    private int heart_sw_cnt = 0;
    private int timer_second = 0;
    private int heartAvg = 0;
    private int respiratoryavg = 0;

    private final int MEASURE_TIME = 60;
    private long measureStartTime;

    private boolean mStartPlayingAudio = false;
    private boolean mStartPlayingRR = false;
    private boolean pressure_fr_tm_sw = false;
    private boolean pressure_sw = false;
    private boolean pressure_balance_sw = false;
    private boolean pressure_cnt_sw = false;
    private boolean heart_sw = false;
    private boolean heart_timer_is_run = false;
    private boolean respiratory_sw = false;
    private boolean respiratory_timer_is_run = false;
    private boolean respiratory_calc_sw = false;
    private boolean heart_calc_sw = false;
    private boolean isStarted = false;

    private MaterialButtonToggleGroup tg_group;

    private Timer respiratory_timer;
    private Timer heart_timer;
    private Timer get_battery_timer;
    private Timer timerRealTimeMeasure;

    private ProgressBar progress_real_time;

    private MainBaseActivity mainBaseActivity;

    private int rr_graph_val = 500;
    private int hr_graph_val = 500;
    private int rr_bpm = 0;
    private int hr_bpm = 0;

    //핸들러 생성
    private Handler mHandler;
    private Handler pHandler;

    //작업 스레드 관련
    private MeasureDolittleFragment.ThreadMicroPhone threadMicroPhone;
    private MeasureDolittleFragment.ThreadPressure threadPressure;
    private String mHeartTimeStamp;
    private short[] microPhoneData;

    //-1번 소리(로우데이터)
    //-2번 카운팅
    //-측정하는 타입에 따라서 분류
    private int btn_MeasureType_flag;


    //호흡수 로우 데이터
    //-배열로 오지 않고 한개의 값이 온다 ex) 654.57
    private String pressureData;

    private int testInt_m;
    private int testInt_p;
    String mPressureTimeStamp;
    Runnable respirationRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_test);

        img_bluetooth = findViewById(R.id.img_bluetooth);
        btn_real_time_start = findViewById(R.id.btn_real_time_start);


        connectingPopupDialog = new ConnectingPopupDialog(getApplicationContext());
        deviceConnectingPopup = new DeviceConnectingPopup();
        deviceConnectingPopup.getInstance(ThingyUtils.THINGY_BASE_UUID);
        mThingySdkManager = ThingySdkManager.getInstance();

//        progress_real_time = findViewById(R.id.progress_real_time);
//        progress_real_time.setMax(60);

        press_ha = new MovingAverageFilter(10);
        press_avg = new MovingAverageFilter(25);
        sound_ha = new MovingAverageFilter(55);
        sound_avg = new MovingAverageFilter(150);
        sound_avg_min = new MovingAverageFilter(55);
        sound_avg_max = new MovingAverageFilter(55);
        mThingySdkManager.bindService(getApplicationContext(), ThingyService.class);
        getApplicationContext().registerReceiver(mBleStateChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        ThingyListenerHelper.registerThingyListener(getApplicationContext(), mThingyListener);


        //블루투스 연결
        img_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mThingySdkManager != null) {
                    if (mThingySdkManager.isConnected(mDevice)) {
                        mThingySdkManager.disconnectFromThingy(mDevice);
                        mDevice = null;
                        setDevice(mDevice);
                    } else {
                        if (!deviceConnectingPopup.isVisible()) {
                            deviceConnectingPopup.show(getSupportFragmentManager(), null);
                            deviceConnectingPopup.setScannerListener(scannerListener);
                        }
                    }
                }

            }
        });

        //실시간 측정 시작
        btn_real_time_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //측정 중인 상태인 경우 리셋
                if (isStarted) {
                    //스레드 종료
                    threadFlag_row = 2;
//                    realTimeMeasureReset();
                } else {
                    //측정 방식 선택 여부 검사
                    if (btn_MeasureType_flag == 0) {
                        Toast.makeText(getApplicationContext(), "측정 방식을 선택해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    startRealTimeMeasure();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        if (!isBleEnabled()) {
            enableBle();
        }

        if (mThingySdkManager != null) {
            if (mDevice != null) {
                if (mThingySdkManager.isConnected(mDevice)) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        img_bluetooth.setImageResource(R.drawable.icon_bluetooth);
                        DrawableCompat.setTint(img_bluetooth.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.mainColor1));
                    } else {
                        img_bluetooth.setImageResource(R.drawable.icon_bluetooth);
                        img_bluetooth.setColorFilter(getResources().getColor(R.color.mainColor1));
                    }
                } else {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        img_bluetooth.setImageResource(R.drawable.icon_bluetooth_disconnect);
                    } else {
                        img_bluetooth.setImageResource(R.drawable.icon_bluetooth_disconnect);
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    img_bluetooth.setImageResource(R.drawable.icon_bluetooth_disconnect);
                } else {
                    img_bluetooth.setImageResource(R.drawable.icon_bluetooth_disconnect);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDevice != null) {
            ThingyListenerHelper.registerThingyListener(getApplicationContext(), mThingyListener, mDevice);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mAudioRecordBroadcastReceiver, createAudioRecordIntentFilter(mDevice.getAddress()));
        } else {
            ThingyListenerHelper.registerThingyListener(getApplicationContext(), mThingyListener);
        }
    }

    public void setDevice(BluetoothDevice device) {
        mDevice = device;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    private static IntentFilter createAudioRecordIntentFilter(final String address) {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Utils.EXTRA_DATA_AUDIO_RECORD + address);
        intentFilter.addAction(Utils.ERROR_AUDIO_RECORD);
        return intentFilter;
    }

    private BroadcastReceiver mAudioRecordBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.startsWith(Utils.EXTRA_DATA_AUDIO_RECORD)) {
                //final byte[] tempPcmData = intent.getExtras().getByteArray(ThingyUtils.EXTRA_DATA_PCM);
                final short[] tempPcmData = intent.getExtras().getShortArray(ThingyUtils.EXTRA_DATA_PCM);
                final int length = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA);
                if (tempPcmData != null) {
                    if (length != 0) {
                        String TimeStamp = ThingyUtils.TIME_FORMAT.format(System.currentTimeMillis());
//                        addHeartData(TimeStamp, tempPcmData[tempPcmData.length - 1]);
                    }
                }
            } else if (action.equals(Utils.ERROR_AUDIO_RECORD)) {
                final String error = intent.getExtras().getString(Utils.EXTRA_DATA);
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onServiceConnected() {
        mBinder = (ThingyService.ThingyBinder) mThingySdkManager.getThingyBinder();
    }

    final BroadcastReceiver mBleStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    enableBle();
                }
            }
        }
    };


    ThingyListener mThingyListener = new ThingyListener() {

        String mPressureTimeStamp;
        String mHeartTimeStamp;

        @Override
        public void onDeviceConnected(BluetoothDevice device, int connectionState) {
            mDevice = device;
            mainBaseActivity.setDevice(device);
            mThingySdkManager.setSelectedDevice(device);
//            Utils.ShowToast(getActivity(), String.valueOf(device.getName()) + "연결에 성공하였습니다.", false);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                img_bluetooth.setImageResource(R.drawable.icon_bluetooth);
                DrawableCompat.setTint(img_bluetooth.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.mainColor1));
            } else {
                img_bluetooth.setImageResource(R.drawable.icon_bluetooth);
                img_bluetooth.setColorFilter(getResources().getColor(R.color.mainColor1));
            }

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    connectingPopupDialog.dismiss();
                }
            };
            executor.schedule(task, 2, TimeUnit.SECONDS);
            executor.shutdown();
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, int connectionState) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                img_bluetooth.setImageResource(R.drawable.icon_bluetooth_disconnect);
            } else {
                img_bluetooth.setImageResource(R.drawable.icon_bluetooth_disconnect);
            }
            mDevice = null;
            mainBaseActivity.setDevice(null);
//            realTimeMeasureReset();
        }

        @Override
        public void onServiceDiscoveryCompleted(BluetoothDevice device) {
            mThingySdkManager.requestMtu(device);
//            mDatabaseHelper.updateNotificationsState(device.getAddress(), true, DatabaseContract.ThingyDbColumns.COLUMN_NOTIFICATION_PRESSURE);
//            mThingySdkManager.enableSoundNotifications(device, true);


        }

        @Override
        public void onMicrophoneValueChangedEvent(BluetoothDevice bluetoothDevice, short[] data) {
//            testInt_m++;
//            Log.e("Micro!@: ", String.valueOf(data));
//            Log.e("Microphone: ", String.valueOf(testInt_m));
            microPhoneData = data;
        }

        //압력
        @Override
        public void onPressureValueChangedEvent(BluetoothDevice bluetoothDevice, String pressure) {
//            testInt_p++;
//            Log.e("PRESS: ", String.valueOf(testInt_p));
//            Log.e("PRESS!@: ", pressure);
            pressureData = pressure;
        }

        @Override
        public void onBatteryLevelChanged(BluetoothDevice bluetoothDevice, int batteryLevel) {

        }

        @Override
        public void onTemperatureValueChangedEvent(BluetoothDevice bluetoothDevice, String temperature) {

        }

        @Override
        public void onHumidityValueChangedEvent(BluetoothDevice bluetoothDevice, String humidity) {

        }

        @Override
        public void onRealtimeRrValueChangedEvent(BluetoothDevice bluetoothDevice, int RrValue) {
            Log.d("RrValue: ", String.valueOf(RrValue));
            respiratory_cnt = RrValue;
            String TimeStamp = ThingyUtils.TIME_FORMAT.format(System.currentTimeMillis());
//            addRespiratoryEntry(TimeStamp, rr_graph_val + 300);
        }

        //실시간 카운팅 심장박동수 전달
        @Override
        public void onRealtimeHrValueChangedEvent(
                BluetoothDevice bluetoothDevice
                , int HrValue
                , int DbValue) {
            heart_cnt = HrValue;
            Log.d("HrValue: ", String.valueOf(HrValue));
            Log.d("DbValue: ", String.valueOf(DbValue));
            String TimeStamp = ThingyUtils.TIME_FORMAT.format(System.currentTimeMillis());
//            addHeartData(TimeStamp, (float) (hr_graph_val + Math.abs(DbValue)));
        }


        @Override
        public void onAirQualityValueChangedEvent(BluetoothDevice bluetoothDevice, int eco2, int tvoc) {

        }

        @Override
        public void onColorIntensityValueChangedEvent(BluetoothDevice bluetoothDevice, float red, float green, float blue, float alpha) {

        }

        @Override
        public void onButtonStateChangedEvent(BluetoothDevice bluetoothDevice, int buttonState) {

        }

        @Override
        public void onTapValueChangedEvent(BluetoothDevice bluetoothDevice, int direction, int count) {

        }

        @Override
        public void onOrientationValueChangedEvent(BluetoothDevice bluetoothDevice, int orientation) {

        }

        @Override
        public void onQuaternionValueChangedEvent(BluetoothDevice bluetoothDevice, float w, float x, float y, float z) {

        }

        @Override
        public void onPedometerValueChangedEvent(BluetoothDevice bluetoothDevice, int steps, long duration) {

        }

        @Override
        public void onAccelerometerValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

        }

        @Override
        public void onGyroscopeValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

        }

        @Override
        public void onCompassValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

        }

        @Override
        public void onEulerAngleChangedEvent(BluetoothDevice bluetoothDevice, float roll, float pitch, float yaw) {

        }

        @Override
        public void onRotationMatrixValueChangedEvent(BluetoothDevice bluetoothDevice, byte[] matrix) {

        }

        @Override
        public void onHeadingValueChangedEvent(BluetoothDevice bluetoothDevice, float heading) {

        }

        @Override
        public void onGravityVectorChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

        }

        @Override
        public void onSpeakerStatusValueChangedEvent(BluetoothDevice bluetoothDevice, int status) {

        }
    };

    ScannerListener scannerListener = new ScannerListener() {
        @Override
        public void onDeviceSelected(BluetoothDevice device, String name) {
            mDevice = device;
            mThingySdkManager.connectToThingy(getApplicationContext(), device, ThingyService.class);
            connectingPopupDialog.setDeviceName(device.getName());
            connectingPopupDialog.getWindow().setBackgroundDrawable(getApplicationContext().getDrawable(R.drawable.corner_popup));
            connectingPopupDialog.show();
            connectingPopupDialog.setCancelable(false);

            connectingPopupDialog.findViewById(R.id.btn_connecting_device_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (device != null) {
                        mThingySdkManager.disconnectFromThingy(device);
                    }
                    connectingPopupDialog.dismiss();
                    deviceConnectingPopup.dismiss();
                }
            });
        }

        @Override
        public void onNothingSelected() {
        }
    };

//    public void realTimeMeasureReset() {
//        if (timerRealTimeMeasure != null) {
//            timerRealTimeMeasure.cancel();
//        }
//
//        if (mDevice != null) {
//            if (mThingySdkManager.isConnected(mDevice)) {
//                threadFlag_row = 2;
//                realTimeMeasurePause();
//                // heart reset
//                mStartPlayingAudio = false;
//                heart_cnt = 0;
//                heart_sw = false;
//                heart_old_timestamp = "";
//                heart_median.clear();
//                heart_timer_is_run = false;
//
//
//                // res reset
//                mStartPlayingRR = false;
//                respiratory_cnt = 0;
//                respiratory_sw = false;
//                respiratory_old_timestamp = "";
//                rest_median.clear();
//                respiratory_timer_is_run = false;
//            }
//        }
//
//
//        lc_RespiratoryChart.getData().clearValues();
//        lc_RespiratoryChart.invalidate();
//        lc_RespiratoryChart.getAxisLeft().removeAllLimitLines();
//
//        lc_HeartChart.getData().clearValues();
//        lc_HeartChart.invalidate();
//        lc_HeartChart.getAxisLeft().removeAllLimitLines();
//
//        isStarted = false;
//        btn_real_time_start.setText(getString(R.string.obj_measure_dolittle_start));
//        progress_real_time.setProgress(0);
//    }

    private boolean isBleEnabled() {
        final BluetoothManager bm = (BluetoothManager) getApplicationContext().getSystemService(getApplicationContext().BLUETOOTH_SERVICE);
        final BluetoothAdapter ba = bm.getAdapter();
        return ba != null && ba.isEnabled();
    }

    private void enableBle() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }


}*/
