package com.systemfreund.stocktwits

import org.scalatest.{Matchers, FunSuite}
import spray.http.Uri

class UriTest extends FunSuite with Matchers {

  test("Symbol endpoint uri is correct") {
    Symbol("GOOG").uri shouldEqual Uri("https://api.stocktwits.com/api/2/streams/symbol/GOOG.json")
  }

  test("User endpoint uri is correct") {
    User("traderjoe").uri shouldEqual Uri("https://api.stocktwits.com/api/2/streams/user/traderjoe.json")
  }

  test("uri with 'since'") {
    Uri("http://test").since(1234) shouldEqual Uri("http://test?since=1234")
  }

  test("uri with option 'since'") {
    Uri("http://test").since(Some(1234)) shouldEqual Uri("http://test?since=1234")
  }

  test("uri with absent 'since'") {
    Uri("http://test").since(None) shouldEqual Uri("http://test")
  }

  test("uri with 'max'") {
    Uri("http://test").max(1234) shouldEqual Uri("http://test?max=1234")
  }

  test("uri with option 'max'") {
    Uri("http://test").max(Some(1234)) shouldEqual Uri("http://test?max=1234")
  }

  test("uri with absent 'max'") {
    Uri("http://test").max(None) shouldEqual Uri("http://test")
  }

}
