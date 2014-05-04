package com.systemfreund.stocktwits

import org.scalatest.{Matchers, FunSuite}
import spray.http._
import scala.concurrent.{Await, Future}
import spray.http.HttpRequest
import spray.http.HttpResponse
import scala.concurrent.duration._
import spray.httpx.PipelineException
import scala.language.postfixOps
import com.systemfreund.stocktwits.Models.{StreamResponse, Error, Response, ErrorResponse}
import spray.httpx.SprayJsonSupport._
import Models.JsonProtocol._
import com.systemfreund.stocktwits.Stream._

class StreamTest extends FunSuite with Matchers {

  import scala.concurrent.ExecutionContext.Implicits.global

  test("fail on empty response") {
    val stream =
      Stream(
        Symbol("TEST"),
        withResponse(""))

    intercept[PipelineException] {
      Await.result(stream(None), 5 seconds)
    }
  }

  test("fail on empty object") {
    val stream =
      Stream(
        Symbol("TEST"),
        withResponse("{}"))

    intercept[PipelineException] {
      Await.result(stream(None), 5 seconds)
    }
  }

  test("expect api error 404") {
    val stream =
      Stream(
        Symbol("TEST"),
        withResponse( """{ "response": {"status": 404}, "errors": [{"message": "notfound"}] }""", StatusCodes.NotFound))

    try {
      Await.result(stream(None), 5 seconds)
    } catch {
      case ApiError(resp) => resp shouldEqual ErrorResponse(Response(404), Seq(Error("notfound")))
    }
  }

  test("expect RuntimeException when unmarshalling error response fails ") {
    val stream =
      Stream(
        Symbol("TEST"),
        withResponse( """{ "response": {"status": 404} }""", StatusCodes.NotFound))

    try {
      Await.result(stream(None), 5 seconds)
    } catch {
      case e: RuntimeException => // ok, because "errors" field is missing
      case e: Throwable => fail("Unexpected exception", e)
    }
  }

  def withResponse(data: String, status: StatusCode = StatusCodes.OK): HttpRequest => Future[HttpResponse] = {
    request => Future.successful(HttpResponse(status, HttpEntity(ContentTypes.`application/json`, data)))
  }

}
