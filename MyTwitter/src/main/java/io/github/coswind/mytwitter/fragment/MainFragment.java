package io.github.coswind.mytwitter.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.TwitterConstants;
import io.github.coswind.mytwitter.model.Account;
import io.github.coswind.mytwitter.utils.AccountUtils;
import io.github.coswind.mytwitter.utils.LogUtils;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.internal.http.HttpClient;
import twitter4j.internal.http.HttpClientFactory;
import twitter4j.internal.http.HttpParameter;
import twitter4j.internal.http.HttpRequest;
import twitter4j.internal.http.RequestMethod;

/**
 * Created by coswind on 14-2-13.
 */
public class MainFragment extends Fragment {
    private HttpClient httpClient;
    private Twitter twitter;

    public MainFragment() {
        httpClient = HttpClientFactory.getInstance(TwitterConstants.configuration);
        twitter = new TwitterFactory(TwitterConstants.configuration).getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTwitterToken();
    }

    public void getTwitterToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("start");
                try {
                    Account account = AccountUtils.getInstance().getAccount(getActivity());
                    if (account == null) {
                        RequestToken requestToken = twitter.getOAuthRequestToken();
                        LogUtils.d("requestToken: " + requestToken);
                        LogUtils.d("getAuthorizationURL: " + requestToken.getAuthorizationURL());
                        String authenticity_token = getAuthenticityToken(requestToken);
                        String oauth_verifier = getOauthVerifier(requestToken, authenticity_token);
                        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
                        account = new Account();
                        account.setAccessToken(accessToken);
                        AccountUtils.getInstance().setAccount(getActivity(), account);
                    } else {
                        AccessToken accessToken = account.getAccessToken();
                        twitter.setOAuthAccessToken(accessToken);
                    }
                    LogUtils.d("start get home time line.");
                    ResponseList<Status> statuses = twitter.getHomeTimeline();
                    for (Status status : statuses) {
                        LogUtils.d("status: " + status.getText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.d("error: " + e);
                }
            }
        }).start();
    }

    private String getAuthenticityToken(RequestToken requestToken) throws TwitterException {
        String authenticity_token = "";
        HttpRequest request = new HttpRequest(RequestMethod.GET,
                requestToken.getAuthorizationURL(), null, null, null);
        String response = httpClient.request(request).asString();
        Matcher matcher = Pattern.compile("authenticity_token.*value=\"(\\w+)").matcher(response);
        if (matcher.find()) {
            authenticity_token = matcher.group(1);
            LogUtils.d("authenticity_token: " + authenticity_token);
        }
        return authenticity_token;
    }

    private String getOauthVerifier(RequestToken requestToken, String authenticity_token) throws TwitterException {
        HttpParameter[] params = new HttpParameter[4];
        params[0] = new HttpParameter("authenticity_token", authenticity_token);
        params[1] = new HttpParameter("oauth_token", requestToken.getToken());
        params[2] = new HttpParameter("session[username_or_email]", "xy2491259@gmail.com");
        params[3] = new HttpParameter("session[password]", "xy@2491259");
        HttpRequest request = new HttpRequest(RequestMethod.POST,
                TwitterConstants.configuration.getOAuthAuthorizationURL(), params, null, null);
        String response = httpClient.request(request).asString();
        Matcher matcher = Pattern.compile("oauth_verifier=(\\w+)").matcher(response);
        String oauth_verifier= "";
        if (matcher.find()) {
            oauth_verifier = matcher.group(1);
            LogUtils.d("oauth_verifier: " + oauth_verifier);
        }
        return oauth_verifier;
    }
}
