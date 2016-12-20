package services

import javax.inject._

import akka.actor._
import play.api.Logger
import redis.RedisClient
import repository.RedisSchema

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by marksu on 12/19/16.
  */
@Singleton
class FollowService @Inject()(implicit _system: ActorSystem) {

  //TODO: refoactoring move to DAO
  val redis = RedisClient(host="10.8.3.124", port=7000)

  // follow a user
  def follow(uid: String, toFollowUid: String): Future[Unit] = {
    Logger.debug(s"$uid following $toFollowUid")

    val timestamp = System.currentTimeMillis

    for {
      _ <- redis.zadd(RedisSchema.following(uid), (timestamp, toFollowUid))
      _ <- redis.zadd(RedisSchema.followers(toFollowUid), (timestamp, uid))
    } yield {}
  }

  // unfollow a user
  def unfollow(uid: String, unFollowUid: String): Future[Unit] = {
    Logger.debug(s"$uid unfollowing $unFollowUid")

    for {
      _ <- redis.zrem(RedisSchema.following(uid), (unFollowUid))
      _ <- redis.zrem(RedisSchema.followers(unFollowUid), (uid))
    } yield {}
  }

  // TODO: pagination
  // Get the following of the User
  def getFollowing(uid: String): Future[Seq[String]] = {
    for{
      following <- redis.zrange(RedisSchema.following(uid), 0, -1)
    } yield (following.map(_.utf8String))
  }

  // TODO: pagination
  // Get the followers of the User
  def getFollowers(uid: String): Future[Seq[String]] = {
    for{
      followers <- redis.zrange(RedisSchema.followers(uid), 0, -1)
    } yield (followers.map(_.utf8String))

  }

  // Get the following in common
  def getCommonFollowing(uid: String, intersectionUid: String): Future[Seq[String]] = {
    Logger.debug(s"$uid get the common following with $intersectionUid")
    for {
      _ <- redis.zinterstore(RedisSchema.commonFollowing(uid,intersectionUid), RedisSchema.following(uid), Seq(RedisSchema.following(intersectionUid)))
      commonFollowing <- redis.zrange(RedisSchema.commonFollowing(uid,intersectionUid), 0, -1)
    } yield {
//      Logger.debug("" + commonFollowing.map(_.utf8String))
      commonFollowing.map(_.utf8String )
    }

  }

  // Get the followers in common
  def getCommonFollowers(uid: String, intersectionUid: String): Future[Seq[String]] = {
    Logger.debug(s"$uid get the common followers with $intersectionUid")
    for {
      _ <- redis.zinterstore(RedisSchema.commonFollowers(uid,intersectionUid), RedisSchema.followers(uid), Seq(RedisSchema.followers(intersectionUid)))
      commonFollowing <- redis.zrange(RedisSchema.commonFollowers(uid,intersectionUid), 0, -1)
    } yield {
      //      Logger.debug("" + commonFollowing.map(_.utf8String))
      commonFollowing.map(_.utf8String )
    }

  }

}
