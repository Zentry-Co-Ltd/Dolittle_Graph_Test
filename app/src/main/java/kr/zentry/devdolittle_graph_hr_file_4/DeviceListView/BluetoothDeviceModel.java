package kr.zentry.devdolittle_graph_hr_file_4.DeviceListView;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.Nullable;

import kr.zentry.devdolittle_graph_hr_file_4.BluetoothScanner.ScanResult;

public class BluetoothDeviceModel {
    public BluetoothDevice device;
    public int rssi;
    public String name;
    public String Adress;

    public BluetoothDeviceModel(final ScanResult scanResult) {
        this.device = scanResult.getDevice();
        this.name = scanResult.getScanRecord() != null ? scanResult.getScanRecord().getDeviceName() : null;
        this.rssi = scanResult.getRssi();
        this.Adress = this.device != null ? this.device.getAddress() : null;
    }

    public boolean matches(final ScanResult scanResult) {
        return device.getAddress().equals(scanResult.getDevice().getAddress());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof BluetoothDeviceModel) {
            final BluetoothDeviceModel that = (BluetoothDeviceModel) obj;
            return device.getAddress().equals(that.device.getAddress());
        }
        return super.equals(obj);
    }
}
