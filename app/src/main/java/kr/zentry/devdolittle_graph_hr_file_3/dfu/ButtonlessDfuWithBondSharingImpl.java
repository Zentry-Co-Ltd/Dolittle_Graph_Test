package kr.zentry.devdolittle_graph_hr_file_3.dfu;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.UUID;

import kr.zentry.devdolittle_graph_hr_file_3.dfu.internal.exception.DeviceDisconnectedException;
import kr.zentry.devdolittle_graph_hr_file_3.dfu.internal.exception.DfuException;
import kr.zentry.devdolittle_graph_hr_file_3.dfu.internal.exception.UploadAbortedException;

class ButtonlessDfuWithBondSharingImpl extends ButtonlessDfuImpl {
    /**
     * The UUID of the Secure DFU service from SDK 12.
     */
    static final UUID DEFAULT_BUTTONLESS_DFU_SERVICE_UUID = SecureDfuImpl.DEFAULT_DFU_SERVICE_UUID;
    /**
     * The UUID of the Secure Buttonless DFU characteristic with bond sharing from SDK 14 or newer.
     */
    static final UUID DEFAULT_BUTTONLESS_DFU_UUID = new UUID(0x8EC90004F3154F60L, 0x9FB8838830DAEA50L);

    static UUID BUTTONLESS_DFU_SERVICE_UUID = DEFAULT_BUTTONLESS_DFU_SERVICE_UUID;
    static UUID BUTTONLESS_DFU_UUID = DEFAULT_BUTTONLESS_DFU_UUID;

    private BluetoothGattCharacteristic mButtonlessDfuCharacteristic;

    ButtonlessDfuWithBondSharingImpl(@NonNull final Intent intent, @NonNull final DfuBaseService service) {
        super(intent, service);
    }

    @Override
    public boolean isClientCompatible(@NonNull final Intent intent, @NonNull final BluetoothGatt gatt) {
        final BluetoothGattService dfuService = gatt.getService(BUTTONLESS_DFU_SERVICE_UUID);
        if (dfuService == null)
            return false;
        final BluetoothGattCharacteristic characteristic = dfuService.getCharacteristic(BUTTONLESS_DFU_UUID);
        if (characteristic == null || characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG) == null)
            return false;
        mButtonlessDfuCharacteristic = characteristic;
        return true;
    }

    @Override
    protected int getResponseType() {
        return INDICATIONS;
    }

    @Override
    protected BluetoothGattCharacteristic getButtonlessDfuCharacteristic() {
        return mButtonlessDfuCharacteristic;
    }

    @Override
    protected boolean shouldScanForBootloader() {
        return false;
    }

    @Override
    public void performDfu(@NonNull final Intent intent)
            throws DfuException, DeviceDisconnectedException, UploadAbortedException {
        logi("Buttonless service with bond sharing found -> SDK 14 or newer");
        if (!isBonded()) {
            logw("Device is not paired, cancelling DFU");
            mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_WARNING, "Device is not bonded");
            mService.terminateConnection(mGatt, DfuBaseService.ERROR_DEVICE_NOT_BONDED);
            return;
        }
        // In Secure DFU with Bond Sharing the bond information should not be removed
        intent.putExtra(DfuBaseService.EXTRA_KEEP_BOND, true);
        intent.putExtra(DfuBaseService.EXTRA_RESTORE_BOND, false);

        super.performDfu(intent);
    }
}
