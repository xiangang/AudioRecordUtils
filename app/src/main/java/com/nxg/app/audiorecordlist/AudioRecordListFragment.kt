package com.nxg.app.audiorecordlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.nxg.app.R
import com.nxg.app.AppShareViewModel
import com.nxg.app.audiorecord.AudioRecordFragment
import com.nxg.app.databinding.AudioRecordListFragmentBinding
import com.nxg.app.utils.setupRefreshLayout
import com.nxg.audiorecord.AudioRecordHandler
import com.nxg.audiorecord.LogUtil
import com.nxg.mvvm.applicationViewModels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioRecordListFragment : Fragment() {

    companion object {
        const val TAG = "MainFragment"
        fun newInstance() = AudioRecordListFragment()
    }

    private val args: AudioRecordListFragmentArgs by navArgs()

    private val appShareViewModel: AppShareViewModel by applicationViewModels()

    private val audioRecordListViewModel: AudioRecordListViewModel by viewModels()

    private lateinit var viewDataBinding: AudioRecordListFragmentBinding

    private lateinit var listAdapter: AudioRecordListAdapter

    @Inject
    lateinit var audioRecordHandler: AudioRecordHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = AudioRecordListFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = audioRecordListViewModel
        }
        LogUtil.i(AudioRecordFragment.TAG, "audioRecordHandler $audioRecordHandler")
        audioRecordHandler.stop()
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupListAdapter()
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.dataList)
        audioRecordListViewModel.refresh()
        audioRecordListViewModel.notifyItemPosition.observe(viewLifecycleOwner, {
            notifyItemChanged(it)
        })
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = AudioRecordListAdapter(viewModel)
            viewDataBinding.dataList.adapter = listAdapter
        } else {
            LogUtil.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun notifyItemChanged(position: Int) {
        LogUtil.w(TAG, "notifyItemChanged: position $position.")
        listAdapter.notifyItemChanged(position)
    }


}