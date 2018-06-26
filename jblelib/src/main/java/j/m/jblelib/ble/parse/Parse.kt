package j.m.jblelib.ble.parse

/**
 * Created by 111 on 2018/4/26.
 */
interface Parse {
    fun parseData(byte: Byte)
    fun isWorking(): Boolean
    //ICEGPS ,S, 11 22 33 44 55 * 00
}