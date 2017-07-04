package erikterwiel.consecutivealarms;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erik on 3/5/2017.
 */

public class ConsecAlarmLab {
    private static ConsecAlarmLab sConsecAlarmLab;
    private List<ConsecAlarm> mConsecAlarms;

    private ConsecAlarmLab(Context context) {
        mConsecAlarms = new ArrayList<ConsecAlarm>();
        for (int i = 0; i < 8; i++) {
            ConsecAlarm consecAlarm = new ConsecAlarm();
            consecAlarm.setFromHour(i + 4);
            consecAlarm.setFromMinute(15);
            consecAlarm.setFromAM(true);
            consecAlarm.setToHour(i + 4);
            consecAlarm.setToMinute(30);
            consecAlarm.setToAM(true);
            consecAlarm.setNumAlarms(8);
            mConsecAlarms.add(consecAlarm);
        }
    }

    public static ConsecAlarmLab get(Context context) {
        if (sConsecAlarmLab == null) {
            sConsecAlarmLab = new ConsecAlarmLab(context);
        }
        return sConsecAlarmLab;
    }

    public List<ConsecAlarm> getConsecAlarms() {
        return mConsecAlarms;
    }
}
