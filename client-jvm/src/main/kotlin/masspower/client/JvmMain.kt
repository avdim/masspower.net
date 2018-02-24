@file:JvmName("Main")

package masspower.client

fun main(vararg args:String) {
  println("client-jvm: ${JvmLib.test()} ${ServerCommon.test()}")
}