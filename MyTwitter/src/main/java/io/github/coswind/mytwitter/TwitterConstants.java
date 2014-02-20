package io.github.coswind.mytwitter;

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
    public final static String TWITTER_PROXY_API_HOST = "coswindwebapp.appspot.com";

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
