package kr.zentry.devdolittle_graph_hr_file_3.thingylib;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.HashMap;
import java.util.Map;

import kr.zentry.devdolittle_graph_hr_file_3.thingylib.utils.ThingyUtils;

public class ThingyListenerHelper {

    private static ThingyBroadcastReceiver mThingyBroadcastReceiver;

    public static class ThingyBroadcastReceiver extends BroadcastReceiver {

        private ThingyListener mGlobalThingyListener;
        private Map<BluetoothDevice, ThingyListener> mListeners = new HashMap<>();

        public void setThingyListener(final ThingyListener listener) {
            this.mGlobalThingyListener = listener;
        }

        public void setThingyListener(final BluetoothDevice device, final ThingyListener listener) {
            this.mListeners.put(device, listener);
        }

        public boolean removeThingyListener(final ThingyListener listener) {

            if (mGlobalThingyListener == listener) {
                mGlobalThingyListener = null;
            }

            // We do it 2 times as the listener was added for 2 addresses
            for (final Map.Entry<BluetoothDevice, ThingyListener> entry : mListeners.entrySet()) {
                if (entry.getValue() == listener) {
                    mListeners.remove(entry.getKey());
                    break;
                }
            }

            return mGlobalThingyListener == null && mListeners.isEmpty();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final BluetoothDevice device = intent.getParcelableExtra(ThingyUtils.EXTRA_DEVICE);

            // Find proper listeners
            final ThingyListener globalListener = mGlobalThingyListener;
            final ThingyListener thingyListener = mListeners.get(device);

            if (globalListener == null && thingyListener == null)
                return;

            final String action = intent.getAction();
            switch (action) {
                //소리 측정(심박)
                case ThingyUtils.MICROPHONE_NOTIFICATION:
                    //final byte [] data = intent.getExtras().getByteArray(ThingyUtils.EXTRA_DATA_PCM);
                    final short[] data = intent.getExtras().getShortArray(ThingyUtils.EXTRA_DATA_PCM);
                    //final short data = intent.getExtras().getShort(ThingyUtils.EXTRA_DATA_PCM);
                    if (globalListener != null) {
                        globalListener.onMicrophoneValueChangedEvent(device, data);
                    }

                    if (thingyListener != null) {
                        thingyListener.onMicrophoneValueChangedEvent(device, data);
                    }
                    break;
                //심박 그래프 테스트 로우데이터 값
                case ThingyUtils.HrROWDATAGRAPH_NOTIFICATION:
                    final int CurrentData_hr = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_CURRNET);
                    final float mAverData_hr = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_AVER);
                    final float mThreshold_hr = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_THRESHOLD);
                    final int mCountCandidate_hr = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_COUNTCANDIDATE);
                    final int mCountFinal_hr = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_COUNTFINAL);

                    if (globalListener != null) {
                        globalListener.onHrRowDataGraphChangedEvent(device, CurrentData_hr, mAverData_hr, mThreshold_hr, mCountCandidate_hr, mCountFinal_hr);
                    }
                    break;

                //호흡 그래프 테스트 로우데이터 값
                case ThingyUtils.RrROWDATAGRAPH_NOTIFICATION:
                    final int mCurrentData_rr = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_CURRNET);
                    final float mAverData_rr = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_AVER);
                    final float mThreshold_rr = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_THRESHOLD);
                    final int mCountCandidate_rr = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_COUNTCANDIDATE);
                    final int mCountFinal_rr = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_COUNTFINAL);

                    if (globalListener != null) {
                        globalListener.onRrRowDataGraphChangedEvent(device, mCurrentData_rr, mAverData_rr, mThreshold_rr, mCountCandidate_rr, mCountFinal_rr);
                    }
                    break;


                //카운팅 심박 수
                case ThingyUtils.REALTIME_HR_NOTIFICATION:
                    final int REALTIME_HR = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA);
                    final int REALTIME_DB = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA1);
                    //심박수 정보를 MeasureDolittleFragment에 전달
                    if (globalListener != null) {
                        globalListener.onRealtimeHrValueChangedEvent(device, REALTIME_HR, REALTIME_DB);
                    }
                    if (thingyListener != null) {
                        thingyListener.onRealtimeHrValueChangedEvent(device, REALTIME_HR, REALTIME_DB);
                    }
                    break;


                //호흡 압력 값 측정
                case ThingyUtils.PRESSURE_NOTIFICATION:
                    final String pressure = intent.getExtras().getString(ThingyUtils.EXTRA_DATA);
                    if (globalListener != null) {
                        globalListener.onPressureValueChangedEvent(device, pressure);
                    }

                    if (thingyListener != null) {
                        thingyListener.onPressureValueChangedEvent(device, pressure);
                    }
                    break;
                //카운팅 호흡 수
                case ThingyUtils.REALTIME_RR_NOTIFICATION:
                    final int REALTIME_RR = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA);

                    if (globalListener != null) {
                        globalListener.onRealtimeRrValueChangedEvent(device, REALTIME_RR);
                    }

                    if (thingyListener != null) {
                        thingyListener.onRealtimeRrValueChangedEvent(device, REALTIME_RR);
                    }
                    break;


                case ThingyUtils.ACTION_DEVICE_CONNECTED:
                    if (globalListener != null) {
                        globalListener.onDeviceConnected(device, BluetoothGatt.STATE_CONNECTED);
                    }

                    if (thingyListener != null) {
                        thingyListener.onDeviceConnected(device, 1);
                    }
                    break;
                case ThingyUtils.ACTION_DEVICE_DISCONNECTED:
                    boolean isActivated = false;

                    if (globalListener != null) {
                        globalListener.onDeviceDisconnected(device, BluetoothGatt.STATE_DISCONNECTED);
                        isActivated = true;
                    }

                    if (thingyListener != null & !isActivated) {
                        thingyListener.onDeviceDisconnected(device, 1);
                    }
                    break;
                case ThingyUtils.ACTION_SERVICE_DISCOVERY_COMPLETED:
                    if (globalListener != null) {
                        globalListener.onServiceDiscoveryCompleted(device);
                    }

                    if (thingyListener != null) {
                        thingyListener.onServiceDiscoveryCompleted(device);
                    }
                    break;
                case ThingyUtils.ACTION_DATA_RECEIVED:
                    break;
                case ThingyUtils.BATTERY_LEVEL_NOTIFICATION:
                    final int batteryLevel = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA);
                    if (globalListener != null) {
                        globalListener.onBatteryLevelChanged(device, batteryLevel);
                    }

                    if (thingyListener != null) {
                        thingyListener.onBatteryLevelChanged(device, batteryLevel);
                    }
                    break;
