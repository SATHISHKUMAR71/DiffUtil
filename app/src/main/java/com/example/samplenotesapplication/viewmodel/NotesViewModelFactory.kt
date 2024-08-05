package com.example.samplenotesapplication.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.samplenotesapplication.repository.NoteRepository

class NotesViewModelFactory(private val application: Application,private val noteRepository:NoteRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesAppViewModel(application,noteRepository) as T
    }
}