package kr.zentry.devdolittle_graph_hr_file_4.dfu;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.UUID;

import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.DeviceDisconnectedException;
import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.DfuException;
import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.UploadAbortedException;

class ExperimentalButtonlessDfuImpl extends ButtonlessDfuImpl {
    /** The UUID of the experimental Buttonless DFU service from SDK 12.x. */
    static final UUID DEFAULT_EXPERIMENTAL_BUTTONLESS_DFU_SERVICE_UUID = new UUID(0x8E400001F3154F60L, 0x9FB8838830DAEA50L);
    /** The UUID of the experimental Buttonless DFU characteristic from SDK 12.x. */
    static final UUID DEFAULT_EXPERIMENTAL_BUTTONLESS_DFU_UUID         = new UUID(0x8E400001F3154F60L, 0x9FB8838830DAEA50L); // the same as service

    static UUID EXPERIMENTAL_BUTTONLESS_DFU_SERVICE_UUID = DEFAULT_EXPERIMENTAL_BUTTONLESS_DFU_SERVICE_UUID;
    static UUID EXPERIMENTAL_BUTTONLESS_DFU_UUID         = DEFAULT_EXPERIMENTAL_BUTTONLESS_DFU_UUID;

    private BluetoothGattCharacteristic mButtonlessDfuCharacteristic;

    ExperimentalButtonlessDfuImpl(@NonNull final Intent intent, @NonNull final DfuBaseService service) {
        super(intent, service);
    }

    @Override
    public boolean isClientCompatible(@NonNull final Intent intent, @NonNull final BluetoothGatt gatt) {
        final BluetoothGattService dfuService = gatt.getService(EXPERIMENTAL_BUTTONLESS_DFU_SERVICE_UUID);
        if (dfuService == null)
            return false;
        final BluetoothGattCharacteristic characteristic = dfuService.getCharacteristic(EXPERIMENTAL_BUTTONLESS_DFU_UUID);
        if (characteristic == null || characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG) == null)
            return false;
        mButtonlessDfuCharacteristic = characteristic;
        return true;
    }

    @Override
    protected int getResponseType() {
        return NOTIFICATIONS;
    }

    @Override
    protected BluetoothGattCharacteristic getButtonlessDfuCharacteristic() {
        return mButtonlessDfuCharacteristic;
    }

    @Override
    protected boolean shouldScanForBootloader() {
        return true;
    }

    @Override
    public void performDfu(@NonNull final Intent intent) throws DfuException, DeviceDisconnectedException, UploadAbortedException {
        logi("Experimental buttonless service found -> SDK 12.x");
        super.performDfu(intent);
    }
}
