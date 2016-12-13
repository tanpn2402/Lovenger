package com.tanpn.messenger.setting;

/**
 * Created by phamt_000 on 11/26/16.
 */
public class OnGroupChange {
    public interface onGroupChangeListener{
        void onChanged();
}

    public static OnGroupChange mInstance;
    private onGroupChangeListener listener;
    private String groupID;

    public OnGroupChange(){}

    public static OnGroupChange getInstance(){
        if(mInstance == null)
            mInstance = new OnGroupChange();

        return mInstance;
    }

    public void setListener(onGroupChangeListener _l){
        listener = _l;
    }

    public void changeGroup(String id){
        if(listener != null){
            groupID = id;
            notifyGroupChange();
        }
    }

    public String getGroupID(){
        return groupID;
    }

    public void notifyGroupChange(){
        this.listener.onChanged();
    }
}
