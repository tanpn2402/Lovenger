package com.tanpn.messenger.setting;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanpn.messenger.R;
import com.tanpn.messenger.fragment_dialog.InviteDialog;
import com.tanpn.messenger.utils.PrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InviteManager extends AppCompatActivity implements InviteDialog.onAcceptInvite {

    private ListView listInvites;
    private InviteAdapter inviteAdapter;

    private PrefUtil prefUtil;

    private InviteDialog inviteDialog;

    private void init(){
        prefUtil = new PrefUtil(this);


        listInvites = (ListView) findViewById(R.id.listInvites);
        inviteAdapter = new InviteAdapter(this);
        listInvites.setAdapter(inviteAdapter);

        listInvites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InviteAdapter invite = (InviteAdapter) adapterView.getAdapter();
                InviteItem iten = (InviteItem)invite.getItem(i);

                InviteDialog inviteDialog =  new InviteDialog();
                inviteDialog.setInviteItem(iten);
                inviteDialog.show(getSupportFragmentManager(), "dialog");

            }
        });


    }

    /**
     * Khoi tao su kien addChild cua Firebase de lay nhung loi moi
     * */

    /**
     * cau truc cua 1 loi de nghi
     * - id
     * - name
     * - userPhoto
     * - datetime
     *
     *
     * */
    private DatabaseReference userRef;
    private FirebaseDatabase root;
    private void initFirebase(){
        root = FirebaseDatabase.getInstance();
        userRef = root.getReference("user");
        userRef.child(prefUtil.getString(R.string.pref_key_uid)).child("invites").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // doc json
                try {
                    JSONObject obj = new JSONObject(dataSnapshot.getValue().toString());

                    String id = obj.getString("id");
                    String name = obj.getString("userName");
                    String photo = obj.getString("userPhoto");
                    String datetime = obj.getString("datetime");

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    Calendar c = Calendar.getInstance();
                    c.setTime(sdf.parse(datetime));

                    inviteAdapter.add(new InviteItem(id, name, photo, c));
                    inviteAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
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
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_manager);


        init();
        initFirebase();
    }

    @Override
    public void onAccept(final InviteItem data) {
        // group = data
        final String g = prefUtil.getString(R.string.pref_key_groups) + "|" + data.id;


        // luu len firebase

        userRef.child(prefUtil.getString(R.string.pref_key_uid))
                .child("groups")
                .setValue(g)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        inviteAdapter.delete(data);
                        inviteAdapter.notifyDataSetChanged();

                        // xoa tren firebase
                        userRef.child(prefUtil.getString(R.string.pref_key_uid))
                                .child("invites")
                                .child(data.id)
                                .removeValue();

                        // user / <uid>  / invites / <group id>   -> remove

                        // them vao pref groups
                        prefUtil.put(R.string.pref_key_groups, g);
                        prefUtil.apply();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
