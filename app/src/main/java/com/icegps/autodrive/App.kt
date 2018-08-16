package com.icegps.autodrive

import android.app.Application
import com.icegps.autodrive.data.WorkWidth
import com.icegps.autodrive.gen.GreenDaoUtils
import com.icegps.autodrive.utils.Init
import com.icegps.jblelib.ble.BleHelper
import com.tencent.bugly.crashreport.CrashReport
import timber.log.Timber

/**
 * Created by 111 on 2018/4/27.
 */
class App : Application() {
    private var serviceUUID = "0000f1f0-0000-1000-8000-00805f9b34fb"
    private var writeUUID = "0000f1f1-0000-1000-8000-00805f9b34fb"
    private var notifyUUID = "0000f1f2-0000-1000-8000-00805f9b34fb"
    override fun onCreate() {
        super.onCreate()
        Init.init(this)
        BleHelper.init(this, serviceUUID, notifyUUID, writeUUID, false)
        Timber.plant(Timber.DebugTree())
        CrashReport.initCrashReport(this, "cdf90effad", false);
        GreenDaoUtils.initGrrenDaoUtils(this)
        var loadAll = GreenDaoUtils.daoSession.workWidthDao.loadAll()
        if (loadAll == null || loadAll.size == 0) {
            loadAll = ArrayList()
            loadAll.add(WorkWidth(3f, 0f, 0f, "播种", 0))
            loadAll.add(WorkWidth(3f, 0f, 0f, "打药", 1))
            loadAll.add(WorkWidth(3f, 0f, 0f, "施肥", 2))
            loadAll.add(WorkWidth(3f, 0f, 0f, "开沟", 3))
            loadAll.add(WorkWidth(3f, 0f, 0f, "犁地", 4))
            GreenDaoUtils.daoSession.workWidthDao.insertOrReplaceInTx(loadAll)
        }
    }


}