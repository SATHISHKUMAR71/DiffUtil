package com.example.samplenotesapplication.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.model.NotesDatabase
import com.example.samplenotesapplication.repository.NoteRepository
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel
import com.example.samplenotesapplication.viewmodel.NotesViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AppbarFragment(val fab:FloatingActionButton) : Fragment() {
    private lateinit var search:SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_appbar, container, false)
        search = view.findViewById(R.id.searchView)
        if(NotesAppViewModel.query.value?.isEmpty()==false){
            println("ON QUERY CHANGED isEmpty")
            search.setQuery(NotesAppViewModel.query.value?:"",true)
        }
        search.isFocusable = false
        search.isFocusableInTouchMode = false

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("ON QUERY CHANGED Submit")
                NotesAppViewModel.query.value = query
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(search.windowToken, 0)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                println("ON QUERY CHANGED Text Change")
                NotesAppViewModel.query.value = newText
                return true
            }
        })
        search.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                fab.visibility = View.GONE
            }
            else{
                fab.show()
            }
        }
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
    override fun onStop() {
        super.onStop()
    }
    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
    }
}