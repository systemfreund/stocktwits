package com.systemfreund.stocktwits

import spray.json.DefaultJsonProtocol

object Models {

  case class ResponseStatus(val status: Int)
  case object OkStatus extends ResponseStatus(200)
//  case class ErrorStatus(override val status: Int) extends ResponseStatus(status)
  case class ResponseMessage(message: String)

  trait Response {
    def response: ResponseStatus
    def errors: Seq[ResponseMessage]
  }

  object JsonProtocol extends DefaultJsonProtocol {
    implicit val responseStatusFormat = jsonFormat1(ResponseStatus)
  }

}
