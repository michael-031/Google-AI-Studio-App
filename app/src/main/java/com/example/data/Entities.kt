package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val coins: Int = 100,
    val stars: Int = 0,
    val highestUnlockedLevel: Int = 1,
    val currentStreak: Int = 1,
    val bestStreak: Int = 1,
    val lastActiveDate: String = "",
    val activeAvatarId: String = "young_bard",
    val activeSkinId: String = "classic_ivory",
    val unlockedAvatarsString: String = "young_bard",
    val unlockedSkinsString: String = "classic_ivory",
    val totalPracticeSeconds: Long = 0
) {
    fun isAvatarUnlocked(avatarId: String): Boolean {
        return unlockedAvatarsString.split(",").contains(avatarId)
    }

    fun isSkinUnlocked(skinId: String): Boolean {
        return unlockedSkinsString.split(",").contains(skinId)
    }
}

@Entity(tableName = "completed_levels")
data class CompletedLevelUnit(
    @PrimaryKey val levelIndex: Int,
    val stars: Int,
    val score: Int,
    val accuracy: Float,
    val completedDate: String
)

@Entity(tableName = "daily_quests")
data class DailyQuestEntity(
    @PrimaryKey val questId: String,
    val description: String,
    val targetCount: Int,
    val progressCount: Int,
    val isCompleted: Boolean,
    val isClaimed: Boolean,
    val rewardXp: Int,
    val rewardCoins: Int
)
