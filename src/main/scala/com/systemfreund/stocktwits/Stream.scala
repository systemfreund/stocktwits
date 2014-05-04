package com.systemfreund.stocktwits

import spray.client.pipelining._
import spray.httpx.encoding.{Deflate, Gzip}
import spray.http.HttpRequest
import spray.http.HttpResponse
import scala.concurrent.{ExecutionContext, Future}
import akka.actor.ActorRefFactory
import com.systemfreund.stocktwits.Models._
import com.systemfreund.stocktwits.Models.JsonProtocol._
import spray.httpx.SprayJsonSupport._
import spray.httpx.UnsuccessfulResponseException
import spray.httpx.unmarshalling.{FromResponseUnmarshaller, PimpedHttpResponse}
import spray.http.Uri

class Stream[A <: StreamResponse] private(val entity: StreamEntity[A],
                                          val sendRecv: HttpRequest => Future[HttpResponse])
                                         (implicit val dispatcher: ExecutionContext,
                                          contextBounds: FromResponseUnmarshaller[A]) {

  private val pipeline: HttpRequest => Future[A] = (encode(Gzip)
    ~> sendRecv
    ~> decode(Deflate)
    ~> unmarshal[A])

  private def unmarshalErrorResponse(response: HttpResponse): ErrorResponse = response.as[ErrorResponse] match {
    case Left(error) => throw new RuntimeException(error.toString)
    case Right(errorResp) => errorResp
  }

  private def get(uri: Uri)(since: Option[Int] = None): Future[A] = pipeline(Get(uri.since(since))) recover {
    case e: UnsuccessfulResponseException => throw ApiError(unmarshalErrorResponse(e.response))
  }

  private def get: Option[Int] => Future[A] = entity match {
    case entity: StreamEntity[A] => get(entity.uri)
  }

}

object Stream {
  type StreamFunc[A <: StreamResponse] = Option[Int] => Future[A]

  def apply[A <: StreamResponse](entity: StreamEntity[A])
                                (implicit actorSys: ActorRefFactory,
                                 dispatcher: ExecutionContext,
                                 contextBounds: FromResponseUnmarshaller[A]): StreamFunc[A] = apply(entity, sendReceive)

  def apply[A <: StreamResponse](entity: StreamEntity[A],
                                 sendRecv: HttpRequest => Future[HttpResponse])
                                (implicit dispatcher: ExecutionContext,
                                 contextBounds: FromResponseUnmarshaller[A]): StreamFunc[A] = new Stream[A](entity, sendRecv).get
}
