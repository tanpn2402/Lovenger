package com.tanpn.messenger.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tanpn.messenger.R;
import com.tanpn.messenger.photo.ActivityViewPhoto;
import com.tanpn.messenger.photo.PhotoListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPicture extends Fragment {


    public FragmentPicture() {
        // Required empty public constructor
    }


    private GridView photoList;

    private final String[] photo = new String[]{
            "https://firebasestorage.googleapis.com/v0/b/messenger-d08e4.appspot.com/o/tcu7xozdgqxoxmm5ieyh-1473826982014.jpg?alt=media&token=c4f072a2-2fec-4c68-9673-0c50202c4e83",
            "https://firebasestorage.googleapis.com/v0/b/messenger-d08e4.appspot.com/o/48.png?alt=media&token=389b9a15-20d4-4aed-a756-4bb42cf370f4",
            "https://firebasestorage.googleapis.com/v0/b/messenger-d08e4.appspot.com/o/photo%2FC360_2016-02-08-10-45-46-090.jpg?alt=media&token=8c1295e6-20c4-49b1-8b82-497bfbcc07c6"

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_picture, container, false);

        photoList = (GridView) v.findViewById(R.id.photoList);

        Integer[] photos = new Integer[]{
                1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
                1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1


        };

        PhotoListAdapter adapter = new PhotoListAdapter(getContext(), photo);
        photoList.setAdapter(adapter);


        photoList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        photoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent viewPhoto = new Intent(getContext(), ActivityViewPhoto.class);
                startActivity(viewPhoto);
            }
        });

        return v;
    }

}
