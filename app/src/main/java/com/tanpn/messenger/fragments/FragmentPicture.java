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

        PhotoListAdapter adapter = new PhotoListAdapter(getContext(), photos);
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
