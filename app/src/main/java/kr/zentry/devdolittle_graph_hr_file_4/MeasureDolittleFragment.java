package kr.zentry.devdolittle_graph_hr_file_4;

import static java.lang.Math.abs;
import static kr.zentry.devdolittle_graph_hr_file_4.MainBaseActivity.threadFlag_row;
import static kr.zentry.devdolittle_graph_hr_file_4.common.Utils.REQUEST_ENABLE_BT;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kr.zentry.devdolittle_graph_hr_file_4.BluetoothScanner.ScannerListener;
import kr.zentry.devdolittle_graph_hr_file_4.common.Utils;
import kr.zentry.devdolittle_graph_hr_file_4.database.DatabaseHelper;
import kr.zentry.devdolittle_graph_hr_file_4.thingy.Thingy;
import kr.zentry.devdolittle_graph_hr_file_4.thingy.ThingyService;
import kr.zentry.devdolittle_graph_hr_file_4.thingylib.ThingyListener;
import kr.zentry.devdolittle_graph_hr_file_4.thingylib.ThingyListenerHelper;
import kr.zentry.devdolittle_graph_hr_file_4.thingylib.ThingySdkManager;
import kr.zentry.devdolittle_graph_hr_file_4.thingylib.utils.ThingyUtils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/* ????????? ????????? ??? ?????? */
public class MeasureDolittleFragment extends Fragment implements ThingySdkManager.ServiceConnectionListener {

    private Button btn_real_time_start;
    private TextView tv_bpm;
    private TextView tv_rr_bpm;
    private ImageButton img_bluetooth;


    private LineChart lc_chart1;
    private LineChart lc_chart2;
    private LineChart lc_chart3;

    private BluetoothDevice mDevice = null;
    private DatabaseHelper mDatabaseHelper;
    private ThingySdkManager mThingySdkManager = null;
    private ThingyService.ThingyBinder mBinder;

    private ConnectingPopupDialog connectingPopupDialog;
    private DeviceConnectingPopup deviceConnectingPopup;


    private String heart_old_timestamp = "";

    private int respiratory_cnt = 0;
    private int heart_cnt = 0;

    private final int MEASURE_TIME = 60;

    private boolean heart_sw = false;
    private boolean heart_timer_is_run = false;
    private boolean respiratory_sw = false;
    private boolean respiratory_calc_sw = false;
    private boolean heart_calc_sw = false;
    private boolean isStarted = false;


    private Timer timerRealTimeMeasure;


    private MainBaseActivity mainBaseActivity;


    private Handler pHandler;

    //?????? ????????? ??????
    private ThreadGraphRowData threadGraphRowData;

    private ThreadHandleGraphUpdate threadHandleGraphUpdate;

    //-1??? ??????(???????????????)
    //-2??? ?????????
    //-???????????? ????????? ????????? ??????


    //??????, ?????? ??? ?????? ????????? (??? ???)
    private float current_row_Data;
    private float aver_row_Data;
    private float thres_row_Data;
    //??????, ?????? ??? ????????? ?????????
    private int count_candidate;
    //??????, ?????? ??? ?????? ?????????
    private int count_final;

    private RadioGroup rg_hr_rr;
    private RadioButton rb_hr;
    private RadioButton rb_rr;

    /*?????? ?????? ?????? ??????*/
    StringBuilder sb;
    ArrayList<RowData> rowDatas = new ArrayList<>();


    /* ??????*/
    private long start_time = 0; //?????? ?????? ??? ????????? ??????

    /* ?????? */
    private ArrayDeque<Float> hr_interval_Deque = new ArrayDeque<>();
    private int hr_cnt = 0;
    private long hr_old_interval_time = 0L;
    private int hr_sec_old = 0; // ?????? ?????? ?????? ???(??????) //?????? ?????? ????????? ?????? ??????
    private int hr_sec_refresh = 0; // ?????? ?????? ?????? ???(??????)
    private int hr_bpm = 0;

    /* ?????? */
    private ArrayDeque<Float> rr_interval_Deque = new ArrayDeque<>();
    private int rr_cnt = 0;
    private long rr_old_interval_time = 0L;
    private int rr_sec_old = 0;
    private int rr_sec_refresh = 0;
    private int rr_bpm = 0;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sb = new StringBuilder();

        mainBaseActivity = (MainBaseActivity) getActivity();
        mDatabaseHelper = mainBaseActivity.getDatabaseHelper();
        mThingySdkManager = mainBaseActivity.getThingySdkManager();
        mBinder = mainBaseActivity.getThingyBinder();
        mDevice = mainBaseActivity.getDevice();

