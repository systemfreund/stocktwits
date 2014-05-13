stocktwits
==========

Stocktwits API implementation in Scala.

*Work in progress and far from finished :)*

#### Usage

###### Fetch stream of stock symbol
```scala
val google = Stream(Symbol("GOOG"))

val result = google(Limit(10))
// -> GET https://api.stocktwits.com/api/2/streams/symbol/GOOG.json?limit=10
// result.getClass == Future[SymbolStreamResponse]
```
###### Fetch stream of user
```scala
val user = Stream(User("traderjoe"))

val result = user(Since(12345), Limit(10))
// -> GET https://api.stocktwits.com/api/2/streams/user/traderjoe.json?since=12345&limit=10
// result.getClass == Future[UserStreamResponse]
```
