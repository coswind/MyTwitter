package io.github.coswind.mytwitter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.negusoft.holoaccent.activity.AccentActivity;

import io.github.coswind.mytwitter.utils.ThemeUtils;

/**
 * Created by coswind on 14-3-3.
 */
public class BaseActivity extends AccentActivity {
    private boolean isVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance(this).addActivity(this);
        getActionBar().setBackgroundDrawable(ThemeUtils.getActionBarBackground(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance(this).removeActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
