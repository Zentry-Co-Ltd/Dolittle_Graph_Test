package kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.manifest;

import com.google.gson.annotations.SerializedName;

public class SoftDeviceBootloaderFileInfo extends FileInfo {
    @SerializedName("bl_size") private int bootloaderSize;
    @SerializedName("sd_size") private int softdeviceSize;

    public int getSoftdeviceSize() {
        return softdeviceSize;
    }

    public int getBootloaderSize() {
        return bootloaderSize;
    }
}
