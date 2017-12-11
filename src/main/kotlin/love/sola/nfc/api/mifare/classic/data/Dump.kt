package love.sola.nfc.api.mifare.classic.data

import love.sola.nfc.api.mifare.classic.constants.KeyType
import love.sola.nfc.api.mifare.classic.constants.MifareClassicCardType
import java.io.Serializable
import java.util.*

/**
 * @author Sola
 */
class Dump(private val sectors: Array<Sector>) : Cloneable, Serializable {

    val uidBlock: Block get() = this[0][0]

    companion object {
        val DEFAULT_1K = MifareClassicCardType.MIFARE_CLASSIC_1K.defaultDump()

        val DEFAULT_4K = MifareClassicCardType.MIFARE_CLASSIC_4K.defaultDump()

        fun parse(data: ByteArray): Dump {
            val type = MifareClassicCardType.values().first { data.size == it.size }
            val ins = data.inputStream()
            val buf = ByteArray(16)
            val blocks = Array(type.blocksCount) {
                ins.read(buf)
                Block(buf.clone())
            }
            return parse(blocks)
        }

        fun parse(blocks: Array<Block>): Dump {
            val type = MifareClassicCardType.values().first { blocks.size == it.blocksCount }
            val sectors = arrayListOf<Sector>()
            var prev = 0
            for (sectorIndex in type.layout) {
                sectors.add(Sector(blocks.sliceArray(prev..sectorIndex)))
                prev = sectorIndex + 1
            }
            return Dump(sectors.toTypedArray())
        }
    }

    fun extractKey(keyType: KeyType): List<Key> = sectors.map {
        when (keyType) {
            KeyType.A -> it.keyA
            KeyType.B -> it.keyB
        }
    }

    fun isValidUID(): Boolean =
            uidBlock[4] == (uidBlock[0].toInt() xor uidBlock[1].toInt() xor uidBlock[2].toInt() xor uidBlock[3].toInt()).toByte()

    val size get() = sectors.size
    operator fun get(index: Int): Sector = sectors[index]

    override fun toString(): String {
        return "Dump(${Arrays.toString(sectors)})"
    }

}
