stocktwits
==========

Stocktwits API implementation in Scala.

*Work in progress and far from finished :)*

#### Usage

###### Fetch stream of stock symbol
```scala
val google = Stream(Symbol("GOOG"))

// type of google == Future[SymbolStreamResponse]
```
###### Fetch stream of user
```scala
val user = Stream(User("traderjoe"))

// type of user == Future[UserStreamResponse]
```
