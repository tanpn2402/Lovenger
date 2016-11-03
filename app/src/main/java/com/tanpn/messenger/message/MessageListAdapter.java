package com.tanpn.messenger.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tanpn.messenger.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamt_000 on 11/1/16.
 */
public class MessageListAdapter extends BaseAdapter {

    private List<MessageListElement> messagelist;
    private Context context;
    public MessageListAdapter(Context _context, List<MessageListElement> list){
        context = _context;
        messagelist = new ArrayList<>(list);
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
        return 2;
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
        View v =
                inflater.inflate(R.layout.layout_msg_sender, viewGroup, false);


        TextView tvMessage = (TextView) v.findViewById(R.id.tvMessage);
        tvMessage.setText(messagelist.get(i).message);

        // tin nhắn của sender chỉ hiển thị status khi đó là tin nhắn cuối cùng
        // tuy nhiên những tin nhắn trước đó cũng phải hiển thị status nếu chúng chưa được nhận

        TextView tvStatus = (TextView) v.findViewById(R.id.tvStatus);

        if(messagelist.get(i).status.equals(context.getString(R.string.message_status_sending))){
            // nếu đang gửi thì hiển thị
            tvStatus.setText(context.getString(R.string.message_status_sending));
        }
        else{
            // nếu không đang gửi

            if(messagelist.get(i).isLast()){
                // nếu là tin nhắn cuối cùng thì phải hiện thị rõ status
                tvStatus.setText(messagelist.get(i).status);
            }else{
                tvStatus.setVisibility(View.GONE);
            }
        }



        messagelist.get(i).view = v;
        return v;
    }

    private View createMessageReceiver(int i, View view, ViewGroup viewGroup){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v =
                inflater.inflate(R.layout.layout_msg_receiver, viewGroup, false);

        if(messagelist.get(i).avatar != null){
            ImageView imAvatar = (ImageView) v.findViewById(R.id.imAvatar);
            imAvatar.setImageBitmap(messagelist.get(i).avatar);
        }
        else{
            ImageView imAvatar = (ImageView) v.findViewById(R.id.imAvatar);
            imAvatar.setVisibility(View.GONE);
        }

        TextView tvMessage = (TextView) v.findViewById(R.id.tvMessage);
        tvMessage.setText(messagelist.get(i).message);

        // tin nhắn từ receiver thì k có status, chỉ hiển thị status khi người dùng click vào message đó
        TextView tvStatus = (TextView) v.findViewById(R.id.tvStatus);
        tvStatus.setVisibility(View.GONE);

        messagelist.get(i).view = v;
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
        messagelist.add(element);

        if(element.isSender){
            // đặt tin nhắn trước đó last = false;
            messagelist.get(messagelist.size() - 2).setLast(false);

            // thêm tin nhắn và đặt tin nhắn này là last
            messagelist.get(messagelist.size() - 1).setLast(true);
        }

        notifyDataSetChanged();
    }

    public void modifyStatus(int i, String status){
        MessageListElement element = messagelist.get(i);

        element.status = status;
        TextView tvStatus = (TextView) element.view.findViewById(R.id.tvStatus);
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(status);

        notifyDirtyStateChanged(true);

    }
}
