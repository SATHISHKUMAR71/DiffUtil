package com.example.samplenotesapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.repository.NoteRepository
import kotlinx.coroutines.launch

class NotesAppViewModel(private val application: Application,private val noteRepository: NoteRepository):AndroidViewModel(application) {

    fun addNote(note: Note){
        viewModelScope.launch {
            noteRepository.addNote(note)
        }
    }

    fun updateNote(note:Note){
        viewModelScope.launch {
            noteRepository.updateNote(note)
        }
    }

    fun deleteNote(note:Note){
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }

    fun getAllNotes(): LiveData<MutableList<Note>> {
        return noteRepository.getAllNotes()
    }
}