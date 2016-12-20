package repository

/**
  * Created by marksu on 12/19/16.
  */
object RedisSchema {

  // Sorted Set of users followed by uid
  def followers(uid: String): String = s"followers:${uid}"

  // Sorted Set of users following uid
  def following(uid: String): String = s"following:${uid}"

  def next_post_id: String = "global:nextPostId"


}