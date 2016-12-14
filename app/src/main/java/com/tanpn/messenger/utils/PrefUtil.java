package com.tanpn.messenger.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by phamt_000 on 11/20/16.
 *
 * Lấy các giá trị lưu lại, các thông số cài đặt, sửa đổi các giá trị đó
 */
public class PrefUtil {

    public static final String PREF_FILE_DEFAULT = "com.tanpn.messenger.prefs.default";
    public static final String PREF_USER_ACCOUNT = "com.tanpn.messenger.pref.user_account";

    private final Context context;
    private final SharedPreferences prefs;

    //public static SharedPreferences getPrefUserAccount(){
    //    return getSharedPreferences
    //}

    SharedPreferences.Editor editor;    // được dùng để lưu giá trị

    public PrefUtil(Context c) {
        context = c;
        prefs = context.getSharedPreferences(
                PREF_FILE_DEFAULT,
                Context.MODE_PRIVATE);
    }

    SharedPreferences.Editor generateEditor() {
        if (editor == null) {
            editor = prefs.edit();
        }
        return editor;
    }

    public SharedPreferences.Editor put(int keyResId, Object value) {
        /**
         * Lưu giá trị
         * */
        final String key = context.getString(keyResId);
        if (key == null) {
            throw new IllegalArgumentException(
                    "No resource matched key resource id");
        }

        final SharedPreferences.Editor editor = generateEditor();

        if (value instanceof String)
            editor.putString(key, (String) value);

        else if (value instanceof Integer)
            editor.putInt(key, (Integer) value);

        else if (value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);

        else if (value instanceof Float)
            editor.putFloat(key, (Float) value);

        else if (value instanceof Long)
            editor.putLong(key, (Long) value);
        else
            throw new IllegalArgumentException("Unknown data type");

        return editor;
    }

    /**
     lưu string được lấy từ file strings.xml
     (Thông qua context.getString)
     */
    public SharedPreferences.Editor putString(int keyResId, int valueResId) {
        final SharedPreferences.Editor editor = generateEditor();

        editor.putString(context.getString(keyResId),
                context.getString(valueResId));

        return editor;
    }


    // luu lai gia tri cua mEditor
    @SuppressLint("NewApi")
    public static void apply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            editor.commit();
        } else {
            editor.apply();
        }
    }
    public void apply() {
        apply(generateEditor());
        editor = null;
    }



    // get function

    // ham lay string tu preferences
    public String getString(int keyResId) {
        return prefs.getString(context.getString(keyResId), null);
    }

    /**
     lấy string với key = keyResId
     nếu k có giá trị nào có key bằng nó thì lấy giá trị mặc định có key = defResId
     */
    public String getString(int keyResId, int defResId) {
        final String key = context.getString(keyResId);
        return (prefs.contains(key)) ? prefs.getString(key, null) : context
                .getString(defResId);
    }
    /**
     lấy string với key = keyResId
     nếu k có giá trị nào có key bằng nó thì lấy giá trị mặc định = defValue
     */
    public String getString(int keyResId, String defValue) {
        return prefs.getString(context.getString(keyResId), defValue);
    }


    public boolean getBoolean(int keyRes, boolean defValue){
        return prefs.getBoolean(context.getString(keyRes), defValue);
    }

}
