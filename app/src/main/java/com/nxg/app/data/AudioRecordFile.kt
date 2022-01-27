package com.nxg.app.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nxg.app.R

@Entity(tableName = "AudioRecordFile")
data class AudioRecordFile constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long,
    @NonNull @ColumnInfo(name = "createTime") var createTime: Long,
    @NonNull @ColumnInfo(name = "duration") var duration: String,
    @NonNull @ColumnInfo(name = "fileName") var fileName: String,
    @NonNull @ColumnInfo(name = "filePath") var filePath: String
) {
    @Ignore
    var play: Boolean = false

    fun play() {
        play = true
    }

    fun pause() {
        play = false
    }

    fun getIcon(): Int {
        return if (play) {
            R.mipmap.ic_pause
        } else {
            R.mipmap.ic_play
        }
    }

    /*override fun equals(other: Any?): Boolean {
        val o = other as AudioRecordFile
        return id == o.id && play == o.play
    }*/

}