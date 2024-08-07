package com.example.samplenotesapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel
import com.google.android.material.appbar.MaterialToolbar


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
        view.findViewById<MaterialToolbar>(R.id.longPressedToolbar).setOnMenuItemClickListener {
            when(it.itemId){
                (R.id.selectAllItems)->{
                    Toast.makeText(context,"Select all Clicked",Toast.LENGTH_SHORT).show()
                    NotesAppViewModel.selectAllItem.value = NotesAppViewModel.selectAllItem.value != true
                    true
                }
                (R.id.deleteSelectedItems)->{
                    NotesAppViewModel.deleteSelectedItems.value = true
                    onDestroyView()
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
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("On Destroy")
        NotesAppViewModel.onBackPressed.value = true
        NotesAppViewModel.selectAllItem.value = false
        NotesAppViewModel.deleteSelectedItems.value = false
        parentFragmentManager.popBackStack()
    }

}