package io.github.joker1007.twitter_prowl

trait Notifier {
  def notify(ev: Notifiable)
}
