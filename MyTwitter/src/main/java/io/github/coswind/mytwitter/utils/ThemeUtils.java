package io.github.coswind.mytwitter.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import io.github.coswind.mytwitter.R;

/**
 * Created by coswind on 14-3-6.
 */
public class ThemeUtils {
    public static Drawable getItemBackgroundSelector(Context context) {
        final TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        assert a != null;
        Drawable drawable = a.getDrawable(0);
        a.recycle();
        return drawable;
    }

    public static int getThemeColor(Context context) {
        final TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.accentColor});
        assert a != null;
        int color = a.getColor(0, R.color.light_blue);
        a.recycle();
        return color;
    }

    public static Drawable getActionBarBackground(Context context) {
        final TypedArray a = context.obtainStyledAttributes(null, new int[] { android.R.attr.background },
                android.R.attr.actionBarStyle, 0);
        final Drawable d = a.getDrawable(0);
        a.recycle();
        if (d instanceof LayerDrawable) {
            final Drawable colorLayer = ((LayerDrawable) d).findDrawableByLayerId(R.id.color_layer);
            if (colorLayer != null) {
                colorLayer.setColorFilter(getThemeColor(context), PorterDuff.Mode.MULTIPLY);
            }
        }
        return d;
    }
}
