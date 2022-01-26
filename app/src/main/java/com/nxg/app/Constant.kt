package com.nxg.app

import android.os.Environment

const val PCM = ".pcm"
const val WAV = ".wav"

class Constants {
    companion object {
        val DEFAULT_AUDIO_RECORD_FILE_DIR =
            Environment.getExternalStorageDirectory().toString() + "/ARecordMaster"
    }
}

