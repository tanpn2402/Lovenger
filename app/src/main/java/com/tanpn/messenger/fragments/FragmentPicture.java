package com.tanpn.messenger.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.tanpn.messenger.R;
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
                R.drawable.ic_birthday_gray,
                R.drawable.ic_birthday_gray,
                R.drawable.ic_birthday_gray,
                R.drawable.ic_birthday_gray,
                R.drawable.ic_birthday_gray


        };

        PhotoListAdapter adapter = new PhotoListAdapter(getContext(), photos);
        photoList.setAdapter(adapter);




        return v;
    }

}
