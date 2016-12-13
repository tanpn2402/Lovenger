package com.tanpn.messenger.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tanpn.messenger.R;
import com.tanpn.messenger.event.Event;
import com.tanpn.messenger.fragment_dialog.EventCategoryDialog;
import com.tanpn.messenger.fragment_dialog.EventRemindDialog;
import com.tanpn.messenger.message.MessageListElement;
import com.tanpn.messenger.photo.GalleryPicker;
import com.tanpn.messenger.photo.PhotoElement;
import com.tanpn.messenger.photo.PhotoListAdapter;
import com.tanpn.messenger.photo.PreviewPhoto;
import com.tanpn.messenger.setting.GroupManager;
import com.tanpn.messenger.utils.PrefUtil;
import com.tanpn.messenger.utils.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetail extends AppCompatActivity implements View.OnClickListener, PhotoListAdapter.OnEventListener,
        PreviewPhoto.OnResultListener, GroupManager.onGroupChange {


    private TextView tvEventTitle, tvEventDate,  tvSetEvTime, tvSetEvDate, tvEventDays;

    public static TextView tvEventCategory, tvEventRemind;

    private EditText edtEventTitle;

    private FloatingActionButton fabSave, fabPicture;

    private Switch swEventNotify;

    private GridView photoList;
    private PhotoListAdapter photoListAdapter;
    private Map<String, String> listPath;
    private EventListElement event;

    private EventCategoryDialog eventCategoryDialog;
    private EventRemindDialog eventRemindDialog;
    private PreviewPhoto previewPhoto;

    private PrefUtil prefUtil;


    private void init(){

        prefUtil = new PrefUtil(this);



        listPath = new HashMap<>();

        tvEventTitle = (TextView) findViewById(R.id.tvEventTitle);
        tvEventDate = (TextView) findViewById(R.id.tvEventDate);
        tvEventDays = (TextView) findViewById(R.id.tvEventDays);
        tvEventCategory = (TextView) findViewById(R.id.tvEventCategory);
        tvEventRemind = (TextView) findViewById(R.id.tvEventRemind);
        tvSetEvTime = (TextView) findViewById(R.id.tvSetEvTime);
        tvSetEvDate = (TextView) findViewById(R.id.tvSetEvDate);
        swEventNotify = (Switch) findViewById(R.id.swtEventNotify);

        edtEventTitle = (EditText) findViewById(R.id.edtEventTitle);

        //init fragment dialog
        eventCategoryDialog = new EventCategoryDialog();
        eventRemindDialog = new EventRemindDialog();

        tvEventCategory.setOnClickListener(this);
        tvEventRemind.setOnClickListener(this);
        tvSetEvTime.setOnClickListener(this);
        tvSetEvDate.setOnClickListener(this);

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


        swEventNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });





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


        // grid view
        photoList = (GridView) findViewById(R.id.photoList);
        photoListAdapter = new PhotoListAdapter(this);
        photoList.setAdapter(photoListAdapter);

        // preview dialog
        previewPhoto = new PreviewPhoto();
        photoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                PhotoElement element = (PhotoElement) photoListAdapter.getItem(i);
                previewPhoto.setPhotoElement(element);
                previewPhoto.setDeletale(true);
                previewPhoto.show(getFragmentManager(), "dialog");

                return true;
            }
        });


        initValues();
    }

    /**
     * View details event: set values for all components
     * */
    private void initValues(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String s = bundle.getString("data");
            event = utils.readJSONString(s);

            if(event != null){
                tvEventCategory.setText(event.type.toString());
                tvEventDate.setText(utils.calendarToDateString(event.datetime));
                tvEventRemind.setText(event.remind.toString());
                tvEventTitle.setText(event.title);
                tvSetEvTime.setText(utils.calendarToTimeString(event.datetime));
                tvSetEvDate.setText(tvEventDate.getText());
                tvEventDays.setText(event.days >= 0 ? event.days +"" : -event.days + "");
                swEventNotify.setChecked(event.notify);

                eventCategoryDialog.setCategory(event.type);
                eventRemindDialog.setReminder(event.remind);

                //listPath = event.pictures;

                edtEventTitle.setText(event.title);

                /**
                 * event.pictures: la 1 hashmap<String, String>
                 *     <id, path> cua photo
                 * */
                for(Map.Entry<String, String> e : event.pictures.entrySet()){
                    listPath.put(e.getKey(), e.getValue());

                    photoListAdapter.add(
                            e.getKey(),
                            new PhotoElement(e.getKey(), 0, e.getValue())
                    );
                }

            }
        }
    }

    private DatabaseReference eventRef;
    private StorageReference eventStorageRef;
    private FirebaseDatabase root;
    private void initFirebase(){
        // Write a message to the database
        root = FirebaseDatabase.getInstance();
        eventRef = root.getReference(prefUtil.getString(R.string.pref_key_current_groups)).child("event");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        eventStorageRef = storage.getReferenceFromUrl("gs://messenger-d08e4.appspot.com/event/");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_event);

        init();
        initFirebase();

    }



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
                new createNewEvent().execute();
                showNotificationForUploading();
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
    private Map<String, String> listPathInStorage= new HashMap<>();


    /**
     * change group
     * */
    @Override
    public void onChange(String data) {
        eventRef = root.getReference(data).child("event");
    }

    private class createNewEvent extends AsyncTask<Void, Void, Boolean>{
        private int totalPhoto = listPath.size();
        private int index = 0;

        @Override
        protected Boolean doInBackground(Void... voids) {

            for(final Map.Entry<String, String> photo : listPath.entrySet()){

                Uri uri = Uri.fromFile(new File(photo.getValue()));
                UploadTask uploadTask = eventStorageRef.child(photo.getKey()).putFile(uri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        listPathInStorage.put(photo.getKey(), taskSnapshot.getDownloadUrl().toString());

                        index++;

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        index++;
                    }
                });
            }

            // wait for int upload done
            while(index < totalPhoto){
                SystemClock.sleep(100);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            try {
                saveNewEvent();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveNewEvent() throws JSONException {
        String eID;
        if(event != null){
            // chinh sua event
            eID = event.id;
        }
        else{
            // tao event moi
            eID = utils.generateEventId();
        }
        JSONObject event = new JSONObject();

        event.put("id", eID);
        event.put("title", tvEventTitle.getText().toString());
        event.put("category", Event.EventType.valueOf(eventCategoryDialog.getCategory().toString()).ordinal());
        event.put("date", tvEventDate.getText().toString());
        event.put("time", tvSetEvTime.getText().toString());
        event.put("creater", "Tan Pham");
        event.put("remind", Event.Reminder.valueOf(eventRemindDialog.getReminder().toString()).ordinal());
        event.put("notification", swEventNotify.isChecked());

        JSONArray list = new JSONArray();

        /**
         * Caution: luu duong dan photo tren firebase storage
         * */
        for(Map.Entry<String, String> e : listPathInStorage.entrySet()){
            JSONObject o = new JSONObject();
            o.put("photoid", e.getKey());
            o.put("photopath", e.getValue());

            list.put(o);
        }

        event.put("picture", list);


        eventRef.child(eID).setValue(event.toString());

        // ẩn snackbar
        notificationUploading.dismiss();

        // trở về event fragment
        onBackPressed();

    }

    private Snackbar notificationUploading;
    private void showNotificationForUploading(){
        notificationUploading = Snackbar.make(
                findViewById(R.id.layout_add_event),
                "Đang tải lên " + listPath.size() + " ảnh và tạo sự kiện mới",
                Snackbar.LENGTH_INDEFINITE);

        notificationUploading.setAction("Ẩn", new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                notificationUploading.dismiss();
            }
        });
        //notificationUploading.setActionTextColor(getResources().getColor(R.color.white));

        //View view = notificationUploading.getView();
        //view.setBackgroundColor(getResources().getColor(R.color.orange_warning));

        notificationUploading.show();
    }

    private final int GALLERY_CODE = 1;
    private void addImage(){
        Intent photo = new Intent(this, GalleryPicker.class);
        startActivityForResult(photo, GALLERY_CODE);
    }

    private Handler handler = new Handler();

    private class loadPhoto implements Runnable{

        private String[] paths;
        public loadPhoto(String[] p){
            paths = p;
        }

        @Override
        public void run() {
            for(int i = 0; i < paths.length; i++){
                String id = utils.generatePhotoId();
                PhotoElement element = new PhotoElement(id,0, paths[i].toString());
                listPath.put(id, paths[i].toString());
                photoListAdapter.add(
                        id,
                        element
                );

                SystemClock.sleep(100);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == GALLERY_CODE){

            String[] imagesPath = data.getStringExtra("data").split("\\|");

            handler.post(new loadPhoto(imagesPath));

        }

    }

    @Override
    public void onDirtyStateChanged(boolean dirty) {}

    // implement tu photo preview

    @Override
    public void OnResult(PhotoElement photo) {

        Log.i("TAGGGG", "result photo");
        if(listPath.containsKey(photo.id))
            listPath.remove(photo.id);

        /**
         * remove bang key
         * see at: https://www.tutorialspoint.com/java/util/hashmap_remove.htm
         * */


        // remove anh ra khoi danh sanh gridview
        if(photoListAdapter.contain(photo)){
            photoListAdapter.delete(photo.id);
            photoListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();

    }
}
