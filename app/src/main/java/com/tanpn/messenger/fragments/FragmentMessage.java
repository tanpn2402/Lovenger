package com.tanpn.messenger.fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tanpn.messenger.R;
import com.tanpn.messenger.message.*;
import com.tanpn.messenger.paint.ActivityPaint;
import com.tanpn.messenger.photo.GalleryPicker;
import com.tanpn.messenger.photo.PreviewPhoto;
import com.tanpn.messenger.setting.GroupManager;
import com.tanpn.messenger.utils.PrefUtil;
import com.tanpn.messenger.utils.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMessage extends Fragment implements MessageListAdapter.OnEventListener, AdapterView.OnItemClickListener,
        View.OnTouchListener, View.OnClickListener, ChildEventListener {
    public FragmentMessage() {
        // Required empty public constructor
    }



    private ImageView ibtVoice, ibtSend, ibtCamera, ibtPicture, ibtDraw, ibtSetting;
    private EditText edtTyping;

    private ListView messageList;

    private MessageListAdapter messageListAdapter;

    private PrefUtil prefUser;
    private void setupLastMessage(String msg){
        prefUser.put(R.string.pref_key_last_message, msg);
        prefUser.apply();
    }

    // dialog fragment
    private VoiceRecorder voiceRecorder;    // dialog voice recorder


    private void init(View view){

        prefUser = new PrefUtil(getContext());

        voiceRecorder = new VoiceRecorder();


        ibtCamera = (ImageView) view.findViewById(R.id.ibtCamera);
        ibtSend = (ImageView) view.findViewById(R.id.ibtSend);
        ibtVoice = (ImageView) view.findViewById(R.id.ibtVoice);
        ibtPicture = (ImageView) view.findViewById(R.id.ibtPicture);
        ibtDraw = (ImageView) view.findViewById(R.id.ibtDraw);
        ibtSetting = (ImageView) view.findViewById(R.id.ibtSetting);

        ibtCamera.setOnTouchListener(this);
        ibtSend.setOnTouchListener(this);
        ibtVoice.setOnTouchListener(this);
        ibtPicture.setOnTouchListener(this);
        ibtDraw.setOnTouchListener(this);
        ibtSetting.setOnTouchListener(this);

        ibtCamera.setOnClickListener(this);
        ibtSend.setOnClickListener(this);
        ibtVoice.setOnClickListener(this);
        ibtPicture.setOnClickListener(this);
        ibtDraw.setOnClickListener(this);
        ibtSetting.setOnClickListener(this);

        ibtSend.setEnabled(false);



        edtTyping = (EditText) view.findViewById(R.id.edtTyping);
        edtTyping.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(textView.getText().equals(""))
                    ibtSend.setEnabled(false);
                else
                    ibtSend.setEnabled(true);

                return false;
            }
        });

        edtTyping.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edtTyping.getText().equals(""))
                    ibtSend.setEnabled(false);
                else
                    ibtSend.setEnabled(true);
            }
        });

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(changeGroup, new IntentFilter("CHANGE_GROUP"));

    }


    /**
     * Local Broadcast
     * */
    private BroadcastReceiver changeGroup = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            onChange(message);
        }
    };

    private StorageReference photoRef;  // reference to storage
    private DatabaseReference messageRef; // reference to database
    private StorageReference voiceRef;
    private FirebaseDatabase root;

    private void initFirebase(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        photoRef = storage.getReferenceFromUrl("gs://messenger-d08e4.appspot.com/message/");    // photo trong message khac voi photo trong PHOTO
        voiceRef = storage.getReferenceFromUrl("gs://messenger-d08e4.appspot.com/voice/");


        root = FirebaseDatabase.getInstance();
        messageRef = root.getReference(prefUser.getString(R.string.pref_key_current_groups)).child("message");
        messageRef.addChildEventListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);

        View layout_typing = v.findViewById(R.id.typing_area);
        init(layout_typing);

        messageList = (ListView) v.findViewById(R.id.messageList);
        messageListAdapter = new MessageListAdapter(getContext());


        messageList.setAdapter(messageListAdapter);

        // firebase
        initFirebase();

        messageList.setOnItemClickListener(this);
        messageList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                MessageListElement item = (MessageListElement) adapterView.getItemAtPosition(i);
                if(item.messageType == MessageListElement.MESSAGE_TYPE.PHOTO){
                    PreviewPhoto preview = new PreviewPhoto();

                    /**
                     * Vì message này chỉ chứa 1 element duy nhất nên dùng cái này được, k ảnh hưởng nhiều đến thời gian
                     * do có thể download hình này về device nên truyền tham số Map
                     * */

                    preview.setPhotoDetail(item.message);
                    preview.show(getActivity().getFragmentManager(),  "dialog");
                }

                else if(item.messageType == MessageListElement.MESSAGE_TYPE.VOICE){
                    VoicePlayer preview = new VoicePlayer();
                    preview.setVoiceDetail(item.message);
                    preview.show(getActivity().getFragmentManager(),  "dialog");
                }

                return true;
            }
        });


        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MessageListElement item = (MessageListElement) adapterView.getItemAtPosition(i);
    }


    @Override
    public void onDirtyStateChanged(boolean dirty) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                changeStatus(view, true);
                return false;
            case MotionEvent.ACTION_UP:
                changeStatus(view, false);
                return false;

        }

        return false;
    }


    private void changeStatus(View view, boolean isPress){
        switch (view.getId()){
            case R.id.ibtCamera:

                break;
            case R.id.ibtVideo:

                break;
            case R.id.ibtDraw:

                break;
            case R.id.ibtEmoji:

                break;
            case R.id.ibtPicture:

                break;
            case R.id.ibtSend:
                if(isPress)
                    ibtSend.setImageResource(R.drawable.ic_send_pink);
                else
                    ibtSend.setImageResource(R.drawable.ic_send_gray);
                break;
            case R.id.ibtSetting:

                break;
        }
    }

    private final int PAINT_CODE = 1;
    private final int GALLERY_CODE = 2;
    private final int CAMERA_CODE = 3;
    private final int VOICE_CODE = 4;
    private final int VIDEO_CODE = 4;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtCamera:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_CODE);
                break;
            case R.id.ibtVideo:

                break;
            case R.id.ibtDraw:
                // open paint
                Intent paint = new Intent(getContext(), ActivityPaint.class);
                startActivityForResult(paint, 1);
                break;
            case R.id.ibtEmoji:

                break;
            case R.id.ibtPicture:
                // photo
                Intent photo = new Intent(getContext(), GalleryPicker.class);
                startActivityForResult(photo, GALLERY_CODE);
                break;
            case R.id.ibtSend:
                String me = edtTyping.getText().toString();
                sendTextMessage(me);
                break;
            case R.id.ibtSetting:

                break;
            case R.id.ibtVoice:
                voiceRecorder.show(getActivity().getSupportFragmentManager(), "dialog");
                voiceRecorder.setTargetFragment(getTargetFragment(), VOICE_CODE);
                /*
                * see at: http://stackoverflow.com/questions/10905312/receive-result-from-dialogfragment
                * */
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK)
            return;

        if(requestCode == GALLERY_CODE){
            sendPhotoMessage(data);
        }
        else if(requestCode == PAINT_CODE){
            sendPaintMessage(data);
        }

        else if(requestCode == CAMERA_CODE){
            sendCameraMessage(data);
        }
        else if(requestCode == VOICE_CODE){
            Log.i("REQ", "voice");
            sendVoiceMessage(data);
        }
        else if(requestCode == VIDEO_CODE){
            sendVideoMessage(data);
        }

    }



    private void sendTextMessage(String me){


        Map<String, String> m = new HashMap<>();
        m.put("null", me);

        JSONObject obj = makeMessage(m, MessageListElement.MESSAGE_TYPE.TEXT);

        try {
            messageListAdapter.add(
                    new MessageListElement(
                            obj.getString("id"),
                            true,                                                   // isSender
                            prefUser.getString(R.string.pref_key_username, "null"),                                         // name
                            MessageListElement.MESSAGE_TYPE.TEXT,
                            m,
                            prefUser.getString(R.string.pref_key_user_photo_name, "null"),                                           //avatar
                            MessageListElement.MESSAGE_STATUS.SENDING,
                            getTimeSent(),                                         // sentDate
                            ""));                                                   // receiveDate

            // luu vao pref

            setupLastMessage( obj.getString("id"));

            // upload lên database
            messageRef.child(obj.getString("id")).setValue(obj.toString()); // upload leen database

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private void sendPaintMessage(Intent data){

    }

    private void sendPhotoMessage(Intent data){
        String[] imagesPath = data.getStringExtra("data").split("\\|");
        for(int i = 0; i < imagesPath.length; i++){

            // upload to storage
            Uri uri = Uri.fromFile(new File(imagesPath[i]));
            final String photoID = utils.generatePhotoId();
            UploadTask uploadTask = photoRef.child(photoID).putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // upload to storage success
                    // now, next to  upload message to message in database
                    Map<String, String> ms = new HashMap<String, String>();
                    ms.put(photoID, taskSnapshot.getDownloadUrl().toString());

                    JSONObject msg = makeMessage(ms, MessageListElement.MESSAGE_TYPE.PHOTO);
                    if(msg != null){
                        try {
                            messageRef.child(msg.getString("id")).setValue(msg.toString()); // upload leen database
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // add to adapter
                        MessageListElement m = new MessageListElement(msg.toString());
                        messageListAdapter.add(m);
                        setupLastMessage(  m.id);
                        Log.i("loi", "ok");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("loi", "loi");
                }
            });

        }
    }

    private void sendVoiceMessage(Intent data){
        String voicePath =  data.getStringExtra("data");
        Uri uri = Uri.fromFile(new File(voicePath));
        final String voiceID = utils.generatePhotoId();

        UploadTask uploadTask = voiceRef.child(voiceID).putFile(uri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // upload to storage success
                // now, next to  upload message to message in database
                Map<String, String> ms = new HashMap<String, String>();
                ms.put(voiceID, taskSnapshot.getDownloadUrl().toString());

                JSONObject msg = makeMessage(ms, MessageListElement.MESSAGE_TYPE.VOICE);
                if(msg != null){
                    try {
                        messageRef.child(msg.getString("id").toString()).setValue(msg.toString()); // upload leen database
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // add to adapter
                    MessageListElement m = new MessageListElement(msg.toString());
                    messageListAdapter.add(m);
                    setupLastMessage( m.id);
                    Log.i("loi", "ok");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("loi", "loi");
            }
        });
    }

    private void sendCameraMessage(Intent data){
        Bitmap photo = (Bitmap) data.getExtras().get("data");

        if(photo == null)
            return;

        // convert to upload to storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, baos);   // full quality 100
        byte[] bytes = baos.toByteArray();

        final String photoName = utils.generatePhotoId();
        UploadTask uploadTask = photoRef.child(photoName).putBytes(bytes);  // upload len storage

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {}
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Map<String, String> ms = new HashMap<String, String>();
                ms.put(photoName, taskSnapshot.getDownloadUrl().toString());
                JSONObject msg = makeMessage(ms, MessageListElement.MESSAGE_TYPE.PHOTO);
                if(msg != null)
                    try {
                        messageRef.child(msg.getString("id").toString()).setValue(msg.toString()); // upload leen database
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });
    }

    private void sendVideoMessage(Intent data){

    }



    private String getTimeSent(){
        Calendar calendar = Calendar.getInstance();
        return "" + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.MONTH) +"/"+ calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

    }

    private JSONObject makeMessage(Map<String, String> msg, MessageListElement.MESSAGE_TYPE type){
        JSONObject obj = new JSONObject();


        try {
            obj.put("id", utils.generateMessageId());
            obj.put("name", prefUser.getString(R.string.pref_key_username, "null"));
            obj.put("type", type.ordinal());

            JSONObject o = new JSONObject();
            for( Map.Entry<String, String> m : msg.entrySet() ){
                o.put("id", m.getKey());
                o.put("path", m.getValue());
            }


            obj.put("message", o);
            obj.put("avatar", prefUser.getString(R.string.pref_key_user_photo_name, "null"));
            obj.put("sentDate", getTimeSent());
            obj.put("receiveDate", "14/11/2016 9:31");
            obj.put("status", MessageListElement.MESSAGE_STATUS.RECEIVE.ordinal());

            return obj;


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * firebase
     * */
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        try {
            JSONObject obj = new JSONObject(dataSnapshot.getValue().toString());

            JSONObject o = (JSONObject) obj.get("message");
            Map<String, String> m = new HashMap<>();
            m.put(o.getString("id"), o.getString("path"));

            MessageListElement msg = new MessageListElement(
                    obj.getString("id"),
                    obj.getString("name").equals(prefUser.getString(R.string.pref_key_username, "null")),       // isSender
                    obj.getString("name"),
                    MessageListElement.MESSAGE_TYPE.values()[obj.getInt("type")],
                    m,
                    obj.getString("avatar"),
                    MessageListElement.MESSAGE_STATUS.values()[obj.getInt("status")],
                    obj.getString("sentDate"),
                    obj.getString("receiveDate")
            );

            messageListAdapter.add(msg);
            setupLastMessage(msg.id);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onCancelled(DatabaseError databaseError) {}


    /**
     * change group
     * */
    public void onChange(String data) {
        messageRef.removeEventListener(this);
        messageListAdapter.deleteAll();
        messageListAdapter.notifyDataSetChanged();

        messageRef = root.getReference(data).child("message");
        messageRef.addChildEventListener(this);
    }
}
