package io.github.coswind.mytwitter;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by coswind on 14-2-14.
 */
public class TwitterConstants {
    public final static String CONSUMER_KEY = "QNDgnEVG5ZNGdrO2pBzfAw";
    public final static String CONSUMER_SECRET = "P0CiG5quXW9VwfynUhuDtVjfBkgCKlRtnncR0mNlVo";

    public final static String HOST = "coswindtestwebapp2.appspot.com";

    public static Configuration configuration;

    static {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder
//                .setUseSSL(true)
//                .setRestBaseURL("https://" + HOST + "/1.1/")
//                .setOAuthRequestTokenURL("https://" + HOST + "/oauth/request_token")
//                .setOAuthAuthenticationURL("https://" + HOST + "/oauth/authenticate")
//                .setOAuthAuthorizationURL("https://" + HOST + "/oauth/authorize")
//                .setOAuthAccessTokenURL("https://" + HOST + "/oauth/access_token")
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET);

        configuration = configurationBuilder.build();
    }
}
