package com.nxg.app.utils

import android.media.MediaMetadataRetriever
import com.nxg.app.Constants.Companion.DEFAULT_AUDIO_RECORD_FILE_DIR
import com.nxg.app.WAV
import com.nxg.app.audiorecord.AudioRecordFragment
import com.nxg.app.data.source.local.AudioRecordFileLocalDataSource
import com.nxg.app.audiorecordlist.AudioRecordListFragment
import com.nxg.app.data.AudioRecordFile
import com.nxg.audiorecord.LogUtil
import java.io.File
import java.util.*
import java.util.Comparator

import java.util.Arrays


/**
 * 通过MediaMetadataRetriever读取WAV文件的音频时长
 */
fun getWAVFileDuration(filePath: String): String {
    var duration = "00:00:00"
    val mmr = MediaMetadataRetriever()
    try {
        mmr.setDataSource(filePath)
        //返回时长时长(毫秒)
        mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.let {
            duration = getFormatDuration(it.toLong())
        }
    } catch (ex: Exception) {
        LogUtil.i(AudioRecordListFragment.TAG, "duration ${ex.message}")
    } finally {
        mmr.release()
    }

    LogUtil.i(AudioRecordFileLocalDataSource.TAG, "duration $duration")
    return duration
}

/**
 * 获取目录的所有录音文件
 */
fun getAllLocalAudioRecordFileFromDir(
    dir: String = DEFAULT_AUDIO_RECORD_FILE_DIR,
    suffix: String = WAV
): MutableList<AudioRecordFile> {
    val result = mutableListOf<AudioRecordFile>()
    val fileDir = if (dir.isEmpty()) {
        DEFAULT_AUDIO_RECORD_FILE_DIR
    } else {
        dir
    }
    val files = File(
        fileDir
    ).listFiles { pathname ->
        pathname.isFile && pathname.name.endsWith(suffix)
    }
    if (files.isNullOrEmpty()) {
        return result
    }
    for (file in files) {
        getWAVFileDuration(file.absolutePath).let {
            result.add(
                AudioRecordFile(
                    0,
                    file.lastModified(),
                    it,
                    file.name,
                    file.absolutePath
                )
            )
        }

    }
    return result
}

fun getAllLocalWAVFileFromDir(
    dir: String = DEFAULT_AUDIO_RECORD_FILE_DIR,
    suffix: String = WAV
): Array<out File>? {
    val files = File(
        dir
    ).listFiles { pathname ->
        //LogUtil.i("AppUtils", "getAllLocalWAVFileFromDir pathname $pathname")
        pathname.isFile && pathname.name.endsWith(suffix)
    }
    //LogUtil.i("AppUtils", "getAllLocalWAVFileFromDir dir $dir")
    //LogUtil.i("AppUtils", "getAllLocalWAVFileFromDir suffix $suffix")
    //LogUtil.i("AppUtils", "getAllLocalWAVFileFromDir files $files")
    //按日期倒序，数据库查询时按照时间倒序查询就行了
    /*files?.let {
        Arrays.sort(it, Comparator<File> { f1, f2 ->
            val diff = f1.lastModified() - f2.lastModified()
            if (diff > 0) -1 else if (diff == 0L) 0 else 1
        })
    }*/
    return files
}


/**
 * @param timeMillis 毫秒
 */
fun getStringTime(timeMillis: Long): String {
    //LogUtil.i(AudioRecordFragment.TAG, "timeMillis $timeMillis")
    val millis = timeMillis % 1000 / 10
    //LogUtil.i(AudioRecordFragment.TAG, "millis $millis")
    val second = timeMillis / 1000 % 60
    val min = timeMillis / 1000 % 3600 / 60
    val hour = timeMillis / 1000 / 3600
    return String.format(Locale.CHINA, "%02d:%02d:%02d:%02d", hour, min, second, millis)
}

/**
 * @param timeMillis 毫秒
 */
fun getFormatDuration(timeMillis: Long, format: String = "%02d:%02d:%02d"): String {
    //LogUtil.i(AudioRecordFragment.TAG, "timeMillis $timeMillis")
    val millis = timeMillis % 1000 / 10
    //LogUtil.i(AudioRecordFragment.TAG, "millis $millis")
    val second = timeMillis / 1000 % 60
    val min = timeMillis / 1000 % 3600 / 60
    val hour = timeMillis / 1000 / 3600
    return String.format(Locale.CHINA, format, hour, min, second)
}



