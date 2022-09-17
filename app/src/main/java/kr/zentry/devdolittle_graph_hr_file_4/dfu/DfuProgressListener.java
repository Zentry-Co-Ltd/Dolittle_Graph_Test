package kr.zentry.devdolittle_graph_hr_file_4.dfu;

import androidx.annotation.NonNull;

/**
 * Listener for status, progress and error events. This listener should be used instead of
 * creating the BroadcastReceiver on your own.
 *
 * @see DfuServiceListenerHelper
 */
public interface DfuProgressListener {

    /**
     * Method called when the DFU service started connecting with the DFU target.
     *
     * @param deviceAddress the target device address.
     */
    void onDeviceConnecting(@NonNull final String deviceAddress);

    /**
     * Method called when the service has successfully connected, discovered services and found
     * DFU service on the DFU target.
     *
     * @param deviceAddress the target device address.
     */
    void onDeviceConnected(@NonNull final String deviceAddress);

    /**
     * Method called when the DFU process is starting. This includes reading the DFU Version
     * characteristic, sending DFU_START command as well as the Init packet, if set.
     *
     * @param deviceAddress the target device address.
     */
    void onDfuProcessStarting(@NonNull final String deviceAddress);

    /**
     * Method called when the DFU process was started and bytes about to be sent.
     *
     * @param deviceAddress the target device address
     */
    void onDfuProcessStarted(@NonNull final String deviceAddress);

    /**
     * Method called when the service discovered that the DFU target is in the application mode
     * and must be switched to DFU mode. The switch command will be sent and the DFU process
     * should start again. There will be no {@link #onDeviceDisconnected(String)} event following
     * this call.
     *
     * @param deviceAddress the target device address.
     */
    void onEnablingDfuMode(@NonNull final String deviceAddress);

    /**
     * Method called during uploading the firmware. It will not be called twice with the same
     * value of percent, however, in case of small firmware files, some values may be omitted.
     *
     * @param deviceAddress the target device address.
     * @param percent       the current status of upload (0-99).
     * @param speed         the current speed in bytes per millisecond.
     * @param avgSpeed      the average speed in bytes per millisecond
     * @param currentPart   the number pf part being sent. In case the ZIP file contains a Soft Device
     *                      and/or a Bootloader together with the application the SD+BL are sent as part 1,
     *                      then the service starts again and send the application as part 2.
     * @param partsTotal    total number of parts.
     */
    void onProgressChanged(@NonNull final String deviceAddress, final int percent,
                           final float speed, final float avgSpeed,
                           final int currentPart, final int partsTotal);

    /**
     * Method called when the new firmware is being validated on the target device.
     *
     * @param deviceAddress the target device address.
     */
    void onFirmwareValidating(@NonNull final String deviceAddress);

    /**
     * Method called when the service started to disconnect from the target device.
     *
     * @param deviceAddress the target device address.
     */
    void onDeviceDisconnecting(final String deviceAddress);

    /**
     * Method called when the service disconnected from the device. The device has been reset.
     *
     * @param deviceAddress the target device address.
     */
    void onDeviceDisconnected(@NonNull final String deviceAddress);

    /**
     * Method called when the DFU process succeeded.
     *
     * @param deviceAddress the target device address.
     */
    void onDfuCompleted(@NonNull final String deviceAddress);

    /**
     * Method called when the DFU process has been aborted.
     *
     * @param deviceAddress the target device address.
     */
    void onDfuAborted(@NonNull final String deviceAddress);

    /**
     * Method called when an error occur.
     *
     * @param deviceAddress the target device address.
     * @param error         error number.
     * @param errorType     the error type, one of
     *                      {@link DfuBaseService#ERROR_TYPE_COMMUNICATION_STATE},
     *                      {@link DfuBaseService#ERROR_TYPE_COMMUNICATION},
     *                      {@link DfuBaseService#ERROR_TYPE_DFU_REMOTE},
     *                      {@link DfuBaseService#ERROR_TYPE_OTHER}.
     * @param message       the error message.
     */
    void onError(@NonNull final String deviceAddress,
                 final int error, final int errorType, final String message);
}
