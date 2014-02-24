package io.github.coswind.mytwitter.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import io.github.coswind.mytwitter.utils.LogUtils;

/**
 * Created by coswind on 14-2-21.
 */
public class ThemedTextView extends TextView {
    public ThemedTextView(Context context) {
        this(context, null);
    }

    public ThemedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface typeface = Typeface.create("sans-serif-light", getTypeface() == null ? Typeface.NORMAL : getTypeface().getStyle());
        setTypeface(typeface);
    }

    public ThemedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface typeface = Typeface.create("sans-serif-light", getTypeface() == null ? Typeface.NORMAL : getTypeface().getStyle());
        setTypeface(typeface);
    }
}
