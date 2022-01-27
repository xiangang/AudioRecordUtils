package com.nxg.app.data.source

import androidx.lifecycle.LiveData
import com.nxg.app.data.AudioRecordFile
import com.nxg.app.utils.getAllLocalWAVFileFromDir
import com.nxg.app.utils.getWAVFileDuration
import com.nxg.audiorecord.LogUtil
import kotlinx.coroutines.*

/**
 * Default implementation of [IAudioRecordFileRepository]. Single entry point for managing AudioRecordDoc' data.
 */
class AudioRecordFileRepository constructor(
    private val audioRecordLocalDataSource: IAudioRecordFileDataSource,
    private val audioRecordRemoteDataSource: IAudioRecordFileDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IAudioRecordFileRepository {

    companion object {
        const val TAG = "AudioRecordRepository"
    }

    /**
     * 从本地加载数据
     */
    override suspend fun loadDataFormLocalFile() {
        withContext(ioDispatcher) {
            val allFiles = getAllLocalWAVFileFromDir()
            allFiles?.let {
                val toSaveAudioRecordFileList = mutableListOf<AudioRecordFile>()
                for (file in allFiles) {
                    getAudioRecordByFilePath(file.absolutePath).let { result ->
                        //LogUtil.i(TAG, "getOrNull ${result.getOrNull()}")
                        if (result.isFailure) {
                            toSaveAudioRecordFileList.add(
                                AudioRecordFile(
                                    0,
                                    file.lastModified(),
                                    getWAVFileDuration(file.absolutePath),
                                    file.name,
                                    file.absolutePath
                                )
                            )
                        }
                    }
                }
                insertAudioRecordFileList(toSaveAudioRecordFileList)
            }
        }

    }

    override fun observeAudioRecordFileList(): LiveData<Result<List<AudioRecordFile>>> {
        return audioRecordLocalDataSource.observeAudioRecordFileList()
    }

    override suspend fun refreshAudioRecordFileList() {
        loadDataFormLocalFile()
    }

    override suspend fun getAudioRecordFileList(forceUpdate: Boolean): Result<List<AudioRecordFile>> {
        //TODO 支持云端同步
        /*if (forceUpdate) {
            try {
                //updateAudioRecordFromRemoteDataSource()
            } catch (ex: Exception) {
                LogUtil.e(TAG, "Exception:{${ex.message}}")
            }
        }*/
        return audioRecordLocalDataSource.getAudioRecordFileList()
    }

    override fun observeAudioRecordFile(AudioRecordFileId: String): LiveData<Result<AudioRecordFile>> {
        return audioRecordLocalDataSource.observeAudioRecordFile(AudioRecordFileId)
    }

    override suspend fun getAudioRecordFile(
        AudioRecordFileId: String,
        forceUpdate: Boolean
    ): Result<AudioRecordFile> {
        //TODO 支持云端同步
        /*if (forceUpdate) {
            //updateAudioRecordFromRemoteDataSource(taskId)
        }*/
        return audioRecordLocalDataSource.getAudioRecordFile(AudioRecordFileId)
    }

    override suspend fun getAudioRecordByFilePath(
        filePath: String,
        forceUpdate: Boolean
    ): Result<AudioRecordFile> {
        //TODO 支持云端同步
        /*if (forceUpdate) {
            //updateAudioRecordFromRemoteDataSource(taskId)
        }*/
        return audioRecordLocalDataSource.getAudioRecordFileByFilePath(filePath)
    }

    override suspend fun findAudioRecordFileListWithFileName(search: String): Result<List<AudioRecordFile>> {
        return audioRecordLocalDataSource.findAudioRecordFileListWithFileName(search)
    }

    override suspend fun insertAudioRecordFileList(audioRecordList: List<AudioRecordFile>) {
        audioRecordLocalDataSource.insertAudioRecordFileList(audioRecordList)
    }

    override suspend fun insertAudioRecordFile(audioRecordFile: AudioRecordFile): Long {
        return audioRecordLocalDataSource.insertAudioRecordFile(audioRecordFile)
    }

    override suspend fun updateAudioRecordFile(audioRecordFile: AudioRecordFile): Int {
        return audioRecordLocalDataSource.updateAudioRecordFile(audioRecordFile)
    }

    override suspend fun deleteAllAudioRecordFileList() {
        coroutineScope {
            launch { audioRecordLocalDataSource.deleteAllAudioRecordFileList() }
            //TODO 支持云端同步
            //launch { audioRecordRemoteDataSource.deleteAllAudioRecordFileList() }
        }

    }

    override suspend fun deleteAudioRecordFile(AudioRecordFileId: String) {
        coroutineScope {
            launch { audioRecordLocalDataSource.deleteAudioRecordFile(AudioRecordFileId) }
            //TODO 支持云端同步
            //launch { audioRecordRemoteDataSource.deleteAudioRecordFile(AudioRecordFileId) }
        }
    }
}