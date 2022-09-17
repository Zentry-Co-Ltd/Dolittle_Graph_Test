package kr.zentry.devdolittle_graph_hr_file_3.common.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class schedules_md {
    private schedule_md[] result;

    public schedules_md(){

    }

    public List<schedule_md> getScheduleArrays(){
        List<schedule_md> ret = new ArrayList<>(Arrays.asList(result));
        return ret;
    }

    public schedules_md(schedule_md[] result){
        this.result = result;
    }

    public schedule_md[] getResult() {
        return result;
    }

    public void setResult(schedule_md[] result) {
        this.result = result;
    }
}
