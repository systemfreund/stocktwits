package com.systemfreund

import spray.http.Uri
import com.systemfreund.stocktwits.Models.ErrorResponse

package object stocktwits {

  class ApiError(val response: ErrorResponse) extends RuntimeException

  val baseUri = Uri("https://api.stocktwits.com/api/2")

  val streamsUri = baseUri.withPath(baseUri.path / "streams")

  def symbolUriOf(symbol: String) = streamsUri withPath streamsUri.path / "symbol" / symbol + ".json"

}
