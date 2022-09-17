package kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception;

import java.io.IOException;

public class HexFileValidationException extends IOException {
    private static final long serialVersionUID = -6467104024030837875L;

    public HexFileValidationException(final String message) {
        super(message);
    }
}
