package utils

import java.text.SimpleDateFormat
import java.util.Calendar

import actors.{TransResp, TransMsg}
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
    val msg = generateFundTransferMsg(trans)
    val msgLen = f"${Integer.toHexString(msg.length).toUpperCase}%4s".replaceAll(" ", "0")
    val esh = generateEsh(msgLen)

    TransMsg(s"$esh$msg")
  }

  def generateFundTransferMsg(trans: Trans) = {
    val transId = "0000000000000001" // transaction ID, 16 digits // TODO generate unique value
    val payMode = "02" // pay mode
    val epinb = "ffffffffffffffff" // ePINB, 16 digits
    val offset = "ffffffffffff" // offset, 12 digits
    val mobileNo = "0775432015" // customers mobile no
    val fromAcc = "343434343434" // TODO trans.agent // from account, bank account, 12 digits
    val toAcc = "646464646464" // TODO trans.account // to account, customer account, 12 digits
    val amnt = "%012d".format(trans.amount.toInt) // amount, 12 digits

    s"$transId$payMode$epinb$offset$mobileNo$fromAcc$toAcc$amnt"
  }

  def generateEsh(msgLen: String) = {
    val l = msgLen // fund transfer message length (0000-FFFF)
    val a = "MOB" // incoming channel mode[mobile]
    val b = "01" // transaction process type[financial]
    val c = "04" // transaction code[fund transfer]
    val d = "00000002" // TID, 8 digits TODO in prod 00000001
    val e = "000000000000002" // MID, 15 digits TODO in prod 000000000000001
    val f = "000001" // trace no, 6 digits TODO generate this
    val g = getTransTime // date time MMDDHHMMSS
    val h = "0001" // application ID, 4 digits
    val i = "0000000000000000" // private data, 16 digits

    s"$l$a$b$c$d$e$f$g$h$i"
  }

  def getTransTime = {
    val now = Calendar.getInstance().getTime
    val format = new SimpleDateFormat("MMddhhmmss")

    format.format(now)
  }

  def getTransResp(response: String) = {
    TransResp(response.substring(0, 70), response.substring(70, 72), response.substring(72))
  }

}

//object Main extends App {
//  val agent = "222222222222"
//  val customer = "555555555555"
//  val msg = TransUtils.getTransMsg(Trans(agent, "3423432", customer, "250", "PENDING"))
//  println(msg)
//
//
//  TransUtils.getTransResp(msg.msg) match {
//    case TransResp(_, "11", _) =>
//      println("Transaction done")
//    case TransResp(_, status, _) =>
//      println("hoooo " + status)
//    case transResp =>
//      println("Invalid response " + transResp)
//  }
//}