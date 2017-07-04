package erikterwiel.consecutivealarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Erik on 3/12/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("asdf", "NotificationReceiver has received intent");
        Intent stopServiceIntent = new Intent(context, RingtonePlayingService.class);
        int startCode = stopServiceIntent.getIntExtra("startCode", 1);
        context.stopService(stopServiceIntent);
        if (startCode == 0) {
            Log.e("NotificationReceiver", "Received request to end one alarm");
            Intent mainActivity = new Intent(context, AlarmListActivity.class);
            context.startActivity(mainActivity);
        }
        if (startCode == 1) {
            Log.e("NotificationReceiver", "Received request to stop");
            Intent mainActivity = new Intent(context, AlarmListActivity.class);
            context.startActivity(mainActivity);
        }
    }
}
