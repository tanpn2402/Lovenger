package com.tanpn.messenger.pagetransformer;

/**
 * Created by phamt_000 on 11/22/16.
 */
import android.support.v4.view.ViewPager;
import android.view.View;

public class FlipHorizontalTransformer implements ViewPager.PageTransformer {



    @Override
    public void transformPage(View page, float position) {
        final float rotation = 180f * position;

        page.setAlpha(rotation > 90f || rotation < -90f ? 0 : 1);
        page.setPivotX(page.getWidth() * 0.5f);
        page.setPivotY(page.getHeight() * 0.5f);
        page.setRotationY(rotation);
    }
}