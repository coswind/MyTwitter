package io.github.coswind.mytwitter.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import io.github.coswind.mytwitter.utils.LogUtils;

/**
 * Created by coswind on 14-2-27.
 */
public class CardLinearLayout extends LinearLayout {
    private Paint paint = new Paint();
    private boolean isDrawMask = false;

    public CardLinearLayout(Context context) {
        this(context, null);
    }

    public CardLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isDrawMask) {
            canvas.drawRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom(), paint);
        }
        super.dispatchDraw(canvas);
    }

    public void setDrawMask(boolean isDrawMask) {
        this.isDrawMask = isDrawMask;
    }

    public void setMaskColor(int color) {
        paint.setColor(color);
    }
}
