@file:JvmName("Main")

package masspower.client

import common.*

fun main(vararg args:String) {
  val answer = JvmLib.test()
  val multiplatform = ServerCommon.test()
  println("client-jvm: $answer. $multiplatform")
}
