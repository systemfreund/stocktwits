package com.systemfreund.stocktwits

import org.scalatest.FunSuite
import akka.actor.ActorSystem
import spray.http.{StatusCodes, StatusCode, HttpResponse, HttpRequest}
import scala.concurrent.{Await, Future}
import spray.client.pipelining._
import spray.httpx.encoding.{Deflate, Gzip}
import spray.httpx.SprayJsonSupport._
import com.systemfreund.stocktwits.Models._
import scala.concurrent.duration._
import com.systemfreund.stocktwits.Models.JsonProtocol._
import spray.http.StatusCodes._

class IntegrationTest extends FunSuite {


  implicit val system = ActorSystem()

  import system.dispatcher

  def handleErrorStatus(): ResponseTransformer = response => response.status match {
    case NotFound => response.copy(status = OK)
    case _ => response
  }

  val pipeline: HttpRequest => Future[StreamResponse] = (
    encode(Gzip)
      ~> sendReceive
      ~> decode(Deflate)
      ~> handleErrorStatus()
      ~> unmarshal[StreamResponse]
    )

  test("get 'streams/symbol'") {
    val response = pipeline(Get("https://api.stocktwits.com/api/2/streams/symbol/GOOG.json"))
    val stream = Await.result(response, 5 seconds)
    assert(stream.response.status == 200)
  }

  test("unknown 'streams/symbol'") {
    val response = pipeline(Get("https://api.stocktwits.com/api/2/streams/symbol/GOOGO.json"))

    val stream = Await.result(response, 5 seconds)

    assert(stream.response.status == 404)
  }

}
