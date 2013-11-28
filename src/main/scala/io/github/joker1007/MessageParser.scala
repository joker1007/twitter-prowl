package io.github.joker1007

import org.json4s._
import org.json4s.native.JsonMethods

class MessageParser(myAccount: MyAccount) {
  private[this] val account = myAccount

  def parse(json: JValue): Option[Notifiable] = {
    val jReplyTo = json \ "in_reply_to_user_id"
    val replyEvent = jReplyTo match {
      case JInt(replyTo) => parseReplyMessage(json)
      case _ => None
    }
    if (!replyEvent.isEmpty)
      return replyEvent

    val jDM = json \ "direct_message"
    val dmEvent = jDM match {
      case JObject(dm) => parseDirectMessage(jDM)
      case _ => None
    }
    if (!dmEvent.isEmpty)
      return dmEvent

    val jEventName = json \ "event"
    val streamEvent = jEventName match {
      case JString(eventName) =>
        eventName match {
          case "favorite" => parseFavoriteMessage(json)
          case _ => None
        }
      case _ => None
    }
    if (!streamEvent.isEmpty)
      return streamEvent

    None
  }

  def parseReplyMessage(json: JValue): Option[Notifiable] = {
    val JArray(mentions) = json \ "entities" \ "user_mentions"
    val mentionIds = mentions map (_ \ "id") map {
      case JInt(id) => id.toLong
      case _ => 0
    }
    if (!mentionIds.contains(account.id))
      return None

    val jText = json \ "text"
    val jScreenName = json \ "user" \ "screen_name"
    (jText, jScreenName) match {
      case (JString(text), JString(screenName)) =>
        Some(ReplyReceivedEvent(screenName, text))
      case _ => None
    }
  }

  def parseDirectMessage(json: JValue): Option[Notifiable] = {
    val jText = json \ "text"
    val jScreenName = json \ "sender" \ "screen_name"
    (jText, jScreenName) match {
      case (JString(text), JString(screenName)) =>
        Some(DirectMessageReceivedEvent(screenName, text))
      case _ => None
    }
  }

  def parseFavoriteMessage(json: JValue): Option[Notifiable] = {
    try {
      val JString(source) = json \ "source" \ "screen_name"
      val JInt(targetId) = json \ "target" \ "id"
      val JString(target) = json \ "target" \ "screen_name"
      val JString(text) = json \ "target_object" \ "text"
      val JInt(tweetId) = json \ "target_object" \ "id"
      val JString(tweetScreenName) = json \ "target_object" \ "user" \ "screen_name"

      if (targetId.toLong == account.id)
        Some(FavoritedEvent("favorite", target, source, Some(Tweet(tweetId.toLong, tweetScreenName, text))))
      else
        None
    } catch {
      case e: MatchError => None
    }
  }
}
