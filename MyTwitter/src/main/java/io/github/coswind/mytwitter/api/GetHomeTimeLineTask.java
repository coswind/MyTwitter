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
public class GetHomeTimeLineTask extends AsyncTask<Void, Void, ArrayList<TwitterStatus>> {
    public final static int FROM_TOP = 0;
    public final static int FROM_BOTTOM = 1;
    public final static int FROM_CENTER = 2;

    private Twitter twitter;
    private HomeTimeLineCallback cb;
    private Paging paging;

    private int type;

    public GetHomeTimeLineTask(Twitter twitter, HomeTimeLineCallback cb, Paging paging, int type) {
        this.twitter = twitter;
        this.cb = cb;
        this.paging = paging;
        this.type = type;
    }

    @Override
    protected ArrayList<TwitterStatus> doInBackground(Void... params) {
        ResponseList<twitter4j.Status> statuses = null;
        ArrayList<TwitterStatus> statusList = null;
        try {
            if (paging != null) {
                LogUtils.d("paging:" + paging);
                statuses = twitter.getHomeTimeline(paging);
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
        cb.onHomeTimeLine(type, paging, statuses);
    }

    public static interface HomeTimeLineCallback {
        void onHomeTimeLine(int type, Paging paging, ArrayList<TwitterStatus> statuses);
    }
}
