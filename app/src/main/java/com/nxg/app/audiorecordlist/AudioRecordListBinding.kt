package com.nxg.app.audiorecordlist

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nxg.app.data.AudioRecordFile

/**
 * [BindingAdapter]s for the [AudioRecordFile]s list.
 */
@BindingAdapter("items")
fun setItems(listView: RecyclerView, items: List<AudioRecordFile>?) {
    items?.let {
        (listView.adapter as AudioRecordListAdapter).submitList(items)
    }
}