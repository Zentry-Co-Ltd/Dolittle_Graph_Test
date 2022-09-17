package kr.zentry.devdolittle_graph_hr_file_4.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

public class Mmath {
    // 평균
    public double avg(ArrayList<Double> _nums){
        double sum = 0.0;
        double ret = 0.0;
        int size = _nums.size();
        for (int i=0; i<size; i++){
            sum += _nums.get(i);
        }

        ret = sum / size;

        return ret;
    }

    // 표준 편차
    public double std(ArrayList<Double> _nums){
        double avg = avg(_nums);
        double ret = 0.0;
        double sum = 0.0;
        int size = _nums.size();

        ArrayList<Double> dis = new ArrayList<>();
        for(int i=0; i< _nums.size(); i++){
            dis.add(Math.pow((_nums.get(i) - avg), 2));
        }

        for(int i=0; i<size; i++){
            sum += dis.get(i);
        }

        ret = Math.sqrt(sum);

        return ret;
    }

    // 중간 요소 평균
    public double median(Queue<Double> _nums){
        ArrayList<Double> list = new ArrayList<>(_nums);
        Collections.sort(list);

        int size = _nums.size();
        double ret = 0.0;
        // get count of scores
        // check if total number of scores is even
        if (size % 2 == 0) {
            double sumOfMiddleElements = list.get(size / 2) + list.get(size / 2 - 1);
            // calculate average of middle elements
            ret = sumOfMiddleElements / 2;
        } else {
            // get the middle element
            ret = list.get(size / 2);
        }
        return ret;

    }
}
