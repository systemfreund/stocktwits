package com.systemfreund.stocktwits

import org.scalatest.FunSuite
import akka.actor.ActorSystem
import spray.httpx.SprayJsonSupport._
import Models.JsonProtocol._
import com.systemfreund.stocktwits.Streams._

class WatchTest extends FunSuite {

  implicit val system = ActorSystem()

  import system.dispatcher

  test("test") {
    val stream = Stream(Symbol("GOOG"))
    val obs = Watch(stream)

    obs.subscribe(next => println(s"Got $next"))

    val first = obs.toBlockingObservable.first
    println(s"First $first")

  }


}
