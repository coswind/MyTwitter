package io.github.coswind.mytwitter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.coswind.mytwitter.constant.TwitterConstants;
import io.github.coswind.mytwitter.dao.DaoMaster;
import io.github.coswind.mytwitter.model.Account;
import io.github.coswind.mytwitter.sp.AccountSpUtils;
import io.github.coswind.mytwitter.utils.ImageLoaderWrapper;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

/**
 * Created by coswind on 14-2-20.
 */
public class MyApplication extends Application {
    private Account account;
    private Twitter twitter;
    private ImageLoaderWrapper imageLoaderWrapper;
    private Set<Activity> activitySet = Collections.synchronizedSet(new HashSet<Activity>());
    private SQLiteOpenHelper sqLiteOpenHelper;

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

    public ImageLoaderWrapper getImageLoaderWrapper() {
        if (imageLoaderWrapper == null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this);
            builder.threadPriority(Thread.NORM_PRIORITY - 2);
            builder.denyCacheImageMultipleSizesInMemory();
            builder.tasksProcessingOrder(QueueProcessingType.LIFO);
            imageLoader.init(builder.build());
            imageLoaderWrapper = new ImageLoaderWrapper(imageLoader);
        }

        return imageLoaderWrapper;
    }

    public SQLiteOpenHelper getSqLiteOpenHelper() {
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new DaoMaster.DevOpenHelper(this, "twitter", null);
        }
        return sqLiteOpenHelper;
    }

    public void addActivity(Activity activity) {
        activitySet.add(activity);
    }

    public void removeActivity(Activity activity) {
        activitySet.remove(activity);
    }

    public MainActivity getMainActivity() {
        for (Activity activity : activitySet) {
            if (activity instanceof MainActivity) {
                return (MainActivity) activity;
            }
        }
        return null;
    }
}
