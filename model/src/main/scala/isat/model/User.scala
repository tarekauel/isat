package isat.model

import java.util.Date

/**
 * @author Tarek Auel
 * @since June 04, 2015.
 */
case class User(userId: Long) extends Twitter {


  override def getId: Long = userId

  var name: String = null
    var screenName: String = null
    var location: String = null
    var description: String = null
    var contributorsEnabled: Boolean = false
    var profileImageUrl: String = null
    var biggerProfileImageUrl: String = null
    var miniProfileImageUrl: String = null
    var isDefaultProfileImage: Boolean = false
    var userUrl: String = null
    var isProtected: Boolean = false
    var followersCount: Int = 0
    var followingsCount: Int = 0
    var createdAt: Date = null
    var favoritesCount: Int = 0
    var lang: String = null
    var statusCount: Int = 0
    var geoEnabled: Boolean = false
    var verified: Boolean = false
    var isTranslator: Boolean = false
    var listedCount: Int = 0
    var watched: Boolean = false
    var lastUpdate: Long = 0L

  override def getLabel: String = s"$screenName ($name)"

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case u: User if u.userId == userId => true
      case _ => false
    }
  }
}

object User {

  val collectionName = "twitterUser"

  def getUser(in: String): User  = {
    //JacksMapper.readValue[User](in)
    Gson.gson.fromJson(in, classOf[User])
  }
  
  def getUser(in: twitter4j.User): User = {
    val u = new User(in.getId)
    u.name = in.getName
    u.screenName = in.getScreenName
    u.location = in.getLocation
    u.description = in.getDescription
    u.contributorsEnabled = in.isContributorsEnabled
    u.profileImageUrl = in.getProfileImageURL
    u.biggerProfileImageUrl = in.getBiggerProfileImageURL
    u.miniProfileImageUrl = in.getMiniProfileImageURL
    u.isDefaultProfileImage = in.isDefaultProfileImage
    u.userUrl = in.getURL
    u.isProtected = in.isProtected
    u.followersCount = in.getFollowersCount
    u.followingsCount = in.getFriendsCount
    u.createdAt = new Date(in.getCreatedAt.getTime)
    u.favoritesCount = in.getFavouritesCount
    u.lang = in.getLang
    u.statusCount = in.getStatusesCount
    u.geoEnabled = in.isGeoEnabled
    u.verified = in.isVerified
    u.isTranslator = in.isTranslator
    u.listedCount = in.getListedCount
    u
  }
  
 /*def getUser(in: Imports.DBObject): User = {
    new User(
      in.get("userId").asInstanceOf[Long],
      in.get("name").asInstanceOf[String],
      in.get("screenName").asInstanceOf[String],
      in.get("location").asInstanceOf[String],
      in.get("description").asInstanceOf[String],
      in.get("contributorsEnabled").asInstanceOf[Boolean],
      in.get("profileImageUrl").asInstanceOf[String],
      in.get("biggerProfileImageUrl").asInstanceOf[String],
      in.get("miniProfileImageUrl").asInstanceOf[String],
      in.get("isDefaultProfileImage").asInstanceOf[Boolean],
      in.get("userUrl").asInstanceOf[String],
      in.get("isProtected").asInstanceOf[Boolean],
      in.get("followersCount").asInstanceOf[Int],
      in.get("followingsCount").asInstanceOf[Int],
      new Date(in.get("createdAt").asInstanceOf[java.util.Date].getTime),
      in.get("favoritesCount").asInstanceOf[Int],
      in.get("lang").asInstanceOf[String],
      in.get("statusCount").asInstanceOf[Int],
      in.get("geoEnabled").asInstanceOf[Boolean],
      in.get("verified").asInstanceOf[Boolean],
      in.get("isTranslator").asInstanceOf[Boolean],
      in.get("listedCount").asInstanceOf[Int]
    )
  }

  def getMongo(in: User): Imports.DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "userId" -> in.userId
    builder += "name" -> in.name
    builder += "screenName" -> in.screenName
    builder += "location" -> in.location
    builder += "description" -> in.description
    builder += "contributorsEnabled" -> in.contributorsEnabled
    builder += "profileImageUrl" -> in.profileImageUrl
    builder += "biggerProfileImageUrl" -> in.biggerProfileImageUrl
    builder += "miniProfileImageUrl" -> in.miniProfileImageUrl
    builder += "isDefaultProfileImage" -> in.isDefaultProfileImage
    builder += "userUrl" -> in.userUrl
    builder += "isProtected" -> in.isProtected
    builder += "followersCount" -> in.followersCount
    builder += "followingsCount" -> in.followingsCount
    builder += "createdAt" -> in.createdAt
    builder += "favoritesCount" -> in.favoritesCount
    builder += "lang" -> in.lang
    builder += "statusCount" -> in.statusCount
    builder += "geoEnabled" -> in.geoEnabled
    builder += "verified" -> in.verified
    builder += "isTranslator" -> in.isTranslator
    builder += "listedCount" -> in.listedCount
    builder.result()
  }*/
  
}





