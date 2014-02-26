package io.github.coswind.mytwitter.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.github.coswind.mytwitter.MyApplication;
import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.animation.Rotate3dAnimation;
import io.github.coswind.mytwitter.api.ReTweetTask;
import io.github.coswind.mytwitter.utils.DateUtils;
import io.github.coswind.mytwitter.utils.ImageLoaderWrapper;
import io.github.coswind.mytwitter.utils.LogUtils;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by coswind on 14-2-20.
 */
public class TimeLineAdapter extends BaseAdapter implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, ReTweetTask.ReTweetCallback {
    private ResponseList<Status> statuses;

    private LayoutInflater layoutInflater;
    private ImageLoaderWrapper imageLoaderWrapper;

    private Twitter twitter;
    private Activity activity;
    private int maxAnimationPosition = -1;

    private Status clickedStatus;

    public TimeLineAdapter(Activity activity) {
        this.activity = activity;
        this.twitter = MyApplication.getInstance(activity).getTwitter();
        this.layoutInflater = activity.getLayoutInflater();
        this.imageLoaderWrapper = MyApplication.getInstance(activity).getImageLoaderWrapper();
    }

    public ResponseList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ResponseList<Status> statuses) {
        this.statuses = statuses;
    }

    @Override
    public int getCount() {
        if (statuses == null) return 0;
        return statuses.size();
    }

    @Override
    public Object getItem(int position) {
        if (statuses == null) return null;
        return statuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (statuses == null) return 0;
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.time_line_item, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.text_view);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.screenName = (TextView) convertView.findViewById(R.id.screenName);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.rightStatus = (TextView) convertView.findViewById(R.id.right_status);
            viewHolder.rightStatus.setText(R.string.retweeted);
            viewHolder.rightStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_retweeted, 0, 0, 0);
            viewHolder.overflowImage = (ImageView) convertView.findViewById(R.id.ellipsis);
            viewHolder.overflowImage.setOnClickListener(this);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Status status = statuses.get(position);

        viewHolder.overflowImage.setTag(position);
        viewHolder.text.setText(status.getText());
        viewHolder.name.setText(status.getUser().getName());
        viewHolder.screenName.setText("@" + status.getUser().getScreenName());
        viewHolder.time.setText(DateUtils.formatTimestamp(activity, status.getCreatedAt()));
        imageLoaderWrapper.displayProfileImage(viewHolder.profileImage, status.getUser().getProfileImageURL());

        viewHolder.rightStatus.setVisibility(status.isRetweetedByMe() ? View.VISIBLE : View.GONE);

        if (position > maxAnimationPosition) {
            convertView.startAnimation(viewHolder.animationSet);
            maxAnimationPosition = position;
        }

        return convertView;
    }

    private void showPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(activity, view);
        popupMenu.inflate(R.menu.card_status);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.retweet:
                reTweet();
                break;
            default:
                break;
        }

        return false;
    }

    private void reTweet() {
        new ReTweetTask(twitter, this).execute(clickedStatus.getId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ellipsis:
                int position = (Integer) v.getTag();
                clickedStatus = statuses.get(position);
                showPopUpMenu(v);
                break;
            default:
                break;
        }
    }

    @Override
    public void onReTweet(boolean isSuccess) {
        if (isSuccess) {
            Crouton.makeText(activity, "Retweet Success", Style.INFO).show();
        } else {
            Crouton.makeText(activity, "Retweet Fail", Style.ALERT).show();
        }
    }

    static class ViewHolder {
        ImageView overflowImage;
        TextView text;
        ImageView profileImage;
        TextView screenName;
        TextView name;
        TextView time;
        TextView rightStatus;

        AnimationSet animationSet;

        ViewHolder() {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            Rotate3dAnimation rotate3dAnimation = new Rotate3dAnimation(10, 0, 0.5f, 1, 0, false);
            animationSet = new AnimationSet(false);
            animationSet.addAnimation(translateAnimation);
            animationSet.addAnimation(rotate3dAnimation);
            animationSet.setDuration(500);
        }
    }
}
