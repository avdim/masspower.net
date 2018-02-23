@file:JvmName("Main")

package cli

import common.Multiplatform
import core.*

fun main(vararg args:String) {
  val answer = DeepThought.compute()
  val multiplatform = Multiplatform.multiplatform()
  println("The answer to the ultimate question of Life, the Universe and Everything is $answer. $multiplatform")
}
