package com.example.samplenotesapplication.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentContainerView
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.constants.Months
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddNote(private var viewModel: NotesAppViewModel) : Fragment() {

    private var noteId=0
    private var note: Note? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requireActivity().findViewById<FragmentContainerView>(R.id.fragmentContainerMenu).apply{
            visibility = View.GONE }
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_add_note, container, false)
        val title = view.findViewById<EditText>(R.id.title)
        val content = view.findViewById<EditText>(R.id.content)
        val date = view.findViewById<TextView>(R.id.date)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a")
        val now = LocalDateTime.now()
        var time = now.format(formatter)
        time = "$time ${now.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())}"
        if(arguments!=null){
            arguments?.let {
                title.setText(it.getString("title"))
                content.setText(it.getString("content"))
                date.text = (it.getString("date"))
                noteId = it.getInt("id")
                note = Note(noteId,title.text.toString(),content.text.toString(),time,time,arguments?.getInt("isPinned")?:0,false,false,false)
            }
        }
        else{
            val newDateTime = time.split(" ")
            val dateValues = newDateTime[0].split("-")
            val day = dateValues[2]
            val monthName = Months.MONTHS[dateValues[2].toInt()]
            val timeValues = newDateTime[1].split(":")
            val normalTime = if(timeValues[0].toInt()>12){
                timeValues[0].toInt() - 12
            } else {
                timeValues[0].toInt()
            }
            val newDateTimeFormat = "$day $monthName ${normalTime}:${timeValues[1]} ${newDateTime[2]}"
            date.text = newDateTimeFormat
        }
        view.findViewById<ImageButton>(R.id.backNavigator).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        view.findViewById<ImageButton>(R.id.save).setOnClickListener {
            if(arguments==null){
                note = Note(0,title.text.toString(),content.text.toString(),time,time,0,false,false,false)
//                INSERT NOTE
                if((title.text.toString()!="")||(content.text.toString()!="")){
                    note?.let {
                        viewModel.addNote(it)
                    }
                }
            }
            else{
                note = Note(noteId,title.text.toString(),content.text.toString(),time,time,0,false,false,false)
//                UPDATE NOTE
                if((title.text.toString()!="")||(content.text.toString()!="")) {
                    note?.let {
                        viewModel.updateNote(it)
                    }
                }
                else{
                    note?.let {
                        viewModel.deleteNote(it)
                    }
                }
            }
            parentFragmentManager.popBackStack()
        }
        view.findViewById<ImageButton>(R.id.deleteNote).setOnClickListener {
//            DELETE NOTE
            note?.let {
                viewModel.deleteNote(it)
            }
            parentFragmentManager.popBackStack()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<FragmentContainerView>(R.id.fragmentContainerMenu).apply{
            visibility = View.VISIBLE }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}