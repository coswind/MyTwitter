package io.github.coswind.mytwitter.api;

import android.os.AsyncTask;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by coswind on 14-2-20.
 */
public class ReTweetTask extends AsyncTask<Long, Void, Boolean> {
    private Twitter twitter;
    private ReTweetCallback cb;

    public ReTweetTask(Twitter twitter, ReTweetCallback cb) {
        this.twitter = twitter;
        this.cb = cb;
    }

    @Override
    protected Boolean doInBackground(Long... params) {
        try {
            twitter.retweetStatus(params[0]);
        } catch (TwitterException e) {
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        cb.onReTweet(isSuccess);
    }

    public static interface ReTweetCallback {
        void onReTweet(boolean isSuccess);
    }
}
