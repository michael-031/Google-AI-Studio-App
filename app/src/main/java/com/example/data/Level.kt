package com.example.data

data class Level(
    val index: Int,
    val worldId: Int,
    val worldName: String,
    val title: String,
    val conceptExplanation: String,
    val targetNotes: List<Int>, // MIDI Note numbers (Middle C = 60)
    val targetNoteNames: List<String>,
    val targetSequence: List<Int>, // Frequencies / Key indexes to play in sequence
    val chords: List<List<Int>>? = null,
    val chordNames: List<String>? = null,
    val rhythmBeats: List<Float>? = null, // Note duration multipliers (1f = Quarter, 2f = Half, 4f = Whole)
    val showTrebleStaff: Boolean = false,
    val isBoss: Boolean = false
)

object LevelCurriculum {
    val WORLDS = listOf(
        "Village of Notes" to "World 1",
        "Rhythm Forest" to "World 2",
        "Chord Castle" to "World 3",
        "Melody Mountain" to "World 4",
        "Harmony Kingdom" to "World 5",
        "Master Pianist Realm" to "World 6"
    )

    val LEVELS = listOf(
        // WORLD 1: THE VILLAGE OF NOTES (Intro, Key identification)
        Level(
            index = 1,
            worldId = 1,
            worldName = "Village of Notes",
            title = "Identify Middle C",
            conceptExplanation = "Welcome to Piano Quest! Let's start with Middle C. On your keyboard, look at the black keys grouped in twos and threes. Middle C is the white key just to the left of the group of TWO black keys.",
            targetNotes = listOf(60),
            targetNoteNames = listOf("C4"),
            targetSequence = listOf(60, 60, 60)
        ),
        Level(
            index = 2,
            worldId = 1,
            worldName = "Village of Notes",
            title = "Find C, D, and E",
            conceptExplanation = "Great! D is the white key nestled between the two black keys. E is the white key just to the right of them. Let's play the three notes in sequence: C, D, E.",
            targetNotes = listOf(60, 62, 64),
            targetNoteNames = listOf("C4", "D4", "E4"),
            targetSequence = listOf(60, 62, 64)
        ),
        Level(
            index = 3,
            worldId = 1,
            worldName = "Village of Notes",
            title = "Stepping Stones",
            conceptExplanation = "Let's practice stepping up and down these notes. Keep your index, middle, and ring fingers relaxed. Play: C - D - E - D - C.",
            targetNotes = listOf(60, 62, 64),
            targetNoteNames = listOf("C4", "D4", "E4"),
            targetSequence = listOf(60, 62, 64, 62, 60)
        ),
        Level(
            index = 4,
            worldId = 1,
            worldName = "Village of Notes",
            title = "Left & Right Hand Basics",
            conceptExplanation = "Typically, the Right Hand plays higher notes, and the Left Hand plays lower notes. Let's practice switching hands! Play B3 (Left Hand) then Middle C (Right Hand).",
            targetNotes = listOf(59, 60),
            targetNoteNames = listOf("B3 (LH)", "C4 (RH)"),
            targetSequence = listOf(59, 60, 59, 60)
        ),
        Level(
            index = 5,
            worldId = 1,
            worldName = "Village of Notes",
            title = "Your First Song",
            conceptExplanation = "Time to play a complete mini-song! We will play the melody of \"Hot Cross Buns\" using C, D, and E keys.",
            targetNotes = listOf(60, 62, 64),
            targetNoteNames = listOf("C4", "D4", "E4"),
            targetSequence = listOf(64, 62, 60, 64, 62, 60, 60, 60, 60, 60, 62, 62, 62, 62, 64, 62, 60)
        ),
        Level(
            index = 6,
            worldId = 1,
            worldName = "Village of Notes",
            title = "Boss: Ode to Joy",
            conceptExplanation = "BOSS BATTLE! Defeat the Note Goblin by performing Beethoven's Ode to Joy melody perfectly. Tap the illuminated keys in exact succession!",
            targetNotes = listOf(60, 62, 64, 65, 67),
            targetNoteNames = listOf("C4", "D4", "E4", "F4", "G4"),
            targetSequence = listOf(64, 64, 65, 67, 67, 65, 64, 62, 60, 60, 62, 64, 64, 62, 62),
            isBoss = true
        ),

        // WORLD 2: RHYTHM FOREST (Note lengths & beats)
        Level(
            index = 7,
            worldId = 2,
            worldName = "Rhythm Forest",
            title = "Quarter Notes",
            conceptExplanation = "Rhythm is the heartbeat of music. Quarter notes represent 1 beat of sound. Play these notes in a steady 1-2-3-4 marching rhythm.",
            targetNotes = listOf(60, 62, 64, 65),
            targetNoteNames = listOf("C4", "D4", "E4", "F4"),
            targetSequence = listOf(60, 62, 64, 65),
            rhythmBeats = listOf(1f, 1f, 1f, 1f)
        ),
        Level(
            index = 8,
            worldId = 2,
            worldName = "Rhythm Forest",
            title = "Half Notes",
            conceptExplanation = "Half notes last for 2 beats! Hold each note twice as long as a quarter note before playing the next. Play: C4 (hold) then G4 (hold).",
            targetNotes = listOf(60, 67),
            targetNoteNames = listOf("C4", "G4"),
            targetSequence = listOf(60, 67, 60, 67),
            rhythmBeats = listOf(2f, 2f, 2f, 2f)
        ),
        Level(
            index = 9,
            worldId = 2,
            worldName = "Rhythm Forest",
            title = "Whole Notes",
            conceptExplanation = "Whole notes are long celestial echoes. They last for 4 full beats! Play Middle C and hold it while counting 1-2-3-4 in your head.",
            targetNotes = listOf(60, 64),
            targetNoteNames = listOf("C4", "E4"),
            targetSequence = listOf(60, 64),
            rhythmBeats = listOf(4f, 4f)
        ),
        Level(
            index = 10,
            worldId = 2,
            worldName = "Rhythm Forest",
            title = "Boss: Rhythm Challenge",
            conceptExplanation = "BOSS BATTLE! The rhythm beast requires a mix of quarter, half, and whole notes. Match the different note lengths to unlock the Chord Castle!",
            targetNotes = listOf(60, 62, 64, 67),
            targetNoteNames = listOf("C4", "D4", "E4", "G4"),
            targetSequence = listOf(60, 60, 62, 64, 64, 62, 60, 67),
            rhythmBeats = listOf(1f, 1f, 2f, 1f, 1f, 2f, 2f, 4f),
            isBoss = true
        ),

        // WORLD 3: CHORD CASTLE (Polychords, harmonies)
        Level(
            index = 11,
            worldId = 3,
            worldName = "Chord Castle",
            title = "C Major Chord",
            conceptExplanation = "A chord is played by striking multiple notes AT THE SAME TIME. Let's form the C Major chord with three fingers: C (root), E (third), and G (fifth). Play them simultaneously!",
            targetNotes = listOf(60, 64, 67),
            targetNoteNames = listOf("C4+E4+G4"),
            targetSequence = listOf(60), // We will represent chord sequence triggers in UI
            chords = listOf(listOf(60, 64, 67)),
            chordNames = listOf("C Major")
        ),
        Level(
            index = 12,
            worldId = 3,
            worldName = "Chord Castle",
            title = "F and G Major Chords",
            conceptExplanation = "Great job! Let's learn two more chords vital for thousands of songs. F Major: F-A-C (65, 69, 72) and G Major: G-B-D (67, 71, 74). Practice switching between them.",
            targetNotes = listOf(65, 69, 72, 67, 71, 74),
            targetNoteNames = listOf("F chord", "G chord"),
            targetSequence = listOf(1, 2, 1, 2),
            chords = listOf(listOf(65, 69, 72), listOf(67, 71, 74)),
            chordNames = listOf("F Major", "G Major")
        ),
        Level(
            index = 13,
            worldId = 3,
            worldName = "Chord Castle",
            title = "The Sad A-Minor Chord",
            conceptExplanation = "Minor chords have a darker, more emotional sound. Play the A Minor chord (A3 - C4 - E4) using MIDI keys 57, 60, and 64.",
            targetNotes = listOf(57, 60, 64),
            targetNoteNames = listOf("A3+C4+E4"),
            targetSequence = listOf(0),
            chords = listOf(listOf(57, 60, 64)),
            chordNames = listOf("A Minor")
        ),
        Level(
            index = 14,
            worldId = 3,
            worldName = "Chord Castle",
            title = "Boss: Royal Chord Progression",
            conceptExplanation = "BOSS BATTLE! Face the Castle Guard. Complete a majestic Royal Progression: C Major -> A Minor -> F Major -> G Major to unlock Melody Mountain!",
            targetNotes = listOf(57, 60, 64, 67, 69, 72, 74),
            targetNoteNames = listOf("C Maj", "A Min", "F Maj", "G Maj"),
            targetSequence = listOf(0, 1, 2, 3),
            chords = listOf(
                listOf(60, 64, 67),
                listOf(57, 60, 64),
                listOf(65, 69, 72),
                listOf(67, 71, 74)
            ),
            chordNames = listOf("C Major", "A Minor", "F Major", "G Major"),
            isBoss = true
        ),

        // WORLD 4: MELODY MOUNTAIN (Reading sheet music/stave)
        Level(
            index = 15,
            worldId = 4,
            worldName = "Melody Mountain",
            title = "Treble Clef Lines",
            conceptExplanation = "Melody Mountain teaches you how to SIGHT READ! The treble clef lines represent notes E4, G4, B4, D5, F5 (Every Good Boy Does Fine). Touch them as they light up on the staff!",
            targetNotes = listOf(64, 67, 71, 74, 77),
            targetNoteNames = listOf("E4", "G4", "B4", "D5", "F5"),
            targetSequence = listOf(64, 67, 100), // Custom index
            showTrebleStaff = true
        ),
        Level(
            index = 16,
            worldId = 4,
            worldName = "Melody Mountain",
            title = "Treble Clef Spaces",
            conceptExplanation = "Notes in spaces draw the word F-A-C-E! Touch F4 (1st space), A4 (2nd space), C5 (3rd space), and E5 (4th space). Learn to read them instantly.",
            targetNotes = listOf(65, 69, 72, 76),
            targetNoteNames = listOf("F4", "A4", "C5", "E5"),
            targetSequence = listOf(65, 69, 72, 76),
            showTrebleStaff = true
        ),
        Level(
            index = 17,
            worldId = 4,
            worldName = "Melody Mountain",
            title = "Boss: Sight-reading Ascent",
            conceptExplanation = "BOSS BATTLE! The mountain dragon tests your sight-reading. Read the notes directly off the treble staff canvas and play them on your virtual piano!",
            targetNotes = listOf(60, 64, 67, 72),
            targetNoteNames = listOf("C4", "E4", "G4", "C5"),
            targetSequence = listOf(60, 64, 67, 72, 67, 64, 60),
            showTrebleStaff = true,
            isBoss = true
        ),

        // WORLD 5: HARMONY KINGDOM (Scales & Coordination)
        Level(
            index = 18,
            worldId = 5,
            worldName = "Harmony Kingdom",
            title = "The Full C Major Scale",
            conceptExplanation = "Welcome to the magical Harmony Kingdom. Let's play the complete C Major Scale sequentially. Use your thumb to tuck under your middle finger when shifting from E to F!",
            targetNotes = listOf(60, 62, 64, 65, 67, 69, 71, 72),
            targetNoteNames = listOf("C4", "D4", "E4", "F4", "G4", "A4", "B4", "C5"),
            targetSequence = listOf(60, 62, 64, 65, 67, 69, 71, 72)
        ),
        Level(
            index = 19,
            worldId = 5,
            worldName = "Harmony Kingdom",
            title = "Finger Jump exercises",
            conceptExplanation = "To strengthen finger coordination, let's practice interval jumps: play C4, skip to E4, play D4, skip to F4, and so on.",
            targetNotes = listOf(60, 62, 64, 65, 67, 69),
            targetNoteNames = listOf("C4", "D4", "E4", "F4", "G4", "A4"),
            targetSequence = listOf(60, 64, 62, 65, 64, 67, 65, 69)
        ),
        Level(
            index = 20,
            worldId = 5,
            worldName = "Harmony Kingdom",
            title = "Boss: Scales Master",
            conceptExplanation = "BOSS BATTLE! Defeat the Harmony Knight by performing the ascending AND descending scale with flawless speed and timing!",
            targetNotes = listOf(60, 62, 64, 65, 67, 69, 71, 72),
            targetNoteNames = listOf("C4", "D4", "E4", "F4", "G4", "A4", "B4", "C5"),
            targetSequence = listOf(60, 62, 64, 65, 67, 69, 71, 72, 71, 69, 67, 65, 64, 62, 60),
            isBoss = true
        ),

        // WORLD 6: MASTER PIANIST REALM (Advanced dynamics and completion)
        Level(
            index = 21,
            worldId = 6,
            worldName = "Master Pianist Realm",
            title = "Musical Dynamics (p vs f)",
            conceptExplanation = "Dynamics show emotion! 'Piano' (p) means soft, and 'Forte' (f) means strong/loud. We show volume triggers. Play notes softly, then hard (we highlight feedback!).",
            targetNotes = listOf(60, 62, 64),
            targetNoteNames = listOf("C4", "D4", "E4"),
            targetSequence = listOf(60, 60, 64, 64, 62, 62)
        ),
        Level(
            index = 22,
            worldId = 6,
            worldName = "Master Pianist Realm",
            title = "Aesthetic Expression",
            conceptExplanation = "In this exercise, play with changing tempo. Watch the glowing beat ring - tap exactly when the ring contacts the feedback line!",
            targetNotes = listOf(60, 64, 67, 72),
            targetNoteNames = listOf("C4", "E4", "G4", "C5"),
            targetSequence = listOf(60, 64, 67, 72, 67, 64, 60),
            rhythmBeats = listOf(1f, 1f, 1.5f, 2f, 1f, 1f, 3f)
        ),
        Level(
            index = 23,
            worldId = 6,
            worldName = "Master Pianist Realm",
            title = "Final Boss: Master Performance",
            conceptExplanation = "FINAL BOSS BATTLE! Perform the complete classical symphony \"Twinkle Twinkle Little Star\" combined with automatic minor chord harmonies in the background. Claim your legend!",
            targetNotes = listOf(60, 62, 64, 65, 67),
            targetNoteNames = listOf("C4", "D4", "E4", "F4", "G4"),
            targetSequence = listOf(60, 60, 67, 67, 69, 69, 67, 65, 65, 64, 64, 62, 62, 60),
            rhythmBeats = listOf(1f, 1f, 1f, 1f, 1f, 1f, 2f, 1f, 1f, 1f, 1f, 1f, 1f, 2f),
            isBoss = true
        )
    )

    fun getLevelsForWorld(worldId: Int): List<Level> {
        return LEVELS.filter { it.worldId == worldId }
    }
}
