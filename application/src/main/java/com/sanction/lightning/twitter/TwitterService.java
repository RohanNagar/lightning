package com.sanction.lightning.twitter;

import com.sanction.lightning.models.PublishType;
import com.sanction.lightning.models.twitter.TwitterAccessToken;
import com.sanction.lightning.models.twitter.TwitterOAuthRequest;
import com.sanction.lightning.models.twitter.TwitterUser;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterService {
  private static final Logger LOG = LoggerFactory.getLogger(TwitterService.class);

  private final Twitter twitterClient;

  /**
   * Constructs a new TwitterService for use with an authenticating user.
   *
   * @param applicationKey The Twitter consumer application key.
   * @param applicationSecret The Twitter consumer application secret.
   * @param userKey The key to use when authenticating the user.
   * @param userSecret The secret to use when authenticating the user.
   */
  public TwitterService(String applicationKey, String applicationSecret,
                        String userKey, String userSecret) {
    this.twitterClient = new TwitterFactory(new ConfigurationBuilder()
        .setOAuthConsumerKey(applicationKey)
        .setOAuthConsumerSecret(applicationSecret)
        .setOAuthAccessToken(userKey)
        .setOAuthAccessTokenSecret(userSecret)
        .build()).getInstance();
  }

  /**
   * Constructs a new TwitterService for use without an authenticating user.
   *
   * @param applicationKey The Twitter consumer application key.
   * @param applicationSecret The Twitter consumer application secret.
   */
  public TwitterService(String applicationKey, String applicationSecret) {
    this.twitterClient = new TwitterFactory(new ConfigurationBuilder()
        .setOAuthConsumerKey(applicationKey)
        .setOAuthConsumerSecret(applicationSecret)
        .build()).getInstance();
  }

  /**
   * Retrieves the TwitterUser information for the current authenticated user.
   *
   * @return The TwitterUser object representing the user's information from Twitter.
   */
  public TwitterUser getTwitterUser() {
    User twitterUser;
    try {
      long userId = twitterClient.getId();
      twitterUser = twitterClient.showUser(userId);
    } catch (TwitterException e) {
      LOG.error("Unable to get user from Twitter. "
          + "Twitter error code: {}", e.getErrorCode(), e);
      return null;
    }

    return new TwitterUser(
        twitterUser.getId(),
        twitterUser.getFavouritesCount(),
        twitterUser.getFollowersCount(),
        twitterUser.getCreatedAt().toString(),
        twitterUser.getLocation(),
        twitterUser.getName(),
        twitterUser.getScreenName(),
        twitterUser.getProfileImageURL(),
        twitterUser.isVerified());
  }

  /**
   * Publishes text or media to Twitter.
   *
   * @param type The type to upload to perform.
   * @param message The text to publish.
   * @param filename The name to call the file on Twitter.
   *                 Will be ignored if only publishing text.
   * @param inputStream The InputStream of the file to upload to Twitter.
   * @return The ID of the post if successful, or {@code null} on failure.
   */
  public Long publish(PublishType type, String message, String filename, InputStream inputStream) {
    StatusUpdate update = new StatusUpdate(message);
    if (type.equals(PublishType.PHOTO) || type.equals(PublishType.VIDEO)) {
      update.setMedia(filename, inputStream);
    }

    try {
      Status status = twitterClient.updateStatus(update);

      return status.getId();
    } catch (TwitterException e) {
      LOG.error("Unable to publish to Twitter. "
          + "Twitter error code: {}", e.getErrorCode(), e);
      return null;
    }
  }

  /**
   * Retrieves a new OAuth URL from Twitter.
   * Should only be called if the TwitterService was constructed without an authenticating user.
   *
   * @param redirectUrl The URL that Twitter should redirect to after the user authenticates.
   * @return The URL to redirect to for authentication or {@code null} if unable to fetch the URL.
   */
  public TwitterOAuthRequest getAuthorizationUrl(String redirectUrl) {
    try {
      RequestToken requestToken = twitterClient.getOAuthRequestToken(redirectUrl);

      return new TwitterOAuthRequest(
          requestToken.getToken(),
          requestToken.getTokenSecret(),
          requestToken.getAuthorizationURL());
    } catch (TwitterException e) {
      LOG.error("Unable to get authorization URL from Twitter. "
          + "Twitter error code: {}", e.getErrorCode(), e);
      return null;
    }
  }

  public TwitterAccessToken getOAuthAccessToken(String requestToken,
                                                String requestTokenSecret,
                                                String oauthVerifier) {
    try {
      RequestToken token = new RequestToken(requestToken, requestTokenSecret);
      AccessToken accessToken = twitterClient.getOAuthAccessToken(token, oauthVerifier);

      return new TwitterAccessToken(
          accessToken.getToken(),
          accessToken.getTokenSecret());
    } catch (TwitterException e) {
      LOG.error("Unable to get OAuth tokens from Twitter. "
          + "Twitter error code: {}", e.getErrorCode(), e);
      return null;
    }
  }
}
