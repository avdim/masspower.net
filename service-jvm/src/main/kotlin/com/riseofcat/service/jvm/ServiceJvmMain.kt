@file:JvmName("ServiceJvmMainJavaAlias")

package com.riseofcat.service.jvm

import com.riseofcat.client.*
import com.riseofcat.common.*
import com.riseofcat.lib.*
import com.riseofcat.server.*
import com.riseofcat.share.mass.*
import kotlinx.coroutines.experimental.*

fun main(vararg args:String) {
  lib.log.info("test info line")
  TestJson.testJson()
  val a = field
  bots()
}

val field by lib.smoothByTime {5.0}
val lz by lazy{"asd"}

private fun todo() {
  val player:RoomsDecorator<ClientPayload,ServerPayload>.Room.Player? = null
  val startTime = player!!.session.get(UsageMonitorDecorator.Extra::class.java)!!.startTime
  val pingDelay = player!!.session.get(PingDecorator.Extra::class.java)!!.lastPingDelay
}

private fun test() {
  degreesAngle(370) == degreesAngle(10)
  degreesAngle(-10) == degreesAngle(350)
  degreesAngle(360) == degreesAngle(0)
}

fun testRnd(){
  val state = State()
//  repeat(100) {
    lib.log.info(state.rnd(360).toString())
//  }
}

fun bots() = runBlocking {
  val models = mutableListOf<DummyModel>()
  val jobs:MutableList<Job> = mutableListOf()
  repeat(300) {rpt:Int->
    val job:Job = launch {
      var messages = 0
      val model = DummyModel(confs.current)
      models.add(model)
      while(messages < 200) {
        messages++
        delay(rnd(100, 150))
        if(rnd(0,1) == 1) model.move(degreesAngle(rnd(0,360)))
        else model.newCar()
      }
    }
    jobs.add(job)
    delay(100L)
  }
  jobs.forEach{it.join()}
  delay(10*1000L)
  lib.log.info("summ: ${models.sumBy {it.client.clientMessages}}")
}