        mThingySdkManager.bindService(getActivity(), ThingyService.class);
        ThingyListenerHelper.registerThingyListener(getActivity(), mThingyListener);
        getActivity().registerReceiver(mBleStateChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pHandler = new Handler();
        checkPermission();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_measure_dolittle, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lc_chart1 = view.findViewById(R.id.lc_Chart1_test);
        lc_chart2 = view.findViewById(R.id.lc_Chart2_test);
        lc_chart3 = view.findViewById(R.id.lc_Chart3_test);

        lc_chart3 = view.findViewById(R.id.lc_Chart3_test);
        lc_chart3 = view.findViewById(R.id.lc_Chart3_test);
        //??????, ?????? ?????? ???????????????
        rg_hr_rr = view.findViewById(R.id.rg_hr_rr);
        rb_hr = view.findViewById(R.id.rb_hr);
        rb_rr = view.findViewById(R.id.rb_rr);
        //??????, ??????  Bpm TextView
        tv_bpm = view.findViewById(R.id.tv_bpm);

        img_bluetooth = view.findViewById(R.id.img_bluetooth);
        btn_real_time_start = view.findViewById(R.id.btn_real_time_start);
        connectingPopupDialog = new ConnectingPopupDialog(getActivity());
        deviceConnectingPopup = new DeviceConnectingPopup();
        deviceConnectingPopup.getInstance(ThingyUtils.THINGY_BASE_UUID);


        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);


        //?????? ??? ?????? ????????? ?????? ??????

        //1.??????
        initGraph(lc_chart1, R.color.zentry_blue, XAxis.XAxisPosition.BOTTOM);
        initGraph(lc_chart2, R.color.zentry_blue, XAxis.XAxisPosition.BOTTOM);
        initGraph(lc_chart3, R.color.zentry_blue, XAxis.XAxisPosition.TOP);
//        initGraphThres(lc_chart2, R.color.zentry_red, XAxis.XAxisPosition.BOTTOM);

        //2.??????
     /*   initGraph(lc_chart1, R.color.zentry_red, XAxis.XAxisPosition.BOTTOM);
        initGraph(lc_chart2, R.color.zentry_red, XAxis.XAxisPosition.BOTTOM);
        initGraph(lc_chart3, R.color.zentry_red, XAxis.XAxisPosition.TOP);*/


        //        rg_hr_rr.check(binding.);

