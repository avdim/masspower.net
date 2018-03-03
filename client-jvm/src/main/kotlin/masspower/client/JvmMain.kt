@file:JvmName("Main")

package masspower.client

import com.riseofcat.common.*

fun main(vararg args:String) {
  println("client-jvm: ${JvmLib.test()} ${ServerCommon.test()}")
}