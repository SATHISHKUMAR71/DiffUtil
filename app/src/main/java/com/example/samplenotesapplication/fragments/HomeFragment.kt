package com.example.samplenotesapplication.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.recyclerview.NotesAdapter
import com.example.samplenotesapplication.model.NotesDatabase
import com.example.samplenotesapplication.repository.NoteRepository
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel
import com.example.samplenotesapplication.viewmodel.NotesViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    private lateinit var appbarFragment: AppbarFragment
    private var searchActionPerformed = false
    private lateinit var view:View
    private lateinit var fab:FloatingActionButton
    private var query = ""
    private var i = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_home, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.notesRecyclerView)
        val viewModelFactory = NotesViewModelFactory(requireActivity().application, NoteRepository(NotesDatabase.getNoteDatabase(requireContext())))
        val viewModel = ViewModelProvider(this,viewModelFactory)[NotesAppViewModel::class.java]

//        Floating Action Button On Click Listener
        fab = view.findViewById(R.id.addButton)
        fab.apply {
            setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .addToBackStack("Add Note")
                    .replace(R.id.fragmentContainerView,AddNote(viewModel))
                    .commit()
            }
        }
        appbarFragment = AppbarFragment(fab)
        if(parentFragmentManager.findFragmentByTag("longFragmentEnabled")?.isVisible != true){
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerMenu,appbarFragment,"appbarFragment")
                .commit()
        }
        (context as FragmentActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner,object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })

        val adapter = NotesAdapter(viewModel,this)
//        SearchView Observer


        NotesAppViewModel.query.observe(viewLifecycleOwner, Observer {
            if(it == ""){
                viewModel.getAllNotes().observe(viewLifecycleOwner, Observer { getAll->
                    println("GetAll Notes Observer Called 1")
                    adapter.setNotes(getAll)
                })
            }
            else{
                searchActionPerformed = true
                viewModel.getNotesByQuery(it).observe(viewLifecycleOwner, Observer { note ->
                    println("GetAll Notes Query Observer Called 1")
                    query = it
                    adapter.setNotesQuery(note,it)
                })
            }
        })

//        Read Notes Observer
        if(!searchActionPerformed){
            viewModel.getAllNotes().observe(viewLifecycleOwner, Observer {
                adapter.setNotes(it)
            })
        }

//        DELETE CONFIRMATION DIALOG OBSERVER
        NotesAppViewModel.deleteConfirmation.observe(viewLifecycleOwner, Observer {
            if(it){
                println("Delete Observer Called 2")
                adapter.deleteSelectedItem()
                parentFragmentManager.popBackStack()
            }
        })


//        SelectedNotes Observer
        viewModel.selectedNote.observe(viewLifecycleOwner, Observer {
            println("Selected Note Observer Called 3")
            adapter.selectedItem()
        })

//        Select All Items Observer
        NotesAppViewModel.selectAllItem.observe(viewLifecycleOwner, Observer {

            if(it){
                println("Selected All Item Observer Called 4")
                adapter.selectAllItems()
            }
            else{
                println("UnSelect All Item Observer Called 4")
                adapter.unSelectAllItems()
            }
        })

//        On BackPressed Observer
        NotesAppViewModel.onBackPressed.observe(viewLifecycleOwner, Observer {
            println("Back Pressed Observer Called 5")
            adapter.onBackPressed()
        })

//        Delete Selected Item Observer
        NotesAppViewModel.deleteSelectedItems.observe(viewLifecycleOwner, Observer {
            println("Delete Selected Item Observer Called 6 $it")
            if(it){
                adapter.deleteDialog(requireContext())
            }
        })

//        Pin Items Observer
        NotesAppViewModel.pinItemsClicked.observe(viewLifecycleOwner, Observer {
            if(NotesAppViewModel.isPinned.value== 0){
                println("Pin Item Observer Called 7")
                adapter.pinSelectedItems()
            }
            else{
                println("Unpin Item Observer Called 7")
                adapter.unpinSelectedItems()
            }
        })


//        Adapter initialization
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

        return view
    }



//    APPBAR CLEAR FOCUS WHEN BACK PRESSED
    private fun handleBackPress() {

        // Clear focus and hide keyboard if necessary
        val searchView = (appbarFragment.view?.findViewById<SearchView>(R.id.searchView))
        if(parentFragmentManager.findFragmentByTag("longFragmentEnabled")?.isVisible == true){
            parentFragmentManager.popBackStack()
        }


        else if ((searchView?.hasFocus() == true)||(searchActionPerformed)) {

            searchView?.setQuery("",false)
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchView?.windowToken, 0)
            searchView?.clearFocus()
            searchActionPerformed = false
        }
        else {
            requireActivity().finish()
        }
    }

}