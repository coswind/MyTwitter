package io.github.coswind.mytwitter.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
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

import java.util.regex.Pattern;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.github.coswind.mytwitter.MyApplication;
import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.animation.Rotate3dAnimation;
import io.github.coswind.mytwitter.api.FavoriteTask;
import io.github.coswind.mytwitter.api.ReTweetTask;
import io.github.coswind.mytwitter.constant.ColorConstants;
import io.github.coswind.mytwitter.layout.CardLinearLayout;
import io.github.coswind.mytwitter.utils.ImageLoaderWrapper;
import twitter4j.MediaEntity;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

/**
 * Created by coswind on 14-2-20.
 */
public class TimeLineAdapter extends BaseAdapter implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, ReTweetTask.ReTweetCallback, FavoriteTask.FavoriteCallback {
    public final static Pattern IMAGES = Pattern.compile(".*\\.(png|jpeg|jpg|gif|bmp)");

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
            convertView = layoutInflater.inflate(R.layout.card, null);
            viewHolder = new ViewHolder();
            viewHolder.cardLinearLayout = (CardLinearLayout) convertView.findViewById(R.id.layout);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text_view);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.profile_image);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.screenName = (TextView) convertView.findViewById(R.id.screenName);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.rightStatus = (TextView) convertView.findViewById(R.id.right_status);
            viewHolder.previewImageLayout = convertView.findViewById(R.id.preview_layout);
            viewHolder.previewImage = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.overflowImage = (ImageView) convertView.findViewById(R.id.ellipsis);
            viewHolder.overflowImage.setOnClickListener(this);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Status status = statuses.get(position);
        Status realStatus;

        if (status.isRetweet()) {
            realStatus = status.getRetweetedStatus();
        } else {
            realStatus = status;
        }

        viewHolder.overflowImage.setTag(position);
        viewHolder.text.setText(realStatus.getText());
        viewHolder.name.setText(realStatus.getUser().getName());
        viewHolder.screenName.setText("@" + realStatus.getUser().getScreenName());
        viewHolder.time.setText(DateUtils.getRelativeTimeSpanString(realStatus.getCreatedAt().getTime()));
        imageLoaderWrapper.displayProfileImage(viewHolder.profileImage, realStatus.getUser().getProfileImageURL());

        if (status.isRetweet()) {
            viewHolder.cardLinearLayout.setDrawMask(true);
            viewHolder.cardLinearLayout.setMaskColor(ColorConstants.RE_TWEET_COLOR);
            viewHolder.rightStatus.setText(String.format(activity.getString(R.string.retweeted), status.getUser().getScreenName(), status.getRetweetCount()));
            viewHolder.rightStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_retweet, 0, 0, 0);
            viewHolder.rightStatus.setVisibility(View.VISIBLE);
        } else if (status.getInReplyToStatusId() > 0) {
            viewHolder.cardLinearLayout.setDrawMask(false);
            viewHolder.rightStatus.setText(String.format(activity.getString(R.string.in_reply), getInReplyName(status)));
            viewHolder.rightStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_conversation, 0, 0, 0);
            viewHolder.rightStatus.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cardLinearLayout.setDrawMask(false);
            viewHolder.rightStatus.setVisibility(View.GONE);
        }

        String previewUrl = getPreviewUrl(realStatus);
        if (TextUtils.isEmpty(previewUrl)) {
            viewHolder.previewImageLayout.setVisibility(View.GONE);
        } else {
            viewHolder.previewImageLayout.setVisibility(View.VISIBLE);
            imageLoaderWrapper.displayPreviewImage(viewHolder.previewImage, previewUrl);
        }

        if (position > maxAnimationPosition) {
            convertView.startAnimation(viewHolder.animationSet);
            maxAnimationPosition = position;
        }

        return convertView;
    }

    private String getPreviewUrl(Status status) {
        MediaEntity[] mediaEntities = status.getMediaEntities();
        if (mediaEntities.length > 0 && !TextUtils.isEmpty(mediaEntities[0].getMediaURLHttps())) {
            String mediaUrl = mediaEntities[0].getMediaURLHttps();
            if (IMAGES.matcher(mediaUrl).matches()) {
                return mediaUrl;
            }
        }
        URLEntity[] urlEntities = status.getURLEntities();
        if (urlEntities.length > 0 && !TextUtils.isEmpty(urlEntities[0].getExpandedURL())) {
            String expandedUrl = urlEntities[0].getExpandedURL();
            if (IMAGES.matcher(expandedUrl).matches()) {
                return expandedUrl;
            }
        }
        return null;
    }

    private String getInReplyName(Status status) {
        long inReplyUserId = status.getInReplyToUserId();
        UserMentionEntity[] entities = status.getUserMentionEntities();
        for (UserMentionEntity entity : entities) {
            if (inReplyUserId == entity.getId()) return entity.getName();
        }
        return status.getInReplyToScreenName();
    }

    private void showPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(activity, view);
        popupMenu.inflate(R.menu.card_status);
        popupMenu.setOnMenuItemClickListener(this);
        Menu menu = popupMenu.getMenu();
        if (clickedStatus.isRetweetedByMe()) {
            MenuItem retweetItem = menu.findItem(R.id.retweet);
            if (retweetItem != null) {
                retweetItem.setVisible(false);
            }
        }
        if (clickedStatus.isFavorited()) {
            MenuItem retweetItem = menu.findItem(R.id.favorite);
            if (retweetItem != null) {
                retweetItem.setVisible(false);
            }
        }
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.retweet:
                reTweet();
                break;
            case R.id.favorite:
                favorite();
                break;
            default:
                break;
        }

        return false;
    }

    private void reTweet() {
        new ReTweetTask(twitter, this).execute(clickedStatus.getId());
    }

    private void favorite() {
        new FavoriteTask(twitter, this).execute(clickedStatus.getId());
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
            clickedStatus.setRetweetedByMe(true);
            notifyDataSetChanged();
        } else {
            Crouton.makeText(activity, "Retweet Fail", Style.ALERT).show();
        }
    }

    @Override
    public void onFavorite(boolean isSuccess) {
        if (isSuccess) {
            Crouton.makeText(activity, "Favorite Success", Style.INFO).show();
            clickedStatus.setFavorite(true);
            notifyDataSetChanged();
        } else {
            Crouton.makeText(activity, "Favorite Fail", Style.ALERT).show();
        }
    }

    static class ViewHolder {
        CardLinearLayout cardLinearLayout;
        ImageView overflowImage;
        TextView text;
        ImageView profileImage;
        TextView screenName;
        TextView name;
        TextView time;
        TextView rightStatus;
        View previewImageLayout;
        ImageView previewImage;

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
