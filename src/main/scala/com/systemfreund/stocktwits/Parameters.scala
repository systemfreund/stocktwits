package com.systemfreund.stocktwits

import spray.http.Uri
import spray.http.Uri.Query

object Parameters {

  sealed abstract class RequestParameter[A](val key: String, val value: A)

  /** Return results with an ID greater than (more recent than) the specified ID. */
  case class Since(id: Int) extends RequestParameter("since", id)

  /** Return results with an ID less than (older than) or equal to the specified ID. */
  case class Max(id: Int) extends RequestParameter("max", id)

  /** Limit results count to the specified amount. */
  case class Limit(count: Int) extends RequestParameter("limit", count)

  private def toQuery(params: RequestParameter[_]*) = Query(params.map(p => (p.key, p.value.toString)).toMap)

  implicit class RequestParameterSupport(val uri: Uri) {
    def withQuery(params: RequestParameter[_]*): Uri = uri.withQuery(toQuery(params : _*))
  }

}
