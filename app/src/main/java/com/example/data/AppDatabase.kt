package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgressFlow(): Flow<UserProgress?>

    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    suspend fun getUserProgress(): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProgress(progress: UserProgress)
}

@Dao
interface CompletedLevelDao {
    @Query("SELECT * FROM completed_levels")
    fun getCompletedLevelsFlow(): Flow<List<CompletedLevelUnit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedLevel(completedLevel: CompletedLevelUnit)
}

@Dao
interface DailyQuestDao {
    @Query("SELECT * FROM daily_quests")
    fun getDailyQuestsFlow(): Flow<List<DailyQuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<DailyQuestEntity>)

    @Query("UPDATE daily_quests SET progressCount = :progress, isCompleted = :completed WHERE questId = :questId")
    suspend fun updateQuestProgress(questId: String, progress: Int, completed: Boolean)

    @Query("UPDATE daily_quests SET isClaimed = 1 WHERE questId = :questId")
    suspend fun markQuestClaimed(questId: String)
}

@Database(
    entities = [UserProgress::class, CompletedLevelUnit::class, DailyQuestEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PianoQuestDatabase : RoomDatabase() {
    abstract fun userProgressDao(): UserProgressDao
    abstract fun completedLevelDao(): CompletedLevelDao
    abstract fun dailyQuestDao(): DailyQuestDao

    companion object {
        @Volatile
        private var INSTANCE: PianoQuestDatabase? = null

        fun getDatabase(context: Context): PianoQuestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PianoQuestDatabase::class.java,
                    "piano_quest_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
