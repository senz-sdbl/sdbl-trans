package actors

import java.net.URL

import akka.actor.{Actor, Props}
import akka.util.Timeout
import config.AppConf
import protocols.Msg
import utils.SenzLogger

import scala.concurrent.{Await, Future}
import scala.io.Source
import scala.util.parsing.json.JSON
import scala.util.{Success, Try}

object BalInqHandler {

  case class BalInq(agent: String, account: String)

  case class BalInqMsg(msgStream: Array[Byte])

  case class BalInqResp(esh: String, status: String, authCode: String, rst: String)

  case class BalInqTimeout()

  def props: Props = Props(new BalInqHandler)

}

class BalInqHandler extends Actor with AppConf with SenzLogger {

  import BalInqHandler._

  import scala.concurrent.ExecutionContext.Implicits._
  import scala.concurrent.duration._

  implicit val timeout = Timeout(30.seconds)

  // we need senz sender to send reply back
  val senzActor = context.actorSelection("/user/SenzActor")

  override def preStart(): Unit = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case BalInq(agent, account) =>
      logger.debug(s"balance inquery $agent $account")

      // call http endpoint
      val f = doInq(account.replaceAll("^0*", ""))
      Try(Await.result(f, timeout.duration)) match {
        case Success(response) =>
          logger.info(s"inq response: $response")

          val json = JSON.parseFull(response).asInstanceOf[Some[Map[String, List[Any]]]]
          val bal = json.flatMap(c =>
            c("CustomerDetails")
              .head.asInstanceOf[Map[String, Any]]
              .get("BalanceDetails")
              .flatMap(_.asInstanceOf[List[Map[String, Any]]].head.get("MemoBalance").map(_.asInstanceOf[String])))
          val name = json.flatMap(c =>
            c("CustomerDetails")
              .head.asInstanceOf[Map[String, Any]]
              .get("FullOrDispName")
              .map(_.asInstanceOf[String])
          )
          logger.info(s"balance $bal")
          logger.info(s"name $name")

          // return to sender
          val senz = s"DATA #bal ${bal.getOrElse("")} #name ${name.map(_.replaceAll(" ", "|")).getOrElse("")} @$agent ^$senzieName"
          senzActor ! Msg(senz)
        case e =>
          logger.info(s"fail to complete acc inq $e")
          val senz = s"DATA #status ERROR @$agent ^$senzieName"
          senzActor ! Msg(senz)

          context.stop(self)
      }
  }

  private def doInq(acc: String): Future[String] = {
    val url = new URL(s"http://10.100.31.43:8080/dailyCollection/DailyCollectionAccountDetails?ACCOUNTNUMBER=$acc")
    val conn = url.openConnection.asInstanceOf[java.net.HttpURLConnection]
    conn.setRequestMethod("GET")
    conn.setDoOutput(true)

    // read response to string
    Future {
      Source.fromInputStream(conn.getInputStream).mkString
    }
  }
}

//object M extends App {
//  val response =
//    """
//      |{"CustomerDetails":[{"CIF":"","NewNICNo":"","SuccessMsg":"No Records Found","ShortName":"","City":"","Gender":"","CustomerType":"","OldNICNo":"","Nationality":"","DOB":"","BalanceDetails":[{"CurrentBalance":"3444","MemoBalance":"","SuccessMsg":"No Records Found","AccountNumber":""}],"FullOrDispName":"eranga","BranchCode":""}]}
//    """.stripMargin
//
//  // parse json
//  //  val bal = for {
//  //    Some(map: Map[String, List[Any]]) <- JSON.parseFull(response)
//  //    Some(cl: List[Any]) <- map.get("CustomerDetails")
//  //    Some(c: Map[String, Any]) <- map.get("CustomerDetails")
//  //    Some(bl: List[Any]) <- c.get("BalanceDetails")
//  //    Some(b: Map[String, Any]) <- bl.headOption
//  //  } yield {
//  //    b("CurrentBalance").asInstanceOf[String]
//  //  }
//
//
//  val json = JSON.parseFull(response).asInstanceOf[Some[Map[String, List[Any]]]]
//  val bal = json.flatMap(c =>
//    c("CustomerDetails")
//      .head.asInstanceOf[Map[String, Any]]
//      .get("BalanceDetails")
//      .flatMap(_.asInstanceOf[List[Map[String, Any]]].head.get("CurrentBalance").map(_.asInstanceOf[String])))
//
//  //    .map(
//  //      l => l.headOption.asInstanceOf[Some[Map[String, List[Any]]]]
//  //        .get("BalanceDetails")
//  //        .map(m => m.headOption.asInstanceOf[Map[String, Any]])
//  //        .get("CurrentBalance")
//  //    )
//  val name = json.flatMap(c =>
//    c("CustomerDetails")
//      .head.asInstanceOf[Map[String, Any]]
//      .get("FullOrDispName")
//  )
//
//  println(bal)
//  println(name)
//}
