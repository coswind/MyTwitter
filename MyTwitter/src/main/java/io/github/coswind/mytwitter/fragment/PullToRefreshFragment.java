package io.github.coswind.mytwitter.fragment;

import android.app.Fragment;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.utils.LogUtils;

/**
 * Created by coswind on 14-2-25.
 */
public class PullToRefreshFragment extends Fragment implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private ListView listView;
    private SmoothProgressBar upProgressBar;
    private SmoothProgressBar bottomProgressBar;
    private GestureDetector gestureDetector;

    private boolean isRefreshingUp;
    private boolean isRefreshingBottom;

    public final static float MAX_PULL_DISTANCE = 400.0f;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gestureDetector = new GestureDetector(getActivity(), this);
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

    protected void onPullCancel() {
        if (!isRefreshingUp && this.upProgressBar != null) {
            upProgressBar.setVisibility(View.INVISIBLE);
            upProgressBar.setProgress(0);
            upProgressBar.setIndeterminate(false);
        }

        if (!isRefreshingBottom && this.bottomProgressBar != null) {
            bottomProgressBar.setVisibility(View.INVISIBLE);
            bottomProgressBar.setProgress(0);
            bottomProgressBar.setIndeterminate(false);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                onPullCancel();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!isRefreshingUp && isReadyForPullFromTop()) {
            float progress = (e2.getY() - e1.getY()) / MAX_PULL_DISTANCE;
            onPullUp(progress);
            if (progress >= 1.0f) {
                onRefreshingUp();
            }
        } else if (!isRefreshingBottom && isReadyForPullFromBottom()) {
            float progress = (e1.getY() - e2.getY()) / MAX_PULL_DISTANCE;
            onPullBottom(progress);
            if (progress >= 1.0f) {
                onRefreshingBottom();
            }
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

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
