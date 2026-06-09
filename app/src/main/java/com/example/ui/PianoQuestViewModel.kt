package com.example.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AudioSynth
import com.example.data.CompletedLevelUnit
import com.example.data.DailyQuestEntity
import com.example.data.Level
import com.example.data.LevelCurriculum
import com.example.data.PianoQuestDatabase
import com.example.data.UserProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PianoQuestViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PianoQuestDatabase.getDatabase(application)
    private val progressDao = db.userProgressDao()
    private val completedLevelDao = db.completedLevelDao()
    private val dailyQuestDao = db.dailyQuestDao()

    // UI state flows
    private val _userProgress = MutableStateFlow<UserProgress>(UserProgress())
    val userProgress: StateFlow<UserProgress> = _userProgress.asStateFlow()

    private val _completedLevels = MutableStateFlow<List<CompletedLevelUnit>>(emptyList())
    val completedLevels: StateFlow<List<CompletedLevelUnit>> = _completedLevels.asStateFlow()

    private val _dailyQuests = MutableStateFlow<List<DailyQuestEntity>>(emptyList())
    val dailyQuests: StateFlow<List<DailyQuestEntity>> = _dailyQuests.asStateFlow()

    // ACTIVE LESSON GAMEPLAY STATE
    private val _activeLevel = MutableStateFlow<Level?>(null)
    val activeLevel: StateFlow<Level?> = _activeLevel.asStateFlow()

    private val _currentPlayIndex = MutableStateFlow(0)
    val currentPlayIndex: StateFlow<Int> = _currentPlayIndex.asStateFlow()

    private val _wrongNoteTrigger = MutableStateFlow<Int?>(null) // Midi key to flash as red error
    val wrongNoteTrigger: StateFlow<Int?> = _wrongNoteTrigger.asStateFlow()

    private val _correctNoteTrigger = MutableStateFlow<Int?>(null) // Midi key to flash gold / green
    val correctNoteTrigger: StateFlow<Int?> = _correctNoteTrigger.asStateFlow()

    // Lesson evaluation metrics
    private val _lessonAccuracy = MutableStateFlow(100f)
    val lessonAccuracy: StateFlow<Float> = _lessonAccuracy.asStateFlow()

    private val _totalLessonNotes = MutableStateFlow(0)
    private val _correctLessonNotes = MutableStateFlow(0)

    private val _isLessonComplete = MutableStateFlow(false)
    val isLessonComplete: StateFlow<Boolean> = _isLessonComplete.asStateFlow()

    private val _lessonEvaluationGrade = MutableStateFlow("S") // S, A, B, C
    val lessonEvaluationGrade: StateFlow<String> = _lessonEvaluationGrade.asStateFlow()

    private val _coinsAwarded = MutableStateFlow(0)
    val coinsAwarded: StateFlow<Int> = _coinsAwarded.asStateFlow()

    private val _xpAwarded = MutableStateFlow(0)
    val xpAwarded: StateFlow<Int> = _xpAwarded.asStateFlow()

    private val _starsAwarded = MutableStateFlow(0)
    val starsAwarded: StateFlow<Int> = _starsAwarded.asStateFlow()

    // ADAPTIVE TEMPO / HINTS
    private val _tempoMultiplier = MutableStateFlow(1f)
    val tempoMultiplier: StateFlow<Float> = _tempoMultiplier.asStateFlow()

    private val _isHintActive = MutableStateFlow(false)
    val isHintActive: StateFlow<Boolean> = _isHintActive.asStateFlow()

    private var failCountThisLesson = 0

    init {
        // Observe database updates
        viewModelScope.launch(Dispatchers.IO) {
            progressDao.getUserProgressFlow().collectLatest { progress ->
                if (progress == null) {
                    // Initialize default progress
                    val dfProgress = UserProgress()
                    progressDao.saveUserProgress(dfProgress)
                    _userProgress.value = dfProgress
                } else {
                    _userProgress.value = progress
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            completedLevelDao.getCompletedLevelsFlow().collectLatest { list ->
                _completedLevels.value = list
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            dailyQuestDao.getDailyQuestsFlow().collectLatest { list ->
                if (list.isEmpty()) {
                    val defaultQuests = listOf(
                        DailyQuestEntity("quest_notes", "Play 15 correct notes", 15, 0, false, false, 50, 40),
                        DailyQuestEntity("quest_lesson", "Complete 1 piano lesson", 1, 0, false, false, 80, 50),
                        DailyQuestEntity("quest_xp", "Gain 100 XP", 100, 0, false, false, 100, 80)
                    )
                    dailyQuestDao.insertQuests(defaultQuests)
                    _dailyQuests.value = defaultQuests
                } else {
                    _dailyQuests.value = list
                }
            }
        }

        // Setup streak check on launch
        viewModelScope.launch {
            checkAndRefreshStreak()
        }
    }

    // STREAK MANAGEMENT
    private fun getTodayString(): String {
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return df.format(Calendar.getInstance().time)
    }

    private suspend fun checkAndRefreshStreak() {
        val progress = progressDao.getUserProgress() ?: return
        val today = getTodayString()
        val lastDate = progress.lastActiveDate

        if (lastDate.isEmpty()) {
            progressDao.saveUserProgress(progress.copy(lastActiveDate = today, currentStreak = 1))
            return
        }

        if (lastDate == today) {
            // Already active today
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val d1 = sdf.parse(lastDate)
            val d2 = sdf.parse(today)
            if (d1 != null && d2 != null) {
                val diffMills = d2.time - d1.time
                val diffDays = diffMills / (1000 * 60 * 60 * 24)

                val newStreak: Int
                val newBestStreak: Int
                if (diffDays == 1L) {
                    // Yesterday was active => increment streak
                    newStreak = progress.currentStreak + 1
                    newBestStreak = maxOf(newStreak, progress.bestStreak)
                } else if (diffDays > 1L) {
                    // Missed days => reset streak
                    newStreak = 1
                    newBestStreak = progress.bestStreak
                } else {
                    newStreak = progress.currentStreak
                    newBestStreak = progress.bestStreak
                }

                progressDao.saveUserProgress(
                    progress.copy(
                        lastActiveDate = today,
                        currentStreak = newStreak,
                        bestStreak = newBestStreak
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // GAMEPLAY CONTROLLER
    fun startLesson(level: Level) {
        _activeLevel.value = level
        _currentPlayIndex.value = 0
        _wrongNoteTrigger.value = null
        _correctNoteTrigger.value = null
        _isLessonComplete.value = false
        _lessonAccuracy.value = 100f
        _totalLessonNotes.value = 0
        _correctLessonNotes.value = 0
        _tempoMultiplier.value = 1f
        _isHintActive.value = false
        failCountThisLesson = 0
    }

    fun playLocalSound(midiNote: Int) {
        AudioSynth.playNote(midiNote)
    }

    fun onPianoKeyPress(midiNote: Int) {
        val level = _activeLevel.value ?: return
        if (_isLessonComplete.value) return

        _totalLessonNotes.value += 1
        val isChordLevel = level.chords != null

        // Check if correct key
        if (isChordLevel) {
            // Standard chord check: does the chord containing this midi note exist?
            val activeChordIndex = _currentPlayIndex.value
            val chordsList = level.chords ?: return
            if (activeChordIndex < chordsList.size) {
                val targetChordNotes = chordsList[activeChordIndex]
                if (targetChordNotes.contains(midiNote)) {
                    // Correct chord note trigger
                    handleCorrectKeyPress(midiNote)
                    // Trigger sound synthetically
                    AudioSynth.playNote(midiNote)

                    // If they explored all chord keys, advance index
                    _correctLessonNotes.value += 1
                    _currentPlayIndex.value += 1
                    if (_currentPlayIndex.value >= chordsList.size) {
                        endLessonWithSuccess()
                    }
                } else {
                    handleWrongKeyPress(midiNote)
                }
            }
        } else {
            // Standard melody note check
            val targetSequence = level.targetSequence
            val activeIndex = _currentPlayIndex.value
            if (activeIndex < targetSequence.size) {
                val expectedNote = targetSequence[activeIndex]
                if (midiNote == expectedNote) {
                    handleCorrectKeyPress(midiNote)
                    AudioSynth.playNote(midiNote)

                    _correctLessonNotes.value += 1
                    _currentPlayIndex.value += 1

                    // Update Play Notes Daily Quest!
                    incrementQuestProgress("quest_notes", 1)

                    if (_currentPlayIndex.value >= targetSequence.size) {
                        endLessonWithSuccess()
                    }
                } else {
                    handleWrongKeyPress(midiNote)
                }
            }
        }

        recalculateAccuracy()
    }

    private fun handleCorrectKeyPress(midiNote: Int) {
        _correctNoteTrigger.value = midiNote
        _wrongNoteTrigger.value = null
        viewModelScope.launch {
            delay(400)
            if (_correctNoteTrigger.value == midiNote) {
                _correctNoteTrigger.value = null
            }
        }
    }

    private fun handleWrongKeyPress(midiNote: Int) {
        _wrongNoteTrigger.value = midiNote
        _correctNoteTrigger.value = null
        failCountThisLesson++

        // Adaptive Help system!
        if (failCountThisLesson >= 3) {
            _isHintActive.value = true
            _tempoMultiplier.value = 0.75f // slow tempo
        }

        viewModelScope.launch {
            delay(400)
            if (_wrongNoteTrigger.value == midiNote) {
                _wrongNoteTrigger.value = null
            }
        }
    }

    private fun recalculateAccuracy() {
        val total = _totalLessonNotes.value
        val correct = _correctLessonNotes.value
        if (total > 0) {
            val rawAcc = (correct.toFloat() / total.toFloat()) * 100f
            _lessonAccuracy.value = rawAcc.coerceIn(0f, 100f)
        } else {
            _lessonAccuracy.value = 100f
        }
    }

    private fun endLessonWithSuccess() {
        val level = _activeLevel.value ?: return
        _isLessonComplete.value = true

        val accuracy = _lessonAccuracy.value
        val stars = when {
            accuracy >= 95f -> 3
            accuracy >= 75f -> 2
            else -> 1
        }
        val grade = when {
            accuracy >= 98f -> "S"
            accuracy >= 85f -> "A"
            accuracy >= 65f -> "B"
            else -> "C"
        }
        
        _lessonEvaluationGrade.value = grade
        _starsAwarded.value = stars

        val baseXp = if (level.isBoss) 150 else 60
        val baseCoins = if (level.isBoss) 100 else 40

        // Scale by accuracy
        val awardXp = (baseXp * (accuracy / 100f)).toInt().coerceIn(10, 200)
        val awardCoins = (baseCoins * (accuracy / 100f)).toInt().coerceIn(5, 150)

        _xpAwarded.value = awardXp
        _coinsAwarded.value = awardCoins

        viewModelScope.launch(Dispatchers.IO) {
            // Save level completion stats
            val model = CompletedLevelUnit(
                levelIndex = level.index,
                stars = stars,
                score = (accuracy * 10).toInt(),
                accuracy = accuracy,
                completedDate = getTodayString()
            )
            completedLevelDao.insertCompletedLevel(model)

            // Update entire user profile
            val curProgress = progressDao.getUserProgress() ?: UserProgress()
            
            val nextUnlocked = if (level.index == curProgress.highestUnlockedLevel) {
                (level.index + 1).coerceAtMost(LevelCurriculum.LEVELS.size)
            } else {
                curProgress.highestUnlockedLevel
            }

            // Accumulate global stars
            val existingCompletions = _completedLevels.value
            val starsDiffOfThisLevel = stars - (existingCompletions.find { it.levelIndex == level.index }?.stars ?: 0)
            val newTotalStars = (curProgress.stars + starsDiffOfThisLevel.coerceAtLeast(0)).coerceAtLeast(0)

            val updatedProgress = curProgress.copy(
                xp = curProgress.xp + awardXp,
                coins = curProgress.coins + awardCoins,
                stars = newTotalStars,
                highestUnlockedLevel = nextUnlocked,
                totalPracticeSeconds = curProgress.totalPracticeSeconds + 120 // simulated 2 min practice
            )
            progressDao.saveUserProgress(updatedProgress)

            // Accumulate Quest markers!
            incrementQuestProgress("quest_lesson", 1)
            incrementQuestProgress("quest_xp", awardXp)
        }
    }

    fun finishLesson() {
        _activeLevel.value = null
        _isLessonComplete.value = false
    }

    // QUEST MECHANICS
    fun incrementQuestProgress(questId: String, increment: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = _dailyQuests.value
            val quest = list.find { it.questId == questId } ?: return@launch
            if (quest.isClaimed) return@launch

            val newProgress = (quest.progressCount + increment).coerceAtMost(quest.targetCount)
            val completed = newProgress >= quest.targetCount
            dailyQuestDao.updateQuestProgress(questId, newProgress, completed)
        }
    }

    fun claimQuestReward(quest: DailyQuestEntity) {
        if (!quest.isCompleted || quest.isClaimed) return

        viewModelScope.launch(Dispatchers.IO) {
            dailyQuestDao.markQuestClaimed(quest.questId)

            val currentPrg = progressDao.getUserProgress() ?: return@launch
            val updated = currentPrg.copy(
                xp = currentPrg.xp + quest.rewardXp,
                coins = currentPrg.coins + quest.rewardCoins
            )
            progressDao.saveUserProgress(updated)
        }
    }

    // SHOP MECHANICS
    fun selectAvatar(avatarId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val progress = progressDao.getUserProgress() ?: return@launch
            if (progress.isAvatarUnlocked(avatarId)) {
                progressDao.saveUserProgress(progress.copy(activeAvatarId = avatarId))
            }
        }
    }

    fun buyAvatar(avatarId: String, cost: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val progress = progressDao.getUserProgress() ?: return@launch
            if (progress.coins >= cost && !progress.isAvatarUnlocked(avatarId)) {
                val newList = progress.unlockedAvatarsString + ",$avatarId"
                progressDao.saveUserProgress(
                    progress.copy(
                        coins = progress.coins - cost,
                        unlockedAvatarsString = newList,
                        activeAvatarId = avatarId
                    )
                )
            }
        }
    }

    fun selectPianoSkin(skinId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val progress = progressDao.getUserProgress() ?: return@launch
            if (progress.isSkinUnlocked(skinId)) {
                progressDao.saveUserProgress(progress.copy(activeSkinId = skinId))
            }
        }
    }

    fun buyPianoSkin(skinId: String, cost: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val progress = progressDao.getUserProgress() ?: return@launch
            if (progress.coins >= cost && !progress.isSkinUnlocked(skinId)) {
                val newList = progress.unlockedSkinsString + ",$skinId"
                progressDao.saveUserProgress(
                    progress.copy(
                        coins = progress.coins - cost,
                        unlockedSkinsString = newList,
                        activeSkinId = skinId
                    )
                )
            }
        }
    }
}
