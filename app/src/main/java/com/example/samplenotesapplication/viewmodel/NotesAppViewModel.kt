package com.example.samplenotesapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.repository.NoteRepository
import kotlinx.coroutines.launch

class NotesAppViewModel(private val application: Application,private val noteRepository: NoteRepository):AndroidViewModel(application) {


    var selectedNote = MutableLiveData<Note>()
    companion object{
        var isPinned = 0
        var newPinnedCount = 0
        var onBackPressed = MutableLiveData(false)
        var selectAllItem = MutableLiveData(false)
        var pinItemsClicked = MutableLiveData(false)
        var deleteSelectedItems = MutableLiveData(false)
        fun setBackPressed(pressed:Boolean){
            onBackPressed.value = pressed
        }
    }


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


    fun setSelectedNote(note:Note){
        selectedNote.value = note
    }
}