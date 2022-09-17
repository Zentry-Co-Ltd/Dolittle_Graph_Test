package kr.zentry.devdolittle_graph_hr_file_3;

public class RowData {
    String currentData;
    String average;
    String threshold;
    String candidateCount;
    String lastCount;

    public RowData(String currentData, String average, String threshold, String candidateCount, String lastCount) {
        this.currentData = currentData;
        this.average = average;
        this.threshold = threshold;
        this.candidateCount = candidateCount;
        this.lastCount = lastCount;
    }
}
