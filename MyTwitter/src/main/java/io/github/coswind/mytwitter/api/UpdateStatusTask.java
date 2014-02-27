package io.github.coswind.mytwitter.api;

import android.os.AsyncTask;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by coswind on 14-2-20.
 */
public class UpdateStatusTask extends AsyncTask<StatusUpdate, Void, Status> {
    private Twitter twitter;
    private UpdateStatusCallback cb;

    public UpdateStatusTask(Twitter twitter, UpdateStatusCallback cb) {
        this.twitter = twitter;
        this.cb = cb;
    }

    @Override
    protected twitter4j.Status doInBackground(StatusUpdate... params) {
        twitter4j.Status status;
        try {
            status = twitter.updateStatus(params[0]);
        } catch (TwitterException e) {
            return null;
        }

        return status;
    }

    @Override
    protected void onPostExecute(twitter4j.Status status) {
        cb.onUpdateStatus(status);
    }

    public static interface UpdateStatusCallback {
        void onUpdateStatus(twitter4j.Status status);
    }
}
