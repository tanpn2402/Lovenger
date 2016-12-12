package com.tanpn.messenger.setting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.tanpn.messenger.R;
import com.tanpn.messenger.utils.PrefUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupManager extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    private PrefUtil pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manager);

        expandableListView = (ExpandableListView) findViewById(R.id.listGroup);


        //pref = new PrefUtil(this);
        //String[] groupList = pref.getString(R.string.pref_key_groups).split("\\|");
        String groupList[] = new String[]{
                "123",
                "432"
        };
        //expandableListDetail =
        expandableListDetail = new HashMap<>();
        List<String> function = new ArrayList<String>();
        function.add("Tham gia");
        function.add("Rời nhóm");
        for(String s : groupList){
            // s = id cua nhom
            expandableListDetail.put(s, function);
        }

        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());



        expandableListAdapter = new MyExpandableAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                String groupID = expandableListTitle.get(i); // lay id nhóm
                String func = expandableListDetail.get(groupID).get(i1);    // lay id function
                return false;
            }
        });
    }
}
