package com.example.samplenotesapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel


class LongPressedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_long_pressed, container, false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        println("On Destroy")
        NotesAppViewModel.onBackPressed.value = true
    }

}