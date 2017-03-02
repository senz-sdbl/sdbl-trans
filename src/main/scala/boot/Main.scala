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
  SenzFactory.setupLogging()

  // setup keys
  SenzFactory.setupKeys()

  // init db
  DbFactory.initDb()

  implicit val system = ActorSystem("senz")

  // start senz actor
  val senzActor = system.actorOf(SenzActor.props, name = "SenzActor")
  senzActor ! InitSenz

  //  import scala.concurrent.Await
  //  import scala.concurrent.duration._
  //  import db.dao.TranDAO
  //  import db.model.Trans//  DbFactory.initDb()
  //  Await.result(TranDAO.create(Trans(Option(1), "34q4rrr", "deee", 300, "32323223", "P", "eranga")), 5.seconds)
}
