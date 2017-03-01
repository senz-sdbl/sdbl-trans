package components

import protocols.{Trans, Agent}


trait TransDbComp {

  val transDb: TransDb

  trait TransDb {
    def createAgent(agent: Agent)

    def getAgent(name: String): Option[Agent]

    def createTrans(trans: Trans)

    def updateTrans(trans: Trans)

    def getTrans(agent: String, timestamp: String): Option[Trans]
  }

}
