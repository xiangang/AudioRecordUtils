package com.nxg.app.audiorecordlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nxg.app.data.AudioRecordFile
import com.nxg.app.databinding.AudioRecordListItemBinding


/**
 * Adapter for the audio record list. Has a reference to the [AudioRecordListViewModel] to send actions back to it.
 */
class AudioRecordListAdapter(private val viewModel: AudioRecordListViewModel) :
    ListAdapter<AudioRecordFile, AudioRecordListAdapter.ViewHolder>(AudioRecordListDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: AudioRecordListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: AudioRecordListViewModel, item: AudioRecordFile) {

            binding.position = layoutPosition
            binding.audioRecordFile = item
            binding.audioRecordListViewModel = viewModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AudioRecordListItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class AudioRecordListDiffCallback : DiffUtil.ItemCallback<AudioRecordFile>() {
    override fun areItemsTheSame(oldItem: AudioRecordFile, newItem: AudioRecordFile): Boolean {
        //LogUtil.i(TAG, "areItemsTheSame ${oldItem.id == newItem.id}")
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AudioRecordFile, newItem: AudioRecordFile): Boolean {
        //LogUtil.i(TAG, "areContentsTheSame ${oldItem == newItem}")
        return oldItem == newItem
    }
}
