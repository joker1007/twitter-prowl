package io.github.joker1007

abstract class StreamEvent[A] extends Notifiable {
  def target: EventTarget
  def source: EventSource
}

trait EventTarget

trait EventSource

trait TargetObject

case class FavoritedEvent(event: String, target: User, source: User, targetObject: Tweet) extends StreamEvent {
  def subject: String = "Favorited by " + source.screenName
  def description: String = targetObject.text
}
