package com.tanpn.messenger.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.tanpn.messenger.R;
import com.tanpn.messenger.message.*;
import com.tanpn.messenger.paint.ActivityPaint;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMessage extends Fragment implements MessageListAdapter.OnEventListener, AdapterView.OnItemClickListener, View.OnTouchListener {
    public FragmentMessage() {
        // Required empty public constructor
    }



    private ImageView ibtVoice, ibtSend, ibtCamera, ibtPicture, ibtDraw, ibtSetting;
    private EditText edtTyping;

    private ListView messageList;

    private MessageListAdapter messageListAdapter;

    private void init(View view){
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);

        View layout_typing = v.findViewById(R.id.typing_area);
        init(layout_typing);

        messageList = (ListView) v.findViewById(R.id.messageList);

        List<MessageListElement> list = new ArrayList<>();
        list.add(new MessageListElement(true, "tan", "chap em yeu 1", null, getString(R.string.message_status_sending), "12:00 20/10/2016", null));
        list.add(new MessageListElement(false, "hang", "chao anh yeu 1", null, getString(R.string.message_status_delivered), "12:00 20/10/2016", null));
        list.add(new MessageListElement(false, "hang", "chao anh yeu 2", null, getString(R.string.message_status_delivered), "12:00 20/10/2016", null));
        list.add(new MessageListElement(false, "hang", "chao anh yeu 3", null, getString(R.string.message_status_delivered), "12:00 20/10/2016", null));
        list.add(new MessageListElement(false, "hang", "chao anh yeu 4", null, getString(R.string.message_status_delivered), "12:00 20/10/2016", null));
        list.add(new MessageListElement(false, "hang", "chao anh yeu 5", null, getString(R.string.message_status_sending), "12:00 20/10/2016", null));
        list.add(new MessageListElement(true, "tan", "chap em yeu 2", null, getString(R.string.message_status_delivered), "12:00 20/10/2016", null));
        list.add(new MessageListElement(true, "tan", "chap em yeu 3", null, getString(R.string.message_status_delivered), "12:00 20/10/2016", null));
        list.add(new MessageListElement(true, "tan", "chap em yeu 4", null, getString(R.string.message_status_delivered), "12:00 20/10/2016", null));
        list.add(new MessageListElement(true, "tan", "chap em yeu 5", null, getString(R.string.message_status_sending), "12:00 20/10/2016", null));

        messageListAdapter = new MessageListAdapter(getContext(), list);

        messageList.setAdapter(messageListAdapter);

        messageList.setOnItemClickListener(this);

        ibtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String me = edtTyping.getText().toString();
                messageListAdapter.add(new MessageListElement(true, "",me, null, getString(R.string.message_status_sending),"",""));


            }
        });

        ibtVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageListAdapter.modifyStatus(messageListAdapter.getCount()-1, getString(R.string.message_status_seen));

                onDirtyStateChanged(true);
            }
        });


        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MessageListElement item = (MessageListElement) adapterView.getItemAtPosition(i);



        Log.i("TAN", item.message);
    }

    @Override
    public void onLoadComplete() {

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
}
