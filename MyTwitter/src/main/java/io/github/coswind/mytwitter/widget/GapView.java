package io.github.coswind.mytwitter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import io.github.coswind.mytwitter.R;

/**
 * Created by coswind on 14-3-7.
 */
public class GapView extends FrameLayout {
    public GapView(Context context) {
        this(context, null);
    }

    public GapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.card_gap, this);
    }
}
