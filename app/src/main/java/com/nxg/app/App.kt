package com.nxg.app

import com.blankj.utilcode.util.Utils
import com.nxg.audiorecord.LogUtil
import com.nxg.mvvm.BaseViewModelApplication
import dagger.hilt.android.HiltAndroidApp
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@HiltAndroidApp
class App : BaseViewModelApplication() {

    companion object {
        const val TAG = "AppApplication"
        private var INSTANCE: App by NotNullSingleValueVar()
        fun instance() = INSTANCE
    }

    //定义一个属性管理类，进行非空和重复赋值的判断
    private class NotNullSingleValueVar<T> : ReadWriteProperty<Any?, T> {
        private var value: T? = null
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value ?: throw IllegalStateException("application not initialized")
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value = if (this.value == null) value
            else throw IllegalStateException("application already initialized")
        }
    }


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        Utils.init(this)
        LogUtil.enable = BuildConfig.DEBUG
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }
}