package kr.zentry.devdolittle_graph_hr_file_3.thingylib;

import android.bluetooth.BluetoothDevice;

public interface ThingyListener {
    //Connection state listener callbacks
    void onDeviceConnected(BluetoothDevice device, int connectionState);

    void onDeviceDisconnected(BluetoothDevice device, int connectionState);

    void onServiceDiscoveryCompleted(BluetoothDevice device);

    void onBatteryLevelChanged(final BluetoothDevice bluetoothDevice, final int batteryLevel);

    void onTemperatureValueChangedEvent(final BluetoothDevice bluetoothDevice, final String temperature);

    void onPressureValueChangedEvent(final BluetoothDevice bluetoothDevice, final String pressure);

    void onHumidityValueChangedEvent(final BluetoothDevice bluetoothDevice, final String humidity);

    void onRealtimeRrValueChangedEvent(final BluetoothDevice bluetoothDevice, final int RrValue);

    //    void onRealtimeHrValueChangedEvent(final BluetoothDevice bluetoothDevice, final String HrValue);
    void onRealtimeHrValueChangedEvent(final BluetoothDevice bluetoothDevice, final int HrValue, final int DbValue);

    void onAirQualityValueChangedEvent(final BluetoothDevice bluetoothDevice, final int eco2, final int tvoc);

    void onColorIntensityValueChangedEvent(final BluetoothDevice bluetoothDevice, final float red, final float green, final float blue, final float alpha);

    void onButtonStateChangedEvent(final BluetoothDevice bluetoothDevice, final int buttonState);

    void onTapValueChangedEvent(final BluetoothDevice bluetoothDevice, final int direction, final int count);

    void onOrientationValueChangedEvent(final BluetoothDevice bluetoothDevice, final int orientation);

    void onQuaternionValueChangedEvent(final BluetoothDevice bluetoothDevice, final float w, final float x, final float y, final float z);

    void onPedometerValueChangedEvent(final BluetoothDevice bluetoothDevice, final int steps, final long duration);

    void onAccelerometerValueChangedEvent(final BluetoothDevice bluetoothDevice, final float x, final float y, final float z);

    void onGyroscopeValueChangedEvent(final BluetoothDevice bluetoothDevice, final float x, final float y, final float z);

    void onCompassValueChangedEvent(final BluetoothDevice bluetoothDevice, final float x, final float y, final float z);

    void onEulerAngleChangedEvent(final BluetoothDevice bluetoothDevice, final float roll, final float pitch, final float yaw);

    void onRotationMatrixValueChangedEvent(final BluetoothDevice bluetoothDevice, final byte[] matrix);

    void onHeadingValueChangedEvent(final BluetoothDevice bluetoothDevice, final float heading);

    void onGravityVectorChangedEvent(final BluetoothDevice bluetoothDevice, final float x, final float y, final float z);

    void onSpeakerStatusValueChangedEvent(final BluetoothDevice bluetoothDevice, final int status);

    //void onMicrophoneValueChangedEvent(final BluetoothDevice bluetoothDevice, final byte [] data);
    void onMicrophoneValueChangedEvent(final BluetoothDevice bluetoothDevice, final short[] data);


    void onHrRowDataGraphChangedEvent(final BluetoothDevice bluetoothDevice,
                                      final int mCurrentData,
                                      final float mAverData,
                                      final float mThreshold,
                                      final int mCountCandidate,
                                      final int mCountFinal);

    void onRrRowDataGraphChangedEvent(final BluetoothDevice bluetoothDevice,
                                         final int mCurrentData,
                                         final float mAverData,
                                         final float mThreshold,
                                         final int mCountCandidate,
                                         final int mCountFinal);
}
