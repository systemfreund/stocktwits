package com.systemfreund.stocktwits

import org.scalatest.FunSuite
import akka.actor.ActorSystem
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Success, Failure}

class IntegrationTest extends FunSuite {

  val system = ActorSystem()

  test("get 'streams/symbol'") {
    val streams = new Streams(system)
    val future = streams.symbol("GOOG")
    val result = Await.result(future, 5 seconds)

    result match {
      case Failure(exception) => fail("Unexpected failure", exception)
      case Success(value) => assert(value.symbol.ticker == "GOOG")
    }
  }

  test("unknown 'streams/symbol'") {
    val streams = new Streams(system)
    val future = streams.symbol("GOOGOO")
    val result = Await.result(future, 5 seconds)

    result match {
      case Failure(e: ApiError) => assert(e.response.response.status == 404)
      case Success(value) => fail("Unexpected success")
      case _ => fail("Unexpected case")
    }
  }

}
