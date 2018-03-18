package com.riseofcat.test

import com.badlogic.gdx.utils.*
import com.google.gson.*
import com.riseofcat.lib.*
import com.riseofcat.share.ping.*
import com.riseofcat.share.mass.*
import kotlinx.serialization.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

@Serializable
data class Data2(val a: Int)
@Serializable
data class Box<T>(val boxed: T)

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
      val strJetBrains:String = lib.json.stringify(data)
      val dataJetBrains:DataExtra = lib.json.parse(strJetBrains)
      testCodeGenerated()
      println("complete")

      if(true) {
        val serverPayloadSerializer: KSerializer<ServerPayload> = ServerPayload.serializer()
        val serverSayServerPayloadSerializer: KSerializer<ServerSay<ServerPayload>> = ServerSay.serializer(serverPayloadSerializer)
        val serverSay = ServerSay<ServerPayload>(ServerPayload(TickDbl(0.0)), latency = 11)
        val strJetBrains2 = lib.json.stringify(serverSayServerPayloadSerializer, serverSay)
        val serverSay2 = lib.json.parse(serverSayServerPayloadSerializer, strJetBrains2)
        println("serverSayS2.latency = ${serverSay2.latency}")
        println("jetbrains")
      }

      val dataSerial:KSerializer<Data2> = Data2.serializer()
      val boxedDataSerial:KSerializer<Box<Data2>> = Box.serializer(dataSerial)
      val box:Box<Data2> = Box(Data2(123))
      val box2 = lib.json.parse(boxedDataSerial,lib.json.stringify(boxedDataSerial,box))
      if(box == box2) {
        println("box2.boxed.a = ${box2.boxed.a}")
      }
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