package com.systemfreund.stocktwits

import org.scalatest.{Matchers, FunSuite}
import spray.json._
import com.systemfreund.stocktwits.Models._
import com.systemfreund.stocktwits.Models.JsonProtocol._

class ModelsTest extends FunSuite with Matchers {

  test("map json to 'Response'") {
    val source = """{ "status": 200 }""".parseJson.convertTo[Response]
    assert(source.status == 200)
  }

  test("map errors to 'Error'") {
    val err = """{ "message": "err1"}""".parseJson.convertTo[Error]
    assert(err.message == "err1")
  }

  test("map error response to 'ErrorResponse'") {
    val resp = """{ "response": {"status": 404}, "errors": [{"message": "err1"}] }""".parseJson.convertTo[ErrorResponse]
    assert(resp.response.status == 404)
    assert(resp.errors.size == 1)
    assert(resp.errors(0).message == "err1")
  }

  test("map cursor to 'Cursor'") {
    val cursor = """{"more":false,"since":1,"max":10}""".parseJson.convertTo[Cursor]

    assert(cursor.more == false)
    assert(cursor.since == 1)
    assert(cursor.max == Some(10))
  }

  test("map cursor without max to 'Cursor'") {
    val cursor = """{"more":false,"since":1,"max":null}""".parseJson.convertTo[Cursor]

    assert(cursor.more == false)
    assert(cursor.since == 1)
    assert(cursor.max == None)
  }

  test("map symbol stream to 'SymbolStreamResponse'") {
    val source = """
      {
        "response": { "status": 200 },
        "symbol": { "id": 17, "symbol": "JOY", "title": "Joy Global, Inc." },
        "cursor": { "more": true, "since": 49, "max": 51 },
        "messages": [ {
          "id": 1,
          "body": "hello",
          "created_at": "2012-10-08 21:41:38 UTC",
          "user": {
            "id": 2,
            "username": "mcescher",
            "name": "M.C. Escher",
            "avatar_url": "http://avatar",
            "avatar_url_ssl": "https://avatar",
            "identity": "User",
            "classification": [ "suggested" ]
          },
          "source": {
            "id": 1,
            "title": "StockTwits",
            "url": "http://stocktwits.com"
          },
          "symbols": [ { "id": 17, "symbol": "JOY", "title": "Joy Global, Inc." } ]
        } ]
      }""".parseJson.convertTo[SymbolStreamResponse]

    assert(source.symbol.id == 17)
    assert(source.symbol.ticker == "JOY")
    assert(source.symbol.title == "Joy Global, Inc.")
    assert(source.cursor.more == true)
    assert(source.cursor.since == 49)
    assert(source.cursor.max == Some(51))
  }

  test("map user stream to 'UserStreamResponse'") {
    val source = """
      {
        "response": { "status": 200 },
        "user": {
          "id": 17,
          "username": "traderjoe",
          "name": "Trader Joe",
          "avatar_url": "http://avatar",
          "avatar_url_ssl": "https://avatar",
          "identity": "User",
          "classification": ["class1", "class2"]
        },
        "cursor": { "more": true, "since": 49, "max": 51 },
        "messages": [ {
          "id": 1,
          "body": "hello",
          "created_at": "2012-10-08 21:41:38 UTC",
          "user": {
            "id": 2,
            "username": "mcescher",
            "name": "M.C. Escher",
            "avatar_url": "http://avatar",
            "avatar_url_ssl": "https://avatar",
            "identity": "User",
            "classification": [ "suggested" ]
          },
          "source": {
            "id": 1,
            "title": "StockTwits",
            "url": "http://stocktwits.com"
          },
          "symbols": [ { "id": 17, "symbol": "JOY", "title": "Joy Global, Inc." } ]
        } ]
      }""".parseJson.convertTo[UserStreamResponse]

    source.user.id shouldEqual 17
    source.user.username shouldEqual "traderjoe"
    source.user.name shouldEqual "Trader Joe"
    source.user.avatarUrl shouldEqual "http://avatar"
    source.user.avatarUrlSsl shouldEqual "https://avatar"
    source.user.identity shouldEqual "User"
    source.user.classification shouldEqual Seq("class1", "class2")
  }

}
