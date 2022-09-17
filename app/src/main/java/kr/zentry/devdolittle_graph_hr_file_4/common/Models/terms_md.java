package kr.zentry.devdolittle_graph_hr_file_4.common.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class terms_md {
    private term_md[] result;

    public List<term_md> getWeightsArrays() {
        List<term_md> ret = new ArrayList<>(Arrays.asList(result));
        return ret;
    }

    public term_md[] getWeights() {
        return result;
    }

    public void setWeights(term_md[] weights) {
        this.result = weights;
    }
}
