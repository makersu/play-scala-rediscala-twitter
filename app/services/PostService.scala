package services

import javax.inject.{Singleton, _}

import akka.actor._
import redis.RedisClient

/**
  * Created by marksu on 12/19/16.
  */
@Singleton
class PostService @Inject()(implicit _system: ActorSystem) {

  val redis = RedisClient()

}
