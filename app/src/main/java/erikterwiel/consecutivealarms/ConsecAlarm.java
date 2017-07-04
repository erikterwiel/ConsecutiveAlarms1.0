package erikterwiel.consecutivealarms;

import android.app.PendingIntent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Parcelable;
import android.provider.Settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Erik on 3/5/2017.
 */

public class ConsecAlarm implements Serializable {
    private int mFromHour;
    private int mFromMinute;
    private boolean mFromAM;
    private int mToHour;
    private int mToMinute;
    private boolean mToAM;
    private int mNumAlarms;
    private int mInterval;
    private boolean mOn;
    private int mID;
    private boolean mSunday;
    private boolean mMonday;
    private boolean mTuesday;
    private boolean mWednesday;
    private boolean mThursday;
    private boolean mFriday;
    private boolean mSaturday;
    private String mLabel;
    private List<Integer> alarmHours;
    private List<Integer> alarmMins;
    private List<Integer> alarmIDs;
    private List<Integer> alarmIDsToRemove;
    private List<String> alarmUri;
    private List<String> alarmName;
    private List<Boolean> alarmVibrate;

    public ConsecAlarm() {
        mFromHour = 7;
        mFromMinute = 20;
        mFromAM = true;
        mToHour = 7;
        mToMinute = 30;
        mToAM = true;
        mNumAlarms = 3;
        mInterval = 5;
        mOn = false;
        mID = ConsecAlarm.getID();
        mSunday = false;
        mMonday = false;
        mTuesday = false;
        mWednesday = false;
        mThursday = false;
        mFriday = false;
        mSaturday = false;
        alarmHours = new ArrayList<Integer>();
        alarmMins = new ArrayList<Integer>();
        alarmIDs = new ArrayList<Integer>();
        alarmIDsToRemove = new ArrayList<Integer>();
        alarmUri = new ArrayList<String>();
        alarmName = new ArrayList<String>();
        alarmVibrate = new ArrayList<Boolean>();
    }

    public int getFromHour() {
        return mFromHour;
    }

    public void setFromHour(int fromHour) {
        mFromHour = fromHour;
    }

    public int getFromMinute() {
        return mFromMinute;
    }

    public void setFromMinute(int fromMinute) {
        mFromMinute = fromMinute;
    }

    public boolean isFromAM() {
        return mFromAM;
    }

    public void setFromAM(boolean fromAM) {
        mFromAM = fromAM;
    }

    public int getToHour() {
        return mToHour;
    }

    public void setToHour(int toHour) {
        mToHour = toHour;
    }

    public int getToMinute() {
        return mToMinute;
    }

    public void setToMinute(int toMinute) {
        mToMinute = toMinute;
    }

    public boolean isToAM() {
        return mToAM;
    }

    public void setToAM(boolean toAM) {
        mToAM = toAM;
    }

    public int getNumAlarms() {
        return mNumAlarms;
    }

    public void setNumAlarms(int numAlarms) {
        mNumAlarms = numAlarms;
    }

    public int getInterval() {
        return mInterval;
    }

    public void setInterval(int interval) {
        mInterval = interval;
    }

    public boolean isOn() {
        return mOn;
    }

    public void setOn(boolean on) {
        mOn = on;
    }

    public int getConsecAlarmID() {
        return mID;
    }

    public void setConsecAlarmID(int toAdd) {
        mID = toAdd;
    }

    public boolean isSunday() {
        return mSunday;
    }

    public void setSunday(boolean sunday) {
        mSunday = sunday;
    }

    public boolean isMonday() {
        return mMonday;
    }

    public void setMonday(boolean monday) {
        mMonday = monday;
    }

    public boolean isTuesday() {
        return mTuesday;
    }

    public void setTuesday(boolean tuesday) {
        mTuesday = tuesday;
    }

    public boolean isWednesday() {
        return mWednesday;
    }

