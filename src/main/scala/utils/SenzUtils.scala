package utils

import config.Configuration
import crypto.RSAUtils

/**
 * Created by eranga on 1/11/16.
 */
object SenzUtils extends Configuration {
  def getRegistrationSenz() = {
    // unsigned senz
    val publicKey = RSAUtils.loadRSAPublicKey()
    val timestamp = (System.currentTimeMillis / 1000).toString
    val receiver = switchName
    val sender = clientName
    val unSignedSenzPayload = s"SHARE #pubkey $publicKey #time $timestamp @$receiver ^$sender"

    // sign senz
    val senzSignature = RSAUtils.signSenz(unSignedSenzPayload.replaceAll(" ", ""))
    s"$unSignedSenzPayload $senzSignature"
  }

  def getPingSenz() = {
    // unsigned senz
    val timestamp = (System.currentTimeMillis / 1000).toString
    val receiver = switchName
    val sender = clientName
    val unSignedSenzPayload = s"PING #time $timestamp @$receiver ^$sender"

    // sign senz
    val senzSignature = RSAUtils.signSenz(unSignedSenzPayload.replaceAll(" ", ""))
    s"$unSignedSenzPayload $senzSignature"
  }
}
