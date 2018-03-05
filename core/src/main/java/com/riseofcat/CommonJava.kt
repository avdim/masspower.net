package com.riseofcat

import com.riseofcat.common.*

class CommonJava {
  companion object {
    @JvmStatic
    fun toJson(obj:Any):String {
      return Common.toJson(obj)
    }

    @JvmStatic
    fun <T>fromJson(str:String,clazz:Class<T>):T {
      return gson.fromJson(str, clazz)
    }
  }

}