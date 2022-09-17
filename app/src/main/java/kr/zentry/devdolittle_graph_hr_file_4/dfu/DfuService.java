package kr.zentry.devdolittle_graph_hr_file_4.dfu;

import android.bluetooth.BluetoothGatt;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;

import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.DeviceDisconnectedException;
import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.DfuException;
import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.UploadAbortedException;

interface DfuService extends DfuCallback {

    /**
     * This method must return true if the device is compatible with this DFU implementation,
     * false otherwise.
     *
     * @throws DeviceDisconnectedException Thrown when the device will disconnect in the middle of
     *                                     the transmission.
     * @throws DfuException                Thrown if DFU error occur.
     * @throws UploadAbortedException      Thrown if DFU operation was aborted by user.
     */
    boolean isClientCompatible(@NonNull final Intent intent, @NonNull final BluetoothGatt gatt)
            throws DfuException, DeviceDisconnectedException, UploadAbortedException;

    /**
     * Initializes the DFU implementation and does some initial setting up.
     *
     * @return True if initialization was successful and the DFU process may begin,
     * false to finish teh DFU service.
     * @throws DeviceDisconnectedException Thrown when the device will disconnect in the middle of
     *                                     the transmission.
     * @throws DfuException                Thrown if DFU error occur.
     * @throws UploadAbortedException      Thrown if DFU operation was aborted by user.
     */
    boolean initialize(@NonNull final Intent intent, @NonNull final BluetoothGatt gatt,
                       @FileType final int fileType,
                       @NonNull final InputStream firmwareStream,
                       @Nullable final InputStream initPacketStream)
            throws DfuException, DeviceDisconnectedException, UploadAbortedException;

    /**
     * Performs the DFU process.
     *
     * @throws DeviceDisconnectedException Thrown when the device will disconnect in the middle of
     *                                     the transmission.
     * @throws DfuException                Thrown if DFU error occur.
     * @throws UploadAbortedException      Thrown if DFU operation was aborted by user.
     */
    void performDfu(@NonNull final Intent intent)
            throws DfuException, DeviceDisconnectedException, UploadAbortedException;

    /**
     * Releases the service.
     */
    void release();
}
