package io.github.joker1007.twitter_prowl

import dispatch._
import Defaults._

object ProwlNotifier extends Notifier {
  private[this] val API_URL= "https://api.prowlapp.com/publicapi/add"
  private[this] val PROWL_TOKEN = App.properties.getProperty("prowl.token")

  def notify(ev: Notifiable) {
    Http(reqWithParams(ev) OK as.String)
  }

  private[this] def req = url(API_URL).POST
  private[this] def reqWithParams(ev: Notifiable) = req << Map(
    "apikey" -> PROWL_TOKEN,
    "application" -> "twitter-notification",
    "event" -> ev.subject,
    "description" -> ev.description
  )
}
