package com.tanpn.messenger.message;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by phamt_000 on 11/1/16.
 */
public class MessageListElement {

    public String name;
    public String message;
    public Bitmap avatar;
    public boolean isSender;
    public String status;
    public String receivedDate;
    public String sendDate;
    private boolean isLast;

    public View view;

    public MessageListElement(boolean _isSender, String _name, String _message, Bitmap _avatar, String _status, String _receivedDate, String _sendDate){
        isSender = _isSender;
        name = _name;
        message = _message;
        avatar = _avatar;

        status = _status;
        receivedDate = _receivedDate;
        sendDate = _sendDate;

        isLast = false;
    }

    public void setLast(boolean last){
        // sender thì mới xét status của message
        if( isSender){
            isLast = last;
        }
    }

    public boolean isLast(){
        return isLast;
    }
    /*MessageListElement(boolean _isSender, String _name, String _message){
        isSender = _isSender;
        name = _name;
        message = _message;
        avatar = null;
    }*/
}
