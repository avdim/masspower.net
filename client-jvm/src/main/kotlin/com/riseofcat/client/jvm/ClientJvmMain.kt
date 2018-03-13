@file:JvmName("ClientJvmMainJavaAlias")

package com.riseofcat.client.jvm

import com.riseofcat.lib.*

fun main(vararg args:String) {
  println("client-jvm")
  println(LibJvm.test())
}