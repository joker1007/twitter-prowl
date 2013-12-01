package io.github.joker1007

case class Tweet(id: Long, text: String, user: User, entities: Entity) extends TargetObject {
  def userMentions = entities.userMentions
}

case class Entity(userMentions: List[User])
