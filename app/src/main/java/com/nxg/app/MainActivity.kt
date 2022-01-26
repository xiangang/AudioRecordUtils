package com.nxg.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.nxg.app.databinding.MainActivityBinding
import com.nxg.audiorecord.AudioRecordHandler
import com.nxg.audiorecord.LogUtil
import com.nxg.mvvm.applicationViewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG ="MainActivity"
    }

    private lateinit var binding: MainActivityBinding

    private val appShareViewModel: AppShareViewModel by applicationViewModels()

    @Inject
    lateinit var audioRecordHandler: AudioRecordHandler

    @SuppressLint("RestrictedApi", "ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = getString(R.string.app_name)

        LogUtil.i(TAG,"audioRecordHandler $audioRecordHandler")

        /**
         * 使用 FragmentContainerView 创建 NavHostFragment，或通过 FragmentTransaction 手动将 NavHostFragment 添加到您的 activity 时，
         * 尝试通过 Navigation.findNavController(Activity, @IdRes int) 检索 activity 的 onCreate() 中的 NavController 将失败。
         * 您应改为直接从 NavHostFragment 检索 NavController。
         */
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        binding.buttonRecord.setOnClickListener {
            appShareViewModel.setUiState(AppShareViewModel.UiState.START)
            navController.navigate(R.id.action_mainFragment_to_audioRecordFragment)
        }

        // Start a coroutine in the lifecycle scope
        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                // Note that this happens when lifecycle is STARTED and stops
                // collecting when the lifecycle is STOPPED
                appShareViewModel.uiState.collect {
                    // New value received
                    when (it) {
                        AppShareViewModel.UiState.PERMISSION -> {
                            binding.toolbar.navigationIcon = null
                            binding.buttonRecord.visibility = View.GONE
                            binding.appBar.setExpanded(false)
                            //binding.appBar.isNestedScrollingEnabled = false
                        }
                        AppShareViewModel.UiState.HOME -> {
                            binding.appBar.setExpanded(true)
                            binding.appBar.isNestedScrollingEnabled = true
                            binding.buttonRecord.visibility = View.VISIBLE
                            //binding.toolbarLayout.isTitleEnabled = true
                            binding.nestedScrollView.isNestedScrollingEnabled = true
                            binding.nestedScrollView.scrollable = true
                            //binding.toolbar.setCollapsible(true)
                            delay(200)
                            binding.toolbar.navigationIcon = null
                        }
                        AppShareViewModel.UiState.START -> {
                            binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24_white)
                            binding.appBar.setExpanded(false)
                            binding.appBar.isNestedScrollingEnabled = false
                            binding.buttonRecord.visibility = View.GONE
                            //binding.toolbar.setCollapsible(false)
                            //binding.toolbarLayout.isTitleEnabled = false
                            binding.nestedScrollView.isNestedScrollingEnabled = false
                            binding.nestedScrollView.scrollable = false
                            binding.toolbar.setNavigationOnClickListener {
                                appShareViewModel.setUiState(AppShareViewModel.UiState.HOME)
                                navController.popBackStack()
                            }
                        }
                        AppShareViewModel.UiState.PAUSE -> {
                            binding.appBar.setExpanded(false)
                        }
                        AppShareViewModel.UiState.PLAY -> {
                            binding.appBar.setExpanded(false)
                        }

                    }
                }
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}