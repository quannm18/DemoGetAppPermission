package com.example.demogetapppermission.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.demogetapppermission.model.JunkClean
import com.example.demogetapppermission.repository.AppPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class AppViewModel(application: Application) : AndroidViewModel(application) {

    var listApp: Flow<PagingData<JunkClean>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { AppPagingSource.newInstance(getApplication()) }
    ).flow.cachedIn(viewModelScope)


    suspend fun listPermission(packageName: String): Flow<MutableList<Pair<String, Boolean>>> = flow {
        val listPerm = mutableListOf<Pair<String, Boolean>>()
        withContext(Dispatchers.IO) {
            val pm = getApplication<Application>().packageManager
            val listPackageInfo = pm.getInstalledPackages(0)
            Log.e("TAG::", "packageName: $packageName")
            Log.e("TAG::", "listPackageInfo: $listPackageInfo")

            listPackageInfo.forEach {
                Log.e("TAG:::", "---------------------------------------------")
                Log.e("TAG:::", "packageName: ${it.packageName}")
                Log.e("TAG:::", "listPermission: ${it.requestedPermissions}")
                Log.e("TAG:::", "---------------------------------------------\n")
            }

//            val packageInfo = listPackageInfo.firstOrNull { it.packageName.contains(packageName)  }
//            Log.e("TAG::", "listPermission: ${packageInfo?.requestedPermissions != null}")
//            if (packageInfo?.requestedPermissions != null) {
//                for (permission in packageInfo.requestedPermissions) {
//                    listPerm.add(Pair(permission, pm.checkPermission(permission, packageInfo.packageName) == PackageManager.PERMISSION_GRANTED))
//                }
//            }
        }
        emit(listPerm)
    }
}