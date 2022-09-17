package kr.zentry.devdolittle_graph_hr_file_4.dfu.internal;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.exception.HexFileValidationException;

public class HexInputStream extends FilterInputStream {
    private final int LINE_LENGTH = 128;

    private final byte[] localBuf;
    private int localPos;
    private int pos;
    private int size;
    private int lastAddress;
    private int available, bytesRead;
    private final int MBRSize;

    /**
     * Creates the HEX Input Stream. The constructor calculates the size of the BIN content which
     * is available through {@link #sizeInBytes()}. If HEX file is invalid then the bin size is 0.
     *
     * @param in      the input stream to read from.
     * @param mbrSize the MBR (Master Boot Record) size in bytes. Data with addresses below than
     *                number will be trimmed and not transferred to DFU target.
     * @throws HexFileValidationException if HEX file is invalid, e.g. there is no semicolon (':')
     *                                    on the beginning of each line.
     * @throws IOException                if the stream is closed or another IOException occurs.
     */
    public HexInputStream(@NonNull final InputStream in, final int mbrSize)
            throws HexFileValidationException, IOException {
        super(new BufferedInputStream(in));
        this.localBuf = new byte[LINE_LENGTH];
        this.localPos = LINE_LENGTH; // we are at the end of the local buffer, new one must be obtained
        this.size = localBuf.length;
        this.lastAddress = 0;
        this.MBRSize = mbrSize;

        this.available = calculateBinSize(mbrSize);
    }

