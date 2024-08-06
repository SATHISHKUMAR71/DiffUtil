package com.example.samplenotesapplication.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.samplenotesapplication.R

import com.example.samplenotesapplication.fragments.AddNote
import com.example.samplenotesapplication.fragments.LongPressedFragment
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel

class NotesAdapter(private val viewModel: NotesAppViewModel):RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private var selectedItemList :MutableList<Note> = mutableListOf()
    private var notesList: MutableList<Note> = mutableListOf()
    private var selectedItemPos = 0
    private var isLongPressed = 0
    private lateinit var originalBackgroundColor:Drawable
    inner class NotesViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notes_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {

        holder.itemView.apply {
            selectedItemPos = holder.adapterPosition
            val date = findViewById<TextView>(R.id.dateNote)
            val title = findViewById<TextView>(R.id.titleNote)
            val content = findViewById<TextView>(R.id.contentNote)
            title.text = notesList[position].title
            println("Created at: ${notesList[position].createdAt}")
            date.text = notesList[position].createdAt
            content.text = notesList[position].content
            findViewById<CheckBox>(R.id.isChecked).apply {
                setOnClickListener {
                    selectedItemPos = holder.adapterPosition
                    isChecked = !isChecked
                    notesList[holder.adapterPosition].isSelected = !notesList[holder.adapterPosition].isSelected
                    viewModel.setSelectedNote(notesList[holder.adapterPosition])
                }
            }
            if(!notesList[position].isSelected){
                background = ContextCompat.getDrawable(context,R.drawable.normal_background_drawable)
                findViewById<CheckBox>(R.id.isChecked).apply {
                    visibility = View.INVISIBLE
                    isChecked = false
                }
            }
            else{
                background = ContextCompat.getDrawable(context,R.drawable.long_pressed_drawable)
                findViewById<CheckBox>(R.id.isChecked).apply {
                    visibility = View.VISIBLE
                    isChecked = true
                }
            }
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
            originalBackgroundColor = background

            setOnLongClickListener {
                selectedItemPos = holder.adapterPosition
                if(isLongPressed==0){
                    isLongPressed = 1
                    (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerMenu,LongPressedFragment())
                        .addToBackStack("Long pressed by the user")
                        .commit()
                }
                false
            }

            this.setOnClickListener {
                if(isLongPressed == 1){
                    selectedItemPos = holder.adapterPosition
                    notesList[holder.adapterPosition].isSelected = !notesList[holder.adapterPosition].isSelected
                    viewModel.setSelectedNote(notesList[holder.adapterPosition])
                }
                else{
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
    }

    fun setNotes(notes:MutableList<Note>){
        val diffUtil = NotesDiffUtil(notesList,notes)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        notesList = notes
        notesList.forEach { println("NotesList in DIFFUTIL $it") }
        diffResults.dispatchUpdatesTo(this)
    }

     fun onBackPressed() {
        println("On Back Pressed")
        val list = notesList.map {
            it.copy(isSelected = false)
        }.toMutableList()
        notesList.forEach {
            println("NotesList in backPressed $it")
        }
        setNotes(list)
        isLongPressed = 0
    }

    fun selectedItem(){
        notifyItemChanged(selectedItemPos)
    }
}