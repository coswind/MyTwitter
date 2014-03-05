package io.github.coswind.mytwitter.fragment;

import android.app.Fragment;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.github.coswind.mytwitter.R;

/**
 * Created by coswind on 14-2-25.
 */
public class PullToRefreshFragment extends Fragment implements View.OnTouchListener {
    private ListView listView;
    private SmoothProgressBar upProgressBar;
    private SmoothProgressBar bottomProgressBar;

    private boolean isRefreshingUp;
    private boolean isRefreshingBottom;

    private boolean isDragFromUp;
    private boolean isDragFromBottom;

    private float initialMotionY;
    private float initialMotionX;
    private float lastMotionY;

    private float touchSlop;

    public final static float MAX_PULL_DISTANCE = 300.0f;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        touchSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
    }

    public void setListView(ListView listView) {
        this.listView = listView;
        this.listView.setOnTouchListener(this);
    }

    public void setUpProgressBar(SmoothProgressBar upProgressBar) {
        this.upProgressBar = upProgressBar;
        applyProgressBarSettings(upProgressBar);
    }

    public void setBottomProgressBar(SmoothProgressBar bottomProgressBar) {
        this.bottomProgressBar = bottomProgressBar;
        applyProgressBarSettings(bottomProgressBar);
    }

    protected void onRefreshUpEnd() {
        isRefreshingUp = false;
        if (this.upProgressBar != null) {
            this.upProgressBar.setVisibility(View.INVISIBLE);
            this.upProgressBar.setProgress(0);
            this.upProgressBar.setIndeterminate(false);
        }
    }

    protected void onRefreshingBottomEnd() {
        isRefreshingBottom = false;
        if (this.bottomProgressBar != null) {
            this.bottomProgressBar.setVisibility(View.INVISIBLE);
            this.bottomProgressBar.setProgress(0);
            this.bottomProgressBar.setIndeterminate(false);
        }
    }

    protected void onRefreshingUp() {
        isRefreshingUp = true;
        if (this.upProgressBar != null) {
            this.upProgressBar.setProgress(100);
            this.upProgressBar.setIndeterminate(true);
            this.upProgressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void onRefreshingBottom() {
        isRefreshingBottom = true;
        if (this.bottomProgressBar != null) {
            this.bottomProgressBar.setProgress(100);
            this.bottomProgressBar.setIndeterminate(true);
            this.bottomProgressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void onPullUp(float progress) {
        if (this.upProgressBar != null) {
            this.upProgressBar.setProgress(Math.round(progress * upProgressBar.getMax()));
            this.upProgressBar.setIndeterminate(false);
            this.upProgressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void onPullBottom(float progress) {
        if (this.bottomProgressBar != null) {
            this.bottomProgressBar.setProgress(Math.round(progress * bottomProgressBar.getMax()));
            this.bottomProgressBar.setIndeterminate(false);
            this.bottomProgressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void onPullBottomCancel() {
        if (!isRefreshingUp && isDragFromUp && this.upProgressBar != null) {
            upProgressBar.setVisibility(View.INVISIBLE);
            upProgressBar.setProgress(0);
            upProgressBar.setIndeterminate(false);
            isDragFromUp = false;
        }
    }

    protected void onPullUpCancel() {
        if (!isRefreshingBottom && isDragFromBottom && this.bottomProgressBar != null) {
            bottomProgressBar.setVisibility(View.INVISIBLE);
            bottomProgressBar.setProgress(0);
            bottomProgressBar.setIndeterminate(false);
            isDragFromBottom = false;
        }
    }

    protected void onReset() {
        onPullUpCancel();
        onPullBottomCancel();
        lastMotionY = -1f;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX(), y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                onReset();
                break;
            case MotionEvent.ACTION_DOWN:
                onReset();
                if (!isRefreshingUp && isReadyForPullFromTop()) {
                    initialMotionX = x;
                    initialMotionY = y;
                    lastMotionY = y;
                    isDragFromUp = true;
                } else if (!isRefreshingBottom && isReadyForPullFromBottom()) {
                    initialMotionX = x;
                    initialMotionY = y;
                    lastMotionY = y;
                    isDragFromBottom = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isRefreshingUp && isDragFromUp) {
                    float yDx = y - lastMotionY;
                    if (yDx >= -touchSlop) {
                        float progress = (y - initialMotionY) / MAX_PULL_DISTANCE;
                        onPullUp(progress);
                        if (progress >= 1.0f) {
                            onRefreshingUp();
                        }
                        if (yDx > 0f) {
                            lastMotionY = y;
                        }
                    } else {
                        onReset();
                    }
                } else if (!isRefreshingBottom && isDragFromBottom) {
                    float yDx = y - lastMotionY;
                    if (yDx <= touchSlop) {
                        float progress = (initialMotionY - y) / MAX_PULL_DISTANCE;
                        onPullBottom(progress);
                        if (progress >= 1.0f) {
                            onRefreshingBottom();
                        }
                        if (yDx < 0f) {
                            lastMotionY = y;
                        }
                    } else {
                        onReset();
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    private boolean isReadyForPullFromTop() {
        boolean ready = false;
        if (listView.getCount() == 0) {
            ready = true;
        } else if (listView.getFirstVisiblePosition() == 0) {
            final View firstVisibleChild = listView.getChildAt(0);
            ready = firstVisibleChild != null && firstVisibleChild.getTop() >= 0;
        }
        return ready;
    }

    private boolean isReadyForPullFromBottom() {
        boolean ready = false;
        if (listView.getLastVisiblePosition() == listView.getCount() - 1) {
            final View lastVisibleChild = listView.getChildAt(listView.getLastVisiblePosition() -
                    listView.getFirstVisiblePosition());
            ready = lastVisibleChild != null && (lastVisibleChild.getBottom() +
                    listView.getListPaddingBottom() == listView.getHeight());
        }
        return ready;
    }

    private void applyProgressBarSettings(SmoothProgressBar smoothProgressBar) {
        if (smoothProgressBar != null) {
            int progressDrawableColor = getResources().getColor(R.color.default_progress_bar_color);
            ShapeDrawable shape = new ShapeDrawable();
            shape.setShape(new RectShape());
            shape.getPaint().setColor(progressDrawableColor);
            ClipDrawable clipDrawable = new ClipDrawable(shape, Gravity.CENTER, ClipDrawable.HORIZONTAL);

            smoothProgressBar.setProgressDrawable(clipDrawable);
        }
    }
}
