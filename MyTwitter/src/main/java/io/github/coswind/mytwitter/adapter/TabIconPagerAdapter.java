package io.github.coswind.mytwitter.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;

import io.github.coswind.mytwitter.support.FragmentPagerAdapter;

/**
 * Created by coswind on 14-3-6.
 */
public class TabIconPagerAdapter extends FragmentPagerAdapter {
    public TabIconPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    public Drawable getIcon(int position) {
        return null;
    }
}
