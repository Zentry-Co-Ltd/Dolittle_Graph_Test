package kr.zentry.devdolittle_graph_hr_file_3.BluetoothScanner;

import android.bluetooth.BluetoothDevice;

public interface ScannerListener {
    void onDeviceSelected(final BluetoothDevice device, final String name);
    void onNothingSelected();
}