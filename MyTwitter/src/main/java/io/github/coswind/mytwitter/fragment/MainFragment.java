package io.github.coswind.mytwitter.fragment;

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
import io.github.coswind.mytwitter.constant.TwitterConstants;
import io.github.coswind.mytwitter.dao.DaoMaster;
import io.github.coswind.mytwitter.dao.StatusDao;
import io.github.coswind.mytwitter.model.Account;
import io.github.coswind.mytwitter.sp.AccountSpUtils;
import io.github.coswind.mytwitter.utils.LogUtils;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.internal.http.HttpClient;
import twitter4j.internal.http.HttpClientFactory;
import twitter4j.internal.http.HttpRequest;
import twitter4j.internal.http.RequestMethod;
import twitter4j.internal.json.ResponseListImpl;
import twitter4j.internal.json.StatusJSONImpl;
import twitter4j.internal.org.json.JSONObject;

/**
 * Created by coswind on 14-2-13.
 */
public class MainFragment extends PullToRefreshFragment implements GetHomeTimeLineTask.HomeTimeLineCallback {
    private HttpClient httpClient;
    private Twitter twitter;

    private ListView listView;
    private ProgressBar progressBar;
    private TimeLineAdapter timeLineAdapter;

    private Status latestStatus;
    private Status oldestStatus;

    public final static int FROM_TOP = 0;
    public final static int FROM_BOTTOM = 1;

    private SQLiteDatabase sqLiteDatabase;
    private StatusDao statusDao;

    private int listViewPaddingTop;

    public MainFragment() {
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

    private ResponseList<Status> getResponseListStatus() {
        ResponseList<Status> statuses = new ResponseListImpl<Status>();
        Cursor cursor = sqLiteDatabase.query(statusDao.getTablename(), statusDao.getAllColumns(),
                null, null, null, null, StatusDao.Properties.Status_id.columnName + " DESC");
        if (cursor.moveToFirst()) {
            do {
                io.github.coswind.mytwitter.dao.Status status = statusDao.readEntity(cursor, 0);
                try {
                    statuses.add(new StatusJSONImpl(new JSONObject(status.getJsonString())));
                } catch (Exception e) {
                    LogUtils.d("add error: " + e);
                }
            } while (cursor.moveToNext());
        }
        return statuses;
    }

    private void init() {
        new AsyncTask<Void, Void, ResponseList<Status>>() {
            @Override
            protected ResponseList<twitter4j.Status> doInBackground(Void... params) {
                progressBar.setVisibility(View.VISIBLE);
                initTwitter();
                ResponseList<twitter4j.Status> statuses = getResponseListStatus();
                return statuses;
            }

            @Override
            protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                alphaAnimation.setDuration(500);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                progressBar.startAnimation(alphaAnimation);
                super.onPostExecute(statuses);
                timeLineAdapter = new TimeLineAdapter(getActivity());
                listView.setAdapter(timeLineAdapter);
                if (statuses.size() == 0) {
                    onRefreshingUp();
                } else {
                    latestStatus = statuses.get(0);
                    if (oldestStatus == null) {
                        oldestStatus = statuses.get(statuses.size() - 1);
                    }
                    timeLineAdapter.setStatuses(statuses);
                    timeLineAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.list_view);
        progressBar = (ProgressBar) view.findViewById(R.id.ptr_progress_center);
        listViewPaddingTop = listView.getPaddingTop();
        setListView(listView);
        setUpProgressBar((SmoothProgressBar) view.findViewById(R.id.ptr_progress_up));
        setBottomProgressBar((SmoothProgressBar) view.findViewById(R.id.ptr_progress_bottom));
    }

    private void initTwitter() {
        Account account = MyApplication.getInstance(getActivity()).getAccount();
        if (account == null) {
            // TODO
            try {
                account = signIn();
                AccountSpUtils.getInstance().setAccount(getActivity(), account);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AccessToken accessToken = account.getAccessToken();
        MyApplication myApplication = MyApplication.getInstance(getActivity());

        twitter = myApplication.getTwitter();
        twitter.setOAuthAccessToken(accessToken);
    }

    public void getHomeTimeLine(int type, Paging paging) {
        LogUtils.d("start get home time line.");

        GetHomeTimeLineTask getHomeTimeLineTask = new GetHomeTimeLineTask(twitter, this, type);

        if (paging == null) {
            getHomeTimeLineTask.execute();
        } else {
            getHomeTimeLineTask.execute(paging);
        }
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
    public void onHomeTimeLine(int type, ResponseList<Status> statuses) {
        int statusCount = statuses == null ? -1 : statuses.size();
        if (statuses == null) {
            Crouton.makeText(getActivity(), String.format(getString(R.string.home_time_line_refresh_error),
                    getString(R.string.network_error)), Style.ALERT).show();
        } else if (statusCount == 0) {
            Crouton.makeText(getActivity(), "No Tweets.", Style.ALERT).show();
        } else {
            storeStatusList(statuses);
            Configuration.Builder builder = new Configuration.Builder();
            ResponseList<Status> oldStatuses = timeLineAdapter.getStatuses();
            int scrollOffset = 0;
            if (type == FROM_TOP) {
                if (listView.getChildCount() > 0) {
                    final View firstView = listView.getChildAt(0);
                    if (firstView != null) {
                        scrollOffset = firstView.getTop() - listViewPaddingTop;
                    }
                }
                latestStatus = statuses.get(0);
                if (oldStatuses != null) {
                    statuses.addAll(oldStatuses);
                }
                if (oldestStatus == null) {
                    oldestStatus = statuses.get(statusCount - 1);
                }
            } else if (type == FROM_BOTTOM) {
                oldestStatus = statuses.get(statusCount - 1);
                if (oldStatuses != null) {
                    oldStatuses.addAll(statuses);
                    statuses = oldStatuses;
                }
                if (latestStatus == null) {
                    latestStatus = statuses.get(0);
                }
                builder.setViewGroupPosition(Configuration.POSITION_END);
            }
            Crouton.makeText(getActivity(), "Load " + statusCount + " Tweets.", Style.INFO)
                    .setConfiguration(builder.build()).show();
            timeLineAdapter.setStatuses(statuses);
            timeLineAdapter.notifyDataSetChanged();
            if (type == FROM_TOP && oldStatuses != null && oldStatuses.size() > 0 && statusCount > 0) {
                listView.setSelectionFromTop(statusCount, scrollOffset);
            }
        }
        if (type == FROM_TOP) {
            onRefreshUpEnd();
        } else if (type == FROM_BOTTOM) {
            onRefreshingBottomEnd();
        }
    }

    private void storeStatusList(ResponseList<Status> statuses) {
        ArrayList<io.github.coswind.mytwitter.dao.Status> statusList = new ArrayList<io.github.coswind.mytwitter.dao.Status>();
        for (Status status : statuses) {
            statusList.add(new io.github.coswind.mytwitter.dao.Status(status.getId(), status.getJson().toString()));
        }

        statusDao.insertInTx(statusList);
    }

    @Override
    protected void onRefreshingUp() {
        super.onRefreshingUp();

        Paging paging = new Paging();
        if (latestStatus != null) {
            paging.setSinceId(latestStatus.getId());
        }
        getHomeTimeLine(FROM_TOP, paging);
    }

    @Override
    protected void onRefreshingBottom() {
        super.onRefreshingBottom();

        Paging paging = new Paging();
        if (oldestStatus != null) {
            paging.setMaxId(oldestStatus.getId() - 1);
        }
        getHomeTimeLine(FROM_BOTTOM, paging);
    }
}
