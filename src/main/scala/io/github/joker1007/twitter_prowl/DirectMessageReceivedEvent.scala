package io.github.joker1007.twitter_prowl

case class DirectMessageReceivedEvent(directMessage: DirectMessage) extends Notifiable {
  val event = "direct_message"
  def subject: String = "DM from " + directMessage.sender.screenName
  def description: String = directMessage.text
}
