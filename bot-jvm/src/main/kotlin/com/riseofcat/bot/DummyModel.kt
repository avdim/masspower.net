package com.riseofcat.bot

import com.riseofcat.client.*
import com.riseofcat.lib.*
import com.riseofcat.share.mass.*

//todo delete
class DummyModel(conf:Conf):IClientModel {
  val client:PingClient<ServerPayload,ClientPayload> = PingClient(conf.host,conf.port,"socket",SerializeHelp.serverSayServerPayloadSerializer,SerializeHelp.clientSayClientPayloadSerializer)
  override val playerName get() = welcome?.id?.let {"Player $it"} ?: "Wait connection..."
  var welcome:Welcome?=null
  var recommendendLatency:Duration?=null

  init {
    client.connect {s:ServerPayload->
      synchronized(this) {
        if(s.welcome!=null) welcome = s.welcome
        if(s.recommendedLatency != null) recommendendLatency = s.recommendedLatency
      }
    }
  }
  val latency:Duration get() = recommendendLatency?: Duration(150)
  val realtimeTick get():Tick = welcome?.run{Tick((client.serverTime-roomCreate)/GameConst.UPDATE)}?:Tick(0)
  override fun ready() = welcome!=null
  override fun move(direction:Angle) = synchronized(this) {
    if(!ready()) return
    val t = realtimeTick + Tick(latency/GameConst.UPDATE+1)
    val a = ClientPayload.ClientAction(tick = t)
    a.moveDirection = direction
    client.say(ClientPayload(mutableListOf(a))) //todo если предудыщее отправление было в этом же тике, то задержать текущий набор действий на следующий tick
  }
  override fun newCar() = synchronized(this) {
    if(!ready()) return
    val t = realtimeTick + Tick(latency/GameConst.UPDATE+1)
    val a = ClientPayload.ClientAction(tick = t)
    a.newCar = true
    client.say(ClientPayload(mutableListOf(a)))
  }
  override fun dispose() { client.close() }

}
