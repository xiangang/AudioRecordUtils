package com.nxg.app.data.source

import androidx.lifecycle.LiveData
import com.nxg.app.data.AudioRecordFile

interface IAudioRecordFileRepository {

    suspend fun loadDataFormLocalFile()

    fun observeAudioRecordFileList(): LiveData<Result<List<AudioRecordFile>>>

    suspend fun getAudioRecordFileList(forceUpdate: Boolean = false): Result<List<AudioRecordFile>>

    suspend fun refreshAudioRecordFileList()

    fun observeAudioRecordFile(AudioRecordFileId: String): LiveData<Result<AudioRecordFile>>

    suspend fun getAudioRecordFile(
        AudioRecordFileId: String,
        forceUpdate: Boolean = false
    ): Result<AudioRecordFile>

    suspend fun getAudioRecordByFilePath(
        filePath: String,
        forceUpdate: Boolean = false
    ): Result<AudioRecordFile>

    suspend fun findAudioRecordFileListWithFileName(search: String): Result<List<AudioRecordFile>>

    suspend fun insertAudioRecordFile(audioRecordFile: AudioRecordFile): Long

    suspend fun updateAudioRecordFile(audioRecordFile: AudioRecordFile): Int

    suspend fun insertAudioRecordFileList(audioRecordList: List<AudioRecordFile>)

    suspend fun deleteAllAudioRecordFileList()

    suspend fun deleteAudioRecordFile(AudioRecordFileId: String)
}