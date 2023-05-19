package com.example.demogetapppermission

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demogetapppermission.databinding.ActivityDetailPermBinding
import com.example.demogetapppermission.model.JunkClean
import com.example.demogetapppermission.view.dapter.PermissionAdapter
import com.example.demogetapppermission.viewmodel.AppViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailPermActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailPermBinding
    private val appViewModel : AppViewModel by viewModels()

    companion object{
        fun newInstance(context: Context, junkClean: JunkClean) {
            context.startActivity(Intent(context, DetailPermActivity::class.java).apply {
                putExtra("junkClean", junkClean )
            })
        }
    }

    private val permissionAdapter: PermissionAdapter by lazy {
        PermissionAdapter{

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPermBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.apply {
            adapter = permissionAdapter
            layoutManager = LinearLayoutManager(this@DetailPermActivity)
        }

        lifecycleScope.launch {
            appViewModel.listPermission((intent.getSerializableExtra("junkClean") as JunkClean).packageName).collectLatest {
                permissionAdapter.submitList(it)
            }
        }
    }
}