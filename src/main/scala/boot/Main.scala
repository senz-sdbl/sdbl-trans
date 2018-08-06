package boot

import actors._
import akka.actor.ActorSystem
import akka.io.IO
import config.AppConf
import db.DbFactory
import spray.can.Http
import utils.SenzFactory

/**
  * Created by eranga on 1/9/16.
  */
object Main extends App with AppConf {
  // setup logging
  // setup keys
  // init db
  SenzFactory.setupLogging()
  SenzFactory.setupKeys()
  DbFactory.initDb()

  implicit val system = ActorSystem("senz")

  // create and start rest service actor
  val restService = system.actorOf(ServiceActor.props(), "contract-service")

  // start HTTP server with rest service actor as a handler
  IO(Http) ! Http.Bind(restService, "0.0.0.0", switchPort)

  //import scala.concurrent.Await
  //import scala.concurrent.duration._
  //import db.dao.TranDAO
  //import db.dao.AgentDAO
  //import db.model.Transaction
  //import db.model.Agent
  //DbFactory.initDb()
  //Await.result(AgentDAO.create(Agent("eranga", "KO")), 5.seconds)
  //println(Await.result(TranDAO.getOrCreate(Transaction("342222", "deee", 300, "32323223", "D", Option("323232"), "eranga")), 5.seconds))
  //Await.result(TranDAO.updateStatus(Transaction("332323", "deee", 300, "443323232", "D", Option("00333"), "eranga")), 5.seconds)
}
