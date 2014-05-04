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
import spray.http.Uri

class Stream private(val sendRecv: HttpRequest => Future[HttpResponse], val entity: StreamEntity)(implicit val dispatcher: ExecutionContext) {

  private val pipeline = (encode(Gzip)
    ~> sendRecv
    ~> decode(Deflate)
    ~> unmarshal[StreamResponse])

  private def unmarshalErrorResponse(response: HttpResponse): ErrorResponse = response.as[ErrorResponse] match {
    case Left(error) => throw new RuntimeException(error.toString)
    case Right(response) => response
  }

  private def get(uri: Uri)(since: Option[Int] = None): Future[StreamResponse] = pipeline(Get(uri.since(since))) recover {
    case e: UnsuccessfulResponseException => throw ApiError(unmarshalErrorResponse(e.response))
  }

  private def get: Option[Int] => Future[StreamResponse] = entity match {
    case entity: StreamEntity => get(entity.uri) _
  }

}

object Stream {
  type StreamFunc = Option[Int] => Future[StreamResponse]

  def apply(entity: StreamEntity)(implicit actorSys: ActorRefFactory, dispatcher: ExecutionContext): StreamFunc = apply(entity, sendReceive)

  def apply(entity: StreamEntity, sendRecv: HttpRequest => Future[HttpResponse])(implicit dispatcher: ExecutionContext): StreamFunc = new Stream(sendRecv, entity).get
}
