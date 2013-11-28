package io.github.joker1007

trait Notifiable {
  val eventName: String
  def subject: String
  def description: String
}
