package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PartyQuestionsData
import com.example.data.local.CustomCategoryEntity
import com.example.data.local.CustomCharadesWordEntity
import com.example.data.local.CustomLoopWordEntity
import com.example.data.local.CustomQuestionEntity
import com.example.data.local.CustomSpyLocationEntity
import com.example.data.local.DeletedItemEntity
import com.example.data.local.PartyAppDatabase
import com.example.data.repository.CustomCategoryRepository
import com.example.model.CardItem
import com.example.model.CharadesGameState
import com.example.model.CharadesPhase
import com.example.model.CharadesPlayMode
import com.example.model.CharadesRoundHistoryItem
import com.example.model.CharadesSubCategory
import com.example.model.CharadesWordItem
import com.example.model.GameCategory
import com.example.model.LoopGameState
import com.example.model.LoopPhase
import com.example.model.LoopRoleAssignment
import com.example.model.MafiaGamePhase
import com.example.model.MafiaGameState
import com.example.model.MafiaPlayerRole
import com.example.model.MafiaRoleType
import com.example.model.PenaltyItem
import com.example.model.Player
import com.example.model.PlayerSessionStats
import com.example.model.PlayerTitle
import com.example.model.PlayerWithTitle
import com.example.model.ScreenState
import com.example.model.SpyGameState
import com.example.model.SpyLocation
import com.example.model.SpyPhase
import com.example.model.SpyRoleAssignment
import com.example.model.Team
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

private data class AdminDataPayload(
  val catEntities: List<CustomCategoryEntity>,
  val qEntities: List<CustomQuestionEntity>,
  val delEntities: List<com.example.data.local.DeletedItemEntity>,
  val loopEntities: List<CustomLoopWordEntity>,
  val spyEntities: List<CustomSpyLocationEntity>,
  val charadesEntities: List<CustomCharadesWordEntity>
)

data class GameUiState(
  val currentScreen: ScreenState = ScreenState.SETUP,
  val selectedCategory: GameCategory = GameCategory.WHO_IS_MOST_LIKELY,
  val players: List<Player> = listOf(
    Player(id = "p1", name = "رامي", avatarColorIndex = 0),
    Player(id = "p2", name = "سارة", avatarColorIndex = 1),
    Player(id = "p3", name = "أحمد", avatarColorIndex = 2),
    Player(id = "p4", name = "نورة", avatarColorIndex = 3)
  ),
  val customCategories: List<GameCategory> = emptyList(),
  val allCustomCards: List<CardItem> = emptyList(),
  val deletedItemIds: Set<String> = emptySet(),
  val customLoopWords: List<CustomLoopWordEntity> = emptyList(),
  val customSpyLocations: List<SpyLocation> = emptyList(),
  val customCharadesWords: List<CustomCharadesWordEntity> = emptyList(),
  val customQuestionsCountMap: Map<String, Int> = emptyMap(),
  val currentDeck: List<CardItem> = emptyList(),
  val currentCardIndex: Int = 0,
  val isCardFlipped: Boolean = false,
  val activePenalty: PenaltyItem? = null,
  val penaltyTargetPlayer: Player? = null,
  val pickedRoulettePlayer: Player? = null,
  val isDarkMode: Boolean = true,
  val isRouletteSpinning: Boolean = false,
  val loopGameState: LoopGameState = LoopGameState(),
  val spyGameState: SpyGameState = SpyGameState(),
  val charadesGameState: CharadesGameState = CharadesGameState(),
  val mafiaGameState: MafiaGameState = MafiaGameState(),
  val cardVotes: Map<String, Int> = emptyMap(),
  val randomTargetPlayer: Player? = null,
  val maxRounds: Int = 0,
  val currentRound: Int = 1,
  val isGameFinished: Boolean = false,
  val playerStatsMap: Map<String, PlayerSessionStats> = emptyMap(),
  val totalSessionCardsPlayed: Int = 0
) {
  val allCategories: List<GameCategory>
    get() = GameCategory.defaultCategories + customCategories

  val currentCard: CardItem?
    get() = if (currentDeck.isNotEmpty() && currentCardIndex in currentDeck.indices) {
      currentDeck[currentCardIndex]
    } else null

  val progressPercent: Float
    get() = if (currentDeck.isNotEmpty()) {
      (currentCardIndex + 1).toFloat() / currentDeck.size.toFloat()
    } else 0f
}

class PartyGameViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: CustomCategoryRepository
  private val _uiState = MutableStateFlow(GameUiState())
  val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

  init {
    val database = PartyAppDatabase.getDatabase(application)
    repository = CustomCategoryRepository(database.customPartyDao())

    // Load initial built-in deck
    loadDeckForCategory(GameCategory.WHO_IS_MOST_LIKELY)

    val flow1 = combine(
      repository.customCategories,
      repository.allCustomQuestions,
      repository.allDeletedItems
    ) { cats, qs, dels ->
      Triple(cats, qs, dels)
    }

    val flow2 = combine(
      repository.allCustomLoopWords,
      repository.allCustomSpyLocations,
      repository.allCustomCharadesWords
    ) { loopWords, spyLocs, charadesWords ->
      Triple(loopWords, spyLocs, charadesWords)
    }

    // Observe custom categories, questions, deleted items, loop words, spy locations, and charades words from Room
    viewModelScope.launch {
      combine(flow1, flow2) { f1, f2 ->
        val catEntities = f1.first
        if (catEntities.isEmpty()) {
          repository.seedSampleCategoryIfEmpty(0)
        }
        AdminDataPayload(
          catEntities = f1.first,
          qEntities = f1.second,
          delEntities = f1.third,
          loopEntities = f2.first,
          spyEntities = f2.second,
          charadesEntities = f2.third
        )
      }.collect { payload ->
        val deletedIds = payload.delEntities.map { it.itemId }.toSet()

        val customCats = payload.catEntities.map { entity ->
          GameCategory(
            id = entity.id,
            titleArabic = entity.title,
            subtitleArabic = entity.subtitle,
            iconEmoji = entity.iconEmoji,
            primaryColor = colorFromStoredLong(entity.colorHex),
            isCustom = true
          )
        }
        val allCatsMap = (GameCategory.defaultCategories + customCats).associateBy { it.id }

        val customCards = payload.qEntities.mapNotNull { q ->
          val cat = allCatsMap[q.categoryId]
          if (cat != null) {
            CardItem(
              id = q.id,
              category = cat,
              questionArabic = q.questionArabic,
              optionA = q.optionA,
              optionB = q.optionB,
              hintArabic = q.hintArabic
            )
          } else null
        }

        val customSpies = payload.spyEntities.map { entity ->
          SpyLocation(
            id = entity.id,
            nameArabic = entity.nameArabic,
            iconEmoji = entity.iconEmoji,
            descriptionArabic = entity.descriptionArabic,
            roles = entity.rolesListRaw.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            sampleQuestions = entity.sampleQuestionsRaw.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
          )
        }

        val countsMap = (customCards.groupBy { it.category.id }.mapValues { it.value.size })

        _uiState.update { state ->
          val updatedState = state.copy(
            customCategories = customCats,
            allCustomCards = customCards,
            deletedItemIds = deletedIds,
            customLoopWords = payload.loopEntities,
            customSpyLocations = customSpies,
            customCharadesWords = payload.charadesEntities,
            customQuestionsCountMap = countsMap
          )

          // Refresh current deck with latest items (always thoroughly shuffled)
          val refreshedDeck = PartyQuestionsData.getAllQuestionsForCategory(
            category = state.selectedCategory,
            customCards = customCards,
            deletedIds = deletedIds
          ).shuffled()
          if (refreshedDeck.isNotEmpty()) {
            val clampedIndex = state.currentCardIndex.coerceIn(0, refreshedDeck.size - 1)
            updatedState.copy(currentDeck = refreshedDeck, currentCardIndex = clampedIndex)
          } else {
            updatedState
          }
        }
      }
    }
  }


  fun toggleDarkMode() {
    _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
  }

  fun selectCategory(category: GameCategory) {
    if (category.id == GameCategory.OUT_OF_THE_LOOP.id) {
      startOutOfTheLoopGame()
      _uiState.update {
        it.copy(
          selectedCategory = category,
          currentScreen = ScreenState.OUT_OF_THE_LOOP
        )
      }
    } else if (category.id == GameCategory.THE_SPY.id) {
      startSpyGame()
      _uiState.update {
        it.copy(
          selectedCategory = category,
          currentScreen = ScreenState.THE_SPY
        )
      }
    } else if (category.id == GameCategory.MAFIA.id) {
      startMafiaGame()
      _uiState.update {
        it.copy(
          selectedCategory = category,
          currentScreen = ScreenState.MAFIA
        )
      }
    } else if (category.id == GameCategory.CHARADES.id) {
      startCharadesGame()
      _uiState.update {
        it.copy(
          selectedCategory = category,
          currentScreen = ScreenState.CHARADES
        )
      }
    } else {
      loadDeckForCategory(category)
      _uiState.update {
        it.copy(
          selectedCategory = category,
          currentScreen = ScreenState.GAME,
          isCardFlipped = false,
          cardVotes = emptyMap(),
          randomTargetPlayer = it.players.randomOrNull()
        )
      }
    }
  }

  fun navigateToHome() {
    pauseLoopTimer()
    pauseSpyTimer()
    pauseCharadesTimer()
    pauseMafiaTimer()
    _uiState.update {
      it.copy(
        currentScreen = ScreenState.HOME,
        activePenalty = null,
        pickedRoulettePlayer = null
      )
    }
  }

  fun navigateToSetup() {
    pauseLoopTimer()
    pauseSpyTimer()
    pauseCharadesTimer()
    pauseMafiaTimer()
    _uiState.update { it.copy(currentScreen = ScreenState.SETUP) }
  }

  fun navigateToAdminDashboard() {
    pauseLoopTimer()
    pauseSpyTimer()
    pauseCharadesTimer()
    pauseMafiaTimer()
    _uiState.update { it.copy(currentScreen = ScreenState.ADMIN_DASHBOARD) }
  }

  fun navigateToSessionStats() {
    pauseLoopTimer()
    pauseSpyTimer()
    pauseCharadesTimer()
    pauseMafiaTimer()
    _uiState.update { it.copy(currentScreen = ScreenState.SESSION_STATS) }
  }


  fun startGameFromSetup() {
    if (_uiState.value.players.isEmpty()) {
      addPlayer("رامي")
      addPlayer("سارة")
      addPlayer("خالد")
    }
    _uiState.update { it.copy(currentScreen = ScreenState.HOME) }
  }

  fun navigateToGame() {
    if (_uiState.value.currentDeck.isEmpty()) {
      loadDeckForCategory(_uiState.value.selectedCategory)
    }
    _uiState.update { it.copy(currentScreen = ScreenState.GAME) }
  }

  fun flipCard() {
    _uiState.update { it.copy(isCardFlipped = !it.isCardFlipped) }
  }

  fun nextCard() {
    val state = _uiState.value
    if (state.currentDeck.isEmpty()) return

    // Credit stats for card that was just completed
    val finishedCard = state.currentCard
    val activePlayer = if (state.players.isNotEmpty()) state.players[state.currentCardIndex % state.players.size] else null
    val targetPlayer = state.randomTargetPlayer ?: activePlayer
    if (finishedCard != null && targetPlayer != null) {
      when (finishedCard.category.id) {
        GameCategory.CHALLENGES_PENALTIES.id -> recordChallengeCompleted(targetPlayer.id)
        GameCategory.CONFESSIONS_TRUTH.id -> recordConfessionAnswered(targetPlayer.id)
        GameCategory.WOULD_YOU_RATHER.id -> recordWouldYouRatherAnswered(targetPlayer.id)
      }
    }

    val nextIndex = (state.currentCardIndex + 1) % state.currentDeck.size
    _uiState.update {
      it.copy(
        currentCardIndex = nextIndex,
        totalSessionCardsPlayed = it.totalSessionCardsPlayed + 1,
        isCardFlipped = false,
        activePenalty = null,
        pickedRoulettePlayer = null,
        cardVotes = emptyMap(),
        randomTargetPlayer = it.players.randomOrNull()
      )
    }
  }

  fun previousCard() {
    val state = _uiState.value
    if (state.currentDeck.isEmpty()) return

    val prevIndex = if (state.currentCardIndex - 1 < 0) {
      state.currentDeck.size - 1
    } else {
      state.currentCardIndex - 1
    }
    _uiState.update {
      it.copy(
        currentCardIndex = prevIndex,
        isCardFlipped = false,
        activePenalty = null,
        pickedRoulettePlayer = null,
        cardVotes = emptyMap(),
        randomTargetPlayer = it.players.randomOrNull()
      )
    }
  }

  fun shuffleDeck() {
    _uiState.update { state ->
      val shuffled = state.currentDeck.shuffled()
      state.copy(
        currentDeck = shuffled,
        currentCardIndex = 0,
        isCardFlipped = false,
        activePenalty = null
      )
    }
  }

  fun setMaxRounds(rounds: Int) {
    _uiState.update { it.copy(maxRounds = rounds) }
  }

  fun advanceToNextRound() {
    val state = _uiState.value
    val nextRound = state.currentRound + 1
    _uiState.update {
      it.copy(currentRound = nextRound)
    }
    if (state.selectedCategory.id == GameCategory.OUT_OF_THE_LOOP.id) {
      startOutOfTheLoopGame()
    } else if (state.selectedCategory.id == GameCategory.THE_SPY.id) {
      startSpyGame()
    }
  }

  fun resetRoundsAndScores() {
    _uiState.update { state ->
      state.copy(
        currentRound = 1,
        isGameFinished = false,
        players = state.players.map { it.copy(score = 0, penaltiesCount = 0) }
      )
    }
  }

  fun triggerPenalty() {
    val penalties = PartyQuestionsData.penaltiesList
    val randomPenalty = penalties.random()
    val players = _uiState.value.players
    val randomPlayer = if (players.isNotEmpty()) players.random() else null

    _uiState.update {
      it.copy(
        activePenalty = randomPenalty,
        penaltyTargetPlayer = randomPlayer
      )
    }
  }

  fun dismissPenalty(applyToPlayer: Boolean = false) {
    if (applyToPlayer) {
      val target = _uiState.value.penaltyTargetPlayer
      if (target != null) {
        recordPenaltyTaken(target.id)
        _uiState.update { state ->
          val updatedPlayers = state.players.map { p ->
            if (p.id == target.id) p.copy(penaltiesCount = p.penaltiesCount + 1) else p
          }
          state.copy(players = updatedPlayers, activePenalty = null, penaltyTargetPlayer = null)
        }
        return
      }
    }
    _uiState.update { it.copy(activePenalty = null, penaltyTargetPlayer = null) }
  }

  fun applyPenalty() {
    dismissPenalty(applyToPlayer = true)
  }

  fun rerollPenalty() {
    val penalties = PartyQuestionsData.penaltiesList
    val randomPenalty = penalties.random()
    _uiState.update {
      it.copy(activePenalty = randomPenalty)
    }
  }

  fun advanceMafiaToNextRound() {
    advanceToNextMafiaRound()
  }

  fun spinPlayerRoulette() {
    val players = _uiState.value.players
    if (players.isEmpty()) return

    viewModelScope.launch {
      _uiState.update { it.copy(isRouletteSpinning = true) }
      repeat(10) {
        val randomTemp = players.random()
        _uiState.update { it.copy(pickedRoulettePlayer = randomTemp) }
        delay(100)
      }
      val chosen = players.random()
      _uiState.update {
        it.copy(
          pickedRoulettePlayer = chosen,
          isRouletteSpinning = false
        )
      }
    }
  }

  fun dismissRoulette() {
    _uiState.update { it.copy(pickedRoulettePlayer = null, isRouletteSpinning = false) }
  }

  // --- Players Management ---

  fun addPlayer(name: String) {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return

    _uiState.update { state ->
      val newPlayer = Player(
        id = UUID.randomUUID().toString(),
        name = trimmed,
        avatarColorIndex = state.players.size % 6
      )
      state.copy(players = state.players + newPlayer)
    }
  }

  fun addPlayerAt(name: String, insertIndex: Int) {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return

    _uiState.update { state ->
      val newPlayer = Player(
        id = UUID.randomUUID().toString(),
        name = trimmed,
        avatarColorIndex = state.players.size % 6
      )
      val currentList = state.players.toMutableList()
      val safeIndex = insertIndex.coerceIn(0, currentList.size)
      currentList.add(safeIndex, newPlayer)
      state.copy(players = currentList)
    }
  }

  fun movePlayerUp(playerId: String) {
    _uiState.update { state ->
      val list = state.players.toMutableList()
      val index = list.indexOfFirst { it.id == playerId }
      if (index > 0) {
        val item = list.removeAt(index)
        list.add(index - 1, item)
        state.copy(players = list)
      } else {
        state
      }
    }
  }

  fun movePlayerDown(playerId: String) {
    _uiState.update { state ->
      val list = state.players.toMutableList()
      val index = list.indexOfFirst { it.id == playerId }
      if (index in 0 until list.size - 1) {
        val item = list.removeAt(index)
        list.add(index + 1, item)
        state.copy(players = list)
      } else {
        state
      }
    }
  }

  fun updatePlayerName(playerId: String, newName: String) {
    val trimmed = newName.trim()
    if (trimmed.isEmpty()) return

    _uiState.update { state ->
      state.copy(
        players = state.players.map { p ->
          if (p.id == playerId) p.copy(name = trimmed) else p
        }
      )
    }
  }

  fun removePlayer(playerId: String) {
    _uiState.update { state ->
      state.copy(players = state.players.filterNot { it.id == playerId })
    }
  }

  // --- Voting & Target Player Mechanics ("مين أكتر واحد") ---

  fun voteForPlayer(playerId: String) {
    recordVoteForPlayer(playerId)
    _uiState.update { state ->
      val currentVotes = state.cardVotes.toMutableMap()
      val count = currentVotes.getOrDefault(playerId, 0)
      currentVotes[playerId] = count + 1
      state.copy(cardVotes = currentVotes)
    }
  }

  // --- Session Stats & Funny Titles System (نظام الألقاب وإحصائيات الجلسة) ---

  fun recordChallengeCompleted(playerId: String) {
    updatePlayerStats(playerId) { it.copy(challengesCompleted = it.challengesCompleted + 1, totalInteractions = it.totalInteractions + 1) }
    _uiState.update { state ->
      val updated = state.players.map { if (it.id == playerId) it.copy(score = it.score + 2) else it }
      state.copy(players = updated)
    }
  }

  fun recordConfessionAnswered(playerId: String) {
    updatePlayerStats(playerId) { it.copy(confessionsAnswered = it.confessionsAnswered + 1, totalInteractions = it.totalInteractions + 1) }
    _uiState.update { state ->
      val updated = state.players.map { if (it.id == playerId) it.copy(score = it.score + 1) else it }
      state.copy(players = updated)
    }
  }

  fun recordWouldYouRatherAnswered(playerId: String) {
    updatePlayerStats(playerId) { it.copy(wouldYouRatherAnswered = it.wouldYouRatherAnswered + 1, totalInteractions = it.totalInteractions + 1) }
    _uiState.update { state ->
      val updated = state.players.map { if (it.id == playerId) it.copy(score = it.score + 1) else it }
      state.copy(players = updated)
    }
  }

  fun recordVoteForPlayer(playerId: String) {
    updatePlayerStats(playerId) { it.copy(votesReceivedInWhoIsMost = it.votesReceivedInWhoIsMost + 1, totalInteractions = it.totalInteractions + 1) }
  }

  fun recordPenaltyTaken(playerId: String) {
    updatePlayerStats(playerId) { it.copy(penaltiesTaken = it.penaltiesTaken + 1, totalInteractions = it.totalInteractions + 1) }
  }

  fun recordCharadesWordForPlayer(playerId: String) {
    updatePlayerStats(playerId) { it.copy(charadesWordsGuessed = it.charadesWordsGuessed + 1, totalInteractions = it.totalInteractions + 1) }
    _uiState.update { state ->
      val updated = state.players.map { if (it.id == playerId) it.copy(score = it.score + 1) else it }
      state.copy(players = updated)
    }
  }

  fun updatePlayerStats(playerId: String, transform: (PlayerSessionStats) -> PlayerSessionStats) {
    _uiState.update { state ->
      val currentMap = state.playerStatsMap.toMutableMap()
      val currentStats = currentMap.getOrDefault(playerId, PlayerSessionStats(playerId = playerId))
      currentMap[playerId] = transform(currentStats)
      state.copy(playerStatsMap = currentMap)
    }
  }

  fun resetSessionStats() {
    _uiState.update { state ->
      state.copy(
        playerStatsMap = emptyMap(),
        totalSessionCardsPlayed = 0,
        players = state.players.map { it.copy(penaltiesCount = 0, score = 0) }
      )
    }
  }

  fun getTotalSessionRounds(): Int {
    val s = _uiState.value
    return s.totalSessionCardsPlayed + s.charadesGameState.roundHistory.size + s.currentCardIndex
  }

  fun getPlayersWithTitles(): List<PlayerWithTitle> {
    val state = _uiState.value
    val players = state.players
    val statsMap = state.playerStatsMap

    val defaultTitles = listOf(
      PlayerTitle("مستشار القعدة", "🛋️", "قاعد بالصالون وبراقب الوضع بهدوء وبوزع نصائح بدون ما حدا يطلب!", "حكيم الجلسة", 0xFF795548),
      PlayerTitle("زعيم السوالف", "🗣️", "ما بسكت وبسلك أي قصة وبفتح مواضيع من تحت الأرض!", "راديو متنقل", 0xFF3F51B5),
      PlayerTitle("كاشف المؤامرات", "🔍", "شاكك بالكل وعيونه رادار بتحسب حركة كل لقمة ونظرة!", "المحقق كونان", 0xFF009688),
      PlayerTitle("راعي الفزعات", "🤝", "جاهز يفزع للشباب بأي عقاب وتحدي بدون تردد!", "النشمي الأصيل", 0xFF2196F3),
      PlayerTitle("الهدوء الاستراتيجي", "🤫", "هادي جداً بس حركاته وتصويتاته بتودّي بداهية!", "المخطط الصامت", 0xFF673AB7),
      PlayerTitle("ملك التسليك", "😎", "بضحك وبسلك لأتفه النكت والمواقف كأنه ولا صار إشي!", "دبلوماسي معتمد", 0xFFFF9800),
      PlayerTitle("حريف الشاي والنعنع", "☕", "مسؤول ضبط قعدة الشاي ومتابعة الكاسات بدون ما يلعب!", "كبير القعدة", 0xFF8D6E63),
      PlayerTitle("وزير التبرير", "📋", "عنده تبرير مقنع لأي ورطة وحجة جاهزة لأي انسحاب مفاجئ!", "محامي الشلة", 0xFF607D8B),
      PlayerTitle("ضحكة السهرة", "😂", "ضحكته بتسبق النكتة وبولع الجلسة طاقة إيجابية وضحك!", "دينامو الضحك", 0xFFE91E63),
      PlayerTitle("عمدة الحارة", "🏛️", "بعرف كل تفاصيل الشباب وعنده أرشيف لكل المقالب القديمة!", "الصندوق الأسود", 0xFF303F9F)
    )

    val assignedTitles = mutableSetOf<String>()

    return players.mapIndexed { index, player ->
      val s = statsMap[player.id] ?: PlayerSessionStats(playerId = player.id)
      val totalPenalties = player.penaltiesCount + s.penaltiesTaken
      val score = player.score

      val title: PlayerTitle = when {
        s.challengesCompleted > 0 && s.challengesCompleted >= (statsMap.values.maxOfOrNull { it.challengesCompleted } ?: 0) && "ملك التحديات" !in assignedTitles -> {
          PlayerTitle("ملك التحديات", "⚡", "نفّذ أكبر عدد من الأحكام والتحديات الجريئة بدون ما يرمش له جفن!", "قلبه فولاذي", 0xFFFF5722)
        }
        s.charadesWordsGuessed > 0 && s.charadesWordsGuessed >= (statsMap.values.maxOfOrNull { it.charadesWordsGuessed } ?: 0) && "أسرع حزاز" !in assignedTitles -> {
          PlayerTitle("أسرع حزاز", "🚀", "أسرع بديهة وحزر أصعب الكلمات والأفلام من أول إشارة تمثيل!", "رادار إشارات", 0xFF4CAF50)
        }
        s.confessionsAnswered > 0 && s.confessionsAnswered >= (statsMap.values.maxOfOrNull { it.confessionsAnswered } ?: 0) && "صاحب الصراحة المطلقة" !in assignedTitles -> {
          PlayerTitle("صاحب الصراحة المطلقة", "🕊️", "اعترف بكل أسراره ع المكشوف وما خبى ولا تفصيلة ع الشلة!", "كتاب مفتوح", 0xFF00BCD4)
        }
        s.votesReceivedInWhoIsMost > 0 && s.votesReceivedInWhoIsMost >= (statsMap.values.maxOfOrNull { it.votesReceivedInWhoIsMost } ?: 0) && "كبش فداء الجلسة" !in assignedTitles -> {
          PlayerTitle("كبش فداء الجلسة", "🎯", "الكل اتفق عليه بالإشارة في أسئلة 'مين أكثر واحد' بدون تردد!", "الهدف المفضل", 0xFFE91E63)
        }
        totalPenalties > 0 && totalPenalties >= (players.maxOfOrNull { it.penaltiesCount } ?: 0) && "الضحية الرسمية" !in assignedTitles -> {
          PlayerTitle("الضحية الرسمية", "🎰", "عجلة الروليت والأحكام معلقة معه ونازلة فيه قصف!", "مغناطيس العقوبات", 0xFFFF5252)
        }
        s.wouldYouRatherAnswered > 0 && s.wouldYouRatherAnswered >= (statsMap.values.maxOfOrNull { it.wouldYouRatherAnswered } ?: 0) && "ملك الخيارات المستحيلة" !in assignedTitles -> {
          PlayerTitle("ملك الخيارات المستحيلة", "⚖️", "عنده جواب وقرار حاسم لأعقد وأصعب الخيارات المحرجة!", "حلاّل العقد", 0xFF9C27B0)
        }
        score > 0 && score >= (players.maxOfOrNull { it.score } ?: 0) && "نشمي السهرة" !in assignedTitles -> {
          PlayerTitle("نشمي السهرة", "👑", "متصدر النقاط وجامع أعلى سكور بكل الألعاب والتحديات!", "المتصدر الأول", 0xFFFFD700)
        }
        totalPenalties == 0 && (players.any { it.penaltiesCount > 0 }) && "المحظوظ السالك" !in assignedTitles -> {
          PlayerTitle("المحظوظ السالك", "🍀", "نجا من كل عقاب وورطة كأنه معاه واسطة كونية بالجلسة!", "فالت من العقاب", 0xFF8BC34A)
        }
        s.charadesRoundsPlayed > 0 && "فنان التمثيل الصامت" !in assignedTitles -> {
          PlayerTitle("فنان التمثيل الصامت", "🎭", "أداء درامي أسطوري بالإشارات وصل فريقه للفوز!", "ممثل الأوسكار", 0xFFFF9800)
        }
        s.spyGamesPlayed > 0 && "الجاسوس الشبح" !in assignedTitles -> {
          PlayerTitle("الجاسوس الشبح", "🕵️‍♂️", "أعصابه باردة وبضيع الشلة كلها بدون ما حدا يشك فيه!", "مراوغ داهية", 0xFF607D8B)
        }
        else -> {
          val fallback = defaultTitles.firstOrNull { it.title !in assignedTitles }
            ?: defaultTitles[index % defaultTitles.size]
          fallback
        }
      }
      assignedTitles.add(title.title)
      PlayerWithTitle(player = player, stats = s, title = title)
    }
  }

  fun resetCardVotes() {
    _uiState.update { it.copy(cardVotes = emptyMap()) }
  }

  fun rerollRandomTargetPlayer() {
    _uiState.update { state ->
      val pool = state.players
      if (pool.isEmpty()) return@update state
      val others = pool.filter { it.id != state.randomTargetPlayer?.id }
      val picked = if (others.isNotEmpty()) others.random() else pool.random()
      state.copy(randomTargetPlayer = picked)
    }
  }

  // --- Category 5: "برا السالفة" (Out of the Loop Game) Logic ---

  private var loopTimerJob: Job? = null
  private val recentOutPlayerHistory = mutableListOf<String>()
  private val recentSpyPlayerHistory = mutableListOf<String>()

  private fun pickRandomOutPlayer(players: List<Player>): Player {
    if (players.size <= 1) return players.first()
    for (i in 0 until 10) {
      val candidate = players.random()
      val lastThree = recentOutPlayerHistory.takeLast(3)
      if (lastThree.size == 3 && lastThree.all { it == candidate.id }) {
        continue
      }
      recentOutPlayerHistory.add(candidate.id)
      return candidate
    }
    val fallback = players.random()
    recentOutPlayerHistory.add(fallback.id)
    return fallback
  }

  private fun pickRandomSpyPlayer(players: List<Player>): Player {
    if (players.size <= 1) return players.first()
    for (i in 0 until 10) {
      val candidate = players.random()
      val lastThree = recentSpyPlayerHistory.takeLast(3)
      if (lastThree.size == 3 && lastThree.all { it == candidate.id }) {
        continue
      }
      recentSpyPlayerHistory.add(candidate.id)
      return candidate
    }
    val fallback = players.random()
    recentSpyPlayerHistory.add(fallback.id)
    return fallback
  }

  fun getAllActiveLoopWordsGrouped(): Map<String, List<String>> {
    val deleted = _uiState.value.deletedItemIds
    val grouped = mutableMapOf<String, MutableList<String>>()

    // Built-in topics
    for (topic in PartyQuestionsData.loopTopics) {
      val active = topic.words.filterNot { it in deleted }
      if (active.isNotEmpty()) {
        grouped.getOrPut(topic.categoryName) { mutableListOf() }.addAll(active)
      }
    }

    // Custom loop words added by admin
    for (customWord in _uiState.value.customLoopWords) {
      if (customWord.word !in deleted && customWord.id !in deleted) {
        grouped.getOrPut(customWord.categoryName) { mutableListOf() }.add(customWord.word)
      }
    }

    return grouped
  }

  fun startOutOfTheLoopGame() {
    loopTimerJob?.cancel()
    val players = _uiState.value.players
    if (players.isEmpty()) return

    val grouped = getAllActiveLoopWordsGrouped()
    val availableTopics = grouped.filterValues { it.isNotEmpty() }
    val (catName, wordsList) = if (availableTopics.isNotEmpty()) {
      availableTopics.entries.random()
    } else {
      MapEntry("أكلات أردنية", listOf("منسف", "جميد كركي", "شاورما"))
    }
    val word = wordsList.random()
    val outPlayer = pickRandomOutPlayer(players)

    val assignments = players.map { player ->
      LoopRoleAssignment(
        player = player,
        isOutOfTheLoop = player.id == outPlayer.id
      )
    }

    _uiState.update {
      it.copy(
        loopGameState = LoopGameState(
          categoryName = catName,
          secretWord = word,
          outPlayerId = outPlayer.id,
          assignments = assignments,
          currentPassingPlayerIndex = 0,
          phase = LoopPhase.PASS_PHONE,
          timerSecondsRemaining = 180,
          isTimerRunning = false,
          turnPhase = 1,
          currentTurnOrderIndex = 0,
          suspectedPlayerId = null,
          playerVotes = emptyMap(),
          awardedPoints = emptyMap(),
          guessWordAttempt = "",
          isWordGuessedCorrectly = null,
          isRoundOver = false
        )
      )
    }
  }

  fun advanceLoopPassPhone() {
    val currentLoop = _uiState.value.loopGameState
    val nextIndex = currentLoop.currentPassingPlayerIndex + 1
    if (nextIndex < currentLoop.assignments.size) {
      _uiState.update {
        it.copy(loopGameState = currentLoop.copy(currentPassingPlayerIndex = nextIndex))
      }
    } else {
      _uiState.update {
        it.copy(
          loopGameState = currentLoop.copy(
            phase = LoopPhase.DISCUSSION,
            isTimerRunning = true,
            turnPhase = 1,
            currentTurnOrderIndex = 0
          )
        )
      }
      startLoopTimer()
    }
  }

  fun advanceLoopTurnOrder() {
    val currentLoop = _uiState.value.loopGameState
    val players = _uiState.value.players
    val nextIdx = currentLoop.currentTurnOrderIndex + 1
    if (nextIdx < players.size) {
      _uiState.update {
        it.copy(loopGameState = currentLoop.copy(currentTurnOrderIndex = nextIdx))
      }
    } else {
      // Completed sequential round -> Move to Round 2 free-for-all asking!
      _uiState.update {
        it.copy(
          loopGameState = currentLoop.copy(
            turnPhase = 2,
            currentTurnOrderIndex = 0,
            timerSecondsRemaining = 120,
            isTimerRunning = true
          )
        )
      }
      startLoopTimer()
    }
  }

  fun switchLoopToFreeAsking() {
    _uiState.update {
      it.copy(
        loopGameState = it.loopGameState.copy(
          turnPhase = 2,
          timerSecondsRemaining = 120,
          isTimerRunning = true
        )
      )
    }
    startLoopTimer()
  }

  fun startLoopTimer() {
    loopTimerJob?.cancel()
    _uiState.update { it.copy(loopGameState = it.loopGameState.copy(isTimerRunning = true)) }
    loopTimerJob = viewModelScope.launch {
      while (_uiState.value.loopGameState.timerSecondsRemaining > 0 && _uiState.value.loopGameState.isTimerRunning) {
        delay(1000)
        _uiState.update {
          val remaining = (it.loopGameState.timerSecondsRemaining - 1).coerceAtLeast(0)
          it.copy(
            loopGameState = it.loopGameState.copy(
              timerSecondsRemaining = remaining,
              isTimerRunning = remaining > 0
            )
          )
        }
      }
    }
  }

  fun pauseLoopTimer() {
    loopTimerJob?.cancel()
    _uiState.update { it.copy(loopGameState = it.loopGameState.copy(isTimerRunning = false)) }
  }

  fun toggleLoopTimer() {
    if (_uiState.value.loopGameState.isTimerRunning) {
      pauseLoopTimer()
    } else {
      startLoopTimer()
    }
  }

  fun proceedToLoopVoting() {
    pauseLoopTimer()
    _uiState.update {
      it.copy(loopGameState = it.loopGameState.copy(phase = LoopPhase.VOTING))
    }
  }

  fun castLoopVote(voterId: String, suspectId: String) {
    val currentVotes = _uiState.value.loopGameState.playerVotes.toMutableMap()
    currentVotes[voterId] = suspectId
    _uiState.update {
      it.copy(
        loopGameState = it.loopGameState.copy(
          playerVotes = currentVotes
        )
      )
    }
  }

  fun selectLoopSuspect(suspectPlayerId: String) {
    finalizeLoopVoting(suspectPlayerId)
  }

  fun finalizeLoopVoting(suspectPlayerId: String? = null) {
    pauseLoopTimer()
    val state = _uiState.value
    val loop = state.loopGameState
    val players = state.players
    val outPlayerId = loop.outPlayerId

    val finalSuspect = suspectPlayerId ?: run {
      if (loop.playerVotes.isNotEmpty()) {
        loop.playerVotes.values.groupBy { it }.maxByOrNull { it.value.size }?.key ?: outPlayerId
      } else outPlayerId
    }

    // Scoring: Players who guessed Out of the Loop correctly get 1 point; wrong guess = 0 points.
    val pointsMap = mutableMapOf<String, Int>()
    for (player in players) {
      if (player.id == outPlayerId) continue
      val votedFor = loop.playerVotes[player.id] ?: (if (suspectPlayerId == outPlayerId) outPlayerId else "")
      if (votedFor == outPlayerId) {
        pointsMap[player.id] = 1
      } else {
        pointsMap[player.id] = 0
      }
    }

    // If Out of the Loop escaped detection by the group, they get 2 points!
    if (finalSuspect != outPlayerId) {
      pointsMap[outPlayerId] = 2
    }

    val updatedPlayers = players.map { p ->
      val pts = pointsMap[p.id] ?: 0
      p.copy(score = p.score + pts)
    }

    val isFinished = state.maxRounds > 0 && state.currentRound >= state.maxRounds

    _uiState.update {
      it.copy(
        players = updatedPlayers,
        loopGameState = loop.copy(
          suspectedPlayerId = finalSuspect,
          phase = LoopPhase.REVEAL,
          awardedPoints = pointsMap,
          isRoundOver = true
        ),
        isGameFinished = isFinished
      )
    }
  }

  fun guessLoopWord(guess: String) {
    val state = _uiState.value
    val currentLoop = state.loopGameState
    val normalizedGuess = guess.trim()
    val isCorrect = normalizedGuess.equals(currentLoop.secretWord.trim(), ignoreCase = true) ||
      (currentLoop.secretWord.contains(normalizedGuess, ignoreCase = true) && normalizedGuess.length >= 3)

    val pointsMap = currentLoop.awardedPoints.toMutableMap()
    val outPlayerId = currentLoop.outPlayerId

    // Scoring: If Out of the Loop person figures out the secret word, they get 2 points; otherwise = 0 points.
    val outPoints = if (isCorrect) 2 else 0
    pointsMap[outPlayerId] = outPoints

    val updatedPlayers = state.players.map { p ->
      if (p.id == outPlayerId) {
        p.copy(score = p.score + outPoints)
      } else {
        p
      }
    }

    _uiState.update {
      it.copy(
        players = updatedPlayers,
        loopGameState = currentLoop.copy(
          guessWordAttempt = guess,
          isWordGuessedCorrectly = isCorrect,
          awardedPoints = pointsMap
        )
      )
    }
  }

  // --- Category 6: "الجاسوس" (The Spy Game) Logic ---

  private var spyTimerJob: Job? = null

  fun getAllActiveSpyLocations(): List<SpyLocation> {
    val deleted = _uiState.value.deletedItemIds
    val builtInActive = PartyQuestionsData.spyLocations.filterNot { it.id in deleted }
    val customActive = _uiState.value.customSpyLocations.filterNot { it.id in deleted }
    return builtInActive + customActive
  }

  fun startSpyGame() {
    spyTimerJob?.cancel()
    val players = _uiState.value.players
    if (players.isEmpty()) return

    val locations = getAllActiveSpyLocations()
    val location = if (locations.isNotEmpty()) locations.random() else PartyQuestionsData.spyLocations.first()
    val spyPlayer = pickRandomSpyPlayer(players)
    val shuffledRoles = (location.roles + location.roles).shuffled()

    var roleIdx = 0
    val assignments = players.map { player ->
      if (player.id == spyPlayer.id) {
        SpyRoleAssignment(player = player, isSpy = true, roleName = "الجاسوس 🕵️‍♂️")
      } else {
        val role = shuffledRoles.getOrElse(roleIdx++) { "مواطن بالمكان" }
        SpyRoleAssignment(player = player, isSpy = false, roleName = role)
      }
    }

    val asker = players.random()
    val answerer = players.filter { it.id != asker.id }.randomOrNull() ?: asker

    _uiState.update {
      it.copy(
        spyGameState = SpyGameState(
          location = location,
          spyPlayerId = spyPlayer.id,
          assignments = assignments,
          currentPassingPlayerIndex = 0,
          phase = SpyPhase.PASS_PHONE,
          timerSecondsRemaining = 300,
          isTimerRunning = false,
          currentAskerName = asker.name,
          currentAnswererName = answerer.name,
          accusedPlayerId = null,
          playerVotes = emptyMap(),
          awardedPoints = emptyMap(),
          spyGuessedLocationId = null,
          isSpyLocationGuessedCorrectly = null,
          isRoundOver = false
        )
      )
    }
  }

  fun advanceSpyPassPhone() {
    val currentSpy = _uiState.value.spyGameState
    val nextIndex = currentSpy.currentPassingPlayerIndex + 1
    if (nextIndex < currentSpy.assignments.size) {
      _uiState.update {
        it.copy(spyGameState = currentSpy.copy(currentPassingPlayerIndex = nextIndex))
      }
    } else {
      _uiState.update {
        it.copy(
          spyGameState = currentSpy.copy(
            phase = SpyPhase.QUESTIONING,
            isTimerRunning = true
          )
        )
      }
      startSpyTimer()
    }
  }

  fun startSpyTimer() {
    spyTimerJob?.cancel()
    _uiState.update { it.copy(spyGameState = it.spyGameState.copy(isTimerRunning = true)) }
    spyTimerJob = viewModelScope.launch {
      while (_uiState.value.spyGameState.timerSecondsRemaining > 0 && _uiState.value.spyGameState.isTimerRunning) {
        delay(1000)
        _uiState.update {
          val remaining = (it.spyGameState.timerSecondsRemaining - 1).coerceAtLeast(0)
          it.copy(
            spyGameState = it.spyGameState.copy(
              timerSecondsRemaining = remaining,
              isTimerRunning = remaining > 0
            )
          )
        }
      }
    }
  }

  fun pauseSpyTimer() {
    spyTimerJob?.cancel()
    _uiState.update { it.copy(spyGameState = it.spyGameState.copy(isTimerRunning = false)) }
  }

  fun toggleSpyTimer() {
    if (_uiState.value.spyGameState.isTimerRunning) {
      pauseSpyTimer()
    } else {
      startSpyTimer()
    }
  }

  fun proceedToSpyAccusation() {
    pauseSpyTimer()
    _uiState.update {
      it.copy(spyGameState = it.spyGameState.copy(phase = SpyPhase.ACCUSATION))
    }
  }

  fun castSpyVote(voterId: String, suspectId: String) {
    val currentVotes = _uiState.value.spyGameState.playerVotes.toMutableMap()
    currentVotes[voterId] = suspectId
    _uiState.update {
      it.copy(
        spyGameState = it.spyGameState.copy(
          playerVotes = currentVotes
        )
      )
    }
  }

  fun accuseSpyPlayer(playerId: String) {
    finalizeSpyVoting(playerId)
  }

  fun finalizeSpyVoting(accusedPlayerId: String? = null) {
    pauseSpyTimer()
    val state = _uiState.value
    val spy = state.spyGameState
    val players = state.players
    val spyPlayerId = spy.spyPlayerId

    val finalAccused = accusedPlayerId ?: run {
      if (spy.playerVotes.isNotEmpty()) {
        spy.playerVotes.values.groupBy { it }.maxByOrNull { it.value.size }?.key ?: spyPlayerId
      } else spyPlayerId
    }

    // Scoring: Players who guessed correctly get 1 point; wrong guess = 0 points.
    val pointsMap = mutableMapOf<String, Int>()
    for (player in players) {
      if (player.id == spyPlayerId) continue
      val votedFor = spy.playerVotes[player.id] ?: (if (accusedPlayerId == spyPlayerId) spyPlayerId else "")
      if (votedFor == spyPlayerId) {
        pointsMap[player.id] = 1
      } else {
        pointsMap[player.id] = 0
      }
    }

    // If Spy escaped detection by the group, the spy gets 2 points!
    if (finalAccused != spyPlayerId) {
      pointsMap[spyPlayerId] = 2
    }

    val updatedPlayers = players.map { p ->
      val pts = pointsMap[p.id] ?: 0
      p.copy(score = p.score + pts)
    }

    val isFinished = state.maxRounds > 0 && state.currentRound >= state.maxRounds

    _uiState.update {
      it.copy(
        players = updatedPlayers,
        spyGameState = spy.copy(
          accusedPlayerId = finalAccused,
          phase = SpyPhase.REVEAL,
          awardedPoints = pointsMap,
          isRoundOver = true
        ),
        isGameFinished = isFinished
      )
    }
  }

  fun spyGuessLocation(locationId: String) {
    val state = _uiState.value
    val currentSpy = state.spyGameState
    val actualLocation = currentSpy.location
    val isCorrect = locationId == actualLocation?.id

    val pointsMap = currentSpy.awardedPoints.toMutableMap()
    val spyPlayerId = currentSpy.spyPlayerId

    // Scoring: If Spy figures out the secret location, they get 2 points; otherwise = 0 points.
    val spyPoints = if (isCorrect) 2 else 0
    pointsMap[spyPlayerId] = (pointsMap[spyPlayerId] ?: 0) + spyPoints

    val updatedPlayers = state.players.map { p ->
      if (p.id == spyPlayerId) {
        p.copy(score = p.score + spyPoints)
      } else {
        p
      }
    }

    val isFinished = state.maxRounds > 0 && state.currentRound >= state.maxRounds

    _uiState.update {
      it.copy(
        players = updatedPlayers,
        spyGameState = currentSpy.copy(
          spyGuessedLocationId = locationId,
          isSpyLocationGuessedCorrectly = isCorrect,
          awardedPoints = pointsMap,
          phase = SpyPhase.REVEAL,
          isRoundOver = true
        ),
        isGameFinished = isFinished
      )
    }
  }

  fun pickNextQuestionPair() {
    val players = _uiState.value.players
    if (players.size < 2) return

    val currentAsker = _uiState.value.spyGameState.currentAskerName
    val asker = players.random()
    val answerer = players.filter { it.name != asker.name && it.name != currentAsker }.randomOrNull()
      ?: players.filter { it.name != asker.name }.randomOrNull()
      ?: asker

    _uiState.update {
      it.copy(
        spyGameState = it.spyGameState.copy(
          currentAskerName = asker.name,
          currentAnswererName = answerer.name
        )
      )
    }
  }

  fun nextSpyTurn() {
    pickNextQuestionPair()
  }

  fun accuseSpy(spyId: String) {
    accuseSpyPlayer(spyId)
  }

  // --- Dynamic Content Management (Admin Controls) ---

  fun addQuestion(
    categoryId: String,
    questionText: String,
    optionA: String? = null,
    optionB: String? = null,
    hint: String? = null
  ) {
    val trimmed = questionText.trim()
    if (trimmed.isBlank()) return

    viewModelScope.launch {
      val question = CustomQuestionEntity(
        id = UUID.randomUUID().toString(),
        categoryId = categoryId,
        questionArabic = trimmed,
        optionA = optionA?.trim()?.ifEmpty { null },
        optionB = optionB?.trim()?.ifEmpty { null },
        hintArabic = hint?.trim()?.ifEmpty { "سؤال مخصص للجمعة!" }
      )
      repository.saveQuestion(question)
    }
  }

  fun editQuestion(
    questionId: String,
    categoryId: String,
    questionText: String,
    optionA: String? = null,
    optionB: String? = null,
    hint: String? = null
  ) {
    val trimmed = questionText.trim()
    if (trimmed.isBlank()) return

    viewModelScope.launch {
      val question = CustomQuestionEntity(
        id = questionId,
        categoryId = categoryId,
        questionArabic = trimmed,
        optionA = optionA?.trim()?.ifEmpty { null },
        optionB = optionB?.trim()?.ifEmpty { null },
        hintArabic = hint?.trim()?.ifEmpty { "كرت معدل" }
      )
      repository.saveQuestion(question)
      // If it was marked as deleted previously, unmark it
      repository.unmarkItemDeleted(questionId)
    }
  }

  fun deleteQuestion(questionId: String, categoryId: String) {
    viewModelScope.launch {
      repository.deleteQuestion(questionId)
      repository.markItemDeleted(questionId, "QUESTION")
    }
  }

  fun addLoopWord(categoryName: String, word: String, iconEmoji: String = "🇯🇴") {
    val trimmedWord = word.trim()
    val trimmedCat = categoryName.trim().ifEmpty { "كلمات وسوالف أردنية" }
    if (trimmedWord.isBlank()) return

    viewModelScope.launch {
      val entity = CustomLoopWordEntity(
        id = UUID.randomUUID().toString(),
        categoryName = trimmedCat,
        word = trimmedWord,
        iconEmoji = iconEmoji
      )
      repository.saveCustomLoopWord(entity)
    }
  }

  fun deleteLoopWord(wordTextOrId: String) {
    viewModelScope.launch {
      repository.markItemDeleted(wordTextOrId, "LOOP_WORD")
      repository.deleteCustomLoopWord(wordTextOrId)
    }
  }

  fun addSpyLocation(
    nameArabic: String,
    iconEmoji: String,
    descriptionArabic: String,
    roles: List<String>,
    sampleQuestions: List<String>
  ) {
    val trimmedName = nameArabic.trim()
    if (trimmedName.isBlank()) return

    viewModelScope.launch {
      val entity = CustomSpyLocationEntity(
        id = UUID.randomUUID().toString(),
        nameArabic = trimmedName,
        iconEmoji = iconEmoji.ifEmpty { "📍" },
        descriptionArabic = descriptionArabic.trim().ifEmpty { "مكان أردني سري!" },
        rolesListRaw = roles.joinToString(","),
        sampleQuestionsRaw = sampleQuestions.joinToString("\n")
      )
      repository.saveCustomSpyLocation(entity)
    }
  }

  fun deleteSpyLocation(locationId: String) {
    viewModelScope.launch {
      repository.markItemDeleted(locationId, "SPY_LOCATION")
      repository.deleteCustomSpyLocation(locationId)
    }
  }

  fun createCustomCategory(
    title: String,
    subtitle: String,
    iconEmoji: String,
    primaryColor: Color,
    initialQuestions: List<String>,
    onComplete: (GameCategory) -> Unit = {}
  ) {
    viewModelScope.launch {
      val catId = UUID.randomUUID().toString()
      val catEntity = CustomCategoryEntity(
        id = catId,
        title = title.trim(),
        subtitle = subtitle.trim().ifEmpty { "أسئلة وسوالف مخصصة لشلتنا!" },
        iconEmoji = iconEmoji.ifEmpty { "👥" },
        colorHex = colorToStoredLong(primaryColor)
      )
      repository.saveCategory(catEntity)

      initialQuestions.filter { it.isNotBlank() }.forEach { qText ->
        repository.saveQuestion(
          CustomQuestionEntity(
            id = UUID.randomUUID().toString(),
            categoryId = catId,
            questionArabic = qText.trim(),
            hintArabic = "كرت مخصص لشلتكم!"
          )
        )
      }

      val newCategory = GameCategory(
        id = catId,
        titleArabic = catEntity.title,
        subtitleArabic = catEntity.subtitle,
        iconEmoji = catEntity.iconEmoji,
        primaryColor = primaryColor,
        isCustom = true
      )
      onComplete(newCategory)
    }
  }

  fun deleteCustomCategory(categoryId: String) {
    viewModelScope.launch {
      repository.deleteCategory(categoryId)
      if (_uiState.value.selectedCategory.id == categoryId) {
        selectCategory(GameCategory.WHO_IS_MOST_LIKELY)
      }
    }
  }

  fun getQuestionsForCategoryFiltered(category: GameCategory): List<CardItem> {
    return PartyQuestionsData.getAllQuestionsForCategory(
      category = category,
      customCards = _uiState.value.allCustomCards,
      deletedIds = _uiState.value.deletedItemIds
    )
  }

  private fun loadDeckForCategory(category: GameCategory) {
    val questions = PartyQuestionsData.getAllQuestionsForCategory(
      category = category,
      customCards = _uiState.value.allCustomCards,
      deletedIds = _uiState.value.deletedItemIds
    )
    val finalDeck = if (questions.isNotEmpty()) {
      questions.shuffled()
    } else {
      listOf(
        CardItem(
          id = "empty_${category.id}",
          category = category,
          questionArabic = "لا توجد أسئلة مضافة في هذه الفئة بعد! أضف كروت جديدة من لوحة الأدمن.",
          hintArabic = "شاركوا مواقفكم وأسراركم الخاصة!"
        )
      )
    }

    _uiState.update {
      it.copy(
        currentDeck = finalDeck,
        currentCardIndex = 0,
        isCardFlipped = false,
        activePenalty = null
      )
    }
  }

  fun addQuestionToCategory(categoryId: String, questionArabic: String) {
    addQuestion(categoryId, questionArabic)
  }

  fun deleteQuestion(questionId: String) {
    deleteQuestion(questionId, "")
  }

  // --- Category 7: "ولا كلمة" (Charades Game) Logic ---

  private var charadesTimerJob: Job? = null

  fun getAllActiveCharadesWords(subCategory: CharadesSubCategory = _uiState.value.charadesGameState.selectedSubCategory): List<CharadesWordItem> {
    return PartyQuestionsData.getCharadesWords(
      subCategory = subCategory,
      customWords = _uiState.value.customCharadesWords,
      deletedIds = _uiState.value.deletedItemIds
    )
  }

  fun selectCharadesSubCategory(subCategory: CharadesSubCategory) {
    pauseCharadesTimer()
    val players = _uiState.value.players
    val (teamA, teamB) = if (players.size >= 2) {
      val shuffled = players.shuffled()
      val half = (shuffled.size + 1) / 2
      Pair(shuffled.take(half), shuffled.drop(half))
    } else {
      Pair(players, emptyList())
    }

    val allWords = getAllActiveCharadesWords(subCategory).shuffled()
    val firstWord = allWords.firstOrNull()

    val currentSubState = _uiState.value.charadesGameState
    _uiState.update {
      it.copy(
        charadesGameState = CharadesGameState(
          teamAScore = 0,
          teamBScore = 0,
          teamAPlayers = teamA,
          teamBPlayers = teamB,
          teamAName = currentSubState.teamAName,
          teamBName = currentSubState.teamBName,
          playMode = currentSubState.playMode,
          currentTurnTeam = Team.TEAM_A,
          currentActorPlayer = teamA.firstOrNull(),
          currentWordItem = firstWord,
          playedWordIdsInGame = emptySet(),
          timerSecondsRemaining = 60,
          isTimerRunning = false,
          selectedSubCategory = subCategory,
          phase = CharadesPhase.READY,
          roundNumber = 1,
          wordsCorrectThisRound = emptyList(),
          wordsSkippedThisRound = emptyList()
        )
      )
    }
  }

  fun updateCharadesTeamNames(teamAName: String, teamBName: String) {
    val cleanA = teamAName.trim().ifEmpty { "فريق النشمي" }
    val cleanB = teamBName.trim().ifEmpty { "فريق النشامى" }
    _uiState.update {
      it.copy(
        charadesGameState = it.charadesGameState.copy(
          teamAName = cleanA,
          teamBName = cleanB
        )
      )
    }
  }

  fun setCharadesPlayMode(mode: CharadesPlayMode) {
    _uiState.update {
      it.copy(
        charadesGameState = it.charadesGameState.copy(playMode = mode)
      )
    }
  }

  fun openCharadesSubCategorySelection() {
    pauseCharadesTimer()
    _uiState.update {
      it.copy(
        charadesGameState = it.charadesGameState.copy(
          phase = CharadesPhase.CATEGORY_SELECTION,
          isTimerRunning = false
        )
      )
    }
  }

  fun startCharadesGame() {
    pauseCharadesTimer()
    val players = _uiState.value.players
    val (teamA, teamB) = if (players.size >= 2) {
      val shuffled = players.shuffled()
      val half = (shuffled.size + 1) / 2
      Pair(shuffled.take(half), shuffled.drop(half))
    } else {
      Pair(players, emptyList())
    }

    val currentSubState = _uiState.value.charadesGameState
    val subCat = currentSubState.selectedSubCategory
    val allWords = getAllActiveCharadesWords(subCat).shuffled()
    val firstWord = allWords.firstOrNull()

    _uiState.update {
      it.copy(
        charadesGameState = CharadesGameState(
          teamAScore = 0,
          teamBScore = 0,
          teamAPlayers = teamA,
          teamBPlayers = teamB,
          teamAName = currentSubState.teamAName,
          teamBName = currentSubState.teamBName,
          playMode = currentSubState.playMode,
          currentTurnTeam = Team.TEAM_A,
          currentActorPlayer = teamA.firstOrNull(),
          currentWordItem = firstWord,
          playedWordIdsInGame = emptySet(),
          timerSecondsRemaining = 60,
          isTimerRunning = false,
          selectedSubCategory = subCat,
          phase = CharadesPhase.CATEGORY_SELECTION,
          roundNumber = 1,
          wordsCorrectThisRound = emptyList(),
          wordsSkippedThisRound = emptyList()
        )
      )
    }
  }

  private fun createCharadesRoundHistoryItem(
    state: CharadesGameState,
    correct: List<String> = state.wordsCorrectThisRound,
    skipped: List<String> = state.wordsSkippedThisRound,
    scoreA: Int = state.teamAScore,
    scoreB: Int = state.teamBScore
  ): CharadesRoundHistoryItem {
    return CharadesRoundHistoryItem(
      roundNumber = state.roundNumber,
      team = state.currentTurnTeam,
      teamName = state.getTeamName(state.currentTurnTeam),
      actorName = state.currentActorPlayer?.name,
      correctCount = correct.size,
      correctWords = correct,
      skippedWords = skipped,
      teamAScoreAfter = scoreA,
      teamBScoreAfter = scoreB
    )
  }

  fun clearCharadesHistory() {
    charadesTimerJob?.cancel()
    _uiState.update {
      val cur = it.charadesGameState
      it.copy(
        charadesGameState = cur.copy(
          teamAScore = 0,
          teamBScore = 0,
          roundNumber = 1,
          roundHistory = emptyList(),
          wordsCorrectThisRound = emptyList(),
          wordsSkippedThisRound = emptyList(),
          playedWordIdsInGame = emptySet(),
          timerSecondsRemaining = 60,
          isTimerRunning = false
        )
      )
    }
  }

  fun startCharadesRound() {
    charadesTimerJob?.cancel()
    val state = _uiState.value.charadesGameState
    val unplayedWords = getAllActiveCharadesWords().filterNot { it.id in state.playedWordIdsInGame }
    val nextWord = unplayedWords.randomOrNull() ?: getAllActiveCharadesWords().randomOrNull()

    _uiState.update {
      it.copy(
        charadesGameState = it.charadesGameState.copy(
          phase = CharadesPhase.PLAYING,
          timerSecondsRemaining = 60,
          isTimerRunning = true,
          currentWordItem = nextWord,
          wordsCorrectThisRound = emptyList(),
          wordsSkippedThisRound = emptyList()
        )
      )
    }

    charadesTimerJob = viewModelScope.launch {
      while (_uiState.value.charadesGameState.timerSecondsRemaining > 0 &&
        _uiState.value.charadesGameState.isTimerRunning &&
        _uiState.value.charadesGameState.phase == CharadesPhase.PLAYING
      ) {
        delay(1000L)
        val remaining = _uiState.value.charadesGameState.timerSecondsRemaining - 1
        if (remaining <= 0) {
          _uiState.update {
            val cur = it.charadesGameState
            val historyItem = createCharadesRoundHistoryItem(cur)
            it.copy(
              charadesGameState = cur.copy(
                timerSecondsRemaining = 0,
                isTimerRunning = false,
                phase = CharadesPhase.ROUND_ENDED,
                roundHistory = cur.roundHistory + historyItem
              )
            )
          }
          break
        } else {
          _uiState.update {
            it.copy(
              charadesGameState = it.charadesGameState.copy(
                timerSecondsRemaining = remaining
              )
            )
          }
        }
      }
    }
  }

  fun pauseCharadesTimer() {
    charadesTimerJob?.cancel()
    _uiState.update {
      it.copy(charadesGameState = it.charadesGameState.copy(isTimerRunning = false))
    }
  }

  fun resumeCharadesTimer() {
    val current = _uiState.value.charadesGameState
    if (!current.isTimerRunning && current.phase == CharadesPhase.PLAYING && current.timerSecondsRemaining > 0) {
      charadesTimerJob?.cancel()
      _uiState.update {
        it.copy(charadesGameState = it.charadesGameState.copy(isTimerRunning = true))
      }
      charadesTimerJob = viewModelScope.launch {
        while (_uiState.value.charadesGameState.timerSecondsRemaining > 0 &&
          _uiState.value.charadesGameState.isTimerRunning &&
          _uiState.value.charadesGameState.phase == CharadesPhase.PLAYING
        ) {
          delay(1000L)
          val remaining = _uiState.value.charadesGameState.timerSecondsRemaining - 1
          if (remaining <= 0) {
            _uiState.update {
              val cur = it.charadesGameState
              val historyItem = createCharadesRoundHistoryItem(cur)
              it.copy(
                charadesGameState = cur.copy(
                  timerSecondsRemaining = 0,
                  isTimerRunning = false,
                  phase = CharadesPhase.ROUND_ENDED,
                  roundHistory = cur.roundHistory + historyItem
                )
              )
            }
            break
          } else {
            _uiState.update {
              it.copy(
                charadesGameState = it.charadesGameState.copy(
                  timerSecondsRemaining = remaining
                )
              )
            }
          }
        }
      }
    }
  }

  fun onCharadesCorrectGuess() {
    val charadesState = _uiState.value.charadesGameState
    if (charadesState.phase != CharadesPhase.PLAYING) return

    val actor = charadesState.currentActorPlayer
    if (actor != null) {
      recordCharadesWordForPlayer(actor.id)
    }

    val currentWord = charadesState.currentWordItem
    val newPlayed = if (currentWord != null) charadesState.playedWordIdsInGame + currentWord.id else charadesState.playedWordIdsInGame
    val newCorrect = if (currentWord != null) charadesState.wordsCorrectThisRound + currentWord.word else charadesState.wordsCorrectThisRound

    val newScoreA = if (charadesState.currentTurnTeam == Team.TEAM_A) charadesState.teamAScore + 1 else charadesState.teamAScore
    val newScoreB = if (charadesState.currentTurnTeam == Team.TEAM_B) charadesState.teamBScore + 1 else charadesState.teamBScore

    if (charadesState.playMode == CharadesPlayMode.SINGLE_CARD_PER_ROUND) {
      charadesTimerJob?.cancel()
      _uiState.update {
        val historyItem = createCharadesRoundHistoryItem(
          state = charadesState,
          correct = newCorrect,
          scoreA = newScoreA,
          scoreB = newScoreB
        )
        it.copy(
          charadesGameState = charadesState.copy(
            teamAScore = newScoreA,
            teamBScore = newScoreB,
            wordsCorrectThisRound = newCorrect,
            playedWordIdsInGame = newPlayed,
            timerSecondsRemaining = 0,
            isTimerRunning = false,
            phase = CharadesPhase.ROUND_ENDED,
            roundHistory = charadesState.roundHistory + historyItem
          )
        )
      }
      return
    }

    val availableWords = getAllActiveCharadesWords().filterNot { it.id in newPlayed }
    val nextWord = availableWords.randomOrNull() ?: getAllActiveCharadesWords().randomOrNull()

    _uiState.update {
      it.copy(
        charadesGameState = charadesState.copy(
          teamAScore = newScoreA,
          teamBScore = newScoreB,
          wordsCorrectThisRound = newCorrect,
          playedWordIdsInGame = newPlayed,
          currentWordItem = nextWord
        )
      )
    }
  }

  fun onCharadesSkipWord() {
    val charadesState = _uiState.value.charadesGameState
    if (charadesState.phase != CharadesPhase.PLAYING) return

    val currentWord = charadesState.currentWordItem
    val newPlayed = if (currentWord != null) charadesState.playedWordIdsInGame + currentWord.id else charadesState.playedWordIdsInGame
    val newSkipped = if (currentWord != null) charadesState.wordsSkippedThisRound + currentWord.word else charadesState.wordsSkippedThisRound

    val availableWords = getAllActiveCharadesWords().filterNot { it.id in newPlayed }
    val nextWord = availableWords.randomOrNull() ?: getAllActiveCharadesWords().randomOrNull()

    _uiState.update {
      it.copy(
        charadesGameState = charadesState.copy(
          wordsSkippedThisRound = newSkipped,
          playedWordIdsInGame = newPlayed,
          currentWordItem = nextWord
        )
      )
    }
  }

  fun nextCharadesTurn() {
    charadesTimerJob?.cancel()
    val charadesState = _uiState.value.charadesGameState
    val nextTeam = if (charadesState.currentTurnTeam == Team.TEAM_A) Team.TEAM_B else Team.TEAM_A
    val nextRound = if (nextTeam == Team.TEAM_A) charadesState.roundNumber + 1 else charadesState.roundNumber

    val teamPlayers = if (nextTeam == Team.TEAM_A) charadesState.teamAPlayers else charadesState.teamBPlayers
    val currentActor = charadesState.currentActorPlayer
    val nextActor = if (teamPlayers.isNotEmpty()) {
      val currentIdx = teamPlayers.indexOfFirst { it.id == currentActor?.id }
      if (currentIdx in teamPlayers.indices) {
        teamPlayers[(currentIdx + 1) % teamPlayers.size]
      } else {
        teamPlayers.first()
      }
    } else null

    val availableWords = getAllActiveCharadesWords().filterNot { it.id in charadesState.playedWordIdsInGame }
    val nextWord = availableWords.randomOrNull() ?: getAllActiveCharadesWords().randomOrNull()

    _uiState.update {
      it.copy(
        charadesGameState = charadesState.copy(
          currentTurnTeam = nextTeam,
          currentActorPlayer = nextActor,
          currentWordItem = nextWord,
          timerSecondsRemaining = 60,
          isTimerRunning = false,
          phase = CharadesPhase.READY,
          roundNumber = nextRound,
          wordsCorrectThisRound = emptyList(),
          wordsSkippedThisRound = emptyList()
        )
      )
    }
  }

  fun switchPlayerTeam(playerId: String) {
    val charadesState = _uiState.value.charadesGameState
    val inA = charadesState.teamAPlayers.any { it.id == playerId }
    val inB = charadesState.teamBPlayers.any { it.id == playerId }

    val newA: List<Player>
    val newB: List<Player>
    if (inA) {
      val p = charadesState.teamAPlayers.first { it.id == playerId }
      newA = charadesState.teamAPlayers.filterNot { it.id == playerId }
      newB = charadesState.teamBPlayers + p
    } else if (inB) {
      val p = charadesState.teamBPlayers.first { it.id == playerId }
      newB = charadesState.teamBPlayers.filterNot { it.id == playerId }
      newA = charadesState.teamAPlayers + p
    } else {
      val p = _uiState.value.players.firstOrNull { it.id == playerId } ?: return
      newA = charadesState.teamAPlayers + p
      newB = charadesState.teamBPlayers
    }

    _uiState.update {
      it.copy(
        charadesGameState = charadesState.copy(
          teamAPlayers = newA,
          teamBPlayers = newB
        )
      )
    }
  }

  fun shuffleTeams() {
    val players = _uiState.value.players.shuffled()
    val half = (players.size + 1) / 2
    val teamA = players.take(half)
    val teamB = players.drop(half)

    _uiState.update {
      it.copy(
        charadesGameState = it.charadesGameState.copy(
          teamAPlayers = teamA,
          teamBPlayers = teamB,
          currentActorPlayer = if (it.charadesGameState.currentTurnTeam == Team.TEAM_A) teamA.firstOrNull() else teamB.firstOrNull()
        )
      )
    }
  }

  fun addCharadesWord(word: String, categoryTag: String = "عام") {
    val trimmed = word.trim()
    if (trimmed.isEmpty()) return
    val id = "custom_charades_" + UUID.randomUUID().toString().take(8)
    viewModelScope.launch {
      repository.saveCustomCharadesWord(
        com.example.data.local.CustomCharadesWordEntity(
          id = id,
          word = trimmed,
          categoryTag = categoryTag
        )
      )
      repository.unmarkItemDeleted(trimmed)
    }
  }

  // --- Category: "المافيا" (Mafia Game) Logic ---

  private var mafiaTimerJob: Job? = null

  fun pauseMafiaTimer() {
    mafiaTimerJob?.cancel()
    _uiState.update {
      it.copy(mafiaGameState = it.mafiaGameState.copy(isTimerRunning = false))
    }
  }

  fun resumeMafiaTimer() {
    val current = _uiState.value.mafiaGameState
    if (current.isTimerRunning) return
    _uiState.update {
      it.copy(mafiaGameState = current.copy(isTimerRunning = true))
    }
    mafiaTimerJob?.cancel()
    mafiaTimerJob = viewModelScope.launch {
      while (_uiState.value.mafiaGameState.timerSecondsRemaining > 0 && _uiState.value.mafiaGameState.isTimerRunning) {
        delay(1000L)
        _uiState.update { state ->
          val curMafia = state.mafiaGameState
          if (curMafia.isTimerRunning && curMafia.timerSecondsRemaining > 0) {
            val newSeconds = curMafia.timerSecondsRemaining - 1
            curMafia.copy(timerSecondsRemaining = newSeconds).let { state.copy(mafiaGameState = it) }
          } else {
            state
          }
        }
      }
      _uiState.update { state ->
        state.copy(mafiaGameState = state.mafiaGameState.copy(isTimerRunning = false))
      }
    }
  }

  fun toggleMafiaTimer() {
    if (_uiState.value.mafiaGameState.isTimerRunning) {
      pauseMafiaTimer()
    } else {
      resumeMafiaTimer()
    }
  }

  fun startMafiaGame() {
    mafiaTimerJob?.cancel()
    val players = _uiState.value.players
    if (players.isEmpty()) return

    // Role assignment based on player count
    val shuffledPlayers = players.shuffled()
    val total = shuffledPlayers.size

    val mafiaCount = when {
      total >= 10 -> 3
      total >= 7 -> 2
      else -> 1
    }

    val rolesList = mutableListOf<MafiaRoleType>()
    repeat(mafiaCount) { rolesList.add(MafiaRoleType.MAFIA) }
    rolesList.add(MafiaRoleType.DETECTIVE)
    if (total >= 4) {
      rolesList.add(MafiaRoleType.DOCTOR)
    }
    while (rolesList.size < total) {
      rolesList.add(MafiaRoleType.VILLAGER)
    }
    val shuffledRoles = rolesList.shuffled()

    val assignments = shuffledPlayers.mapIndexed { index, player ->
      MafiaPlayerRole(
        player = player,
        role = shuffledRoles[index],
        isAlive = true
      )
    }

    _uiState.update {
      it.copy(
        mafiaGameState = MafiaGameState(
          assignments = assignments,
          currentPassingIndex = 0,
          phase = MafiaGamePhase.ROLE_DISTRIBUTION,
          roundNumber = 1,
          timerSecondsRemaining = 120,
          isTimerRunning = false
        )
      )
    }
  }

  fun advanceMafiaPassPhone() {
    val current = _uiState.value.mafiaGameState
    val nextIdx = current.currentPassingIndex + 1
    if (nextIdx < current.assignments.size) {
      _uiState.update {
        it.copy(mafiaGameState = current.copy(currentPassingIndex = nextIdx))
      }
    } else {
      // Finished passing phone, transition to Night Intro
      _uiState.update {
        it.copy(
          mafiaGameState = current.copy(
            phase = MafiaGamePhase.NIGHT_INTRO,
            currentPassingIndex = 0
          )
        )
      }
    }
  }

  fun proceedToNightMafia() {
    _uiState.update {
      it.copy(mafiaGameState = it.mafiaGameState.copy(phase = MafiaGamePhase.NIGHT_MAFIA))
    }
  }

  fun chooseMafiaTarget(victimPlayerId: String) {
    val current = _uiState.value.mafiaGameState
    val hasDoctor = current.assignments.any { it.isAlive && it.role == MafiaRoleType.DOCTOR }
    val nextPhase = if (hasDoctor) MafiaGamePhase.NIGHT_DOCTOR else {
      val hasDetective = current.assignments.any { it.isAlive && it.role == MafiaRoleType.DETECTIVE }
      if (hasDetective) MafiaGamePhase.NIGHT_DETECTIVE else MafiaGamePhase.NIGHT_RECAP
    }

    val updated = current.copy(
      nightTargetVictimId = victimPlayerId,
      phase = nextPhase
    )

    if (nextPhase == MafiaGamePhase.NIGHT_RECAP) {
      resolveNightOutcome(updated)
    } else {
      _uiState.update { it.copy(mafiaGameState = updated) }
    }
  }

  fun chooseDoctorProtected(protectedPlayerId: String) {
    val current = _uiState.value.mafiaGameState
    val hasDetective = current.assignments.any { it.isAlive && it.role == MafiaRoleType.DETECTIVE }
    val nextPhase = if (hasDetective) MafiaGamePhase.NIGHT_DETECTIVE else MafiaGamePhase.NIGHT_RECAP

    val updated = current.copy(
      nightProtectedPlayerId = protectedPlayerId,
      phase = nextPhase
    )

    if (nextPhase == MafiaGamePhase.NIGHT_RECAP) {
      resolveNightOutcome(updated)
    } else {
      _uiState.update { it.copy(mafiaGameState = updated) }
    }
  }

  fun chooseDetectiveInvestigation(investigatedPlayerId: String) {
    val current = _uiState.value.mafiaGameState
    val targetRole = current.assignments.firstOrNull { it.player.id == investigatedPlayerId }
    val isMafia = targetRole?.role == MafiaRoleType.MAFIA

    val updated = current.copy(
      nightInvestigatedPlayerId = investigatedPlayerId,
      lastInvestigationResultIsMafia = isMafia,
      phase = MafiaGamePhase.NIGHT_RECAP
    )
    resolveNightOutcome(updated)
  }

  fun confirmDetectiveInvestigation(investigatedPlayerId: String) {
    chooseDetectiveInvestigation(investigatedPlayerId)
  }

  private fun resolveNightOutcome(state: MafiaGameState) {
    val targetId = state.nightTargetVictimId
    val protectedId = state.nightProtectedPlayerId
    val wasSaved = targetId != null && targetId == protectedId
    val eliminatedId = if (!wasSaved) targetId else null

    val updatedAssignments = state.assignments.map { assignment ->
      if (assignment.player.id == eliminatedId) {
        assignment.copy(isAlive = false)
      } else {
        assignment
      }
    }

    val aliveMafia = updatedAssignments.count { it.isAlive && it.role == MafiaRoleType.MAFIA }
    val aliveInnocents = updatedAssignments.count { it.isAlive && it.role != MafiaRoleType.MAFIA }

    val (winner, isOver) = when {
      aliveMafia == 0 -> Pair("الحارة الشريفة 🛡️", true)
      aliveMafia >= aliveInnocents -> Pair("عصابة المافيا 🔪", true)
      else -> Pair(null, false)
    }

    _uiState.update {
      it.copy(
        mafiaGameState = state.copy(
          assignments = updatedAssignments,
          lastNightEliminatedPlayerId = eliminatedId,
          wasSavedByDoctor = wasSaved,
          phase = if (isOver) MafiaGamePhase.GAME_OVER else MafiaGamePhase.NIGHT_RECAP,
          winningTeamArabic = winner,
          isGameOver = isOver
        )
      )
    }
  }

  fun proceedToDayDiscussion() {
    mafiaTimerJob?.cancel()
    _uiState.update {
      it.copy(
        mafiaGameState = it.mafiaGameState.copy(
          phase = MafiaGamePhase.DAY_DISCUSSION,
          timerSecondsRemaining = 120,
          isTimerRunning = true,
          dayVotes = emptyMap()
        )
      )
    }
    resumeMafiaTimer()
  }

  fun proceedToDayVoting() {
    pauseMafiaTimer()
    _uiState.update {
      it.copy(
        mafiaGameState = it.mafiaGameState.copy(
          phase = MafiaGamePhase.DAY_VOTING,
          dayVotes = emptyMap()
        )
      )
    }
  }

  fun castMafiaDayVote(voterId: String, suspectId: String) {
    _uiState.update { state ->
      val votes = state.mafiaGameState.dayVotes.toMutableMap()
      votes[voterId] = suspectId
      state.copy(mafiaGameState = state.mafiaGameState.copy(dayVotes = votes))
    }
  }

  fun finalizeMafiaDayVoting(eliminatedPlayerId: String?) {
    val current = _uiState.value.mafiaGameState
    val updatedAssignments = current.assignments.map { assignment ->
      if (assignment.player.id == eliminatedPlayerId) {
        assignment.copy(isAlive = false)
      } else {
        assignment
      }
    }

    val aliveMafia = updatedAssignments.count { it.isAlive && it.role == MafiaRoleType.MAFIA }
    val aliveInnocents = updatedAssignments.count { it.isAlive && it.role != MafiaRoleType.MAFIA }

    val (winner, isOver) = when {
      aliveMafia == 0 -> Pair("الحارة الشريفة 🛡️", true)
      aliveMafia >= aliveInnocents -> Pair("عصابة المافيا 🔪", true)
      else -> Pair(null, false)
    }

    _uiState.update {
      it.copy(
        mafiaGameState = current.copy(
          assignments = updatedAssignments,
          dayEliminatedPlayerId = eliminatedPlayerId,
          phase = if (isOver) MafiaGamePhase.GAME_OVER else MafiaGamePhase.DAY_ELIMINATION,
          winningTeamArabic = winner,
          isGameOver = isOver
        )
      )
    }
  }

  fun advanceToNextMafiaRound() {
    val current = _uiState.value.mafiaGameState
    _uiState.update {
      it.copy(
        mafiaGameState = current.copy(
          roundNumber = current.roundNumber + 1,
          phase = MafiaGamePhase.NIGHT_INTRO,
          nightTargetVictimId = null,
          nightProtectedPlayerId = null,
          nightInvestigatedPlayerId = null,
          lastInvestigationResultIsMafia = null,
          lastNightEliminatedPlayerId = null,
          wasSavedByDoctor = false,
          dayVotes = emptyMap(),
          dayEliminatedPlayerId = null
        )
      )
    }
  }

  fun resetMafiaGame() {
    startMafiaGame()
  }

  fun deleteCharadesWord(wordId: String, wordText: String) {
    viewModelScope.launch {
      if (wordId.startsWith("custom_charades_")) {
        repository.deleteCustomCharadesWord(wordId)
      }
      repository.markItemDeleted(wordId, "CHARADES_WORD")
      repository.markItemDeleted(wordText, "CHARADES_WORD")
    }
  }


  companion object {
    fun colorFromStoredLong(storedLong: Long): Color {
      return try {
        val candidate = if ((storedLong ushr 32) != 0L) {
          (storedLong ushr 32) and 0xFFFFFFFFL
        } else {
          storedLong and 0xFFFFFFFFL
        }
        if (candidate == 0L) {
          Color(0xFFD0BCFF)
        } else {
          Color(candidate)
        }
      } catch (e: Exception) {
        Color(0xFFD0BCFF)
      }
    }

    fun colorToStoredLong(color: Color): Long {
      val a = (color.alpha * 255.0f + 0.5f).toInt() and 0xFF
      val r = (color.red * 255.0f + 0.5f).toInt() and 0xFF
      val g = (color.green * 255.0f + 0.5f).toInt() and 0xFF
      val b = (color.blue * 255.0f + 0.5f).toInt() and 0xFF
      return (((a.toLong() and 0xFF) shl 24) or
              ((r.toLong() and 0xFF) shl 16) or
              ((g.toLong() and 0xFF) shl 8) or
              (b.toLong() and 0xFF)) and 0xFFFFFFFFL
    }
  }
}

private data class MapEntry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>
