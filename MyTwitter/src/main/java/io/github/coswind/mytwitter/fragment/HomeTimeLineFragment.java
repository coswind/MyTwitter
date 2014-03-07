package io.github.coswind.mytwitter.fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.github.coswind.mytwitter.MyApplication;
import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.adapter.TimeLineAdapter;
import io.github.coswind.mytwitter.api.GetHomeTimeLineTask;
import io.github.coswind.mytwitter.api.StoreStatusTask;
import io.github.coswind.mytwitter.constant.TwitterConstants;
import io.github.coswind.mytwitter.dao.DaoMaster;
import io.github.coswind.mytwitter.dao.StatusDao;
import io.github.coswind.mytwitter.dao.TwitterStatus;
import io.github.coswind.mytwitter.layout.PullToRefreshLayout;
import io.github.coswind.mytwitter.model.Account;
import io.github.coswind.mytwitter.sp.AccountSpUtils;
import io.github.coswind.mytwitter.utils.LogUtils;
import io.github.coswind.mytwitter.widget.GapView;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.internal.http.HttpClient;
import twitter4j.internal.http.HttpClientFactory;
import twitter4j.internal.http.HttpRequest;
import twitter4j.internal.http.RequestMethod;

/**
 * Created by coswind on 14-2-13.
 */
public class HomeTimeLineFragment extends Fragment implements GetHomeTimeLineTask.HomeTimeLineCallback, PullToRefreshLayout.PullRefreshListener, AdapterView.OnItemClickListener {
    private HttpClient httpClient;
    private Twitter twitter;

    private ListView listView;
    private ProgressBar progressBar;
    private PullToRefreshLayout pullToRefreshLayout;
    private TimeLineAdapter timeLineAdapter;

    private SQLiteDatabase sqLiteDatabase;
    private StatusDao statusDao;

    private int listViewPaddingTop;

    public HomeTimeLineFragment() {
        httpClient = HttpClientFactory.getInstance(TwitterConstants.configuration);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_line, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SQLiteOpenHelper sqLiteOpenHelper = MyApplication.getInstance(getActivity()).getSqLiteOpenHelper();
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        statusDao = new DaoMaster(sqLiteDatabase).newSession().getStatusDao();

        init();
    }

    private ArrayList<TwitterStatus> getResponseListStatus() {
        ArrayList<TwitterStatus> statuses = new ArrayList<TwitterStatus>();
        Cursor cursor = sqLiteDatabase.query(statusDao.getTablename(), statusDao.getAllColumns(),
                null, null, null, null, StatusDao.Properties.StatusId.columnName + " DESC");
        if (cursor.moveToFirst()) {
            do {
                TwitterStatus status = statusDao.readEntity(cursor, 0);
                try {
                    statuses.add(status);
                } catch (Exception e) {
                    LogUtils.d("add error: " + e);
                }
            } while (cursor.moveToNext());
        }
        return statuses;
    }

