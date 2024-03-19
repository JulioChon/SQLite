package chon.julio.sqlite_233242.presentation


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chon.julio.sqlite_233242.data.Note

data class NoteState(

    val notes: List<Note> = emptyList(),
    val title: MutableState<String> = mutableStateOf(""),
    val description: MutableState<String> = mutableStateOf("")

)