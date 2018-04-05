
@file:JvmName("BotJvmMainJavaAlias")

package com.riseofcat.bot

import com.riseofcat.client.*
import com.riseofcat.common.*
import com.riseofcat.lib.*
import com.riseofcat.share.mass.*
import kotlin.concurrent.*

fun main(vararg args:String) {
  println("bot-jvm")
  println(LibJvm.test())
  repeat(1) {
//    val model:ClientModel = ClientModel(Conf(5000, "192.168.43.176"))
//    val model = ClientModel(Conf(5000, "localhost"))
    val model = DummyModel(Conf(5000, "localhost"))
    timer("client i", true, period = rnd(300, 400).toLong()) {
      if(rnd(0,1) == 1) {
        model.move(degreesAngle(rnd(0,360)))
      } else {
        model.newCar()
      }
    }
    Thread.sleep(2000L)
  }
}
