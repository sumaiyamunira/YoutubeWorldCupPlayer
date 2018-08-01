package e.ddev.worldcupvibes.DataModel;

import java.io.Serializable;

/**
 * Created by Sumaiya on 05-Nov-17.
 */

public class YTVideoDTO implements Serializable, Comparable<YTVideoDTO> {
    private String channelID;
    private String channelName;
    private String channelType;
    private String eventType;
    private String title = "";
    private String description;
    private String publishedAt;
    private String imgThumbnailDefault = "";
    private String imgThumbnailHigh = "" ;
    private String contentDuration = "";
    private String contentSpectRatio;

    private String videoType;

    private boolean isEmbaddable;

    private String viewCount;
    private String likeCount;
    private String commentCount;
    private String dislikeCount;
    private String favCount;
    private boolean isCopyRightIssue = false;


    private String playlistID;
    private String videoID = "";
    private final String YOUTUBE_VIDEO_PREFIX = "https://www.youtube.com/watch?v=";
    private final String YOUTUBE_IMAGE_THUMB_PREFIX = "http://img.youtube.com/vi/";


    @Override
    public int compareTo(YTVideoDTO ytVideoDTO) {

        return -1;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getImgThumbnailDefault() {
        return imgThumbnailDefault;
    }

    public void setImgThumbnailDefault(String imgThumbnailDefault) {
        this.imgThumbnailDefault = imgThumbnailDefault;
    }
    public void setImgThumbnailFromVideoID() {
        this.imgThumbnailHigh = YOUTUBE_IMAGE_THUMB_PREFIX+videoID+"/0.jpg";
        this.imgThumbnailDefault = YOUTUBE_IMAGE_THUMB_PREFIX+videoID+"/0.jpg";

    }

    public String getImgThumbnailHigh() {
        return imgThumbnailHigh;
    }

    public void setImgThumbnailHigh(String imgThumbnailHigh) {
        this.imgThumbnailHigh = imgThumbnailHigh;
    }

    public String getContentDuration() {
        return contentDuration;
    }

    public void setContentDuration(String contentDuration) {
        this.contentDuration = contentDuration;
    }

    public String getContentSpectRatio() {
        return contentSpectRatio;
    }

    public void setContentSpectRatio(String contentSpectRatio) {
        this.contentSpectRatio = contentSpectRatio;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(String dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public String getFavCount() {
        return favCount;
    }

    public void setFavCount(String favCount) {
        this.favCount = favCount;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }


    public String getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getFormattedLinkURL() {

        return YOUTUBE_VIDEO_PREFIX+videoID;
    }


    public boolean isCopyRightIssue() {
        return isCopyRightIssue;
    }

    public void setCopyRightIssue(boolean copyRightIssue) {
        isCopyRightIssue = copyRightIssue;
    }


    public boolean isEmbaddable() {
        return isEmbaddable;
    }

    public void setEmbaddable(boolean embaddable) {
        isEmbaddable = embaddable;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }
}
