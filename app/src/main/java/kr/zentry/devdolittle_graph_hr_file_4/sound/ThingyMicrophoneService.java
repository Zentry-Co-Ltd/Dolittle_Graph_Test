package kr.zentry.devdolittle_graph_hr_file_4.sound;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import kr.zentry.devdolittle_graph_hr_file_4.common.Utils;
import kr.zentry.devdolittle_graph_hr_file_4.thingylib.ThingyConnection;
import kr.zentry.devdolittle_graph_hr_file_4.thingylib.ThingySdkManager;
import kr.zentry.devdolittle_graph_hr_file_4.thingylib.utils.ThingyUtils;

public class ThingyMicrophoneService extends IntentService {
    private static final int AUDIO_BUFFER = 512;

    private boolean mStartRecordingAudio = false;
    private BluetoothDevice mDevice;
    private ThingySdkManager mThingySdkManager;

    private BroadcastReceiver mAudioBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Utils.STOP_RECORDING)) {
                stopRecordingAudio();
                stopSelf();
            }
        }
    };

    /**
     * Default constructor, required to instantiate the service.
     */
    public ThingyMicrophoneService() {
        super("Thingy Microphone Service");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case Utils.START_RECORDING:
                mDevice = intent.getParcelableExtra(Utils.EXTRA_DEVICE);
                startRecordingAudio(mDevice);
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Utils.STOP_RECORDING);
        LocalBroadcastManager.getInstance(this).registerReceiver(mAudioBroadcastReceiver, filter);
        mThingySdkManager = ThingySdkManager.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAudioBroadcastReceiver);
    }

    public void startRecordingAudio(final BluetoothDevice device) {
        if (mStartRecordingAudio) {
            return;
        }
        mStartRecordingAudio = true;

        final ThingyConnection thingyConnection = mThingySdkManager.getThingyConnection(device);
        final AudioRecord audioRecorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, AUDIO_BUFFER);
        if (audioRecorder.getState() != AudioRecord.STATE_UNINITIALIZED) {
            byte audioData[] = new byte[AUDIO_BUFFER];
            audioRecorder.startRecording();
            while (mStartRecordingAudio) {
                int status = audioRecorder.read(audioData, 0, AUDIO_BUFFER);

                if (status == AudioRecord.ERROR_INVALID_OPERATION ||
                        status == AudioRecord.ERROR_BAD_VALUE) {
                    break;
                }
                try {
                    thingyConnection.playVoiceInput(downSample(audioData));
                    sendAudioRecordBroadcast(device, audioData, status);
                } catch (Exception e) {
                    break;
                }
            }

            audioRecorder.stop();
            audioRecorder.release();
        } else {
            sendAudioRecordErrorBroadcast(mDevice, audioRecorder.getState());
        }
    }

    public void stopRecordingAudio() {
        mStartRecordingAudio = false;
    }

    private byte[] downSample(final byte[] data) {
        final byte[] output = new byte[data.length / 2];
        int length = data.length;
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < length; i += 2) {
            output[i / 2] = (byte) (((bb.getShort() * 128.0) / 32768.0) + 128.0);
        }
        return output;
    }

    private void sendAudioRecordBroadcast(final BluetoothDevice device, final byte[] data, final int status) {
        final Intent intent = new Intent(Utils.EXTRA_DATA_AUDIO_RECORD + device.getAddress());
        intent.putExtra(ThingyUtils.EXTRA_DEVICE, device);
        intent.putExtra(ThingyUtils.EXTRA_DATA_PCM, data);
        intent.putExtra(ThingyUtils.EXTRA_DATA, status);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendAudioRecordErrorBroadcast(final BluetoothDevice device, final int error) {
        final Intent intent = new Intent(Utils.ERROR_AUDIO_RECORD + device.getAddress());
        intent.putExtra(ThingyUtils.EXTRA_DEVICE, device);
        intent.putExtra(ThingyUtils.EXTRA_DATA, error);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
