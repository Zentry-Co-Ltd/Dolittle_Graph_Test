package kr.zentry.devdolittle_graph_hr_file_4.dfu;

import android.bluetooth.BluetoothGattCharacteristic;

public interface DfuSettingsConstants {
    /**
     * This property must contain a boolean value.
     * <p>
     * If true (default) the Packet Receipt Notification procedure will be enabled.
     * See DFU documentation on
     * <a href="https://www.nordicsemi.com/en/DocLib/Content/SDK_Doc/nRF5_SDK/v15-2-0/examples_bootloader">NordicSemi/DocLib</a>
     * for more details. The number of packets before receiving a Packet Receipt Notification
     * is set with property {@link #SETTINGS_NUMBER_OF_PACKETS}.
     *
     * @deprecated Use {@link DfuServiceInitiator#setPacketsReceiptNotificationsEnabled(boolean)} to set it.
     */
    @Deprecated
    String SETTINGS_PACKET_RECEIPT_NOTIFICATION_ENABLED = "settings_packet_receipt_notification_enabled";

    /**
     * This property must contain a positive integer value, usually from range 1-200.
     * <p>The default value is {@value #SETTINGS_NUMBER_OF_PACKETS_DEFAULT}. Setting it to 0 will
     * disable the Packet Receipt Notification procedure. When sending a firmware using the
     * DFU procedure the service will send this number of packets before waiting for a notification.
     * Packet Receipt Notifications are used to synchronize the sender with receiver.
     * On Android, calling {@link android.bluetooth.BluetoothGatt#writeCharacteristic(BluetoothGattCharacteristic)}
     * will simply add the packet to outgoing queue before returning the callback. Adding the
     * next packet in the callback is much faster than the real transmission (also the speed depends on
     * the device chip manufacturer) and the queue may reach its limit. When does, the transmission
     * stops and Android Bluetooth hangs. Using PRN procedure eliminates this problem as
     * the notification is send when all packets were delivered the queue is empty.
     * <p>
     * Note: this bug has been fixed on Android 6.0 Marshmallow and now no notifications are required.
     * The onCharacteristicWrite callback will be postponed until half of the queue is empty.
     *
     * @deprecated Use {@link DfuServiceInitiator#setPacketsReceiptNotificationsValue(int)} to set it.
     */
    @Deprecated
    String SETTINGS_NUMBER_OF_PACKETS = "settings_number_of_packets";

    /**
     * The default value of {@link #SETTINGS_NUMBER_OF_PACKETS} property.
     * Different phones sent a different number of packets each connection interval.
     * The values are (for tested phones):
     * <ul>
     *     <li>1 packet - Nexus 4 and Nexus 7 and others</li>
     *     <li>4 packets - Nexus 5 and Nexus 6 and others</li>
     *     <li>6 packets - LG F60 and others</li>
     * </ul>
     * The least common multiplier is 12 which is reasonably small. You may try other values,
     * like 24 etc. Values higher than ~300 may cause the Bluetooth outgoing queue overflow
     * error on Android versions before Marshmallow.
     *
     * @deprecated Use {@link DfuServiceInitiator#setPacketsReceiptNotificationsValue(int)} to set it.
     */
    @Deprecated
    int SETTINGS_NUMBER_OF_PACKETS_DEFAULT = DfuServiceInitiator.DEFAULT_PRN_VALUE;

    /**
     * This property must contain an integer value.
     * <p>
     * Size of the MBR (Master Boot Record) for the target chip. This applies only if you are
     * using HEX files. The HEX_to_BIN parser included in the library will skip the addresses
     * from 0 to this value. By default for nRF51 and the SoftDevice S110 this value is equal
     * 4096 (0x1000 HEX) and for nRF52 has to be changed to 12288 (0x3000). If you want to send
     * a firmware in HEX onto another MCU via nRF chip, set this value to 0.
     * <p>
     * If you are using the PC nrf util tool to create a ZIP Distribution Packet with the
     * firmware and Init Packet this option does not apply as the nrf tool will convert
     * HEX to BIN itself.
     *
     * @deprecated Use {@link DfuServiceInitiator#setMbrSize(int)} instead.
     */
    @Deprecated
    String SETTINGS_MBR_SIZE = "settings_mbr_size";

    /**
     * This property must contain a boolean value.
     * <p>
     * The {@link DfuBaseService}, when connected to a DFU target will check whether it is
     * in application or in DFU bootloader mode. For DFU implementations from SDK 7.0 or newer
     * this is done by reading the value of DFU Version characteristic. If the returned value
     * is equal to 0x0100 (major = 0, minor = 1) it means that we are in the application mode and
     * jump to the bootloader mode is required.
     * <p>
     * However, for DFU implementations from older SDKs, where there was no DFU Version
     * characteristic, the service must guess. If this option is set to false (default) it will count
     * number of device's services. If the count is equal to 3 (Generic Access, Generic Attribute,
     * DFU Service) it will assume that it's in DFU mode. If greater than 3 - in app mode.
     * This guessing may not be always correct. One situation may be when the nRF chip is used
     * to flash update on external MCU using DFU. The DFU procedure may be implemented in the
     * application, which may (and usually does) have more services. In such case set the
     * value of this property to true.
     *
     * @deprecated Use {@link DfuServiceInitiator#setForceDfu(boolean)} instead.
     */
    @Deprecated
    String SETTINGS_ASSUME_DFU_NODE = "settings_assume_dfu_mode";
}

