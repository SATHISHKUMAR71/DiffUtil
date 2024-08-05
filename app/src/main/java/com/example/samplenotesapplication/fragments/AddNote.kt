package com.example.samplenotesapplication.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel
import java.time.LocalDate

class AddNote(private var viewModel: NotesAppViewModel) : Fragment() {

    private var noteId=0
    private lateinit var note: Note
    override fun onCreate(savedInstanceState: Bundle?) {
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
        date.text = LocalDate.now().toString()
        if(arguments!=null){
            arguments?.let {
                title.setText(it.getString("title"))
                content.setText(it.getString("content"))
                date.text = (it.getString("date"))
                noteId = it.getInt("id")
                note = Note(noteId,title.text.toString(),content.text.toString(),LocalDate.now().toString(),LocalDate.now().toString(),0)
            }
        }
        view.findViewById<ImageButton>(R.id.backNavigator).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        view.findViewById<ImageButton>(R.id.save).setOnClickListener {
            if(arguments==null){
                note = Note(0,title.text.toString(),content.text.toString(),LocalDate.now().toString(),LocalDate.now().toString(),0)
//                INSERT NOTE
                viewModel.addNote(note)
            }
            else{
                note = Note(noteId,title.text.toString(),content.text.toString(),LocalDate.now().toString(),LocalDate.now().toString(),0)
//                UPDATE NOTE
                viewModel.updateNote(note)
            }
            println((Note(noteId,title.text.toString(),content.text.toString(),LocalDate.now().toString(),LocalDate.now().toString(),0)).toString())
            parentFragmentManager.popBackStack()
        }
        view.findViewById<ImageButton>(R.id.deleteNote).setOnClickListener {
//            DELETE NOTE
            viewModel.deleteNote(note)
            parentFragmentManager.popBackStack()
            Toast.makeText(context,"Notes Deleted Successfully",Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        println("On Fragment Destroy")
    }
}