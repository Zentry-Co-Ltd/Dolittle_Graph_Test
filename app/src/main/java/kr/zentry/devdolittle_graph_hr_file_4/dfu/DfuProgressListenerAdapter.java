package kr.zentry.devdolittle_graph_hr_file_4.dfu;


import androidx.annotation.NonNull;

public class DfuProgressListenerAdapter implements DfuProgressListener {

    @Override
    public void onDeviceConnecting(@NonNull final String deviceAddress) {
        // empty default implementation
    }

    @Override
    public void onDeviceConnected(@NonNull final String deviceAddress) {
        // empty default implementation
    }

    @Override
    public void onDfuProcessStarting(@NonNull final String deviceAddress) {
        // empty default implementation
    }

    @Override
    public void onDfuProcessStarted(@NonNull final String deviceAddress) {
        // empty default implementation
    }

    @Override
    public void onEnablingDfuMode(@NonNull final String deviceAddress) {
        // empty default implementation
    }

    @Override
    public void onProgressChanged(@NonNull final String deviceAddress, final int percent,
                                  final float speed, final float avgSpeed,
                                  final int currentPart, final int partsTotal) {
        // empty default implementation
    }

    @Override
    public void onFirmwareValidating(@NonNull final String deviceAddress) {
        // empty default implementation
    }

    @Override
    public void onDeviceDisconnecting(final String deviceAddress) {
        // empty default implementation
    }

    @Override
    public void onDeviceDisconnected(@NonNull final String deviceAddress) {
        // empty default implementation
    }

    @Override
    public void onDfuCompleted(@NonNull final String deviceAddress) {
        // empty default implementation
    }

    @Override
    public void onDfuAborted(@NonNull final String deviceAddress) {
        // empty default implementation
    }

    @Override
    public void onError(@NonNull final String deviceAddress,
                        final int error, final int errorType, final String message) {
        // empty default implementation
    }
}
