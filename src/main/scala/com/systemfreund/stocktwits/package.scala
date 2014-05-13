package com.systemfreund

import spray.http.Uri.Path
import com.systemfreund.stocktwits.Models.ErrorResponse

package object stocktwits {

  /**
   * Thrown by <code>Future[StreamResponse]</code> when the server returns anything but HTTP 200.
   *
   * @see [[com.systemfreund.stocktwits.Stream]]
   * @see http://stocktwits.com/developers/docs/responses
   */
  case class ApiError(response: ErrorResponse) extends RuntimeException

  private[systemfreund] val basePath = Path("https://api.stocktwits.com/api/2")

}
