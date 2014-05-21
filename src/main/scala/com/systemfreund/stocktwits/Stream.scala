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
import com.systemfreund.stocktwits.Stream.StreamFunc
import com.systemfreund.stocktwits.Parameters.RequestParameter
import com.systemfreund.stocktwits.Streams.StreamEntity

private class Stream[A <: StreamResponse : FromResponseUnmarshaller] private(val entity: StreamEntity[A],
                                                                             val sendRecv: HttpRequest => Future[HttpResponse])
                                                                            (implicit val dispatcher: ExecutionContext) {

  val pipeline: HttpRequest => Future[A] = (
    encode(Gzip)
      ~> sendRecv
      ~> decode(Deflate)
      ~> unmarshal[A])

  def unmarshalErrorResponse(response: HttpResponse): ErrorResponse = response.as[ErrorResponse] match {
    case Left(error) => throw new RuntimeException(error.toString)
    case Right(errorResp) => errorResp
  }

  def get(uri: Uri)(params: RequestParameter[_]*): Future[A] = pipeline(Get(uri.withQuery(params: _*))) recover {
    case e: UnsuccessfulResponseException => throw ApiError(unmarshalErrorResponse(e.response))
  }

  def get: StreamFunc[A] = entity match {
    case entity: StreamEntity[A] => get(entity.uri)
  }

}

object Stream {
  //  implicit def noArgToNone[A <: StreamResponse](f: StreamFunc[A]): () => Future[A] = {
  //    () => f(Parameters())
  //  }

  type StreamFunc[A <: StreamResponse] = (RequestParameter[_] *) => Future[A]

  // Reminder:
  // "A : B" maps to (implicit evidence: B[A])

  def apply[A <: StreamResponse : FromResponseUnmarshaller](entity: StreamEntity[A])
                                                           (implicit actorSys: ActorRefFactory,
                                                            dispatcher: ExecutionContext): StreamFunc[A] =
    apply(entity, sendReceive)

  def apply[A <: StreamResponse : FromResponseUnmarshaller](entity: StreamEntity[A],
                                                            sendRecv: HttpRequest => Future[HttpResponse])
                                                           (implicit dispatcher: ExecutionContext): StreamFunc[A] =
    new Stream[A](entity, sendRecv).get
}
