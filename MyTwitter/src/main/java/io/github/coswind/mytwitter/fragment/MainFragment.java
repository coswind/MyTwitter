package io.github.coswind.mytwitter.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.github.coswind.mytwitter.MyApplication;
import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.TwitterConstants;
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
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by coswind on 14-2-13.
 */
public class MainFragment extends Fragment implements GetHomeTimeLineTask.HomeTimeLineCallback, OnRefreshListener {
    private HttpClient httpClient;
    private Twitter twitter;

    private ListView listView;
    private TimeLineAdapter timeLineAdapter;

    private PullToRefreshLayout pullToRefreshLayout;

    private Status latestStatus;

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
                Paging paging = new Paging();
                paging.setCount(200);
                getHomeTimeLine(paging);
                pullToRefreshLayout.setRefreshing(true);
            }
        }.execute();
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.list_view);

        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(getActivity())
                .options(Options.create().scrollDistance(0.5f).build())
                .allChildrenArePullable()
                .listener(this)
                .setup(pullToRefreshLayout);
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

    public void getHomeTimeLine() {
        getHomeTimeLine(null);
    }

    public void getHomeTimeLine(Paging paging) {
        LogUtils.d("start get home time line.");

        GetHomeTimeLineTask getHomeTimeLineTask = new GetHomeTimeLineTask(twitter, this);

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
    public void onHomeTimeLine(ResponseList<Status> statuses) {
        if (statuses == null) {
            // TODO
            Crouton.makeText(getActivity(), String.format(getString(R.string.home_time_line_refresh_error),
                    getString(R.string.network_error)), Style.ALERT).show();
        } else if (statuses.size() == 0) {
            // TODO
            Crouton.makeText(getActivity(), "No Tweets.", Style.ALERT).show();
        } else {
            Crouton.makeText(getActivity(), "Load " + statuses.size() + " Tweets.", Style.INFO).show();
            latestStatus = statuses.get(0);

            for (Status status : statuses) {
                LogUtils.d(status.getUser().getName() + " --> " + status.getId() + " --> " + status.getText());
            }

            ResponseList<Status> oldStatuses = timeLineAdapter.getStatuses();
            if (oldStatuses != null) {
                statuses.addAll(oldStatuses);
            }
            timeLineAdapter.setStatuses(statuses);
        }

        timeLineAdapter.notifyDataSetChanged();
        pullToRefreshLayout.setRefreshComplete();
    }

    @Override
    public void onRefreshStarted(View view) {
        if (latestStatus == null) {
            getHomeTimeLine();
        } else {
            Paging paging = new Paging();
            paging.setSinceId(latestStatus.getId());
            getHomeTimeLine(paging);
        }
    }
}
