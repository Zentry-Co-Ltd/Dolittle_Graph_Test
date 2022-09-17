package kr.zentry.devdolittle_graph_hr_file_4;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ConnectingPopupDialog extends Dialog {
    TextView tv_connecting_device_name;

    public ConnectingPopupDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_connecting_popup);

        tv_connecting_device_name = findViewById(R.id.tv_connecting_device_name);

        getWindow().getAttributes().windowAnimations = R.style.ConnectingPopupSlideAnimation;
    }

    public void setDeviceName(String strDeviceName) {
        tv_connecting_device_name.setText(strDeviceName + " 연결중...");
    }
}
