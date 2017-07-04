package erikterwiel.consecutivealarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Erik on 3/5/2017.
 */

public class AlarmListFragment extends Fragment {
    public static Context baseContext;
    private static final String EXTRA_CONSECALARM_FROM = "erikterwiel.consecutivealarms.extra_consecalarm_from";
    private static final int REQUEST_NEW = 0;
    private static final int REQUEST_EDIT = 1;
    private RecyclerView mAlarmRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private ConsecAlarmAdapter mAdapter;
    private ArrayList<ConsecAlarm> mConsecAlarms = new ArrayList<ConsecAlarm>();
    private int mCurrentIndex = 0;
    private ArrayList<ConsecAlarm> alarmsToDelete = new ArrayList<ConsecAlarm>();
    private AlarmManager mAlarmManager;
    private boolean isRepeatingAlarm = false;
    private int repeatingAlarmDays = 0;
    private boolean openingOnAlarmCancelation = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("asdfasdf", "onCreateView() called");
        baseContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);
        mAlarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsecAlarm toSend = new ConsecAlarm();
                for (int i = 0; i < 3; i++) {
                    toSend.addAlarmUri(AlarmListFragment.getDefaultUri());
                    toSend.addAlarmName(AlarmListFragment.getDefaultName());
                    toSend.addAlarmVibrate(false);
                }
                Intent i = EditAlarmActivity.newIntent(getActivity(), toSend);
                startActivityForResult(i, REQUEST_NEW);
            }
        });
        mAlarmRecyclerView = (RecyclerView) view.findViewById(R.id.alarm_recycler_view);
        mAlarmRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("qwerqwer", "onCreate() called");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AlarmDatabase", Context.MODE_PRIVATE);
        boolean firstTime = sharedPreferences.getBoolean("firstTime", true);
        if (sharedPreferences.contains("arraySize")) {
            int toLoadSize = sharedPreferences.getInt("arraySize", 0);
            Log.e("qwerqwer", Integer.toString(toLoadSize) + " to be loaded");
//          Toast.makeText(getActivity(), Integer.toString(toLoadSize) + " to be loaded", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < toLoadSize; i++) {
                ConsecAlarm toAdd = new ConsecAlarm();
                Log.e("qwerwqer", "new ConsecAlarm() created");
                toAdd.setFromHour(sharedPreferences.getInt(i + "fromHour", 7));
                toAdd.setFromMinute(sharedPreferences.getInt(i + "fromMinute", 20));
                toAdd.setFromAM(sharedPreferences.getBoolean(i + "fromAM", true));
                toAdd.setToHour(sharedPreferences.getInt(i + "toHour", 7));
                toAdd.setToMinute(sharedPreferences.getInt(i + "toMinute", 30));
                toAdd.setToAM(sharedPreferences.getBoolean(i + "toAM", true));
                toAdd.setInterval(sharedPreferences.getInt(i + "interval", 5));
                toAdd.setOn(sharedPreferences.getBoolean(i + "on", false));
                toAdd.setConsecAlarmID(sharedPreferences.getInt(i + "ID", 0));
                toAdd.setSunday(sharedPreferences.getBoolean(i + "sunday", false));
                toAdd.setMonday(sharedPreferences.getBoolean(i + "monday", false));
                toAdd.setTuesday(sharedPreferences.getBoolean(i + "tuesday", false));
                toAdd.setWednesday(sharedPreferences.getBoolean(i + "wednesday", false));
                toAdd.setThursday(sharedPreferences.getBoolean(i + "thursday", false));
                toAdd.setFriday(sharedPreferences.getBoolean(i + "friday", false));
                toAdd.setSaturday(sharedPreferences.getBoolean(i + "saturday", false));
                toAdd.setLabel(sharedPreferences.getString(i + "label", null));
                int numAlarms = sharedPreferences.getInt(i + "numAlarms", 3);
                toAdd.setNumAlarms(numAlarms);
                for (int j = 0; j < numAlarms; j++) {
                    toAdd.addAlarmHour(sharedPreferences.getInt(i + "alarmHour" + j, 0));
                    toAdd.addAlarmMin(sharedPreferences.getInt(i + "alarmMin" + j, 0));
                    toAdd.addAlarmID(sharedPreferences.getInt(i + "alarmID" + j, 0));
                    toAdd.addAlarmUri(sharedPreferences.getString(i + "alarmUri" + j, "null"));
                    toAdd.addAlarmName(sharedPreferences.getString(i + "alarmName" + j, "null"));
                    toAdd.addAlarmVibrate(sharedPreferences.getBoolean(i + "alarmVibrate" + j, false));
                    Log.e("qwerqwer", toAdd.getAlarmHour(j) + ":" + toAdd.getAlarmMin(j) + " loaded");
                }
                mConsecAlarms.add(toAdd);
            }
        }
        int alarmToDelete = sharedPreferences.getInt("alarmIDToDelete", -1);
        if (alarmToDelete == 1000001) {
            openingOnAlarmCancelation = true;
        }
        if (alarmToDelete != -1 && alarmToDelete != 1000001) {
            int position = 0;
            for (int i = 0; i < mConsecAlarms.size(); i++) {
                if (alarmToDelete == mConsecAlarms.get(i).getConsecAlarmID()) {
                    position = i;
                    break;
                }
            }
            mConsecAlarms.get(position).setOn(false);
            Log.e("AlarmListFragment", "Alarm with ID " + alarmToDelete + " deleted");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("alarmIDToDelete", -1);
            editor.commit();
            if (mConsecAlarms.get(position).isSunday() || mConsecAlarms.get(position).isMonday()
                    || mConsecAlarms.get(position).isTuesday() || mConsecAlarms.get(position).isWednesday()
                    || mConsecAlarms.get(position).isThursday() || mConsecAlarms.get(position).isFriday()
                    || mConsecAlarms.get(position).isSaturday()) {
                Log.e("AlarmListFragment", "One of the days is set for repeat");
                Calendar calendar = Calendar.getInstance();
                repeatingAlarmDays = 0;
                boolean foundDay = false;
                while (!foundDay) {
                    repeatingAlarmDays += 1;

                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    if (day == Calendar.SUNDAY && mConsecAlarms.get(position).isSunday()) {
                        Log.e("AlarmListFragment", "Sunday is found to be repeating");
                        foundDay = true;
                    } else if (day == Calendar.MONDAY && mConsecAlarms.get(position).isMonday()) {
                        Log.e("AlarmListFragment", "Monday is found to be repeating");
                        foundDay = true;
                    } else if (day == Calendar.TUESDAY && mConsecAlarms.get(position).isTuesday()) {
                        Log.e("AlarmListFragment", "Tuesday is found to be repeating");
                        foundDay = true;
                    } else if (day == Calendar.WEDNESDAY && mConsecAlarms.get(position).isWednesday()) {
                        Log.e("AlarmListFragment", "Wednesday is found to be repeating");
                        foundDay = true;
                    } else if (day == Calendar.THURSDAY && mConsecAlarms.get(position).isThursday()) {
                        Log.e("AlarmListFragment", "Thursday is found to be repeating");
                        foundDay = true;
                    } else if (day == Calendar.FRIDAY && mConsecAlarms.get(position).isFriday()) {
                        Log.e("AlarmListFragment", "Friday is found to be repeating");
                        foundDay = true;
                    } else if (day == Calendar.SATURDAY && mConsecAlarms.get(position).isSaturday()) {
                        Log.e("AlarmListFragment", "Saturday is found to be repeating");
                        foundDay = true;
                    }
                }
                isRepeatingAlarm = true;
                mConsecAlarms.get(position).setOn(true);
            }
        }
        if (firstTime) {
            Intent helpIntent = new Intent(getActivity(), HelpActivity.class);
            startActivity(helpIntent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("asdfasdf", "onStop() called");
        Collections.sort(mConsecAlarms, new Comparator<ConsecAlarm>() {
            @Override
            public int compare(ConsecAlarm o1, ConsecAlarm o2) {
                return o1.getToHour() - o2.getToHour();
            }
        });
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AlarmDatabase", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("firstTime", false);
        int toSaveSize = mConsecAlarms.size();
//      Toast.makeText(getActivity(), Integer.toString(toSaveSize) + " to be saved", Toast.LENGTH_LONG).show();
        editor.putInt("arraySize", toSaveSize);
        for (int i = 0; i < toSaveSize; i++) {
            editor.putInt(i + "fromHour", mConsecAlarms.get(i).getFromHour());
            editor.putInt(i + "fromMinute", mConsecAlarms.get(i).getFromMinute());
            editor.putBoolean(i + "fromAM", mConsecAlarms.get(i).isFromAM());
            editor.putInt(i + "toHour", mConsecAlarms.get(i).getToHour());
            editor.putInt(i + "toMinute", mConsecAlarms.get(i).getToMinute());
            editor.putBoolean(i + "toAM", mConsecAlarms.get(i).isToAM());
            editor.putInt(i + "interval", mConsecAlarms.get(i).getInterval());
            editor.putBoolean(i + "on", mConsecAlarms.get(i).isOn());
            editor.putInt(i + "ID", mConsecAlarms.get(i).getConsecAlarmID());
            editor.putBoolean(i + "sunday", mConsecAlarms.get(i).isSunday());
            editor.putBoolean(i + "monday", mConsecAlarms.get(i).isMonday());
            editor.putBoolean(i + "tuesday", mConsecAlarms.get(i).isTuesday());
            editor.putBoolean(i + "wednesday", mConsecAlarms.get(i).isWednesday());
            editor.putBoolean(i + "thursday", mConsecAlarms.get(i).isThursday());
            editor.putBoolean(i + "friday", mConsecAlarms.get(i).isFriday());
            editor.putBoolean(i + "saturday", mConsecAlarms.get(i).isSaturday());
            editor.putString(i + "label", mConsecAlarms.get(i).getLabel());
            int numAlarms = mConsecAlarms.get(i).getNumAlarms();
            editor.putInt(i + "numAlarms", numAlarms);
            for (int j = 0; j < numAlarms; j++) {
                editor.putInt(i + "alarmHour" + j, mConsecAlarms.get(i).getAlarmHour(j));
                editor.putInt(i + "alarmMin" + j, mConsecAlarms.get(i).getAlarmMin(j));
                editor.putInt(i + "alarmID" + j, mConsecAlarms.get(i).getAlarmID(j));
                editor.putString(i + "alarmUri" + j, mConsecAlarms.get(i).getAlarmUri(j));
                editor.putString(i + "alarmName" + j, mConsecAlarms.get(i).getAlarmName(j));
                editor.putBoolean(i + "alarmVibrate" + j, mConsecAlarms.get(i).getAlarmVibrate(j));
                Log.e("asdfasdf", mConsecAlarms.get(i).getAlarmHour(j) + ":" + mConsecAlarms.get(i).getAlarmMin(j) + " saved");
            }
        }
        editor.commit();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NEW && resultCode == RESULT_OK) {
            mConsecAlarms.add((ConsecAlarm) data.getSerializableExtra(EXTRA_CONSECALARM_FROM));
            updateUI();
        } else if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK){
            mConsecAlarms.remove(mCurrentIndex);
            mConsecAlarms.add((ConsecAlarm) data.getSerializableExtra(EXTRA_CONSECALARM_FROM));
            updateUI();
        }
    }

    private void updateUI() {
        Collections.sort(mConsecAlarms, new Comparator<ConsecAlarm>() {
            @Override
            public int compare(ConsecAlarm o1, ConsecAlarm o2) {
                return o1.getToHour() - o2.getToHour();
            }
        });
        mAdapter = new ConsecAlarmAdapter(mConsecAlarms);
        mAlarmRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallBack());
        itemTouchHelper.attachToRecyclerView(mAlarmRecyclerView);
    }

    public ItemTouchHelper.Callback createHelperCallBack() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
