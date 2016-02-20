package components

import protocols.{Trans, Agent}


/**
 * Created by eranga on 2/2/16.
 */
trait TransDbComp {

  val transDb: TransDb

  trait TransDb {
    def createAgent(agent: Agent)

    def getAgent(name: String): Agent

    def createTrans(trans: Trans)

    def updateTrans(trans: Trans)

    def getTrans(agent: String, timestamp: String): Trans
  }

}
