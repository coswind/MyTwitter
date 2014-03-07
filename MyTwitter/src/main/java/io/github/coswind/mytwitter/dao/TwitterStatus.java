package io.github.coswind.mytwitter.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table STATUS.
 */
public class TwitterStatus {

    private Long id;
    private Long statusId;
    private Boolean isRetweet;
    private Boolean isRetweetedByMe;
    private Boolean isFavorite;
    private Long retweetId;
    private Long retweetedByUserId;
    private String retweetedByUserName;
    private String retweetedByUserScreenName;
    private Long userId;
    private String userName;
    private String userScreenName;
    private String userProfileImageUrl;
    private Long statusTimeStamp;
    private String text;
    private Integer retweetCount;
    private String source;
    private String mediaLink;
    private Long inReplyToStatusId;
    private Long inReplyToUserId;
    private String inReplyToUserName;
    private String inReplyToUserScreenName;
    private String jsonString;

    private boolean isGap;

    public boolean isGap() {
        return isGap;
    }

    public void setGap(boolean isGap) {
        this.isGap = isGap;
    }

    public TwitterStatus() {
    }

    public TwitterStatus(Long id) {
        this.id = id;
    }

    public TwitterStatus(Long id, Long statusId, Boolean isRetweet, Boolean isRetweetedByMe, Boolean isFavorite, Long retweetId, Long retweetedByUserId, String retweetedByUserName, String retweetedByUserScreenName, Long userId, String userName, String userScreenName, String userProfileImageUrl, Long statusTimeStamp, String text, Integer retweetCount, String source, String mediaLink, Long inReplyToStatusId, Long inReplyToUserId, String inReplyToUserName, String inReplyToUserScreenName, String jsonString) {
        this.id = id;
        this.statusId = statusId;
        this.isRetweet = isRetweet;
        this.isRetweetedByMe = isRetweetedByMe;
        this.isFavorite = isFavorite;
        this.retweetId = retweetId;
        this.retweetedByUserId = retweetedByUserId;
        this.retweetedByUserName = retweetedByUserName;
        this.retweetedByUserScreenName = retweetedByUserScreenName;
        this.userId = userId;
        this.userName = userName;
        this.userScreenName = userScreenName;
        this.userProfileImageUrl = userProfileImageUrl;
        this.statusTimeStamp = statusTimeStamp;
        this.text = text;
        this.retweetCount = retweetCount;
        this.source = source;
        this.mediaLink = mediaLink;
        this.inReplyToStatusId = inReplyToStatusId;
        this.inReplyToUserId = inReplyToUserId;
        this.inReplyToUserName = inReplyToUserName;
        this.inReplyToUserScreenName = inReplyToUserScreenName;
        this.jsonString = jsonString;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Boolean isRetweet() {
        return isRetweet;
    }

    public void setIsRetweet(Boolean isRetweet) {
        this.isRetweet = isRetweet;
    }

    public Boolean getIsRetweetedByMe() {
        return isRetweetedByMe;
    }

    public void setIsRetweetedByMe(Boolean isRetweetedByMe) {
        this.isRetweetedByMe = isRetweetedByMe;
    }

    public Boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Long getRetweetId() {
        return retweetId;
    }

    public void setRetweetId(Long retweetId) {
        this.retweetId = retweetId;
    }

    public Long getRetweetedByUserId() {
        return retweetedByUserId;
    }

    public void setRetweetedByUserId(Long retweetedByUserId) {
        this.retweetedByUserId = retweetedByUserId;
    }

    public String getRetweetedByUserName() {
        return retweetedByUserName;
    }

    public void setRetweetedByUserName(String retweetedByUserName) {
        this.retweetedByUserName = retweetedByUserName;
    }

    public String getRetweetedByUserScreenName() {
        return retweetedByUserScreenName;
    }

    public void setRetweetedByUserScreenName(String retweetedByUserScreenName) {
        this.retweetedByUserScreenName = retweetedByUserScreenName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public Long getStatusTimeStamp() {
        return statusTimeStamp;
    }

    public void setStatusTimeStamp(Long statusTimeStamp) {
        this.statusTimeStamp = statusTimeStamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(Integer retweetCount) {
        this.retweetCount = retweetCount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    public Long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public void setInReplyToStatusId(Long inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public Long getInReplyToUserId() {
        return inReplyToUserId;
    }

    public void setInReplyToUserId(Long inReplyToUserId) {
        this.inReplyToUserId = inReplyToUserId;
    }

    public String getInReplyToUserName() {
        return inReplyToUserName;
    }

    public void setInReplyToUserName(String inReplyToUserName) {
        this.inReplyToUserName = inReplyToUserName;
    }

    public String getInReplyToUserScreenName() {
        return inReplyToUserScreenName;
    }

    public void setInReplyToUserScreenName(String inReplyToUserScreenName) {
        this.inReplyToUserScreenName = inReplyToUserScreenName;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

}
