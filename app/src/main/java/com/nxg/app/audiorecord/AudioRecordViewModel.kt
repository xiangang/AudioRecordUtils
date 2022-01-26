package com.nxg.app.audiorecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.app.data.AudioRecordFile
import com.nxg.app.data.source.IAudioRecordFileRepository
import com.nxg.app.utils.getStringTime
import com.nxg.audiorecord.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AudioRecordViewModel @Inject constructor(private val audioRecordFileRepository: IAudioRecordFileRepository) :
    ViewModel() {

    // Backing property to avoid flow emissions from other classes
    private val _tickFlow = MutableSharedFlow<String>(replay = 0)
    val tickFlow: MutableSharedFlow<String> = _tickFlow
    private var tickJob: Job? = null
    private var time = 0L
    private var paused = false

    /**
     * 开始计时
     */
    fun start() {
        tickJob?.cancel()
        paused = false
        tickJob = viewModelScope.launch {
            while (true) {
                if (!paused) {
                    time += 1
                    LogUtil.i(AudioRecordFragment.TAG, "time $time")
                    _tickFlow.emit(getStringTime(time))
                }
                //1毫秒刷新一次
                delay(1)
            }
        }
    }

    /**
     * 继续计时
     */
    fun resume() {
        paused = false
    }

    /**
     * 暂停计时
     */
    fun pause() {
        paused = true
    }

    /**
     * 取消计时
     */
    fun stop() {
        tickJob?.cancel()
    }

    /**
     * 录音结束，创建录音记录
     */
    fun createAudioRecordFile(audioRecordFile: AudioRecordFile) =
        viewModelScope.launch {
            audioRecordFileRepository.saveAudioRecordFile(audioRecordFile)

        }
}