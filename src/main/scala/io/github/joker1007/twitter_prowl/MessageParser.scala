package io.github.joker1007.twitter_prowl

import org.json4s._

class MessageParser(myAccount: MyAccount) {
  implicit val format = DefaultFormats

  def parse(json: JValue): Option[Notifiable] = {
    val transformed = json transformField {
      case ("screen_name", x) => ("screenName", x)
      case ("user_mentions", x) => ("userMentions", x)
      case ("target_object", x) => ("targetObject", x)
    }

    val replyEvent = for {
      tweet <- transformed.extractOpt[Tweet] if tweet.userMentions map (_.id) contains myAccount.id
    } yield ReplyReceivedEvent(tweet)
    if (!replyEvent.isEmpty)
      return replyEvent

    val jDM = transformed \ "direct_message"
    val dmEvent = for {
      directMessage <- jDM.extractOpt[DirectMessage] if directMessage.recipient.id == myAccount.id
    } yield DirectMessageReceivedEvent(directMessage)
    if (!dmEvent.isEmpty)
      return dmEvent

    val event = (json \ "event").extractOpt[String]
    val streamEvent = event match {
      case Some(n) =>
        n match {
          case "favorite" => parseFavoriteMessage(transformed)
          case _ => None
        }
      case _ => None
    }
    if (!streamEvent.isEmpty)
      return streamEvent

    None
  }

  def parseFavoriteMessage(json: JValue): Option[FavoritedEvent] = {
    for {
      favoriteEvent <- json.extractOpt[FavoritedEvent] if favoriteEvent.target.id == myAccount.id
    } yield favoriteEvent
  }
}
