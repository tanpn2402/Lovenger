package com.tanpn.messenger.pagetransformer;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by phamt_000 on 11/22/16.
 */
public class RotateDownTransformer implements ViewPager.PageTransformer {
    private static final float ROT_MOD = -15f;
    @Override
    public void transformPage(View view, float position) {
        final float width = view.getWidth();
        final float height = view.getHeight();
        final float rotation = ROT_MOD * position * -1.25f;

        view.setPivotX(width * 0.5f);
        view.setPivotY(height);
        view.setRotation(rotation);
    }
}
