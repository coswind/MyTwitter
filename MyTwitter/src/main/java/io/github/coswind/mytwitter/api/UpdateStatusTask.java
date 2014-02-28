package io.github.coswind.mytwitter.api;

import android.app.Activity;
import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.github.coswind.mytwitter.MainActivity;
import io.github.coswind.mytwitter.MyApplication;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by coswind on 14-2-20.
 */
public class UpdateStatusTask extends AsyncTask<StatusUpdate, Void, Status> {
    private Twitter twitter;
    private MyApplication myApplication;

    public UpdateStatusTask(Twitter twitter, MyApplication myApplication) {
        this.twitter = twitter;
        this.myApplication = myApplication;
    }

    @Override
    protected twitter4j.Status doInBackground(StatusUpdate... params) {
        twitter4j.Status status;
        try {
            status = twitter.updateStatus(params[0]);
        } catch (TwitterException e) {
            return null;
        }

        return status;
    }

    @Override
    protected void onPostExecute(twitter4j.Status status) {
        MainActivity activity = myApplication.getMainActivity();
        if (activity == null) {
            return;
        }
        if (status != null) {
            if (activity.isVisible()) {
                Crouton.makeText(activity, "Tweet Sent.", Style.INFO).show();
            } else {
                Toast.makeText(myApplication, "Tweet Sent.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (activity.isVisible()) {
                Crouton.makeText(activity, "Tweet Sent Fail.", Style.ALERT).show();
            } else {
                Toast.makeText(myApplication, "Tweet Sent Fail.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
