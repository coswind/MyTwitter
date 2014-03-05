package io.github.coswind.mytwitter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by coswind on 14-3-5.
 */
public class PreviewImageLayout extends FrameLayout {
    public PreviewImageLayout(Context context) {
        this(context, null);
    }

    public PreviewImageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewImageLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec), height = width / 2;
        int hMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, hMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
