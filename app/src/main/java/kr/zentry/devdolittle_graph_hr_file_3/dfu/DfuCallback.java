package kr.zentry.devdolittle_graph_hr_file_3.dfu;


import android.bluetooth.BluetoothGattCallback;

/* package */ interface DfuCallback extends DfuController {

    class DfuGattCallback extends BluetoothGattCallback {
        public void onDisconnected() {
            // empty initial implementation
        }
    }

    /**
     * Returns the final BluetoothGattCallback instance, depending on the implementation.
     */
    DfuGattCallback getGattCallback();

    /**
     * Callback invoked when bond state changes to
     * {@link android.bluetooth.BluetoothDevice#BOND_BONDED BOND_BONDED} or
     * {@link android.bluetooth.BluetoothDevice#BOND_NONE BOND_NONE}.
     */
    void onBondStateChanged(final int state);
}
