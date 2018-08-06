package protocols

import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class Contract(uid: String, senz: String)

object ContractProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val format = jsonFormat2(Contract.apply)
}

