package com.nxg.app.audiorecord


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.app.data.AudioRecordFile
import com.nxg.app.data.source.IAudioRecordFileRepository
import com.nxg.app.utils.getStringTime
import com.nxg.app.utils.getWAVFileDuration
import com.nxg.audiorecord.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
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
                    //LogUtil.i(AudioRecordFragment.TAG, "time $time")
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

    private var audioRecordFile: AudioRecordFile? = null

    /**
     * 录音开始，创建录音记录
     */
    fun createAudioRecordFile(
        createTime: Long = System.currentTimeMillis(),
        fileName: String,
        filePath: String
    ) {
        LogUtil.i(AudioRecordFragment.TAG, "createAudioRecordFile $filePath")
        //创建一个默认的录音文件，保存后会
        audioRecordFile = AudioRecordFile(
            0,
            createTime,
            "00:00:00",
            fileName,
            filePath
        )
        audioRecordFile?.let {
            viewModelScope.launch {
                audioRecordFileRepository.insertAudioRecordFile(it).apply {
                    it.id = this
                    LogUtil.i(AudioRecordFragment.TAG, "create audioRecordFile $audioRecordFile")
                }
            }
        }

    }

    /**
     * 录音结束
     */
    fun updateAudioRecordFile(
        filePath: String
    ) {
        LogUtil.i(AudioRecordFragment.TAG, "updateAudioRecordFile $filePath")
        audioRecordFile?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    it.duration = getWAVFileDuration(filePath)
                    audioRecordFileRepository.updateAudioRecordFile(it).apply {
                        LogUtil.i(
                            AudioRecordFragment.TAG,
                            "update audioRecordFile $audioRecordFile"
                        )
                    }
                }
            }
        }

    }
}