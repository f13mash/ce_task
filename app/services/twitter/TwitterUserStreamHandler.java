package services.twitter;

import services.RedisService;
import twitter4j.*;

import java.util.Set;

/**
 * Created by mahesh on 21/08/16.
 */
public class TwitterUserStreamHandler implements UserStreamListener {
    public static final String TWITTER_EVENTS= "TWITTER_EVENTS";
    private final RedisService redisService;

    public TwitterUserStreamHandler(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void onDeletionNotice(long directMessageId, long userId) {
        redisService.incrementKey(TWITTER_EVENTS, "on", 1);
    }

    @Override
    public void onFriendList(long[] friendIds) {
        redisService.incrementKey(TWITTER_EVENTS, "list_friends", 1);
    }

    @Override
    public void onFavorite(User source, User target, Status favoritedStatus) {
        redisService.incrementKey(TWITTER_EVENTS, "status_favorite", 1);
    }

    @Override
    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
        redisService.incrementKey(TWITTER_EVENTS, "status_unfavorite", 1);
    }

    @Override
    public void onFollow(User source, User followedUser) {
        redisService.incrementKey(TWITTER_EVENTS, "follow", 1);
    }

    @Override
    public void onUnfollow(User source, User unfollowedUser) {
        redisService.incrementKey(TWITTER_EVENTS, "unfollow", 1);
    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {
        redisService.incrementKey(TWITTER_EVENTS, "direct_message", 1);
    }

    @Override
    public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
        redisService.incrementKey(TWITTER_EVENTS, "user_list_member_add", 1);
    }

    @Override
    public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
        redisService.incrementKey(TWITTER_EVENTS, "user_list_member_del", 1);
    }

    @Override
    public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
        redisService.incrementKey(TWITTER_EVENTS, "user_list_subscribe", 1);
    }

    @Override
    public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
        redisService.incrementKey(TWITTER_EVENTS, "user_list_unsubscribe", 1);
    }

    @Override
    public void onUserListCreation(User listOwner, UserList list) {
        redisService.incrementKey(TWITTER_EVENTS, "user_list_create", 1);
    }

    @Override
    public void onUserListUpdate(User listOwner, UserList list) {
        redisService.incrementKey(TWITTER_EVENTS, "user_list_update", 1);
    }

    @Override
    public void onUserListDeletion(User listOwner, UserList list) {
        redisService.incrementKey(TWITTER_EVENTS, "user_list_delete", 1);
    }

    @Override
    public void onUserProfileUpdate(User updatedUser) {
        redisService.incrementKey(TWITTER_EVENTS, "user_profile_update", 1);
    }

    @Override
    public void onUserSuspension(long suspendedUser) {
        redisService.incrementKey(TWITTER_EVENTS, "user_suspension", 1);
    }

    @Override
    public void onUserDeletion(long deletedUser) {
        redisService.incrementKey(TWITTER_EVENTS, "user_deletion", 1);
    }

    @Override
    public void onBlock(User source, User blockedUser) {
        redisService.incrementKey(TWITTER_EVENTS, "user_block", 1);
    }

    @Override
    public void onUnblock(User source, User unblockedUser) {
        redisService.incrementKey(TWITTER_EVENTS, "user_unblock", 1);
    }

    @Override
    public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {
        redisService.incrementKey(TWITTER_EVENTS, "retweeted_retweet", 1);
    }

    @Override
    public void onFavoritedRetweet(User source, User target, Status favoritedRetweeet) {
        redisService.incrementKey(TWITTER_EVENTS, "favorited_retweet", 1);

    }

    @Override
    public void onQuotedTweet(User source, User target, Status quotingTweet) {
        redisService.incrementKey(TWITTER_EVENTS, "quoted_retweet", 1);
    }

    @Override
    public void onStatus(Status status) {
        redisService.incrementKey(TWITTER_EVENTS, "status", 1);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {

    }

    @Override
    public void onStallWarning(StallWarning warning) {

    }

    @Override
    public void onException(Exception ex) {
        ex.printStackTrace();
    }
}
