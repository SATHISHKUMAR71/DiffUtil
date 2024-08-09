package com.example.samplenotesapplication.notescontentprovider

import android.net.Uri

class ContractNotes {

    companion object{
        const val AUTHORITY = "com.example.samplenotesapplication.notescontentprovider"
        const val PATH_NOTES = "notes"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH_NOTES")
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_CREATED_AT = "createdAt"
        const val COLUMN_UPDATED_AT = "updatedAt"
        const val COLUMN_IS_PINNED = "isPinned"
        const val isSelected = "isSelected"
        const val isCheckable = "isCheckable"
    }
}