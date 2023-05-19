package com.example.demogetapppermission

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demogetapppermission.databinding.ActivityMainBinding
import com.example.demogetapppermission.model.JunkClean
import com.example.demogetapppermission.view.dapter.ListAppAdapter
import com.example.demogetapppermission.view.dapter.PermissionAdapter
import com.example.demogetapppermission.viewmodel.AppViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private val listAppAdapter: ListAppAdapter by lazy {
        ListAppAdapter{
            goToDetail(it)
        }
    }

    private val appViewModel : AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestPermissionUsageStats(this)
        lifecycleScope.launchWhenResumed {
            appViewModel.listApp.collectLatest {
                listAppAdapter.submitData(it)
            }

            listAppAdapter.addLoadStateListener {
                if (it.refresh is LoadState.NotLoading){
                    if (listAppAdapter.itemCount > 0){
                        binding.progressBar.visibility = View.GONE
                    }else{
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = listAppAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }


    fun requestPermissionUsageStats(activity: Activity) {
        val appOps = activity.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                activity.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                activity.packageName
            )
        }
        if (mode != AppOpsManager.MODE_ALLOWED) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivityForResult(intent, REQUEST_CODE_OVERLAY_GET_USAGE_STATS)
        }
    }

    companion object{

        const val REQUEST_CODE_OVERLAY_GET_USAGE_STATS = 21
    }

    private fun goToDetail(junkClean: JunkClean) {
        DetailPermActivity.newInstance(this, junkClean)
    }
}