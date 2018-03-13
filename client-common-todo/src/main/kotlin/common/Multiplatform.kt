package common

import com.n8cats.common.*
import com.riseofcat.common.*

object Multiplatform {
  fun multiplatform():String {
    val k = "kotlin"
    val m = "multiplatform 3"
    return "$k $m ${ServerCommon.test()}"
  }
}