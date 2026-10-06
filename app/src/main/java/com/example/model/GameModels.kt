package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CategoryChallengeColor
import com.example.ui.theme.CategoryCharadesColor
import com.example.ui.theme.CategoryLoopColor
import com.example.ui.theme.CategoryMafiaColor
import com.example.ui.theme.CategoryMixColor
import com.example.ui.theme.CategoryRatherColor
import com.example.ui.theme.CategorySpyColor
import com.example.ui.theme.CategoryTruthColor
import com.example.ui.theme.CategoryWhoColor

data class GameCategory(
  val id: String,
  val titleArabic: String,
  val subtitleArabic: String,
  val iconEmoji: String,
  val primaryColor: Color,
  val isCustom: Boolean = false
) {
  companion object {
    val WHO_IS_MOST_LIKELY = GameCategory(
      id = "who",
      titleArabic = "مين أكتر واحد",
      subtitleArabic = "تصويت وسوالف مين تنطبق عليه الصفة!",
      iconEmoji = "👥",
      primaryColor = CategoryWhoColor
    )
    val WOULD_YOU_RATHER = GameCategory(
      id = "rather",
      titleArabic = "لو خيروك",
      subtitleArabic = "اختيارات صعبة ومواقف طريفة!",
      iconEmoji = "⚖️",
      primaryColor = CategoryRatherColor
    )
    val CHALLENGES_PENALTIES = GameCategory(
      id = "challenges",
      titleArabic = "تحديات وأحكام",
      subtitleArabic = "تحديات جريئة وحماس بدون تردد!",
      iconEmoji = "⚡",
      primaryColor = CategoryChallengeColor
    )
    val CONFESSIONS_TRUTH = GameCategory(
      id = "truth",
      titleArabic = "اعترافات وصراحة",
      subtitleArabic = "أسئلة من القلب وبدون مجاملة!",
      iconEmoji = "🔮",
      primaryColor = CategoryTruthColor
    )
    val OUT_OF_THE_LOOP = GameCategory(
      id = "loop",
      titleArabic = "برا السالفة",
      subtitleArabic = "واحد منكم برا السالفة ومضيع، مين يصيده؟",
      iconEmoji = "🤫",
      primaryColor = CategoryLoopColor
    )
    val THE_SPY = GameCategory(
      id = "spy",
      titleArabic = "الجاسوس",
      subtitleArabic = "الكل بالمكان السري إلا الجاسوس.. اكشفوه بالأسئلة!",
      iconEmoji = "🕵️‍♂️",
      primaryColor = CategorySpyColor
    )
    val CHARADES = GameCategory(
      id = "charades",
      titleArabic = "ولا كلمة",
      subtitleArabic = "تحدي التمثيل والإشارات.. فريقين وتوقيت دقيقة وسكور لايف!",
      iconEmoji = "🎬",
      primaryColor = CategoryCharadesColor
    )
    val MAFIA = GameCategory(
      id = "mafia",
      titleArabic = "المافيا",
      subtitleArabic = "لعبة الشك والذكاء.. مافيا ومحقق وطبيب ومواطنين! مين يكشف الثاني؟",
      iconEmoji = "🕵️‍♂️",
      primaryColor = CategoryMafiaColor
    )
    val ALL_MIX = GameCategory(
      id = "mix",
      titleArabic = "خلطة الجلسة",
      subtitleArabic = "مكس عشوائي من كل الفئات!",
      iconEmoji = "🎲",
      primaryColor = CategoryMixColor
    )

    val defaultCategories: List<GameCategory> = listOf(
      WHO_IS_MOST_LIKELY,
      WOULD_YOU_RATHER,
      CHALLENGES_PENALTIES,
      CONFESSIONS_TRUTH,
      OUT_OF_THE_LOOP,
      THE_SPY,
      MAFIA,
      CHARADES,
      ALL_MIX
    )
  }
}

data class CardItem(
  val id: String,
  val category: GameCategory,
  val questionArabic: String,
  val optionA: String? = null,
  val optionB: String? = null,
  val targetPlayerName: String? = null,
  val hintArabic: String? = null
)

