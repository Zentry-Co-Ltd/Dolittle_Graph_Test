package kr.zentry.devdolittle_graph_hr_file_4.common.Models;

public class session_login_md {
    private String accessToken;
    private String refreshToken;
    private String nickname;
    private String result;

    public session_login_md(){

    }

    public session_login_md(String accessToken, String refreshToken, String nickname, String result) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.nickname = nickname;
        this.result = result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getResult() {
        return result;
    }
}
