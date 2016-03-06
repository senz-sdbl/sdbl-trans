package utils

import protocols.{SenzType, Senz}


/**
 * Created by eranga on 1/10/16.
 */
object SenzParser {
  def getSenz(senzMsg: String): Senz = {
    val tokens = senzMsg.split(" ")
    val senzType = SenzType.withName(tokens.head)
    val signature = if (tokens.last.startsWith("^")) None else Some(tokens.last.trim)
    var sender = ""
    var receiver = ""
    val attr = scala.collection.mutable.Map[String, String]()

    // remove first and last element of the token list
    tokens.drop(1).dropRight(1)

    var i = 0
    while (i < tokens.length) {
      if (tokens(i).startsWith("@")) {
        // receiver
        receiver = tokens(i).substring(1)
      } else if (tokens(i).startsWith("^")) {
        // sender
        sender = tokens(i).substring(1)
      } else if (tokens(i).startsWith("#")) {
        // attribute
        if (tokens(i + 1).startsWith("#") || tokens(i + 1).startsWith("@") | tokens(i + 1).startsWith("^")) {
          attr(tokens(i).substring(1)) = ""
        } else {
          attr(tokens(i).substring(1)) = tokens(i + 1)
          i += 1
        }
      }

      i += 1
    }

    Senz(senzType, sender, receiver, attr, signature)
  }

  def getSenzMsg(senz: Senz): String = {
    // attributes comes as
    //    1. #lat 3.432 #lon 23.343
    //    2. #lat #lon
    var attr = ""
    for ((k, v) <- senz.attributes) {
      attr += s"#$k $v".trim + " "
    }

    s"${senz.senzType} ${attr.trim} @${senz.receiver} ^${senz.sender} ${senz.signature}"
  }
}

//object Main extends App {
//  val senz = SenzParser.getSenz("SHARE #lat #lon sdf @era ^bal")
//
//  println(senz.senzType)
//  println(senz.attributes)
//  println(senz.receiver)
//  println(senz.sender)
//  println(senz.signature)
//
//  val msg = SenzParser.getSenzMsg(senz)
//  println(msg)
//}
