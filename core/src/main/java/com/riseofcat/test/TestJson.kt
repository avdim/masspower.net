package com.riseofcat.test

import com.badlogic.gdx.utils.*
import com.google.gson.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

@Serializable data class DataExtra(var d2:Data<Extra>)

@Serializable data class Data<T>(
  var a:String? = null,
  var b:Double = 0.0,
  var c:T? = null)

@Serializable data class Extra (
  val extra:String,
  val extraInt:Int
)

class TestJson {
  companion object {
    fun testJson() {
      Json()//todo test
      val data = DataExtra(Data<Extra>("abc",3.14, Extra("extra data", 123)))
      val strGson = Gson().toJson(data)
      val dataGson:Map<String, Any> = Gson().fromJson<Map<String, Any>>(strGson,getKClass<Map<String, Any>>().java)
//      val strKlaxon = Klaxon().toJsonString(data)
//      val dataKlaxon = Klaxon().parse<Data<Extra>>(strKlaxon)
      val strJetBrains:String = JSON.stringify(data)
      val dataJetBrains:DataExtra = JSON.parse(strJetBrains)
      testCodeGenerated()
      println("complete")
    }
    fun testCodeGenerated() {
      val constructors:List<KFunction<Data<Extra>>> = getKClass<Data<Extra>>().constructors as List
      println("constructors.size = ${constructors.size}")
      val constructor:KFunction<Data<Extra>> = constructors.last()
      val codeGenerated:Data<Extra> = constructor.call("def",2.71,Extra("super extra",321))

      val map = mapOf<KParameter,Any?>()
      for(p in constructor.parameters) {
        p.type.jvmErasure
        p.name
        map[p]
      }
      val v = constructor.callBy(map)
      println("generated")
    }
  }
}

inline fun <reified T:Any>getKClass() = T::class