data class PenaltyItem(
  val id: String,
  val textArabic: String,
  val severity: String = "عقاب خفيف ومضحك",
  val iconEmoji: String = "🎭"
)

data class Player(
  val id: String,
  val name: String,
  val avatarColorIndex: Int = 0,
  val penaltiesCount: Int = 0,
  val score: Int = 0
)

data class PlayerTitle(
  val title: String,
  val emoji: String,
  val description: String,
  val funnyBadge: String,
  val badgeColorHex: Long = 0xFFFFB300
)

data class PlayerSessionStats(
  val playerId: String,
  val challengesCompleted: Int = 0,
  val confessionsAnswered: Int = 0,
  val wouldYouRatherAnswered: Int = 0,
  val votesReceivedInWhoIsMost: Int = 0,
  val penaltiesTaken: Int = 0,
  val charadesWordsGuessed: Int = 0,
  val charadesRoundsPlayed: Int = 0,
  val spyGamesPlayed: Int = 0,
  val loopGamesPlayed: Int = 0,
  val mafiaGamesPlayed: Int = 0,
  val totalInteractions: Int = 0
)

data class PlayerWithTitle(
  val player: Player,
  val stats: PlayerSessionStats,
  val title: PlayerTitle
)

enum class ScreenState {
  SETUP,
  HOME,
  GAME,
  OUT_OF_THE_LOOP,
  THE_SPY,
  MAFIA,
  CHARADES,
  ADMIN_DASHBOARD,
  SESSION_STATS
}

// Charades Game Models ("ولا كلمة")
enum class Team(val displayNameArabic: String, val iconEmoji: String) {
  TEAM_A("فريق النشمي (أ)", "🟡"),
  TEAM_B("فريق النشامى (ب)", "🟣")
}

data class CharadesWordItem(
  val id: String,
  val word: String,
  val categoryTag: String = "عام",
  val isCustom: Boolean = false
)

enum class CharadesSubCategory(
  val id: String,
  val titleArabic: String,
  val subtitleArabic: String,
  val iconEmoji: String
) {
  MOVIES_AND_SERIES(
    id = "movies",
    titleArabic = "مسلسلات وأفلام",
    subtitleArabic = "أشهر المسلسلات والأفلام والمسرحيات العربية (١٥٠ خيار)",
    iconEmoji = "🎬"
  ),
  SPECIFIC_DETAILS(
    id = "details",
    titleArabic = "تفاصيل وأشياء دقيقة",
    subtitleArabic = "١٠٠ غرض وتفصيلة دقيقة وعفوية من حياتنا اليومية",
    iconEmoji = "🔍"
  )
}

enum class CharadesPlayMode(
  val titleArabic: String,
  val subtitleArabic: String,
  val iconEmoji: String
) {
  SINGLE_CARD_PER_ROUND(
    titleArabic = "كرت واحد لكل دقيقة",
    subtitleArabic = "تنتهي جولة الفريق بمجرد حزر الكرت أو انتهاء الوقت 🎯",
    iconEmoji = "🎯"
  ),
  MULTIPLE_CARDS_SPEED(
    titleArabic = "عدة كروت حتى ينتهي الوقت",
    subtitleArabic = "حزر أكبر عدد من الكروت واجمع نقاط متتالية خلال الـ ٦٠ ثانية ⚡",
    iconEmoji = "⚡"
  )
}

enum class CharadesPhase {
  CATEGORY_SELECTION,
  READY,
  PLAYING,
  ROUND_ENDED,
  GAME_OVER
}

data class CharadesRoundHistoryItem(
  val id: String = java.util.UUID.randomUUID().toString(),
  val roundNumber: Int,
  val team: Team,
  val teamName: String,
  val actorName: String?,
  val correctCount: Int,
  val correctWords: List<String> = emptyList(),
  val skippedWords: List<String> = emptyList(),
  val teamAScoreAfter: Int,
  val teamBScoreAfter: Int,
  val timestamp: Long = System.currentTimeMillis()
)