        //???????????? ??????
        img_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                if (mThingySdkManager != null) {
                    if (mThingySdkManager.isConnected(mDevice)) {
                        mThingySdkManager.disconnectFromThingy(mDevice);
                        mDevice = null;
                        ((MainBaseActivity) getActivity()).setDevice(mDevice);
                    } else {
                        if (!deviceConnectingPopup.isVisible()) {
                            deviceConnectingPopup.show(getActivity().getSupportFragmentManager(), null);
                            deviceConnectingPopup.setScannerListener(scannerListener);
                        }
                    }
                }

            }
        });

        //????????? ?????? ??????
        btn_real_time_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????? ?????? ????????? ?????? ??????
                if (isStarted) {
                    //?????? ?????? ??????
                    writeFileOutStorage(getCurrentTime_file());

                    //????????? ??????
                    threadFlag_row = 2;
                    realTimeMeasureReset(lc_chart1);
                    realTimeMeasureReset(lc_chart2);
                    realTimeMeasureReset(lc_chart3);
                } else {
                    startRealTimeMeasure("START");
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
                        DrawableCompat.setTint(img_bluetooth.getDrawable(), ContextCompat.getColor(getActivity(), R.color.mainColor1));
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
            ThingyListenerHelper.registerThingyListener(getActivity(), mThingyListener, mDevice);
        } else {
            ThingyListenerHelper.registerThingyListener(getActivity(), mThingyListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ThingyListenerHelper.unregisterThingyListener(getActivity(), mThingyListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //?????? ?????? ??????
        threadFlag_row = 2;
        realTimeMeasureReset(lc_chart1);
        realTimeMeasureReset(lc_chart2);
        realTimeMeasureReset(lc_chart3);

        if (mThingySdkManager.isConnected(mDevice)) {
            mThingySdkManager.disconnectFromThingy(mDevice);
            mDevice = null;
            ((MainBaseActivity) getActivity()).setDevice(mDevice);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //????????? ??????
        threadFlag_row = 2;
    }

    private String getCurrentTime_file() {

        // ?????? ??????/??????
        Date now = new Date();

        // ?????? ??????/?????? ??????
        System.out.println(now); // Thu Jun 17 06:57:32 KST 2021

        // ????????? ??????
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy??? MM??? dd??? HH??? mm??? ss???", Locale.KOREA);

        // ????????? ??????
        String formatedNow = formatter.format(now);

        return "??????-" + formatedNow.replaceAll(" ", "") + ".txt";
    }


    private int calcCountBpm(ArrayDeque<Float> interval_Deque,
                             long new_time,
                             long old_time,
                             float total_sec,
                             String type) {

        /* 1.??? ??????  ?????? interval ?????? */
        float interval_sum = 0;
        float interval_aver = 0;
        float interval = 0;
        float bpm = 0;
        //?????? ?????? ???
        if (interval_Deque.size() == 0) {
            interval_Deque.add((float) (new_time - start_time));
        } else {
            //?????? ??? ?????????
            interval = new_time - old_time;

            //0.3??? ?????? ?????? ??? ??? ?????????
            if (interval < 300) {
                return -1;
            }

            //30??? ??????
            //30????????? ?????? 10??? ?????? ->
            if (total_sec > 30 && interval_Deque.size() > 10) {
                interval_Deque.removeLast();
                interval_Deque.addFirst(interval);
            } else
            //30??? ??????
            {
                interval_Deque.add(interval);
            }
        }

        /* ????????? */
        //??????
        if (type.equals("HR")) {
            hr_old_interval_time = new_time;
        }
        //??????
        else if (type.equals("RR")) {
            rr_old_interval_time = new_time;
        }

        Log.e("realTimeMeasureStar", "total_sec: " + total_sec);

        /* 2.BPM ????????? ??? View??? ?????? */
        //??????
        for (float interval2 : interval_Deque) {
            interval_sum += interval2;
        }

        interval_aver = interval_sum / (interval_Deque.size() * 1000);
        try {
            bpm = (60 / interval_aver);
        } catch (Exception e) {
            bpm = 0;
        }

        return (int) bpm;
    }

    public void startRealTimeMeasure(String type) {
        if (mDevice != null) {
            if (mThingySdkManager.isConnected(mDevice)) {
                int maxWaitTime_hr = 7;
                int maxWaitTime_rr = 20;
                if (type.equals("START")) {
                    resetBpm("ALL");
                }
                //?????????(?????????)??? ????????? ????????????
                mThingySdkManager.enableRealtimeRrNotifications(mDevice, true); //?????? ????????? ?????? ??????
                mThingySdkManager.enableRrGraphRowDataNotifications(mDevice, true); //?????? ????????? ?????? ??????
//                mThingySdkManager.enableRealtimeHrNotifications(mDevice, true); //?????? ????????? ?????? ??????
//                mThingySdkManager.enableHrGraphRowDataNotifications(mDevice, true); //?????? ????????? ?????? ??????

                /*?????? ?????? ??? ????????? ????????? ?????? ?????? (????????????)*/
                //?????? ????????? ?????? ????????? ??????
                threadFlag_row = 1;

                threadGraphRowData = new ThreadGraphRowData();
                threadGraphRowData.setDaemon(true);
                threadGraphRowData.start();

                threadHandleGraphUpdate = new ThreadHandleGraphUpdate();
                threadHandleGraphUpdate.setDaemon(true);
                threadHandleGraphUpdate.start();


                TimerTask realTimeTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            hr_sec_refresh += 1;
                            rr_sec_refresh += 1;

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("startRealtimeMeasure: ", "resetBpm()");
                                    //?????? ?????? ??????
                                    //7??? ??????
                                 /*   if (hr_sec_refresh - hr_sec_old > maxWaitTime_hr) {
                                        resetBpm("HR");
                                    }*/
                                    //20??? ??????
                                    if (rr_sec_refresh - rr_sec_old > maxWaitTime_rr) {
                                        resetBpm("RR");
                                    }
                                }
                            });
                        }
                        ;
                    }
                };

                isStarted = true;
                btn_real_time_start.setText(getString(R.string.obj_measure_dolittle_cancel));
                timerRealTimeMeasure = new Timer();
                timerRealTimeMeasure.schedule(realTimeTask, 0, 1000);

            } else {
                Toast.makeText(getActivity(), getString(R.string.glb_device_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.glb_device_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    //?????? ?????????
    //-????????? ????????? ?????? ??? ??????
    //-????????? ???????????? ??????
    private void resetBpm(String type) {
        start_time = getCurrentTime();

        /*1.?????? ?????????*/
        if (type.equals("HR") || type.equals("ALL")) {
            hr_interval_Deque.clear();
            hr_sec_refresh = 0;
            hr_sec_old = 0;
            hr_old_interval_time = 0;
            hr_bpm = 0;
            tv_bpm.setText("--");
        }

        /*2.?????? ?????????*/
        if (type.equals("RR") || type.equals("ALL")) {
            rr_interval_Deque.clear();
            rr_sec_refresh = 0;
            rr_sec_old = 0;
            rr_old_interval_time = 0;
            rr_bpm = 0;
            tv_bpm.setText("--");
        }

    }

    public void realTimeMeasureReset(LineChart lc_Chart) {
        if (timerRealTimeMeasure != null) {
            timerRealTimeMeasure.cancel();
        }

        if (mDevice != null) {
            if (mThingySdkManager.isConnected(mDevice)) {
                threadFlag_row = 2;
                realTimeMeasurePause();
                Log.e("?????? ??????", "realTimeMeasurePause()");

                heart_cnt = 0;
                heart_sw = false;


                // res reset
                respiratory_cnt = 0;
                respiratory_sw = false;
            }
        }
        current_row_Data = 0;
        aver_row_Data = 0;
        thres_row_Data = 0;
        count_candidate = 0;
        count_final = 0;

        isStarted = false;
        btn_real_time_start.setText(getString(R.string.obj_measure_dolittle_start));

        lc_Chart.fitScreen();
        lc_Chart.getData().clearValues();
        lc_Chart.getXAxis().setValueFormatter(null);
        lc_Chart.getAxisLeft().removeAllLimitLines();
        lc_Chart.getAxisRight().removeAllLimitLines();
        lc_Chart.getXAxis().removeAllLimitLines();
        lc_Chart.getLineData().clearValues();
        lc_Chart.notifyDataSetChanged();
        lc_Chart.invalidate();


    }


    public void realTimeMeasurePause() {
        if (mDevice != null) {
            if (mThingySdkManager.isConnected(mDevice)) {
                mThingySdkManager.enableThingyMicrophone(mDevice, false);
                mThingySdkManager.enablePressureNotifications(mDevice, false);
                mThingySdkManager.enableHrGraphRowDataNotifications(mDevice, false);
                mThingySdkManager.enableRrGraphRowDataNotifications(mDevice, false);
                mThingySdkManager.enableRealtimeRrNotifications(mDevice, false);
                mThingySdkManager.enableRealtimeHrNotifications(mDevice, false);
            }
        }
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


    //????????? ?????????
    @Override
    public void onServiceConnected() {
        mBinder = (ThingyService.ThingyBinder) mThingySdkManager.getThingyBinder();
    }

    //????????? ???????????? ?????????
    class ThreadHandleGraphUpdate extends Thread {
        @Override
        public void run() {
            //0: ??????
            //1: ??????
            while (threadFlag_row == 1) {
                pHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (aver_row_Data != 0) {
                            handleGraphLineUpdates(lc_chart1);
                            handleGraphLineUpdates(lc_chart2);
                            handleGraphLineUpdates(lc_chart3);
                        }
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //?????? ?????? ?????? ???????????? ?????????
    class ThreadGraphRowData extends Thread {
        @Override
        public void run() {
            //0: ??????
            //1: ??????
            while (threadFlag_row == 1) {
                pHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (aver_row_Data != 0) {
                            //??????????????? ???????????? ????????? ??????
//                            addCurrentRowDataEntry(lc_chart1, (float) hr_current_row_Data);
//                            addAverThresDataEntry(lc_chart2, (float) hr_aver_row_Data, hr_thres_row_Data);
//                            addCountDataEntry(lc_chart3, hr_count_candidate, hr_count_final);

                            final LineData data_current = lc_chart1.getLineData();
                            final LineData data_aver_thres = lc_chart2.getLineData();
                            final LineData data_count = lc_chart3.getLineData();


                     /*       //1)???????????????
                            float min_current = data_current.getYMin();
                            float max_current = data_current.getYMax();
                            lc_chart1.getAxisLeft().setAxisMinValue(min_current - 20);
                            lc_chart1.getAxisRight().setAxisMaxValue(max_current + 50);

                            //2)?????? & ????????? ??????
                            float min_aver = data_aver_thres.getYMin();
                            float max_aver = data_aver_thres.getYMax();
                            lc_chart2.getAxisLeft().setAxisMinValue(min_aver - 20);
                            lc_chart2.getAxisLeft().setAxisMaxValue(max_aver + 50);
                            lc_chart2.getAxisRight().setAxisMinValue(min_aver - 20);
                            lc_chart2.getAxisRight().setAxisMaxValue(max_aver + 50);
//                            lc_chart2.getAxisLeft().setAxisMinValue(min_aver - 20);
//                            lc_chart2.getAxisRight().setAxisMaxValue(max_aver + 50);

                            //3)?????????
                            float min_count = data_count.getYMin();
                            float max_count = data_count.getYMax();
                            lc_chart3.getAxisLeft().setAxisMinValue(min_count - 20);
                            lc_chart3.getAxisRight().setAxisMaxValue(max_count + 50);*/
                        }
                    }
                });
                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /* ????????? ????????? */
    ScannerListener scannerListener = new ScannerListener() {
        @Override
        public void onDeviceSelected(BluetoothDevice device, String name) {
            mDevice = device;
            mThingySdkManager.connectToThingy(getActivity(), device, ThingyService.class);
            connectingPopupDialog.setDeviceName(device.getName());
            connectingPopupDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.corner_popup));
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

    /* Thingy ?????? */
    ThingyListener mThingyListener = new ThingyListener() {

        @Override
        public void onDeviceConnected(BluetoothDevice device, int connectionState) {
            mDevice = device;
            mainBaseActivity.setDevice(device);
            mThingySdkManager.setSelectedDevice(device);
//            Utils.ShowToast(getActivity(), String.valueOf(device.getName()) + "????????? ?????????????????????.", false);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                img_bluetooth.setImageResource(R.drawable.icon_bluetooth);
                DrawableCompat.setTint(img_bluetooth.getDrawable(), ContextCompat.getColor(getActivity(), R.color.mainColor1));
            } else {
                img_bluetooth.setImageResource(R.drawable.icon_bluetooth);
                img_bluetooth.setColorFilter(getResources().getColor(R.color.mainColor1));
            }
            updateSelectionInDb(new Thingy(device), true);

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    connectingPopupDialog.dismiss();
                }
            };
            executor.schedule(task, 2, TimeUnit.SECONDS);
            executor.shutdown();
            mThingySdkManager.requestMtu(device);
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
            realTimeMeasureReset(lc_chart1);
            realTimeMeasureReset(lc_chart2);
            realTimeMeasureReset(lc_chart3);
            //?????? ?????? ??????

        }

        @Override
        public void onServiceDiscoveryCompleted(BluetoothDevice device) {
//            mThingySdkManager.setMeasurementDelete(mDevice, 1);
        }

        @Override
        public void onMicrophoneValueChangedEvent(BluetoothDevice bluetoothDevice, short[] data) {
        }


        //??????
        @Override
        public void onPressureValueChangedEvent(BluetoothDevice bluetoothDevice, String pressure) {
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

        //????????? ????????? ??????????????? ??????
        @Override
        public void onRealtimeHrValueChangedEvent(
                BluetoothDevice bluetoothDevice
                , int HrValue
                , int DbValue) {
            if (HrValue != hr_cnt) {
                heart_cnt = HrValue;
                hr_sec_old = hr_sec_refresh;
                if (HrValue != 0) {
                    hr_bpm = calcCountBpm(
                            hr_interval_Deque,
                            getCurrentTime(),
                            hr_old_interval_time,
                            hr_sec_refresh,
                            "HR"
                    );
                    /* 1.????????? ??? ?????? */
                    //1) 0.3??? ????????? ????????? ??? ??????
                    if (hr_bpm == -1) {
                        return;
                    }

                    //2) ???????????? 3??? ?????? ??? ??????
                    if (rr_sec_refresh < 3) {
                        return;
                    }


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // runOnUiThread??? ???????????? ??? ?????? UI????????? ??????.
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_bpm.setText(String.valueOf(hr_bpm));
                                }
                            });
                        }
                    }).start();
                }
            }

        }

        @Override
        public void onRealtimeRrValueChangedEvent(BluetoothDevice bluetoothDevice, int RrValue) {
            if (rr_cnt != RrValue) {
                rr_cnt = RrValue;
                //?????? ?????? ??????
                rr_sec_old = rr_sec_refresh;
                if (RrValue != 0) {
                    /* 1.????????? ??? ?????? */
                    rr_bpm = calcCountBpm(
                            rr_interval_Deque,
                            getCurrentTime(),
                            rr_old_interval_time,
                            rr_sec_refresh,
                            "RR"
                    );
                    //1) 0.3??? ????????? ????????? ??? ??????
                    if (rr_bpm == -1) {
                        return;
                    }

                    //2) ???????????? 3??? ??????  View??? ?????? X
                    if (rr_sec_refresh < 3) {
                        return;
                    }


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // runOnUiThread??? ???????????? ??? ?????? UI????????? ??????.
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_bpm.setText(String.valueOf(rr_bpm));
                                }
                            });
                        }
                    }).start();
                }
            }

        }

        //?????? ??????????????? ????????? ???????????? ????????? ??????
        @Override
        public void onHrRowDataGraphChangedEvent(BluetoothDevice bluetoothDevice,
                                                 int mCurrentData,
                                                 float mAverData,
                                                 float mThreshold,
                                                 int mCountCandidate,
                                                 int mCountFinal) {
            current_row_Data = mCurrentData;
            aver_row_Data = mAverData;
            thres_row_Data = mThreshold;
            count_candidate = mCountCandidate;
            count_final = mCountFinal;
            //?????? ???????????? ???????????? ????????? ??????????????? ????????????.
            int countCandidate_convert = 0;
            int countFinal_convert = 0;

            if (isStarted) {
                addCurrentRowDataEntry(lc_chart1, current_row_Data);
                addAverThresDataEntry(lc_chart2, aver_row_Data, thres_row_Data);
                addCountDataEntry(lc_chart3, count_candidate, count_final);
                //ui ???????????? ???????????? ????????? ????????? ?????? ?????????????????? ????????? ?????? ??? ??????.
            }


            //1)mCountCandidate(????????? ?????????)
            if (mCountCandidate == 0) {
                countCandidate_convert = -300;  //?????? ??????
            } else if (Math.abs(mCountCandidate) == 1) {
                countCandidate_convert = -1000;
            }

            //2)mCountFinal(?????? ?????????)
            if (mCountFinal == 0) {
                countFinal_convert = -1000;
            } else if (Math.abs(mCountFinal) == 1) {
                countFinal_convert = -1500;
            }

            rowDatas.add(new RowData(
                    String.valueOf(mCurrentData),
                    String.valueOf(mAverData),
                    String.valueOf(mThreshold),
                    String.valueOf(countCandidate_convert),
                    String.valueOf(countFinal_convert)
            ));


    /*        Log.e("1mCurrentData: ", String.valueOf(mCurrentData));
            Log.e("1mAverData: ", String.valueOf(mAverData));
            Log.e("1mThreshold: ", String.valueOf(mThreshold));
            Log.e("1mCountCandidate: ", String.valueOf(mCountCandidate));
            Log.e("1mCountFinal:  mCountFinal: ", String.valueOf(mCountFinal));*/
        }

        //?????? ??????????????? ????????? ???????????? ????????? ??????
        @Override
        public void onRrRowDataGraphChangedEvent(BluetoothDevice bluetoothDevice, int mCurrentData, float mAverData, float mThreshold, int mCountCandidate, int mCountFinal) {
            current_row_Data = mCurrentData;
            aver_row_Data = mAverData;
            thres_row_Data = mThreshold;
            count_candidate = mCountCandidate;
            count_final = mCountFinal;


            //?????? ???????????? ???????????? ????????? ??????????????? ????????????.
            int countCandidate_convert = 0;
            int countFinal_convert = 0;

            // Thread??? ????????????.
            // ????????? ????????? ????????? ????????? ????????????
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // runOnUiThread??? ???????????? ??? ?????? UI????????? ??????.
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addCountDataEntry(lc_chart3, count_candidate, count_final);
                            addCurrentRowDataEntry(lc_chart1, current_row_Data);
                            addAverThresDataEntry(lc_chart2, aver_row_Data, thres_row_Data);
                        }
                    });
                }
            }).start();
            Log.e("mCountCandidate: ", String.valueOf(mCountCandidate));

            //1)mCountCandidate
            if (mCountCandidate == 0) {
                countCandidate_convert = -300;
            } else if (Math.abs(mCountCandidate) == 1) {
                countCandidate_convert = -1000;
            }

            //2)mCountFinal
            if (mCountFinal == 0) {
                countFinal_convert = -1000;
            } else if (Math.abs(mCountFinal) == 1) {
                countFinal_convert = -1500;
            }

            rowDatas.add(new RowData(
                    String.valueOf(mCurrentData),
                    String.valueOf(mAverData),
                    String.valueOf(mThreshold),
                    String.valueOf(countCandidate_convert),
                    String.valueOf(countFinal_convert)
            ));
