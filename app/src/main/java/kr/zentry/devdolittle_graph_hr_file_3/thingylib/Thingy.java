package kr.zentry.devdolittle_graph_hr_file_3.thingylib;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class Thingy implements Parcelable {

    private String deviceAddress;
    private String deviceId;
    private String deviceName;

    public Thingy(BluetoothDevice device){
        this.deviceAddress = device.getAddress();
        this.deviceName = device.getName();
    }

    public Thingy(final String address, final String name){
        this.deviceAddress = address;
        this.deviceName = name;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceAddress);
        dest.writeString(deviceId);
        dest.writeString(deviceName);
    }

    protected Thingy(Parcel in) {
        deviceAddress = in.readString();
        deviceId = in.readString();
        deviceName = in.readString();
    }

    public static final Creator<Thingy> CREATOR = new Creator<Thingy>() {
        @Override
        public Thingy createFromParcel(Parcel in) {
            return new Thingy(in);
        }

        @Override
        public Thingy[] newArray(int size) {
            return new Thingy[size];
        }
    };
}
