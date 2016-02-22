package utils

import actors.TransMsg
import protocols.{Senz, Trans}

/**
 * Created by eranga on 2/20/16.
 */
object TransUtils {
  def getTrans(senz: Senz): Trans = {
    val agent = senz.sender
    val timestamp = senz.attributes.getOrElse("time", "")
    val acc = senz.attributes.getOrElse("acc", "")
    val amnt = senz.attributes.getOrElse("amnt", "")

    Trans(agent, timestamp, acc, amnt, "PENDING")
  }

  def getTransMsg(trans: Trans): TransMsg = {
    TransMsg("transMsg")
  }

  def getTransResp(response: String) = {

  }
}
