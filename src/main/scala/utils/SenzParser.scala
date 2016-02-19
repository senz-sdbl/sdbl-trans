package utils

object SenzType extends Enumeration {
  type SenzType = Value
  val SHARE, GET, PUT, DATA, PING = Value
}

import SenzType._

case class Senz(senzType: SenzType, sender: String, receiver: String, attributes: scala.collection.mutable.Map[String, String], signature: String)

/**
 * Created by eranga on 1/10/16.
 */
object SenzParser {
  def getSenz(msg: String): Senz = {
    val tokens = msg.split(" ")
    val senzType = SenzType.withName(tokens.head)
    val signature = tokens.last
    var sender = ""
    var receiver = ""
    val attr = scala.collection.mutable.Map[String, String]()

    // remove first and last element of the token list
    tokens.drop(1).dropRight(1)

    var i = 0
    while (i < tokens.length) {
      if (tokens(i).startsWith("@")) {
        // receiver
        sender = tokens(i)
      } else if (tokens(i).startsWith("^")) {
        // sender
        receiver = tokens(i)
      } else if (tokens(i).startsWith("#")) {
        // attribute
        if (tokens(i + 1).startsWith("#") || tokens(i + 1).startsWith("@") | tokens(i + 1).startsWith("^")) {
          attr(tokens(i)) = ""
        } else {
          attr(tokens(i)) = tokens(i + 1)
          i += 1
        }
      }

      i += 1
    }

    Senz(senzType, sender, receiver, attr, signature)
  }

  def getSenzPayload(senz: Senz) = {
  }
}
