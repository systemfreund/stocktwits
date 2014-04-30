package com.systemfreund.stocktwits

import rx.lang.scala.Observable
import rx.lang.scala.Subject
import com.systemfreund.stocktwits.Models.Message
import rx.operators.OperationDelay
import java.util.concurrent.TimeUnit

class Watch private(val streams: Streams) {

  import streams.dispatcher

  def symbol(id: String): Observable[Message] = {
    val subject = Subject[Message]()
    var since: Option[Int] = None

    def reschedule() {
      println(s"rescheduling. since=$since")

      import rx.lang.scala.JavaConversions
      val o: Observable[AnyRef] = JavaConversions.toScalaObservable(rx.Observable.timer(5, TimeUnit.SECONDS))

      val obs = o.flatMap(_ => Observable.from(streams.symbol(id, since))
        .doOnNext(resp => since = Some(resp.cursor.since))
        .doOnCompleted(reschedule)
        .flatMap(resp => Observable.from(resp.messages.reverse)))

      obs.subscribe(msg => subject.onNext(msg))
    }

    reschedule()

    subject
  }

}

object Watch {

  def apply(streams: Streams) = new Watch(streams)

}