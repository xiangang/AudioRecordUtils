package com.nxg.app.data.source

import androidx.annotation.NonNull
import androidx.appsearch.app.SearchResults
import androidx.lifecycle.LiveData
import com.google.common.util.concurrent.FutureCallback
import com.nxg.app.data.AudioRecordDoc
import com.nxg.app.data.AudioRecordFile

/**
 * Main entry point for accessing audio record data.
 */
interface IAudioRecordDocDataSource {

    suspend fun createAudioRecordDoc(a: AudioRecordDoc)

    suspend fun updateAudioRecordDoc(a: AudioRecordDoc)

    suspend fun readAudioRecordDoc(
        @NonNull queryExpression: String,
        futureCallback: FutureCallback<SearchResults>
    )

    suspend fun deleteAudioRecordDoc(ids: Collection<String>)
}

/**
 * Main entry point for accessing AudioRecordFile data.
 */
interface IAudioRecordFileDataSource {

    fun observeAudioRecordFileList(): LiveData<Result<List<AudioRecordFile>>>

    suspend fun getAudioRecordFileList(): Result<List<AudioRecordFile>>

    fun observeAudioRecordFile(audioRecordFileId: String): LiveData<Result<AudioRecordFile>>

    suspend fun getAudioRecordFile(audioRecordFileId: String): Result<AudioRecordFile>

    suspend fun getAudioRecordFileByFilePath(filePath: String): Result<AudioRecordFile>

    suspend fun findAudioRecordFileListWithFileName(search: String): Result<List<AudioRecordFile>>

    suspend fun insertAudioRecordFile(audioRecordFile: AudioRecordFile): Long

    suspend fun updateAudioRecordFile(audioRecordFile: AudioRecordFile): Int

    suspend fun insertAudioRecordFileList(audioRecordFileList: List<AudioRecordFile>)

    suspend fun deleteAllAudioRecordFileList()

    suspend fun deleteAudioRecordFile(audioRecordFileId: String)
}
