package io.github.coswind.mytwitter.api;

import android.os.AsyncTask;

import java.util.ArrayList;

import io.github.coswind.mytwitter.dao.TwitterStatus;
import io.github.coswind.mytwitter.dao.TwitterStatusUtils;
import io.github.coswind.mytwitter.utils.LogUtils;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by coswind on 14-2-20.
 */
public class GetHomeTimeLineTask extends AsyncTask<Paging, Void, ArrayList<TwitterStatus>> {
    private Twitter twitter;
    private HomeTimeLineCallback cb;

    private int type;

    public GetHomeTimeLineTask(Twitter twitter, HomeTimeLineCallback cb, int type) {
        this.twitter = twitter;
        this.cb = cb;
        this.type = type;
    }

    @Override
    protected ArrayList<TwitterStatus> doInBackground(Paging... params) {
        ResponseList<twitter4j.Status> statuses = null;
        ArrayList<TwitterStatus> statusList = null;
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

        if (statuses != null) {
            statusList = TwitterStatusUtils.makeTwitterStatus(statuses);
        }

        return statusList;
    }

    @Override
    protected void onPostExecute(ArrayList<TwitterStatus> statuses) {
        cb.onHomeTimeLine(type, statuses);
    }

    public static interface HomeTimeLineCallback {
        void onHomeTimeLine(int type, ArrayList<TwitterStatus> statuses);
    }
}
