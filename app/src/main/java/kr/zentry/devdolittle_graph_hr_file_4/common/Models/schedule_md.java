package kr.zentry.devdolittle_graph_hr_file_4.common.Models;

public class schedule_md {
    private String _id;
    private String owner;
    private String animal;
    private String title;
    private boolean alarmSwitch;
    private boolean repeat;
    private String time;
    private schedule_week_md week;
    private String createdAt;
    private String updatedAt;
    private int __v;

    public schedule_md(){

    }

    public schedule_md(String _id, String owner, String animal, String title, boolean alarmSwitch, boolean repeat, String time, schedule_week_md week, String createdAt, String updatedAt, int __v){
        this._id = _id;
        this.owner = owner;
        this.animal = animal;
        this.title = title;
        this.alarmSwitch = alarmSwitch;
        this.repeat = repeat;
        this.time = time;
        this.week = week;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.__v = __v;
    }

    public boolean getAlarmSwitch() {
        return alarmSwitch;
    }

    public boolean getRepeat() {
        return repeat;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String get_id() {
        return _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getOwner() {
        return owner;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public int get__v() {
        return __v;
    }

    public schedule_week_md getWeek() {
        return week;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAlarmSwitch(boolean alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWeek(schedule_week_md week) {
        this.week = week;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }
}