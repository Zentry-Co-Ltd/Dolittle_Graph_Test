package kr.zentry.devdolittle_graph_hr_file_4.dfu;

import android.bluetooth.BluetoothGatt;
import android.content.Intent;

import androidx.annotation.NonNull;

import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.DeviceDisconnectedException;
import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.DfuException;
import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.UploadAbortedException;

/* package */ class DfuServiceProvider implements DfuCallback {
    private BaseDfuImpl mImpl;
    private boolean mPaused;
    private boolean mAborted;

    DfuService getServiceImpl(@NonNull final Intent intent, @NonNull final DfuBaseService service, @NonNull final BluetoothGatt gatt)
            throws DfuException, DeviceDisconnectedException, UploadAbortedException {
        try {
            mImpl = new ButtonlessDfuWithBondSharingImpl(intent, service);
            if (mImpl.isClientCompatible(intent, gatt))
                return mImpl;

            mImpl = new ButtonlessDfuWithoutBondSharingImpl(intent, service);
            if (mImpl.isClientCompatible(intent, gatt))
                return mImpl;

            mImpl = new SecureDfuImpl(intent, service);
            if (mImpl.isClientCompatible(intent, gatt))
                return mImpl;

            mImpl = new LegacyButtonlessDfuImpl(intent, service); // This will read the DFU Version char...
            if (mImpl.isClientCompatible(intent, gatt))
                return mImpl;

            mImpl = new LegacyDfuImpl(intent, service);           // ...that this impl will then use.
            if (mImpl.isClientCompatible(intent, gatt))
                return mImpl;

            // Support for experimental Buttonless DFU Service from SDK 12.
            // This feature must be explicitly enabled in the initiator.
            final boolean enableUnsafeExperimentalButtonlessDfuService = intent.getBooleanExtra(DfuBaseService.EXTRA_UNSAFE_EXPERIMENTAL_BUTTONLESS_DFU, false);
            if (enableUnsafeExperimentalButtonlessDfuService) {
                mImpl = new ExperimentalButtonlessDfuImpl(intent, service);
                if (mImpl.isClientCompatible(intent, gatt))
                    return mImpl;
            }
            // No implementation found
            return null;
        } finally {
            // Call pause() or abort() only on the chosen implementation
            if (mImpl != null) {
                if (mPaused)
                    mImpl.pause();
                if (mAborted)
                    mImpl.abort();
            }
        }
    }

    @Override
    public DfuGattCallback getGattCallback() {
        return mImpl != null ? mImpl.getGattCallback() : null;
    }

    @Override
    public void onBondStateChanged(final int state) {
        if (mImpl != null)
            mImpl.onBondStateChanged(state);
    }

    @Override
    public void pause() {
        mPaused = true;
    }

    @Override
    public void resume() {
        mPaused = false;
    }

    @Override
    public void abort() {
        mAborted = true;
        if (mImpl != null)
            mImpl.abort();
    }
}