    private void init() {
        progressBar.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, ArrayList<TwitterStatus>>() {
            @Override
            protected ArrayList<TwitterStatus> doInBackground(Void... params) {
                initTwitter();
                ArrayList<TwitterStatus> statuses = getResponseListStatus();
                return statuses;
            }

            @Override
            protected void onPostExecute(ArrayList<TwitterStatus> statuses) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                alphaAnimation.setDuration(500);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                progressBar.startAnimation(alphaAnimation);
                timeLineAdapter = new TimeLineAdapter(getActivity());
                listView.setAdapter(timeLineAdapter);
                if (statuses.size() == 0) {
                    pullToRefreshLayout.onRefreshingUp();
                } else {
                    timeLineAdapter.setStatuses(statuses);
                    timeLineAdapter.notifyDataSetChanged();
                }
                super.onPostExecute(statuses);
            }
        }.execute();
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        progressBar = (ProgressBar) view.findViewById(R.id.ptr_progress_center);
        listViewPaddingTop = listView.getPaddingTop();
        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.pull_refresh_layout);
        pullToRefreshLayout.setListView(listView);
        pullToRefreshLayout.setUpProgressBar((SmoothProgressBar) view.findViewById(R.id.ptr_progress_up));
        pullToRefreshLayout.setBottomProgressBar((SmoothProgressBar) view.findViewById(R.id.ptr_progress_bottom));
        pullToRefreshLayout.setOnPullRefreshListener(this);
    }

    private void initTwitter() {
        Account account = MyApplication.getInstance(getActivity()).getAccount();
        if (account == null) {
            try {
                account = signIn();
            } catch (Exception e) {
                LogUtils.d("signIn error: " + e);
            }
        }
        AccountSpUtils.getInstance().setAccount(getActivity(), account);
        AccessToken accessToken = account.getAccessToken();
        MyApplication myApplication = MyApplication.getInstance(getActivity());
        twitter = myApplication.getTwitter();
        twitter.setOAuthAccessToken(accessToken);
    }

    public void getHomeTimeLine(int type, Paging paging) {
        LogUtils.d("start get home time line.");
        GetHomeTimeLineTask getHomeTimeLineTask = new GetHomeTimeLineTask(twitter, this, paging, type);
        getHomeTimeLineTask.execute();
    }

    private Account signIn() throws Exception {
        LogUtils.d("start signIn");
        HttpRequest request = new HttpRequest(RequestMethod.GET,
                TwitterConstants.DIRECT_SIGN_IN_URL, null, null, null);
        String response = httpClient.request(request).asString();
        AccessToken accessToken = JSON.parseObject(response, AccessToken.class);
        LogUtils.d("accessToken: " + accessToken);
        Account account = new Account();
        account.setAccessToken(accessToken);
        return account;
    }

    @Override
    public void onHomeTimeLine(int type, Paging paging, ArrayList<TwitterStatus> statuses) {
        int statusCount = statuses == null ? -1 : statuses.size();
        if (statuses == null) {
            Crouton.makeText(getActivity(), String.format(getString(R.string.home_time_line_refresh_error),
                    getString(R.string.network_error)), Style.ALERT).show();
        } else if (statusCount == 0) {
            Crouton.makeText(getActivity(), "No Tweets.", Style.ALERT).show();
        } else {
            Configuration.Builder builder = new Configuration.Builder();
            ArrayList<TwitterStatus> oldStatuses = timeLineAdapter.getStatuses();
            int scrollPosition = 0;
            int scrollOffset = 0;
            if (type == GetHomeTimeLineTask.FROM_TOP) {
                scrollPosition = statusCount;
                if (listView.getChildCount() > 0) {
                    final View firstView = listView.getChildAt(0);
                    if (firstView != null) {
                        scrollOffset = firstView.getTop() - listViewPaddingTop;
                    }
                }
                if (statuses.size() >= TwitterConstants.PAGING_COUNT) {
                    TwitterStatus twitterStatus = new TwitterStatus();
                    twitterStatus.setGap(true);
                    statuses.add(twitterStatus);
                    scrollPosition++;
                }
            } else if (type == GetHomeTimeLineTask.FROM_BOTTOM) {
                builder.setViewGroupPosition(Configuration.POSITION_END);
                StoreStatusTask storeStatusTask = new StoreStatusTask(statusDao, sqLiteDatabase, false, oldStatuses);
                storeStatusTask.execute(statuses);
            } else {
                timeLineAdapter.setMaxAnimationPosition(paging.getPosition() - 1);
            }
            Crouton.makeText(getActivity(), "Load " + statusCount + " Tweets.", Style.INFO)
                    .setConfiguration(builder.build()).show();
            timeLineAdapter.addStatuses(statuses, type, paging);
            timeLineAdapter.notifyDataSetChanged();
            if (type == GetHomeTimeLineTask.FROM_TOP && oldStatuses != null
                    && oldStatuses.size() > 0 && statusCount > 0) {
                listView.setSelectionFromTop(scrollPosition, scrollOffset);
            }
        }
        if (type == GetHomeTimeLineTask.FROM_BOTTOM) {
            pullToRefreshLayout.setRefreshingBottomEnd();
        } else {
            pullToRefreshLayout.setRefreshUpEnd();
        }
    }

    @Override
    public void onRefreshingUp() {
        Paging paging = new Paging();
        if (timeLineAdapter.getLatestStatus() != null) {
            paging.setSinceId(timeLineAdapter.getLatestStatus().getStatusId());
        }
        getHomeTimeLine(GetHomeTimeLineTask.FROM_TOP, paging);
    }

    @Override
    public void onRefreshingBottom() {
        Paging paging = new Paging();
        if (timeLineAdapter.getOldestStatus() != null) {
            paging.setMaxId(timeLineAdapter.getOldestStatus().getStatusId() - 1);
        }
        getHomeTimeLine(GetHomeTimeLineTask.FROM_BOTTOM, paging);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view instanceof GapView) {
            Paging paging = new Paging();
            paging.setMaxId(timeLineAdapter.getItem(position - 1).getStatusId() - 1);
            paging.setSinceId(timeLineAdapter.getItem(position + 1).getStatusId());
            paging.setPosition(position);
            getHomeTimeLine(GetHomeTimeLineTask.FROM_CENTER, paging);
            pullToRefreshLayout.setRefreshingUp();
        }
    }
}
