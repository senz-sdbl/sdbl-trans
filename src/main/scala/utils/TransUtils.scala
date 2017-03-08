package utils

import java.text.SimpleDateFormat
import java.util.Calendar

import actors.TransHandler.{TransMsg, TransResp}
import db.model.Transaction
import protocols.Senz


object TransUtils {
  def getTrans(senz: Senz): Transaction = {
    val agent = senz.sender
    val uid = senz.attributes("#uid")
    val customer = senz.attributes.getOrElse("#acc", "")
    val amnt = senz.attributes.getOrElse("#amnt", "").toInt
    val timestamp = senz.attributes.getOrElse("#time", "")
    val mobile = senz.attributes.get("#mob")

    Transaction(uid, customer, amnt, timestamp, "P", mobile, agent)
  }

  def getTransMsg(trans: Transaction) = {
    val fundTranMsg = generateFundTransMsg(trans)
    val esh = generateEsh
    val msg = s"$esh$fundTranMsg"
    val header = generateHeader(msg)

    TransMsg(header ++ msg.getBytes)
  }

  def generateFundTransMsg(trans: Transaction) = {
    val pip = "|" // terminating pip for all attributes
    val rnd = new scala.util.Random //  genaration of transaction ID
    val randomInt = 100000 + rnd.nextInt(900000) //  random num of 6 digits
    val transId = s"$randomInt$getTransTime" // random in of length 6 and time stamp of 10 digits

    val payMode = "02" // pay mode
    val epinb = "ffffffffffffffff" // ePINB, 16 digits
    val offset = "ffffffffffff" // offset, 12 digits
    val mobile = trans.mobile.getOrElse("0000000000")
    val fromAcc = trans.agent
    val toAcc = trans.customer
    val amnt = "%012d".format(trans.amount) // amount, 12 digits

    s"$transId$pip$payMode$pip$epinb$pip$offset$pip$mobile$pip$fromAcc$pip$toAcc$pip$amnt"
  }

  def generateEsh = {
    val pip = "|" // add a pip after the ESH
    val a = "DEP" // incoming channel mode[mobile]
    val b = "01" // transaction process type[financial]
    val c = "13" // transaction code[Cash deposit{UCSC}]
    val d = "00000002" // TID, 8 digits
    val e = "000000000000002" // MID, 15 digits

    val rnd = new scala.util.Random // generation of trace no
    val f = 100000 + rnd.nextInt(900000) // generation of trace no
    val g = getTransTime // date time MMDDHHMMSS
    val h = "0001" // application ID, 4 digits
    val i = "0000000000000000" // private data, 16 digits

    s"$a$b$c$d$e$f$g$h$i$pip"
  }

  def generateHeader(msg: String) = {
    val hexLen = f"${Integer.toHexString(msg.getBytes.length).toUpperCase}%4s".replaceAll(" ", "0")

    // convert hex to bytes
    hexLen.replaceAll("[^0-9A-Fa-f]", "").sliding(2, 2).toArray.map(Integer.parseInt(_, 16).toByte)
  }

  def getTransTime = {
    val now = Calendar.getInstance().getTime
    val format = new SimpleDateFormat("MMddhhmmss")

    format.format(now)
  }

  def getTransResp(response: String) = {
    TransResp(response.substring(0, 70), response.substring(77, 79), response.substring(72))
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