package io.github.joker1007.twitter_prowl


case class User(id: Long, screenName: String) extends EventTarget with EventSource
