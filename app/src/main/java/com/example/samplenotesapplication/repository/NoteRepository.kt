package com.example.samplenotesapplication.repository

import androidx.lifecycle.LiveData
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.model.NoteDao
import com.example.samplenotesapplication.model.NotesDatabase

class NoteRepository(private var noteDB: NotesDatabase) {

    suspend fun addNote(note: Note){
        noteDB.getNoteDao().insertNote(note)
    }

    suspend fun deleteNote(note:Note){
        noteDB.getNoteDao().deleteNote(note)
    }

    suspend fun updateNote(note:Note){
        noteDB.getNoteDao().updateNote(note)
    }

    fun getAllNotes(): LiveData<MutableList<Note>> {
        return noteDB.getNoteDao().getAllNotes()
    }

    fun getNotesByQuery(query:String):LiveData<MutableList<Note>>{
        return noteDB.getNoteDao().getNotesByQuery(query)
    }
}