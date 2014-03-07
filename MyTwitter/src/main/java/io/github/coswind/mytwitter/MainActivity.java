package io.github.coswind.mytwitter;

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import io.github.coswind.mytwitter.adapter.TabIconPagerAdapter;
import io.github.coswind.mytwitter.fragment.BaseFragment;
import io.github.coswind.mytwitter.fragment.HomeTimeLineFragment;
import io.github.coswind.mytwitter.layout.IconPagerIndicator;

public class MainActivity extends BaseActivity {
    private SlidingMenu slidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        slidingMenu.setTouchmodeMarginThreshold(getResources().getDimensionPixelSize(R.dimen.padding_3));
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.left_sidebar_shadow);
        slidingMenu.setSecondaryShadowDrawable(R.drawable.right_sidebar_shadow);
        slidingMenu.setBehindWidthRes(R.dimen.sidebar_width);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        slidingMenu.setMenu(R.layout.left_sidebar);
        slidingMenu.setSecondaryMenu(R.layout.right_sidebar);

        getActionBar().setCustomView(R.layout.custom_action_bar);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new TabIconPagerAdapter(getFragmentManager()) {
            private TabProvider[] tabProviders = new TabProvider[] {
                    new TabProvider(new HomeTimeLineFragment(), getResources().getDrawable(R.drawable.ic_tab_home)),
                    new TabProvider(new BaseFragment(), getResources().getDrawable(R.drawable.ic_tab_mention)),
                    new TabProvider(new BaseFragment(), getResources().getDrawable(R.drawable.ic_tab_trends)),
                    new TabProvider(new BaseFragment(), getResources().getDrawable(R.drawable.ic_tab_message))
            };
            @Override
            public int getCount() {
                return tabProviders.length;
            }
            @Override
            public Fragment getItem(int position) {
                return tabProviders[position].fragment;
            }
            @Override
            public Drawable getIcon(int position) {
                return tabProviders[position].drawable;
            }
        });

        View view = getActionBar().getCustomView();
        IconPagerIndicator indicator = (IconPagerIndicator) view.findViewById(R.id.pager_indicator);
        indicator.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.card_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.retweet) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class TabProvider {
        Fragment fragment;
        Drawable drawable;
        private TabProvider(Fragment fragment, Drawable drawable) {
            this.fragment = fragment;
            this.drawable = drawable;
        }
    }
}
