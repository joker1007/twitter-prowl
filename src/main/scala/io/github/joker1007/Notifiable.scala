package io.github.joker1007

trait Notifiable {
  val event: String
  def subject: String
  def description: String
}
