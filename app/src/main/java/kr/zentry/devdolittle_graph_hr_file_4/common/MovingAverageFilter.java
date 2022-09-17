package kr.zentry.devdolittle_graph_hr_file_4.common;

import java.util.ArrayList;


/* 이동 평균 필터 값으로 변환 */
public class MovingAverageFilter {
    private double sum = 0.0;
    private int check_N = 0;
    private boolean sw = false;
    private int MAX = 0;

    ArrayList<Double> raw_array = new ArrayList<>();

    public MovingAverageFilter(int _MAX) {
        MAX = _MAX;
        for (int i = 0; i < _MAX; i++) {
            raw_array.add(0.0);
        }
    }


    public float calc(double number) {
        if (check_N > MAX-1) {
            check_N = 0;
            sw = true;
        }

        raw_array.set(check_N, number);
        check_N += 1;
        sum = 0;

        if (sw) {
            for (int i = 0; i < MAX; i++) {
                sum = sum + raw_array.get(i);
            }
            return ((float) sum / MAX);
        } else {
            for (int i = 0; i < check_N; i++) {
                sum = sum + raw_array.get(i);
            }
            return ((float) sum / check_N);
        }
    }
}