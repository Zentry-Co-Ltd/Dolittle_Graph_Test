package kr.zentry.devdolittle_graph_hr_file_4.dfu;

public interface DfuLogListener {
    /**
     * Method called when a log event was sent from the DFU service.
     *
     * @param deviceAddress the target device address
     * @param level the log level, one of:
     * 		<ul>
     * 		    <li>{@link DfuBaseService#LOG_LEVEL_DEBUG}</li>
     * 		    <li>{@link DfuBaseService#LOG_LEVEL_VERBOSE}</li>
     * 		    <li>{@link DfuBaseService#LOG_LEVEL_INFO}</li>
     * 		    <li>{@link DfuBaseService#LOG_LEVEL_APPLICATION}</li>
     * 		    <li>{@link DfuBaseService#LOG_LEVEL_WARNING}</li>
     * 		    <li>{@link DfuBaseService#LOG_LEVEL_ERROR}</li>
     * 		</ul>
     * @param message the log message
     */
    void onLogEvent(final String deviceAddress, final int level, final String message);
}
