import love.sola.nfc.api.getCard
import love.sola.nfc.api.mifare.classic.constants.KeyType
import love.sola.nfc.api.mifare.classic.data.Block
import love.sola.nfc.api.mifare.classic.data.Key
import love.sola.nfc.api.mifare.classic.functions.isBlank
import love.sola.nfc.api.waitDisconnect
import java.util.*

/**
 * This script provides following functions:
 *  - create a new DJMAX Techinka 3 card
 *  - lookup DJMAX Technika 3 card's serial number
 */

val serialChars = ('A'..'Z' union '1'..'9').toList()
val random = Random()

val keyA = Key("3721536A7240")
val trailerBlock = Block("3721536A7240FF078069FFFFFFFFFFFF")
while (true) {
    val card = getCard()!!

    if (card.authBlock(Key.DEFAULT, 0, KeyType.A)) {
        val serial = genSerial()
        println("Serial: $serial")
        val data1 = Block(serial.substring(0, 16).toByteArray(Charsets.US_ASCII))
        val data2 = Block(serial.substring(16, 20).toByteArray(Charsets.US_ASCII).copyOf(16))
        card.writeBlock(1, data1)
        card.writeBlock(2, data2)
        card.writeBlock(3, trailerBlock)
        println("Done.")
    } else {
        if (card.authBlock(keyA, 0, KeyType.A)) {
            val data1 = card.readBlock(1)
            val data2 = card.readBlock(2)
            val serial = (data1.data() + data2.data()).toString(Charsets.US_ASCII).take(20)
            println("Formatted card, Serial: $serial")
        } else {
            println("Invalid card.")
        }
    }
    waitDisconnect()
}


fun genSerial(): String {
    val sb = StringBuilder()
    repeat(20) {
        val i = random.nextInt(serialChars.size)
        sb.append(serialChars[i])
    }
    return sb.toString()
}
