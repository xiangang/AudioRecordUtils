package com.nxg.app.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.nxg.app.data.AudioRecordFile
import com.nxg.app.data.source.IAudioRecordFileDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRecordFileLocalDataSource @Inject constructor(
    private val audioRecordFileDao: AudioRecordFileDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    IAudioRecordFileDataSource {

    companion object {
        const val TAG = "AudioRecordLocalDataSource"
    }

    override fun observeAudioRecordFileList(): LiveData<Result<List<AudioRecordFile>>> {
        return audioRecordFileDao.observeAudioRecordFileList().map {
            Result.success(it)
        }
    }

    override suspend fun getAudioRecordFileList(): Result<List<AudioRecordFile>> =
        withContext(ioDispatcher) {
            return@withContext try {
                Result.success(audioRecordFileDao.getAudioRecordList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun observeAudioRecordFile(audioRecordFileId: String): LiveData<Result<AudioRecordFile>> {
        return audioRecordFileDao.observeAudioRecordFileById(audioRecordFileId).map {
            Result.success(it)
        }
    }

    override suspend fun getAudioRecordFile(audioRecordFileId: String): Result<AudioRecordFile> =
        withContext(ioDispatcher) {
            try {
                val audioRecordFile = audioRecordFileDao.getAudioRecordFileById(audioRecordFileId)
                if (audioRecordFile != null) {
                    return@withContext Result.success(audioRecordFile)
                } else {
                    return@withContext Result.failure(Exception("AudioRecordFile not found!"))
                }
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    override suspend fun getAudioRecordFileByFilePath(filePath: String): Result<AudioRecordFile> =
        withContext(ioDispatcher)
        {
            try {
                val audioRecordFile = audioRecordFileDao.getAudioRecordFileByFilePath(filePath)
                if (audioRecordFile != null) {
                    return@withContext Result.success(audioRecordFile)
                } else {
                    return@withContext Result.failure(Exception("AudioRecordFile not found!"))
                }
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    override suspend fun findAudioRecordFileListWithFileName(search: String): Result<List<AudioRecordFile>> =
        withContext(ioDispatcher)
        {
            try {
                val audioRecordFile = audioRecordFileDao.findAudioRecordFileListWithFileName(search)
                if (!audioRecordFile.isNullOrEmpty()) {
                    return@withContext Result.success(audioRecordFile)
                } else {
                    return@withContext Result.failure(Exception("AudioRecordFile not found for $search!"))
                }
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    override suspend fun insertAudioRecordFileList(audioRecordFileList: List<AudioRecordFile>) =
        withContext(ioDispatcher) {
            audioRecordFileDao.insertAudioRecordFileList(audioRecordFileList)
        }

    override suspend fun insertAudioRecordFile(audioRecordFile: AudioRecordFile): Long =
        withContext(ioDispatcher) {
            audioRecordFileDao.insertAudioRecordFile(audioRecordFile)
        }

    override suspend fun updateAudioRecordFile(audioRecordFile: AudioRecordFile): Int =
        withContext(ioDispatcher) {
            audioRecordFileDao.updateAudioRecordFile(audioRecordFile)
        }

    override suspend fun deleteAllAudioRecordFileList() =
        withContext(ioDispatcher) {
            audioRecordFileDao.deleteAudioRecordList()
        }

    override suspend fun deleteAudioRecordFile(audioRecordFileId: String) =
        withContext<Unit>(ioDispatcher) {
            audioRecordFileDao.deleteAudioRecordFileById(audioRecordFileId)
        }
}