package components


/**
 * Database component
 */
trait MsgDbComp {

  val msgDb: MsgDb

  trait MsgDb {
    def saveMsg(msg: String)
  }

}
