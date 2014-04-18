package com.systemfreund.stocktwits

import org.scalatest.FunSuite
import spray.json._
import com.systemfreund.stocktwits.Models._
import com.systemfreund.stocktwits.Models.JsonProtocol._

class ModelsTest extends FunSuite {

  test("map json to 'Response'") {
    val source = """{ "status": 200 }""".parseJson.convertTo[Response]
    assert(source.status == 200)
  }

  test("'stream' to response") {
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
      }""".parseJson.convertTo[StreamResponse]

    assert(source.symbol.id == 17)
    assert(source.symbol.symbol == "JOY")
    assert(source.symbol.title == "Joy Global, Inc.")
    assert(source.cursor.more == true)
    assert(source.cursor.since == 49)
    assert(source.cursor.max == 51)
  }

}
