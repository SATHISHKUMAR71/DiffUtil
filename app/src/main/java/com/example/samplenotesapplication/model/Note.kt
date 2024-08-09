package com.example.samplenotesapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var title:String,
    var content:String,
    var createdAt:String,
    var updatedAt:String,
    var isPinned:Int,
    var isSelected:Boolean,
    var isCheckable:Boolean,
    var isHighlighted:Boolean
)