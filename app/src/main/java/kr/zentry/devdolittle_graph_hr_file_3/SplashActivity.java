package kr.zentry.devdolittle_graph_hr_file_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.gson.Gson;

import kr.zentry.devdolittle_graph_hr_file_3.common.DolittleAPI;
import retrofit2.Retrofit;

public class SplashActivity extends AppCompatActivity {

    private Retrofit retrofit_kakao;
    private Retrofit retrofit_dolittle;
    private DolittleAPI.KakaoAPIs kakaoAPIs;
    private DolittleAPI.DolittleAPIs dolittleAPIs;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



    }

}