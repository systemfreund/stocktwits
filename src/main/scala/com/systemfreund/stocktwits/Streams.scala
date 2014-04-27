package com.systemfreund.stocktwits

import spray.client.pipelining._
import spray.httpx.encoding.{Deflate, Gzip}
import spray.http.HttpRequest
import spray.http.HttpResponse
import scala.concurrent.{ExecutionContext, Future}
import spray.http.StatusCodes._
import akka.actor.ActorRefFactory
import scala.concurrent.duration._
import com.systemfreund.stocktwits.Models._
import scala.util.{Failure, Try, Success}
import com.systemfreund.stocktwits.Models.JsonProtocol._
import spray.httpx.SprayJsonSupport._
import spray.httpx.UnsuccessfulResponseException
import spray.httpx.unmarshalling.PimpedHttpResponse

class Streams private(val sendRecv: HttpRequest => Future[HttpResponse])(implicit val dispatcher: ExecutionContext) {

  private val pipeline = (encode(Gzip)
    ~> sendRecv
    ~> decode(Deflate)
    ~> unmarshal[StreamResponse])

  private def unmarshalErrorResponse(response: HttpResponse): ErrorResponse = response.as[ErrorResponse] match {
    case Left(error) => throw new RuntimeException(error.toString)
    case Right(response) => response
  }

  def symbol(id: String, since: Option[Int] = None): Future[StreamResponse] = pipeline(Get(symbolUriOf(id).since(since))) recover {
    case e: UnsuccessfulResponseException => throw ApiError(unmarshalErrorResponse(e.response))
  }

}

object Streams {
  def apply()(implicit actorSys: ActorRefFactory, dispatcher: ExecutionContext) = new Streams(sendReceive)

  def apply(sendRecv: HttpRequest => Future[HttpResponse])(implicit dispatcher: ExecutionContext) = new Streams(sendRecv)
}
