package com.tanpn.messenger.pagetransformer;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by phamt_000 on 11/22/16.
 */
public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    public ZoomOutPageTransformer(){}

    private Button btn;

    private final int MAX_DISTANCE = 30;
    public ZoomOutPageTransformer(View v){
        btn = (Button) v;
    }

    private float x = 0;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
                //btn.setTranslationX(MAX_DISTANCE);


            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
                //btn.setTranslationX(-MAX_DISTANCE);
            }


            //x += position;

            Log.i("cale", position + "");
            //btn.setScaleX( Math.max(MIN_SCALE, Math.abs(position)));
            //btn.setScaleY(Math.max(MIN_SCALE, Math.abs(position)));
            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}