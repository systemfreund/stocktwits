package com.systemfreund.stocktwits

import spray.json.DefaultJsonProtocol

object Models {

  case class ErrorResponse(response: Response,
                           errors: Seq[Error])

  case class Response(status: Int)

  case class Error(message: String)

  sealed trait StreamResponse {
    val cursor: Cursor
    val messages: Seq[Message]
  }

  case class SymbolFeed(symbol: Symbol,
                        cursor: Cursor,
                        messages: Seq[Message]) extends StreamResponse

  case class UserFeed(user: User,
                      cursor: Cursor,
                      messages: Seq[Message]) extends StreamResponse

  case class InvestorRelationsFeed(cursor: Cursor,
                                   messages: Seq[Message]) extends StreamResponse

  case class Cursor(more: Boolean,
                    since: Int,
                    max: Option[Int])

  case class Symbol(id: Int,
                    ticker: String,
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
                     symbols: Option[Seq[Symbol]])

  object JsonProtocol extends DefaultJsonProtocol {
    implicit val errorFormat = jsonFormat1(Error)
    implicit val responseFormat = jsonFormat1(Response)
    implicit val errorResponseFormat = jsonFormat2(ErrorResponse)
    implicit val cursorFormat = jsonFormat3(Cursor)
    implicit val symbolFormat = jsonFormat(Symbol, "id", "symbol", "title")
    implicit val userFormat = jsonFormat(User, "id", "username", "name", "avatar_url", "avatar_url_ssl", "identity", "classification")
    implicit val sourceFormat = jsonFormat3(Source)
    implicit val messageFormat = jsonFormat(Message, "id", "body", "created_at", "user", "source", "symbols")
    implicit val symbolFeedFormat = jsonFormat3(SymbolFeed)
    implicit val userFeedFormat = jsonFormat3(UserFeed)
    implicit val investorRelationsFormat = jsonFormat2(InvestorRelationsFeed)
  }

}
