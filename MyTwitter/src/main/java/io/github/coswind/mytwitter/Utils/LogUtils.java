package io.github.coswind.mytwitter.Utils;

import android.util.Log;

public class LogUtils {
	private static final boolean DEBUG = true;
	private static final String DEFAULT_DEBUG_TAG = "sonmiDebug";

	public static void i(String tag, String string) {
		if (DEBUG) {
			Log.i(tag, string);
		}
	}

	public static void e(String tag, String string) {
		if (DEBUG) {
			Log.e(tag, string);
		}
	}

    public static void e(String string) {
        if (DEBUG) {
            Log.e(DEFAULT_DEBUG_TAG, string);
        }
    }
	
	public static void d(String string) {
		if (DEBUG) {
			Log.d(DEFAULT_DEBUG_TAG, string);
		}
	}

	public static void d(String tag, String string) {
		if (DEBUG) {
			Log.d(tag, string);
		}
	}

	public static void v(String tag, String string) {
		if (DEBUG) {
			Log.v(tag, string);
		}
	}

	public static void w(String tag, String string) {
		if (DEBUG) {
			Log.w(tag, string);
		}
	}
}
