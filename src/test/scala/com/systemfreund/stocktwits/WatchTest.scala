package com.systemfreund.stocktwits

import org.scalatest.FunSuite
import akka.actor.ActorSystem
import spray.httpx.SprayJsonSupport._
import Models.JsonProtocol._
import com.systemfreund.stocktwits.Models.SymbolStreamResponse

class WatchTest extends FunSuite {

  implicit val system = ActorSystem()

  import system.dispatcher

  test("test") {
    val stream = Stream[SymbolStreamResponse](Symbol("GOOG"))
    val obs = Watch(stream)

    obs.subscribe(next => println(s"Got $next"))

    val first = obs.toBlockingObservable.first
    println(s"First $first")

  }


}
