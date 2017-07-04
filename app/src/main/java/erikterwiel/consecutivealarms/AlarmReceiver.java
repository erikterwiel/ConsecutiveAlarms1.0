package erikterwiel.consecutivealarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Erik on 3/8/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("zxcvzxcv", "Alarm set");
        Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
        String alarmTone = intent.getStringExtra("alarmTone");
        int alarmNumber = intent.getIntExtra("alarmNumber", 0);
        int alarmID = intent.getIntExtra("alarmID", -1);
        int startCode = intent.getIntExtra("startCode", 1);
        boolean alarmVibrate = intent.getBooleanExtra("alarmVibrate", false);
        serviceIntent.putExtra("alarmTone", alarmTone);
        serviceIntent.putExtra("alarmNumber", alarmNumber);
        serviceIntent.putExtra("alarmID", alarmID);
        serviceIntent.putExtra("alarmVibrate", alarmVibrate);
        serviceIntent.putExtra("startCode", startCode);
        context.startService(serviceIntent);
    }
}
