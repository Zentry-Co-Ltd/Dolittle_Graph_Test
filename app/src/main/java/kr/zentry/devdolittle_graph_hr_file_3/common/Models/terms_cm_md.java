package kr.zentry.devdolittle_graph_hr_file_3.common.Models;

public class terms_cm_md {
    private String result;
    private Boolean state;

    public terms_cm_md() {

    }

    public terms_cm_md(String result, Boolean state) {
        this.result = result;
        this.state = state;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public Boolean getState() {
        return state;
    }
}
