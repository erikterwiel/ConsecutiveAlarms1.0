package erikterwiel.consecutivealarms;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class EditAlarmActivity extends AppCompatActivity {
    private static final String EXTRA_CONSECALARM_TO = "erikterwiel.consecutivealarms.extra_consecalarm_to";
    private static final String EXTRA_CONSECALARM_FROM = "erikterwiel.consecutivealarms.extra_consecalarm_from";
    private static final int DIALOG_ID = 12345678;
    private ConsecAlarm mConsecAlarm;
    private TextView mFromTime;
    private TextView mFromAM;
    private TextView mToTime;
    private TextView mToAM;
    private NumberPicker mAlarmPicker;
    private NumberPicker mIntervalPicker;
    private MenuItem mDoneButton;
    private RecyclerView mAlarmsRecyclerView;
    private AlarmAdapter mAlarmAdapter;
    private int mPosition;
    private CheckBox mSunday;
    private CheckBox mMonday;
    private CheckBox mTuesday;
    private CheckBox mWednesday;
    private CheckBox mThursday;
    private CheckBox mFriday;
    private CheckBox mSaturday;
    private LinearLayout mLabelLayout;
    private TextView mLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        mConsecAlarm = (ConsecAlarm) getIntent().getSerializableExtra(EXTRA_CONSECALARM_TO);
        mFromTime = (TextView) findViewById(R.id.edit_from_time);
        mFromAM = (TextView) findViewById(R.id.edit_from_am);
        mToTime = (TextView) findViewById(R.id.edit_to_time);
        mToAM = (TextView) findViewById(R.id.edit_to_am);
        mAlarmPicker = (NumberPicker) findViewById(R.id.edit_alarm_picker);
        mIntervalPicker = (NumberPicker) findViewById(R.id.edit_interval_picker);
        mSunday = (CheckBox) findViewById(R.id.list_item_sunday);
        mMonday = (CheckBox) findViewById(R.id.list_item_monday);
        mTuesday = (CheckBox) findViewById(R.id.list_item_tuesday);
        mWednesday = (CheckBox) findViewById(R.id.list_item_wednesday);
        mThursday = (CheckBox) findViewById(R.id.list_item_thursday);
        mFriday = (CheckBox) findViewById(R.id.list_item_friday);
        mSaturday = (CheckBox) findViewById(R.id.list_item_saturday);
        mLabelLayout = (LinearLayout) findViewById(R.id.alarm_label_layout);
        mLabel = (TextView) findViewById(R.id.alarm_label);

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
        mToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
        mAlarmPicker.setMinValue(1);
        mAlarmPicker.setMaxValue(10);
        mAlarmPicker.setValue(mConsecAlarm.getNumAlarms());
        mAlarmPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker,int oldVal, int newVal) {
                mConsecAlarm.setNumAlarms(newVal);
                boolean isAM = true;
                int decreasingHour = mConsecAlarm.getToHour();
                if (!mConsecAlarm.isToAM()) {
                    decreasingHour += 12;
                }
                int decreasingMinute = mConsecAlarm.getToMinute();
                for (int i = 0; i < newVal - 1; i++) {
                    if (decreasingMinute - mIntervalPicker.getValue() < 0) {
                        decreasingHour -= 1;
                        decreasingMinute -= mIntervalPicker.getValue();
                        decreasingMinute += 60;
                    } else {
                        decreasingMinute -= mIntervalPicker.getValue();
                    }
                }
                if (decreasingHour > 12) {
                    decreasingHour -= 12;
                    isAM = false;
                }
                mConsecAlarm.setFromHour(decreasingHour);
                mConsecAlarm.setFromMinute(decreasingMinute);
                mConsecAlarm.setFromAM(isAM);
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
                mConsecAlarm.resize(newVal);
                updateUI();
            }
        });
        mIntervalPicker.setMinValue(1);
        mIntervalPicker.setMaxValue(60);
        mIntervalPicker.setValue(mConsecAlarm.getInterval());
        mIntervalPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker,int oldVal, int newVal) {
                mConsecAlarm.setInterval(newVal);
                boolean isAM = true;
                int decreasingHour = mConsecAlarm.getToHour();
                if (!mConsecAlarm.isToAM()) {
                    decreasingHour += 12;
                }
                int decreasingMinute = mConsecAlarm.getToMinute();
                for (int i = 0; i < mAlarmPicker.getValue() - 1; i++) {
                    if (decreasingMinute - mIntervalPicker.getValue() < 0) {
                        decreasingHour -= 1;
                        decreasingMinute -= mIntervalPicker.getValue();
                        decreasingMinute += 60;
                    } else {
                        decreasingMinute -= mIntervalPicker.getValue();
                    }
                }
                if (decreasingHour > 12) {
                    decreasingHour -= 12;
                    isAM = false;
                }
                mConsecAlarm.setFromHour(decreasingHour);
                mConsecAlarm.setFromMinute(decreasingMinute);
                mConsecAlarm.setFromAM(isAM);
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
            }
        });
        mAlarmsRecyclerView = (RecyclerView) findViewById(R.id.alarms_recycler_view);
        mAlarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConsecAlarm.setSunday(isChecked);
                Log.e("EditAlarmActivity", "Sunday checked");
            }
        });
        mMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConsecAlarm.setMonday(isChecked);
                Log.e("EditAlarmActivity", "Monday checked");
            }
        });
        mTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConsecAlarm.setTuesday(isChecked);
                Log.e("EditAlarmActivity", "Tuesday checked");
            }
        });
        mWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConsecAlarm.setWednesday(isChecked);
                Log.e("EditAlarmActivity", "Wednesday checked");
            }
        });
        mThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConsecAlarm.setThursday(isChecked);
                Log.e("EditAlarmActivity", "Thursday checked");
            }
        });
        mFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConsecAlarm.setFriday(isChecked);
                Log.e("EditAlarmActivity", "Friday checked");
            }
        });
        mSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConsecAlarm.setSaturday(isChecked);
                Log.e("EditAlarmActivity", "Saturday checked");
            }
        });
        if (mConsecAlarm.isSunday()) {
            mSunday.setChecked(true);
        }
        if (mConsecAlarm.isMonday()) {
            mMonday.setChecked(true);
        }
        if (mConsecAlarm.isTuesday()) {
            mTuesday.setChecked(true);
        }
        if (mConsecAlarm.isWednesday()) {
            mWednesday.setChecked(true);
        }
        if (mConsecAlarm.isThursday()) {
            mThursday.setChecked(true);
        }
        if (mConsecAlarm.isFriday()) {
            mFriday.setChecked(true);
        }
        if (mConsecAlarm.isSaturday()) {
            mSaturday.setChecked(true);
        }
        mLabelLayout.setOnClickListener(new View.OnClickListener() {
            AlertDialog mAlert;
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(EditAlarmActivity.this);
                input.setSingleLine();
                input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                input.setText(mConsecAlarm.getLabel());
                input.setSelection(input.getText().length());
                FrameLayout container = new FrameLayout(EditAlarmActivity.this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                input.setLayoutParams(params);
                container.addView(input);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditAlarmActivity.this)
                        .setTitle("Label")
                        .setView(container);
                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mConsecAlarm.setLabel(input.getText().toString());
                        mLabel.setText(input.getText());
                        mAlert.cancel();
                    }
                });
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlert.cancel();
                    }
                });
                mAlert = alertBuilder.create();
                mAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                mAlert.show();
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            mLabel.setTextAppearance(EditAlarmActivity.this, R.style.TextAppearance_AppCompat_Body2);
        } else {
            mLabel.setTextAppearance(R.style.TextAppearance_AppCompat_Body2);
        }
        mLabel.setText(mConsecAlarm.getLabel());
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.done_button_menu, menu);
        mDoneButton = menu.findItem(R.id.done_button);
        mDoneButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int numAlarms = mConsecAlarm.getNumAlarms();
                boolean isAM = true;
                int decreasingHour = mConsecAlarm.getToHour();
                if (!mConsecAlarm.isToAM()) {
                    decreasingHour += 12;
                }
                int decreasingMinute = mConsecAlarm.getToMinute();
                mConsecAlarm.clearIDsToRemove();
                mConsecAlarm.copyIDs();
                mConsecAlarm.clearAlarmHour();
                mConsecAlarm.clearAlarmMin();
                mConsecAlarm.clearAlarmIDs();
                mConsecAlarm.addAlarmHour(decreasingHour);
                mConsecAlarm.addAlarmMin(decreasingMinute);
                mConsecAlarm.addAlarmID(ConsecAlarm.getID());
