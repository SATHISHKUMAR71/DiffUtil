package com.example.samplenotesapplication.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.constants.Months

import com.example.samplenotesapplication.fragments.AddNote
import com.example.samplenotesapplication.fragments.LongPressedFragment
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class NotesAdapter(private val viewModel: NotesAppViewModel):RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private var selectedItemList :MutableList<Note> = mutableListOf()
    private var notesList: MutableList<Note> = mutableListOf()
    private var selectedItemPos = 0
    private var isLongPressed = 0
    var firstTimeLongPressed = 0
    private lateinit var originalBackgroundColor:Drawable
    private lateinit var currentTime:List<String>
    private var currentDay = 0
    private var format = ""
    private var currentYear = 0
    private var currentMonth = 0

    inner class NotesViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a")
        val now = LocalDateTime.now()
        val currentDate = now.format(formatter)
        println(currentDate)
        val dateAndTime = currentDate.split(" ")
        val date = dateAndTime[0].split("-")
        currentTime = dateAndTime[1].split(":")
        currentDay = date[2].toInt()
        currentMonth = date[1].toInt()
        currentYear = date[0].toInt()
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
            var editedDate = notesList[position].createdAt
            val dateAndTime = editedDate.split(" ")
            val date1 = dateAndTime[0].split("-")
            val time = dateAndTime[1].split(":")
            format = dateAndTime[2]
            println(time)
            val day = date1[2].toInt()
            val month = date1[1].toInt()
            val year = date1[0].toInt()
            val monthName = Months.MONTHS[month-1]
            var dateInfo = "$day $monthName ${time[0]}:${time[1]} $format"
            if((currentMonth == month)&&(currentYear==year)&&(day==currentDay)){
                editedDate = "Today ${time[0]}:${time[1]} $format"
            }
            else if ((currentMonth == month) && (abs(day-currentDay)<7)){
                editedDate = if(abs(day-currentDay)==1){
                    "Yesterday ${time[0]}:${time[1]} $format"
                } else{
                    "${dateAndTime[3]} ${time[0]}:${time[1]} $format"
                }
            }
            else if(((currentMonth == month)&&(currentYear==year)) || (currentYear==year)){
                editedDate = "$monthName $day"
                println(editedDate)
            }
            else{
                dateInfo = "$day $monthName, $year ${time[0]}:${time[1]} $format"
                editedDate = "$monthName $day, $year"
            }
            date.text = editedDate
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
                    notesList[holder.adapterPosition].isSelected = !notesList[holder.adapterPosition].isSelected
                    viewModel.setSelectedNote(notesList[holder.adapterPosition])
                    (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerMenu,LongPressedFragment())
                        .addToBackStack("Long pressed by the user")
                        .commit()
                }
                false
            }

            this.setOnClickListener {

                if((isLongPressed == 1) && (firstTimeLongPressed == 1)){
                    selectedItemPos = holder.adapterPosition
                    notesList[holder.adapterPosition].isSelected = !notesList[holder.adapterPosition].isSelected
                    viewModel.setSelectedNote(notesList[holder.adapterPosition])
                }
                else if(firstTimeLongPressed == 0){
                    firstTimeLongPressed = 1
                }
                else{
                    val addNoteFragment = AddNote(viewModel)
                    addNoteFragment.arguments = Bundle().apply {
                        putInt("id",notesList[position].id)
                        putString("title",notesList[position].title)
                        putString("date",dateInfo)
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