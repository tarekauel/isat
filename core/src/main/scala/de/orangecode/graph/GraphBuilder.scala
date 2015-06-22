package de.orangecode.graph

import de.orangecode.Context
import de.orangecode.entitymanager.{HashTagManager, TweetManager, UserManager}
import isat.model.{HashTag, Tweet, User, Vertex}
import org.apache.spark.graphx.{Edge, Graph}

/**
 * @author Tarek Auel
 * @since June 05, 2015.
 */
object GraphBuilder {

  def buildGraph(ctx: Context): Graph[Vertex, String] = {

    def checkUser(userId: Long)(v: Vertex): Boolean = {
      v match {
        case u: User if u.userId == userId => true
        case _ => false
      }
    }

    def checkTweet(tweetId: Long)(v: Vertex): Boolean = {
      v match {
        case t: Tweet if t.tweetId == tweetId => true
        case _ => false
      }
    }

    def checkHashTag(hashTag: String)(v: Vertex): Boolean = {
      v match {
        case h: HashTag if h.text == hashTag => true
        case _ => false
      }
    }

    val allTweets = TweetManager.get.getAll.collect().toList
    val allHashTags = HashTagManager.get.getAll.collect().toList
    val allUsers = UserManager.get.getAll.collect().toList

    val vertices: List[(Vertex, Int)] =
      (allHashTags ++ allTweets ++ allUsers).zipWithIndex

    val edges: List[Edge[String]] =
      allTweets.flatMap((t) => {
        val tweetVid = lookup(vertices, checkTweet(t.tweetId)).get
        val author =
          List(lookup(vertices, checkUser(t.userId)))
            .filter(_.isDefined)
            .map((a) => new Edge[String](a.get, tweetVid, "isAuthorOf"))
        val contributor =
          t.contributors.map((x) => lookup(vertices, checkUser(x)))
          .filter(_.isDefined)
          .map((c) => new Edge[String](c.get, tweetVid, "contributedTo"))
        val mentioned =
          t.mentionedUsers.map((x) => lookup(vertices, checkUser(x)))
          .filter(_.isDefined)
          .map((m) => new Edge[String](tweetVid, m.get, "mentions"))
        /*val hashTags =
          t.hashTags.map((x) => lookup(vertices, checkHashTag(x.text)))
          .filter(_.isDefined)
          .map((h) => new Edge[String](tweetVid, h.get, "refersTo"))*/
        author ++ contributor ++ mentioned //++ hashTags
      })

    Graph[Vertex, String](
      ctx.sc.parallelize(vertices.map((t) => (t._2.toLong, t._1))),
      ctx.sc.parallelize(edges))
  }


  def lookup(vertices: List[(Vertex, Int)], f: Vertex => Boolean)
      : Option[Int] = {

    val result = vertices.filter((t) => f(t._1)).map(_._2)
    result.headOption
  }

}
