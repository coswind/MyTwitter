package io.github.coswind.mytwitter;

import android.app.Application;
import android.content.Context;

import io.github.coswind.mytwitter.model.Account;
import io.github.coswind.mytwitter.sp.AccountSpUtils;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

/**
 * Created by coswind on 14-2-20.
 */
public class MyApplication extends Application {
    private Account account;
    private Twitter twitter;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static MyApplication getInstance(Context context) {
        if (context == null) {
            return null;
        }
        Context app = context.getApplicationContext();
        return app instanceof MyApplication ? (MyApplication) app : null;
    }

    public Account getAccount() {
        if (account == null) {
            account = AccountSpUtils.getInstance().getAccount(this);
        }

        return account;
    }

    public Twitter getTwitter() {
        if (twitter == null) {
            twitter = new TwitterFactory(TwitterConstants.configuration).getInstance();
        }

        return twitter;
    }
}
