package kr.zentry.devdolittle_graph_hr_file_3.dfu.internal.Scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Locale;

public class BootloaderScannerJB implements BootloaderScanner, BluetoothAdapter.LeScanCallback {
    private final Object mLock = new Object();
    private String mDeviceAddress;
    private String mDeviceAddressIncremented;
    private String mBootloaderAddress;
    private boolean mFound;

    @SuppressWarnings("deprecation")
    @Override
    public String searchFor(final String deviceAddress) {
        final String firstBytes = deviceAddress.substring(0, 15);
        final String lastByte = deviceAddress.substring(15); // assuming that the device address is correct
        final String lastByteIncremented = String.format(Locale.US, "%02X", (Integer.valueOf(lastByte, 16) + ADDRESS_DIFF) & 0xFF);

        mDeviceAddress = deviceAddress;
        mDeviceAddressIncremented = firstBytes + lastByteIncremented;
        mBootloaderAddress = null;
        mFound = false;

        // Add timeout
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(BootloaderScanner.TIMEOUT);
                } catch (final InterruptedException e) {
                    // do nothing
                }

                if (mFound)
                    return;

                mBootloaderAddress = null;
                mFound = true;

                // Notify the waiting thread
                synchronized (mLock) {
                    mLock.notifyAll();
                }
            }
        }, "Scanner timer").start();

        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || adapter.getState() != BluetoothAdapter.STATE_ON)
            return null;
        adapter.startLeScan(this);

        try {
            synchronized (mLock) {
                while (!mFound)
                    mLock.wait();
            }
        } catch (final InterruptedException e) {
            // do nothing
        }

        adapter.stopLeScan(this);
        return mBootloaderAddress;
    }

    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        final String address = device.getAddress();

        if (mDeviceAddress.equals(address) || mDeviceAddressIncremented.equals(address)) {
            mBootloaderAddress = address;
            mFound = true;

            // Notify the waiting thread
            synchronized (mLock) {
                mLock.notifyAll();
            }
        }
    }

}
