package com.example.notes_jp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes_jp.database.NoteDatabase
import com.example.notes_jp.repository.NoteRepository
import com.example.notes_jp.screen.NoteListScreen
import com.example.notes_jp.screen.NoteScreen
import com.example.notes_jp.ui.theme.Notes_JPTheme
import com.example.notes_jp.viewmodel.NoteViewModel
import com.example.notes_jp.viewmodel.NoteViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = NoteDatabase.getDatabase(this)
        val repository = NoteRepository(database.noteDao())
        val viewModelFactory = NoteViewModelFactory(repository)
        noteViewModel = ViewModelProvider(this, viewModelFactory).get(NoteViewModel::class.java)
        enableEdgeToEdge()
        setContent {
            Notes_JPTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "note_list"
                    ) {
                        composable("note_list") {
                            NoteListScreen(navController, noteViewModel)
                        }
                        composable("note/{noteId}") { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
                            NoteScreen(navController, noteViewModel, noteId)
                        }
                    }
                }

            }
        }
    }
}

