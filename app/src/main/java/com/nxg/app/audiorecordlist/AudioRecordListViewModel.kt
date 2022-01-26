package com.nxg.app.audiorecordlist

import androidx.lifecycle.*
import com.blankj.utilcode.util.TimeUtils
import com.nxg.app.R
import com.nxg.app.data.AudioRecordFile
import com.nxg.app.data.source.IAudioRecordFileRepository
import com.nxg.audiorecord.AudioTrackHandler
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
        audioRecordFileRepository.observeAudioRecordFileList().distinctUntilChanged()
            .switchMap { handleAudioRecordFileList(it) }
    }
    val items: LiveData<List<AudioRecordFile>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    fun loadTasks(forceUpdate: Boolean) {
        _forceUpdate.value = forceUpdate
    }

    fun refresh() {
        loadTasks(true)
    }

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
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
        } else {
            result.value = emptyList()
        }
        return result
    }

    init {
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
    fun playAudioRecord(audioRecordFile: AudioRecordFile) {
        audioRecordFile.play = !audioRecordFile.play
    }

    /**
     * 录音文件时间
     */
    fun getAudioRecordCreateTime(time: Long): String {

        return TimeUtils.millis2String(time)

    }

}