//              Log.e("asdfasdf", mConsecAlarm.getAlarmHour(0) + ":" + mConsecAlarm.getAlarmMin(0) + " added");
                for (int i = 1; i < numAlarms; i++) {
                    if (decreasingMinute - mConsecAlarm.getInterval() < 0) {
                        decreasingHour -= 1;
                        decreasingMinute -= mConsecAlarm.getInterval();
                        decreasingMinute += 60;
                    } else {
                        decreasingMinute -= mConsecAlarm.getInterval();
                    }
                    mConsecAlarm.addAlarmHour(decreasingHour);
                    mConsecAlarm.addAlarmMin(decreasingMinute);
                    mConsecAlarm.addAlarmID(ConsecAlarm.getID());
//                  Log.e("asdfasdf", mConsecAlarm.getAlarmHour(i) + ":" + mConsecAlarm.getAlarmMin(i) + " added");
                }
                mConsecAlarm.reverse();
                mConsecAlarm.setOn(true);
                ConsecAlarm toSend = mConsecAlarm;
                Intent i = new Intent();
                i.putExtra(EXTRA_CONSECALARM_FROM, toSend);
                setResult(RESULT_OK, i);
                finish();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID) {
            final Calendar c = Calendar.getInstance();
            int hour = mConsecAlarm.getToHour();
            if (!mConsecAlarm.isToAM()) {
                hour += 12;
            }
            int minute = mConsecAlarm.getToMinute();
            return new TimePickerDialog(EditAlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    boolean isAM = true;
                    if (hourOfDay > 12) {
                        hourOfDay -= 12;
                        isAM = false;
                    }
                    mConsecAlarm.setToHour(hourOfDay);
                    mConsecAlarm.setToMinute(minute);
                    mConsecAlarm.setToAM(isAM);
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

                    isAM = true;
                    int decreasingHour = mConsecAlarm.getToHour();
                    if (!mConsecAlarm.isToAM()) {
                        decreasingHour += 12;
                    }
                    int decreasingMinute = mConsecAlarm.getToMinute();
                    for (int i = 0; i < mAlarmPicker.getValue() - 1; i++) {
                        if (decreasingMinute - mIntervalPicker.getValue() < 0) {
                            decreasingHour -= 1;
                            decreasingMinute -= mIntervalPicker.getValue();
                            decreasingMinute += 60;
                        } else {
                            decreasingMinute -= mIntervalPicker.getValue();
                        }
                    }
                    if (decreasingHour > 12) {
                        decreasingHour -= 12;
                        isAM = false;
                    }
                    mConsecAlarm.setFromHour(decreasingHour);
                    mConsecAlarm.setFromMinute(decreasingMinute);
                    mConsecAlarm.setFromAM(isAM);
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
                }
            }, hour, minute, false);
        }
        return null;
    }

    public void updateUI() {
        mAlarmAdapter = new AlarmAdapter(mConsecAlarm);
        mAlarmsRecyclerView.setAdapter(mAlarmAdapter);
    }

    public void startPicker(int position) {
        mPosition = position;
//      RingtoneManager.setActualDefaultRingtoneUri(EditAlarmActivity.this, RingtoneManager.TYPE_ALARM, Settings.System.DEFAULT_ALARM_ALERT_URI);
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm sound");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        startActivityForResult(intent, 999);
    }

    public void setVibrate(int position, boolean isChecked) {
        mPosition = position;
        mConsecAlarm.replaceAlarmVibrate(mPosition, isChecked);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            String title = ringtone.getTitle(this);
            mConsecAlarm.replaceAlarmUri(mPosition, uri.toString());
            mConsecAlarm.replaceAlarmName(mPosition, title);
            Log.e("Uri added with name ", uri.toString());
            Log.e("Name added with name ", title);
            updateUI();
        }
    }

    private class AlarmAdapter extends RecyclerView.Adapter<AlarmHolder> {
        private List<String> mAlarmName;

        public AlarmAdapter(ConsecAlarm consecAlarm) {
            mAlarmName = consecAlarm.getAlarmNameList();
        }

        @Override
        public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(EditAlarmActivity.this);
            View view = layoutInflater.inflate(R.layout.alarm_list_item_alarm, parent, false);
            return new EditAlarmActivity.AlarmHolder(view);
        }

        @Override
        public void onBindViewHolder(AlarmHolder holder, int position) {
            String alarmName = mAlarmName.get(position);
            holder.bindAlarm(alarmName);
        }

        @Override
        public int getItemCount() {
            return mAlarmName.size();
        }
    }

    private class AlarmHolder extends RecyclerView.ViewHolder {
        private String mAlarmName;
        private LinearLayout layoutButton;
        private CheckBox vibrateButton;
        private TextView alarmNameView;

        public AlarmHolder(View itemView) {
            super(itemView);
            layoutButton = (LinearLayout) itemView.findViewById(R.id.alarm_list_item_click_layout);
            alarmNameView = (TextView) itemView.findViewById(R.id.alarm_list_item_ringname);
            vibrateButton = (CheckBox) itemView.findViewById(R.id.alarm_list_item_check);
        }

        public void bindAlarm(String alarmName) {
            mAlarmName = alarmName;
            alarmNameView.setText(mAlarmName);
            layoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPicker(getLayoutPosition());
                }
            });
            vibrateButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setVibrate(getLayoutPosition(), isChecked);
                }
            });
            if (mConsecAlarm.getAlarmVibrate(getLayoutPosition())) {
                vibrateButton.setChecked(true);
            }
        }
    }

    public static Intent newIntent(Context packageContext, ConsecAlarm recievedConsecAlarm) {
        Intent i = new Intent(packageContext, EditAlarmActivity.class);
        i.putExtra(EXTRA_CONSECALARM_TO, recievedConsecAlarm);
        return i;
    }
}
