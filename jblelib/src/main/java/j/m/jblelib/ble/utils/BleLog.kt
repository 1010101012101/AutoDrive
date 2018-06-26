package j.m.jblelib.ble.utils

import android.util.Log

/**
 * Created by 111 on 2018/4/12.
 */
class BleLog {
    companion object {
        var isPrintLog = true;
        fun d(any: Any, vararg msg: String): Int {
            var content = "";
            for (i in msg.indices) {
                if (i == msg.size - 1) {
                    content += msg[i]
                } else {
                    content += msg[i] + "-->"
                }
            }
            content = content.trim()
            return if (isPrintLog) Log.d("BleLog-->" + any.javaClass.simpleName, content) else -1
        }
    }
}