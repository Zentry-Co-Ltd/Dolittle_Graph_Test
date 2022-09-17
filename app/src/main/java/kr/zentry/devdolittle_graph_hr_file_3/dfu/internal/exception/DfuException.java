package kr.zentry.devdolittle_graph_hr_file_3.dfu.internal.exception;

import kr.zentry.devdolittle_graph_hr_file_3.dfu.DfuBaseService;

public class DfuException extends Exception {
    private static final long serialVersionUID = -6901728550661937942L;

    private final int mError;

    public DfuException(final String message, final int state) {
        super(message);

        mError = state;
    }

    public int getErrorNumber() {
        return mError;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " (error " + (mError & ~DfuBaseService.ERROR_CONNECTION_MASK) + ")";
    }
}
