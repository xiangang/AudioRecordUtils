package com.nxg.app.audiorecord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.nxg.app.AppShareViewModel
import com.nxg.app.R
import com.nxg.app.databinding.AudioRecordFragmentBinding
import com.nxg.audiorecord.AudioRecordHandler
import com.nxg.audiorecord.AudioRecordingCallback
import com.nxg.audiorecord.LogUtil
import com.nxg.mvvm.applicationViewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 录音界面，录音结束返回时，要传参：录音文件的路径
 */
@AndroidEntryPoint
class AudioRecordFragment : Fragment() {

    companion object {
        const val TAG = "AudioRecordFragment"
        fun newInstance() = AudioRecordFragment()
    }

    private val appShareViewModel: AppShareViewModel by applicationViewModels()

    private val audioRecordViewModel: AudioRecordViewModel by viewModels()

    @Inject
    lateinit var audioRecordHandler: AudioRecordHandler

    private var _binding: AudioRecordFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AudioRecordFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navController = findNavController()
        val waveView = binding.waveView
        LogUtil.i(TAG, "audioRecordHandler $audioRecordHandler")
        LogUtil.i(TAG, "audioRecordViewModel $audioRecordViewModel")
        audioRecordHandler.start()
        audioRecordHandler.setAudioRecordingCallback(object : AudioRecordingCallback {
            override fun onRecording(audioData: ByteArray?, length: Int, volume: Int) {
                lifecycleScope.launch {
                    if (!audioRecordHandler.isPause) {
                        waveView.putValue(volume)
                    }
                }
            }

            override fun onStop(pcmFilePath: String?, wavFilePath: String?) {
                lifecycleScope.launch {
                    appShareViewModel.setUiState(AppShareViewModel.UiState.HOME)
                    navController.popBackStack()
                }
            }

            override fun onCancel() {
                lifecycleScope.launch {
                    appShareViewModel.setUiState(AppShareViewModel.UiState.HOME)
                    navController.popBackStack()
                }
            }
        })
        binding.buttonPause.setOnClickListener {
            if (audioRecordHandler.isPause) {
                audioRecordHandler.resume()
                audioRecordViewModel.resume()
                binding.buttonPause.setBackgroundResource(R.mipmap.ic_pause)
            } else {
                audioRecordHandler.pause()
                audioRecordViewModel.pause()
                binding.buttonPause.setBackgroundResource(R.mipmap.ic_play)
            }
        }
        binding.buttonCancel.setOnClickListener {
            audioRecordHandler.cancel()
        }

        binding.buttonComplete.setOnClickListener {
            audioRecordHandler.stop()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                audioRecordViewModel.tickFlow.collect {
                    LogUtil.i(TAG, "time $it")
                    binding.textTime.text = it
                }
            }
        }
        audioRecordViewModel.start()
        return root
    }

}