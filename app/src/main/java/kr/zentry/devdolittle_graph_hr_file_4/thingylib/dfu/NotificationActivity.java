package kr.zentry.devdolittle_graph_hr_file_4.thingylib.dfu;

import android.app.Activity;
import android.os.Bundle;

public class NotificationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*// If this activity is the root activity of the task, the app is not running
        if (isTaskRoot()) {
            // Start the app before finishing
            final Intent parentIntent = new Intent(this, MainActivity.class);
            parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(parentIntent);
        }

        // Now finish, which will drop the user in to the activity that was at the top
        //  of the task stack
        finish();*/
    }
}