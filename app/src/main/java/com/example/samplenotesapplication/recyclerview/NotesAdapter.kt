package com.example.samplenotesapplication.recyclerview

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
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

    private var pinnedList:MutableList<Int> = mutableListOf(2)
    private var notesList: MutableList<Note> = mutableListOf()
    private var selectedItemPos = 0
    private var isCheckable = false
    private var isLongPressed = 0
    private var firstTimeLongPressed = 0
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
                    if(notesList[holder.adapterPosition].isSelected){
                        notesList[holder.adapterPosition].isSelected = false
                        if(notesList[holder.adapterPosition].isPinned==1){
                            pinnedList.remove(1)
                        }
                        else{
                            pinnedList.remove(0)
                        }
                    }
                    else{
                        notesList[holder.adapterPosition].isSelected = true
                        if(notesList[holder.adapterPosition].isPinned==1){
                            pinnedList.add(1)
                        }
                        else{
                            pinnedList.add(0)
                        }
                    }
                    NotesAppViewModel.setPinnedValues(pinnedList)
                    viewModel.setSelectedNote(notesList[holder.adapterPosition])
                }
            }
            if(notesList[position].isPinned==1){
                findViewById<ImageView>(R.id.pushPin).visibility = View.VISIBLE
            }
            else{
                findViewById<ImageView>(R.id.pushPin).visibility = View.INVISIBLE
            }
            if(notesList[position].isCheckable){
                findViewById<CheckBox>(R.id.isChecked).visibility = View.VISIBLE
            }
            else{
                findViewById<CheckBox>(R.id.isChecked).visibility = View.INVISIBLE
            }
            if(!notesList[position].isSelected){
                background = ContextCompat.getDrawable(context,R.drawable.normal_background_drawable)
                findViewById<CheckBox>(R.id.isChecked).apply {
                    isChecked = false
                }
            }
            else{
                background = ContextCompat.getDrawable(context,R.drawable.long_pressed_drawable)
                findViewById<CheckBox>(R.id.isChecked).apply {
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
                    makeClickable()
                    isCheckable = true
                    isLongPressed = 1
                    if(notesList[holder.adapterPosition].isSelected){
                        notesList[holder.adapterPosition].isSelected = false
                        if(notesList[holder.adapterPosition].isPinned==1){
                            pinnedList.remove(1)
                        }
                        else{
                            pinnedList.remove(0)
                        }
                    }
                    else{
                        notesList[holder.adapterPosition].isSelected = true
                        if(notesList[holder.adapterPosition].isPinned==1){
                            pinnedList.add(1)
                        }
                        else{
                            pinnedList.add(0)
                        }
                    }
                    NotesAppViewModel.setPinnedValues(pinnedList)
                    viewModel.setSelectedNote(notesList[holder.adapterPosition])
                    (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerMenu,LongPressedFragment(viewModel))
                        .addToBackStack("Long pressed by the user")
                        .commit()
                }
                false
            }

//            Recycler view Item Click Listener
            this.setOnClickListener {
                if((isLongPressed == 1) && (firstTimeLongPressed == 1)){
                    selectedItemPos = holder.adapterPosition
                    println("ITEM CLICKED ${NotesAppViewModel.isPinned}")
                    if(notesList[holder.adapterPosition].isSelected){
                        notesList[holder.adapterPosition].isSelected = false
                        if(notesList[holder.adapterPosition].isPinned==1){
                            pinnedList.remove(1)
                        }
                        else{
                            pinnedList.remove(0)
                        }
                    }
                    else{
                        notesList[holder.adapterPosition].isSelected = true
                        if(notesList[holder.adapterPosition].isPinned==1){
                            pinnedList.add(1)
                        }
                        else{
                            pinnedList.add(0)
                        }
                    }
                    NotesAppViewModel.setPinnedValues(pinnedList)
                    viewModel.setSelectedNote(notesList[holder.adapterPosition])
                }
                else if((isLongPressed == 1) && (firstTimeLongPressed == 0)){
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
        diffResults.dispatchUpdatesTo(this)
    }

     fun onBackPressed() {
        println("On Back Pressed")
         firstTimeLongPressed = 0
        val list = notesList.map {
            it.copy(isSelected = false)
        }.toMutableList()
         isCheckable = false
        setNotes(list)
        isLongPressed = 0
         pinnedList = mutableListOf(2)
         NotesAppViewModel.isPinned.value = 0
         makeUnClickable()
    }

    fun selectedItem(){
        notifyItemChanged(selectedItemPos)
    }

    fun selectAllItems() {
        pinnedList = mutableListOf(2)
        val list = notesList.map {
                if(it.isPinned==1){
                    pinnedList.add(1)
                }
                else{
                    pinnedList.add(0)
                }
            NotesAppViewModel.setPinnedValues(pinnedList)
            it.copy(isSelected = true)
        }.toMutableList()
        setNotes(list)
    }

    fun unSelectAllItems() {
        val list = notesList.map {
            it.copy(isSelected = false)
        }.toMutableList()
        pinnedList = mutableListOf(2)
        NotesAppViewModel.setPinnedValues(pinnedList)
        setNotes(list)
    }

    fun deleteSelectedItem() {
        val list = notesList.filter {
            if(it.isSelected){
                viewModel.deleteNote(it)
                false
            }
            else{
                true
            }
        }.toMutableList()
        pinnedList = mutableListOf(2)
        isLongPressed = 0
        setNotes(list)
    }

    fun pinSelectedItems() {
        val list = notesList.map { note ->
            if (note.isSelected) {
                val updatedNote = note.copy(isPinned = 1, isSelected = false)
                viewModel.updateNote(updatedNote)
                updatedNote
            } else {
                note
            }
        }.toMutableList()
        isLongPressed = 0
        setNotes(list)
    }

    fun unpinSelectedItems() {
        val list = notesList.map { note ->
            if (note.isSelected) {
                val updatedNote = note.copy(isPinned = 0, isSelected = false)
                viewModel.updateNote(updatedNote)
                updatedNote
            } else {
                note
            }
        }.toMutableList()
        isLongPressed = 0
        setNotes(list)
    }

    private fun makeClickable(){
        val list = notesList.map { note ->
            note.copy(isCheckable = true)
        }.toMutableList()
        isLongPressed = 0
        setNotes(list)
    }

    private fun makeUnClickable(){
        val list = notesList.map {
            if(!isCheckable){
                viewModel.updateNote(it.copy(isCheckable = false))
            }
            it.copy(isCheckable = false)

        }.toMutableList()
        setNotes(list)
    }
}