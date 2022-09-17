package kr.zentry.devdolittle_graph_hr_file_4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import kr.zentry.devdolittle_graph_hr_file_4.thingy.ThingyService;
import kr.zentry.devdolittle_graph_hr_file_4.thingylib.ThingySdkManager;

public class MeasureBaseActivity extends AppCompatActivity {
    private FrameLayout container_measure;
    private TextView tv_measure_title;
    private RadioGroup radio_measure;
    private RadioButton radio_measure_tap;
    private RadioButton radio_measure_dolittle;
    private RadioButton radio_measure_record;

    private ThingySdkManager mThingySdkManager = null;
    private ThingyService.ThingyBinder mBinder;
    private boolean isHeartMeasure; //true - 심박 측정, false - 호흡 측정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_base);

        tv_measure_title = findViewById(R.id.tv_measure_title);
        container_measure = findViewById(R.id.container_measure);
        radio_measure = findViewById(R.id.radio_measure);
        radio_measure_tap = findViewById(R.id.radio_measure_tap);
        radio_measure_dolittle = findViewById(R.id.radio_measure_dolittle);
        radio_measure_record = findViewById(R.id.radio_measure_record);

        Intent intent = getIntent();
        mThingySdkManager = ThingySdkManager.getInstance();

        if (intent.getStringExtra(getString(R.string.glb_intent_extra_measure_type)).equals(getString(R.string.glb_intent_extra_measure_heart))) {
            //심박 측정
            isHeartMeasure = true;
            tv_measure_title.setText(getString(R.string.obj_measure_title_heart));
        } else {
            //호흡 측정
            isHeartMeasure = false;
            tv_measure_title.setText(getString(R.string.obj_measure_title_res));
        }

        radio_measure.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_measure_tap:
                        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.container_measure, new MeasureTapFragment());
                        fragmentTransaction1.commit();
                        break;

                    case R.id.radio_measure_dolittle:
                        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.container_measure, new MeasureDolittleFragment());
                        fragmentTransaction2.commit();
                        break;

                }
            }
        });

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container_measure, new MeasureDolittleFragment());
        fragmentTransaction.commit();
    }

    void radioButtonEnable(boolean enabled) {
        if (enabled) {
            radio_measure_tap.setClickable(true);
            radio_measure_record.setClickable(true);
            radio_measure_dolittle.setClickable(true);
        } else {
            radio_measure_tap.setClickable(false);
            radio_measure_record.setClickable(false);
            radio_measure_dolittle.setClickable(false);
        }
    }

    boolean getIsHeartMeasure() {
        if(isHeartMeasure) {
            return true;
        } else {
            return false;
        }
    }
}