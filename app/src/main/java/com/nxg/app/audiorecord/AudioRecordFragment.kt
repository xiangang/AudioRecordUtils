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
import com.nxg.app.WAV
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

    @Inject
    lateinit var audioRecordHandlerLifecycleObserver: AudioRecordHandlerLifecycleObserver

    private var _binding: AudioRecordFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AudioRecordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val waveView = binding.waveView
        LogUtil.i(TAG, "audioRecordHandler $audioRecordHandler")
        LogUtil.i(TAG, "audioRecordViewModel $audioRecordViewModel")
        LogUtil.i(TAG, "audioRecordHandlerLifecycleObserver $audioRecordHandlerLifecycleObserver")
        lifecycle.addObserver(audioRecordHandlerLifecycleObserver)
        audioRecordHandler.setAudioRecordingCallback(object : AudioRecordingCallback {
            override fun onStart(pcmFilePath: String?, wavFilePath: String?) {
                wavFilePath?.let {
                    audioRecordViewModel.createAudioRecordFile(
                        System.currentTimeMillis(),
                        it.substring(0, it.indexOf(WAV)),
                        it
                    )
                }
            }

            override fun onRecording(audioData: ByteArray?, length: Int, volume: Int) {
                lifecycleScope.launch {
                    if (!audioRecordHandler.isPause) {
                        waveView.putValue(volume)
                    }
                }
            }

            override fun onStop(pcmFilePath: String?, wavFilePath: String?) {
                lifecycleScope.launch {
                    //TODO show dialog
                    wavFilePath?.let {
                        audioRecordViewModel.updateAudioRecordFile(
                            it
                        )
                    }
                    //TODO dismiss dialog
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
                    binding.textTime.text = it
                }
            }
        }
        audioRecordViewModel.start()
    }

}