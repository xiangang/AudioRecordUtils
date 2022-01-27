package com.nxg.app.audiorecord

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.nxg.app.audiorecordlist.AudioRecordListFragment.Companion.TAG
import com.nxg.audiorecord.AudioRecordHandler
import com.nxg.audiorecord.LogUtil
import javax.inject.Inject

class AudioRecordHandlerLifecycleObserver @Inject constructor(private val audioRecordHandler: AudioRecordHandler) :
    DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        LogUtil.i(TAG, "AudioRecordHandlerLifecycleObserver onResume")
        audioRecordHandler.start()
    }

    override fun onStop(owner: LifecycleOwner) {
        LogUtil.i(TAG, "AudioRecordHandlerLifecycleObserver onStop")
        audioRecordHandler.stop()
    }


}