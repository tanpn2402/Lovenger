package com.tanpn.messenger.setting;

import com.tanpn.messenger.utils.utils;

import java.util.Calendar;

/**
 * Created by phamt_000 on 11/25/16.
 */
public class InviteItem {
    public String id;
    public String name;
    public String userPhotoName;
    public Calendar datatime;

    public InviteItem(String _name, String _userPhoto, Calendar _datatime){
        id = utils.generateInviteId();
        name = _name;
        userPhotoName = _userPhoto;
        datatime = _datatime;
    }

    public InviteItem(String _id, String _name, String _userPhoto, Calendar _datatime){
        id = _id;
        name = _name;
        userPhotoName = _userPhoto;
        datatime = _datatime;
    }

}