    public void setWednesday(boolean wednesday) {
        mWednesday = wednesday;
    }

    public boolean isThursday() {
        return mThursday;
    }

    public void setThursday(boolean thursday) {
        mThursday = thursday;
    }

    public boolean isFriday() {
        return mFriday;
    }

    public void setFriday(boolean friday) {
        mFriday = friday;
    }

    public boolean isSaturday() {
        return mSaturday;
    }

    public void setSaturday(boolean saturday) {
        mSaturday = saturday;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public void addAlarmHour(int toAdd) {
        alarmHours.add(toAdd);
    }

    public int getAlarmHour(int index) {
        return alarmHours.get(index);
    }

    public void clearAlarmHour() {
        alarmHours = new ArrayList<Integer>();
    }

    public void addAlarmMin(int toAdd) {
        alarmMins.add(toAdd);
    }

    public int getAlarmMin(int index) {
        return alarmMins.get(index);
    }

    public void clearAlarmMin() {
        alarmMins = new ArrayList<Integer>();
    }

    public void addAlarmID(int toAdd) {
        alarmIDs.add(toAdd);
    }

    public int getAlarmID(int index) {
        return alarmIDs.get(index);
    }

    public void clearAlarmIDs() {
        alarmIDs = new ArrayList<Integer>();
    }

    public void replaceAlarmID(int index, int ID) {
        alarmIDs.set(index, getID());
    }

    public int getSizeAlarmID() {
        return alarmIDs.size();
    }

    public void addIDToRemove(int toAdd) {
        alarmIDsToRemove.add(toAdd);
    }

    public int getIDToRemove(int index) {
        return alarmIDsToRemove.get(index);
    }

    public void clearIDsToRemove() {
        alarmIDsToRemove = new ArrayList<Integer>();
    }

    public void copyIDs() {
        alarmIDsToRemove = alarmIDs;
    }

    public int getSizeIDToRemove() {
        return alarmIDsToRemove.size();
    }

    public void addAlarmUri(String toAdd) {
        alarmUri.add(toAdd);
    }

    public String getAlarmUri(int index) {
        return alarmUri.get(index);
    }

    public void replaceAlarmUri(int index, String toReplace) {
        alarmUri.set(index, toReplace);
    }

    public void addAlarmName(String toAdd) {
        alarmName.add(toAdd);
    }

    public String getAlarmName(int index) {
        return alarmName.get(index);
    }

    public void replaceAlarmName(int index, String toReplace) {
        alarmName.set(index, toReplace);
    }

    public List<String> getAlarmNameList() {
        return alarmName;
    }

    public void addAlarmVibrate(Boolean toAdd) {
        alarmVibrate.add(toAdd);
    }

    public boolean getAlarmVibrate(int index) {
        return alarmVibrate.get(index);
    }

    public void replaceAlarmVibrate(int index, boolean toReplace) {
        alarmVibrate.set(index, toReplace);
    }

    public void resize(int size) {
        ArrayList<String> tempUriList = new ArrayList<String>();
        ArrayList<String> tempNameList = new ArrayList<String>();
        ArrayList<Boolean> tempVibrateList = new ArrayList<Boolean>();
        for (int i = 0; i < size; i++) {
            if (i < alarmUri.size()) {
                tempUriList.add(alarmUri.get(i));
                tempNameList.add(alarmName.get(i));
                tempVibrateList.add(alarmVibrate.get(i));
            } else {
                tempUriList.add(AlarmListFragment.getDefaultUri());
                tempNameList.add(AlarmListFragment.getDefaultName());
                tempVibrateList.add(false);
            }
        }
        alarmUri = tempUriList;
        alarmName = tempNameList;
        alarmVibrate = tempVibrateList;
    }

    public void reverse() {
        Collections.reverse(alarmHours);
        Collections.reverse(alarmMins);
    }

    public static int getID() {
        return (int) ((Math.random() * 1000) * (Math.random() * 1000));
    }
}

