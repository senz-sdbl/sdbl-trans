package utils

import db.model.Agent
import protocols.Senz

object AgentUtil {

  def getAgent(senz: Senz): Agent = {
    Agent(senz.attributes("branch"), "agent")
  }
}
