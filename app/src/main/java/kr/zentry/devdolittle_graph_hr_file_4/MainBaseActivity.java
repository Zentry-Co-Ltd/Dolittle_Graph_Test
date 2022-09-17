package kr.zentry.devdolittle_graph_hr_file_4;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.realm.Realm;
import kr.zentry.devdolittle_graph_hr_file_4.common.DolittleAPI;
import kr.zentry.devdolittle_graph_hr_file_4.common.Utils;
import kr.zentry.devdolittle_graph_hr_file_4.database.DatabaseHelper;
import kr.zentry.devdolittle_graph_hr_file_4.thingy.ThingyService;
import kr.zentry.devdolittle_graph_hr_file_4.thingylib.ThingySdkManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainBaseActivity extends AppCompatActivity implements ThingySdkManager.ServiceConnectionListener {
    public BottomNavigationView nav_bottom;
    private FrameLayout container_fragment;

    private Realm realm;
    private Retrofit retrofit;
    private DolittleAPI.DolittleAPIs router;
    private Gson gson = new Gson();

    private DatabaseHelper mDatabaseHelper;
    private ThingySdkManager mThingySdkManager = null;
    private ThingyService.ThingyBinder mBinder;
    private BluetoothDevice mDevice = null;

    private String accessToken;
    private String refreshToken;

    private long tabMoveLimitTime = 0;
    private long backKeyPressedTime = 0;
    private int prePage = 0;
    private int selPage = 0;
    private double currentLng = 0; //Long = X, Lat = Y
    private double currentLat = 0;

    private boolean isCreated = false;
    //측정(로우데이터) 값 전달 스레드 제어 플래그
    //1번 실행 2번 종료
    static int threadFlag_row = 0;


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            nav_bottom.getMenu().getItem(prePage).setChecked(true);
            nav_bottom.getMenu().getItem(selPage).setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mThingySdkManager = ThingySdkManager.getInstance();

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.glb_api_dolittle_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build();

        router = retrofit.create(DolittleAPI.DolittleAPIs.class);
        realm = Realm.getDefaultInstance();

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());


        container_fragment = findViewById(R.id.container_fragment);
        nav_bottom = findViewById(R.id.nav_bottom);


        nav_bottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                //로우 데이터 전달 스레드 종료
                threadFlag_row = 2;
                switch (item.getItemId()) {
                    case R.id.nav_goto_measure:
                        FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction3.replace(R.id.container_fragment, new MeasureBaseFragment());
                        fragmentTransaction3.addToBackStack(null);
                        fragmentTransaction3.commit();
                        return true;


                    default:
                        return false;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        accessToken = Utils.getAccessValue(getApplicationContext());
        refreshToken = Utils.getRefreshValue(getApplicationContext());

        FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction3.replace(R.id.container_fragment, new MeasureBaseFragment());
        fragmentTransaction3.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDevice != null) {
            if (mThingySdkManager.isConnected(mDevice)) {
                mThingySdkManager.disconnectFromThingy(mDevice);
                mDevice = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mOnKeyBackPressedListener != null) {
            mOnKeyBackPressedListener.onBackKey();
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
                backKeyPressedTime = System.currentTimeMillis();
                Utils.ShowToast(getApplicationContext(), "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", false);
                return;
            }

            if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
                finish();
            }
        }

    }


    public ThingySdkManager getThingySdkManager() {
        return mThingySdkManager;
    }

    public ThingyService.ThingyBinder getThingyBinder() {
        return mBinder;
    }

    public DatabaseHelper getDatabaseHelper() {
        return mDatabaseHelper;
    }

    public void setDevice(BluetoothDevice device) {
        mDevice = device;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public interface onKeyBackPressedListener {
        void onBackKey();
    }

    private onKeyBackPressedListener mOnKeyBackPressedListener;

    public void setOnKeyBackPressedListener(onKeyBackPressedListener listener) {
        mOnKeyBackPressedListener = listener;
    }


    public void replaceFragment(int containerViewId, Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, fragmentTag).commit();
    }

    public void removeFragment(String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
        fragmentTransaction.remove(fragment).commit();
    }

    @Override
    public void onServiceConnected() {
        mBinder = (ThingyService.ThingyBinder) mThingySdkManager.getThingyBinder();
    }
}