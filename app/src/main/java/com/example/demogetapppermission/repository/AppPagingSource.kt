package com.example.demogetapppermission.repository

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.demogetapppermission.model.JunkClean
import com.example.demogetapppermission.utils.AppManagerUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File


class AppPagingSource constructor(
    val context: Context
) : PagingSource<Int, JunkClean>() {

    companion object {
        private var INSTANCE: AppPagingSource? = null
        fun newInstance(application: Application): AppPagingSource {
            if (INSTANCE == null) {
                INSTANCE = AppPagingSource(application)
            }
            return INSTANCE!!
        }
    }

    override fun getRefreshKey(state: PagingState<Int, JunkClean>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, JunkClean> {
        val position = params.key ?: -1
        return try {
            val images: MutableList<JunkClean> = mutableListOf()
            getAllApp(
                imagesOffset = position,
                imagesLimit = params.loadSize
            ).collect { imageDataList ->
                images.addAll(imageDataList)
            }

            LoadResult.Page(
                data = images,
                prevKey = if (position == -1) null else position,
                nextKey = if (images.isEmpty()) null else position + params.loadSize
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    val tempList = AppManagerUtils.getListInstalledApps(context = context.applicationContext, systemOnly = false, userOnly = true)

    private suspend fun getAllApp(
        imagesOffset: Int,
        imagesLimit: Int,
    ): Flow<List<JunkClean>> = flow {
        val mList: MutableList<JunkClean> = mutableListOf()
        withContext(Dispatchers.IO) {

            tempList.forEach { packageInfo ->
                Log.e("TAG:::", "getAllApp: $packageInfo")

                var nameApp = ""
                context.packageManager.let { pm ->
                    try {
                        val res = pm.getResourcesForApplication(packageInfo.applicationInfo)
                        val resID = packageInfo.applicationInfo.labelRes
                        if (resID != 0) {
                            nameApp = res.getString(resID)
                        }
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }
                }

                val cache = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    AppManagerUtils.getStorageStats(context.applicationContext, packageInfo.packageName)?.cacheBytes ?: 0
                } else {
                    AppManagerUtils.createApplicationInfo(context.packageManager, packageInfo.packageName).let {
                        if (it != null) {
                            File(it.publicSourceDir).length()
                        } else {
                            0L
                        }
                    }
                }
                if (cache > 0L && nameApp.isNotEmpty()) {
                    mList.add(JunkClean(name = nameApp, size = cache, packageName = packageInfo.packageName))
                }
            }
            if (mList.size > 0) {
                mList.add(0, JunkClean(isRoot = true, size = mList.sumOf { it.size }, totalItem = mList.size))
            }

            Log.d("getAllApp", "${mList.size}")
            mList
        }
        emit(mList)
    }

}