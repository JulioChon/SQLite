package chon.julio.sqlite_233242.data
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [Note::class],
    version = 1
)
abstract class NotesDatabase: RoomDatabase(){
    abstract val dao: NoteDao
}