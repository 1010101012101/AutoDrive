package com.icegps.autodrive.map2.threadpool

import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

open class ThreadPool : ThreadPoolExecutor {

    companion object {
        private var NUMCORES = Runtime.getRuntime().availableProcessors()
        private var COR_OPPL_SIZE = NUMCORES shr 1
        private var MAXIMUM_POOL_SIZE = NUMCORES
        private var KEEP_ALIVE_TIME = 1L
        private var KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
        
    }

    constructor() : super(COR_OPPL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, LinkedBlockingDeque<Runnable>())

    /**
     * 拿到view实例,通过偏移值来确定当前需要加载的图片
     */

}