package services

import javax.inject._

import akka.actor._
import play.api.{Configuration, Logger, Play}
import redis.{RedisClient, RedisCluster, RedisServer}
import repository.RedisSchema

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import redis.protocol._

/**
  * Created by marksu on 12/19/16.
  */
@Singleton
class FollowService @Inject()(implicit _system: ActorSystem, config: Configuration) {

  //TODO: refoactoring move to DAO
//  val redisClient = RedisClient()

  val host = "10.8.3.124"
  val posts= Seq(7000, 7001, 7002)
  val redisClient = RedisCluster(posts.map(p=>RedisServer(host,p)))

  // follow a user
  def follow(uid: String, toFollowUid: String): Future[Unit] = {
    Logger.debug(s"$uid following $toFollowUid")

    val timestamp = System.currentTimeMillis

    for {
      _ <- redisClient.zadd(RedisSchema.following(uid), (timestamp, toFollowUid))
      _ <- redisClient.zadd(RedisSchema.followers(toFollowUid), (timestamp, uid))
    } yield {}
  }

  // unfollow a user
  def unfollow(uid: String, unFollowUid: String): Future[Unit] = {
    Logger.debug(s"$uid unfollowing $unFollowUid")

    for {
      _ <- redisClient.zrem(RedisSchema.following(uid), (unFollowUid))
      _ <- redisClient.zrem(RedisSchema.followers(unFollowUid), (uid))
    } yield {}
  }

  // TODO: pagination
  // Get the following of the User
  def getFollowing(uid: String): Future[Seq[String]] = {
    for{
      following <- redisClient.zrange(RedisSchema.following(uid), 0, -1)
    } yield (following.map(_.utf8String))
  }

  // TODO: pagination
  // Get the followers of the User
  def getFollowers(uid: String): Future[Seq[String]] = {
    for{
      followers <- redisClient.zrange(RedisSchema.followers(uid), 0, -1)
    } yield (followers.map(_.utf8String))

  }

  // Get the following in common
  def getCommonFollowing(uid: String, intersectionUid: String): Future[Seq[String]] = {
    Logger.debug(s"$uid get the common following with $intersectionUid")
    for {
      _ <- redisClient.zinterstore(RedisSchema.commonFollowing(uid,intersectionUid), RedisSchema.following(uid), Seq(RedisSchema.following(intersectionUid)))
      commonFollowing <- redisClient.zrange(RedisSchema.commonFollowing(uid,intersectionUid), 0, -1)
    } yield {
//      Logger.debug("" + commonFollowing.map(_.utf8String))
      commonFollowing.map(_.utf8String )
    }

  }

  // Get the followers in common
  def getCommonFollowers(uid: String, intersectionUid: String): Future[Seq[String]] = {
    Logger.debug(s"$uid get the common followers with $intersectionUid")
    for {
      _ <- redisClient.zinterstore(RedisSchema.commonFollowers(uid,intersectionUid), RedisSchema.followers(uid), Seq(RedisSchema.followers(intersectionUid)))
      commonFollowing <- redisClient.zrange(RedisSchema.commonFollowers(uid,intersectionUid), 0, -1)
    } yield {
      //      Logger.debug("" + commonFollowing.map(_.utf8String))
      commonFollowing.map(_.utf8String )
    }

  }

}
