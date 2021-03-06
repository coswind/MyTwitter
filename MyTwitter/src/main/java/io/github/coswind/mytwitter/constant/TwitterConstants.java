package io.github.coswind.mytwitter.constant;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by coswind on 14-2-14.
 */
public class TwitterConstants {
    public final static String APP_NAME = "coswindtwibo";

    public final static String CONSUMER_KEY = "QNDgnEVG5ZNGdrO2pBzfAw";
    public final static String CONSUMER_SECRET = "P0CiG5quXW9VwfynUhuDtVjfBkgCKlRtnncR0mNlVo";

    public final static String TWITTER_API_HOST = "api.twitter.com";
    public final static String TWITTER_API_HOST_PROXY = "www.google.hk";
    public final static String TWITTER_PROXY_API_HOST = "coswindwebapp.appspot.com";

    public final static String DIRECT_SIGN_IN_URL = "https://api.twitter.com/sign_in";

    public final static int PAGING_COUNT = 20;

    public static Configuration configuration;

    static {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder
                .setUseSSL(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET);

        configuration = configurationBuilder.build();
    }
}
