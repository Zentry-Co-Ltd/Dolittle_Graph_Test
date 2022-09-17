package kr.zentry.devdolittle_graph_hr_file_4.common.Models;

public class user_get_md {
    private String email;
    private String nickname;

    public user_get_md() {
    }

    public user_get_md(String email, String nickname, Boolean marketPush) {
        this.email = email;
        this.nickname = nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }
}
