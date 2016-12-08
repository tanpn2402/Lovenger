package com.tanpn.messenger.message;

import android.graphics.Bitmap;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by phamt_000 on 11/1/16.
 */
public class MessageListElement {

    public static enum MESSAGE_TYPE{
        TEXT,
        PHOTO,
        VOICE,
        VIDEO,
        LINK
    }

    public static enum MESSAGE_STATUS{
        DELIVERY,   // da gui
        RECEIVE,    // da nhan
        SENDING,    // dang gui
        SEEN,        // da xem
        ERROR       // loi
    }

    public String id;
    public String name;
    public Map<String, String> message; // chỉ chứa duy nhất 1 element
    public String avatar;
    public boolean isSender;
    public MESSAGE_STATUS status;
    public String receivedDate;
    public String sendDate;
    public MESSAGE_TYPE messageType;
    private boolean isLast;

    public View view;

    public MessageListElement(String _id, boolean _isSender, String _name, MESSAGE_TYPE type, Map<String, String> _message, String _avatar, MESSAGE_STATUS _status, String _receivedDate, String _sendDate){
        id = _id;
        isSender = _isSender;
        name = _name;
        message = new HashMap<>(_message);
        avatar = _avatar;
        messageType = type;
        status = _status;
        receivedDate = _receivedDate;
        sendDate = _sendDate;

        isLast = false;
    }

    public MessageListElement(String s){
        try {
            JSONObject obj = new JSONObject(s);


            id = obj.getString("id");
            isSender = true;
            name = obj.getString("name");

            JSONObject o = (JSONObject) obj.get("message");
            Map<String, String> m = new HashMap<>();
            m.put(o.getString("id"), o.getString("path"));

            message = new HashMap<>(m);
            avatar = null;
            messageType = MESSAGE_TYPE.values()[obj.getInt("type")];
            status = MESSAGE_STATUS.values()[obj.getInt("status")];
            receivedDate = obj.getString("sentDate");
            sendDate = obj.getString("receiveDate");

            isLast = false;


        } catch (JSONException e) {
            e.printStackTrace();
        }
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
