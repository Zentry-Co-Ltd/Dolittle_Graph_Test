package kr.zentry.devdolittle_graph_hr_file_4.common.Models;

public class manual_md {
    private String _id;
    private String owner;
    private String animal;
    private String type;
    private Integer data;
    private String createdAt;
    private String updatedAt;
    private Integer __v;

    public String getType() {
        return type;
    }

    public String getAnimal() {
        return animal;
    }

    public String getOwner() {
        return owner;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String get_id() {
        return _id;
    }

    public int get__v() {
        return __v;
    }

    public Integer getData() {
        return data;
    }

    public void setType(String type) {
        this.type = type;
    }
    public void setData(Integer data) {
        this.data = data;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void set__v(Integer __v) {
        this.__v = __v;
    }
}
