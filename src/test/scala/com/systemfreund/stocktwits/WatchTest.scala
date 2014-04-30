package com.systemfreund.stocktwits

import org.scalatest.FunSuite
import akka.actor.ActorSystem
import spray.http._
import scala.concurrent.Future
import spray.http.HttpRequest
import spray.http.HttpResponse
import rx.observables.BlockingObservable

class WatchTest extends FunSuite {

  implicit val system = ActorSystem()

  import system.dispatcher

  test("test") {
    val watch = Watch(Streams())
    val obs = watch.symbol("GOOG")

    obs.toBlockingObservable.foreach(msg => println(s"got message: $msg"))
  }


}