    /**
     * Creates the HEX Input Stream. The constructor calculates the size of the BIN content which
     * is available through {@link #sizeInBytes()}. If HEX file is invalid then the bin size is 0.
     *
     * @param data    the input stream to read from.
     * @param mbrSize the MBR (Master Boot Record) size in bytes. Data with addresses below than
     * 	 *                number will be trimmed and not transferred to DFU target.
     * @throws HexFileValidationException if HEX file is invalid, e.g. there is no semicolon (':')
     *                                    on the beginning of each line.
     * @throws IOException                if the stream is closed or another IOException occurs.
     */
    public HexInputStream(@NonNull final byte[] data, final int mbrSize)
            throws HexFileValidationException, IOException {
        super(new ByteArrayInputStream(data));
        this.localBuf = new byte[LINE_LENGTH];
        this.localPos = LINE_LENGTH; // we are at the end of the local buffer, new one must be obtained
        this.size = localBuf.length;
        this.lastAddress = 0;
        this.MBRSize = mbrSize;

        this.available = calculateBinSize(mbrSize);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private int calculateBinSize(final int mbrSize) throws IOException {
        int binSize = 0;
        final InputStream in = this.in;
        in.mark(in.available());

        int b, lineSize, offset, type;
        int lastBaseAddress = 0; // last Base Address, default 0
        int lastAddress;
        try {
            b = in.read();
            while (true) {
                checkComma(b);

                lineSize = readByte(in); // reading the length of the data in this line
                offset = readAddress(in);// reading the offset
                type = readByte(in); // reading the line type
                switch (type) {
                    case 0x01:
                        // end of file
                        return binSize;
                    case 0x04: {
                        // extended linear address record
                        /*
                         * The HEX file may contain jump to different addresses.
                         * The MSB of LBA (Linear Base Address) is given using the line type 4.
                         * We only support files where bytes are located together, no jumps are
                         * allowed. Therefore the newULBA may be only lastULBA + 1 (or any,
                         * if this is the first line of the HEX)
                         */
                        final int newULBA = readAddress(in);
                        if (binSize > 0 && newULBA != (lastBaseAddress >> 16) + 1)
                            return binSize;
                        lastBaseAddress = newULBA << 16;
                        skip(in, 2 /* check sum */);
                        break;
                    }
                    case 0x02: {
                        // extended segment address record
                        final int newSBA = readAddress(in) << 4;
                        if (binSize > 0 && (newSBA >> 16) != (lastBaseAddress >> 16) + 1)
                            return binSize;
                        lastBaseAddress = newSBA;
                        skip(in, 2 /* check sum */);
                        break;
                    }
                    case 0x00:
                        // data type line
                        lastAddress = lastBaseAddress + offset;
                        // we must skip all data from below last MBR address (default 0x1000)
                        // as those are the MBR. The Soft Device starts at the end of MBR (0x1000),
                        // the app and bootloader farther more
                        if (lastAddress >= mbrSize)
                            binSize += lineSize;
                        // no break!
                    default:
                        final long toBeSkipped = lineSize * 2 /* 2 hex per one byte */ + 2 /* check sum */;
                        skip(in, toBeSkipped);
                        break;
                }
                // skip end of line
                do {
                    b = in.read();
                } while (b == '\n' || b == '\r');
            }
        } finally {
            in.reset();
        }
    }

    @Override
    public int available() {
        return available - bytesRead;
    }

    /**
     * Fills the buffer with next bytes from the stream.
     *
     * @param buffer buffer to be filled
     * @return the size of the buffer
     */
    public int readPacket(@NonNull byte[] buffer) throws IOException {
        int i = 0;
        while (i < buffer.length) {
            if (localPos < size) {
                buffer[i++] = localBuf[localPos++];
                continue;
            }

            bytesRead += size = readLine();
            if (size == 0)
                break; // end of file reached
        }
        return i;
    }

    @Override
    public int read() {
        throw new UnsupportedOperationException("Please, use readPacket() method instead");
    }

    @Override
    public int read(@NonNull byte[] buffer) throws IOException {
        return readPacket(buffer);
    }

    @Override
    public int read(@NonNull byte[] buffer, int offset, int count) {
        throw new UnsupportedOperationException("Please, use readPacket() method instead");
    }

    /**
     * Returns the total number of bytes.
     *
     * @return total number of bytes available
     */
    public int sizeInBytes() {
        return available;
    }

    /**
     * Returns the total number of packets with given size that are needed to get all
     * available data.
     *
     * @param packetSize the maximum packet size
     * @return the number of packets needed to get all the content
     */
    public int sizeInPackets(final int packetSize) {
        final int sizeInBytes = sizeInBytes();

        return sizeInBytes / packetSize + ((sizeInBytes % packetSize) > 0 ? 1 : 0);
    }

    /**
     * Reads new line from the input stream. Input stream must be a HEX file.
     * The first line is always skipped.
     *
     * @return the number of data bytes in the new line. 0 if end of file.
     * @throws java.io.IOException if this stream is closed or another IOException occurs.
     */
    private int readLine() throws IOException {
        // end of file reached
        if (pos == -1)
            return 0;
        final InputStream in = this.in;

        // temporary value
        int b;

        int lineSize, type, offset;
        do {
            // skip end of line
            do {
                b = in.read();
                pos++;
            } while (b == '\n' || b == '\r');

            /*
             * Each line starts with comma (':')
             * Data is written in HEX, so each 2 ASCII letters give one byte.
             * After the comma there is one byte (2 HEX signs) with line length
             * (normally 10 -> 0x10 -> 16 bytes -> 32 HEX characters)
             * After that there is a 4 byte of an address. This part may be skipped.
             * There is a packet type after the address (1 byte = 2 HEX characters).
             * 00 is the valid data. Other values can be skipped when converting to BIN file.
             * Then goes n bytes of data followed by 1 byte (2 HEX chars) of checksum,
             * which is also skipped in BIN file.
             */
            checkComma(b); // checking the comma at the beginning
            lineSize = readByte(in); // reading the length of the data in this line
            pos += 2;
            offset = readAddress(in);// reading the offset
            pos += 4;
            type = readByte(in); // reading the line type
            pos += 2;

            // if the line type is no longer data type (0x00), we've reached the end of the file
            switch (type) {
                case 0x00:
                    // data type
                    if (lastAddress + offset < MBRSize) { // skip MBR
                        type = -1; // some other than 0
                        pos += skip(in, lineSize * 2 /* 2 hex per one byte */ + 2 /* check sum */);
                    }
                    break;
                case 0x01:
                    // end of file
                    pos = -1;
                    return 0;
                case 0x02: {
                    // extended segment address
                    final int address = readAddress(in) << 4;
                    pos += 4;
                    if (bytesRead > 0 && (address >> 16) != (lastAddress >> 16) + 1)
                        return 0;
                    lastAddress = address;
                    pos += skip(in, 2 /* check sum */);
                    break;
                }
                case 0x04: {
                    // extended linear address
                    final int address = readAddress(in);
                    pos += 4;
                    if (bytesRead > 0 && address != (lastAddress >> 16) + 1)
                        return 0;
                    lastAddress = address << 16;
                    pos += skip(in, 2 /* check sum */);
                    break;
                }
                default:
                    final long toBeSkipped = lineSize * 2 /* 2 hex per one byte */ + 2 /* check sum */;
                    pos += skip(in, toBeSkipped);
                    break;
            }
        } while (type != 0);

        // otherwise read lineSize bytes or fill the whole buffer
        for (int i = 0; i < localBuf.length && i < lineSize; ++i) {
            b = readByte(in);
            pos += 2;
            localBuf[i] = (byte) b;
        }
        pos += skip(in, 2); // skip the checksum
        localPos = 0;

        return lineSize;
    }

    @Override
    public synchronized void mark(final int readlimit) {
        try {
            super.mark(in.available());
        } catch (final IOException e) {
            // ignore
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();

        pos = 0;
        bytesRead = 0;
        localPos = LINE_LENGTH; // we are at the end of the local buffer, new one must be obtained
    }

    private void checkComma(final int comma) throws HexFileValidationException {
        if (comma != ':')
            throw new HexFileValidationException("Not a HEX file");
    }

    private long skip(@NonNull final InputStream in, final long offset) throws IOException {
        long skipped = in.skip(offset);
        // try to skip 2 times as skip(..) method does not guarantee to skip exactly
        // given number of bytes
        if (skipped < offset)
            skipped += in.skip(offset - skipped);
        return skipped;
    }

    private int readByte(@NonNull final InputStream in) throws IOException {
        final int first = asciiToInt(in.read());
        final int second = asciiToInt(in.read());

        return first << 4 | second;
    }

    private int readAddress(@NonNull final InputStream in) throws IOException {
        return readByte(in) << 8 | readByte(in);
    }

    private int asciiToInt(final int ascii) {
        if (ascii >= 'A')
            return ascii - 0x37;

        if (ascii >= '0')
            return ascii - '0';
        return -1;
    }
}
