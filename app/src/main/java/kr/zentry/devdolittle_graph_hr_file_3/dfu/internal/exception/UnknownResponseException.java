package kr.zentry.devdolittle_graph_hr_file_3.dfu.internal.exception;


import java.util.Locale;

public class UnknownResponseException extends Exception {
    private static final long serialVersionUID = -8716125467309979289L;
    private static final char[] HEX_ARRAY = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private final byte[] mResponse;
    private final int mExpectedReturnCode;
    private final int mExpectedOpCode;

    public UnknownResponseException(final String message, final byte[] response, final int expectedReturnCode, final int expectedOpCode) {
        super(message);

        mResponse = response != null ? response : new byte[0];
        mExpectedReturnCode = expectedReturnCode;
        mExpectedOpCode = expectedOpCode;
    }

    @Override
    public String getMessage() {
        return String.format(Locale.US, "%s (response: %s, expected: 0x%02X%02X..)", super.getMessage(), bytesToHex(mResponse, 0, mResponse.length), mExpectedReturnCode, mExpectedOpCode);
    }

    public static String bytesToHex(final byte[] bytes, final int start, final int length) {
        if (bytes == null || bytes.length <= start || length <= 0)
            return "";

        final int maxLength = Math.min(length, bytes.length - start);
        final char[] hexChars = new char[maxLength * 2];
        for (int j = 0; j < maxLength; j++) {
            final int v = bytes[start + j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return "0x" + new String(hexChars);
    }
}
