package com.example.samplenotesapplication.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.example.samplenotesapplication.model.Note

class NotesDiffUtil(
    private val oldList:MutableList<Note>,
    private val newList:MutableList<Note>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

