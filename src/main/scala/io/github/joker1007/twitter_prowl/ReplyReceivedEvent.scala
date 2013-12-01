package io.github.joker1007.twitter_prowl

case class ReplyReceivedEvent(tweet: Tweet) extends Notifiable {
  val event = "reply"
  def subject: String = "Reply from " + tweet.user.screenName
  def description: String = tweet.text
}
