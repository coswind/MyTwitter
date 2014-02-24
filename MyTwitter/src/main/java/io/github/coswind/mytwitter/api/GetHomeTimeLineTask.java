package io.github.coswind.mytwitter.api;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.github.coswind.mytwitter.utils.LogUtils;
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
            if (params.length > 0) {
                LogUtils.d("paging:" + params[0]);
                statuses = twitter.getHomeTimeline(params[0]);
            } else {
                statuses = twitter.getHomeTimeline();
            }
        } catch (TwitterException e) {
            LogUtils.d("load home time line error: " + e);
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
