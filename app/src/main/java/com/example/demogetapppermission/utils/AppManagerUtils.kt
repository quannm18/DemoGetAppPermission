package com.example.demogetapppermission.utils

import android.app.usage.StorageStats
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.storage.StorageManager
import android.util.Log

object AppManagerUtils {
    fun getListInstalledApps(
        context: Context,
        systemOnly: Boolean,
        userOnly: Boolean
    ): ArrayList<PackageInfo> {
        Log.e("TAG:::", "getListInstalledApps: ", )
        val list = context.packageManager.getInstalledPackages(0)
        val pkgInfoList = ArrayList<PackageInfo>()
        for (i in list.indices) {
            val packageInfo = list[i]
            val flags = packageInfo!!.applicationInfo.flags
            val isSystemApp = (flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val isUpdatedSystemApp = (flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
            val addPkg = (systemOnly && (isSystemApp and !isUpdatedSystemApp)) or
                    (userOnly && (!isSystemApp or isUpdatedSystemApp)) and (packageInfo.packageName != context.packageName) and
                    (packageInfo.packageName != "com.google.chrome") and (packageInfo.packageName != "com.android.vending") and
                    (!packageInfo.packageName.contains("com.google.android.")
                            and (!packageInfo.packageName.contains("com.android.smoketest", true))
                            and (!packageInfo.packageName.contains("com.example.android", true))
                            and (!packageInfo.packageName.contains(
                        "com.android.gesture.builder",
                        true
                    )))
            if (addPkg)
                pkgInfoList.add(packageInfo)
        }
        return pkgInfoList
    }

    fun getStorageStats(context: Context, packageName: String): StorageStats? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return null

        try {
            val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            return storageStatsManager.queryStatsForPackage(
                StorageManager.UUID_DEFAULT, packageName, android.os.Process.myUserHandle()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun createApplicationInfo(packageManager: PackageManager, packageName: String): ApplicationInfo? {
        return try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (ex: PackageManager.NameNotFoundException) {
            null
        }
    }
}