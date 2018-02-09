package boot

import actors.SenzActor.InitSenz
import actors._
import akka.actor.ActorSystem
import db.DbFactory
import utils.SenzFactory

/**
  * Created by eranga on 1/9/16.
  */
object Main extends App {
  // setup logging
  // setup keys
  // init db
  SenzFactory.setupLogging()
  SenzFactory.setupKeys()
  DbFactory.initDb()

  implicit val system = ActorSystem("senz")

  // start senz actor
  val senzActor = system.actorOf(SenzActor.props, name = "SenzActor")
  senzActor ! InitSenz

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
