package exceptions

case class InvalidSenzException(msg: String) extends Exception

case class EmptySenzException(msg: String) extends Exception
