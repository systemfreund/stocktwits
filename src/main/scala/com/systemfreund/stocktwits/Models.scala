package com.systemfreund.stocktwits

import spray.json.DefaultJsonProtocol

object Models {

  type Errors = Option[Seq[Error]]
  type Messages = Seq[Message]
  type Symbols = Seq[Symbol]

  case class Response(status: Int)

  case class StreamResponse(response: Response,
                            errors: Errors,
                            cursor: Cursor,
                            symbol: Symbol,
                            messages: Messages)

  case class Error(message: String)

  case class Cursor(more: Boolean,
                    since: Int,
                    max: Int)

  case class Symbol(id: Int,
                    symbol: String,
                    title: String)

  case class User(id: Int,
                  username: String,
                  name: String,
                  avatarUrl: String,
                  avatarUrlSsl: String,
                  identity: String,
                  classification: Seq[String])

  case class Source(id: Int,
                    title: String,
                    url: String)

  case class Message(id: Int,
                     body: String,
                     createdAt: String,
                     user: User,
                     source: Source,
                     symbols: Symbols)

  object JsonProtocol extends DefaultJsonProtocol {
    implicit val errorFormat = jsonFormat1(Error)
    implicit val responseFormat = jsonFormat1(Response)
    implicit val cursorFormat = jsonFormat3(Cursor)
    implicit val symbolFormat = jsonFormat3(Symbol)
    implicit val userFormat = jsonFormat(User, "id", "username", "name", "avatar_url", "avatar_url_ssl", "identity", "classification")
    implicit val sourceFormat = jsonFormat3(Source)
    implicit val messageFormat = jsonFormat(Message, "id", "body", "created_at", "user", "source", "symbols")
    implicit val streamResponseFormat = jsonFormat5(StreamResponse)
  }

}
