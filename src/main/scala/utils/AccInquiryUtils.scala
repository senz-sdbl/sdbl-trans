package utils

import java.text.SimpleDateFormat
import java.util.Calendar

import actors.AccInqHandler.{AccInq, AccInqMsg, AccInqResp}
import protocols.Senz


object AccInquiryUtils {

  def getAccInq(senz: Senz): AccInq = {
    val nic = senz.attributes.getOrElse("#nic", "")
    val agent = senz.sender
    AccInq(agent, nic)
  }

  def getAccInqMsg(accInq: AccInq) = {
    val esh = generateEsh
    val accInqMsg = generateAccInqMassage(accInq)
    val msg = s"$esh$accInqMsg"
    val header = generateHeader(msg)

    AccInqMsg(header ++ msg.getBytes)
  }

  private def generateAccInqMassage(accInq: AccInq) = {
    // terminating pip for all attributes
    val pipe = "|"

    val nic = accInq.nic

    //  generation of transaction ID
    val rnd = new scala.util.Random
    val randomInt = 100000 + rnd.nextInt(900000)

    // random num of 6 digits
    // random in of length 6 and time stamp of 10 digits
    val transId = s"$randomInt$getTransTime"

    // pay mode
    val requestMode = "02"

    s"$transId$pipe$requestMode$pipe$nic"
  }

  private def generateEsh = {
    val pipe = "|" // add a pipe after the ESH
    val a = "DEP" // incoming channel mode[mobile]
    val b = "01" // transaction process type[financial]
    val c = "06" // transaction code[Cash deposit{UCSC}]
    val d = "00000002" // TID, 8 digits
    val e = "000000000000002" // MID, 15 digits
    val rnd = new scala.util.Random
    val f = 100000 + rnd.nextInt(900000) // generation of trace no
    val g = getTransTime // date time MMDDHHMMSS
    val h = "0001" // application ID, 4 digits
    val i = "0000000000000000" // private data, 16 digits

    s"$a$b$c$d$e$f$g$h$i$pipe"
  }

  private def generateHeader(msg: String) = {
    val hexLen = f"${Integer.toHexString(msg.getBytes.length).toUpperCase}%4s".replaceAll(" ", "0")

    // convert hex length to bytes
    hexLen.replaceAll("[^0-9A-Fa-f]", "").sliding(2, 2).toArray.map(Integer.parseInt(_, 16).toByte)
  }

  private def getTransTime = {
    val now = Calendar.getInstance().getTime
    val format = new SimpleDateFormat("MMddhhmmss")

    format.format(now)
  }

  def getAccInqResp(response: String) = {
    val tuples = response.split("\\|")
    val status = tuples(0).substring(tuples(0).length - 2, tuples(0).length)
    AccInqResp("ESH", status, "AUTH", tuples(3))
  }

}


/*
SUCCESS
Request-massage   SMS010600000002000000000000002787227020310503800011456789456213654994908572057813|02|123456789v
                  aSMS010600000002000000000000002666738030406401400010000000000000000|4196370304064014|02|781142182V
Request-packet    005F534D533031303630303030303030323030303030303030303030303030323738373232373032303331303530333830303031313435363738393435363231333635343939343930383537323035373831337C30327C31323334353637383976
Response-packet   00C4534D53303130363030303030303032303030303030303030303030303032373837323237323031372D30322D30332031303A34353A35372E3330383134353637383934353632313336353430307C3032303331303435353738337C3637383931327C3031233031323334353637383931302373616E736120746573743123313233347E3032233031323334353637383931312373616E736120746573743223323334357E3031233031323334353637383931322373616E73612074657374332331323334
Response-message  SMS0106000000020000000000000027872272017-02-03 10:45:57.308145678945621365400|020310455783|678912|01#012345678910#sansa test1#1234~02#012345678911#sansa test2#2345~01#012345678912#sansa test3#1234
                                                                                                                    AccountType#AccountNumber#OwnerName#CIF ==> 01#012345678910#sansa test1#1234
                                                                                                                                                              > 02#012345678911#sansa test2#2345
                                                                                                                                                              > 01#012345678912#sansa test3#1234


FAIL
Request-massage   SMS0113000000020000000000000029215120203102836000114567894562136545800270600997561|02|123456789v
Request-packet    0060534D53303131333030303030303032303030303030303030303030303032393231353132303230333130323833363030303131343536373839343536323133363534353830303237303630303939373536317C30327C31323334353637383976
Response-packet   0061534D53303131333030303030303032303030303030303030303030303032393231353132323031372D30322D30332031303A32333A35352E3434323134353637383934353632313336353443387C3032303331303233353537307C313032333535
Response-message  SMS0113000000020000000000000029215122017-02-03 10:23:55.4421456789456213654C8|020310235570|102355


*/
