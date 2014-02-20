package io.github.coswind.mytwitter.sp;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.coswind.mytwitter.TwitterConstants;

/**
 * Created by coswind on 13-12-13.
 */
public class BaseSpUtils {
    protected String spFileName = TwitterConstants.APP_NAME;
    protected int spMode = Context.MODE_PRIVATE;

    protected boolean getBoolean(Context context, String string) {
        return getBoolean(context, string, false);
    }

    protected boolean getBoolean(Context context, String string, boolean aBoolean) {
        return context.getSharedPreferences(spFileName, spMode)
                .getBoolean(string, aBoolean);
    }

    protected int getInt(Context context, String string) {
        return getInt(context, string, 0);
    }

    protected int getInt(Context context, String string, int aInt) {
        return context.getSharedPreferences(spFileName, spMode)
                .getInt(string, aInt);
    }

    protected long getLong(Context context, String string) {
        return getLong(context, string, 0L);
    }

    protected long getLong(Context context, String string, long aLong) {
        return context.getSharedPreferences(spFileName, spMode)
                .getLong(string, aLong);
    }

    protected String getString(Context context, String string) {
        return getString(context, string, "");
    }

    protected String getString(Context context, String string, String aString) {
        return context.getSharedPreferences(spFileName, spMode)
                .getString(string, aString);
    }

    protected void putBoolean(Context context, String string, boolean aBoolean) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(spFileName, spMode).edit();

        editor.putBoolean(string, aBoolean);
        editor.commit();
    }

    protected void putInt(Context context, String string, int aInt) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(spFileName, spMode).edit();

        editor.putInt(string, aInt);
        editor.commit();
    }

    protected void putLong(Context context, String string, long aLong) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(spFileName, spMode).edit();

        editor.putLong(string, aLong);
        editor.commit();
    }

    protected void putString(Context context, String stringKey, String stringVal) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(spFileName, spMode).edit();

        editor.putString(stringKey, stringVal);
        editor.commit();
    }

    protected void clear(Context context) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(spFileName, spMode).edit();

        editor.clear();
        editor.commit();
    }
}
