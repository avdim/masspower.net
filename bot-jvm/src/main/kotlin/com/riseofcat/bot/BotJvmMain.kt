
@file:JvmName("BotJvmMainJavaAlias")

package com.riseofcat.bot

import com.riseofcat.client.*
import com.riseofcat.common.*
import com.riseofcat.lib.*
import com.riseofcat.share.mass.*
import kotlin.concurrent.*

fun main(vararg args:String) {
  //todo сделать DUMMY с лёгкой моделью без вызовов getState state.tick()
  val DUMMY = true
  println("bot-jvm")
  println(LibJvm.test())
  repeat(25) {
//    val model:ClientModel = ClientModel(Conf(5000, "192.168.43.176"))
//    val model = ClientModel(Conf(5000, "localhost"))
    val model = DummyModel(Conf(5000, "localhost"))
    timer("client i", true, period = rnd(400, 700).toLong()) {
      if(DUMMY) {
        if(rnd(0,1) == 1) {
          model.move(degreesAngle(rnd(0,360)))
        } else {
          model.newCar()
        }
      }
    }
  }
}
