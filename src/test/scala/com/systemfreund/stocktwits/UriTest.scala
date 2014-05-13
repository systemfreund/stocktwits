package com.systemfreund.stocktwits

import org.scalatest.{Matchers, FunSuite}
import spray.http.Uri
import com.systemfreund.stocktwits.Parameters.{Limit, Max, Since}
import com.systemfreund.stocktwits.Streams._

class UriTest extends FunSuite with Matchers {

  test("Symbol endpoint uri is correct") {
    Symbol("GOOG").uri shouldEqual Uri("https://api.stocktwits.com/api/2/streams/symbol/GOOG.json")
  }

  test("User endpoint uri is correct") {
    User("traderjoe").uri shouldEqual Uri("https://api.stocktwits.com/api/2/streams/user/traderjoe.json")
  }

  test("uri with 'since'") {
    Uri("http://test").withQuery(Since(1234)) shouldEqual Uri("http://test?since=1234")
  }

  test("uri with 'max'") {
    Uri("http://test").withQuery(Max(1234)) shouldEqual Uri("http://test?max=1234")
  }

  test("uri with 'limit'") {
    Uri("http://test").withQuery(Limit(1234)) shouldEqual Uri("http://test?limit=1234")
  }

  test("uri with 'since', 'max' and 'limit'") {
    Uri("http://test").withQuery(Since(1), Max(10), Limit(100)) shouldEqual Uri("http://test?since=1&max=10&limit=100")
  }

}
