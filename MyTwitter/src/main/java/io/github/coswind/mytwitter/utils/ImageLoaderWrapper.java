package io.github.coswind.mytwitter.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import io.github.coswind.mytwitter.R;

/**
 * Created by coswind on 14-2-21.
 */
public class ImageLoaderWrapper {
    private ImageLoader imageLoader;

    private DisplayImageOptions profileImageDisplayOptions;
    private DisplayImageOptions imageDisplayOptions;

    public ImageLoaderWrapper(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;

        DisplayImageOptions.Builder profileOptsBuilder = new DisplayImageOptions.Builder();
        profileOptsBuilder.cacheInMemory(true)
                .cacheOnDisc(true)
                .showImageForEmptyUri(R.drawable.ic_profile_image_default)
                .showImageOnFail(R.drawable.ic_profile_image_default)
                .showImageOnLoading(R.drawable.ic_profile_image_default)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true);

        DisplayImageOptions.Builder imageOptsBuilder = new DisplayImageOptions.Builder();
        imageOptsBuilder.cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true);

        profileImageDisplayOptions = profileOptsBuilder.build();
        imageDisplayOptions = imageOptsBuilder.build();
    }

    public void displayProfileImage(ImageView view, String url) {
        imageLoader.displayImage(url, view, profileImageDisplayOptions);
    }
}
