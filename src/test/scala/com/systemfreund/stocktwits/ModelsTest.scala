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
    val source = testResponseTemplate("symbol",
      """{ "id": 1, "symbol": "SYM1", "title": "Symbol 1" }""").convertTo[SymbolFeed]

    source.symbol shouldEqual testSymbol1
    source.cursor shouldEqual testCursor
    source.messages shouldEqual Seq(testMessage)
  }

  test("map user stream to 'UserStreamResponse'") {
    val source = testResponseTemplate("user",
      """{ "id": 17, "username": "traderjoe", "name": "Trader Joe",
           "avatar_url": "http://avatar", "avatar_url_ssl": "https://avatar",
           "identity": "User", "classification": ["class1", "class2"] }""").convertTo[UserFeed]

    source.user shouldEqual testUser
    source.cursor shouldEqual testCursor
    source.messages shouldEqual Seq(testMessage)
  }

  def testResponseTemplate(name: String, value: String): JsValue = {
    s"""
      {
        "response": { "status": 200 },
        "$name": $value,
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
            "id": 17,
            "username": "traderjoe",
            "name": "Trader Joe",
            "avatar_url": "http://avatar",
            "avatar_url_ssl": "https://avatar",
            "identity": "User",
            "classification": [ "class1", "class2" ]
          },
          "source": {
            "id": 1,
            "title": "StockTwits",
            "url": "http://stocktwits.com"
          },
          "symbols": [ { "id": 1, "symbol": "SYM1", "title": "Symbol 1" }, { "id": 2, "symbol": "SYM2", "title": "Symbol 2" } ]
        } ]
      }
     """.parseJson
  }

  val testUser = Models.User(17, "traderjoe", "Trader Joe", "http://avatar", "https://avatar", "User", Seq("class1", "class2"))
  val testSymbol1 = Models.Symbol(1, "SYM1", "Symbol 1")
  val testSymbol2 = Models.Symbol(2, "SYM2", "Symbol 2")
  val testMessage = Message(1, "hello", "2012-10-08 21:41:38 UTC", testUser, Source(1, "StockTwits", "http://stocktwits.com"), Some(Seq(testSymbol1, testSymbol2)))
  val testCursor = Cursor(true, 49, Some(51))

}
