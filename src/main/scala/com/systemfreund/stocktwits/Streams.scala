package com.systemfreund.stocktwits

import spray.client.pipelining._
import spray.httpx.encoding.{Deflate, Gzip}
import spray.http.HttpRequest
import spray.http.HttpResponse
import scala.concurrent.Future
import spray.http.StatusCodes._
import akka.actor.ActorRefFactory
import scala.concurrent.duration._
import com.systemfreund.stocktwits.Models._
import scala.util.{Failure, Try, Success}
import com.systemfreund.stocktwits.Models.JsonProtocol._
import spray.httpx.SprayJsonSupport._
import spray.httpx.UnsuccessfulResponseException
import spray.httpx.unmarshalling.PimpedHttpResponse

class Streams(val actorSystem: ActorRefFactory) {

  import actorSystem.dispatcher

  private val pipeline: HttpRequest => Future[StreamResponse] =
    (encode(Gzip)
      ~> sendReceive(actorSystem, actorSystem.dispatcher, 5 seconds)
      ~> decode(Deflate)
      ~> unmarshal[StreamResponse])

  private def unmarshalErrorResponse(response: HttpResponse): ErrorResponse = response.as[ErrorResponse] match {
    case Left(error) => throw new RuntimeException(error.toString)
    case Right(response) => response
  }

  def symbol(id: String): Future[Try[StreamResponse]] = pipeline(Get(symbolUriOf(id)))
    .map(t => Success(t))
    .recover { case e: UnsuccessfulResponseException => Failure(new ApiError(unmarshalErrorResponse(e.response))) }

}
