package com.tanpn.messenger.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanpn.messenger.R;
import com.tanpn.messenger.event.Event;
import com.tanpn.messenger.fragment_dialog.EventCategoryDialog;
import com.tanpn.messenger.fragment_dialog.EventRemindDialog;
import com.tanpn.messenger.utils.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ActivityAddEvent extends AppCompatActivity implements View.OnClickListener {


    private TextView tvEventTitle, tvEventDate,  tvSetEvTime, tvSetEvDate;

    public static TextView tvEventCategory, tvEventRemind;

    private EditText edtEventTitle;

    private FloatingActionButton fabSave, fabPicture;

    private Switch swEventNotify;
    private void init(){
        tvEventTitle = (TextView) findViewById(R.id.tvEventTitle);
        tvEventDate = (TextView) findViewById(R.id.tvEventDate);
        tvEventCategory = (TextView) findViewById(R.id.tvEventCategory);
        tvEventRemind = (TextView) findViewById(R.id.tvEventRemind);
        tvSetEvTime = (TextView) findViewById(R.id.tvSetEvTime);
        tvSetEvDate = (TextView) findViewById(R.id.tvSetEvDate);

        tvEventCategory.setOnClickListener(this);
        tvEventRemind.setOnClickListener(this);
        tvSetEvTime.setOnClickListener(this);
        tvSetEvDate.setOnClickListener(this);

        edtEventTitle = (EditText) findViewById(R.id.edtEventTitle);
        edtEventTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edtEventTitle.getText().equals("")){
                    tvEventTitle.setText("Untitled Event");
                }
                else{
                    tvEventTitle.setText(edtEventTitle.getText());
                }
            }
        });

        fabSave = (FloatingActionButton) findViewById(R.id.fab_save);
        fabSave.setOnClickListener(this);

        fabPicture = (FloatingActionButton) findViewById(R.id.fab_add_image);
        fabPicture.setOnClickListener(this);

        swEventNotify = (Switch) findViewById(R.id.swtEventNotify);
        swEventNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });


        //init fragment dialog
        eventCategoryDialog = new EventCategoryDialog();
        eventRemindDialog = new EventRemindDialog();


        // init date time of now
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // set date
        tvSetEvDate.setText(utils.getMonth(mMonth + 1) + " " + mDay + ", " + mYear);
        tvEventDate.setText(utils.getMonth(mMonth+1) + " " + mDay + ", " + mYear);

        // set time
        tvSetEvTime.setText(mHour + ":" + mMinute);
    }

    private DatabaseReference eventRef;
    private void initFirebase(){
        // Write a message to the database
        FirebaseDatabase root = FirebaseDatabase.getInstance();
        eventRef = root.getReference("event");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_event);

        init();
        initFirebase();

    }
    private EventCategoryDialog eventCategoryDialog;
    private EventRemindDialog eventRemindDialog;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvEventCategory:
                eventCategoryDialog.setCategory(eventCategoryDialog.getCategory());
                eventCategoryDialog.show(getSupportFragmentManager(), "dialog");


                break;
            case R.id.tvEventRemind:
                eventRemindDialog.setReminder(eventRemindDialog.getReminder());
                eventRemindDialog.show(getSupportFragmentManager(), "dialog");
                break;
            case R.id.tvSetEvTime:
                showTimePicker();
                break;
            case R.id.tvSetEvDate:
                showDatePicker();

                break;
            case R.id.fab_save:
                try {
                    saveNewEvent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.fab_add_image:
                addImage();
                break;

        }
    }

    private int mYear, mMonth, mDay, mHour, mMinute;


    private void showDatePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


                        tvSetEvDate.setText(utils.getMonth(monthOfYear +1) + " " + dayOfMonth + ", " + year);
                        tvEventDate.setText(utils.getMonth(monthOfYear +1) + " " + dayOfMonth + ", " + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }



    private void showTimePicker(){

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        tvSetEvTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);

        timePickerDialog.show();




    }

    private void saveNewEvent() throws JSONException {
        JSONObject event = new JSONObject();
        String eID = utils.generateEventId();
        event.put("id", eID);
        event.put("title", tvEventTitle.getText().toString());
        event.put("category", Event.EventType.valueOf(eventCategoryDialog.getCategory().toString()).ordinal());
        event.put("date", tvEventDate.getText().toString());
        event.put("time", tvSetEvTime.getText().toString());
        event.put("creater", "Tan Pham");
        event.put("remind", Event.Reminder.valueOf(eventRemindDialog.getReminder().toString()).ordinal());
        event.put("notification", swEventNotify.isChecked());

        JSONArray list = new JSONArray();
        list.put("ddd");
        list.put("msg 2");
        list.put("msg 3");

        event.put("picture", list);


        eventRef.child(eID).setValue(event.toString());

        onBackPressed();

    }

    private void addImage(){

    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();

    }
}
