package kr.zentry.devdolittle_graph_hr_file_4.common.Models;

public class schedule_week_md {
    private boolean Monday;
    private boolean Tuesday;
    private boolean Wednesday;
    private boolean Thursday;
    private boolean Friday;
    private boolean Saturday;
    private boolean Sunday;

    public schedule_week_md() {

    }

    public schedule_week_md(boolean Monday, boolean Tuesday, boolean Wednesday, boolean Thursday, boolean Friday, boolean Saturday, boolean Sunday){
        this.Monday = Monday;
        this.Tuesday = Tuesday;
        this.Wednesday = Wednesday;
        this.Thursday = Thursday;
        this.Friday = Friday;
        this.Saturday = Saturday;
        this.Sunday = Sunday;
    }

    public boolean[] getAllWeek(){
        boolean[] week = {this.Monday, this.Tuesday, this.Wednesday, this.Thursday, this.Friday, this.Saturday, this.Sunday};
        return week;
    }

    public boolean getFriday() {
        return Friday;
    }

    public boolean getMonday() {
        return Monday;
    }

    public boolean getSaturday() {
        return Saturday;
    }

    public boolean getSunday() {
        return Sunday;
    }

    public boolean getThursday() {
        return Thursday;
    }

    public boolean getTuesday() {
        return Tuesday;
    }

    public boolean getWednesday() {
        return Wednesday;
    }

    public void setFriday(boolean friday) {
        Friday = friday;
    }

    public void setMonday(boolean monday) {
        Monday = monday;
    }

    public void setSaturday(boolean saturday) {
        Saturday = saturday;
    }

    public void setSunday(boolean sunday) {
        Sunday = sunday;
    }

    public void setThursday(boolean thursday) {
        Thursday = thursday;
    }

    public void setTuesday(boolean tuesday) {
        Tuesday = tuesday;
    }

    public void setWednesday(boolean wednesday) {
        Wednesday = wednesday;
    }
}
