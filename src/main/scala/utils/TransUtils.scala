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
    val timestamp = (System.currentTimeMillis() / 1000).toString
    val mobile = senz.attributes.get("#mob")

    Transaction(uid, customer, amnt, timestamp, "PENDING", mobile, agent)
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
    val fromAcc = "0" * (12 - trans.agent.length) + trans.agent
    val toAcc = "0" * (12 - trans.customer.length) + trans.customer
    val amnt = "%010d".format(trans.amount) + "00" // amount, 12 digits
    fromAcc.format()

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
    val tuples = response.split("\\|")
    val status = tuples(0).substring(tuples(0).length - 2, tuples(0).length)
    TransResp("ESH", status, "RSP")
  }
}

