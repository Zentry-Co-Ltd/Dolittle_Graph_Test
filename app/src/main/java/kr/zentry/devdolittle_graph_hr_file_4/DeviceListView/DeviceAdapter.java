package kr.zentry.devdolittle_graph_hr_file_4.DeviceListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.zentry.devdolittle_graph_hr_file_4.BluetoothScanner.ScanResult;
import kr.zentry.devdolittle_graph_hr_file_4.DeviceConnectingPopup;
import kr.zentry.devdolittle_graph_hr_file_4.R;

public class DeviceAdapter extends BaseAdapter {
    TextView tv_device_name;
    private List<BluetoothDeviceModel> mDevices = new ArrayList<>();

    public void update(final List<ScanResult> results) {
        for (final ScanResult result : results) {
            final BluetoothDeviceModel device = findDevice(result);
            if (device == null) {
                mDevices.add(new BluetoothDeviceModel(result));
            } else {
                device.name = result.getScanRecord() != null ? result.getScanRecord().getDeviceName() : null;
                device.device = result.getDevice();
                device.rssi = result.getRssi();
                device.Adress = device.device != null ? device.device.getAddress() : null;
            }
        }
        notifyDataSetChanged();
    }

    private BluetoothDeviceModel findDevice(final ScanResult result) {
        for (final BluetoothDeviceModel device : mDevices)
            if (device.matches(result))
                return device;
        return null;
    }

    public void clearDevices() {
        mDevices.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_device, parent, false);
            final DeviceAdapter.ViewHolder holder = new DeviceAdapter.ViewHolder();
            holder.name = view.findViewById(R.id.tv_device_name);
            holder.address = view.findViewById(R.id.tv_device_address);
            holder.rssi_signal = view.findViewById(R.id.img_signal);
            view.setTag(holder);
        }
        final BluetoothDeviceModel device = (BluetoothDeviceModel) getItem(position);
        final DeviceAdapter.ViewHolder holder = (DeviceAdapter.ViewHolder) view.getTag();
        final String name = device.name;
        holder.name.setText(name != null ? name : (parent.getContext().getString(R.string.glb_not_available)));
        holder.address.setText(device.device.getAddress());
//        holder.rssi_signal.setImageResource(R.drawable.icon_signal);
        if (device.rssi != DeviceConnectingPopup.NO_RSSI) {
            final int rssiPercent = (int) (100.0f * (127.0f + device.rssi) / (127.0f + 20.0f));
            holder.rssi_signal.setImageLevel(rssiPercent);
            holder.rssi_signal.setVisibility(View.VISIBLE);
        } else {
            holder.rssi_signal.setVisibility(View.GONE);
        }
        return view;
    }

    private class ViewHolder {
        private TextView name;
        private TextView address;
        private ImageView rssi_signal;
    }
}
