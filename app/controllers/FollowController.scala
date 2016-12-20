package controllers

import javax.inject.Inject

import play.api.mvc._
import services.FollowService
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by marksu on 12/19/16.
  */
class FollowController @Inject()(followService: FollowService) extends Controller {

  //TODO: get the userId from RequestHeader
  def follow(userId: String, toFollowUid: String) = Action { implicit request =>
    followService.follow(userId, toFollowUid)
    Ok
  }

  //TODO: get the userId from RequestHeader
  def unfollow(userId: String, unFollowUid: String) = Action { implicit request =>
    followService.unfollow(userId, unFollowUid)
    Ok
  }

  //TODO: get the userId from RequestHeader
  def getFollowing(userId: String) = Action.async {
    followService.getFollowing(userId).map { following =>
      Ok(Json.obj("following" -> following))
    }
  }

  //TODO: get the userId from RequestHeader
  def getFollowers(userId: String) = Action.async {
    followService.getFollowers(userId).map { followers =>
      Ok(Json.obj("followers" -> followers))
    }
  }

  //  def unfollow(to_unfollow: String) = TODO


}
