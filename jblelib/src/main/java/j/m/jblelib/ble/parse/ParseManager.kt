package j.m.jblelib.ble.parse

import j.m.jblelib.ble.threadpool.ThreadPool

/**
 * Created by 111 on 2018/4/26.
 */
object ParseManager {
    var hex: ParseHex
    var ascii: ParseAscii

    init {
        hex = ParseHex()
        ascii = ParseAscii()
    }


    fun parseData(byteArray: ByteArray) {
        byteArray.forEach {
            if (hex.isWorking()) {
                hex.parseData(it)
            } else if (ascii.isWorking()) {
                ascii.parseData(it)
            } else {
                hex.parseData(it)
                ascii.parseData(it)
            }
        }

    }
}