//                case ThingyUtils.TEMPERATURE_NOTIFICATION:
//                    final String temperature = intent.getExtras().getString(ThingyUtils.EXTRA_DATA);
//                    if(globalListener != null) {
//                        globalListener.onTemperatureValueChangedEvent(device, temperature);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onTemperatureValueChangedEvent(device, temperature);
//                    }
//                    break;
//
//                case ThingyUtils.HUMIDITY_NOTIFICATION:
//                    final String humidity = intent.getExtras().getString(ThingyUtils.EXTRA_DATA);
//                    if(globalListener != null) {
//                        globalListener.onHumidityValueChangedEvent(device, humidity);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onHumidityValueChangedEvent(device, humidity);
//                    }
//                    break;
//                case ThingyUtils.AIR_QUALITY_NOTIFICATION:
//                    final int eco2 = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_ECO2);
//                    final int tvoc = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_TVOC);
//                    if(globalListener != null) {
//                        globalListener.onAirQualityValueChangedEvent(device, eco2, tvoc);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onAirQualityValueChangedEvent(device, eco2, tvoc);
//                    }
//                    break;
//                case ThingyUtils.COLOR_NOTIFICATION:
//                    final float r = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_RED);
//                    final float g = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_GREEN);
//                    final float b = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_BLUE);
//                    final float a = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_CLEAR);
//                    if(globalListener != null) {
//                        globalListener.onColorIntensityValueChangedEvent(device, r, g, b, a);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onColorIntensityValueChangedEvent(device, r, g, b, a);
//                    }
//                    break;
//                case ThingyUtils.BUTTON_STATE_NOTIFICATION:
//                    final int state = intent.getIntExtra(ThingyUtils.EXTRA_DATA_BUTTON, -1);
//                    if(globalListener != null) {
//                        globalListener.onButtonStateChangedEvent(device, state);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onButtonStateChangedEvent(device, state);
//                    }
//                    break;
//                case ThingyUtils.TAP_NOTIFICATION:
//                    final int direction = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_TAP_DIRECTION);
//                    final int tapCount = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_TAP_COUNT);
//                    if(globalListener != null) {
//                        globalListener.onTapValueChangedEvent(device, direction, tapCount);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onTapValueChangedEvent(device, direction, tapCount);
//                    }
//                    break;
//                case ThingyUtils.ORIENTATION_NOTIFICATION:
//                    final int orientation = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA);
//                    if(globalListener != null) {
//                        globalListener.onOrientationValueChangedEvent(device, orientation);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onOrientationValueChangedEvent(device, orientation);
//                    }
//                    break;
//                case ThingyUtils.QUATERNION_NOTIFICATION:
//                    final float w = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_QUATERNION_W);
//                    final float x = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_QUATERNION_X);
//                    final float y = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_QUATERNION_Y);
//                    final float z = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_QUATERNION_Z);
//                    if(globalListener != null) {
//                        globalListener.onQuaternionValueChangedEvent(device, w,x,y,z);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onQuaternionValueChangedEvent(device, w,x,y,z);
//                    }
//                    break;
//                case ThingyUtils.PEDOMETER_NOTIFICATION:
//                    final int stepCount = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_STEP_COUNT);
//                    final long duration = intent.getExtras().getLong(ThingyUtils.EXTRA_DATA_DURATION);
//                    if(globalListener != null) {
//                        globalListener.onPedometerValueChangedEvent(device,stepCount, duration);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onPedometerValueChangedEvent(device,stepCount, duration);
//                    }
//                    break;
//                case ThingyUtils.RAW_DATA_NOTIFICATION:
//                    final float accelerometerX = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_ACCELEROMETER_X);
//                    final float accelerometerY = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_ACCELEROMETER_Y);
//                    final float accelerometerZ = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_ACCELEROMETER_Z);
//
//                    final float gyroscopeX = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_GYROSCOPE_X);
//                    final float gyroscopeY = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_GYROSCOPE_Y);
//                    final float gyroscopeZ = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_GYROSCOPE_Z);
//
//                    final float compassX = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_COMPASS_X);
//                    final float compassY = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_COMPASS_Y);
//                    final float compassZ = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_COMPASS_Z);
//
//                    if(globalListener != null) {
//                        globalListener.onAccelerometerValueChangedEvent(device, accelerometerX, accelerometerY, accelerometerZ);
//                        globalListener.onGyroscopeValueChangedEvent(device, gyroscopeX, gyroscopeY, gyroscopeZ);
//                        globalListener.onCompassValueChangedEvent(device, compassX, compassY, compassZ);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onAccelerometerValueChangedEvent(device, accelerometerX, accelerometerY, accelerometerZ);
//                        thingyListener.onGyroscopeValueChangedEvent(device, gyroscopeX, gyroscopeY, gyroscopeZ);
//                        thingyListener.onCompassValueChangedEvent(device, compassX, compassY, compassZ);
//                    }
//                    break;
//                case ThingyUtils.EULER_NOTIFICATION:
//                    final float roll = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_ROLL);
//                    final float pitch = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_PITCH);
//                    final float yaw = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_YAW);
//                    if(globalListener != null) {
//                        globalListener.onEulerAngleChangedEvent(device, roll, pitch, yaw);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onEulerAngleChangedEvent(device, roll, pitch, yaw);
//                    }
//                    break;
//                case ThingyUtils.ROTATION_MATRIX_NOTIFICATION:
//                    final byte [] rotationMatrix = intent.getExtras().getByteArray(ThingyUtils.EXTRA_DATA_ROTATION_MATRIX);
//                    if(globalListener != null) {
//                        globalListener.onRotationMatrixValueChangedEvent(device, rotationMatrix);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onRotationMatrixValueChangedEvent(device, rotationMatrix);
//                    }
//                    break;
//                case ThingyUtils.HEADING_NOTIFICATION:
//                    final float heading = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA);
//                    if(globalListener != null) {
//                        globalListener.onHeadingValueChangedEvent(device,heading);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onHeadingValueChangedEvent(device,heading);
//                    }
//                    break;
//                case ThingyUtils.GRAVITY_NOTIFICATION:
//                    final float gravityX = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_GRAVITY_X);
//                    final float gravityY = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_GRAVITY_Y);
//                    final float gravityZ = intent.getExtras().getFloat(ThingyUtils.EXTRA_DATA_GRAVITY_Z);
//                    if(globalListener != null) {
//                        globalListener.onGravityVectorChangedEvent(device, gravityX, gravityY, gravityZ);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onGravityVectorChangedEvent(device, gravityX, gravityY, gravityZ);
//                    }
//                    break;
//                case ThingyUtils.SPEAKER_STATUS_NOTIFICATION:
//                    final int status = intent.getExtras().getInt(ThingyUtils.EXTRA_DATA_SPEAKER_STATUS_NOTIFICATION);
//                    if(globalListener != null) {
//                        globalListener.onSpeakerStatusValueChangedEvent(device, status);
//                    }
//
//                    if(thingyListener != null) {
//                        thingyListener.onSpeakerStatusValueChangedEvent(device, status);
//                    }
//                    break;

            }
        }
    }

    /**
     * 11
     * Registers the {@link ThingyListener}. Registered listener will receive the progress events from the BaseThingy service.
     *
     * @param context  the application context
     * @param listener the listener to register
     */
    public static void registerThingyListener(final Context context, final ThingyListener listener) {
        if (mThingyBroadcastReceiver == null) {
            mThingyBroadcastReceiver = new ThingyBroadcastReceiver();

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ThingyUtils.ACTION_DEVICE_CONNECTED);
            intentFilter.addAction(ThingyUtils.ACTION_DEVICE_DISCONNECTED);
            intentFilter.addAction(ThingyUtils.ACTION_SERVICE_DISCOVERY_COMPLETED);
            intentFilter.addAction(ThingyUtils.EXTRA_DEVICE);
            intentFilter.addAction(ThingyUtils.BATTERY_LEVEL_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.TEMPERATURE_NOTIFICATION);
            //심박수 로우데이터 측정 테스트 그래프 용
            intentFilter.addAction(ThingyUtils.HrROWDATAGRAPH_NOTIFICATION);
            //호흡수 로우데이터 측정 테스트 그래프 용
            intentFilter.addAction(ThingyUtils.RrROWDATAGRAPH_NOTIFICATION);
            intentFilter.addAction(ThingyUtils.PRESSURE_NOTIFICATION);
            intentFilter.addAction(ThingyUtils.REALTIME_HR_NOTIFICATION);
            intentFilter.addAction(ThingyUtils.REALTIME_RR_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.HUMIDITY_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.AIR_QUALITY_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.COLOR_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.BUTTON_STATE_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.TAP_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.ORIENTATION_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.QUATERNION_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.PEDOMETER_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.RAW_DATA_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.EULER_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.ROTATION_MATRIX_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.HEADING_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.GRAVITY_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.SPEAKER_STATUS_NOTIFICATION);
            intentFilter.addAction(ThingyUtils.MICROPHONE_NOTIFICATION);

            LocalBroadcastManager.getInstance(context).registerReceiver(mThingyBroadcastReceiver, intentFilter);
        }
        mThingyBroadcastReceiver.setThingyListener(listener);
    }

    /**
     * Registers the {@link ThingyListener}. Registered listener will receive the progress events from the BaseThingy service.
     *
     * @param context  the application context
     * @param listener the listener to register
     */
    public static void registerThingyListener(final Context context, final ThingyListener listener, final BluetoothDevice device) {
        if (mThingyBroadcastReceiver == null) {
            mThingyBroadcastReceiver = new ThingyBroadcastReceiver();

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ThingyUtils.ACTION_DEVICE_CONNECTED);
            intentFilter.addAction(ThingyUtils.ACTION_DEVICE_DISCONNECTED);
            intentFilter.addAction(ThingyUtils.ACTION_SERVICE_DISCOVERY_COMPLETED);
            intentFilter.addAction(ThingyUtils.EXTRA_DEVICE);
            intentFilter.addAction(ThingyUtils.BATTERY_LEVEL_NOTIFICATION);
            //심박수 로우데이터 측정 테스트 그래프 용
            intentFilter.addAction(ThingyUtils.HrROWDATAGRAPH_NOTIFICATION);
            //호흡수 로우데이터 측정 테스트 그래프 용
            intentFilter.addAction(ThingyUtils.RrROWDATAGRAPH_NOTIFICATION);
            intentFilter.addAction(ThingyUtils.PRESSURE_NOTIFICATION);
            intentFilter.addAction(ThingyUtils.REALTIME_HR_NOTIFICATION);
            intentFilter.addAction(ThingyUtils.REALTIME_RR_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.HUMIDITY_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.AIR_QUALITY_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.COLOR_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.BUTTON_STATE_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.TAP_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.ORIENTATION_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.QUATERNION_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.PEDOMETER_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.RAW_DATA_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.EULER_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.ROTATION_MATRIX_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.HEADING_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.GRAVITY_NOTIFICATION);
//            intentFilter.addAction(ThingyUtils.SPEAKER_STATUS_NOTIFICATION);
            intentFilter.addAction(ThingyUtils.MICROPHONE_NOTIFICATION);

            LocalBroadcastManager.getInstance(context).registerReceiver(mThingyBroadcastReceiver, intentFilter);
        }
        mThingyBroadcastReceiver.setThingyListener(device, listener);
    }

    public static void unregisterThingyListener(final Context context, final ThingyListener listener) {
        if (mThingyBroadcastReceiver != null) {
            final boolean empty = mThingyBroadcastReceiver.removeThingyListener(listener);

            if (empty) {
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mThingyBroadcastReceiver);
                mThingyBroadcastReceiver = null;
            }
        }
    }
}

