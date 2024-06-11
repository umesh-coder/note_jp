package com.example.notes_jp.model

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.notes_jp.database.NoteDao
import com.example.notes_jp.database.NoteDatabase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@SmallTest
class NoteDaoTest {

    private lateinit var database: NoteDatabase
    private lateinit var noteDao: NoteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        noteDao = database.noteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertNote() = runTest {
        val note = Note(
            title = "Test Title",
            description = "Test Description"
        )
        noteDao.insert(note)
        val notes = noteDao.getAllNotes().first()
        assertEquals(notes[0].title, "Test Title")
        assertEquals(notes[0].description, "Test Description")
    }

    @Test
    fun getAllNotes() = runTest {
        val note1 = Note(
            title = "Title 1",
            description = "Description 1"
        )
        val note2 = Note(
            title = "Title 2",
            description = "Description 2"
        )
        noteDao.insert(note1)
        noteDao.insert(note2)
        val notes = noteDao.getAllNotes().first().sortedBy { it.title }
        assertEquals(2, notes.size)
        assertEquals("Title 1", notes[0].title)
        assertEquals("Title 2", notes[1].title)
    }


    @Test
    fun deleteNote() = runTest {
        // Insert a note
        val note = Note(
            title = "Test Title",
            description = "Test Description"
        )
        noteDao.insert(note)

        // Delete the inserted note
        noteDao.delete(note)

        // Verify that the note is deleted
        val notes = noteDao.getAllNotes().first()
        assertEquals(0, notes.size)
    }

    @Test
    fun updateNote() = runTest {
        // Insert a note
        val note = Note(
            title = "Initial Title",
            description = "Initial Description"
        )
        noteDao.insert(note)

        // Update the inserted note
        val updatedNote = note.copy(
            title = "Updated Title",
            description = "Updated Description"
        )
        noteDao.update(updatedNote)

        // Verify that the note is updated
        val updatedNoteFromDb = noteDao.getNoteById(note.id)
        Assert.assertNotNull(updatedNoteFromDb)
        assertEquals("Updated Title", updatedNoteFromDb!!.title)
        assertEquals("Updated Description", updatedNoteFromDb.description)
    }



}
