@file:JvmName("ServiceJvmMainJavaAlias")

package com.riseofcat.service.jvm

import com.riseofcat.lib.*
import com.riseofcat.server.*
import com.riseofcat.share.mass.*

fun main(vararg args:String) {
  println("service-jvm")
  println(LibJvm.test())
}

private fun todo() {
  val player:RoomsDecorator<ClientPayload,ServerPayload>.Room.Player? = null
  val startTime = player!!.session.get(UsageMonitorDecorator.Extra::class.java)!!.startTime
  val pingDelay = player!!.session.get(PingDecorator.Extra::class.java)!!.lastPingDelay
}