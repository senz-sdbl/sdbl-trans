package protocols

case class Agent(username: String, branch: String)

case class Trans(agent: String, timestamp: String, account: String, amount: String, status: String)
