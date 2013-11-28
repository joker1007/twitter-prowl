package io.github.joker1007

import twitter4j.conf.ConfigurationBuilder
import twitter4j.TwitterFactory
import App.properties

case class MyAccount(id: Long, screenName: String)

object MyAccount {
  def fetchCredential: MyAccount = {
    val cb = new ConfigurationBuilder()
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(properties.getProperty("twitter.consumer_key"))
      .setOAuthConsumerSecret(properties.getProperty("twitter.consumer_secret"))
      .setOAuthAccessToken(properties.getProperty("twitter.access_token"))
      .setOAuthAccessTokenSecret(properties.getProperty("twitter.access_secret"))
    val tf = new TwitterFactory(cb.build())
    val twitter = tf.getInstance()
    val info = twitter.verifyCredentials()
    MyAccount(info.getId, info.getScreenName)
  }
}
