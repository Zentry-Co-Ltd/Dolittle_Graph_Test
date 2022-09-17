package kr.zentry.devdolittle_graph_hr_file_4.dfu;

import androidx.annotation.IntDef;

@SuppressWarnings("WeakerAccess")
@IntDef(value = {
        DfuServiceInitiator.SCOPE_SYSTEM_COMPONENTS,
        DfuServiceInitiator.SCOPE_APPLICATION
},
        flag = true)
public @interface DfuScope {}
