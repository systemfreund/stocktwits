package com.systemfreund.stocktwits

import rx.lang.scala.{Subject, Observable}
import com.systemfreund.stocktwits.Models.{StreamResponse, Message}
import java.util.concurrent.TimeUnit
import rx.lang.scala.JavaConversions.toScalaObservable
import grizzled.slf4j.Logging
import com.systemfreund.stocktwits.Stream.StreamFunc
import scala.concurrent.ExecutionContext

object Watch extends Logging {

  def apply[A <: StreamResponse](streamFunc: StreamFunc[A])(implicit dispatcher: ExecutionContext): Observable[Message] = {
    val subject = Subject[Message]
    var since: Option[Int] = None

    def reschedule() {
      logger.debug(s"reschedule. since=$since")

      val timeout: Observable[AnyRef] = toScalaObservable(rx.Observable.timer(5, TimeUnit.SECONDS))

      val obs = timeout.flatMap(_ => Observable.from(streamFunc()))
        .doOnNext(resp => since = Some(resp.cursor.since))
        .doOnCompleted(reschedule)
        .flatMap(resp => Observable.from(resp.messages.reverse))

      obs.subscribe(msg => subject.onNext(msg))
    }

    reschedule()

    subject
  }

}