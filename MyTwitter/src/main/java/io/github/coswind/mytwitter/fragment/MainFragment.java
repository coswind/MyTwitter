package io.github.coswind.mytwitter.fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.regex.Pattern;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.github.coswind.mytwitter.MyApplication;
import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.adapter.TimeLineAdapter;
import io.github.coswind.mytwitter.api.GetHomeTimeLineTask;
import io.github.coswind.mytwitter.constant.CacheConstants;
import io.github.coswind.mytwitter.constant.TwitterConstants;
import io.github.coswind.mytwitter.dao.DaoMaster;
import io.github.coswind.mytwitter.dao.StatusDao;
import io.github.coswind.mytwitter.dao.TwitterStatus;
import io.github.coswind.mytwitter.layout.PullToRefreshLayout;
import io.github.coswind.mytwitter.model.Account;
import io.github.coswind.mytwitter.sp.AccountSpUtils;
import io.github.coswind.mytwitter.utils.LogUtils;
import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;
import twitter4j.internal.http.HttpClient;
import twitter4j.internal.http.HttpClientFactory;
import twitter4j.internal.http.HttpRequest;
import twitter4j.internal.http.RequestMethod;

/**
 * Created by coswind on 14-2-13.
 */
public class MainFragment extends Fragment implements GetHomeTimeLineTask.HomeTimeLineCallback, PullToRefreshLayout.PullRefreshListener {
    private HttpClient httpClient;
    private Twitter twitter;

    private ListView listView;
    private ProgressBar progressBar;
    private PullToRefreshLayout pullToRefreshLayout;
    private TimeLineAdapter timeLineAdapter;

