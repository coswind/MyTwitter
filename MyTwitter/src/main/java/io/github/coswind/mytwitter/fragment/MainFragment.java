package io.github.coswind.mytwitter.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.github.coswind.mytwitter.MyApplication;
import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.constant.TwitterConstants;
import io.github.coswind.mytwitter.adapter.TimeLineAdapter;
import io.github.coswind.mytwitter.api.GetHomeTimeLineTask;
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
import twitter4j.internal.http.HttpParameter;
import twitter4j.internal.http.HttpRequest;
import twitter4j.internal.http.RequestMethod;

/**
 * Created by coswind on 14-2-13.
 */
public class MainFragment extends PullToRefreshFragment implements GetHomeTimeLineTask.HomeTimeLineCallback {
    private HttpClient httpClient;
    private Twitter twitter;

    private ListView listView;
    private TimeLineAdapter timeLineAdapter;

    private Status latestStatus;
    private Status oldestStatus;

    public final static int FROM_TOP = 0;
    public final static int FROM_BOTTOM = 1;

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

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                init();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                timeLineAdapter = new TimeLineAdapter(getActivity());
                listView.setAdapter(timeLineAdapter);
                onRefreshingUp();
            }
        }.execute();
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.list_view);
        setListView(listView);
        setUpProgressBar((SmoothProgressBar) view.findViewById(R.id.ptr_progress_up));
        setBottomProgressBar((SmoothProgressBar) view.findViewById(R.id.ptr_progress_bottom));
    }

    private void init() {
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
        httpClient = HttpClientFactory.getInstance(TwitterConstants.configuration);

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
        HttpParameter[] params = new HttpParameter[2];
        params[0] = new HttpParameter("username", "xy2491259@gmail.com");
        params[1] = new HttpParameter("password", "xy@2491259");
        HttpRequest request = new HttpRequest(RequestMethod.GET,
                TwitterConstants.DIRECT_SIGN_IN_URL, params, null, null);
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
        if (statusCount < 0) {
            Crouton.makeText(getActivity(), String.format(getString(R.string.home_time_line_refresh_error),
                    getString(R.string.network_error)), Style.ALERT).show();
        } else if (statusCount == 0) {
            Crouton.makeText(getActivity(), "No Tweets.", Style.ALERT).show();
        } else {
            Configuration.Builder builder = new Configuration.Builder();
            for (Status status : statuses) {
//                LogUtils.d(status.getUser().getName() + " --> " + status.getText());
            }
            ResponseList<Status> oldStatuses = timeLineAdapter.getStatuses();
            if (type == FROM_TOP) {
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
        }
        timeLineAdapter.notifyDataSetChanged();
        if (type == FROM_TOP) {
            onRefreshUpEnd();
        } else if (type == FROM_BOTTOM) {
            onRefreshingBottomEnd();
        }
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