data class CharadesGameState(
  val teamAScore: Int = 0,
  val teamBScore: Int = 0,
  val teamAPlayers: List<Player> = emptyList(),
  val teamBPlayers: List<Player> = emptyList(),
  val teamAName: String = "فريق النشمي",
  val teamBName: String = "فريق النشامى",
  val playMode: CharadesPlayMode = CharadesPlayMode.MULTIPLE_CARDS_SPEED,
  val currentTurnTeam: Team = Team.TEAM_A,
  val currentActorPlayer: Player? = null,
  val currentWordItem: CharadesWordItem? = null,
  val playedWordIdsInGame: Set<String> = emptySet(),
  val timerSecondsRemaining: Int = 60,
  val isTimerRunning: Boolean = false,
  val selectedSubCategory: CharadesSubCategory = CharadesSubCategory.MOVIES_AND_SERIES,
  val phase: CharadesPhase = CharadesPhase.CATEGORY_SELECTION,
  val roundNumber: Int = 1,
  val wordsCorrectThisRound: List<String> = emptyList(),
  val wordsSkippedThisRound: List<String> = emptyList(),
  val roundHistory: List<CharadesRoundHistoryItem> = emptyList()
) {
  fun getTeamName(team: Team): String {
    return if (team == Team.TEAM_A) teamAName else teamBName
  }
}


// Out of the Loop models
data class LoopTopic(
  val id: String,
  val categoryName: String,
  val iconEmoji: String,
  val words: List<String>
)

data class LoopRoleAssignment(
  val player: Player,
  val isOutOfTheLoop: Boolean
)

enum class LoopPhase {
  PASS_PHONE,
  DISCUSSION,
  VOTING,
  REVEAL
}

data class LoopGameState(
  val categoryName: String = "",
  val secretWord: String = "",
  val outPlayerId: String = "",
  val assignments: List<LoopRoleAssignment> = emptyList(),
  val currentPassingPlayerIndex: Int = 0,
  val phase: LoopPhase = LoopPhase.PASS_PHONE,
  val timerSecondsRemaining: Int = 180,
  val isTimerRunning: Boolean = false,
  val turnPhase: Int = 1, // 1 = Round 1 (sequential questioning), 2 = Round 2 (free asking)
  val currentTurnOrderIndex: Int = 0, // Who is asking in Round 1 sequence
  val suspectedPlayerId: String? = null,
  val playerVotes: Map<String, String> = emptyMap(), // voterId -> targetSuspectId
  val awardedPoints: Map<String, Int> = emptyMap(), // playerId -> points won this round
  val guessWordAttempt: String = "",
  val isWordGuessedCorrectly: Boolean? = null,
  val isRoundOver: Boolean = false
)

// Spy Game models
data class SpyLocation(
  val id: String,
  val nameArabic: String,
  val iconEmoji: String,
  val descriptionArabic: String,
  val roles: List<String>,
  val sampleQuestions: List<String>
)

data class SpyRoleAssignment(
  val player: Player,
  val isSpy: Boolean,
  val roleName: String
)

enum class SpyPhase {
  PASS_PHONE,
  QUESTIONING,
  ACCUSATION,
  REVEAL
}

data class SpyGameState(
  val location: SpyLocation? = null,
  val spyPlayerId: String = "",
  val assignments: List<SpyRoleAssignment> = emptyList(),
  val currentPassingPlayerIndex: Int = 0,
  val phase: SpyPhase = SpyPhase.PASS_PHONE,
  val timerSecondsRemaining: Int = 300,
  val isTimerRunning: Boolean = false,
  val currentAskerName: String = "",
  val currentAnswererName: String = "",
  val accusedPlayerId: String? = null,
  val playerVotes: Map<String, String> = emptyMap(), // voterId -> accusedPlayerId
  val awardedPoints: Map<String, Int> = emptyMap(), // playerId -> points won this round
  val spyGuessedLocationId: String? = null,
  val isSpyLocationGuessedCorrectly: Boolean? = null,
  val isRoundOver: Boolean = false
)

data class VoteState(
  val cardId: String = "",
  val votes: Map<String, Int> = emptyMap()
)
