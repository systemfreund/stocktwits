package com.systemfreund.stocktwits

import org.scalatest.{Matchers, FunSuite}
import spray.http.Uri
import com.systemfreund.stocktwits.Parameters._
import com.systemfreund.stocktwits.Streams._
import com.systemfreund.stocktwits.Parameters.Limit
import com.systemfreund.stocktwits.Parameters.Max
import com.systemfreund.stocktwits.Streams.User
import com.systemfreund.stocktwits.Parameters.Filter
import com.systemfreund.stocktwits.Streams.Symbol
import com.systemfreund.stocktwits.Parameters.Since

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

  test("uri with 'since' and 'since'") {
    Uri("http://test").withQuery(Since(1), Since(1)) shouldEqual Uri("http://test?since=1&since=1")
  }

  test("uri with 'since', 'max' and 'limit'") {
    Uri("http://test").withQuery(Since(1), Max(10), Limit(100)) shouldEqual Uri("http://test?since=1&max=10&limit=100")
  }

  test("uri with 'since', 'max' and 'since'") {
    Uri("http://test").withQuery(Since(1), Max(10), Since(100)) shouldEqual Uri("http://test?since=1&max=10&since=100")
  }

  test("uri with 'filter'") {
    Uri("http://test").withQuery(Filter(Top)) shouldEqual Uri("http://test?filter=top")
  }

  test("uri with 'charts'") {
    Uri("http://test").withQuery(Filter(Charts)) shouldEqual Uri("http://test?filter=charts")
  }

  test("uri with 'links") {
    Uri("http://test").withQuery(Filter(Links)) shouldEqual Uri("http://test?filter=links")
  }

  test("uri with 'videos") {
    Uri("http://test").withQuery(Filter(Videos)) shouldEqual Uri("http://test?filter=videos")
  }

}
