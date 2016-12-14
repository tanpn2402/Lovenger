package com.tanpn.messenger.setting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanpn.messenger.R;
import com.tanpn.messenger.utils.PrefUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupManager extends AppCompatActivity {

    ExpandableListView expandableListView;
    MyExpandableAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    private PrefUtil pref;

    private ImageButton ibtBack;

    private DatabaseReference userRef;

    private void init(){
        userRef = FirebaseDatabase.getInstance().getReference("user");

        ibtBack = (ImageButton) findViewById(R.id.ibtBack);
        ibtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manager);

        init();



        expandableListView = (ExpandableListView) findViewById(R.id.listGroup);


        pref = new PrefUtil(this);
        String[] groupList = pref.getString(R.string.pref_key_groups).split("\\|");
        String currentGroup = pref.getString(R.string.pref_key_current_groups);

        //expandableListDetail =
        expandableListDetail = new HashMap<>();

        /**
         * function: có 4 loại function khác nhau:
         *  -   dành riêng cho default group: chỉ Tham gia, nếu đang tham gia thì không có gì cả
         *  -   dành cho các group khác: nếu đang tham gia thì chỉ có rời nhóm, nếu chưa tham gia thì có cả 2
         * */
        List<String> function1 = new ArrayList<String>();   // dành cho group khác và k phải là current
        function1.add("Tham gia");
        function1.add("Rời nhóm");

        List<String> function2 = new ArrayList<String>(); // dành group khác và đang là current group
        function2.add("Rời nhóm");

        List<String> function3 = new ArrayList<String>();   // dành cho default mà k phải current
        function3.add("Tham gia");

        List<String> function4 = new ArrayList<String>();   // dành cho default và đang là current



        if(groupList[0].equals(currentGroup)){
            expandableListDetail.put(groupList[0], function4);
        }
        else{
            expandableListDetail.put(groupList[0], function3);
        }

        if(groupList.length > 1){
            for(int i = 1; i< groupList.length; i++){
                if(groupList[i].equals(currentGroup)){
                    expandableListDetail.put(groupList[i], function2);
                }
                else{
                    expandableListDetail.put(groupList[i], function1);
                }
            }
        }

        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());



        expandableListAdapter = new MyExpandableAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                String groupID = expandableListTitle.get(i); // lay id nhóm
                String func = expandableListDetail.get(groupID).get(i1);    // lay id function

                Log.i("group", groupID + " --->  " + func);

                if(func.equals("Tham gia")){
                    Log.i("group", groupID + " --->  than gia");

                    joinGroup(groupID);
                }
                else if(func.equals("Rời nhóm")){
                    Log.i("group", groupID + " --->  roi nhom");
                    leaveGroup(groupID);
                }
                return false;
            }
        });
    }

    private void joinGroup(String id){
        pref.put(R.string.pref_key_current_groups, id);
        pref.apply();

        expandableListView.refreshDrawableState();
        for(int i =0; i< expandableListView.getChildCount(); i++){
            expandableListView.collapseGroup(i);
        }
        /**
         * local broadcast
         * */
        Intent intent = new Intent("CHANGE_GROUP");
        // You can also include some extra data.
        intent.putExtra("message", id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void leaveGroup(final String Group_id){
        String g = pref.getString(R.string.pref_key_groups);
        final String newG =  g.replaceAll("\\|" + Group_id, "");


        // sua doi tren firebase
        userRef.child(pref.getString(R.string.pref_key_uid))
                .child("groups")
                .setValue(newG)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // thong bao
                        //snackbar.setText("thành công").setDuration(Snackbar.LENGTH_SHORT).show();

                        // update pref
                        pref.put(R.string.pref_key_groups, newG);
                        pref.apply();

                        // update list
                        expandableListAdapter.delete(Group_id);
                        expandableListAdapter.notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //snackbar.setText("thất bại").setDuration(Snackbar.LENGTH_SHORT).show();

                    }
                });



    }
}
