package com.nxg.app.data.source.remote

import android.content.Context
import androidx.lifecycle.LiveData
import com.nxg.app.data.AudioRecordFile
import com.nxg.app.data.source.IAudioRecordFileDataSource
import javax.inject.Inject

class AudioRecordFileRemoteDataSource @Inject constructor(context: Context) :
    IAudioRecordFileDataSource {

    override fun observeAudioRecordFileList(): LiveData<Result<List<AudioRecordFile>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAudioRecordFileList(): Result<List<AudioRecordFile>> {
        TODO("Not yet implemented")
    }

    override fun observeAudioRecordFile(audioRecordFileId: String): LiveData<Result<AudioRecordFile>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAudioRecordFile(audioRecordFileId: String): Result<AudioRecordFile> {
        TODO("Not yet implemented")
    }

    override suspend fun getAudioRecordFileByFilePath(filePath: String): Result<AudioRecordFile> {
        TODO("Not yet implemented")
    }

    override suspend fun findAudioRecordFileListWithFileName(search: String): Result<List<AudioRecordFile>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAudioRecordFileList(audioRecordFileList: List<AudioRecordFile>) {
        TODO("Not yet implemented")
    }

    override suspend fun saveAudioRecordFile(audioRecordFile: AudioRecordFile) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllAudioRecordFileList() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAudioRecordFile(audioRecordFileId: String) {
        TODO("Not yet implemented")
    }
}