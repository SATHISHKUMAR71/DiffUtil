package com.example.samplenotesapplication.recyclerview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Query
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.constants.Months
import com.example.samplenotesapplication.fragments.AddNote
import com.example.samplenotesapplication.fragments.HomeFragment
import com.example.samplenotesapplication.fragments.LongPressedFragment
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs


class NotesAdapter(private val viewModel: NotesAppViewModel):RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private var pinnedList:MutableList<Int> = mutableListOf(2)
    private var notesList: MutableList<Note> = mutableListOf()
    private var isHighlight = false
    private var query = ""
    private var selectedItemPos = 0
    private var selectCount = 0
    private lateinit var title: TextView
    private lateinit var date:TextView
    private lateinit var content: TextView
    private lateinit var deleteDialog:AlertDialog
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
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")
        val now = LocalDateTime.now()
        val currentDate = now.format(formatter)
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
            date = findViewById(R.id.dateNote)
            title = findViewById(R.id.titleNote)
            content = findViewById(R.id.contentNote)
            if(isHighlight && query.isNotEmpty()){
                println("IS HIGHLIGHT CALLED")
                println("IS HIGHLIGHT CALLED query: $query")
                val titleContent = notesList[position].title
                val bodyContent = notesList[position].content
                val spannableTitle = SpannableString(titleContent)
                val spannableContent = SpannableString(bodyContent)
                var startContentIndex = bodyContent.indexOf(query, ignoreCase = true)
                var startIndex = titleContent.indexOf(query, ignoreCase = true)
                while (startIndex >= 0) {
                    val endIndex = startIndex + query.length
                    println("IS HIGHLIGHT CALLED")
                    // Ensure indices are within the bounds of the text length
                    if (startIndex >= 0 && endIndex <= titleContent.length) {
                        // Apply a ForegroundColorSpan to highlight the text
                        spannableTitle.setSpan(
                            ForegroundColorSpan(Color.argb(255,255,20,20)), // You can choose any color
                            startIndex,
                            endIndex,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }
                    // Find the next occurrence of the query text
                    startIndex = titleContent.indexOf(query, endIndex, ignoreCase = true)
                }
                while (startContentIndex >= 0) {
                    val endContentIndex = startContentIndex + query.length
                    println("IS HIGHLIGHT CALLED")
                    // Ensure indices are within the bounds of the text length
                    if (startContentIndex >= 0 && endContentIndex <= bodyContent.length) {
                        // Apply a ForegroundColorSpan to highlight the text
                        spannableContent.setSpan(
                            ForegroundColorSpan(Color.argb(255,255,20,10)), // You can choose any color
                            startContentIndex,
                            endContentIndex,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }
                    // Find the next occurrence of the query text
                    startContentIndex = bodyContent.indexOf(query, endContentIndex, ignoreCase = true)
                }
                title.text = spannableTitle
                content.text = spannableContent
            }
            else{
                title.text = notesList[position].title
                content.text = notesList[position].content
            }

            var editedDate = notesList[position].createdAt
            val dateAndTime = editedDate.split(" ")
            val date1 = dateAndTime[0].split("-")
            val time = dateAndTime[1].split(":")
            format = dateAndTime[2]
            val day = date1[2].toInt()
            val month = date1[1].toInt()
            val year = date1[0].toInt()
            val monthName = Months.MONTHS[month-1]
            val normalTime = if(time[0].toInt()>12){
                time[0].toInt() - 12
            } else {
                time[0].toInt()
            }
            var dateInfo = "$day $monthName ${normalTime}:${time[1]} $format"
            if((currentMonth == month)&&(currentYear==year)&&(day==currentDay)){
                editedDate = "Today ${normalTime}:${time[1]} $format"
            }
            else if ((currentMonth == month) && (abs(day-currentDay)<7)){
                editedDate = if(abs(day-currentDay)==1){
                    "Yesterday ${normalTime}:${time[1]} $format"
                } else{
                    "${dateAndTime[3]} ${normalTime}:${time[1]} $format"
                }
            }
            else if(((currentMonth == month)&&(currentYear==year)) || (currentYear==year)){
                editedDate = "$monthName $day"
            }
            else{
                dateInfo = "$day $monthName, $year ${normalTime}:${time[1]} $format"
                editedDate = "$monthName $day, $year"
            }
            date.text = editedDate
//            9 AUG COMMENTED
//            content.text = notesList[position].content
            findViewById<CheckBox>(R.id.isChecked).apply {
                setOnClickListener {
                    selectedItemPos = holder.adapterPosition
                    isChecked = !isChecked
                    if(notesList[holder.adapterPosition].isSelected){
                        notesList[holder.adapterPosition].isSelected = false
                        selectCount-=1
                        NotesAppViewModel.selectCount.value = selectCount
                        if(notesList[holder.adapterPosition].isPinned==1){
                            pinnedList.remove(1)
                        }
                        else{
                            pinnedList.remove(0)
                        }
                    }
                    else{
                        selectCount+=1
                        NotesAppViewModel.selectCount.value = selectCount
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
            if(notesList[holder.adapterPosition].isPinned==1){
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
                println(notesList.size)
                if(isLongPressed==0){
                    makeClickable()
                    isCheckable = true
                    isLongPressed = 1
                    println(notesList.size)
                    if(notesList[selectedItemPos].isSelected){
                        notesList[selectedItemPos].isSelected = false
                        selectCount -=1
                        NotesAppViewModel.selectCount.value = selectCount
                        if(notesList[selectedItemPos].isPinned==1){
                            pinnedList.remove(1)
                        }
                        else{
                            pinnedList.remove(0)
                        }
                    }
                    else{
                        selectCount+=1
                        NotesAppViewModel.selectCount.value = selectCount
                        notesList[selectedItemPos].isSelected = true
                        if(notesList[selectedItemPos].isPinned==1){
                            pinnedList.add(1)
                        }
                        else{
                            pinnedList.add(0)
                        }
                    }
                    NotesAppViewModel.setPinnedValues(pinnedList)
                    viewModel.setSelectedNote(notesList[selectedItemPos])
                    (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerMenu,LongPressedFragment(),"longFragmentEnabled")
                        .addToBackStack("Long pressed by the user")
                        .commit()
                }
                false
            }

//            Recycler view Item Click Listener
            this.setOnClickListener {
                selectedItemPos = holder.adapterPosition
                if((isLongPressed == 1)){
                    if(notesList[selectedItemPos].isSelected){
                        notesList[selectedItemPos].isSelected = false
                        selectCount -=1
                        NotesAppViewModel.selectCount.value = selectCount
                        if(notesList[selectedItemPos].isPinned==1){
                            pinnedList.remove(1)
                        }
                        else{
                            pinnedList.remove(0)
                        }
                    }
                    else{
                        notesList[selectedItemPos].isSelected = true
                        selectCount +=1
                        NotesAppViewModel.selectCount.value = selectCount
                        if(notesList[selectedItemPos].isPinned==1){
                            pinnedList.add(1)
                        }
                        else{
                            pinnedList.add(0)
                        }
                    }
                    NotesAppViewModel.setPinnedValues(pinnedList)
                    viewModel.setSelectedNote(notesList[selectedItemPos])
                }
                else{
                    val addNoteFragment = AddNote(viewModel)
                    addNoteFragment.arguments = Bundle().apply {
                        putInt("id",notesList[selectedItemPos].id)
                        putString("title",notesList[selectedItemPos].title)
                        putString("date",dateInfo)
                        putInt("isPinned",notesList[selectedItemPos].isPinned)
                        putString("content",notesList[selectedItemPos].content)
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
        println("SET NOTES CALLED")
        isHighlight = false
        val diffUtil = NotesDiffUtil(notesList,notes)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        notesList = notes
        diffResults.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    fun setNotesQuery(notes:MutableList<Note>,query1: String){
        println("SET QUERY NOTES CALLED")
        query = query1
        isHighlight = true
        val diffUtil = NotesDiffUtil(notesList,notes)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        notesList = notes
        diffResults.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

     fun onBackPressed() {
         firstTimeLongPressed = 0
         selectCount = 0
         NotesAppViewModel.selectCount.value = selectCount
        val list = notesList.map {
            it.copy(isSelected = false)
        }.toMutableList()
         isCheckable = false
        setNotes(list)
        isLongPressed = 0
         pinnedList = mutableListOf(2)
         NotesAppViewModel.isPinned.value = 0
//         makeUnClickable()
    }

    fun selectedItem(){
        notifyItemChanged(selectedItemPos)
    }

    fun selectAllItems() {
        pinnedList = mutableListOf(2)
        selectCount = 0
        NotesAppViewModel.selectCount.value = selectCount
        val list = notesList.map {
                if(it.isPinned==1){
                    pinnedList.add(1)
                }
                else{
                    pinnedList.add(0)
                }
            selectCount +=1
            NotesAppViewModel.selectCount.value = selectCount
            NotesAppViewModel.setPinnedValues(pinnedList)
            it.copy(isSelected = true)
        }.toMutableList()
        println("NOTES123: SELECTED")
        setNotes(list)
    }

    fun unSelectAllItems() {
        selectCount = 0
        NotesAppViewModel.selectCount.value = selectCount
        val list = notesList.map {
            it.copy(isSelected = false)
        }.toMutableList()
        println("NOTES123: UNSELECTED")
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
        println("NOTES123: PIN CALLED")
        var i=0
        val list = notesList.map { note ->
            if (note.isSelected) {
                val updatedNote = note.copy(isPinned = 1, isSelected = false,isCheckable = false)
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
        println("NOTES123: UNPIN CALLED")
        var i=0
        val list = notesList.map { note ->
            if (note.isSelected) {
                val updatedNote = note.copy(isPinned = 0, isSelected = false,isCheckable = false)
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
        println("NOTES123: Make clickable")
        val list = notesList.map { note ->
            note.copy(isCheckable = true)
        }.toMutableList()
        isLongPressed = 0
        setNotes(list)
    }

//    private fun makeUnClickable(){
//        println("NOTES123: Make Unclickable")
//        val list = notesList.map {
//            if(!isCheckable){
//                viewModel.updateNote(it.copy(isCheckable = false))
//            }
//            it.copy(isCheckable = false)
//
//        }.toMutableList()
//        setNotes(list)
//    }


    fun deleteDialog(context: Context){
        val builder = AlertDialog.Builder(context)

        val customView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog,null)
        val title = customView.findViewById<TextView>(R.id.dialog_title)
        val message = customView.findViewById<TextView>(R.id.dialog_message)
        title.text = "Delete Notes"
        message.text = "Delete $selectCount items?"
//        builder.setTitle("Delete Notes")
        NotesAppViewModel.selectCount.value = selectCount
//        builder.setMessage("Delete $selectCount items?")
        builder.setView(customView)
        builder.setPositiveButton(null){dialog,_->
            println("DELETE CONFIRMATION TRUE")
            NotesAppViewModel.deleteConfirmation.value = true
            dialog.dismiss()
        }
        builder.setNeutralButton(null){dialog,_->
            println("DELETE CONFIRMATION FALSE")
            NotesAppViewModel.deleteConfirmation.value = false
            dialog.dismiss()
        }
        deleteDialog = builder.create()
        deleteDialog.show()
        deleteDialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.rounded_corners))
        val pos = deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        pos?.let {
            val parent = it.parent as ViewGroup
            parent.removeView(it)
            customView.findViewById<Button>(R.id.positiveBtn).setOnClickListener {
                // Trigger the dialog's positive action
                deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
                println("DELETE CONFIRMATION TRUE")
                NotesAppViewModel.deleteConfirmation.value = true
            }
        }
        val neg = deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        neg?.let {
            val parent = it.parent as ViewGroup
            parent.removeView(it)
            customView.findViewById<Button>(R.id.negativeBtn).setOnClickListener {
                // Trigger the dialog's positive action
                deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick()
                println("DELETE CONFIRMATION FALSE")
                NotesAppViewModel.deleteConfirmation.value = false
            }
        }
    }
}