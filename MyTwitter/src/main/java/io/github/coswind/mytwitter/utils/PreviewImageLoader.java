package io.github.coswind.mytwitter.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;

import io.github.coswind.mytwitter.R;

/**
 * Created by coswind on 14-3-5.
 */
public class PreviewImageLoader implements ImageLoadingListener, ImageLoadingProgressListener {
    private ProgressBar progressBar;

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        LogUtils.d("onLoadingStarted: " + imageUri);
        progressBar = getProgressBar(view);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        progressBar.setMax(100);
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        LogUtils.d("onLoadingFailed: " + imageUri);
        progressBar = getProgressBar(view);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        LogUtils.d("onLoadingComplete: " + imageUri);
        progressBar = getProgressBar(view);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        LogUtils.d("onLoadingCancelled: " + imageUri);
        progressBar = getProgressBar(view);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onProgressUpdate(String imageUri, View view, int current, int total) {
        LogUtils.d("onProgressUpdate: " + imageUri + " progress: " + (100 * current / total));
        if (total == 0) { return;}
        progressBar = getProgressBar(view);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(100 * current / total);
    }

    private ProgressBar getProgressBar(View view) {
        if (progressBar == null) {
            progressBar = (ProgressBar) ((View) view.getParent()).findViewById(R.id.ptr_progress_preview);
        }

        return progressBar;
    }
}
