package com.example.samplenotesapplication.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Note::class], version = 1)
abstract class NotesDatabase:RoomDatabase() {
    abstract fun getNoteDao():NoteDao

    companion object{
        @Volatile
        private var INSTANCE:NotesDatabase? = null
        fun getNoteDatabase(context:Context):NotesDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "notes_app_database")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}