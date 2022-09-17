package kr.zentry.devdolittle_graph_hr_file_3.common.Models;


public class animal_md {
    private String _id;
    private String owner;
    private String animal;
    private String name;
    private String birth;
    private int animal_type;
    private int gender;
    private String breed;
    private int cardiac;
    private String createdAt;
    private String updatedAt;
    private int __v;

    public int get__v() {
        return __v;
    }

    public String get_id() {
        return _id;
    }

    public int getAnimal_type() {
        return animal_type;
    }

    public int getCardiac() {
        return cardiac;
    }

    public int getGender() {
        return gender;
    }

    public String getAnimalId() {
        return animal;
    }

    public String getBirth() {
        return birth;
    }

    public String getBreed() {
        return breed;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setAnimal_type(int animal_type) {
        this.animal_type = animal_type;
    }

    public void setAnimalId(String animalId) {
        this.animal = animalId;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setCardiac(int cardiac) {
        this.cardiac = cardiac;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
