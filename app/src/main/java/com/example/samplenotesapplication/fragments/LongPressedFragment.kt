package com.example.samplenotesapplication.fragments

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton


class LongPressedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_long_pressed, container, false)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.longPressedToolbar)
        NotesAppViewModel.selectCount.observe(viewLifecycleOwner, Observer {
            toolbar.setTitle("$it Items Selected")
            if(it==0){
                toolbar.menu.findItem(R.id.deleteSelectedItems).apply {
                    setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.delete_disabled))
                    isEnabled = false
                }

            }
            else{
                toolbar.menu.findItem(R.id.deleteSelectedItems).apply {
                    setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_delete_24))
                    isEnabled = true
                }
            }
        })

        toolbar.setNavigationOnClickListener {
            onDestroyView()
        }
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                (R.id.selectAllItems)->{
                    Toast.makeText(context,"Select all Clicked",Toast.LENGTH_SHORT).show()
                    NotesAppViewModel.selectAllItem.value = NotesAppViewModel.selectAllItem.value != true
                    if(NotesAppViewModel.selectAllItem.value==true){
                        toolbar.menu.findItem(R.id.selectAllItems).setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_deselect_24))
                    }
                    else{
                        toolbar.menu.findItem(R.id.selectAllItems).setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_select_all_24))
                    }
                    true
                }
                (R.id.deleteSelectedItems)->{
                    if(NotesAppViewModel.selectCount.value!=0){
                        NotesAppViewModel.deleteSelectedItems.value = true
                    }
                    true
                }
                (R.id.pinSelectedNotes) -> {
                    NotesAppViewModel.pinItemsClicked.value = NotesAppViewModel.pinItemsClicked.value != true
                    onDestroyView()
                    true
                }
                else -> false
            }
        }
        NotesAppViewModel.isPinned.observe(viewLifecycleOwner, Observer {
//            UNPIN

            when(NotesAppViewModel.isPinned.value){
                0 -> {
                    toolbar.menu.findItem(R.id.pinSelectedNotes).apply {
                        isVisible = true
                        isEnabled = true
                        toolbar.menu.findItem(R.id.pinSelectedNotes).setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.keep_24px))
                    }
                }
                2 -> {
                    toolbar.menu.findItem(R.id.pinSelectedNotes).apply {
                        setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.pin_disabled))
                        isEnabled = false
                    }
                }
                else -> {
                    toolbar.menu.findItem(R.id.pinSelectedNotes).apply {
                        isVisible = true
                        isEnabled = true
                        toolbar.menu.findItem(R.id.pinSelectedNotes).setIcon(ContextCompat.getDrawable(requireContext(),R.drawable.keep_off_24px))
                    }
                }
            }
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("On Destroy View")
        NotesAppViewModel.onBackPressed.value = true
        NotesAppViewModel.selectAllItem.value = false
        NotesAppViewModel.deleteSelectedItems.value = false
        parentFragmentManager.popBackStack()
    }

}