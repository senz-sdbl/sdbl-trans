package utils

import actors.TransMsg
import protocols.{Senz, Trans}

object TransUtils {
  def getTrans(senz: Senz): Trans = {
    val agent = senz.sender
    val timestamp = senz.attributes.getOrElse("time", "")
    val acc = senz.attributes.getOrElse("acc", "")
    val amnt = senz.attributes.getOrElse("amnt", "")

    Trans(agent, timestamp, acc, amnt, "PENDING")
  }

  def getTransMsg(trans: Trans): TransMsg = {
    TransMsg("Yahoooo")
  }

  def generateFundTransferMsg(fromAcc: String, toAcc: String, amount: Int) = {
    val transId = "00000000000000001" // transaction ID, 16 digits // TODO generate unique value
    val payMode = "02" // pay mode
    val epinb = "ffffffffffffffff" // ePINB, 16 digits
    val offset = "ffffffffffff" // offset, 12 digits
    val mobileNo = "0775432015" // customers mobile no
    val fromAcc = "234323432323" // from account, bank account, 12 digits
    val toAcc = "434523432343" // to account, customer account, 12 digits
    val amount = "000000002400" // amount, 12 digits // TODO generate this

    s"$transId$payMode$epinb$offset$mobileNo$fromAcc$toAcc$amount"
  }

  def generateEsh(msgLen: String) = {
    val l = msgLen // fund transfer message length
    val a = "MOB" // incoming channel mode[mobile]
    val b = "01" // transaction process type[financial]
    val c = "04" // transaction code[fund transfer]
    val d = "00000001" // TID, 8 digits
    val e = "000000000000001" // MID, 16 digits
    val f = "000001" // trace no, 6 digits TODO generate this
    val g = "0323024530" // date time MMDDHHMMSS // TODO generate this
    val h = "0001" // application ID, 4 digits
    val i = "0000000000000000" // private data, 16 digits

    s"$l$a$b$c$d$e$f$g$i"
  }

  def getTransResp(response: String) = {

  }
}
