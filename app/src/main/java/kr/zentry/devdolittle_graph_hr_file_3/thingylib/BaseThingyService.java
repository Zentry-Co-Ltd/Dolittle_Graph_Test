package kr.zentry.devdolittle_graph_hr_file_3.thingylib;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.zentry.devdolittle_graph_hr_file_3.thingylib.utils.ThingyUtils;

public abstract class BaseThingyService extends Service implements ThingyConnection.ThingyConnectionGattCallbacks {

    private static final String TAG = "BaseThingyService";
    private BluetoothDevice mDevice;

    protected Map<BluetoothDevice, ThingyConnection> mThingyConnections;
    protected ArrayList<BluetoothDevice> mDevices;

    protected boolean mBound = false;

    public BaseThingyService() {
        super();
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device, int connectionState) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, int connectionState) {
        mThingyConnections.remove(device);
        mDevices.remove(device);
    }

    public abstract class BaseThingyBinder extends Binder {

        /**
         * Disconnects from all connected devices.
         */
        /*package access*/ final void disconnectFromAllDevices(){
            if (mDevices.size() > 0){
                for(int i = 0; i < mDevices.size(); i++){
                    final ThingyConnection thingyConnection = mThingyConnections.get(mDevices.get(i));
                    if (thingyConnection != null){
                        thingyConnection.disconnect();
                    }
                }
                mDevices.clear();
                mThingyConnections.clear();
            }
        }

        /**
         * Returns the list of connected devices.
         */
        /*package access*/ final List<BluetoothDevice> getConnectedDevices() {
            return Collections.unmodifiableList(mDevices);
        }

        /**
         * Returns the remote connection for the particular bluetooth device.
         *
         * @param device bluetooth device
         */
        /*package access*/ public abstract ThingyConnection getThingyConnection(BluetoothDevice device); /*{
            return mThingyConnections.get(device);
        }*/

        /**
         * Selects the current bluetooth device.
         *
         * @param device bluetooth device
         */
        /*package access*/ final void setSelectedDevice(final BluetoothDevice device) {
            mDevice = device;
        }

        /**
         * Returns the current bluetooth device which was selected from
         * {@link #setSelectedDevice(BluetoothDevice)}.
         */
        /*package access*/ final BluetoothDevice getSelectedDevice() {
            return mDevice;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mThingyConnections = new HashMap<>();
        mDevices = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final BluetoothDevice bluetoothDevice = intent.getParcelableExtra(ThingyUtils.EXTRA_DEVICE);
            if (bluetoothDevice != null) {
                mThingyConnections.put(bluetoothDevice, new ThingyConnection(this, bluetoothDevice));
                if (!mDevices.contains(bluetoothDevice)) {
                    mDevices.add(bluetoothDevice);
                }
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy called on Base service");
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        disconnectFromAllDevices();
    }

    @Nullable
    @Override
    public abstract BaseThingyBinder onBind(Intent intent);

    @Override
    public final void onRebind(Intent intent) {
        mBound = true;
        onRebind();
    }

    @Override
    public final boolean onUnbind(Intent intent) {
        mBound = false;
        onUnbind();
        return true;
    }

    /**
     * Called when the activity has rebinded to the service after being recreated.
     * This method is not called when the activity was killed and recreated just to change the
     * phone orientation.
     */
    protected void onRebind(){
    }

    /**
     * Called when the activity has unbound from the service after being bound.
     */
    protected void onUnbind(){
    }

    /**
     * Disconnects from all connected devices
     *
     */
    /*package access*/ final void disconnectFromAllDevices(){
        if (mDevices.size() > 0){
            for(int i = 0; i < mDevices.size(); i++){
                final ThingyConnection thingyConnection = mThingyConnections.get(mDevices.get(i));
                if (thingyConnection != null){
                    thingyConnection.disconnect();
                }
            }
            mDevices.clear();
            mThingyConnections.clear();
        }
    }

    /* *
     * Create your own notification target class to display notifications
     */
    /*protected abstract Class<? extends Activity> getNotificationTarget();*/
}
