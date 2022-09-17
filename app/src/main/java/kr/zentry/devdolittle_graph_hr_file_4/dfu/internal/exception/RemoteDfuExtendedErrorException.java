package kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception;


import kr.zentry.devdolittle_graph_hr_file_4.error.SecureDfuError;

public class RemoteDfuExtendedErrorException extends RemoteDfuException {
    private static final long serialVersionUID = -6901728550661937942L;

    private final int mError;

    public RemoteDfuExtendedErrorException(final String message, final int extendedError) {
        super(message, SecureDfuError.EXTENDED_ERROR);

        mError = extendedError;
    }

    public int getExtendedErrorNumber() {
        return mError;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " (extended error " + mError + ")";
    }
}
