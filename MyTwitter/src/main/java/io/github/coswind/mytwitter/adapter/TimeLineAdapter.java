package io.github.coswind.mytwitter.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import io.github.coswind.mytwitter.MyApplication;
import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.animation.Rotate3dAnimation;
import io.github.coswind.mytwitter.utils.DateUtils;
import io.github.coswind.mytwitter.utils.ImageLoaderWrapper;
import io.github.coswind.mytwitter.utils.LogUtils;
import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * Created by coswind on 14-2-20.
 */
public class TimeLineAdapter extends BaseAdapter {
    private ResponseList<Status> statuses;

    private LayoutInflater layoutInflater;
    private ImageLoaderWrapper imageLoaderWrapper;

    private Activity activity;
    private int maxAnimationPosition = -1;

    public TimeLineAdapter(Activity activity) {
        this.activity = activity;
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Status status = statuses.get(position);

        viewHolder.text.setText(status.getText());
        viewHolder.name.setText(status.getUser().getName());
        viewHolder.screenName.setText("@" + status.getUser().getScreenName());
        viewHolder.time.setText(DateUtils.formatTimestamp(activity, status.getCreatedAt()));
        imageLoaderWrapper.displayProfileImage(viewHolder.profileImage, status.getUser().getProfileImageURL());

        if (position > maxAnimationPosition) {
            convertView.startAnimation(viewHolder.animationSet);
            maxAnimationPosition = position;
        }

        return convertView;
    }

    static class ViewHolder {
        TextView text;
        ImageView profileImage;
        TextView screenName;
        TextView name;
        TextView time;

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
