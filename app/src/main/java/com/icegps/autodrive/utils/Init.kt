package com.icegps.autodrive.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.widget.Toast

class Init {
    companion object {
        private var toast: Toast? = null
        private lateinit var handler: Handler
        lateinit var context: Context

        fun init(context: Context) {
            this.context = context
            handler = Handler()
        }

        fun post(runnable: Runnable) {
            handler.post(runnable)
        }

        fun postDelayed(runnable: Runnable, delayMillis: Int) {
            handler.postDelayed(runnable, delayMillis.toLong())
        }

        fun showToast(content: String) {
            post(Runnable {
                if (toast == null) {
                    toast = Toast.makeText(context, content, Toast.LENGTH_SHORT)
                } else {
                    toast?.setText(content)
                }
                toast?.show()
            })

        }

        /**
         * 获取可用内存大小
         */
        fun getMemoryCacheSize(): Int {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val largeMemoryClass = activityManager.largeMemoryClass
            return largeMemoryClass * 1024 * 1024 / 8
        }

    }
}