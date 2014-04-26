package com.systemfreund

import spray.http.Uri
import com.systemfreund.stocktwits.Models.ErrorResponse

package object stocktwits {

  class ApiError(val response: ErrorResponse) extends RuntimeException

  val baseUri = Uri("https://api.stocktwits.com/api/2")

  val streamsUri = baseUri.withPath(baseUri.path / "streams")

  def symbolUriOf(symbol: String) = streamsUri withPath streamsUri.path / "symbol" / symbol + ".json"

  implicit class Queries(val uri: Uri) {
    def toUri(opt: Option[Int], f: Int => Uri) = opt.fold(uri)(f(_))

    def since(id: Int): Uri = uri.withQuery(("since", id.toString))
    def since(id: Option[Int]): Uri = toUri(id, since)

    def max(id: Int): Uri = uri.withQuery(("max", id.toString))
    def max(id: Option[Int]): Uri = toUri(id, max)

    def limit(count: Int): Uri = uri.withQuery(("limit", count.toString))
    def limit(count: Option[Int]): Uri = toUri(count, limit)
  }

}