//              Snackbar.make(mAlarmRecyclerView, "Alarm deleted", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//              deleteItem(viewHolder.getAdapterPosition());
                onItemRemove(viewHolder, mAlarmRecyclerView);
            }
        };
        return simpleItemTouchCallback;
    }


    private void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView) {
        final int adapterPosition = viewHolder.getAdapterPosition();
        ConsecAlarm editConsecAlarm = mConsecAlarms.get(adapterPosition);
        if (editConsecAlarm.isOn()) {
            Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
            int alarmsToDelete = editConsecAlarm.getNumAlarms();
            for (int i = 0; i < alarmsToDelete; i++) {
                mAlarmManager.cancel(PendingIntent.getBroadcast(getActivity(),
                        editConsecAlarm.getAlarmID(i), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            }
        }
        final ConsecAlarm consecAlarm = editConsecAlarm;
        Snackbar snackbar = Snackbar.make(recyclerView, "Alarm deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(recyclerView, "Alarm restored", Snackbar.LENGTH_LONG).show();
                int mAdapterPosition = adapterPosition;
                mConsecAlarms.add(mAdapterPosition, consecAlarm);
                mAdapter.notifyItemInserted(mAdapterPosition);
                recyclerView.scrollToPosition(mAdapterPosition);
                alarmsToDelete.remove(consecAlarm);
                ConsecAlarm mConsecAlarm = mConsecAlarms.get(mAdapterPosition);
                Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
                if (mConsecAlarm.isOn()) {
                    int alarmsToRemove = mConsecAlarm.getSizeIDToRemove();
                    for (int i = 0; i < alarmsToRemove; i++) {
                        PendingIntent.getBroadcast(getActivity(),
                                mConsecAlarm.getIDToRemove(i), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    int alarmsToSet = mConsecAlarm.getNumAlarms();
                    int lastRingHour = 0;
                    int lastRingMin = 0;
                    for (int i = 0; i < alarmsToSet; i++) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        calendar.set(Calendar.HOUR_OF_DAY, mConsecAlarm.getAlarmHour(i));
                        calendar.set(Calendar.MINUTE, mConsecAlarm.getAlarmMin(i));
                        long toTestMillis = (calendar.getTimeInMillis() - System.currentTimeMillis());
                        if (toTestMillis >  86400000) {
                            toTestMillis -= 86400000;
                        }
                        double toTestMillisDouble = (double) toTestMillis;
                        long toSetMillis = System.currentTimeMillis() + toTestMillis;
                        Intent appIntent = new Intent(getActivity(), AlarmListActivity.class);
                        PendingIntent appPendingIntent = PendingIntent.getActivity(getActivity(), 0, appIntent, 0);
                        AlarmManager.AlarmClockInfo alarmInfo = new AlarmManager.AlarmClockInfo(toSetMillis, appPendingIntent);

                        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(getActivity(),
                                mConsecAlarm.getAlarmID(i), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mAlarmManager.setAlarmClock(alarmInfo, alarmPendingIntent);
                        if (i == 0) {
                            lastRingHour = (int) toTestMillis / 1000 / 60 / 60;
                            double lastRingHourUncut = toTestMillisDouble / 1000 / 60 / 60;
                            lastRingMin = (int) ((lastRingHourUncut - ((float) lastRingHour)) * 60);
                        }
                    }
                    if (lastRingHour == 0) {
                        Snackbar.make(mAlarmRecyclerView, "Last ring set for " + lastRingMin + " minutes from now.", Snackbar.LENGTH_LONG).show();
                    } else {
                        if (lastRingMin == 1) {
                            Snackbar.make(mAlarmRecyclerView, "Last ring set for " + lastRingHour + " hours and " + lastRingMin + " minute from now.", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(mAlarmRecyclerView, "Last ring set for " + lastRingHour + " hours and " + lastRingMin + " minutes from now.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        snackbar.show();
        mConsecAlarms.remove(adapterPosition);
        mAdapter.notifyItemRemoved(adapterPosition);
        alarmsToDelete.add(consecAlarm);
    }

    private void updateIsOn(int position, boolean on) {
        Log.e("AlarmListFragment", "Alarm set on = " + on);
        mConsecAlarms.get(position).setOn(on);
    }

    private class ConsecAlarmAdapter extends RecyclerView.Adapter<ConsecAlarmHolder> {
        private List<ConsecAlarm> mConsecAlarms;

        public ConsecAlarmAdapter(List<ConsecAlarm> consecAlarms) {
            mConsecAlarms = consecAlarms;
        }

        @Override
        public ConsecAlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_alarm, parent, false);
            return new ConsecAlarmHolder(view);
        }

        @Override
        public void onBindViewHolder(ConsecAlarmHolder holder, int position) {
            ConsecAlarm consecAlarm = mConsecAlarms.get(position);
            holder.bindConsecAlarm(consecAlarm);
        }

        @Override
        public int getItemCount() {
            return mConsecAlarms.size();
        }

        public void dismiss(int position) {
            mConsecAlarms.remove(position);
        }
    }

    private class ConsecAlarmHolder extends RecyclerView.ViewHolder {
        private ConsecAlarm mConsecAlarm;
        private TextView mFromTime;
        private TextView mFromAM;
        private TextView mToTime;
        private TextView mToAM;
        private TextView mNumAlarms;
        private ImageButton mSettings;
        private Switch mOn;
        private TextView mLabel;

        public ConsecAlarmHolder(View itemView) {
            super(itemView);
            mFromTime = (TextView) itemView.findViewById(R.id.list_item_from_time);
            mFromAM = (TextView) itemView.findViewById(R.id.list_item_from_am);
            mToTime = (TextView) itemView.findViewById(R.id.list_item_to_time);
            mToAM = (TextView) itemView.findViewById(R.id.list_item_to_am);
            mNumAlarms = (TextView) itemView.findViewById(R.id.list_item_alarm_count);
            mSettings = (ImageButton) itemView.findViewById(R.id.list_item_settings);
            mOn = (Switch) itemView.findViewById(R.id.list_item_switch);
            mLabel = (TextView) itemView.findViewById(R.id.list_item_label);
        }

        public void bindConsecAlarm(ConsecAlarm consecAlarm) {
            mConsecAlarm = consecAlarm;
            if (mConsecAlarm.getFromMinute() > 9) {
                mFromTime.setText(Integer.toString(mConsecAlarm.getFromHour()) + ":" + Integer.toString(mConsecAlarm.getFromMinute()));
            } else {
                mFromTime.setText(Integer.toString(mConsecAlarm.getFromHour()) + ":0" + Integer.toString(mConsecAlarm.getFromMinute()));
            }
            if (mConsecAlarm.isFromAM()) {
                mFromAM.setText(R.string.time_am);
            } else {
                mFromAM.setText(R.string.time_pm);
            }
            if (mConsecAlarm.getToMinute() > 9) {
                mToTime.setText(Integer.toString(mConsecAlarm.getToHour()) + ":" + Integer.toString(mConsecAlarm.getToMinute()));
            } else {
                mToTime.setText(Integer.toString(mConsecAlarm.getToHour()) + ":0" + Integer.toString(mConsecAlarm.getToMinute()));
            }
            if (mConsecAlarm.isToAM()) {
                mToAM.setText(R.string.time_am);
            } else {
                mToAM.setText(R.string.time_pm);
            }
            mNumAlarms.setText(Integer.toString(mConsecAlarm.getNumAlarms()) + " Alarms");
            mSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConsecAlarm toSend = mConsecAlarm;
                    mCurrentIndex = mConsecAlarms.indexOf(toSend);
                    Intent i = EditAlarmActivity.newIntent(getActivity(), toSend);
                    startActivityForResult(i, REQUEST_EDIT);
                }
            });
            mOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
                    if (!openingOnAlarmCancelation) {
                        if (isChecked) {
                            updateIsOn(getLayoutPosition(), true);
                            int alarmsToRemove = mConsecAlarm.getSizeIDToRemove();
                            for (int i = 0; i < alarmsToRemove; i++) {
                                mAlarmManager.cancel(PendingIntent.getBroadcast(getActivity(),
                                        mConsecAlarm.getIDToRemove(i), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                                Log.e("Alarm ID deleted: ", Integer.toString(mConsecAlarm.getIDToRemove(i)));
                            }
                            int alarmsToSet = mConsecAlarm.getNumAlarms();
                            int lastRingHour = 0;
                            int lastRingMin = 0;
                            for (int i = 0; i < alarmsToSet; i++) {
                                Calendar calendar = Calendar.getInstance();
                                if (isRepeatingAlarm) {
                                    calendar.add(Calendar.DAY_OF_MONTH, repeatingAlarmDays);
                                    Log.e("AlarmListFragment", repeatingAlarmDays + " days from now an alarm is set");
                                } else {
                                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                                }
                                calendar.set(Calendar.HOUR_OF_DAY, mConsecAlarm.getAlarmHour(i));
                                calendar.set(Calendar.MINUTE, mConsecAlarm.getAlarmMin(i));
                                long toTestMillis = (calendar.getTimeInMillis() - System.currentTimeMillis());
                                Log.e("millisFromNowBefore", Long.toString(toTestMillis));
                                if (toTestMillis >=  86400000 && !isRepeatingAlarm) {
                                    toTestMillis -= 86400000;
                                }
                                toTestMillis -= calendar.get(Calendar.SECOND) * 1000;
                                Log.e("millisFromNowAfter", Long.toString(toTestMillis));
                                double toTestMillisDouble = (double) toTestMillis;
                                long toSetMillis = System.currentTimeMillis() + toTestMillis;
                                Intent appIntent = new Intent(getActivity(), AlarmListActivity.class);
                                PendingIntent appPendingIntent = PendingIntent.getActivity(getActivity(), 0, appIntent, 0);
                                AlarmManager.AlarmClockInfo alarmInfo = new AlarmManager.AlarmClockInfo(toSetMillis, appPendingIntent);

                                Intent extraIntent = new Intent(getActivity(), AlarmReceiver.class);
                                extraIntent.putExtra("alarmTone", mConsecAlarm.getAlarmUri(i));
                                extraIntent.putExtra("alarmVibrate", mConsecAlarm.getAlarmVibrate(i));
                                if (i == alarmsToSet - 1) {
                                    extraIntent.putExtra("alarmNumber", 1);
                                    extraIntent.putExtra("alarmID", mConsecAlarm.getConsecAlarmID());
                                } else {
                                    extraIntent.putExtra("alarmNumber", 0);
                                }
                                PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(getActivity(),
                                        mConsecAlarm.getAlarmID(i), extraIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                mAlarmManager.setAlarmClock(alarmInfo, alarmPendingIntent);
                                Log.e("Alarm set for ", mConsecAlarm.getAlarmHour(i) + ":" + mConsecAlarm.getAlarmMin(i));
                                if (i == 0) {
                                    lastRingHour = (int) toTestMillis / 1000 / 60 / 60;
                                    double lastRingHourUncut = toTestMillisDouble / 1000 / 60 / 60;
                                    lastRingMin = (int) ((lastRingHourUncut - ((float) lastRingHour)) * 60);
                                }
                            }
                            if (lastRingHour == 0) {
                                Snackbar.make(mAlarmRecyclerView, "Last ring set for " + lastRingMin + " minutes from now.", Snackbar.LENGTH_LONG).show();
                            } else {
                                if (lastRingMin == 1) {
                                    Snackbar.make(mAlarmRecyclerView, "Last ring set for " + lastRingHour + " hours and " + lastRingMin + " minute from now.", Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(mAlarmRecyclerView, "Last ring set for " + lastRingHour + " hours and " + lastRingMin + " minutes from now.", Snackbar.LENGTH_LONG).show();
                                }
                            }
                            if (isRepeatingAlarm) {
                                isRepeatingAlarm = false;
                            }
                        } else {
                            updateIsOn(getLayoutPosition(), false);
                            int alarmsToDelete = mConsecAlarm.getNumAlarms();
                            for (int i = 0; i < alarmsToDelete; i++) {
                                mAlarmManager.cancel(PendingIntent.getBroadcast(getActivity(),
                                        mConsecAlarm.getAlarmID(i), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                            }
                        }
                    } else {
                        openingOnAlarmCancelation = false;
                    }

                }
            });
            mOn.setChecked(mConsecAlarm.isOn());
            if (Build.VERSION.SDK_INT < 23) {
                mLabel.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Body2);
            } else {
                mLabel.setTextAppearance(R.style.TextAppearance_AppCompat_Body2);
            }
            mLabel.setText(mConsecAlarm.getLabel());
        }
    }

    public static String getDefaultUri() {
        return Settings.System.DEFAULT_ALARM_ALERT_URI.toString();
    }

    public static String getDefaultName() {
        Ringtone ringtone = RingtoneManager.getRingtone(baseContext, Settings.System.DEFAULT_ALARM_ALERT_URI);
        return ringtone.getTitle(baseContext);
    }
}

