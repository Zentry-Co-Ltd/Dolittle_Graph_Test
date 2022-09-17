package kr.zentry.devdolittle_graph_hr_file_4.common.Models;

public class diary_register_md {

    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult (Result result) {
        this.result = result;
    }

    public class Result {
        private String _id;
        private String owner;
        private String animal;
        private String title;
        private String content;
        private String date;
        private String createdAt;
        private String updatedAt;
        private int __v;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getAnimal() {
            return animal;
        }

        public void setAnimal(String animal) {
            this.animal = animal;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }
    }

}
