package com.icegps.autodrive

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import com.icegps.autodrive.ble.OnlyBle
import com.icegps.autodrive.gen.GreenDaoUtils
import com.icegps.autodrive.utils.Init
import com.tencent.bugly.crashreport.CrashReport
import j.m.jblelib.ble.BleHelper
import j.m.jblelib.ble.BleHelper.context
import timber.log.Timber

/**
 * Created by 111 on 2018/4/27.
 */
class App : Application() {
    var serviceUUID = "0000f1f0-0000-1000-8000-00805f9b34fb"
    var writeUUID = "0000f1f1-0000-1000-8000-00805f9b34fb"
    var notifyUUID = "0000f1f2-0000-1000-8000-00805f9b34fb"

    override fun onCreate() {
        super.onCreate()
        Init.init(this)
        Timber.plant(Timber.DebugTree())
        BleHelper.init(this, serviceUUID, notifyUUID, writeUUID, false)
        CrashReport.initCrashReport(this, "cdf90effad", false);
        GreenDaoUtils.initGrrenDaoUtils(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        OnlyBle.unregister()
    }




}