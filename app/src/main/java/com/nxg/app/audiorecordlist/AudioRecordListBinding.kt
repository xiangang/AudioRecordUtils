package com.nxg.app.audiorecordlist

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nxg.app.audiorecordlist.AudioRecordListFragment.Companion.TAG
import com.nxg.app.data.AudioRecordFile
import com.nxg.audiorecord.LogUtil

/**
 * [BindingAdapter]s for the [AudioRecordFile]s list.
 */
@BindingAdapter("items")
fun setItems(listView: RecyclerView, items: List<AudioRecordFile>?) {
    items?.let {
        LogUtil.i(TAG, "setItems ${items.size}")
        (listView.adapter as AudioRecordListAdapter).submitList(items.toMutableList())
    }
}