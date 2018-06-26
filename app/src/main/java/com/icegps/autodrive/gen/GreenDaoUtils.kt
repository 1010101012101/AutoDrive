package com.icegps.autodrive.gen

import android.content.Context
import android.database.sqlite.SQLiteDatabase


class GreenDaoUtils {
    companion object {
        private lateinit var devOpenHelper: DaoMaster.DevOpenHelper
        private lateinit var db: SQLiteDatabase
        private lateinit var daoMaster: DaoMaster
        lateinit var daoSession: DaoSession

        fun initGrrenDaoUtils(context: Context) {
            devOpenHelper = DaoMaster.DevOpenHelper(context, "autoDrive-db", null)
            db = devOpenHelper.getWritableDatabase()
            daoMaster = DaoMaster(db)
            daoSession = daoMaster.newSession()
        }
    }
}