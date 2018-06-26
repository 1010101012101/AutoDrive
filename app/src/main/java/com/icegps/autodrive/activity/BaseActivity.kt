package com.icegps.autodrive.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by 111 on 2018/4/27.
 */
open abstract class BaseActivity: AppCompatActivity() {

    lateinit var activity:Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity=this
        setContentView(layout())
        init()
        setListener()

    }

    abstract fun layout():Int
    abstract fun init()
    abstract fun setListener()
}