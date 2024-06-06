package com.example.notes_jp.screen


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.notes_jp.model.Note
import com.example.notes_jp.viewmodel.NoteViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(navController: NavHostController, noteViewModel: NoteViewModel, noteId: Int?) {


    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val notes by noteViewModel.allNotes.collectAsState(initial = emptyList())

    LaunchedEffect(noteId, notes) {
        if (noteId != null && noteId != 0) {
            val note = notes.find { it.id == noteId }
            note?.let {
                title = it.title
                description = it.description
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == 0) "Create Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (title.isEmpty()) {
                    Toast.makeText(
                        navController.context,
                        "Title cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val note = Note(
                        id = noteId ?: 0,
                        title = title,
                        description = description
                    )
                    scope.launch {
                        if (noteId == 0) {
                            noteViewModel.insert(note)
                        } else {
                            noteViewModel.update(note)
                        }
                        navController.navigateUp()
                    }
                }
            }) {
                Icon(Icons.Default.Done, contentDescription = "Save")

            }
        }
    ) { paddingValues ->
        NoteScreenContent(
            title = title,
            onTitleChange = { title = it },
            description = description,
            onDescriptionChange = { description = it },
            paddingValues = paddingValues
        )
    }


}

@Composable
fun NoteScreenContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    paddingValues: PaddingValues
) {

    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = title,
            onValueChange = {
                onTitleChange(it)
                // Clear title error when user edits the field
                if (titleError != null) {
                    titleError = null
                }
            },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            isError = titleError != null,

            )
        TextField(
            value = description,
            onValueChange = {
                onDescriptionChange(it)
                // Clear description error when user edits the field
                if (descriptionError != null) {
                    descriptionError = null
                }
            },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            maxLines = 5,
            isError = descriptionError != null,
        )
    }


}



