package com.systemfreund.stocktwits

import org.scalatest.{Matchers, FunSuite}
import akka.actor.ActorSystem
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import spray.httpx.SprayJsonSupport._
import Models.JsonProtocol._
import Stream._
import Streams._
import com.systemfreund.stocktwits.Parameters.Limit

class IntegrationTest extends FunSuite with Matchers {

  implicit val system = ActorSystem()

  import system.dispatcher

  test("get 'streams/symbol'") {
    val stream = Stream(Symbol("GOOG"))
    val future = stream()
    val result = Await.result(future, 5 seconds)

    result.symbol.ticker shouldEqual "GOOG"
  }

  test("get 'streams/symbol' with limit") {
    val stream = Stream(Symbol("GOOG"))
    val future = stream(Limit(2))
    val result = Await.result(future, 5 seconds)

    result.messages should have size 2
  }

  test("unknown 'streams/symbol'") {
    val stream = Stream(Symbol("GOOG"))
    val future = stream()

    try {
      Await.result(future, 5 seconds)
    } catch {
      case ApiError(err) => assert(err.response.status == 404)
      case e: Throwable => fail("Unexpected exception", e)
    }
  }

  test("get 'streams/user'") {
    val stream = Stream(User("dschn"))
    val future = stream()
    val result = Await.result(future, 5 seconds)

    result.user.username shouldEqual "dschn"
  }

  test("get 'streams/user' with limit") {
    val stream = Stream(User("dschn"))
    val future = stream(Limit(1))
    val result = Await.result(future, 5 seconds)

    result.messages should have size 1
  }

  test("unknown 'streams/user'") {
    val stream = Stream(User("-1"))
    val future = stream()

    try {
      Await.result(future, 5 seconds)
    } catch {
      case ApiError(err) => assert(err.response.status == 404)
      case e: Throwable => fail("Unexpected exception", e)
    }
  }

}
