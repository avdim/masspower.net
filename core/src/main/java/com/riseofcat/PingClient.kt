package com.riseofcat

import com.github.czyzby.websocket.*
import com.github.czyzby.websocket.data.WebSocketCloseCode
import com.github.czyzby.websocket.data.WebSocketState
import com.github.czyzby.websocket.net.ExtendedNet
import com.google.gson.*
import com.riseofcat.lib_gwt.LibAllGwt
import com.riseofcat.lib_gwt.Signal
import com.riseofcat.share.ClientSay
import com.riseofcat.share.Params
import com.riseofcat.share.ServerSay
import com.riseofcat.common.fromJson
import com.riseofcat.common.toJson

import java.util.ArrayDeque
import java.util.LinkedList
import kotlin.reflect.*

class PingClient<S:Any,C>(private val json:Gson,host:String,port:Int,path:String,typeS:KClass<ServerSay<S>>) {
  private val incoming = Signal<S>()
  private val socket:WebSocket
  private val queue = LinkedList<ClientSay<C>>()//todo test
  var smartLatencyS = Params.DEFAULT_LATENCY_S
  var latencyS = Params.DEFAULT_LATENCY_S
  private val latencies = ArrayDeque<LatencyTime>()
  val state:WebSocketState
    get() = socket.state

  init {
    latencies.add(LatencyTime(Params.DEFAULT_LATENCY_MS,App.timeMs()))
    socket = if(LibAllGwt.TRUE()) ExtendedNet.getNet().newWebSocket(host,port,path) else WebSockets.newSocket(WebSockets.toWebSocketUrl(host,port,path))
    socket.addListener(object:WebSocketAdapter() {
      override fun onOpen(webSocket:WebSocket?):Boolean {
        while(queue.peek()!=null) sayNow(queue.poll())
        return WebSocketListener.FULLY_HANDLED
      }

      override fun onClose(webSocket:WebSocket?,code:WebSocketCloseCode?,reason:String?):Boolean {
        return WebSocketListener.FULLY_HANDLED
      }

      override fun onMessage(webSocket:WebSocket?,packet:String):Boolean {
        if(false) App.log.info(packet)
        val serverSay = json.fromJson(packet,typeS.java)
        if(serverSay.latency!=null) {
          latencyS = serverSay.latency!!/LibAllGwt.MILLIS_IN_SECCOND
          latencies.offer(LatencyTime(serverSay.latency!!,App.timeMs()))
          while(latencies.size>100) latencies.poll()
          var sum = 0f
          var weights = 0f
          val time = App.timeMs()
          for(l in latencies) {
            var w = (1-LibAllGwt.Fun.arg0toInf((time-l.time).toDouble(),10000f)).toDouble()
            w *= (1-LibAllGwt.Fun.arg0toInf(l.latency.toDouble(),Params.DEFAULT_LATENCY_MS.toFloat())).toDouble()
            sum += (w*l.latency).toFloat()
            weights += w.toFloat()
          }
          if(weights>java.lang.Float.MIN_VALUE*1E10) smartLatencyS = sum/weights/LibAllGwt.MILLIS_IN_SECCOND
        }
        if(serverSay.ping) {
          val answer = ClientSay<C>()
          answer.pong = true
          say(answer)
        }
        if(serverSay.payload!=null) incoming.dispatch(serverSay.payload)
        return WebSocketListener.FULLY_HANDLED
      }

      override fun onMessage(webSocket:WebSocket?,packet:ByteArray?):Boolean {
        return super.onMessage(webSocket,packet)
      }

      override fun onError(webSocket:WebSocket?,error:Throwable?):Boolean {
        return super.onError(webSocket,error)//todo
      }
    })
  }

  fun connect(incomeListener:Signal.Listener<S>) {
    incoming.add(incomeListener)
    try {
      socket.connect()
    } catch(e:Exception) {
      Todo.handleOffline()//todo
      //e.printStackTrace();
    }

  }

  fun close() {
    WebSockets.closeGracefully(socket) // Null-safe closing method that catches and logs any exceptions.
    if(false) socket.close()
  }

  fun say(payload:C) {
    val answer = ClientSay<C>()
    answer.payload = payload
    say(answer)
  }

  private fun say(say:ClientSay<C>) {
    if(socket.state==WebSocketState.OPEN)
      sayNow(say)
    else
      queue.offer(say)
  }

  private fun sayNow(say:ClientSay<C>) {
    var attempt = 0
    while(attempt++<3) {//todo Костыль JSON сериализации
      try {
        socket.send(json.toJson(say))
        return
      } catch(t:Throwable) {
      }

    }
    App.log.error("sayNow 3 attempts fail")
  }

  private class LatencyTime(val latency:Int,val time:Long)
}
