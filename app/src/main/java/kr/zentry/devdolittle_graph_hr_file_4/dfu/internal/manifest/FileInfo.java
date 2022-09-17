package kr.zentry.devdolittle_graph_hr_file_4.dfu.internal.manifest;

import com.google.gson.annotations.SerializedName;

public class FileInfo {
    @SerializedName("bin_file") private String binFile;
    @SerializedName("dat_file") private String datFile;

    public String getBinFileName() {
        return binFile;
    }

    public String getDatFileName() {
        return datFile;
    }
}
