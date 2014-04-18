package com.systemfreund.stocktwits

import org.scalatest.FunSuite
import spray.json._
import com.systemfreund.stocktwits.Models._
import com.systemfreund.stocktwits.Models.JsonProtocol._

class ModelsTest extends FunSuite {

  test("map json to ok status") {
    val source = """{ "status": 200 }"""
    val json = source.parseJson
    val model = json.convertTo[ResponseStatus]

    assert(model.status == 200)
  }

}