//            Log.e("1mCurrentData: ", String.valueOf(mCurrentData));
//            Log.e("1mAverData: ", String.valueOf(mAverData));
//            Log.e("1mThreshold: ", String.valueOf(mThreshold));
//            Log.e("1mCountCandidate: ", String.valueOf(countCandidate_convert));
//            Log.e("1mCountFinal: ", String.valueOf(countFinal_convert));
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

    private Long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private synchronized void handleGraphLineUpdates(LineChart lineChart) {
        final LineData lineData = lineChart.getData();

        if (lineData.getEntryCount() > ThingyUtils.MAX_VISISBLE_GRAPH_ENTRIES) {
            ILineDataSet set = lineData.getDataSetByIndex(0);

            if (set != null) {
                if (set.removeFirst()) {
                    for (int i = 0; i < set.getEntryCount(); i++) {
                        Entry entry = set.getEntryForIndex(i);
                        if (entry != null) {
                            entry.setX(i);
                            entry.setY(entry.getY());
                        }
                    }
//                    lineData.notifyDataChanged();
                }
            }

            ILineDataSet set2 = lineData.getDataSetByIndex(1);
            if (set2 != null) {
                if (set2.removeFirst()) {
                    for (int i = 0; i < set2.getEntryCount(); i++) {
                        Entry entry = set2.getEntryForIndex(i);
                        if (entry != null) {
                            entry.setX(i);
                            entry.setY(entry.getY());
                        }
                    }
//                    lineData.notifyDataChanged();
                }
            }

        }
    }

    private void writeFileOutStorage(String fileName) {

        //5??? ????????? ?????? ?????? X
        if (rowDatas.size() < 5) {
            return;
        }
        try {
            //???????????? ????????? ????????? ????????? ??????
            for (RowData rowData : rowDatas) {
                String data = (rowData.currentData + " | "
                        + rowData.average + " | "
                        + rowData.threshold + " | "
                        + rowData.candidateCount + " | "
                        + rowData.lastCount);
                sb.append(data).append("\n");
//                Log.e("rowData.currentData: ", rowData.currentData);
//                Log.e("rowData.average: ", rowData.average);
//                Log.e("rowData.threshold: ", rowData.threshold);
//                Log.e("rowData.candidateCount: ", rowData.candidateCount);
//                Log.e("rowData.lastCount: ", rowData.lastCount);
            }


            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, false);
            writer.write(sb.toString());
            writer.close();

            Toast.makeText(getActivity().getApplicationContext(), "???????????????/Download/" + fileName + "|????????????", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("LOG_TAG", "Directory not created");
        }

        rowDatas.clear();
        sb.delete(0, sb.length());
    }


    private void addCurrentRowDataEntry(LineChart lineChart, float value) {
        final LineData data = lineChart.getData();

        if (data != null) {
            ILineDataSet set_currentRowData = data.getDataSetByIndex(0);
            if (set_currentRowData == null) {
                set_currentRowData = createDataSet("???????????????", R.color.zentry_blue, LineDataSet.Mode.CUBIC_BEZIER, Utils.CHART_LINE_WIDTH);
                data.addDataSet(set_currentRowData);
            }

            data.addEntry(new Entry(set_currentRowData.getEntryCount(), value), 0);

            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(300f);

            if (data.getEntryCount() >= 10) {
                final float highestVisibleIndex = lineChart.getHighestVisibleX();
                if ((data.getEntryCount() - 10) < highestVisibleIndex) {
                    lineChart.moveViewToX(data.getEntryCount() - 11);
                } else {
                    lineChart.invalidate();
                }
            } else {
                lineChart.invalidate();
            }
        }
    }

    private void addAverThresDataEntry(LineChart lineChart, float val_aver, float val_thres) {
        final LineData data = lineChart.getData();
        if (data != null) {
            ILineDataSet set_aver = data.getDataSetByIndex(0);
            ILineDataSet set_threshold = data.getDataSetByIndex(1);
            if (set_aver == null) {
                set_aver = createDataSet("?????????", R.color.zentry_blue, LineDataSet.Mode.CUBIC_BEZIER, Utils.CHART_LINE_WIDTH);
                data.addDataSet(set_aver);
            }
            if (set_threshold == null) {
                set_threshold = createDataSet("???????????????", R.color.black, LineDataSet.Mode.CUBIC_BEZIER, Utils.CHART_LINE_WIDTH);
                data.addDataSet(set_threshold);
            }

            data.addEntry(new Entry(set_aver.getEntryCount(), val_aver), 0);
            data.addEntry(new Entry(set_aver.getEntryCount(), val_thres), 1);

            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(300f);

            if (data.getEntryCount() >= 10) {
                final float highestVisibleIndex = lineChart.getHighestVisibleX();

                //???????????? 100f??? ?????? ??????
                if ((data.getEntryCount() - 10) > highestVisibleIndex) {
                    lineChart.moveViewToX((float) (data.getEntryCount() - 11));
                } else {
                    lineChart.invalidate();
                }
            } else {
                lineChart.invalidate();
            }
        }
    }

    private void addCountDataEntry(LineChart lineChart, int count_candidate, int count_final) {
        final LineData data = lineChart.getData();

        if (data != null) {
            ILineDataSet set_candidate_Data = data.getDataSetByIndex(0);
            ILineDataSet set_final_Data = data.getDataSetByIndex(1);

            if (set_candidate_Data == null) {
                set_candidate_Data = createDataSet("????????? ?????????", R.color.green, LineDataSet.Mode.LINEAR, Utils.CHART_CANDIDATE_LINE_WIDTH);
                data.addDataSet(set_candidate_Data);
            }

            /* ???????????? ?????????*/
            //?????? ?????????

            if (set_final_Data == null) {
                set_final_Data = createDataSet("?????? ????????? ", R.color.zentry_blue, LineDataSet.Mode.LINEAR, Utils.CHART_FINAL_LINE_WIDTH);
                data.addDataSet(set_final_Data);
            }
            //????????? ?????????
            if (count_candidate == 1) {
                data.addEntry(new Entry(set_candidate_Data.getEntryCount(), -50), 0);
            } else {
                data.addEntry(new Entry(set_candidate_Data.getEntryCount(), 0), 0);
            }
            Log.e("addCountDataEntry:  mCountFinal: ", String.valueOf(count_candidate));

            if (count_final == 1) {
                data.addEntry(new Entry(set_candidate_Data.getEntryCount(), -100), 1);
            } else {
                data.addEntry(new Entry(set_candidate_Data.getEntryCount(), 0), 1);
            }


            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(300f);

            if (data.getEntryCount() >= 10) {
                final float highestVisibleIndex = lineChart.getHighestVisibleX();
                if ((data.getEntryCount() - 10) > highestVisibleIndex) {
                    lineChart.moveViewToX(data.getEntryCount() - 11);
                } else {
                    lineChart.invalidate();
                }
            } else {
                lineChart.invalidate();
            }
        }
    }


    private LineDataSet createDataSet(String setName, int color, LineDataSet.Mode mode, float width) {
        LineDataSet lineDataSet = new LineDataSet(null, setName);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(ContextCompat.getColor(getActivity(), color));
        lineDataSet.setFillColor(ContextCompat.getColor(getActivity(), color));
        lineDataSet.setHighLightColor(ContextCompat.getColor(getActivity(), color));
        lineDataSet.setValueFormatter(new TemperatureChartValueFormatter());
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(Utils.CHART_VALUE_TEXT_SIZE);
        lineDataSet.setLineWidth(width);
        lineDataSet.setMode(mode);
        return lineDataSet;
    }


    private void initGraph(LineChart lc_chart, int color, XAxis.XAxisPosition xAxisPos) {
        int resColor = getActivity().getColor(color);

        Description label = new Description();
        label.setText(getString(R.string.time));
        label.setTextColor(resColor);

        lc_chart.setDescription(label);
        lc_chart.setTouchEnabled(true);
        lc_chart.setVisibleXRangeMinimum(5);

        lc_chart.setDragEnabled(true);
        lc_chart.setPinchZoom(true);
        lc_chart.setScaleEnabled(true);
        lc_chart.setAutoScaleMinMaxEnabled(true);
        lc_chart.setDrawGridBackground(false);
        lc_chart.setBackgroundColor(Color.parseColor("#00000000"));

        LineData data = new LineData();
        data.setValueFormatter(new TemperatureChartValueFormatter());
        data.setValueTextColor(resColor);
        lc_chart.setData(data);

        Legend legend = lc_chart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(resColor);

        XAxis xAxis = lc_chart.getXAxis();
        xAxis.setPosition(xAxisPos);
        xAxis.setTextColor(resColor);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawLabels(false);
        xAxis.setAxisLineWidth(2);
        xAxis.setAxisLineColor(resColor);

        YAxis leftAxis = lc_chart.getAxisLeft();
        leftAxis.setTextColor(resColor);
        leftAxis.setValueFormatter(new PressureChartYValueFormatter());
        leftAxis.setDrawLabels(false);
        leftAxis.setLabelCount(10, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisLineWidth(2);
        leftAxis.setAxisLineColor(resColor);


        YAxis rightAxis = lc_chart.getAxisRight();
        rightAxis.setEnabled(false);

    }


    private boolean isBleEnabled() {
        final BluetoothManager bm = (BluetoothManager) getActivity().getSystemService(getActivity().BLUETOOTH_SERVICE);
        final BluetoothAdapter ba = bm.getAdapter();
        return ba != null && ba.isEnabled();
    }

    private void enableBle() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }


    private void checkPermission() {
        // ??????ID??? ???????????????
        int permission = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int permission2 = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        // ????????? ??????????????? ??????
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED) {
            // ??????????????? ?????????????????? ????????? ????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // ?????? ??????(READ_PHONE_STATE??? requestCode??? 1000?????? ??????
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1000);
            }
        }
    }

    // ?????? ?????? ????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        // READ_PHONE_STATE??? ?????? ?????? ????????? ????????????
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == 1000) {
            boolean check_result = true;

            // ?????? ???????????? ??????????????? ??????
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // ?????? ????????? ????????? ?????? ????????? ??????????????? ??????
            if (check_result == true) {
            } else {
            }
        }
    }


    private void updateSelectionInDb(final Thingy thingy, final boolean selected) {
        final ArrayList<Thingy> thingyList = mDatabaseHelper.getSavedDevices();
        for (int i = 0; i < thingyList.size(); i++) {
            if (thingy.getDeviceAddress().equals(thingyList.get(i).getDeviceAddress())) {
                mDatabaseHelper.setLastSelected(thingy.getDeviceAddress(), selected);
            } else {
                mDatabaseHelper.setLastSelected(thingyList.get(i).getDeviceAddress(), !selected);
            }
        }
    }


    class PressureChartYValueFormatter extends ValueFormatter {
        private DecimalFormat mFormat;

        PressureChartYValueFormatter() {
            mFormat = new DecimalFormat("###,##0.00");
        }

        public String getFormattedValue(float value, YAxis yAxis) {
            return mFormat.format(value);
        }
    }

    class TemperatureChartValueFormatter extends ValueFormatter {
        private DecimalFormat mFormat;

        TemperatureChartValueFormatter() {
            mFormat = new DecimalFormat("##,##,#0.00");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }

    }

    public ThingyListener getThingyListener() {
        return mThingyListener;
    }

    public BroadcastReceiver getBleStateChangedReceiver() {
        return mBleStateChangedReceiver;
    }

    public static MeasureDolittleFragment newInstance(String param1, String param2) {
        MeasureDolittleFragment fragment = new MeasureDolittleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
