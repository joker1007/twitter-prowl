package io.github.joker1007

abstract class StreamEvent extends Notifiable {
  def target: String
  def source: String
  def targetObject: Option[Target]
}

trait Target {

}

case class FollowedEvent(eventName: String, target: String, source: String, targetObject: Option[Target]) extends StreamEvent {
  def subject: String = "Followed by " + source
  def description: String = ""
}

case class FavoritedEvent(eventName: String, target: String, source: String, targetObject: Option[Tweet]) extends StreamEvent {
  def subject: String = "Favorited by " + source
  def description: String = targetObject map (_.body) getOrElse("")
}
