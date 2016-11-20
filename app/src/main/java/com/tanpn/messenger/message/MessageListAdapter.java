package com.tanpn.messenger.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tanpn.messenger.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by phamt_000 on 11/1/16.
 */
public class MessageListAdapter extends BaseAdapter {

    private List<MessageListElement> messagelist;
    private List<String> msgID;
    private Context context;

    public MessageListAdapter(Context _context){
        context = _context;
        messagelist = new ArrayList<>();
        msgID = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return messagelist.size();
    }

    @Override
    public Object getItem(int i) {
        return messagelist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        // số kiểu layout
        return 6;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(messagelist.get(i).isSender){
            return createMessageSender(i,view,viewGroup);
        }
        else
            return createMessageReceiver(i, view, viewGroup);
    }

    private View createMessageSender(int i, View view, ViewGroup viewGroup){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        View v = generateMsgText(true, messagelist.get(i), inflater, viewGroup);

        switch (messagelist.get(i).messageType){

            case PHOTO:
                v = generateMsgPhoto(true, messagelist.get(i), inflater, viewGroup);
                break;
            case VOICE:
                v = generateMsgVoice(true, messagelist.get(i), inflater, viewGroup);
                break;
            case VIDEO:
                v = generateMsgVideo(true, messagelist.get(i), inflater, viewGroup);
                break;
            case LINK:
                v = generateMsgLink(true, messagelist.get(i), inflater, viewGroup);
                break;

        }

        messagelist.get(i).view = v;
        return v;
    }

    private View createMessageReceiver(int i, View view, ViewGroup viewGroup){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = generateMsgText(false, messagelist.get(i), inflater, viewGroup);

        switch (messagelist.get(i).messageType){

            case PHOTO:
                v = generateMsgPhoto(false, messagelist.get(i), inflater, viewGroup);
                break;
            case VOICE:
                v = generateMsgVoice(false, messagelist.get(i), inflater, viewGroup);
                break;
            case VIDEO:
                v = generateMsgVideo(false, messagelist.get(i), inflater, viewGroup);
                break;
            case LINK:
                v = generateMsgLink(false, messagelist.get(i), inflater, viewGroup);
                break;

        }
        messagelist.get(i).view = v;
        return v;
    }

    private void generateAvatar(ImageView imAvatar, MessageListElement msg){
        if(msg.avatar != null){
            imAvatar.setImageBitmap(msg.avatar);
        }
        else{
            imAvatar.setVisibility(View.GONE);
        }
    }

    private void generateStatus(boolean isSender, TextView tvStatus, MessageListElement msg){
        if(isSender){

            if(msg.status.equals(context.getString(R.string.message_status_sending))){
                // nếu đang gửi thì hiển thị
                tvStatus.setText(context.getString(R.string.message_status_sending));
            }
            else{
                // nếu không đang gửi

                if(msg.isLast()){
                    // nếu là tin nhắn cuối cùng thì phải hiện thị rõ status
                    tvStatus.setText(getStatusString(msg.status));
                }else{
                    tvStatus.setVisibility(View.GONE);
                }
            }
        }
        else {



            // tin nhắn từ receiver thì k có status, chỉ hiển thị status khi người dùng click vào message đó
            tvStatus.setVisibility(View.GONE);
        }
    }

    private String getStatusString(MessageListElement.MESSAGE_STATUS status){
        switch (status){

            case DELIVERY:
                return "Đã gửi";
            case RECEIVE:
                return "Đã nhận";
            case SENDING:
                return "Đang gửi";
            case SEEN:
                return "Đã xem";
            case ERROR:
                return "Lỗi";
        }

        return "Đang gửi";
    }

