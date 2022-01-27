package com.nxg.app.audiorecordlist

import android.media.AudioTrack
import androidx.lifecycle.*
import com.blankj.utilcode.util.TimeUtils
import com.nxg.app.R
import com.nxg.app.audiorecordlist.AudioRecordListFragment.Companion.TAG
import com.nxg.app.data.AudioRecordFile
import com.nxg.app.data.source.IAudioRecordFileRepository
import com.nxg.audiorecord.AudioTrackHandler
import com.nxg.audiorecord.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioRecordListViewModel @Inject constructor(
    private val audioRecordFileRepository: IAudioRecordFileRepository,
    private val audioTrackHandler: AudioTrackHandler
) :
    ViewModel() {

    private val _forceUpdate = MutableLiveData(false)

    private val _items: LiveData<List<AudioRecordFile>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                audioRecordFileRepository.refreshAudioRecordFileList()
                _dataLoading.value = false
            }
        }
        LogUtil.i(TAG, "observeAudioRecordFileList ")
        audioRecordFileRepository.observeAudioRecordFileList().distinctUntilChanged()
            .switchMap { handleAudioRecordFileList(it) }
    }
    val items: LiveData<List<AudioRecordFile>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    /**
     * 用于更新指定Position的item
     */
    private val _notifyItemPosition = MutableLiveData<Int>()
    val notifyItemPosition: LiveData<Int> = _notifyItemPosition

    /**
     * 当前播放的音频文件对象
     */
    private var currentPlayAudioRecordFile: AudioRecordFile? = null

    private fun loadTasks(forceUpdate: Boolean) {
        _forceUpdate.value = forceUpdate
    }

    fun refresh() {
        loadTasks(true)
    }

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    private fun notifyItemChanged(position: Int) {
        _notifyItemPosition.value = position
    }

    private val _noTaskIconRes = MutableLiveData<Int>().apply {
        value = R.mipmap.ic_file_no_data
    }
    val noTaskIconRes: LiveData<Int> = _noTaskIconRes

    private val _noTasksLabel = MutableLiveData<Int>().apply {
        value = R.string.no_data_image_content_description
    }
    val noTasksLabel: LiveData<Int> = _noTasksLabel

    private fun handleAudioRecordFileList(audioRecordFileListResult: Result<List<AudioRecordFile>>): LiveData<List<AudioRecordFile>> {
        val result = MutableLiveData<List<AudioRecordFile>>()
        if (audioRecordFileListResult.isSuccess) {
            viewModelScope.launch {
                result.value = audioRecordFileListResult.getOrDefault(emptyList())
            }
            LogUtil.i(TAG, "handleAudioRecordFileList size ${result.value?.size}")
        } else {
            result.value = emptyList()
        }
        return result
    }

    init {
        //只有第一次才会读取本地文件
        loadTasks(true)
        audioTrackHandler.prepare()
    }

    /**
     * 录音详情
     */
    fun openAudioRecord(audioRecordFile: AudioRecordFile) {

    }


    /**
     * 播放录音
     */
    fun playAudioRecord(position: Int, audioRecordFile: AudioRecordFile) {
        LogUtil.i(TAG, "playAudioRecord click _items size  ${_items.value?.size}")
        LogUtil.i(TAG, "playAudioRecord click audioRecordFile $audioRecordFile")
        LogUtil.i(TAG, "playAudioRecord current audioRecordFile $currentPlayAudioRecordFile")
        //播放的不是当前的则停止播放当前
        currentPlayAudioRecordFile?.let {
            if (currentPlayAudioRecordFile != audioRecordFile) {
                LogUtil.i(TAG, "playAudioRecord stop play currentPlayAudioRecordFile}")
                audioTrackHandler.pause()
                audioTrackHandler.flush()
                it.pause()
                _items.value?.indexOf(it)?.let { it1 -> notifyItemChanged(it1) }
            }
        }
        currentPlayAudioRecordFile = audioRecordFile
        //播放当前的
        if (!audioRecordFile.play) {
            audioTrackHandler.startPlayRecordFile(
                audioRecordFile.filePath,
                onPlaybackPositionUpdateListener
            )
            audioTrackHandler.play()
            audioRecordFile.play()
        } else {
            //暂停当前的
            audioTrackHandler.pause()
            audioRecordFile.pause()
        }
        //更新界面
        LogUtil.i(TAG, "playAudioRecord position $position")
        audioRecordFile.let {
            _items.value?.indexOf(it)?.let { it1 -> notifyItemChanged(it1) }
        }
        notifyItemChanged(position)
    }

    private val onPlaybackPositionUpdateListener =
        object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(track: AudioTrack?) {
                LogUtil.i(TAG, "onMarkerReached")
                currentPlayAudioRecordFile?.let {
                    it.pause()
                    _items.value?.indexOf(it)?.let { it1 -> notifyItemChanged(it1) }
                }
                currentPlayAudioRecordFile = null
                audioTrackHandler.stop()
                audioTrackHandler.flush()
            }

            override fun onPeriodicNotification(track: AudioTrack?) {
                LogUtil.i(TAG, "onPeriodicNotification")
            }
        }

    /**
     * 录音文件时间
     */
    fun getAudioRecordCreateTime(time: Long): String {

        return TimeUtils.millis2String(time)

    }

}