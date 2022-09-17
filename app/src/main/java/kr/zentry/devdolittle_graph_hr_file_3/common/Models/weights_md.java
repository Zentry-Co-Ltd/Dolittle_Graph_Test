package kr.zentry.devdolittle_graph_hr_file_3.common.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class weights_md {
    private weight_md[] result;

    public List<weight_md> getWeightsArrays() {
        List<weight_md> ret = new ArrayList<>(Arrays.asList(result));
        return ret;
    }

    public weight_md[] getWeights() {
        return result;
    }

    public void setWeights(weight_md[] weights) {
        this.result = weights;
    }
}
