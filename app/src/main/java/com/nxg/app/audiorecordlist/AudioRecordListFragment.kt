package com.nxg.app.audiorecordlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nxg.app.databinding.AudioRecordListFragmentBinding
import com.nxg.app.utils.setupRefreshLayout
import com.nxg.audiorecord.LogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AudioRecordListFragment : Fragment() {

    companion object {
        const val TAG = "AudioRecordList"
        fun newInstance() = AudioRecordListFragment()
    }

    private val audioRecordListViewModel: AudioRecordListViewModel by viewModels()

    private lateinit var viewDataBinding: AudioRecordListFragmentBinding

    private lateinit var listAdapter: AudioRecordListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LogUtil.w(TAG, "onCreateView")
        viewDataBinding = AudioRecordListFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = audioRecordListViewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupListAdapter()
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.dataList)
        audioRecordListViewModel.notifyItemPosition.observe(viewLifecycleOwner, {
            notifyItemChanged(it)
        })
        audioRecordListViewModel.refresh()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = AudioRecordListAdapter(viewModel)
            viewDataBinding.dataList.adapter = listAdapter
        } else {
            LogUtil.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }

        lifecycleScope.launch {
            delay(200)
            viewDataBinding.dataList.scrollToPosition(0)
            listAdapter.notifyItemRangeChanged(0, 10)
        }
    }

    private fun notifyItemChanged(position: Int) {
        LogUtil.w(TAG, "notifyItemChanged: position $position")
        listAdapter.notifyItemChanged(position)
    }


}