package io.github.joker1007

case class DirectMessageReceivedEvent(from: String, body: String) extends Notifiable {
  val eventName = "direct_message"
  def subject: String = "DM from " + from
  def description: String = body
}
