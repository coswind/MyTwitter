package io.github.coswind.mytwitter.api;

import android.os.AsyncTask;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by coswind on 14-2-20.
 */
public class GetHomeTimeLineTask extends AsyncTask<Paging, Void, ResponseList<Status>> {
    private Twitter twitter;
    private HomeTimeLineCallback cb;

    public GetHomeTimeLineTask(Twitter twitter, HomeTimeLineCallback cb) {
        this.twitter = twitter;
        this.cb = cb;
    }

    @Override
    protected ResponseList<twitter4j.Status> doInBackground(Paging... params) {
        ResponseList<twitter4j.Status> statuses = null;
        try {
            statuses = twitter.getHomeTimeline(params[0]);
        } catch (TwitterException e) {
            // TODO
        }

        return statuses;
    }

    @Override
    protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {
        cb.onHomeTimeLine(statuses);
    }

    public static interface HomeTimeLineCallback {
        void onHomeTimeLine(ResponseList<twitter4j.Status> statuses);
    }
}
