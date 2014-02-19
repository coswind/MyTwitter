package io.github.coswind.mytwitter.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.TwitterConstants;
import io.github.coswind.mytwitter.Utils.LogUtils;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
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
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                testHttpClient();
//            }
//        }).start();
    }

    private void testHttpClient() {
        URI uri;
        try {
            uri = new URI("https://coswindtestwebapp2.appspot.com");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        org.apache.http.client.HttpClient client = new DefaultHttpClient();

        HttpRequestBase commonRequest = new HttpPost("https://www.google.com");
        LogUtils.d("host: " + uri.getAuthority());
        commonRequest.addHeader("Host", uri.getAuthority());

        try {
            HttpResponse httpResponse = client.execute(commonRequest);

            LogUtils.d("httpResponse: " + EntityUtils.toString(httpResponse.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getTwitterToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("start");
                try {
                    RequestToken requestToken = twitter.getOAuthRequestToken();
                    LogUtils.d("requestToken: " + requestToken);
                    LogUtils.d("getAuthorizationURL: " + requestToken.getAuthorizationURL());
                    String authenticity_token = getAuthenticityToken(requestToken);
                    LogUtils.d("authenticity_token: " + authenticity_token);
                    String oauth_verifier = getOauthVerifier(requestToken, authenticity_token);
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
                    LogUtils.d("accessToken: " + accessToken);
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

    private static class TrustAllSSLSocketFactory extends SSLSocketFactory {
        final SSLContext sslContext = SSLContext.getInstance(TLS);

        TrustAllSSLSocketFactory(final KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            final TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }

        @Override
        public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose)
                throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        public static SSLSocketFactory getInstance() {
            try {
                final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                final SSLSocketFactory factory = new TrustAllSSLSocketFactory(trustStore);
                factory.setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);
                return factory;
            } catch (final GeneralSecurityException e) {
                LogUtils.e("Exception while creating SSLSocketFactory instance: " + e);
            } catch (final IOException e) {
                LogUtils.e("Exception while creating SSLSocketFactory instance: " + e);
            }
            return null;
        }
    }
}
