package io.github.joker1007

case class User(id: Long, screenName: String) extends EventTarget with EventSource