    private TwitterStatus latestStatus;
    private TwitterStatus oldestStatus;

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
                    pullToRefreshLayout.onRefreshingUp();
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
            Configuration.Builder builder = new Configuration.Builder();
            ArrayList<TwitterStatus> oldStatuses = timeLineAdapter.getStatuses();
            ArrayList<TwitterStatus> resultStatus = new ArrayList<TwitterStatus>();
            int scrollOffset = 0;
            if (type == FROM_TOP) {
                resultStatus = storeStatusListFromTop(statuses, oldStatuses);
                if (listView.getChildCount() > 0) {
                    final View firstView = listView.getChildAt(0);
                    if (firstView != null) {
                        scrollOffset = firstView.getTop() - listViewPaddingTop;
                    }
                }
                latestStatus = resultStatus.get(0);
                if (oldStatuses != null) {
                    resultStatus.addAll(oldStatuses);
                }
                if (oldestStatus == null) {
                    oldestStatus = resultStatus.get(statusCount - 1);
                }
            } else if (type == FROM_BOTTOM) {
                resultStatus = storeStatusListFromBottom(statuses, oldStatuses);
                oldestStatus = resultStatus.get(statusCount - 1);
                if (oldStatuses != null) {
                    oldStatuses.addAll(resultStatus);
                    resultStatus = oldStatuses;
                }
                if (latestStatus == null) {
                    latestStatus = resultStatus.get(0);
                }
                builder.setViewGroupPosition(Configuration.POSITION_END);
            }
            Crouton.makeText(getActivity(), "Load " + statusCount + " Tweets.", Style.INFO)
                    .setConfiguration(builder.build()).show();
            timeLineAdapter.setStatuses(resultStatus);
            timeLineAdapter.notifyDataSetChanged();
            if (type == FROM_TOP && oldStatuses != null && oldStatuses.size() > 0 && statusCount > 0) {
                listView.setSelectionFromTop(statusCount, scrollOffset);
            }
        }
        if (type == FROM_TOP) {
            pullToRefreshLayout.onRefreshUpEnd();
        } else if (type == FROM_BOTTOM) {
            pullToRefreshLayout.onRefreshingBottomEnd();
        }
    }

    private ArrayList<TwitterStatus> storeStatusListFromTop(ResponseList<Status> statuses, ArrayList<TwitterStatus> oldStatus) {
        ArrayList<TwitterStatus> statusList = new ArrayList<TwitterStatus>();
        for (Status status : statuses) {
            TwitterStatus daoStatus = new TwitterStatus();
            daoStatus.setJsonString(status.getJson().toString());
            daoStatus.setStatusId(status.getId());
            daoStatus.setIsRetweet(status.isRetweet());
            daoStatus.setIsRetweetedByMe(status.isRetweetedByMe());
            if (status.isRetweet()) {
                Status retweetStatus = status.getRetweetedStatus();
                daoStatus.setRetweetId(retweetStatus.getId());
                daoStatus.setRetweetedByUserId(status.getUser().getId());
                daoStatus.setRetweetedByUserName(status.getUser().getName());
                daoStatus.setInReplyToUserScreenName(status.getUser().getScreenName());
                status = retweetStatus;
            }
            daoStatus.setIsFavorite(status.isFavorited());
            daoStatus.setUserId(status.getUser().getId());
            daoStatus.setUserName(status.getUser().getName());
            daoStatus.setUserScreenName(status.getUser().getScreenName());
            daoStatus.setUserProfileImageUrl(status.getUser().getProfileImageURL());
            daoStatus.setStatusTimeStamp(status.getCreatedAt().getTime());
            daoStatus.setText(status.getText());
            daoStatus.setRetweetCount(status.getRetweetCount());
            daoStatus.setSource(status.getSource());
            daoStatus.setMediaLink(getPreviewUrl(status));
            daoStatus.setInReplyToStatusId(status.getInReplyToStatusId());
            daoStatus.setInReplyToUserId(status.getInReplyToUserId());
            daoStatus.setInReplyToUserName(getInReplyName(status));
            daoStatus.setInReplyToUserScreenName(status.getInReplyToScreenName());
            statusList.add(daoStatus);
        }
        statusDao.insertInTx(statusList);
        if (oldestStatus == null) { return statusList; }
        int insertCount = statuses.size();
        int primaryCount = oldStatus.size();
        if (primaryCount + insertCount > CacheConstants.DEFAULT_DATABASE_ITEM_LIMIT) {
            sqLiteDatabase.delete(statusDao.getTablename(), StatusDao.Properties.StatusId.columnName + "<?",
                    new String[]{String.valueOf(oldStatus.get(
                            CacheConstants.DEFAULT_DATABASE_ITEM_LIMIT - insertCount).getId())});
        }
        return statusList;
    }

    public final static Pattern IMAGES = Pattern.compile(".*\\.(png|jpeg|jpg|gif|bmp)");
    private String getPreviewUrl(Status status) {
        MediaEntity[] mediaEntities = status.getMediaEntities();
        if (mediaEntities.length > 0 && !TextUtils.isEmpty(mediaEntities[0].getMediaURLHttps())) {
            String mediaUrl = mediaEntities[0].getMediaURLHttps();
            if (IMAGES.matcher(mediaUrl).matches()) {
                return mediaUrl;
            }
        }
        URLEntity[] urlEntities = status.getURLEntities();
        if (urlEntities.length > 0 && !TextUtils.isEmpty(urlEntities[0].getExpandedURL())) {
            String expandedUrl = urlEntities[0].getExpandedURL();
            if (IMAGES.matcher(expandedUrl).matches()) {
                return expandedUrl;
            }
        }
        return null;
    }
    private String getInReplyName(Status status) {
        long inReplyUserId = status.getInReplyToUserId();
        UserMentionEntity[] entities = status.getUserMentionEntities();
        for (UserMentionEntity entity : entities) {
            if (inReplyUserId == entity.getId()) return entity.getName();
        }
        return status.getInReplyToScreenName();
    }

    private ArrayList<TwitterStatus> storeStatusListFromBottom(ResponseList<Status> statuses, ArrayList<TwitterStatus> oldStatuses) {
        ArrayList<TwitterStatus> statusList = new ArrayList<TwitterStatus>();
        if (oldestStatus == null) { return statusList; }
        int truncatedCount = CacheConstants.DEFAULT_DATABASE_ITEM_LIMIT - oldStatuses.size();
        if (truncatedCount < 0) {
            return statusList;
        }
        for (int i = 0, len = Math.min(truncatedCount, statuses.size()); i < len; i++) {
            Status status = statuses.get(i);
            TwitterStatus daoStatus = new TwitterStatus();
            daoStatus.setJsonString(status.getJson().toString());
            daoStatus.setStatusId(status.getId());
            daoStatus.setIsRetweet(status.isRetweet());
            daoStatus.setIsRetweetedByMe(status.isRetweetedByMe());
            if (status.isRetweet()) {
                Status retweetStatus = status.getRetweetedStatus();
                daoStatus.setRetweetId(retweetStatus.getId());
                daoStatus.setRetweetedByUserId(status.getUser().getId());
                daoStatus.setRetweetedByUserName(status.getUser().getName());
                daoStatus.setInReplyToUserScreenName(status.getUser().getScreenName());
                status = retweetStatus;
            }
            daoStatus.setIsFavorite(status.isFavorited());
            daoStatus.setUserId(status.getUser().getId());
            daoStatus.setUserName(status.getUser().getName());
            daoStatus.setUserScreenName(status.getUser().getScreenName());
            daoStatus.setUserProfileImageUrl(status.getUser().getProfileImageURL());
            daoStatus.setStatusTimeStamp(status.getCreatedAt().getTime());
            daoStatus.setText(status.getText());
            daoStatus.setRetweetCount(status.getRetweetCount());
            daoStatus.setSource(status.getSource());
            daoStatus.setMediaLink(getPreviewUrl(status));
            daoStatus.setInReplyToStatusId(status.getInReplyToStatusId());
            daoStatus.setInReplyToUserId(status.getInReplyToUserId());
            daoStatus.setInReplyToUserName(getInReplyName(status));
            daoStatus.setInReplyToUserScreenName(status.getInReplyToScreenName());
            statusList.add(daoStatus);
        }
        statusDao.insertInTx(statusList);
        return  statusList;
    }

    @Override
    public void onRefreshingUp() {
        Paging paging = new Paging();
        if (latestStatus != null) {
            paging.setSinceId(latestStatus.getStatusId());
        }
        getHomeTimeLine(FROM_TOP, paging);
    }

    @Override
    public void onRefreshingBottom() {
        Paging paging = new Paging();
        if (oldestStatus != null) {
            paging.setMaxId(oldestStatus.getStatusId() - 1);
        }
        getHomeTimeLine(FROM_BOTTOM, paging);
    }
}
