package chon.julio.sqlite_233242


import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import chon.julio.sqlite_233242.data.NotesDatabase
import chon.julio.sqlite_233242.presentation.AddNoteScreen
import chon.julio.sqlite_233242.presentation.NotesScreen
import chon.julio.sqlite_233242.presentation.NotesViewModel
import chon.julio.sqlite_233242.ui.theme.RoomDatabaseTheme

class MainActivity : ComponentActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            NotesDatabase::class.java,
            "notes.db"
        ).build()
    }

    private val viewModel by viewModels<NotesViewModel> (
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun<T: ViewModel> create(modelClass: Class<T>): T {
                    return NotesViewModel(database.dao) as T
                }
            }
        }
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        viewModel.showDialogLiveData.observe(this) { shouldShowDialog ->
            if (shouldShowDialog) {
                // Muestra el diálogo aquí
                // Puedes usar un DialogFragment o AlertDialog
                showDialog()
                // Restablece el estado en el ViewModel
                viewModel.showDialogLiveData.value = false
            }
        }
        setContent {
            RoomDatabaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val state by viewModel.state.collectAsState()
                    val navController = rememberNavController()

                    NavHost(navController= navController, startDestination = "NotesScreen") {
                        composable("NotesScreen") {
                            NotesScreen(
                                state = state,
                                navController = navController,
                                onEvent = viewModel::onEvent
                            )
                        }
                        composable("AddNoteScreen") {
                            AddNoteScreen(
                                state = state,
                                navController = navController,
                                onEvent = viewModel::onEvent
                            )
                        }
                    }

                }
            }
        }
    }
    private fun showDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Do you want to delete this note?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.confirmDeleteNote()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
