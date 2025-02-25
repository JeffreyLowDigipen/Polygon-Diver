package com.example.projectpolygondiver

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Entity(tableName = "Scores")
data class PlayerScore(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val playerName: String,
    val score: Int
)

@Dao
interface PlayerScoreDao
{
    @Insert
    fun insertScore(player: PlayerScore)

    @Query("SELECT * FROM Scores ORDER BY score DESC")
    fun getScores(): List<PlayerScore>

    @Query("DELETE FROM Scores")
    fun clearScores()
}

@Database(entities = [PlayerScore::class], version = 1, exportSchema = false)
abstract class PlayerScoreDatabase : RoomDatabase()
{
    abstract fun playerScoreDao(): PlayerScoreDao

    companion object
    {
        @Volatile
        private var INSTANCE: PlayerScoreDatabase? = null

        fun getDatabase(context: Context): PlayerScoreDatabase
        {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlayerScoreDatabase::class.java,
                    "player_score_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class PlayerScoreRepository(private val playerscoredao: PlayerScoreDao)
{
    private val executer: Executor = Executors.newSingleThreadExecutor()

    fun insertScore(score: PlayerScore)
    {
        executer.execute {
            playerscoredao.insertScore(score)
        }
    }

    fun getScores(callback: (List<PlayerScore>) -> Unit)
    {
        executer.execute {
            val scores = playerscoredao.getScores()
            callback(scores)
        }
    }

    fun clearScores()
    {
        executer.execute {
            playerscoredao.clearScores()
        }
    }
}

class PlayerScoreViewModel(private val repository: PlayerScoreRepository) : ViewModel()
{
    private val _scores = MutableLiveData<List<PlayerScore>>()
    val scores: LiveData<List<PlayerScore>> get() = _scores

    init
    {
        loadScores()
    }

    fun insertScore(playerName: String, score: Int)
    {
        val player = PlayerScore(playerName = playerName, score = score)
        repository.insertScore(player)
        loadScores()
    }

    fun loadScores()
    {
        repository.getScores { scoreList ->
            _scores.postValue(scoreList)
        }
    }

    fun clearScores()
    {
        repository.clearScores()
        loadScores()
    }
}

class PlayerScoreViewModelFactory(private val repository: PlayerScoreRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(PlayerScoreViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return PlayerScoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}