package kr.zentry.devdolittle_graph_hr_file_4.dfu;


import androidx.annotation.IntDef;

@SuppressWarnings("WeakerAccess")
@IntDef(value = {
        DfuBaseService.TYPE_SOFT_DEVICE,
        DfuBaseService.TYPE_BOOTLOADER,
        DfuBaseService.TYPE_APPLICATION
},
        flag = true)
public @interface FileType {}

