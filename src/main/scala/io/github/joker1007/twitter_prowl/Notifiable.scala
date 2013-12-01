package io.github.joker1007.twitter_prowl

trait Notifiable {
  val event: String
  def subject: String
  def description: String
}
