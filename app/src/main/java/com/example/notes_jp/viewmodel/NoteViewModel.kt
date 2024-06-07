package com.example.notes_jp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notes_jp.model.Note
import com.example.notes_jp.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _allNotes = MutableStateFlow<List<Note>>(emptyList())
    val allNotes: StateFlow<List<Note>> get() = _allNotes

    private val _filteredNotes = MutableStateFlow<List<Note>>(emptyList())
    val filteredNotes: StateFlow<List<Note>> get() = _filteredNotes

    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> get() = _currentNote

    private var _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title


    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            repository.allNotes.collect { notes ->
                _allNotes.value = notes
                _filteredNotes.value = notes
            }
        }
    }


    fun loadNoteById(noteId: Int) {
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            _currentNote.value = note
        }
    }

    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
        loadNotes()
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
        loadNotes()
    }

    fun delete(note: Note) = viewModelScope.launch {
        repository.delete(note)
        loadNotes()
    }

    fun searchNotes(query: String) {
        viewModelScope.launch {
            repository.searchNotes(query).collect { notes ->
                _filteredNotes.value = notes
            }
        }
    }

    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }


}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
