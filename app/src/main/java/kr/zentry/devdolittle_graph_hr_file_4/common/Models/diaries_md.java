package kr.zentry.devdolittle_graph_hr_file_4.common.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class diaries_md {
    private diary_md[] result;

    public List<diary_md> getDiariesArrays() {
        List<diary_md> ret = new ArrayList<>(Arrays.asList(result));
        return ret;
    }

    public diary_md[] getDiaries() {
        return result;
    }

    public void setDiaries(diary_md[] diaries) {
        this.result = diaries;
    }
}
