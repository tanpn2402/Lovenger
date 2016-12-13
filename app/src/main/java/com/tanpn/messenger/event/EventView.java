package com.tanpn.messenger.event;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanpn.messenger.R;
import com.tanpn.messenger.pagetransformer.FlipHorizontalTransformer;
import com.tanpn.messenger.photo.PhotoViewAdapter;
import com.tanpn.messenger.utils.PrefUtil;
import com.tanpn.messenger.utils.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventView extends AppCompatActivity implements View.OnClickListener, ChildEventListener {

    private TextView tvEventTitle, tvEventDays, tvEventDate, tvEventTime, tvEventCreated;
    private ViewPager viewPager;
    private ImageView imEventStatus, imEventCatregory;

    private ImageView ibtEventDetail, ibtEventShare, ibtEventDelete, ibtBack, ibtHide, ibtShow;

    private RelativeLayout rlPanelDetail, rlPanelHeader, rlPanelComment, rlAppbar;
    private EditText edtTyping;
    private ImageButton ibtSend;
    private ListView listComment;
    private CommentListAdapter adapter;

    private List<String> photoPaths;
    private EventListElement event;
    private String eventString;

    private PrefUtil prefUtil;

    private void initPanel(){
        rlPanelDetail = (RelativeLayout) findViewById(R.id.rlPanelDetail);
        rlPanelHeader = (RelativeLayout) findViewById(R.id.rlPanelHeader);
        rlPanelComment = (RelativeLayout) findViewById(R.id.rlPanelComment);
        rlAppbar = (RelativeLayout) findViewById(R.id.rlAppbar);

    }

    private void initCommmetArea(){
        edtTyping = (EditText) findViewById(R.id.edtTyping);
        ibtSend = (ImageButton) findViewById(R.id.ibtSend );
        ibtSend.setOnClickListener(this);
        listComment = (ListView) findViewById(R.id.commentList);
    }

    private DatabaseReference eventRef;
    private FirebaseDatabase root;
    private void initFirebase(String eId){
        root = FirebaseDatabase.getInstance();
        eventRef  = root.getReference(prefUtil.getString(R.string.pref_key_current_groups)).child("event").child(eId);
        // root / <group id> / event / <event id>/..

        //eventRef.addChildEventListener(this);

        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    adapter.add(dataSnapshot.getValue().toString());
                    Log.i("comment datachaned", dataSnapshot.getValue().toString());
                    // update la eventString
                    eventString = dataSnapshot.getValue().toString();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void init(){
        prefUtil = new PrefUtil(this);

        tvEventDays = (TextView) findViewById(R.id.tvEventDays);
        tvEventTitle = (TextView) findViewById(R.id.tvEventTitle);

        tvEventDate = (TextView) findViewById(R.id.tvEventDate);
        tvEventTime = (TextView) findViewById(R.id.tvEventTime);
        tvEventCreated = (TextView) findViewById(R.id.tvEventCreated);

        ibtEventDetail = (ImageView) findViewById(R.id.ibtEventDetail);
        ibtEventDetail.setOnClickListener(this);
        ibtEventShare = (ImageView) findViewById(R.id.ibtEventShare);
        ibtEventShare.setOnClickListener(this);
        ibtEventDelete = (ImageView) findViewById(R.id.ibtEventDelete);
        ibtEventDelete.setOnClickListener(this);
        ibtBack = (ImageView) findViewById(R.id.ibtBack);
        ibtBack.setOnClickListener(this);
        ibtHide = (ImageView) findViewById(R.id.ibtHide);
        ibtHide.setOnClickListener(this);
        ibtShow = (ImageView) findViewById(R.id.ibtShow);
        ibtShow.setOnClickListener(this);

        imEventCatregory = (ImageView) findViewById(R.id.imEventCategory);
        imEventStatus = (ImageView) findViewById(R.id.imEventStatus);


        initPanel();
        initCommmetArea();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            eventString = bundle.getString("data");
            event = utils.readJSONString(eventString);
        }

        if(event == null)
            return;

        initFirebase(event.id);
        adapter = new CommentListAdapter(this, eventString);
        listComment.setAdapter(adapter);



        tvEventCreated.setText(event.creater);
        tvEventTitle.setText(event.title);
        tvEventDays.setText(Math.abs(event.days) + "");
        tvEventDate.setText(utils.calendarToDateString(event.datetime));
        tvEventTime.setText(utils.calendarToTimeString(event.datetime));

        imEventCatregory.setImageResource(utils.getEventCategoryResourceID(event.type));
        if(event.days > 0){
            imEventStatus.setImageResource(utils.getEventStatusResourceID(Event.EventStatus.PASS));

        }else{
            imEventStatus.setImageResource(utils.getEventStatusResourceID(Event.EventStatus.FURTURE));
        }


        viewPager = (ViewPager) findViewById(R.id.photoView);
        PhotoViewAdapter adapter = new PhotoViewAdapter(this, new ArrayList<>(event.pictures.values()));
        viewPager.setAdapter(adapter);
        //viewPager.setCurrentItem(currentPhoto);

        viewPager.setPageTransformer(false, new FlipHorizontalTransformer());

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtEventDetail:
                viewDetail();
                break;

            case R.id.ibtEventDelete:
                deleteEvent();
                break;

            case R.id.ibtEventShare:
                shareEvent();
                break;
            case R.id.ibtHide:
                hidePanel();
                break;
            case R.id.ibtShow:
                showPanel();
                break;
            case R.id.ibtBack:
                onBackPressed();
                break;
            case R.id.ibtSend:
                sendComment();
                break;
        }
    }


    private void viewDetail(){
        Intent in = new Intent(this, EventDetail.class);
        in.putExtra("data", eventString);
        startActivity(in);
        finish();
    }


    private void deleteEvent(){

        final String eId = event.id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(prefUtil.getString(R.string.pref_key_current_event,null).equals(eId)){
            builder.setMessage("Bạn không thể xóa sự kiện mặc định.")
                    .setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create().show();
        }
        else{
            builder.setMessage("Bạn có muốn xóa sự kiện này.")
                    .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delete(eId);
                        }
                    })
                    .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create().show();
        }

    }

    private void delete(String eId){
        String uid = prefUtil.getString(R.string.pref_key_uid);


        FirebaseDatabase.getInstance().getReference(uid).child("event").child(eId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        onBackPressed();
                    }
                });
    }


    private void shareEvent(){

    }

    private Handler handler = new Handler();
    private void hidePanel(){

        if(rlPanelComment.getVisibility() == View.GONE){
            // dang o trang thai binh thuong
            // ẩn hết chỉ còn lại header
            rlPanelDetail.setVisibility(View.GONE);
            rlPanelComment.setVisibility(View.GONE);
            rlAppbar.setVisibility(View.VISIBLE);

            ibtShow.setVisibility(View.VISIBLE);
            ibtHide.setVisibility(View.GONE);


        }
        else if(rlPanelComment.getVisibility() == View.VISIBLE){
            // dang o trang thai co panel comment
            // ẩn rl comment
            rlPanelDetail.setVisibility(View.VISIBLE);
            rlPanelComment.setVisibility(View.GONE);
            rlAppbar.setVisibility(View.VISIBLE);

            // nút ẩn vẫn còn hiện
            ibtShow.setVisibility(View.GONE);
            ibtHide.setVisibility(View.VISIBLE);
        }



    }

    private void showPanel(){

        if(rlPanelDetail.getVisibility() == View.GONE){
            // từ chỉ còn header đến có cả header + detail
            rlPanelDetail.setVisibility(View.VISIBLE);
            rlPanelComment.setVisibility(View.GONE);
            rlAppbar.setVisibility(View.VISIBLE);

            // nút show vẫn còn hiện
            ibtShow.setVisibility(View.VISIBLE);
            ibtHide.setVisibility(View.GONE);
        }
        else if(rlPanelDetail.getVisibility() == View.VISIBLE){
            // từ có cả header + detail đến có header + commnt
            rlPanelDetail.setVisibility(View.GONE);
            rlPanelComment.setVisibility(View.VISIBLE);
            rlAppbar.setVisibility(View.GONE);

            // nút show vẫn còn hiện
            ibtShow.setVisibility(View.GONE);
            ibtHide.setVisibility(View.VISIBLE);
        }


    }


    private void sendComment(){
        if(edtTyping.getText().toString().isEmpty())
            return;

        String cmt = edtTyping.getText().toString();
        Calendar c  = Calendar.getInstance();
        String dateime = utils.calendarToTimeString(c) + "  " + utils.calendarToDateString(c);
        String userphoto = prefUtil.getString(R.string.pref_key_user_photo_name);
        String id = utils.generateCommentId();


        try {
            JSONObject obj = new JSONObject(eventString);
            JSONArray arr;
            if(obj.has("comment")){
                arr = obj.getJSONArray("comment");
            }
            else{
                arr = new JSONArray();
            }

            JSONObject o = new JSONObject();
            o.put("id", id);
            o.put("userphoto", userphoto);
            o.put("content", cmt);
            o.put("datetime", dateime);

            arr.put(o);

            obj.put("comment", arr);
            Log.i("comment", arr.toString());

            eventRef.setValue(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        edtTyping.setText("");
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onCancelled(DatabaseError databaseError) {}
}
