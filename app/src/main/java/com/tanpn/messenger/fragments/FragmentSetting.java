package com.tanpn.messenger.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tanpn.messenger.R;
import com.tanpn.messenger.photo.SharePhoto;
import com.tanpn.messenger.setting.AccountManager;
import com.tanpn.messenger.setting.GroupManager;
import com.tanpn.messenger.setting.InviteManager;
import com.tanpn.messenger.utils.PrefUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetting extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {


    public FragmentSetting() {
        // Required empty public constructor
    }

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private SwitchPreference powerMode;
    private SwitchPreference sound;
    private SwitchPreference vibration;

    private Preference accountManager;
    private Preference groupManager;
    private Preference inviteManager;

    private Preference sharePhoto;
    private Preference shareVoice;

    private ListPreference menuEffect;
    private ListPreference photoEffect;

    private PrefUtil prefUtil;




    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        addPreferencesFromResource(R.xml.settings);


        PreferenceManager pm = getPreferenceManager();
        pm.setSharedPreferencesName(PrefUtil.PREF_FILE_DEFAULT);
        pm.setSharedPreferencesMode(Context.MODE_PRIVATE);


        pref = pm.getSharedPreferences();
        editor = pm.getSharedPreferences().edit();

        init();
    }
/**
 * list preferences co 2 thuoc tinh quan trong la:
 *  + entries:
 *  + entryValues:
 *
 * ex:
 * android:entries="@array/setting_effect"
 * android:entryValues="@array/setting_effect_val"
 * */
    private void init(){
        powerMode = (SwitchPreference) findPreference(getString(R.string.pref_key_power_saver_mode));
        sound = (SwitchPreference) findPreference(getString(R.string.pref_key_sound));
        vibration = (SwitchPreference) findPreference(getString(R.string.pref_key_vibration));

        accountManager = (Preference) findPreference(getString(R.string.pref_key_account_manager));
        groupManager = (Preference) findPreference(getString(R.string.pref_key_group_manager));
        inviteManager = (Preference) findPreference(getString(R.string.pref_key_invite_manager));

        sharePhoto = (Preference) findPreference(getString(R.string.pref_key_share_photo));
        shareVoice = (Preference) findPreference(getString(R.string.pref_key_share_voice));

        menuEffect = (ListPreference) findPreference(getString(R.string.pref_key_main_menu_effect));
        photoEffect = (ListPreference) findPreference(getString(R.string.pref_key_photo_view_effect));

        prefUtil = new PrefUtil(getContext());


        pref.registerOnSharedPreferenceChangeListener(this);
        powerMode.setOnPreferenceChangeListener(this);
        sound.setOnPreferenceChangeListener(this);
        vibration.setOnPreferenceChangeListener(this);

        menuEffect.setOnPreferenceChangeListener(this);
        photoEffect.setOnPreferenceChangeListener(this);

        accountManager.setOnPreferenceClickListener(this);
        groupManager.setOnPreferenceClickListener(this);
        inviteManager.setOnPreferenceClickListener(this);

        sharePhoto.setOnPreferenceClickListener(this);
        shareVoice.setOnPreferenceClickListener(this);

        initValues();
    }


    private void initValues(){
        powerMode.setChecked(prefUtil.getBoolean(R.string.pref_key_power_saver_mode, true));
        sound.setChecked(prefUtil.getBoolean(R.string.pref_key_sound, true));
        vibration.setChecked(prefUtil.getBoolean(R.string.pref_key_vibration, true));

        //menuEffect.setDefaultValue(prefUtil.getString(R.string.pref_key_main_menu_effect, "Default"));
        //photoEffect.setDefaultValue(prefUtil.getString(R.string.pref_key_photo_view_effect, "Default"));

        CharSequence[] s = menuEffect.getEntries();
        menuEffect.setSummary(s[Integer.parseInt(prefUtil.getString(R.string.pref_key_main_menu_effect, "1")) - 1 ]);
        photoEffect.setSummary(s[Integer.parseInt(prefUtil.getString(R.string.pref_key_photo_view_effect, "1")) - 1 ]);

        menuEffect.setValue(prefUtil.getString(R.string.pref_key_main_menu_effect, "Default"));
        photoEffect.setValue(prefUtil.getString(R.string.pref_key_photo_view_effect, "Default"));


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container,
                savedInstanceState);
    }

    @Override
    public boolean onPreferenceTreeClick() {
        return false;
    }


    // When user clicks preference and it has still to be saved
    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        // ap dung vs checkbox + list
        String key = preference.getKey();

        String k1 = getString(R.string.pref_key_power_saver_mode);
        String k2 = getString(R.string.pref_key_sound);
        String k3 = getString(R.string.pref_key_vibration);

        String k4 = getString(R.string.pref_key_photo_view_effect);
        String k5 = getString(R.string.pref_key_main_menu_effect);

        OnSettingChanged onSettingChanged = (OnSettingChanged) getActivity();
        if(key.equals(k1)){
            Log.i("pref", o + "");
            editor.putBoolean(k1, (Boolean)o);
            editor.commit();

            onSettingChanged.onChanged(R.string.pref_key_power_saver_mode);

            if(powerMode.isChecked() == false)
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Chế độ tiết kiệm pin")
                        .setMessage("Bạn sẽ chỉ nhận được thông báo khi bạn mở ứng dụng.")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .create().show();
            }
        }
        else if(key.equals(k2)){
            Log.i("pref", o + "");
            editor.putBoolean(k2, (Boolean)o);
            editor.commit();
            onSettingChanged.onChanged(R.string.pref_key_sound);
        }
        else if(key.equals(k3)){
            Log.i("pref", o + "");
            editor.putBoolean(k3, (Boolean)o);
            editor.commit();
            onSettingChanged.onChanged(R.string.pref_key_vibration);

        }
        else if(key.equals(k4)){
            // photo view effect
            // luu vao pref la gia tri entryValues: ( so thu tu  1 2 3 4 ..)
            Log.i("pref", o.toString());
            editor.putString(k4, o.toString());
            editor.commit();
            photoEffect.setSummary(photoEffect.getEntries()[Integer.parseInt(o.toString()) - 1]);
            onSettingChanged.onChanged(R.string.pref_key_photo_view_effect);

        }
        else if(key.equals(k5)){
            // menu effect
            Log.i("pref", o.toString());
            editor.putString(k5, o.toString());
            editor.commit();
            menuEffect.setSummary(menuEffect.getEntries()[Integer.parseInt(o.toString()) - 1]);
            onSettingChanged.onChanged(R.string.pref_key_main_menu_effect);
        }

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        String k1 = getString(R.string.pref_key_account_manager);
        String k2 = getString(R.string.pref_key_group_manager);
        String k3 = getString(R.string.pref_key_invite_manager);
        String k4 = getString(R.string.pref_key_share_photo);
        String k5 = getString(R.string.pref_key_share_voice);

        if(key.equals(k1)){
            Intent in = new Intent(getContext(), AccountManager.class);
            startActivity(in);
        }
        else if(key.equals(k2)){
            Intent in = new Intent(getContext(), GroupManager.class);
            startActivity(in);

        }
        else if(key.equals(k3)){
            Intent in = new Intent(getContext(), InviteManager.class);
            startActivity(in);
        }
        else if(key.equals(k4)){
            // share photo activity
            Intent in = new Intent(getContext(), SharePhoto.class);
            startActivity(in);

        }
        else if(key.equals(k5)){
            // voice
        }
        return false;
    }


    // When the preference changed on disk
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }


    public interface OnSettingChanged{
        void onChanged(int resId);
    }
}
