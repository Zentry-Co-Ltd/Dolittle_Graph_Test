package kr.zentry.devdolittle_graph_hr_file_4;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import kr.zentry.devdolittle_graph_hr_file_4.common.DolittleAPI;
import kr.zentry.devdolittle_graph_hr_file_4.common.Utils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeasureTapFragment extends Fragment {

    private TextView tv_start;

    private Button btn_touch;
    private Button btn_tap_restart;

    private RadioGroup radio_tap_measure_time;
    private RadioGroup radio_measure_type;

    private CircleProgressBar progressbar_tap;
    private ConstraintLayout.LayoutParams param_btn_touch;
    private ConstraintLayout.LayoutParams param_progressbar_tap;

    private TimerHandler timerHandler = new TimerHandler();
    private TimerThread timerThread;

    private int MEASURE_TIME; //초 단위임
    private int count = 0;
    private int countPerMinute = 0;

    private double deviceHeight;
    private double deviceWidth;

    private boolean isStart = false;
    private boolean isFinish = false;

    private Realm realm;
    private Retrofit retrofit_dolittle;
    private DolittleAPI.DolittleAPIs dolittleAPIs;
    private Gson gson = new Gson();

    private Vibrator vibrator;

    private String INTENT_EXTRA_TIME;

    private boolean isHeartMeasure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_measure_tap, container, false);

        progressbar_tap = v.findViewById(R.id.progressbar_tap);
        btn_touch = v.findViewById(R.id.btn_tap_touch);
        btn_tap_restart = v.findViewById(R.id.btn_tap_restart);
        tv_start = v.findViewById(R.id.tv_start);
        radio_measure_type = v.findViewById(R.id.radio_measure_type);
        radio_tap_measure_time = v.findViewById(R.id.radio_tap_measure_time);
        param_progressbar_tap = (ConstraintLayout.LayoutParams) progressbar_tap.getLayoutParams();
        param_btn_touch = (ConstraintLayout.LayoutParams) btn_touch.getLayoutParams();
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        INTENT_EXTRA_TIME = getString(R.string.glb_intent_extra_time);

        isHeartMeasure = true;

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);

        retrofit_dolittle = new Retrofit.Builder()
                .baseUrl(getString(R.string.glb_api_dolittle_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build();

        dolittleAPIs = retrofit_dolittle.create(DolittleAPI.DolittleAPIs.class);

        realm = Realm.getDefaultInstance();


        // 측정시간 설정
        MEASURE_TIME = Utils.getMeasureTime(getActivity());

        switch (MEASURE_TIME) {
            case 30:
                radio_tap_measure_time.check(R.id.radio_tap_measure_time_30);
                break;

            case 60:
                radio_tap_measure_time.check(R.id.radio_tap_measure_time_60);
                break;
        }

        radio_measure_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_measure_type_heart:
                        isHeartMeasure = true;
                        break;

                    case R.id.radio_measure_type_res:
                        isHeartMeasure = false;
                        break;
                }
            }
        });
        radio_measure_type.setClipToOutline(true);
        radio_measure_type.check(R.id.radio_measure_type_heart);

        radio_tap_measure_time.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_tap_measure_time_30:
                        Utils.setMeasureTime(getActivity(), 30);
                        MEASURE_TIME = 30;
                        break;

                    case R.id.radio_tap_measure_time_60:
                        Utils.setMeasureTime(getActivity(), 60);
                        MEASURE_TIME = 60;
                        break;
                }
            }
        });
        radio_tap_measure_time.setClipToOutline(true);


        //프로그래스바 초기화
        progressbar_tap.setMax(MEASURE_TIME * 1000);
        progressbar_tap.setProgress(MEASURE_TIME * 1000);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        deviceWidth = display.getWidth();
        deviceHeight = display.getHeight();
        param_btn_touch.height = param_btn_touch.width = param_progressbar_tap.height = param_progressbar_tap.width = (int) (deviceWidth * 0.8);


        //탭 측정 시작
        btn_touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStart) {
                    start();
                }
                if (!isFinish) {
                    vibrator.vibrate(50);
                    CircleClickAnimation(100);

                    count++;
                    tv_start.setText(getString(R.string.msg_touch) + Integer.toString(count));
                }
            }
        });


        //탭 다시 시작
        btn_tap_restart.setVisibility(View.INVISIBLE);
        btn_tap_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset(timerThread);
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timerThread != null) {
            timerThread.timerStop();
            reset(timerThread);
        }
    }

    void start() {
        btn_tap_restart.setVisibility(View.VISIBLE);
        radio_tap_measure_time.setVisibility(View.INVISIBLE);

        progressbar_tap.setMax(MEASURE_TIME * 1000);
        progressbar_tap.setProgress(MEASURE_TIME * 1000);

        timerThread = new TimerThread();
        timerThread.start();
        isStart = true;

        for (int i = 0; i < radio_measure_type.getChildCount(); i++) {
            radio_measure_type.getChildAt(i).setClickable(false);
        }
    }

    void reset(TimerThread timerThread) {
        timerThread.timerStop();
        count = 0;
        isStart = false;
        isFinish = false;
        tv_start.setText(getString(R.string.obj_tv_start));
        progressbar_tap.setProgress(progressbar_tap.getMax());

        btn_tap_restart.setVisibility(View.INVISIBLE);
        radio_tap_measure_time.setVisibility(View.VISIBLE);

        for (int i = 0; i < radio_measure_type.getChildCount(); i++) {
            radio_measure_type.getChildAt(i).setClickable(true);
        }
    }

    public void CircleClickAnimation(int speed) {
        Animation anim_highLight = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.anim_measure_touch);
        anim_highLight.setDuration(speed); //애니메이션 속도, 1000이 1초
        progressbar_tap.startAnimation(anim_highLight);
    }

    class TimerThread extends Thread {
        boolean stop = false;

        public void run() {
            long startTime = System.currentTimeMillis();
            long nowTime = System.currentTimeMillis();
            Message message;
            Bundle bundle;
            while (nowTime - startTime < MEASURE_TIME * 1000) {
                nowTime = System.currentTimeMillis();

                message = timerHandler.obtainMessage();
                bundle = new Bundle();
                bundle.putLong(INTENT_EXTRA_TIME, nowTime - startTime);
                message.setData(bundle);
                timerHandler.sendMessage(message);

                if (stop) {
                    break;
                }
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                }
            }

            message = timerHandler.obtainMessage();
            bundle = new Bundle();

            if (stop) { //'다시 시작하기' 버튼을 통한 종료, -2
                bundle.putLong(INTENT_EXTRA_TIME, -2);
            } else { //시간이 다 되어서 종료, -1
                bundle.putLong(INTENT_EXTRA_TIME, -1);
            }

            message.setData(bundle);
            timerHandler.sendMessage(message);
        }

        public void timerStop() {
            stop = true;
        }
    }

    class TimerHandler extends Handler {

        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();

            // 프로그래스바의 max 값은 총 시간 (MEASURE_TIME * 1000) 임

            // passedTime = 흐른 시간
            // barValue = 총 시간(프로그래스바의 max값) - 흐른 시간
            long passedTime = bundle.getLong(INTENT_EXTRA_TIME);
            long barValue = (MEASURE_TIME * 1000) - passedTime;

            // passedTime(흐른시간)은 끝났을때 음수 전달받음
            // -1 : 시간이 흘러 종료, -2 : '다시 시작하기' 버튼을 눌러 종료
            if (passedTime == -1) {
                progressbar_tap.setProgress(0);

                timeEnd();
            } else if (passedTime == -2) {
                progressbar_tap.setProgress(MEASURE_TIME * 1000);
            } else {
                progressbar_tap.setProgress((int) barValue);
            }

        }
    }


    void timeEnd() {
        isFinish = true; // 더 이상 버튼을 눌러도 카운트가 되지 않게 하기 위한 bool 변수
        //btn_tap_save.setVisibility(View.VISIBLE);

        BottomSheetDialog dialog_save = new BottomSheetDialog(getActivity());
        dialog_save.setContentView(R.layout.fragment_measure_tap_save_popup);
        dialog_save.setCanceledOnTouchOutside(false);
        dialog_save.show();


        Button btn_tap_save_cancel = dialog_save.findViewById(R.id.btn_tap_save_cancel);
        Button btn_tap_save_ok = dialog_save.findViewById(R.id.btn_tap_save_ok);
        TextView tv_tap_save_content = dialog_save.findViewById(R.id.tv_tap_save_content);


        countPerMinute = (count * 60) / MEASURE_TIME;

        if (radio_measure_type.getCheckedRadioButtonId() == R.id.radio_measure_type_heart) {
            tv_tap_save_content.setText(getString(R.string.obj_auto_save_content_hr) + countPerMinute);
            tv_tap_save_content.setTextColor(getActivity().getColor(R.color.zentry_red));
        } else {
            tv_tap_save_content.setText(getString(R.string.obj_auto_save_content_rr) + countPerMinute);
            tv_tap_save_content.setTextColor(getActivity().getColor(R.color.zentry_blue));
        }


        btn_tap_save_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_save.cancel();
                reset(timerThread);
            }
        });

        // 저장 버튼 클릭 시
        btn_tap_save_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_save.cancel();
                reset(timerThread);
            }
        });
    }


    private Date addHoursToDate(Date date, int addHours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, addHours);
        return calendar.getTime();
    }
}