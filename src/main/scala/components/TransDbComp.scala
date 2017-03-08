package components

import db.model.{Agent, Transaction}


trait TransDbComp {

  val transDb: TransDb

  trait TransDb {
    def createAgent(agent: Agent)

    def getAgent(name: String): Option[Agent]

    def createTrans(trans: Transaction)

    def updateTrans(trans: Transaction)

    def getTrans(agent: String, timestamp: String): Option[Transaction]
  }

}
