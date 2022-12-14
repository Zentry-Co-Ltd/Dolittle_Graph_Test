package kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.Scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;

import java.util.Locale;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BootloaderScannerLollipop extends ScanCallback implements BootloaderScanner {
    private final Object mLock = new Object();
    private String mDeviceAddress;
    private String mDeviceAddressIncremented;
    private String mBootloaderAddress;
    private boolean mFound;

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
        final BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (scanner == null)
            return null;
        /*
         * Scanning with filters does not work on Nexus 9 (Android 5.1). No devices are found and scanner terminates on timeout.
         * We will match the device address in the callback method instead. It's not like it should be, but at least it works.
         */
        //final List<ScanFilter> filters = new ArrayList<>();
        //filters.add(new ScanFilter.Builder().setDeviceAddress(mDeviceAddress).build());
        //filters.add(new ScanFilter.Builder().setDeviceAddress(mDeviceAddressIncremented).build());
        final ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        scanner.startScan(/*filters*/ null, settings, this);

        try {
            synchronized (mLock) {
                while (!mFound)
                    mLock.wait();
            }
        } catch (final InterruptedException e) {
            // do nothing
        }

        scanner.stopScan(this);
        return mBootloaderAddress;
    }

    @Override
    public void onScanResult(final int callbackType, final ScanResult result) {
        final String address = result.getDevice().getAddress();

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