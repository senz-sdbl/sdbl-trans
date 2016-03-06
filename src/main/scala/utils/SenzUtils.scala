package utils

import config.Configuration
import crypto.RSAUtils
import exceptions.EmptySenzException

/**
 * Created by eranga on 1/11/16.
 */
object SenzUtils extends Configuration {
  def isValidSenz(msg: String) = {
    if (msg == null || msg.isEmpty)
      throw new EmptySenzException("Empty Senz")

    SenzParser.getSenz(msg)
  }

  def getRegistrationSenzMsg = {
    // unsigned senz
    val publicKey = RSAUtils.loadRSAPublicKey()
    val timestamp = (System.currentTimeMillis / 1000).toString
    val receiver = switchName
    val sender = clientName

    s"SHARE #pubkey $publicKey #time $timestamp @$receiver ^$sender"
  }

  def getPingSenzMsg = {
    // unsigned senz
    val timestamp = (System.currentTimeMillis / 1000).toString
    val receiver = switchName
    val sender = clientName

    s"PING #time $timestamp @$receiver ^$sender"
  }
}
