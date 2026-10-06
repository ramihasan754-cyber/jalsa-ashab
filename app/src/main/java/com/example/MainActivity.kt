package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenState
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CharadesGameScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MafiaGameScreen
import com.example.ui.screens.OutOfTheLoopScreen
import com.example.ui.screens.SessionStatsScreen
import com.example.ui.screens.SetupScreen
import com.example.ui.screens.SpyGameScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.PartyGameViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: PartyGameViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val uiState by viewModel.uiState.collectAsState()
      val context = LocalContext.current
      var showExitConfirmDialog by remember { mutableStateOf(false) }

      MyApplicationTheme(darkTheme = uiState.isDarkMode) {
        // Enforce Arabic RTL layout direction for natural party game UX
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          // 1. Back button handling when inside any sub-menu, game category, active round, or admin dashboard:
          // Smoothly navigate back to main game lobby (Home) instead of exiting or closing the app
          BackHandler(enabled = uiState.currentScreen != ScreenState.HOME && uiState.currentScreen != ScreenState.SETUP) {
            viewModel.navigateToHome()
          }

          // 2. Back button handling when on Home screen or Setup screen:
          // Prevent instant closure and trigger a confirmation dialog asking to confirm before exit
          BackHandler(enabled = uiState.currentScreen == ScreenState.HOME || uiState.currentScreen == ScreenState.SETUP) {
            showExitConfirmDialog = true
          }

          if (showExitConfirmDialog) {
            AlertDialog(
              onDismissRequest = { showExitConfirmDialog = false },
              shape = RoundedCornerShape(24.dp),
              icon = {
                Text(text = "🚪", fontSize = 36.sp)
              },
              title = {
                Text(
                  text = "متأكد بدك تطلع من اللعبة؟",
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.Bold,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth()
                )
              },
              text = {
                Text(
                  text = "جلسة أصحاب وسهرات الضحك ما بتكمل بدونك يا نشمي! متأكد إنك حاب تنهي الجلسة وتطلع من التطبيق؟",
                  style = MaterialTheme.typography.bodyMedium,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth()
                )
              },
              confirmButton = {
                Button(
                  onClick = {
                    showExitConfirmDialog = false
                    (context as? Activity)?.finish()
                  },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                  ),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.testTag("confirm_exit_app_button")
                ) {
                  Text("خروج من اللعبة", fontWeight = FontWeight.Bold)
                }
              },
              dismissButton = {
                OutlinedButton(
                  onClick = { showExitConfirmDialog = false },
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.testTag("cancel_exit_app_button")
                ) {
                  Text("خليني بالقعدة", fontWeight = FontWeight.SemiBold)
                }
              },
              modifier = Modifier.testTag("exit_confirmation_dialog")
            )
          }

          Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
          ) { _ ->
            AnimatedContent(
              targetState = uiState.currentScreen,
              transitionSpec = { fadeIn() togetherWith fadeOut() },
              label = "ScreenTransition"
            ) { screen ->
              when (screen) {
                ScreenState.SETUP -> {
                  SetupScreen(
                    players = uiState.players,
                    isDarkMode = uiState.isDarkMode,
                    maxRounds = uiState.maxRounds,
                    onSelectMaxRounds = { viewModel.setMaxRounds(it) },
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onAddPlayer = { name -> viewModel.addPlayer(name) },
                    onAddPlayerAt = { name, idx -> viewModel.addPlayerAt(name, idx) },
                    onMovePlayerUp = { id -> viewModel.movePlayerUp(id) },
                    onMovePlayerDown = { id -> viewModel.movePlayerDown(id) },
                    onRemovePlayer = { id -> viewModel.removePlayer(id) },
                    onStartGame = { viewModel.startGameFromSetup() },
                    onNavigateAdmin = { viewModel.navigateToAdminDashboard() }
                  )
                }

                ScreenState.HOME -> {
                  HomeScreen(
                    players = uiState.players,
                    isDarkMode = uiState.isDarkMode,
                    customCategories = uiState.customCategories,
                    allCustomCards = uiState.allCustomCards,
                    customQuestionsCountMap = uiState.customQuestionsCountMap,
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onSelectCategory = { category -> viewModel.selectCategory(category) },
                    onAddPlayer = { name -> viewModel.addPlayer(name) },
                    onAddPlayerAt = { name, idx -> viewModel.addPlayerAt(name, idx) },
                    onMovePlayerUp = { id -> viewModel.movePlayerUp(id) },
                    onMovePlayerDown = { id -> viewModel.movePlayerDown(id) },
                    onRemovePlayer = { id -> viewModel.removePlayer(id) },
                    onCreateCustomCategory = { title, sub, emoji, color, questions ->
                      viewModel.createCustomCategory(title, sub, emoji, color, questions) { newCategory ->
                        viewModel.selectCategory(newCategory)
                      }
                    },
                    onAddQuestionToCategory = { catId, qText ->
                      viewModel.addQuestionToCategory(catId, qText)
                    },
                    onDeleteQuestion = { qId ->
                      viewModel.deleteQuestion(qId)
                    },
                    onDeleteCustomCategory = { catId ->
                      viewModel.deleteCustomCategory(catId)
                    },
                    onNavigateSetup = { viewModel.navigateToSetup() },
                    onNavigateAdmin = { viewModel.navigateToAdminDashboard() },
                    onNavigateSessionStats = { viewModel.navigateToSessionStats() },
                    activePenalty = uiState.activePenalty,
                    penaltyTargetPlayer = uiState.penaltyTargetPlayer,
                    onTriggerPenalty = { viewModel.triggerPenalty() },
                    onDismissPenalty = { viewModel.dismissPenalty() },
                    onApplyPenalty = { viewModel.applyPenalty() },
                    onRerollPenalty = { viewModel.rerollPenalty() }
                  )
                }

                ScreenState.GAME -> {
                  GameScreen(
                    uiState = uiState,
                    onNavigateHome = { viewModel.navigateToHome() },
                    onSelectCategory = { category -> viewModel.selectCategory(category) },
                    onFlipCard = { viewModel.flipCard() },
                    onNextCard = { viewModel.nextCard() },
                    onPreviousCard = { viewModel.previousCard() },
                    onShuffleDeck = { viewModel.shuffleDeck() },
                    onTriggerPenalty = { viewModel.triggerPenalty() },
                    onDismissPenalty = { apply -> viewModel.dismissPenalty(apply) },
                    onSpinRoulette = { viewModel.spinPlayerRoulette() },
                    onDismissRoulette = { viewModel.dismissRoulette() },
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onAddPlayer = { name -> viewModel.addPlayer(name) },
                    onRemovePlayer = { id -> viewModel.removePlayer(id) },
                    onAddQuestionToCategory = { catId, qText ->
                      viewModel.addQuestionToCategory(catId, qText)
                    },
                    onVoteForPlayer = { playerId -> viewModel.voteForPlayer(playerId) },
                    onResetVotes = { viewModel.resetCardVotes() },
                    onRerollTargetPlayer = { viewModel.rerollRandomTargetPlayer() },
                    onNavigateSessionStats = { viewModel.navigateToSessionStats() }
                  )
                }

                ScreenState.OUT_OF_THE_LOOP -> {
                  OutOfTheLoopScreen(
                    gameState = uiState.loopGameState,
                    players = uiState.players,
                    currentRound = uiState.currentRound,
                    maxRounds = uiState.maxRounds,
                    isGameFinished = uiState.isGameFinished,
                    activePenalty = uiState.activePenalty,
                    penaltyTargetPlayer = uiState.penaltyTargetPlayer,
                    onAdvancePassPhone = { viewModel.advanceLoopPassPhone() },
                    onToggleTimer = { viewModel.toggleLoopTimer() },
                    onAdvanceTurnOrder = { viewModel.advanceLoopTurnOrder() },
                    onSwitchToFreeAsking = { viewModel.switchLoopToFreeAsking() },
                    onProceedToVoting = { viewModel.proceedToLoopVoting() },
                    onCastVote = { voterId, suspectId -> viewModel.castLoopVote(voterId, suspectId) },
                    onFinalizeVoting = { viewModel.finalizeLoopVoting() },
                    onSelectSuspect = { suspectId -> viewModel.selectLoopSuspect(suspectId) },
                    onGuessWord = { guess -> viewModel.guessLoopWord(guess) },
                    onNextRound = { viewModel.advanceToNextRound() },
                    onRestartGame = { viewModel.startOutOfTheLoopGame() },
                    onNavigateHome = { viewModel.navigateToHome() },
                    onTriggerPenalty = { viewModel.triggerPenalty() },
                    onDismissPenalty = { viewModel.dismissPenalty() },
                    onApplyPenalty = { viewModel.applyPenalty() },
                    onRerollPenalty = { viewModel.rerollPenalty() }
                  )
                }

                ScreenState.THE_SPY -> {
                  SpyGameScreen(
                    gameState = uiState.spyGameState,
                    players = uiState.players,
                    currentRound = uiState.currentRound,
                    maxRounds = uiState.maxRounds,
                    isGameFinished = uiState.isGameFinished,
                    activePenalty = uiState.activePenalty,
                    penaltyTargetPlayer = uiState.penaltyTargetPlayer,
                    onAdvancePassPhone = { viewModel.advanceSpyPassPhone() },
                    onToggleTimer = { viewModel.toggleSpyTimer() },
                    onNextQuestionTurn = { viewModel.nextSpyTurn() },
                    onProceedToAccusation = { viewModel.proceedToSpyAccusation() },
                    onCastVote = { voterId, suspectId -> viewModel.castSpyVote(voterId, suspectId) },
                    onFinalizeVoting = { viewModel.finalizeSpyVoting() },
                    onAccuseSpy = { spyId -> viewModel.accuseSpy(spyId) },
                    onSpyGuessLocation = { locId -> viewModel.spyGuessLocation(locId) },
                    onNextRound = { viewModel.advanceToNextRound() },
                    onRestartGame = { viewModel.startSpyGame() },
                    onNavigateHome = { viewModel.navigateToHome() },
                    onTriggerPenalty = { viewModel.triggerPenalty() },
                    onDismissPenalty = { viewModel.dismissPenalty() },
                    onApplyPenalty = { viewModel.applyPenalty() },
                    onRerollPenalty = { viewModel.rerollPenalty() }
                  )
                }

                ScreenState.CHARADES -> {
                  CharadesGameScreen(
                    charadesState = uiState.charadesGameState,
                    allPlayers = uiState.players,
                    isDarkMode = uiState.isDarkMode,
                    activePenalty = uiState.activePenalty,
                    penaltyTargetPlayer = uiState.penaltyTargetPlayer,
                    onSelectSubCategory = { subCat -> viewModel.selectCharadesSubCategory(subCat) },
                    onChangeSubCategory = { viewModel.openCharadesSubCategorySelection() },
                    onStartRound = { viewModel.startCharadesRound() },
                    onPauseTimer = { viewModel.pauseCharadesTimer() },
                    onResumeTimer = { viewModel.resumeCharadesTimer() },
                    onCorrectGuess = { viewModel.onCharadesCorrectGuess() },
                    onSkipWord = { viewModel.onCharadesSkipWord() },
                    onNextTurn = { viewModel.nextCharadesTurn() },
                    onSwitchPlayerTeam = { playerId -> viewModel.switchPlayerTeam(playerId) },
                    onShuffleTeams = { viewModel.shuffleTeams() },
                    onUpdateTeamNames = { nameA, nameB -> viewModel.updateCharadesTeamNames(nameA, nameB) },
                    onSetPlayMode = { mode -> viewModel.setCharadesPlayMode(mode) },
                    onResetGame = { viewModel.startCharadesGame() },
                    onClearHistory = { viewModel.clearCharadesHistory() },
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onNavigateHome = { viewModel.navigateToHome() },
                    onTriggerPenalty = { viewModel.triggerPenalty() },
                    onDismissPenalty = { viewModel.dismissPenalty() },
                    onApplyPenalty = { viewModel.applyPenalty() },
                    onRerollPenalty = { viewModel.rerollPenalty() }
                  )
                }

                ScreenState.ADMIN_DASHBOARD -> {
                  AdminDashboardScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    onNavigateBack = { viewModel.navigateToHome() }
                  )
                }

                ScreenState.MAFIA -> {
                  MafiaGameScreen(
                    gameState = uiState.mafiaGameState,
                    players = uiState.players,
                    activePenalty = uiState.activePenalty,
                    penaltyTargetPlayer = uiState.penaltyTargetPlayer,
                    onAddPlayer = { name -> viewModel.addPlayer(name) },
                    onRemovePlayer = { id -> viewModel.removePlayer(id) },
                    onAdvancePassPhone = { viewModel.advanceMafiaPassPhone() },
                    onProceedToNightMafia = { viewModel.proceedToNightMafia() },
                    onChooseMafiaTarget = { victimId -> viewModel.chooseMafiaTarget(victimId) },
                    onChooseDoctorProtected = { protectedId -> viewModel.chooseDoctorProtected(protectedId) },
                    onChooseDetectiveInvestigation = { targetId -> viewModel.chooseDetectiveInvestigation(targetId) },
                    onProceedToDayDiscussion = { viewModel.proceedToDayDiscussion() },
                    onToggleTimer = { viewModel.toggleMafiaTimer() },
                    onProceedToDayVoting = { viewModel.proceedToDayVoting() },
                    onCastDayVote = { voterId, suspectId -> viewModel.castMafiaDayVote(voterId, suspectId) },
                    onFinalizeDayVoting = { eliminatedId -> viewModel.finalizeMafiaDayVoting(eliminatedId) },
                    onAdvanceToNextRound = { viewModel.advanceMafiaToNextRound() },
                    onRestartGame = { viewModel.startMafiaGame() },
                    onNavigateHome = { viewModel.navigateToHome() },
                    onTriggerPenalty = { viewModel.triggerPenalty() },
                    onDismissPenalty = { viewModel.dismissPenalty() },
                    onApplyPenalty = { viewModel.applyPenalty() },
                    onRerollPenalty = { viewModel.rerollPenalty() }
                  )
                }

                ScreenState.SESSION_STATS -> {
                  SessionStatsScreen(
                    playersWithTitles = viewModel.getPlayersWithTitles(),
                    totalSessionRounds = viewModel.getTotalSessionRounds(),
                    isDarkMode = uiState.isDarkMode,
                    onResetStats = { viewModel.resetSessionStats() },
                    onNavigateBack = { viewModel.navigateToHome() },
                    onTriggerPenalty = { viewModel.triggerPenalty() }
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

/**
 * Kept for preview and unit test compatibility.
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "أهلاً بك في جلسة أصحاب $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}
