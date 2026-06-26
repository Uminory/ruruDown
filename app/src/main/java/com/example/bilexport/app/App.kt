package com.example.bilexport.app

import android.app.Application
import com.example.bilexport.privileged.shizuku.ShizukuManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Shizuku 初始化已在 MainViewModel 中完成
        // 此处保留为未来扩展点
    }
}