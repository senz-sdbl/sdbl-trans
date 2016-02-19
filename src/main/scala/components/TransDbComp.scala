package components

case class Agent(username: String, branch: String)

case class Balance(agent: String, timestamp: String, account: String, nic: String, amount: String, status: String)

/**
 * Created by eranga on 2/2/16.
 */
trait TransDbComp {

  val transDb: TransDb

  trait TransDb {
    def createAgent(agent: Agent)

    def getAgent(name: String): Agent

    def createBalance(balance: Balance)

    def updateBalance(balance: Balance)

    def getBalance(agent: String, timestamp: String): Balance
  }

}
