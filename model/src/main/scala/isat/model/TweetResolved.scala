package isat.model

/**
 * @author Tarek Auel
 * @since June 14, 2015.
 */
class TweetResolved private(override val tweetId: Long, val user: User) extends Tweet(tweetId) {

}

object TweetResolved {
  
  def getResolved(t: Tweet, u: User): TweetResolved = {
    val tweet = new TweetResolved(t.tweetId, u)
    tweet.text = t.text
    tweet.source = t.source
    tweet.isTruncated = t.isTruncated
    tweet.inReplyToStatusId = t.inReplyToStatusId
    tweet.inReplyToUserId = t.inReplyToUserId
    tweet.favoriteCount = t.favoriteCount
    tweet.isRetweet = t.isRetweet
    tweet.retweetCount = t.retweetCount
    tweet.userId = t.userId
    tweet.contributors = t.contributors
    tweet.lang = t.lang
    tweet.hashTags = t.hashTags
    tweet.mentionedUsers = t.mentionedUsers
    tweet.createdAt = t.createdAt
    tweet
  }
  
}
