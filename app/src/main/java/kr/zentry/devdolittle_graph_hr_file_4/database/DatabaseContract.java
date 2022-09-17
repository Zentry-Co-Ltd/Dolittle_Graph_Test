package kr.zentry.devdolittle_graph_hr_file_4.database;

import android.provider.BaseColumns;

public class DatabaseContract {

    public DatabaseContract() {
    }

    public static abstract class ThingyDbColumns implements BaseColumns {
        public static final String TABLE_NAME = "thingy";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_DEVICE_NAME = "name";
        public static final String COLUMN_LAST_SELECTED = "last_selected";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_MARKER_TITLE = "marker_title";
        public static final String COLUMN_MARKER_DESCRIPTION = "marker_description";
        public static final String COLUMN_NOTIFICATION_TEMPERATURE = "notification_temperature";
        public static final String COLUMN_NOTIFICATION_PRESSURE = "notification_pressure";
        public static final String COLUMN_NOTIFICATION_HUMIDITY = "notification_humidity";
        public static final String COLUMN_NOTIFICATION_AIR_QUALITY = "notification_air_quality";
        public static final String COLUMN_NOTIFICATION_COLOR = "notification_color";
        public static final String COLUMN_NOTIFICATION_BUTTON = "notification_button";
        public static final String COLUMN_NOTIFICATION_EULER = "notification_euler";
        public static final String COLUMN_NOTIFICATION_TAP = "notification_tap";
        public static final String COLUMN_NOTIFICATION_HEADING = "notification_heading";
        public static final String COLUMN_NOTIFICATION_GRAVITY_VECTOR = "notification_gravity";
        public static final String COLUMN_NOTIFICATION_ORIENTATION = "notification_orientation";
        public static final String COLUMN_NOTIFICATION_QUATERNION = "notification_quaternion";
        public static final String COLUMN_NOTIFICATION_PEDOMETER = "notification_pedometer";
        public static final String COLUMN_NOTIFICATION_RAW_DATA = "notification_raw_data";
    }

    public static abstract class CloudDbColumns implements BaseColumns {
        public static final String TABLE_NAME = "CLOUD";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_TEMPERATURE_UPLOAD = "temperature_upload";
        public static final String COLUMN_PRESSURE_UPLOAD = "pressure_upload";
        public static final String COLUMN_BUTTON_STATE_UPLOAD = "button_state_upload";
    }
}
