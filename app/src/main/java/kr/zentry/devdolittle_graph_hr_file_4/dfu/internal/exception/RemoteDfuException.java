package kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception;

public class RemoteDfuException extends Exception {
    private static final long serialVersionUID = -6901728550661937942L;

    private final int mState;

    public RemoteDfuException(final String message, final int state) {
        super(message);

        mState = state;
    }

    public int getErrorNumber() {
        return mState;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " (error " + mState + ")";
    }
}
