package kr.zentry.devdolittle_graph_hr_file_4.dfu;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.Locale;

import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.DeviceDisconnectedException;
import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.DfuException;
import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.RemoteDfuException;
import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.UnknownResponseException;
import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.UploadAbortedException;
import kr.zentry.devdolittle_graph_hr_file_4.error.SecureDfuError;

abstract class ButtonlessDfuImpl extends BaseButtonlessDfuImpl {

    private static final int DFU_STATUS_SUCCESS = 1;

    private static final int OP_CODE_ENTER_BOOTLOADER_KEY = 0x01;
    private static final int OP_CODE_RESPONSE_CODE_KEY = 0x20;
    private static final byte[] OP_CODE_ENTER_BOOTLOADER = new byte[]{OP_CODE_ENTER_BOOTLOADER_KEY};

    ButtonlessDfuImpl(@NonNull final Intent intent, @NonNull final DfuBaseService service) {
        super(intent, service);
    }

    /**
     * This method should return the type of the response received from the device after sending
     * Enable Dfu command. Should be one of {@link #NOTIFICATIONS} or {@link #INDICATIONS}.
     *
     * @return Response type.
     */
    protected abstract int getResponseType();

    /**
     * Returns the buttonless characteristic.
     *
     * @return The characteristic used to trigger buttonless jump to bootloader mode.
     */
    protected abstract BluetoothGattCharacteristic getButtonlessDfuCharacteristic();

    /**
     * This method should return {@code true} if the bootloader is expected to start advertising
     * with address incremented by 1.
     *
     * @return True if the bootloader may advertise with address +1, false if it will keep
     * the same device address.
     */
    protected abstract boolean shouldScanForBootloader();

    @Override
    public void performDfu(@NonNull final Intent intent)
            throws DfuException, DeviceDisconnectedException, UploadAbortedException {
        mProgressInfo.setProgress(DfuBaseService.PROGRESS_STARTING);

        // Add one second delay to avoid the traffic jam before the DFU mode is enabled
        // Related:
        //   issue:        https://github.com/NordicSemiconductor/Android-DFU-Library/issues/10
        //   pull request: https://github.com/NordicSemiconductor/Android-DFU-Library/pull/12
        mService.waitFor(1000);
        // End

        final BluetoothGatt gatt = mGatt;

        // The service is connected to the application, not to the bootloader
        mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_WARNING, "Application with buttonless update found");

        mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_VERBOSE, "Jumping to the DFU Bootloader...");

        final BluetoothGattCharacteristic characteristic = getButtonlessDfuCharacteristic();
        // Enable notifications or indications
        final int type = getResponseType();
        enableCCCD(characteristic, getResponseType());
        mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_APPLICATION, (type == INDICATIONS ? "Indications" : "Notifications") + " enabled");

        // Wait a second here before going further
        // Related:
        //   pull request: https://github.com/NordicSemiconductor/Android-DFU-Library/pull/11
        mService.waitFor(1000);
        // End

        try {
            // Send 'enter bootloader command'
            mProgressInfo.setProgress(DfuBaseService.PROGRESS_ENABLING_DFU_MODE);
            logi("Sending Enter Bootloader (Op Code = 1)");
            writeOpCode(characteristic, OP_CODE_ENTER_BOOTLOADER, true);
            mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_APPLICATION, "Enter bootloader sent (Op Code = 1)");

            byte[] response;
            try {
                // There may be a race condition here. The peripheral should send a notification
                // and disconnect gracefully immediately after that, but the onConnectionStateChange
                // event may be handled before this method ends. Also, sometimes the notification
                // is not received at all.
                response = readNotificationResponse();
            } catch (final DeviceDisconnectedException e) {
                // The device disconnect event was handled before the method finished,
                // or the notification wasn't received. We behave as if we received status success.
                response = mReceivedData;
            }

            if (response != null) {
                /*
                 * The response received from the DFU device contains:
                 * +---------+--------+----------------------------------------------------+
                 * | byte no | value  | description                                        |
                 * +---------+--------+----------------------------------------------------+
                 * | 0       | 0x20   | Response code                                      |
                 * | 1       | 0x01   | The Op Code of a request that this response is for |
                 * | 2       | STATUS | Status code                                        |
                 * +---------+--------+----------------------------------------------------+
                 */
                final int status = getStatusCode(response, OP_CODE_ENTER_BOOTLOADER_KEY);
                logi("Response received (Op Code = " + response[1] + ", Status = " + status + ")");
                mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_APPLICATION,
                        "Response received (Op Code = " + response[1] + ", Status = " + status + ")");
                if (status != DFU_STATUS_SUCCESS)
                    throw new RemoteDfuException("Device returned error after sending Enter Bootloader", status);
                // The device will reset so we don't have to send Disconnect signal.
                mService.waitUntilDisconnected();
            } else {
                logi("Device disconnected before receiving notification");
            }

            mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_INFO, "Disconnected by the remote device");

            finalize(intent, false, shouldScanForBootloader());
        } catch (final UnknownResponseException e) {
            final int error = DfuBaseService.ERROR_INVALID_RESPONSE;
            loge(e.getMessage());
            mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_ERROR, e.getMessage());
            mService.terminateConnection(gatt, error);
        } catch (final RemoteDfuException e) {
            final int error = DfuBaseService.ERROR_REMOTE_TYPE_SECURE_BUTTONLESS | e.getErrorNumber();
            loge(e.getMessage());
            mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_ERROR, String.format(Locale.US,
                    "Remote DFU error: %s", SecureDfuError.parseButtonlessError(error)));
            mService.terminateConnection(gatt, error | DfuBaseService.ERROR_REMOTE_MASK);
        }
    }

    /**
     * Checks whether the response received is valid and returns the status code.
     *
     * @param response the response received from the DFU device.
     * @param request  the expected Op Code.
     * @return The status code.
     * @throws UnknownResponseException if response was not valid.
     */
    @SuppressWarnings("SameParameterValue")
    private int getStatusCode(final byte[] response, final int request) throws UnknownResponseException {
        if (response == null || response.length < 3 || response[0] != OP_CODE_RESPONSE_CODE_KEY || response[1] != request ||
                (response[2] != DFU_STATUS_SUCCESS && response[2] != SecureDfuError.BUTTONLESS_ERROR_OP_CODE_NOT_SUPPORTED
                        && response[2] != SecureDfuError.BUTTONLESS_ERROR_OPERATION_FAILED))
            throw new UnknownResponseException("Invalid response received", response, OP_CODE_RESPONSE_CODE_KEY, request);
        return response[2];
    }
}
