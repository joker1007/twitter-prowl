package io.github.joker1007

import java.util.concurrent.{TimeUnit, LinkedBlockingDeque}
import com.twitter.hbc.core.endpoint.UserstreamEndpoint
import com.twitter.hbc.httpclient.auth.OAuth1
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Constants
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import org.json4s._
import org.json4s.native.JsonMethods
import twitter4j.conf.ConfigurationBuilder
import twitter4j.TwitterFactory
import java.util.Properties

object App {
  private[this] val properties = new Properties()
  properties.load(getClass.getResourceAsStream("/twitter.properties"))

  val CONSUMER_KEY = properties.getProperty("consumer_key")
  val CONSUMER_SECRET = properties.getProperty("consumer_secret")
  val ACCESS_TOKEN = properties.getProperty("access_token")
  val ACCESS_SECRET = properties.getProperty("access_secret")

  def main(args: Array[String]) {
    connect(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_SECRET)
  }

  def connect(consumerKey: String, consumerSecret: String, token: String, secret: String) {
    val queue = new LinkedBlockingDeque[String](10000)
    val endpoint = new UserstreamEndpoint()
    endpoint.allReplies(true)
    endpoint.stallWarnings(false)

    val auth = new OAuth1(consumerKey, consumerSecret, token, secret)

    val client = new ClientBuilder()
      .name("joker1007-hbc-sample")
      .hosts(Constants.USERSTREAM_HOST)
      .endpoint(endpoint)
      .authentication(auth)
      .processor(new StringDelimitedProcessor(queue))
      .build()

    client.connect()

    val cb = new ConfigurationBuilder()
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(CONSUMER_KEY)
      .setOAuthConsumerSecret(CONSUMER_SECRET)
      .setOAuthAccessToken(ACCESS_TOKEN)
      .setOAuthAccessTokenSecret(ACCESS_SECRET)
    val tf = new TwitterFactory(cb.build())
    val twitter = tf.getInstance()
    val info = twitter.verifyCredentials()
    val account = MyAccount(info.getId, info.getScreenName)

    while (true) {
      if (client.isDone) {
        println("client connection is closed")
      } else {
        val msg = queue.poll(5, TimeUnit.SECONDS)
        if (msg == null) {
          println("Did not received message")
        } else {
          println(msg)
          val json = JsonMethods.parse(msg)
          for {
            JObject(info) <- json
            JField("in_reply_to_user_id", JInt(reply_to)) <- info
            JField("text", JString(text)) <- info
            JField("user", JObject(user)) <- info
            JField("screen_name", JString(screen_name)) <- user
          } {
            if (reply_to.toLong == account.id)
              println(ReplyReceivedEvent(screen_name, text))
          }
        }
      }
    }

    client.stop()

    println("End")
  }
}


