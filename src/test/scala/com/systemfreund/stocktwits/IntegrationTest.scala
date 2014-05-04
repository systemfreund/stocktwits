package com.systemfreund.stocktwits

import org.scalatest.FunSuite
import akka.actor.ActorSystem
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import scala.language.postfixOps

class IntegrationTest extends FunSuite {

  implicit val system = ActorSystem()
  import system.dispatcher

  test("get 'streams/symbol'") {
    val stream = Stream(Symbol("GOOG"))
    val future = stream(None)
    val result = Await.result(future, 5 seconds)

    assert(result.symbol.ticker == "GOOG")
  }

  test("unknown 'streams/symbol'") {
    val stream = Stream(Symbol("GOOG"))
    val future = stream(None)

    try {
      Await.result(future, 5 seconds)
    } catch {
      case ApiError(err) => assert(err.response.status == 404)
      case e: Throwable => fail("Unexpected exception", e)
    }
  }

}
