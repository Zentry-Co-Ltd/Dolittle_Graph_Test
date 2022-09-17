package kr.zentry.devdolittle_graph_hr_file_3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.os.Handler;
import android.os.ParcelUuid;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import kr.zentry.devdolittle_graph_hr_file_3.BluetoothScanner.BluetoothLeScannerCompat;
import kr.zentry.devdolittle_graph_hr_file_3.BluetoothScanner.ScanCallback;
import kr.zentry.devdolittle_graph_hr_file_3.BluetoothScanner.ScanFilter;
import kr.zentry.devdolittle_graph_hr_file_3.BluetoothScanner.ScanResult;
import kr.zentry.devdolittle_graph_hr_file_3.BluetoothScanner.ScanSettings;
import kr.zentry.devdolittle_graph_hr_file_3.BluetoothScanner.ScannerListener;
import kr.zentry.devdolittle_graph_hr_file_3.DeviceListView.BluetoothDeviceModel;
import kr.zentry.devdolittle_graph_hr_file_3.DeviceListView.DeviceAdapter;

public class DeviceConnectingPopup extends DialogFragment {
    int REQ_LOCATION_PERMISSION = 1001;

    private Handler mHandler = new Handler();
    private int deviceHeight;
    private int deviceWidth;
    private DeviceAdapter deviceAdapter;
    private ListView listView;
    private ImageView img_refresh;
    private ViewGroup.LayoutParams paramsListview;
    private TextView tv_goto_connect;
    private TextView tv_refresh;
    private TextView tv_no_scan;
    private ScannerListener scannerListener;
    private Animation anim_rotation;

    private boolean mIsScanning = false;
    private final static String PARAM_UUID = "param_uuid";
    private final static long SCAN_DURATION = 8000;
    public static final int NO_RSSI = -1000;
    private ParcelUuid mUuid;

    public DeviceConnectingPopup() {
        // Required empty public constructor
    }

    public static DeviceConnectingPopup newInstance() {
        DeviceConnectingPopup fragment = new DeviceConnectingPopup();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //final Bundle args = getArguments();
    }

    public DeviceConnectingPopup getInstance(@Nullable final UUID uuid) {
        final DeviceConnectingPopup fragment = new DeviceConnectingPopup();

        final Bundle args = new Bundle();
        if (uuid != null) {
            args.putParcelable(PARAM_UUID, new ParcelUuid(uuid));
        }

        mUuid = args.getParcelable(PARAM_UUID);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        //try 1
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
//        getDialog().getWindow().setLayout((int) (deviceWidth), (int) (deviceHeight * 0.6));

        //try 2
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getDialog().getWindow().getAttributes());
        //lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.width = (int) (deviceWidth * 0.9);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = getDialog().getWindow();
        window.setAttributes(lp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_device_connecting_popup, container, false);
        tv_goto_connect = v.findViewById(R.id.tv_goto_connect);
        listView = v.findViewById(R.id.list_device);
        tv_refresh = v.findViewById(R.id.tv_refresh);
        tv_no_scan = v.findViewById(R.id.tv_no_scan);
        img_refresh = v.findViewById(R.id.img_refresh);
        paramsListview = listView.getLayoutParams();

        paramsListview.height = 100;

        getDialog().getWindow().getAttributes().windowAnimations = R.style.ConnectingPopupSlideAnimation;
        getDialog().getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.corner_popup));

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION_PERMISSION);
            }
        }

//        tv_goto_connect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity().getApplicationContext(), DeviceConnectActivity.class);
//                startActivity(intent);
//            }
//        });

        deviceAdapter = new DeviceAdapter();
        listView.setAdapter(deviceAdapter);

        tv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsScanning) {
                    stopScan();
                } else {
                    startScan();
                }
            }
        });

        anim_rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stopScan();
                final BluetoothDeviceModel device = (BluetoothDeviceModel) deviceAdapter.getItem(position);
                scannerListener.onDeviceSelected(device.device, device.name != null ? device.name : getString(R.string.glb_not_available));
                dismiss();
            }
        });

        startScan();

        return v;
    }

    public void setScannerListener(ScannerListener scannerListener) {
        this.scannerListener = scannerListener;
    }

    @Override
    public void onStop() {
        stopScan();
        super.onStop();
    }

    public void onResume() {
        super.onResume();
    }

    public void startScan() {
        deviceAdapter.clearDevices();
        tv_refresh.setText("디바이스 스캔 중");
        tv_no_scan.setVisibility(View.GONE);

        tv_refresh.getPointerIcon();

        img_refresh.startAnimation(anim_rotation);


        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        final ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(750).setUseHardwareBatchingIfSupported(false).setUseHardwareFilteringIfSupported(false).build();
//        final List<ScanFilter> filters = new ArrayList<>();
//        filters.add(new ScanFilter.Builder().setServiceUuid(mUuid).build());
//        scanner.startScan(filters, settings, scanCallback);
        scanner.startScan(null, settings, scanCallback);

        mIsScanning = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsScanning) {
                    stopScan();
                }
            }
        }, SCAN_DURATION);
    }

    private void stopScan() {
        if (mIsScanning) {
            tv_refresh.setText("디바이스 스캔");

            img_refresh.clearAnimation();

            if (deviceAdapter.getCount() == 0) {
                tv_no_scan.setVisibility(View.VISIBLE);
            } else {
                tv_no_scan.setVisibility(View.GONE);
            }

            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(scanCallback);

            mIsScanning = false;
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, @NonNull final ScanResult result) {
            // do nothing
        }

        @Override
        public void onBatchScanResults(final List<ScanResult> results) {
            /* 회사번호와 일치하는 것만 필터링
             *  회사번호: 3655
             * */
            List<ScanResult> scanResults = results.stream().filter(result ->
                    {
                        if (result.getScanRecord() == null) {
                            return false;
                        }
                        if (result.getScanRecord().getManufacturerSpecificData() == null) {
                            return false;
                        }
                        return result.getScanRecord().getManufacturerSpecificData().keyAt(0) == 3655;
                    }
            ).collect(Collectors.toList());

            Log.d("scanResults.length", scanResults.size() + "");
            if (scanResults.size() != 0) {
                paramsListview.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            }
            //스캔 된 디바이스 뷰에 표시
            deviceAdapter.update(scanResults);
        }

        @Override
        public void onScanFailed(final int errorCode) {
            // should never be called
        }
    };
}