package kr.zentry.devdolittle_graph_hr_file_3.dfu.internal.exception;

import java.io.IOException;

/**
 * This exception is thrown when the firmware size is not word-aligned (number of bytes does not divide by 4).
 * This is the requirement for the DFU Bootloader.
 */
public class SizeValidationException extends IOException {
    private static final long serialVersionUID = -6467104024030837875L;

    public SizeValidationException(final String message) {
        super(message);
    }
}
