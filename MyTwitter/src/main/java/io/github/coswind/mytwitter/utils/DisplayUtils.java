package io.github.coswind.mytwitter.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by wangwang on 13-12-24.
 */
public class DisplayUtils {

    private static int windowWidth;

    public static int dip2px(Context context, float dip) {
        return (int) (0.5f + dip * context.getResources().getDisplayMetrics().density);
    }

    public static float px2dip(Context context, int px) {
        return 0.5f + px / context.getResources().getDisplayMetrics().density;
    }

    public static int getWindowWidth(Activity activity) {
        if (windowWidth == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            // 屏幕分辨率
            (activity).getWindowManager().getDefaultDisplay().getMetrics(dm);
            windowWidth = dm.widthPixels;
        }
        return windowWidth;
    }

    public static class AlbumFragment {
        private static final int COLUMNS = 2;
        // w = 358, h = 280
        private static final float WIDTH_HEIGHT_SCALE = 358.0f / 280.0f;
        // 2dip
        private static final float HORIZONTAL_SPACING_DIP = 2.0f;

        private static int ITEM_WIDTH = 0;
        private static int ITEM_HEIGHT = 0;
        private static int HORIZONTAL_SPACING = 0;
        private static int VERTICAL_SPACING = 0;

        public static int getItemWidth(Context context) {
            if (!(context instanceof Activity)) {
                new IllegalArgumentException("Context not instanceof Activity");
            }

            if (ITEM_WIDTH > 0) {
                return ITEM_WIDTH;
            }

            DisplayMetrics dm = new DisplayMetrics();
            // 屏幕分辨率
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            ITEM_WIDTH = (dm.widthPixels - dip2px(context, HORIZONTAL_SPACING_DIP)) / 2;

            return ITEM_WIDTH;
        }

        public static int getItemHeight(Context context) {
            if (ITEM_HEIGHT > 0) {
                return ITEM_HEIGHT;
            }

            return ITEM_HEIGHT = (int) (getItemWidth(context) / WIDTH_HEIGHT_SCALE);
        }

        public static int getHorizontalSpacing(Context context) {
            if (!(context instanceof Activity)) {
                new IllegalArgumentException("Context not instanceof Activity");
            }

            if (HORIZONTAL_SPACING > 0) {
                return HORIZONTAL_SPACING;
            }

            DisplayMetrics dm = new DisplayMetrics();
            // 屏幕分辨率
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

            return HORIZONTAL_SPACING = dm.widthPixels - COLUMNS * getItemWidth(context);
        }

        public static int getVerticalSpacing(Context context) {
            if (VERTICAL_SPACING > 0) {
                return VERTICAL_SPACING;
            }

            return VERTICAL_SPACING = getHorizontalSpacing(context);
        }
    }

}
