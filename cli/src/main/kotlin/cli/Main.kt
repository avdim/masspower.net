@file:JvmName("Main")

package cli

import common.*

fun main(vararg args:String) {
  val answer = JvmLib.test()
  val multiplatform = ServerCommon.test()
  println("cli: $answer. $multiplatform")
}
