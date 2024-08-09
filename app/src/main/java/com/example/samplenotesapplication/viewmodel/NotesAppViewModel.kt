package com.example.samplenotesapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.repository.NoteRepository
import kotlinx.coroutines.launch
import java.util.Collections.min

class NotesAppViewModel(private val application: Application,private val noteRepository: NoteRepository):AndroidViewModel(application) {


    var selectedNote = MutableLiveData<Note>()
    companion object{
        var query = MutableLiveData("")
        var selectCount = MutableLiveData(0)
        var deleteConfirmation = MutableLiveData(false)
        var isPinned = MutableLiveData(0)
        var onBackPressed = MutableLiveData(false)
        var selectAllItem = MutableLiveData(false)
        var pinItemsClicked = MutableLiveData(false)
        var deleteSelectedItems = MutableLiveData(false)
        fun setPinnedValues(list: List<Int>){
            isPinned.value =  min(list)
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
    fun getNotesByQuery(query:String):LiveData<MutableList<Note>>{
        return noteRepository.getNotesByQuery(query)
    }
}