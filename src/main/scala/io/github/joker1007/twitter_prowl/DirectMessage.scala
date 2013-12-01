package io.github.joker1007.twitter_prowl


case class DirectMessage(sender: User, recipient: User, text: String) extends TargetObject
