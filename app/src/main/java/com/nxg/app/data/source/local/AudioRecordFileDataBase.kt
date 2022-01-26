package com.nxg.app.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nxg.app.data.AudioRecordFile

@Database(entities = [AudioRecordFile::class], version = 1, exportSchema = false)
abstract class AudioRecordFileDataBase : RoomDatabase() {

    abstract fun audioRecordFileDao(): AudioRecordFileDao

}