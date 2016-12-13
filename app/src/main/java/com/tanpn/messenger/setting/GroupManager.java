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

    public interface onGroupChange{
        void onChange(String data); // tra ve group id duoc chon
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manager);

        expandableListView = (ExpandableListView) findViewById(R.id.listGroup);


        pref = new PrefUtil(this);
        String[] groupList = pref.getString(R.string.pref_key_groups).split("\\|");
        String currentGroup = pref.getString(R.string.pref_key_current_groups);

        //expandableListDetail =
        expandableListDetail = new HashMap<>();

        /**
         * function: có 3 loại function khác nhau:
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
                return false;
            }
        });
    }
}
