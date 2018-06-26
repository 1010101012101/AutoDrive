package j.m.jblelib.ble.threadpool

import j.m.jblelib.ble.BleHelper.init
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by 111 on 2018/4/26.
 */
object ThreadPool {
    private var cacheThreadPool: ExecutorService
    private var singleThreadPool: ExecutorService

    init {
        cacheThreadPool = Executors.newCachedThreadPool()
        singleThreadPool = Executors.newSingleThreadExecutor()
    }

    fun excute(runnable: Runnable) {
        cacheThreadPool.execute(runnable)
    }
    fun excuteSingle(runnable: Runnable){
        singleThreadPool.execute(runnable)
    }

}