package com.example.samplenotesapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.model.Note

import com.example.samplenotesapplication.recyclerview.NotesAdapter
import com.example.samplenotesapplication.model.NotesDatabase
import com.example.samplenotesapplication.repository.NoteRepository
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel
import com.example.samplenotesapplication.viewmodel.NotesViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.notesRecyclerView)
        val viewModelFactory = NotesViewModelFactory(requireActivity().application, NoteRepository(NotesDatabase.getNoteDatabase(requireContext())))
        val viewModel = ViewModelProvider(this,viewModelFactory)[NotesAppViewModel::class.java]
        val adapter = NotesAdapter(viewModel)

//        Read Notes Observer
        viewModel.getAllNotes().observe(viewLifecycleOwner, Observer {
            println("Observer called")
            adapter.setNotes(it)
            println(it.size)
        })

//        SelectedNotes Observer
        viewModel.selectedNote.observe(viewLifecycleOwner, Observer {
            println("selected note Observer Called")
            adapter.selectedItem()
        })

//        Select All Items Observer
        NotesAppViewModel.selectAllItem.observe(viewLifecycleOwner, Observer {
            if(it){
                adapter.selectAllItems()
            }
            else{
                adapter.unSelectAllItems()
            }
        })

//        On BackPressed Observer
        NotesAppViewModel.onBackPressed.observe(viewLifecycleOwner, Observer {
            println("set notes called in back pressed")
            adapter.onBackPressed()
        })

//        Delete Selected Item Observer
        NotesAppViewModel.deleteSelectedItems.observe(viewLifecycleOwner, Observer {
            adapter.deleteSelectedItem()
        })

//        Pin Items Observer
        NotesAppViewModel.pinItemsClicked.observe(viewLifecycleOwner, Observer {
            println("PIN OBSERVER CALLED Pin Operation ${NotesAppViewModel.isPinned.value}")
            if(NotesAppViewModel.isPinned.value== 0){
                adapter.pinSelectedItems()
            }
            else{
                adapter.unpinSelectedItems()
            }
        })

//        Adapter initialization
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

//        Floating Action Button On Click Listener
        view.findViewById<FloatingActionButton>(R.id.addButton).apply {
            setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .addToBackStack("Add Note")
                    .replace(R.id.fragmentContainerView,AddNote(viewModel))
                    .commit()
            }
        }
        return view
    }

    override fun onPause() {
        super.onPause()
        println("F On Pause")
    }

    override fun onResume() {
        super.onResume()
        println("F On Resume")
    }

    override fun onStop() {
        super.onStop()
        println("F On Stop")
    }

}