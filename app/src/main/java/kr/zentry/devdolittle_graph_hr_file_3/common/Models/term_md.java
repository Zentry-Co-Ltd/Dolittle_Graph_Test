package kr.zentry.devdolittle_graph_hr_file_3.common.Models;

public class term_md {
    private String _id;
    private String owner;
    private String conditions;
    private Integer __v;
    private String createdAt;
    private Boolean state;
    private String updatedAt;

    public Boolean getState() {
        return state;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getOwner() {
        return owner;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String get_id() {
        return _id;
    }

    public Integer get__v() {
        return __v;
    }

    public String getConditions() {
        return conditions;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void set__v(Integer __v) {
        this.__v = __v;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
}
