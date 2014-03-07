package io.github.coswind.mytwitter.dao;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.regex.Pattern;

import twitter4j.MediaEntity;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

/**
 * Created by coswind on 14-3-7.
 */
public class TwitterStatusUtils {
    public final static Pattern IMAGES = Pattern.compile(".*\\.(png|jpeg|jpg|gif|bmp)");

    public static ArrayList<TwitterStatus> makeTwitterStatus(ResponseList<Status> statuses) {
        ArrayList<TwitterStatus> statusList = new ArrayList<TwitterStatus>();
        for (Status status : statuses) {
            TwitterStatus daoStatus = new TwitterStatus();
            daoStatus.setJsonString(status.getJson().toString());
            daoStatus.setStatusId(status.getId());
            daoStatus.setIsRetweet(status.isRetweet());
            daoStatus.setIsRetweetedByMe(status.isRetweetedByMe());
            if (status.isRetweet()) {
                Status retweetStatus = status.getRetweetedStatus();
                daoStatus.setRetweetId(retweetStatus.getId());
                daoStatus.setRetweetedByUserId(status.getUser().getId());
                daoStatus.setRetweetedByUserName(status.getUser().getName());
                daoStatus.setInReplyToUserScreenName(status.getUser().getScreenName());
                status = retweetStatus;
            }
            daoStatus.setIsFavorite(status.isFavorited());
            daoStatus.setUserId(status.getUser().getId());
            daoStatus.setUserName(status.getUser().getName());
            daoStatus.setUserScreenName(status.getUser().getScreenName());
            daoStatus.setUserProfileImageUrl(status.getUser().getProfileImageURL());
            daoStatus.setStatusTimeStamp(status.getCreatedAt().getTime());
            daoStatus.setText(status.getText());
            daoStatus.setRetweetCount(status.getRetweetCount());
            daoStatus.setSource(status.getSource());
            daoStatus.setMediaLink(getPreviewUrl(status));
            daoStatus.setInReplyToStatusId(status.getInReplyToStatusId());
            daoStatus.setInReplyToUserId(status.getInReplyToUserId());
            daoStatus.setInReplyToUserName(getInReplyName(status));
            daoStatus.setInReplyToUserScreenName(status.getInReplyToScreenName());
            statusList.add(daoStatus);
        }

        return statusList;
    }

    private static String getPreviewUrl(Status status) {
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

    private static String getInReplyName(Status status) {
        long inReplyUserId = status.getInReplyToUserId();
        UserMentionEntity[] entities = status.getUserMentionEntities();
        for (UserMentionEntity entity : entities) {
            if (inReplyUserId == entity.getId()) return entity.getName();
        }
        return status.getInReplyToScreenName();
    }
}
