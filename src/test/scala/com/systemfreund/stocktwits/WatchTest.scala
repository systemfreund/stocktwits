package com.systemfreund.stocktwits

import org.scalatest.FunSuite
import akka.actor.ActorSystem

class WatchTest extends FunSuite {

  implicit val system = ActorSystem()

  import system.dispatcher

  test("test") {
    val obs = Watch(Stream(Symbol("GOOG")))

    obs.subscribe(next => println(s"Got $next"))

    val first = obs.toBlockingObservable.first
    println(s"First $first")

  }


}
