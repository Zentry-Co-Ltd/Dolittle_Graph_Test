package kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.manifest;

import com.google.gson.annotations.SerializedName;

public class Manifest {
    private FileInfo application;
    private FileInfo bootloader;
    private FileInfo softdevice;
    @SerializedName("softdevice_bootloader")
    private SoftDeviceBootloaderFileInfo softdeviceBootloader;

    // The following options are available only in some implementations of Secure DFU and will be sent as application (in a single connection).
    // The service is not aware of sizes of each component in the bin file. This information is hidden in the Init Packet.
    @SerializedName("bootloader_application")
    private FileInfo bootloaderApplication;
    @SerializedName("softdevice_application")
    private FileInfo softdeviceApplication;
    @SerializedName("softdevice_bootloader_application")
    private FileInfo softdeviceBootloaderApplication;

    public FileInfo getApplicationInfo() {
        if (application != null)
            return application;
        // The other parts will be sent together with application, so they may be returned here.
        if (softdeviceApplication != null)
            return softdeviceApplication;
        if (bootloaderApplication != null)
            return bootloaderApplication;
        return softdeviceBootloaderApplication;
    }

    public FileInfo getBootloaderInfo() {
        return bootloader;
    }

    public FileInfo getSoftdeviceInfo() {
        return softdevice;
    }

    public SoftDeviceBootloaderFileInfo getSoftdeviceBootloaderInfo() {
        return softdeviceBootloader;
    }

    public boolean isSecureDfuRequired() {
        // Legacy DFU requires sending firmware type together with Start DFU command.
        // The following options were not supported by the legacy bootloader,
        // but in some implementations they are supported in Secure DFU.
        // In Secure DFU the fw type is provided in the Init packet.
        return bootloaderApplication != null || softdeviceApplication != null || softdeviceBootloaderApplication != null;
    }
}
