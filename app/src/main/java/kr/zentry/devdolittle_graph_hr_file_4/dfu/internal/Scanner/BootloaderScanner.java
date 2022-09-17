package kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.Scanner;

import androidx.annotation.Nullable;

public interface BootloaderScanner {
    /**
     * After the buttonless jump from the application mode to the bootloader mode the service
     * will wait this long for the advertising bootloader (in milliseconds).
     */
    long TIMEOUT = 5000L; // ms
    /**
     * The bootloader may advertise with the same address or one with the last byte incremented
     * by this value. F.e. 00:11:22:33:44:55 -&gt; 00:11:22:33:44:56. FF changes to 00.
     */
    int ADDRESS_DIFF = 1;

    /**
     * Searches for the advertising bootloader. The bootloader may advertise with the same device
     * address or one with the last byte incremented by 1.
     * This method is a blocking one and ends when such device is found. There are two
     * implementations of this interface - one for Androids 4.3 and 4.4.x and one for the
     * Android 5+ devices.
     *
     * @param deviceAddress the application device address
     * @return the address of the advertising DFU bootloader. It may be the same as the application
     * address or one with the last byte incremented by 1 (AA:BB:CC:DD:EE:45/FF -&gt; AA:BB:CC:DD:EE:46/00).
     * Null is returned when Bluetooth is off or the device has not been found.
     */
    @Nullable
    String searchFor(final String deviceAddress);
}
