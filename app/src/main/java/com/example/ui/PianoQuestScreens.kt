package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.data.AudioSynth
import com.example.data.CompletedLevelUnit
import com.example.data.DailyQuestEntity
import com.example.data.Level
import com.example.data.LevelCurriculum
import com.example.data.UserProgress
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

// ROUTES
const val ROUTE_MAP = "quest_map"
const val ROUTE_DASHBOARD = "dashboard"
const val ROUTE_PRACTICE = "free_practice"
const val ROUTE_SHOP = "shop"

// COLOR SCHEME COMBOS BY SKIN
data class SkinColors(
    val boardBg: Color,
    val primaryKeyGlow: Color,
    val whiteKeyBg: Color,
    val blackKeyBg: Color
)

val SKINS_MAP = mapOf(
    "classic_ivory" to SkinColors(
        boardBg = Color(0xFF1E213A),
        primaryKeyGlow = Color(0xFF3F51B5),
        whiteKeyBg = Color(0xFFFFFFFF),
        blackKeyBg = Color(0xFF11121A)
    ),
    "emerald_forest" to SkinColors(
        boardBg = Color(0xFF0F3023),
        primaryKeyGlow = Color(0xFF00C853),
        whiteKeyBg = Color(0xFFF1F8E9),
        blackKeyBg = Color(0xFF1B4D3E)
    ),
    "castle_obsidian" to SkinColors(
        boardBg = Color(0xFF1B1B1B),
        primaryKeyGlow = Color(0xFFFF3D00),
        whiteKeyBg = Color(0xFFE0E0E0),
        blackKeyBg = Color(0xFF263238)
    ),
    "golden_majesty" to SkinColors(
        boardBg = Color(0xFF3E2723),
        primaryKeyGlow = Color(0xFFFFD600),
        whiteKeyBg = Color(0xFFFFFDE7),
        blackKeyBg = Color(0xFF5D4037)
    )
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PianoQuestApp(viewModel: PianoQuestViewModel) {
    val userProgress by viewModel.userProgress.collectAsState()
    val completedLevels by viewModel.completedLevels.collectAsState()
    val dailyQuests by viewModel.dailyQuests.collectAsState()
    val activeLevel by viewModel.activeLevel.collectAsState()

    var currentRoute by remember { mutableStateOf(ROUTE_MAP) }

    Scaffold(
        bottomBar = {
            if (activeLevel == null) {
                NavigationBar(
                    containerColor = Color(0xFF131524),
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentRoute == ROUTE_MAP,
                        onClick = { currentRoute = ROUTE_MAP },
                        icon = { Icon(Icons.Filled.Map, contentDescription = "Quest Map") },
                        label = { Text("Quest Map", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFFD600),
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = Color(0xFFFFD600),
                            indicatorColor = Color(0xFF232845)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == ROUTE_DASHBOARD,
                        onClick = { currentRoute = ROUTE_DASHBOARD },
                        icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Progress") },
                        label = { Text("Daily Progress", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFFD600),
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = Color(0xFFFFD600),
                            indicatorColor = Color(0xFF232845)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == ROUTE_PRACTICE,
                        onClick = { currentRoute = ROUTE_PRACTICE },
                        icon = { Icon(Icons.Filled.Piano, contentDescription = "Free Play") },
                        label = { Text("Practice Keyboard", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFFD600),
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = Color(0xFFFFD600),
                            indicatorColor = Color(0xFF232845)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == ROUTE_SHOP,
                        onClick = { currentRoute = ROUTE_SHOP },
                        icon = { Icon(Icons.Filled.Storefront, contentDescription = "Shop") },
                        label = { Text("Quest Shop", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFFD600),
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = Color(0xFFFFD600),
                            indicatorColor = Color(0xFF232845)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF04060F), Color(0xFF0E122B))
                    )
                )
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeLevel,
                transitionSpec = {
                    slideInVertically(initialOffsetY = { it }) with slideOutVertically(targetOffsetY = { it })
                }
            ) { targetLevel ->
                if (targetLevel != null) {
                    LessonPlayScreen(viewModel = viewModel, level = targetLevel)
                } else {
                    when (currentRoute) {
                        ROUTE_MAP -> WorldMapScreen(
                            viewModel = viewModel,
                            userProgress = userProgress,
                            completedLevels = completedLevels
                        )
                        ROUTE_DASHBOARD -> DashboardScreen(
                            viewModel = viewModel,
                            userProgress = userProgress,
                            completedLevels = completedLevels,
                            dailyQuests = dailyQuests
                        )
                        ROUTE_PRACTICE -> PracticeKeyboardScreen(viewModel = viewModel, userProgress = userProgress)
                        ROUTE_SHOP -> ShopScreen(viewModel = viewModel, userProgress = userProgress)
                    }
                }
            }
        }
    }
}

// 1. ADVENTURE MAP SCREEN
@Composable
fun WorldMapScreen(
    viewModel: PianoQuestViewModel,
    userProgress: UserProgress,
    completedLevels: List<CompletedLevelUnit>
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Scroll to the user's highest unlocked level node on first load
    LaunchedEffect(userProgress.highestUnlockedLevel) {
        scope.launch {
            val targetScroll = (userProgress.highestUnlockedLevel - 1) * 120
            scrollState.animateScrollTo(targetScroll.coerceAtLeast(0))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP HEADER HUB
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFF13172E), RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // AVATAR DISPLAY WITH XP PROGRESS BAR
                Box(contentAlignment = Alignment.BottomCenter) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2C3258)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getAvatarIcon(userProgress.activeAvatarId),
                            contentDescription = "Avatar",
                            tint = Color(0xFFFFD600),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = getAvatarName(userProgress.activeAvatarId),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "XP: ${userProgress.xp}",
                        color = Color(0xFFFFD605),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            // STATS CORNER
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.GeneratingTokens, contentDescription = "Coins", tint = Color(0xFFFFC107), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${userProgress.coins}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = "Stars", tint = Color(0xFFFFEA00), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${userProgress.stars}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocalFireDepartment, contentDescription = "Streak", tint = Color(0xFFFF5722), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${userProgress.currentStreak}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        // SCROLLABLE FANTASY QUEST MAP
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎹 QUEST ADVENTURE ⚔️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Render each World and their levels in sequence
                LevelCurriculum.WORLDS.forEach { (worldName, worldLabel) ->
                    val worldId = worldLabel.split(" ").lastOrNull()?.toInt() ?: 1
                    val worldLevels = LevelCurriculum.getLevelsForWorld(worldId)

                    // Card representing the World Region
                    Card(
                        colors = CardDefaults.cardColors(containerColor = getWorldThemeColor(worldId).copy(alpha = 0.15f)),
                        border = BorderStroke(1.5.dp, getWorldThemeColor(worldId)),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = worldLabel.uppercase(),
                                color = getWorldThemeColor(worldId),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = worldName,
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Render levels with a gorgeous alternating layout path!
                            worldLevels.forEachIndexed { insideIdx, level ->
                                val isUnlocked = level.index <= userProgress.highestUnlockedLevel
                                val compInfo = completedLevels.find { it.levelIndex == level.index }
                                val starCount = compInfo?.stars ?: 0

                                val alignment = when (insideIdx % 3) {
                                    0 -> Alignment.Start
                                    1 -> Alignment.CenterHorizontally
                                    else -> Alignment.End
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalAlignment = alignment
                                ) {
                                    LevelMapNode(
                                        level = level,
                                        isUnlocked = isUnlocked,
                                        starCount = starCount,
                                        worldThemeColor = getWorldThemeColor(worldId),
                                        onSelect = { viewModel.startLesson(level) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LevelMapNode(
    level: Level,
    isUnlocked: Boolean,
    starCount: Int,
    worldThemeColor: Color,
    onSelect: () -> Unit
) {
    val scalePulse by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = if (isUnlocked && starCount == 0) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(130.dp)
            .clickable(enabled = isUnlocked) { onSelect() }
            .padding(4.dp)
    ) {
        val nodeBg = if (isUnlocked) {
            Brush.sweepGradient(listOf(worldThemeColor.copy(alpha = 0.8f), worldThemeColor))
        } else {
            Brush.sweepGradient(listOf(Color(0xFF35394B), Color(0xFF262938)))
        }

        Box(
            modifier = Modifier
                .size(76.dp * scalePulse)
                .clip(CircleShape)
                .background(nodeBg)
                .border(2.dp, if (isUnlocked) Color.White else Color.Transparent, CircleShape)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (level.isBoss) {
                    Icon(
                        imageVector = Icons.Filled.LocalActivity,
                        contentDescription = "Boss Flight",
                        tint = Color(0xFFFF1744),
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        imageVector = if (level.showTrebleStaff) Icons.Filled.MusicNote else Icons.Filled.Piano,
                        contentDescription = "Lesson",
                        tint = if (isUnlocked) Color.White else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Stars Indicator Above
                if (isUnlocked) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        repeat(3) { i ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (i < starCount) Color(0xFFFFD600) else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Lvl ${level.index}: ${level.title}",
            color = if (isUnlocked) Color.White else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

// 2. DAILY PROGRESS / ANALYTICS DASHBOARD SCREEN
@Composable
fun DashboardScreen(
    viewModel: PianoQuestViewModel,
    userProgress: UserProgress,
    completedLevels: List<CompletedLevelUnit>,
    dailyQuests: List<DailyQuestEntity>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "⚔️ HERO STATUS & DAILY TRACKER",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

        // STREAK PANEL & WEEKLY PROGRESS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13172E)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF3D00).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = "Active Streak",
                            tint = Color(0xFFFFFF00),
                            modifier = Modifier.size(52.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "${userProgress.currentStreak} Day Practice Streak!",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Best Streak: ${userProgress.bestStreak} Days",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        // Progress bar simulated days
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(7) { day ->
                                val isActive = day < userProgress.currentStreak
                                Box(
                                    modifier = Modifier
                                        .size(width = 24.dp, height = 8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isActive) Color(0xFFFF5722) else Color(0xFF333333))
                                )
                            }
                        }
                    }
                }
            }
        }

        // DAILY QUESTS MODULE (Duolingo style)
        item {
            Text(
                text = "Daily Quests",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(dailyQuests) { quest ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E213E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(quest.description, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Custom quest progress meter
                        val ratio = if (quest.targetCount > 0) quest.progressCount.toFloat() / quest.targetCount else 0f
                        Column {
                            LinearProgressIndicator(
                                progress = ratio,
                                color = Color(0xFF4CAF50),
                                trackColor = Color(0xFF424242),
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${quest.progressCount} / ${quest.targetCount}",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (quest.isClaimed) {
                        Text("Claimed ✓", color = Color.Gray, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    } else {
                        Button(
                            onClick = { viewModel.claimQuestReward(quest) },
                            enabled = quest.isCompleted,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFC107),
                                disabledContainerColor = Color(0xFF424242)
                            )
                        ) {
                            Text(
                                text = "Claim",
                                color = if (quest.isCompleted) Color(0xFF0D0D14) else Color.Gray,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // STATS ANALYTICS: ACCURACY TRENDS DRAWN WITH CANVAS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13172E)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "📈 Performance Analytics",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Your average playing accuracy across lessons",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Draw a canvas line graph!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 3.dp.toPx()
                            val width = size.width
                            val height = size.height

                            // Draw horizontal grid lines
                            drawLine(Color.Gray.copy(alpha = 0.2f), Offset(0f, height * 0.2f), Offset(width, height * 0.2f))
                            drawLine(Color.Gray.copy(alpha = 0.2f), Offset(0f, height * 0.5f), Offset(width, height * 0.5f))
                            drawLine(Color.Gray.copy(alpha = 0.2f), Offset(0f, height * 0.8f), Offset(width, height * 0.8f))

                            // Calculate points
                            val defaultAccuracies = listOf(80f, 90f, 85f, 95f, 100f)
                            val displayAcc = if (completedLevels.isNotEmpty()) {
                                completedLevels.takeLast(7).map { it.accuracy }
                            } else {
                                defaultAccuracies
                            }

                            val stepX = width / (displayAcc.size - 1).coerceAtLeast(1)
                            val path = Path()

                            displayAcc.forEachIndexed { idx, acc ->
                                // Accuracy is (0 - 100). Invert on canvas coordinate
                                val yNormalized = 1f - (acc / 100f)
                                val x = idx * stepX
                                val y = yNormalized * height * 0.8f + height * 0.1f

                                if (idx == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }

                                // Dot
                                drawCircle(Color(0xFFFFD600), radius = 5.dp.toPx(), Offset(x, y))
                            }

                            drawPath(
                                path = path,
                                color = Color(0xFF00E676),
                                style = Stroke(width = strokeWidth)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("World 1", color = Color.Gray, fontSize = 11.sp)
                        Text("Current Level Progress", color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// 3. FREE PRACTICE KEYBOARD SCREEN
@Composable
fun PracticeKeyboardScreen(viewModel: PianoQuestViewModel, userProgress: UserProgress) {
    var activeOctave by remember { mutableStateOf(4) } // MIDI Octave start (C4, C5 etc.)
    var showLabels by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "🏰 MAGIC FREE PRACTICE BOARD",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "No life bar, no penalties. Explore chords, practice scales, or try free improvising around fantasy notes.",
                color = Color.LightGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // KEYBOARD CONTROL OVERLAYS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Octave Selector
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Keyboard Octave: ", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledTonalButton(
                        onClick = { if (activeOctave > 3) activeOctave-- },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Text("-")
                    }
                    Text("C$activeOctave", color = Color(0xFFFFD600), fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 12.dp))
                    FilledTonalButton(
                        onClick = { if (activeOctave < 5) activeOctave++ },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Text("+")
                    }
                }

                // Show Labels Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Key Labels ", color = Color.White, fontSize = 12.sp)
                    Switch(
                        checked = showLabels,
                        onCheckedChange = { showLabels = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD600))
                    )
                }
            }

            // Quick chord templates
            Spacer(modifier = Modifier.height(12.dp))
            Text("Chords Cheat Sheet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "C Major" to listOf(60, 64, 67),
                    "F Major" to listOf(65, 69, 72),
                    "G Major" to listOf(67, 71, 74),
                    "A Minor" to listOf(57, 60, 64)
                ).forEach { (name, notes) ->
                    // Adjust keys according to scale
                    val adjustedNotes = notes.map { it + (activeOctave - 4) * 12 }
                    OutlinedButton(
                        onClick = { AudioSynth.playChord(adjustedNotes) },
                        border = BorderStroke(1.dp, Color(0xFFFFD600)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(name, fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }

        // FULL PIANO LAYOUT BOX
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color(0xFF15182D), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            VirtualPianoBoard(
                startMidiNote = 48 + (activeOctave - 3) * 12, // start on selected octave C
                showLabels = showLabels,
                wrongKey = null,
                correctKey = null,
                skinId = userProgress.activeSkinId,
                onKeySelect = { midi -> AudioSynth.playNote(midi) }
            )
        }
    }
}

// 4. QUEST SHOP SCREEN (Avatars and Keyboard presets)
@Composable
fun ShopScreen(viewModel: PianoQuestViewModel, userProgress: UserProgress) {
    var selectedCategory by remember { mutableStateOf("avatars") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "👑 ROYAL QUEST BAZAAR",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            // Coin Counter
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2413)),
                border = BorderStroke(1.dp, Color(0xFFFFC107))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.GeneratingTokens, contentDescription = "Gold Coins", tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${userProgress.coins} Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        // Category selection tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("avatars" to "Character Costumes", "skins" to "Magic Piano Skins").forEach { (tab, label) ->
                val isActive = tab == selectedCategory
                Button(
                    onClick = { selectedCategory = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) Color(0xFFFFD600) else Color(0xFF1F223D)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(label, color = if (isActive) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedCategory == "avatars") {
            // AVATARS LIST
            val avatars = listOf(
                AvatarData("young_bard", "Young Bard Minstrel", "Your initial default companion archetype. Melodies bloom from wooden lutes.", 0),
                AvatarData("rhythm_wizard", "Rhythm Sorcerer", "Clad in celestial robes with ticking hourglass markers.", 100),
                AvatarData("melodic_elven", "Melodic Hunter", "A crystallised elven longbow that releases sweet resonance.", 250),
                AvatarData("grandmaster_pianist", "Celestial Pianist", "An elegant tux with active stars, golden crowns, and a legendary aura.", 500)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(avatars) { item ->
                    val isOwned = userProgress.isAvatarUnlocked(item.id)
                    val isActive = userProgress.activeAvatarId == item.id

                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isActive) Color(0xFF2C3258) else Color(0xFF191C35)),
                        border = BorderStroke(1.5.dp, if (isActive) Color(0xFFFFD600) else Color.Transparent),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF13172E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    getAvatarIcon(item.id),
                                    contentDescription = item.name,
                                    tint = if (isActive) Color(0xFFFFD200) else Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(item.desc, color = Color.Gray, fontSize = 12.sp, lineHeight = 16.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            if (isOwned) {
                                Button(
                                    onClick = { viewModel.selectAvatar(item.id) },
                                    enabled = !isActive,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                                ) {
                                    Text(if (isActive) "Equipped" else "Equip", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.buyAvatar(item.id, item.cost) },
                                    enabled = userProgress.coins >= item.cost,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                                ) {
                                    Text("${item.cost} G", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // PIANO SKINS
            val skins = listOf(
                SkinData("classic_ivory", "Classic Ivory", "Pure matte ivory layout with traditional gold branding.", 0),
                SkinData("emerald_forest", "Emerald Woods", "A forest aesthetic using rich emerald keys and vines.", 100),
                SkinData("castle_obsidian", "Volcanic Obsidian", "Hot molten active rings on solid igneous dark keys.", 200),
                SkinData("golden_majesty", "Golden Empress", "Royal shimmering key templates wrapped in golden boundaries.", 400)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(skins) { item ->
                    val isOwned = userProgress.isSkinUnlocked(item.id)
                    val isActive = userProgress.activeSkinId == item.id

                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isActive) Color(0xFF2C3258) else Color(0xFF191C35)),
                        border = BorderStroke(1.5.dp, if (isActive) Color(0xFFFFD600) else Color.Transparent),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SKINS_MAP[item.id]?.boardBg ?: Color.Black)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(item.desc, color = Color.Gray, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            if (isOwned) {
                                Button(
                                    onClick = { viewModel.selectPianoSkin(item.id) },
                                    enabled = !isActive,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                                ) {
                                    Text(if (isActive) "Equipped" else "Equip", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.buyPianoSkin(item.id, item.cost) },
                                    enabled = userProgress.coins >= item.cost,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                                ) {
                                    Text("${item.cost} G", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 5. INTERACTIVE LESSON MODE COMPOSE SCREEN
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LessonPlayScreen(viewModel: PianoQuestViewModel, level: Level) {
    val currentPlayIdx by viewModel.currentPlayIndex.collectAsState()
    val wrongKey by viewModel.wrongNoteTrigger.collectAsState()
    val correctKey by viewModel.correctNoteTrigger.collectAsState()
    val accuracy by viewModel.lessonAccuracy.collectAsState()
    val isComplete by viewModel.isLessonComplete.collectAsState()
    
    val grade by viewModel.lessonEvaluationGrade.collectAsState()
    val rewardedXp by viewModel.xpAwarded.collectAsState()
    val rewardedCoins by viewModel.coinsAwarded.collectAsState()
    val rewardedStars by viewModel.starsAwarded.collectAsState()

    val userProgress by viewModel.userProgress.collectAsState()
    val isHintActive by viewModel.isHintActive.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP RETREAT BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.finishLesson() },
                modifier = Modifier.background(Color(0xFF2C1E1E), CircleShape)
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Flee Lesson", tint = Color(0xFFDC143C))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${level.worldName.uppercase()} • LEVEL ${level.index}",
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = level.title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }

            // ACCURACY COUNTER
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF142B24)),
                border = BorderStroke(1.dp, Color(0xFF4CAF50))
            ) {
                Text(
                    text = "${accuracy.toInt()}% ACC",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color(0xFF00E676),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // CONCEPT INFORMATION CARD (Duolingo Style!)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13172E)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF2C3558)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💡 MUSIC LESSON PRECEPT",
                    color = Color(0xFFFFD600),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = level.conceptExplanation,
                    color = Color.White,
                    fontSize = 12.dp.value.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // ADAPTIVE WARNING BANNER
        if (isHintActive) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3E1F21)),
                border = BorderStroke(1.dp, Color.Red),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Help, "Help", tint = Color.Red, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hints Activated! Frequencies slowed down and letter markings on keyboard illuminated.",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // TREBLE CLEF SIGHT-READING SHEET VIEW!
        if (level.showTrebleStaff) {
            TrebleClefStaveView(targetMidiNote = if (currentPlayIdx < level.targetSequence.size) level.targetSequence[currentPlayIdx] else 60)
        } else {
            // STANDARD NOTES SEQUENCE TARGET BAR
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0E0E14)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("MELODY SEQUENCE TO TAP", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Show dots or notes to play in order
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        level.targetSequence.forEachIndexed { idx, midiVal ->
                            val isPast = idx < currentPlayIdx
                            val isActive = idx == currentPlayIdx

                            Box(
                                modifier = Modifier
                                    .size(width = 38.dp, height = 38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isPast -> Color(0xFF00E676)
                                            isActive -> Color(0xFFFFD600)
                                            else -> Color(0xFF21212B)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getSingleNoteLabelFromMidi(midiVal),
                                    color = if (isActive || isPast) Color.Black else Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // KEYBOARD BOARD AREA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .heightIn(min = 200.dp)
        ) {
            // Detect active target note from sequence
            val targetNextMidiNote = if (level.chords != null) {
                val chordIdx = currentPlayIdx.coerceAtMost(level.chords.size - 1)
                level.chords[chordIdx].firstOrNull() ?: 60
            } else {
                if (currentPlayIdx < level.targetSequence.size) level.targetSequence[currentPlayIdx] else 60
            }

            VirtualPianoBoard(
                startMidiNote = 58, // A comfortable, centered range spanning World notes
                showLabels = isHintActive || level.index <= 4, // Auto show for first several levels or whenever hint is on
                wrongKey = wrongKey,
                correctKey = correctKey,
                skinId = userProgress.activeSkinId,
                targetMidiHighlight = targetNextMidiNote,
                onKeySelect = { midi -> viewModel.onPianoKeyPress(midi) }
            )
        }
    }

    // LESSON END SHOW SCORE / CORRESPONDING GRADES DIALOG
    if (isComplete) {
        AlertDialog(
            onDismissRequest = { /* forces click continue button */ },
            containerColor = Color(0xFF131526),
            title = {
                Text(
                    text = if (level.isBoss) "⚔️ BOSS DEFEATED!" else "🎉 LESSON EXCELLED!",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD600),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "GRADE : $grade",
                        style = MaterialTheme.typography.displaySmall,
                        color = when (grade) {
                            "S" -> Color(0xFFFFEA00)
                            "A" -> Color(0xFF00E676)
                            "B" -> Color(0xFF29B6F6)
                            else -> Color(0xFFE0E0E0)
                        },
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "Accuracy: ${accuracy.toInt()}%",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocalActivity, "XP", tint = Color(0xFFFFAB40), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+$rewardedXp XP", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.GeneratingTokens, "Coins", tint = Color(0xFFFFC107), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+$rewardedCoins Coins", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(3) { i ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (i < rewardedStars) Color(0xFFFFD600) else Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.finishLesson() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Claim Artifacts & Continue", fontWeight = FontWeight.Black, color = Color.Black)
                }
            }
        )
    }
}

// TREBLE STAFF RENDERER (Uses native canvas stave lines)
@Composable
fun TrebleClefStaveView(targetMidiNote: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF11142A)),
        border = BorderStroke(1.dp, Color(0xFF22295E)),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Text("READ TREBLE STAFF", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    // Standard 5 stave lines
                    val lineSpacing = 12.dp.toPx()
                    val midpointY = height / 2f
                    val yLines = List(5) { i ->
                        midpointY - (2 - i) * lineSpacing
                    }

                    yLines.forEach { y ->
                        drawLine(Color.White.copy(alpha = 0.6f), Offset(20f, y), Offset(width - 20f, y), strokeWidth = 1.6.dp.toPx())
                    }

                    // Render left/right clef boundary indicators (Simple decoration)
                    drawLine(Color.White, Offset(20f, yLines.first()), Offset(20f, yLines.last()), strokeWidth = 3.dp.toPx())
                    drawLine(Color.White, Offset(width - 20f, yLines.first()), Offset(width - 20f, yLines.last()), strokeWidth = 3.dp.toPx())

                    // Calculate notes ledger lines/spaces offsets depending on midi
                    // Note MIDI 60 (C4) lies below the first line (needs an extra ledger line!)
                    // E4 (64) is 1st line, F4 (65) is 1st space, G4 (67) is 2nd line, A4 (69) is 2nd space, B4 (71) is 3rd line, C5 (72) is 3rd space.
                    val relativeOffsetOnInterval = when (targetMidiNote) {
                        60 -> 3.0f // C4 (lies below - needs a tiny ledger bar!)
                        62 -> 2.5f // D4 (space below stave)
                        64 -> 2.0f // E4 (line 1)
                        65 -> 1.5f // F4 (space 1)
                        67 -> 1.0f // G4 (line 2)
                        69 -> 0.5f // A4 (space 2)
                        71 -> 0.0f // B4 (line 3)
                        72 -> -0.5f // C5 (space 3)
                        74 -> -1.0f // D5 (line 4)
                        76 -> -1.5f // E5 (space 4)
                        77 -> -2.0f // F5 (line 5)
                        else -> 2.0f
                    }

                    val noteHeadY = midpointY + relativeOffsetOnInterval * lineSpacing
                    val noteHeadX = width / 2.2f

                    // Draw optional Ledger Line for Middle C (MIDI 60)
                    if (targetMidiNote == 60) {
                        drawLine(
                            color = Color.White,
                            start = Offset(noteHeadX - 22f, noteHeadY),
                            end = Offset(noteHeadX + 22f, noteHeadY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Draw Note head (Ellipse)
                    drawCircle(
                        color = Color(0xFFFFD600),
                        radius = 8.dp.toPx(),
                        center = Offset(noteHeadX, noteHeadY)
                    )

                    // Draw Note stem (line pointing up or down)
                    val stemDirection = if (relativeOffsetOnInterval <= 0f) 1f else -1f // up or down
                    drawLine(
                        color = Color.White,
                        start = Offset(noteHeadX + 8.dp.toPx(), noteHeadY),
                        end = Offset(noteHeadX + 8.dp.toPx(), noteHeadY + stemDirection * 35.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Helper label overlay on stave
                    drawCircle(
                        color = Color.White.copy(alpha = 0.1f),
                        radius = 20.dp.toPx(),
                        center = Offset(width - 60f, height / 2)
                    )
                }
            }
        }
    }
}

// 6. HIGH-PERFORMANCE VIRTUAL PIANO KEYBOARD VIEW
@Composable
fun VirtualPianoBoard(
    startMidiNote: Int,
    showLabels: Boolean,
    wrongKey: Int?,
    correctKey: Int?,
    skinId: String,
    targetMidiHighlight: Int? = null,
    onKeySelect: (Int) -> Unit
) {
    val skinColors = SKINS_MAP[skinId] ?: SKINS_MAP["classic_ivory"]!!

    // Define standard keyboard layout spanning 12-14 white keys to fit beautiful handheld viewport
    // White keys matching: C, D, E, F, G, A, B, C, D, E, F, G...
    val listWhiteKeys = remember(startMidiNote) {
        val list = mutableListOf<Int>()
        var curr = startMidiNote
        while (list.size < 12) {
            if (!isMidiKeyBlack(curr)) {
                list.add(curr)
            }
            curr++
        }
        list
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(skinColors.boardBg)
            .padding(top = 16.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
    ) {
        val density = LocalConfiguration.current
        val boardWidth = constraints.maxWidth.toFloat()
        val keyWidth = boardWidth / 12f // 12 white keys fit perfectly

        // ROW 1: WHITE KEYS
        Row(modifier = Modifier.fillMaxSize()) {
            listWhiteKeys.forEach { midi ->
                val isWrong = wrongKey == midi
                val isCorrect = correctKey == midi
                val isTargetHighlight = targetMidiHighlight == midi

                val keyBg = when {
                    isWrong -> Color(0xFFFF1744)
                    isCorrect -> Color(0xFF00E676)
                    isTargetHighlight -> skinColors.primaryKeyGlow.copy(alpha = 0.25f)
                    else -> skinColors.whiteKeyBg
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 1.dp)
                        .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                        .background(keyBg)
                        .border(
                            width = if (isTargetHighlight) 2.5.dp else 1.dp,
                            color = if (isTargetHighlight) Color(0xFFFFD600) else Color(0xFFB0BEC5),
                            shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
                        )
                        .clickable { onKeySelect(midi) },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (showLabels || isTargetHighlight) {
                        Text(
                            text = getSingleNoteLabelFromMidi(midi),
                            color = if (isWrong || isCorrect) Color.White else Color(0xFF263238),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }

        // ROW 2: ABSOLUTE OVERLAID BLACK KEYS
        // Black keys are located precisely between white keys seams
        listWhiteKeys.forEachIndexed { index, whiteMidi ->
            // Check if there is a black key immediately higher than this white key
            val blackMidi = whiteMidi + 1
            if (isMidiKeyBlack(blackMidi) && index < 11) {
                // Calculate pixel offset and place black key button
                val startOffsetPx = (index + 1) * keyWidth - (keyWidth * 0.32f)

                val isWrong = wrongKey == blackMidi
                val isCorrect = correctKey == blackMidi
                val isTargetHighlight = targetMidiHighlight == blackMidi

                val blackBg = when {
                    isWrong -> Color(0xFFFF1744)
                    isCorrect -> Color(0xFF00E676)
                    isTargetHighlight -> skinColors.primaryKeyGlow
                    else -> skinColors.blackKeyBg
                }

                Box(
                    modifier = Modifier
                        .offset(x = (startOffsetPx / density.densityDpi * 160).dp) // convert pixels to DPs safely
                        .width((keyWidth * 0.64f / density.densityDpi * 160).dp)
                        .fillMaxHeight(0.6f)
                        .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(blackBg)
                        .border(
                            width = if (isTargetHighlight) 2.dp else 0.5.dp,
                            color = if (isTargetHighlight) Color(0xFFFFD600) else Color.DarkGray,
                            shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                        )
                        .clickable { onKeySelect(blackMidi) },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (showLabels || isTargetHighlight) {
                        Text(
                            text = getSingleNoteLabelFromMidi(blackMidi).replace("#", "♯"),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

// CONVENIENT MUSIC UTILS
fun isMidiKeyBlack(midi: Int): Boolean {
    val r = midi % 12
    return r == 1 || r == 3 || r == 6 || r == 8 || r == 10
}

fun getSingleNoteLabelFromMidi(midi: Int): String {
    val notes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val name = notes[midi % 12]
    val octave = (midi / 12) - 1
    return "$name$octave"
}

fun getWorldThemeColor(worldId: Int): Color {
    return when (worldId) {
        1 -> Color(0xFF00E676) // Village Green
        2 -> Color(0xFF00B0FF) // Forest Cyan
        3 -> Color(0xFFD500F9) // Castle Purple
        4 -> Color(0xFFFF3D00) // Mountain Red
        5 -> Color(0xFFFFC107) // Harmony Gold
        else -> Color(0xFF76FF03) // Scholar Neon
    }
}

fun getAvatarIcon(avatarId: String): ImageVector {
    return when (avatarId) {
        "rhythm_wizard" -> Icons.Outlined.HourglassEmpty
        "melodic_elven" -> Icons.Outlined.DoubleArrow
        "grandmaster_pianist" -> Icons.Outlined.MilitaryTech
        else -> Icons.Outlined.MusicNote // Default Young Bard
    }
}

fun getAvatarName(avatarId: String): String {
    return when (avatarId) {
        "rhythm_wizard" -> "Rhythm Sorcerer"
        "melodic_elven" -> "Melodic Ranger"
        "grandmaster_pianist" -> "Grandmaster Scholar"
        else -> "Young Bard"
    }
}

data class AvatarData(val id: String, val name: String, val desc: String, val cost: Int)
data class SkinData(val id: String, val name: String, val desc: String, val cost: Int)
