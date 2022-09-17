package kr.zentry.devdolittle_graph_hr_file_3.dfu;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DfuServiceController implements DfuController {
    private LocalBroadcastManager mBroadcastManager;
    private boolean mPaused;
    private boolean mAborted;

    /* package */ DfuServiceController(@NonNull final Context context) {
        mBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void pause() {
        if (!mAborted && !mPaused) {
            mPaused = true;
            final Intent pauseAction = new Intent(DfuBaseService.BROADCAST_ACTION);
            pauseAction.putExtra(DfuBaseService.EXTRA_ACTION, DfuBaseService.ACTION_PAUSE);
            mBroadcastManager.sendBroadcast(pauseAction);
        }
    }

    @Override
    public void resume() {
        if (!mAborted && mPaused) {
            mPaused = false;
            final Intent pauseAction = new Intent(DfuBaseService.BROADCAST_ACTION);
            pauseAction.putExtra(DfuBaseService.EXTRA_ACTION, DfuBaseService.ACTION_RESUME);
            mBroadcastManager.sendBroadcast(pauseAction);
        }
    }

    @Override
    public void abort() {
        if (!mAborted) {
            mAborted = true;
            mPaused = false;
            final Intent pauseAction = new Intent(DfuBaseService.BROADCAST_ACTION);
            pauseAction.putExtra(DfuBaseService.EXTRA_ACTION, DfuBaseService.ACTION_ABORT);
            mBroadcastManager.sendBroadcast(pauseAction);
        }
    }

    /**
     * Returns true if the DFU operation was paused.
     * It can be now resumed using {@link #resume()}.
     */
    public boolean isPaused() {
        return mPaused;
    }

    /**
     * Returns true if DFU was aborted.
     */
    public boolean isAborted() {
        return mAborted;
    }
}
