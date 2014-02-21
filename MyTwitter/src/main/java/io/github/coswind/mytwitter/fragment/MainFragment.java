package io.github.coswind.mytwitter.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

import io.github.coswind.mytwitter.MyApplication;
import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.TwitterConstants;
import io.github.coswind.mytwitter.adapter.TimeLineAdapter;
import io.github.coswind.mytwitter.api.GetHomeTimeLineTask;
import io.github.coswind.mytwitter.model.Account;
import io.github.coswind.mytwitter.utils.LogUtils;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.internal.http.HttpClient;
import twitter4j.internal.http.HttpClientFactory;
import twitter4j.internal.http.HttpParameter;
import twitter4j.internal.http.HttpRequest;
import twitter4j.internal.http.RequestMethod;

/**
 * Created by coswind on 14-2-13.
 */
public class MainFragment extends Fragment implements GetHomeTimeLineTask.HomeTimeLineCallback {
    private HttpClient httpClient;
    private Twitter twitter;

    private GetHomeTimeLineTask getHomeTimeLineTask;

    private ListView listView;
    private TimeLineAdapter timeLineAdapter;

    public MainFragment() {
        httpClient = HttpClientFactory.getInstance(TwitterConstants.configuration);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_line, container, false);

        timeLineAdapter = new TimeLineAdapter(inflater);

        initView(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        getHomeTimeLine(new Paging(1));
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(timeLineAdapter);
    }

    private void init() {
        Account account = MyApplication.getInstance(getActivity()).getAccount();
        if (account == null) {
            // TODO
            return;
        }

        AccessToken accessToken = account.getAccessToken();
        httpClient = HttpClientFactory.getInstance(TwitterConstants.configuration);

        MyApplication myApplication = MyApplication.getInstance(getActivity());

        if (myApplication == null) {
            twitter = new TwitterFactory(TwitterConstants.configuration).getInstance();
        } else {
            twitter = myApplication.getTwitter();
        }
        twitter.setOAuthAccessToken(accessToken);
    }

    public void getHomeTimeLine(Paging paging) {
        LogUtils.d("start get home time line.");

        if (getHomeTimeLineTask == null) {
            getHomeTimeLineTask = new GetHomeTimeLineTask(twitter, this);
        }

        getHomeTimeLineTask.execute(paging);
    }

    private Account signIn() throws TwitterException {
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
        for (Status status : statuses) {
            LogUtils.d(status.getUser().getName() + " --> " + status.getText());
        }

        timeLineAdapter.setStatuses(statuses);
        timeLineAdapter.notifyDataSetChanged();
    }
}
