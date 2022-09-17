package kr.zentry.devdolittle_graph_hr_file_3.dfu;

import androidx.annotation.IntDef;

@SuppressWarnings("WeakerAccess")
@IntDef(value = {
        DfuServiceInitiator.SCOPE_SYSTEM_COMPONENTS,
        DfuServiceInitiator.SCOPE_APPLICATION
},
        flag = true)
public @interface DfuScope {}
