package io.github.joker1007

case class ReplyReceivedEvent(from: String, body: String) extends Event {
  def subject: String = "Reply from " + from
  def description: String = body
}
