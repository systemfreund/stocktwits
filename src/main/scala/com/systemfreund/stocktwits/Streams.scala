package com.systemfreund.stocktwits

import spray.http.Uri.Path
import spray.http.Uri.Path.{/, Empty}
import spray.http.Uri
import com.systemfreund.stocktwits.Models.{InvestorRelationsFeed, UserFeed, SymbolFeed}

object Streams {
  private val streamsBasePath = basePath / "streams" ++ /

  /**
   * Type parameter is needed in order to avoid having to call [[com.systemfreund.stocktwits.Stream]] like this:
   * {{{Stream[SymbolFeed](Symbol("GOOGL"))}}}
   * Instead, we can write {{{Stream(Symbol("GOOGL"))}}} because the compiler can now infer the type parameter.
   */
  sealed abstract class StreamEntity[A](private val path: Path = Empty) {
    private[stocktwits] lazy val uri = Uri((streamsBasePath ++ path + ".json").toString())
  }

  case class Symbol(id: String) extends StreamEntity[SymbolFeed](Path("symbol") / id)

  case class User(id: String) extends StreamEntity[UserFeed](Path("user") / id)

  case object InvestorRelations extends StreamEntity[InvestorRelationsFeed](Path("investor_relations"))

}
