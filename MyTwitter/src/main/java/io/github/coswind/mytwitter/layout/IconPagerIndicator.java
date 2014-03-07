package io.github.coswind.mytwitter.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.adapter.TabIconPagerAdapter;

/**
 * Created by coswind on 14-3-6.
 */
public class IconPagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    private LayoutInflater inflater;

    private ViewPager viewPager;

    public IconPagerIndicator(Context context) {
        this(context, null);
    }

    public IconPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflater = LayoutInflater.from(context);
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.setOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        removeAllViews();
        TabIconPagerAdapter iconPagerIndicator = (TabIconPagerAdapter) viewPager.getAdapter();
        int count = iconPagerIndicator.getCount();
        for (int i = 0; i < count; i++) {
            addTab(iconPagerIndicator.getIcon(i), i);
        }
        requestLayout();
        setCurrentItem(0);
    }

    public void addTab(Drawable drawable, int position) {
        View view = inflater.inflate(R.layout.tab_vpi, null);
        view.setFocusable(true);
        view.setTag(position);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItem((Integer)v.getTag());
            }
        });
        ImageView iconImage = (ImageView) view.findViewById(R.id.icon);
        iconImage.setImageDrawable(drawable);
        addView(view, new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
    }

    public void setCurrentItem(int index) {
        viewPager.setCurrentItem(index);
        for (int i = 0, len = getChildCount(); i < len; i++) {
            View child = getChildAt(i);
            boolean isSelect = i == index;
            child.setSelected(isSelect);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
