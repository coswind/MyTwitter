package io.github.coswind.mytwitter.api;

import android.os.AsyncTask;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by coswind on 14-2-20.
 */
public class FavoriteTask extends AsyncTask<Long, Void, Boolean> {
    private Twitter twitter;
    private FavoriteCallback cb;

    public FavoriteTask(Twitter twitter, FavoriteCallback cb) {
        this.twitter = twitter;
        this.cb = cb;
    }

    @Override
    protected Boolean doInBackground(Long... params) {
        try {
            twitter.createFavorite(params[0]);
        } catch (TwitterException e) {
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        cb.onFavorite(isSuccess);
    }

    public static interface FavoriteCallback {
        void onFavorite(boolean isSuccess);
    }
}