    private View generateMsgText(boolean isSender, MessageListElement msg, LayoutInflater inflater, ViewGroup viewGroup){
        View v;
        if(isSender)
            v = inflater.inflate(R.layout.layout_msg_sender, viewGroup, false);
        else
            v = inflater.inflate(R.layout.layout_msg_receiver, viewGroup, false);

        // display message
        TextView tvMessage = (TextView) v.findViewById(R.id.tvMessage);

        String ms = "";
        for(Map.Entry<String, String> m : msg.message.entrySet()){
            ms = m.getValue();
        }
        tvMessage.setText(ms);

        // message status
        generateStatus(isSender, (TextView)v.findViewById(R.id.tvStatus), msg );

        // avatar
        if(!isSender){
            // hien thi avatar cua doi tac
            generateAvatar((ImageView) v.findViewById(R.id.imAvatar), msg);
        }


        return v;
    }

    private View generateMsgPhoto(boolean isSender, MessageListElement msg, LayoutInflater inflater, ViewGroup viewGroup){
        View v;
        if(isSender)
            v = inflater.inflate(R.layout.layout_msg_photo_sender, viewGroup, false);
        else
            v = inflater.inflate(R.layout.layout_msg_receiver, viewGroup, false);

        // display photo
        ImageView im = (ImageView) v.findViewById(R.id.imMsgPhoto);
        // msg.message = url ( photo duoc lay tu device)

        String id = "";
        String path = "";
        for(Map.Entry<String, String> m : msg.message.entrySet()){
            path = m.getValue();
            id = m.getKey();
        }

        if( new File(path).exists()){
            Picasso.with(context)
                    .load(new File(path))
                    .into(im);
        }
        else{
            // load from firebase
            Picasso.with(context)
                    .load(path)
                    .into(im);
        }



        // message status
        //generateStatus(isSender, (TextView)v.findViewById(R.id.tvStatus), msg );

        // avatar
        /*if(!isSender){
            // hien thi avatar cua doi tac
            generateAvatar((ImageView) v.findViewById(R.id.imAvatar), msg);
        }*/



        return v;
    }

    private View generateMsgVoice(boolean isSender, MessageListElement msg, LayoutInflater inflater, ViewGroup viewGroup){
        View v;
        if(isSender)
            v = inflater.inflate(R.layout.layout_msg_voice_sender, viewGroup, false);
        else
            v = inflater.inflate(R.layout.layout_msg_voice_rec, viewGroup, false);

        return v;
    }

    private View generateMsgVideo(boolean isSender, MessageListElement msg, LayoutInflater inflater, ViewGroup viewGroup){
        View v;
        if(isSender)
            v = inflater.inflate(R.layout.layout_msg_voice_sender, viewGroup, false);
        else
            v = inflater.inflate(R.layout.layout_msg_voice_rec, viewGroup, false);

        return v;
    }

    private View generateMsgLink(boolean isSender, MessageListElement msg, LayoutInflater inflater, ViewGroup viewGroup){
        View v;
        if(isSender)
            v = inflater.inflate(R.layout.layout_msg_voice_sender, viewGroup, false);
        else
            v = inflater.inflate(R.layout.layout_msg_voice_rec, viewGroup, false);

        return v;
    }







    private OnEventListener mListener;

    public void setOnEventListener(OnEventListener listener) {
        mListener = listener;
    }

    public interface OnEventListener {
        public void onLoadComplete();

        public void onDirtyStateChanged(boolean dirty);
    }


    private void notifyDirtyStateChanged(boolean dirty) {

        if (mListener != null) {
            mListener.onDirtyStateChanged(dirty);
        }

    }


    public void add(MessageListElement element){
        if(msgID.contains(element.id))
            return;

        messagelist.add(element);
        msgID.add(element.id);

        /*if(element.isSender){
            // đặt tin nhắn trước đó last = false;
            messagelist.get(messagelist.size() - 2).setLast(false);

            // thêm tin nhắn và đặt tin nhắn này là last
            messagelist.get(messagelist.size() - 1).setLast(true);
        }*/

        notifyDataSetChanged();
    }

    public void modifyStatus(int i, MessageListElement.MESSAGE_STATUS status){
        MessageListElement element = messagelist.get(i);

        element.status = status;
        TextView tvStatus = (TextView) element.view.findViewById(R.id.tvStatus);
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(getStatusString(status));

        notifyDirtyStateChanged(true);

    }
}
