package chon.julio.sqlite_233242.presentation
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chon.julio.sqlite_233242.data.Note
import chon.julio.sqlite_233242.data.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesViewModel(
    private val dao: NoteDao
) : ViewModel() {

    private val isSortedByDateAdded = MutableStateFlow(true)
    public val showDialogLiveData = MutableLiveData<Boolean>()
    private var noteToDelete: Note? = null


    private var notes =
        isSortedByDateAdded.flatMapLatest { sort ->
            if (sort) {
                dao.getNotesOrderdByDateAdded()
            } else {
                dao.getNotesOrderdByTitle()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val _state = MutableStateFlow(NoteState())
    val state =
        combine(_state, isSortedByDateAdded, notes) { state, isSortedByDateAdded, notes ->
            state.copy(
                notes = notes
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteState())

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    showDialogLiveData.value = true
                    noteToDelete = event.note

                }
            }

            is NotesEvent.SaveNote -> {

                val title = state.value.title.value.trim()
                val description = state.value.description.value.trim()


                if (title.isNotEmpty() && description.isNotEmpty()) {

                    val note = Note(
                        title = title,
                        description = description,
                        dateAdded = System.currentTimeMillis()
                    )

                    viewModelScope.launch {
                        dao.upsertNote(note)
                    }


                    _state.update {
                        it.copy(
                            title = mutableStateOf(""),
                            description = mutableStateOf("")
                        )
                    }
                } else {

                }
            }

            NotesEvent.SortNotes -> {
                isSortedByDateAdded.value = !isSortedByDateAdded.value
            }
        }
    }

    fun confirmDeleteNote() {

        viewModelScope.launch {
            noteToDelete?.let { note ->
                dao.deleteNote(note)
            }
        }
    }



}