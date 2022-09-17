package kr.zentry.devdolittle_graph_hr_file_3.error;

import kr.zentry.devdolittle_graph_hr_file_3.dfu.DfuBaseService;

public final class LegacyDfuError {
    // DFU status values
    // public static final int SUCCESS = 1; // that's not an error
    public static final int INVALID_STATE = 2;
    public static final int NOT_SUPPORTED = 3;
    public static final int DATA_SIZE_EXCEEDS_LIMIT = 4;
    public static final int CRC_ERROR = 5;
    public static final int OPERATION_FAILED = 6;

    public static String parse(final int error) {
        switch (error) {
            case DfuBaseService.ERROR_REMOTE_TYPE_LEGACY | INVALID_STATE:				return "INVALID STATE";
            case DfuBaseService.ERROR_REMOTE_TYPE_LEGACY | NOT_SUPPORTED:				return "NOT SUPPORTED";
            case DfuBaseService.ERROR_REMOTE_TYPE_LEGACY | DATA_SIZE_EXCEEDS_LIMIT:		return "DATA SIZE EXCEEDS LIMIT";
            case DfuBaseService.ERROR_REMOTE_TYPE_LEGACY | CRC_ERROR:					return "INVALID CRC ERROR";
            case DfuBaseService.ERROR_REMOTE_TYPE_LEGACY | OPERATION_FAILED:			return "OPERATION FAILED";
            default:
                return "UNKNOWN (" + error + ")";
        }
    }
}
