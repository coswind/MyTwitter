package io.github.coswind.mytwitter.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.utils.LogUtils;
import io.github.coswind.mytwitter.utils.ThemeUtils;

/**
 * Created by coswind on 14-2-27.
 */
public class CardLinearLayout extends LinearLayout {
    private Paint paint = new Paint();
    private boolean isDrawMask = false;
    private Drawable itemSelector;

    public CardLinearLayout(Context context) {
        this(context, null);
    }

    public CardLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        setItemSelector(ThemeUtils.getItemBackgroundSelector(context));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isDrawMask) {
            canvas.drawRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom(), paint);
        }
        super.dispatchDraw(canvas);
        if (itemSelector != null) {
            itemSelector.draw(canvas);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final int[] state = getDrawableState();
        if (itemSelector != null) {
            itemSelector.setState(state);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();
        final int l = paddingLeft, t = paddingTop, r = w - paddingRight, b = h - paddingBottom;
        itemSelector.setBounds(l, t, r, b);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == itemSelector;
    }

    private void setItemSelector(Drawable drawable) {
        preSetDrawable(itemSelector);
        itemSelector = drawable;
        if (itemSelector != null) {
            itemSelector.setAlpha(0x80);
        }
        postSetDrawable(itemSelector);
    }

    private void postSetDrawable(final Drawable curr) {
        if (curr != null) {
            if (curr.isStateful()) {
                curr.setState(getDrawableState());
            }
            curr.setCallback(this);
        }
    }

    private void preSetDrawable(final Drawable prev) {
        if (prev != null) {
            unscheduleDrawable(prev);
            prev.setCallback(null);
        }
    }

    public void setDrawMask(boolean isDrawMask) {
        this.isDrawMask = isDrawMask;
    }

    public void setMaskColor(int color) {
        paint.setColor(color);
    }
}
