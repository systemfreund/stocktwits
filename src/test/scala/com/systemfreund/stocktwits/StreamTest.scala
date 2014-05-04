package com.systemfreund.stocktwits

import org.scalatest.FunSuite
import spray.http._
import scala.concurrent.{Await, Future}
import spray.http.HttpRequest
import spray.http.HttpResponse
import akka.actor.ActorSystem
import scala.concurrent.duration._
import spray.httpx.PipelineException
import scala.language.postfixOps
import com.systemfreund.stocktwits.Models.SymbolStreamResponse
import spray.httpx.SprayJsonSupport._
import Models.JsonProtocol._

class StreamTest extends FunSuite {

  import scala.concurrent.ExecutionContext.Implicits.global

  def withResponse(data: String, status: StatusCode = StatusCodes.OK): HttpRequest => Future[HttpResponse] = {
    request => Future.successful(HttpResponse(status, HttpEntity(ContentTypes.`application/json`, data)))
  }

  test("fail on empty response") {
    val stream = Stream[SymbolStreamResponse](Symbol("TEST"), withResponse(""))
    val future = stream(None)

    intercept[PipelineException] {
      Await.result(future, 5 seconds)
    }
  }

  test("fail on empty object") {
    val stream = Stream[SymbolStreamResponse](Symbol("TEST"), withResponse("{}"))
    val future = stream(None)

    intercept[PipelineException] {
      Await.result(future, 5 seconds)
    }
  }

  test("expect api error 404") {
    val stream = Stream[SymbolStreamResponse](Symbol("TEST"), withResponse( """{ "response": {"status": 404}, "errors": [{"message": "notfound"}] }""", StatusCodes.NotFound))
    val future = stream(None)

    try {
      Await.result(future, 5 seconds)
    } catch {
      case ApiError(resp) => {
        assert(resp.response.status == 404)
        assert(resp.errors.size == 1)
        assert(resp.errors(0).message == "notfound")
      }
    }
  }

  test("expect RuntimeException when unmarshalling error response fails ") {
    val stream = Stream[SymbolStreamResponse](Symbol("TEST"), withResponse( """{ "response": {"status": 404} }""", StatusCodes.NotFound))
    val future = stream(None)

    try {
      Await.result(future, 5 seconds)
    } catch {
      case e: RuntimeException => // ok, because "errors" field is missing
      case e: Throwable => fail("Unexpected exception", e)
    }
  }

}
