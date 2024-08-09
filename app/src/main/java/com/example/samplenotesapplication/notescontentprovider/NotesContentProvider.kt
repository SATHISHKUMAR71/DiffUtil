package com.example.samplenotesapplication.notescontentprovider

import android.app.Application
import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.model.NoteDao
import com.example.samplenotesapplication.model.NotesDatabase
import com.example.samplenotesapplication.repository.NoteRepository
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel
import com.example.samplenotesapplication.viewmodel.NotesViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class NotesContentProvider : ContentProvider() {


    private lateinit var noteDao:NoteDao
    companion object{
        const val AUTHORITY = ContractNotes.AUTHORITY
        const val PATH_NOTES = ContractNotes.PATH_NOTES
        val CONTENT_URI: Uri = ContractNotes.CONTENT_URI
        private const val NOTES =1
        private const val NOTES_ID =2
        private const val NOTES_COUNT = 3
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, PATH_NOTES, NOTES)
            addURI(AUTHORITY, "$PATH_NOTES/#", NOTES_ID)
            addURI(AUTHORITY, "$PATH_NOTES/getNotesCount", NOTES_COUNT)
        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(): Boolean {
        val context = context?:return false
        val db = NotesDatabase.getNoteDatabase(context)
        noteDao = db.getNoteDao()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor:Cursor = when(uriMatcher.match(uri)){

            NOTES ->{
                noteDao.getAllNotesCursor(SimpleSQLiteQuery("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC"))
            }
            NOTES_ID -> {
                val id = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid URI")
                noteDao.getAllNoteById(SimpleSQLiteQuery("SELECT * FROM notes WHERE id=$id ORDER BY isPinned DESC, updatedAt DESC"))
            }
            NOTES_COUNT -> {
                noteDao.getNotesCount(SimpleSQLiteQuery("SELECT COUNT(*) FROM notes"))
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        return cursor
    }

    override fun getType(uri: Uri): String {
        return when(uriMatcher.match(uri)){
            NOTES -> "${ContentResolver.CURSOR_DIR_BASE_TYPE}/vnd.$AUTHORITY.$PATH_NOTES"
            NOTES_ID -> "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/vnd.$AUTHORITY.$PATH_NOTES"
            NOTES_COUNT -> "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/vnd.$AUTHORITY.$PATH_NOTES.notesCount"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
//        val id = db.insert(DBHelper.TABLE_NAME,null,contentValues)
        val id = noteDao.insertNoteNormal(Note(
            id = contentValues?.getAsInteger(ContractNotes.COLUMN_ID)?:0,
            content = contentValues?.getAsString(ContractNotes.COLUMN_CONTENT)?:"",
            createdAt = contentValues?.getAsString(ContractNotes.COLUMN_CREATED_AT)?:"",
            isSelected = contentValues?.getAsBoolean(ContractNotes.isSelected)?:false,
            isCheckable = contentValues?.getAsBoolean(ContractNotes.isCheckable)?:false,
            isPinned = contentValues?.getAsInteger(ContractNotes.COLUMN_IS_PINNED)?:0,
            title = contentValues?.getAsString(ContractNotes.COLUMN_TITLE)?:"",
            updatedAt = contentValues?.getAsString(ContractNotes.COLUMN_UPDATED_AT)?:"",
            isHighlighted = false
        ))
        context?.contentResolver?.notifyChange(uri,null)
        return Uri.withAppendedPath(CONTENT_URI,id.toString())
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {

        val count: Int
        when (uriMatcher.match(uri)) {
            NOTES ->  throw IllegalArgumentException("Unknown URI, can't delete item without ID: $uri")
            NOTES_ID -> {
                val id = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid URI")
                noteDao.deleteNoteById(id.toInt())
                count = 1
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {

        var count = 0
        when(uriMatcher.match(uri)){
            NOTES -> throw IllegalArgumentException("Unknown URI, can't update item without ID: $uri")
            NOTES_ID -> {
                    val id = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid URI: $uri")
                    noteDao.insertNoteNormal(Note(
                        id = contentValues?.getAsInteger(ContractNotes.COLUMN_ID)?:0,
                        content = contentValues?.getAsString(ContractNotes.COLUMN_CONTENT)?:"",
                        createdAt = contentValues?.getAsString(ContractNotes.COLUMN_CREATED_AT)?:"",
                        isSelected = contentValues?.getAsBoolean(ContractNotes.isSelected)?:false,
                        isCheckable = contentValues?.getAsBoolean(ContractNotes.isCheckable)?:false,
                        isPinned = contentValues?.getAsInteger(ContractNotes.COLUMN_IS_PINNED)?:0,
                        title = contentValues?.getAsString(ContractNotes.COLUMN_TITLE)?:"",
                        updatedAt = contentValues?.getAsString(ContractNotes.COLUMN_UPDATED_AT)?:"",
                        isHighlighted = false
                    ))
                count =1
            }
            else -> throw IllegalArgumentException("Invalid URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri,null)
        return count
    }

}