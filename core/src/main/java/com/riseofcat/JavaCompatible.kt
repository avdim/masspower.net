package com.riseofcat

import com.riseofcat.common.fromJson
import com.riseofcat.common.toJson
import com.riseofcat.reflect.*
import kotlin.jvm.*

class JavaCompatible {
  companion object {
    @JvmStatic
    fun toJson(obj:Any):String {
      return obj.toJson();
    }

    @JvmStatic
    fun fromJson(str:String):Conf {
      return str.fromJson(Conf::class)
    }
  }

}