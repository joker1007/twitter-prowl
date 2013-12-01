package io.github.joker1007.twitter_prowl

import java.util.concurrent.{TimeUnit, LinkedBlockingDeque}
import com.twitter.hbc.core.endpoint.UserstreamEndpoint
import com.twitter.hbc.httpclient.auth.OAuth1
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Constants
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import org.json4s._
import org.json4s.native.JsonMethods
import java.util.Properties

object App {
  val properties = new Properties()
  properties.load(getClass.getResourceAsStream("/twitter-prowl.properties"))

  private[this] val CONSUMER_KEY = properties.getProperty("twitter.consumer_key")
  private[this] val CONSUMER_SECRET = properties.getProperty("twitter.consumer_secret")
  private[this] val ACCESS_TOKEN = properties.getProperty("twitter.access_token")
  private[this] val ACCESS_SECRET = properties.getProperty("twitter.access_secret")

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
      .hosts(Constants.USERSTREAM_HOST)
      .endpoint(endpoint)
      .authentication(auth)
      .processor(new StringDelimitedProcessor(queue))
      .build()

    client.connect()

    val account = MyAccount.fetchCredential
    val parser = new MessageParser(account)

    while (true) {
      if (client.isDone) {
        println("client connection is closed")
      } else {
        val msg = queue.poll(5, TimeUnit.SECONDS)
        if (msg != null) {
          val json = JsonMethods.parse(msg)
          for (ev <- parser.parse(json)) {
            println(ev)
            ProwlNotifier.notify(ev)
          }
        }
      }
    }

    client.stop()

    println("End")
  }
}


