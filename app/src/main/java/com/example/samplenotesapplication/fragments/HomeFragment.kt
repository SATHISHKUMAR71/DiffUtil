package com.example.samplenotesapplication.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil.Callback
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
    private lateinit var appbarFragment: AppbarFragment
    private var searchActionPerformed = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.notesRecyclerView)
        val viewModelFactory = NotesViewModelFactory(requireActivity().application, NoteRepository(NotesDatabase.getNoteDatabase(requireContext())))
        val viewModel = ViewModelProvider(this,viewModelFactory)[NotesAppViewModel::class.java]
        appbarFragment = AppbarFragment(viewModel = viewModel)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerMenu,appbarFragment)
            .commit()

        (context as FragmentActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner,object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                println("ON BACK PRESSED HOME")
                handleBackPress()
            }

        })
        val adapter = NotesAdapter(viewModel)

//        SearchView Observer

        NotesAppViewModel.query.observe(viewLifecycleOwner, Observer {
            if(it == ""){
                searchActionPerformed = false
                viewModel.getAllNotes().observe(viewLifecycleOwner, Observer { getAll->
                    adapter.setNotes(getAll)
                })
                println("Search Get All Notes Called")
            }
            else{
                searchActionPerformed = true
                viewModel.getNotesByQuery(it).observe(viewLifecycleOwner, Observer { note ->
                    adapter.setNotes(note)
                })
            }
        })

//        Read Notes Observer
        viewModel.getAllNotes().observe(viewLifecycleOwner, Observer {
            adapter.setNotes(it)
        })


//        SelectedNotes Observer
        viewModel.selectedNote.observe(viewLifecycleOwner, Observer {
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
            adapter.onBackPressed()
        })

//        Delete Selected Item Observer
        NotesAppViewModel.deleteSelectedItems.observe(viewLifecycleOwner, Observer {
            adapter.deleteSelectedItem()
        })

//        Pin Items Observer
        NotesAppViewModel.pinItemsClicked.observe(viewLifecycleOwner, Observer {
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



//    APPBAR CLEAR FOCUS WHEN BACK PRESSED
    private fun handleBackPress() {

        // Clear focus and hide keyboard if necessary
        val searchView = (appbarFragment.view?.findViewById<SearchView>(R.id.searchView))
        if ((searchView?.hasFocus() == true)||(searchActionPerformed)) {
            searchView?.setQuery("",false)
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchView?.windowToken, 0)
            searchView?.clearFocus()
        }
        else {
            requireActivity().finish()
        }
    }

}