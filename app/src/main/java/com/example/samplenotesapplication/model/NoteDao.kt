package com.example.samplenotesapplication.model

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note:Note)

    @Update
    suspend fun updateNote(note:Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNoteNormal(note:Note)

    @Update
    fun updateNoteNormal(note:Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM NOTES WHERE id = :id")
    fun deleteNoteById(id:Int)

    @RawQuery
    fun getNotesCount(query: SupportSQLiteQuery):Cursor


    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes():LiveData<MutableList<Note>>

    @RawQuery
    fun getAllNotesCursor(query: SupportSQLiteQuery): Cursor

    @RawQuery
    fun getAllNoteById(query: SupportSQLiteQuery): Cursor

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun getNotesByQuery(query:String):LiveData<MutableList<Note>>

}