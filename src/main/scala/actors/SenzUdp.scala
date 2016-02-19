package actors

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.{IO, UdpConnected}
import akka.util.ByteString

case class SenzMessage(msg: String)

/**
 * Created by eranga on 1/22/16.
 */
class SenzUdp(remote: InetSocketAddress) extends Actor {

  import context.system

  override def preStart = {
    println("----started----- " + context.self.path)
  }

  // connect to senz switch via udp
  IO(UdpConnected) ! UdpConnected.Connect(self, remote)

  def receive = {
    case UdpConnected.Connected =>
      context.become(ready(sender()))

      // TODO handle registration via actor

      // init ping sender
      val pingSender = context.actorSelection("/user/PingSender")
      pingSender ! InitPing

      // init SenzReader
      val senzReader = context.actorSelection("/user/SenzReader")
      senzReader ! InitReader
  }

  def ready(connection: ActorRef): Receive = {
    case UdpConnected.Received(data) =>
      println(data)
    case SenzMessage(msg) =>
      connection ! UdpConnected.Send(ByteString(msg))
    case UdpConnected.Disconnect =>
      connection ! UdpConnected.Disconnect
    case UdpConnected.Disconnected =>
      // reconnect after disconnecting
      IO(UdpConnected) ! UdpConnected.Connect(self, remote)
      //context.stop(self)
      // TODO reconnect
  }
}