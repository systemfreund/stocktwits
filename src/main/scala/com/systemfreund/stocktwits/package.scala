package com.systemfreund

import spray.http.Uri
import spray.http.Uri.Path
import spray.http.Uri.Path._
import com.systemfreund.stocktwits.Models.{UserStreamResponse, SymbolStreamResponse, StreamResponse, ErrorResponse}

package object stocktwits {

  /**
   * Thrown by <code>Future[StreamResponse]</code> when the server returns anything but HTTP 200.
   *
   * @see [[com.systemfreund.stocktwits.Stream]]
   * @see http://stocktwits.com/developers/docs/responses
   */
  case class ApiError(response: ErrorResponse) extends RuntimeException

  private val basePath = Path("https://api.stocktwits.com/api/2")
  private val streamsBasePath = basePath / "streams" ++ /

  /**
   * Type parameter is needed in order to avoid having to call [[com.systemfreund.stocktwits.Stream]] like this:
   * {{{Stream[SymbolStreamResponse](Symbol("GOOGL"))}}}
   * Instead, we can write {{{Stream(Symbol("GOOGL")}}} because the compiler can now infer the type parameter.
   */
  sealed class StreamEntity[A](private val path: Path = Empty) {
    private[stocktwits] lazy val uri = Uri((streamsBasePath ++ path + ".json").toString())
  }

  case class Symbol(id: String) extends StreamEntity[SymbolStreamResponse](Path("symbol") / id)

  case class User(id: String) extends StreamEntity[UserStreamResponse](Path("user") / id)

  implicit class Queries(val uri: Uri) {
    def toUri(opt: Option[Int], f: Int => Uri) = opt.fold(uri)(f)

    def since(id: Int): Uri = uri.withQuery(("since", id.toString))

    def since(id: Option[Int]): Uri = toUri(id, since)

    def max(id: Int): Uri = uri.withQuery(("max", id.toString))

    def max(id: Option[Int]): Uri = toUri(id, max)

    def limit(count: Int): Uri = uri.withQuery(("limit", count.toString))

    def limit(count: Option[Int]): Uri = toUri(count, limit)
  }

}
