package com.example.samplenotesapplication.recyclerview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.samplenotesapplication.R

import com.example.samplenotesapplication.fragments.AddNote
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel

class NotesAdapter(private var context: Context,private val viewModel: NotesAppViewModel):RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private var notesList: MutableList<Note> = mutableListOf()
    private var pos = 0
    inner class NotesViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notes_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.itemView.apply {
            pos = holder.adapterPosition
            val date = findViewById<TextView>(R.id.dateNote)
            val title = findViewById<TextView>(R.id.titleNote)
            val content = findViewById<TextView>(R.id.contentNote)
            title.text = notesList[position].title
            println("Created at: ${notesList[position].createdAt}")
            date.text = notesList[position].createdAt
            content.text = notesList[position].content
            if(!((title.text == "") && (content.text==""))){
                if (title.text == "") {
                    title.visibility = View.GONE
                } else {
                    title.visibility = View.VISIBLE
                }
                if (content.text == "") {
                    content.visibility = View.GONE
                } else {
                    content.visibility = View.VISIBLE
                }
            }
            this.setOnClickListener {
                val addNoteFragment = AddNote(viewModel)
                addNoteFragment.arguments = Bundle().apply {
                    putInt("id",notesList[position].id)
                    putString("title",notesList[position].title)
                    putString("date",notesList[position].createdAt)
                    putString("content",notesList[position].content)
                }
                (context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView,addNoteFragment)
                    .addToBackStack("Note View")
                    .commit()
            }
        }
    }

    fun setNotes(notes:MutableList<Note>){
        val diffUtil = NotesDiffUtil(notesList,notes)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        notesList = notes
        diffResults.dispatchUpdatesTo(this)
    }
}