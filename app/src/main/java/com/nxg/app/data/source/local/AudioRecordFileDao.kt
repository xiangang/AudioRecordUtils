package com.nxg.app.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nxg.app.data.AudioRecordFile

/**
 * Data Access Object for the AudioRecordFile table.
 */
@Dao
interface AudioRecordFileDao {

    /**
     * Observes list of AudioRecordFile.
     *
     * @return all AudioRecordFile.
     */
    @Query("SELECT * FROM AudioRecordFile order by createTime DESC ")
    fun observeAudioRecordFileList(): LiveData<List<AudioRecordFile>>

    /**
     * Observes a single AudioRecordFile.
     *
     * @param audioRecordFileId the AudioRecordFile id.
     * @return the AudioRecordFile with audioRecordFileId.
     */
    @Query("SELECT * FROM AudioRecordFile WHERE id = :audioRecordFileId")
    fun observeAudioRecordFileById(audioRecordFileId: String): LiveData<AudioRecordFile>

    /**
     * Select all AudioRecordFile from the AudioRecordFile table.
     *
     * @return all AudioRecordFile.
     */
    @Query("SELECT * FROM AudioRecordFile order by createTime DESC ")
    suspend fun getAudioRecordList(): List<AudioRecordFile>

    /**
     * Select a AudioRecordFile by id.
     *
     * @param audioRecordFileId the AudioRecordFile id.
     * @return the AudioRecordFile with audioRecordFileId.
     */
    @Query("SELECT * FROM AudioRecordFile WHERE id = :audioRecordFileId")
    suspend fun getAudioRecordFileById(audioRecordFileId: String): AudioRecordFile?

    /**
     * Select a AudioRecordFile by file path.
     *
     * @param filePath the AudioRecordFile file path.
     * @return the AudioRecordFile with filePath.
     */
    @Query("SELECT * FROM AudioRecordFile WHERE filePath = :filePath")
    suspend fun getAudioRecordFileByFilePath(filePath: String): AudioRecordFile?

    @Query("SELECT * FROM AudioRecordFile WHERE fileName LIKE :search OR filePath LIKE :search order by createTime DESC")
    suspend fun findAudioRecordFileListWithFileName(search: String): List<AudioRecordFile>

    /**
     * Insert a AudioRecordFile in the database. If the AudioRecordFile already exists, replace it.
     *
     * @param audioRecordFile the AudioRecordFile to be inserted.
     */
    @Insert
    suspend fun insertAudioRecordFile(audioRecordFile: AudioRecordFile): Long

    /**
     * Update a AudioRecordFile.
     *
     * @param audioRecordFile AudioRecordFile to be updated
     * @return the number of AudioRecordFile updated. This should always be 1.
     */
    @Update
    suspend fun updateAudioRecordFile(audioRecordFile: AudioRecordFile): Int

    /**
     * Insert a AudioRecordFile List in the database. If the AudioRecordFile already exists, replace it.
     *
     * @param audioRecordFileList the AudioRecordFile List to be inserted.
     */
    @Insert
    suspend fun insertAudioRecordFileList(audioRecordFileList: List<AudioRecordFile>)

    /**
     * Delete a AudioRecordFile by id.
     *
     * @return the number of AudioRecordFile deleted. This should always be 1.
     */
    @Query("DELETE FROM AudioRecordFile WHERE id = :audioRecordFileId")
    suspend fun deleteAudioRecordFileById(audioRecordFileId: String): Int

    /**
     * Delete all AudioRecordFile.
     */
    @Query("DELETE FROM AudioRecordFile")
    suspend fun deleteAudioRecordList()
}
