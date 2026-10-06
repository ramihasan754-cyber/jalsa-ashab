package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.example.model.GameCategory
import com.example.model.LoopPhase
import com.example.model.ScreenState
import com.example.model.SpyPhase
import com.example.viewmodel.PartyGameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("جلسة أصحاب", appName)
  }

  @Test
  fun `stored color long properly decodes and supports copy with alpha without crash`() {
    val rawColorLong = 0xFFD0BCFF.toLong()
    val restoredColor = PartyGameViewModel.colorFromStoredLong(rawColorLong)

    val copiedColor = restoredColor.copy(alpha = 0.16f)
    assertNotNull(copiedColor)

    val encoded = PartyGameViewModel.colorToStoredLong(restoredColor)
    val roundTripColor = PartyGameViewModel.colorFromStoredLong(encoded)
    val roundTripCopied = roundTripColor.copy(alpha = 0.5f)
    assertNotNull(roundTripCopied)
  }

  @Test
  fun `setup screen starts game with players`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = PartyGameViewModel(app)

    assertEquals(ScreenState.SETUP, viewModel.uiState.value.currentScreen)
    assertTrue(viewModel.uiState.value.players.isNotEmpty())

    viewModel.addPlayer("زيد")
    assertTrue(viewModel.uiState.value.players.any { it.name == "زيد" })

    viewModel.startGameFromSetup()
    assertEquals(ScreenState.HOME, viewModel.uiState.value.currentScreen)
  }

  @Test
  fun `voting mechanic aggregates votes and resets correctly`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = PartyGameViewModel(app)

    val player1 = viewModel.uiState.value.players[0]
    val player2 = viewModel.uiState.value.players[1]

    viewModel.voteForPlayer(player1.id)
    viewModel.voteForPlayer(player1.id)
    viewModel.voteForPlayer(player2.id)

    assertEquals(2, viewModel.uiState.value.cardVotes[player1.id])
    assertEquals(1, viewModel.uiState.value.cardVotes[player2.id])

    viewModel.resetCardVotes()
    assertTrue(viewModel.uiState.value.cardVotes.isEmpty())
  }

  @Test
  fun `out of the loop game assigns exactly one out player and transitions phases`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = PartyGameViewModel(app)

    viewModel.selectCategory(GameCategory.OUT_OF_THE_LOOP)
    assertEquals(ScreenState.OUT_OF_THE_LOOP, viewModel.uiState.value.currentScreen)

    val loopState = viewModel.uiState.value.loopGameState
    assertEquals(LoopPhase.PASS_PHONE, loopState.phase)
    assertNotNull(loopState.secretWord)
    assertTrue(loopState.secretWord.isNotBlank())

    val outAssignments = loopState.assignments.filter { it.isOutOfTheLoop }
    assertEquals(1, outAssignments.size)

    val outPlayerId = outAssignments.first().player.id
    assertEquals(outPlayerId, loopState.outPlayerId)

    // Advance through all players in pass phone phase
    repeat(loopState.assignments.size) {
      viewModel.advanceLoopPassPhone()
    }

    assertEquals(LoopPhase.DISCUSSION, viewModel.uiState.value.loopGameState.phase)

    viewModel.proceedToLoopVoting()
    assertEquals(LoopPhase.VOTING, viewModel.uiState.value.loopGameState.phase)

    viewModel.selectLoopSuspect(outPlayerId)
    assertEquals(LoopPhase.REVEAL, viewModel.uiState.value.loopGameState.phase)
    assertEquals(outPlayerId, viewModel.uiState.value.loopGameState.suspectedPlayerId)

    // Word guess
    viewModel.guessLoopWord(viewModel.uiState.value.loopGameState.secretWord)
    assertEquals(true, viewModel.uiState.value.loopGameState.isWordGuessedCorrectly)
  }

  @Test
  fun `spy game assigns roles, creates questions, and handles accusation`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = PartyGameViewModel(app)

    viewModel.selectCategory(GameCategory.THE_SPY)
    assertEquals(ScreenState.THE_SPY, viewModel.uiState.value.currentScreen)

    val spyState = viewModel.uiState.value.spyGameState
    assertEquals(SpyPhase.PASS_PHONE, spyState.phase)
    assertNotNull(spyState.location)

    val spyAssignments = spyState.assignments.filter { it.isSpy }
    assertEquals(1, spyAssignments.size)

    repeat(spyState.assignments.size) {
      viewModel.advanceSpyPassPhone()
    }

    assertEquals(SpyPhase.QUESTIONING, viewModel.uiState.value.spyGameState.phase)

    viewModel.proceedToSpyAccusation()
    assertEquals(SpyPhase.ACCUSATION, viewModel.uiState.value.spyGameState.phase)

    val spyId = spyAssignments.first().player.id
    viewModel.accuseSpy(spyId)
    assertEquals(SpyPhase.REVEAL, viewModel.uiState.value.spyGameState.phase)
    assertEquals(spyId, viewModel.uiState.value.spyGameState.accusedPlayerId)
  }

  @Test
  fun `who is most likely category contains exactly 500 unique questions with zero duplicates`() {
    val questions = com.example.data.PartyQuestionsData.whoIsMostLikely
    assertEquals(500, questions.size)

    val uniqueIds = questions.map { it.id }.toSet()
    assertEquals("Should contain 500 strictly unique IDs", 500, uniqueIds.size)
  }

  @Test
  fun `out of the loop topics contain 100 unique words with zero duplicates`() {
    val allWords = com.example.data.PartyQuestionsData.all100LoopWords
    assertEquals(100, allWords.size)

    val uniqueWords = allWords.map { it.trim() }.toSet()
    assertEquals("Should contain 100 strictly unique Jordanian words", 100, uniqueWords.size)
  }

  @Test
  fun `admin dashboard navigation functions properly`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = PartyGameViewModel(app)

    viewModel.navigateToAdminDashboard()
    assertEquals(ScreenState.ADMIN_DASHBOARD, viewModel.uiState.value.currentScreen)

    viewModel.navigateToHome()
    assertEquals(ScreenState.HOME, viewModel.uiState.value.currentScreen)
  }

  @Test
  fun `charades category contains exactly 150 unique items with zero duplicates`() {
    val charadesWords = com.example.data.PartyQuestionsData.charadesWordsList
    assertEquals(150, charadesWords.size)

    val uniqueWords = charadesWords.map { it.trim() }.toSet()
    assertEquals("Should contain 150 strictly unique charades items", 150, uniqueWords.size)
  }

  @Test
  fun `sub-menu, active game rounds, and setup navigate smoothly back to main home lobby`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = PartyGameViewModel(app)

    // Setup screen back navigation
    viewModel.navigateToSetup()
    assertEquals(ScreenState.SETUP, viewModel.uiState.value.currentScreen)
    viewModel.navigateToHome()
    assertEquals(ScreenState.HOME, viewModel.uiState.value.currentScreen)

    // Out of the loop back navigation
    viewModel.selectCategory(com.example.model.GameCategory.OUT_OF_THE_LOOP)
    assertEquals(ScreenState.OUT_OF_THE_LOOP, viewModel.uiState.value.currentScreen)
    viewModel.navigateToHome()
    assertEquals(ScreenState.HOME, viewModel.uiState.value.currentScreen)

    // The Spy back navigation
    viewModel.selectCategory(com.example.model.GameCategory.THE_SPY)
    assertEquals(ScreenState.THE_SPY, viewModel.uiState.value.currentScreen)
    viewModel.navigateToHome()
    assertEquals(ScreenState.HOME, viewModel.uiState.value.currentScreen)

    // Charades back navigation
    viewModel.selectCategory(com.example.model.GameCategory.CHARADES)
    assertEquals(ScreenState.CHARADES, viewModel.uiState.value.currentScreen)
    viewModel.navigateToHome()
    assertEquals(ScreenState.HOME, viewModel.uiState.value.currentScreen)

    // Standard card game back navigation
    viewModel.selectCategory(com.example.model.GameCategory.WHO_IS_MOST_LIKELY)
    assertEquals(ScreenState.GAME, viewModel.uiState.value.currentScreen)
    viewModel.navigateToHome()
    assertEquals(ScreenState.HOME, viewModel.uiState.value.currentScreen)
  }

  @Test
  fun `individual player voting in out of the loop awards points correctly`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = PartyGameViewModel(app)

    viewModel.selectCategory(GameCategory.OUT_OF_THE_LOOP)
    val loopState = viewModel.uiState.value.loopGameState
    val outPlayerId = loopState.outPlayerId!!
    val players = viewModel.uiState.value.players

    repeat(loopState.assignments.size) { viewModel.advanceLoopPassPhone() }
    viewModel.proceedToLoopVoting()

    val regularPlayers = players.filter { it.id != outPlayerId }
    val correctVoter = regularPlayers[0]
    val wrongVoter = regularPlayers[1]

    viewModel.castLoopVote(correctVoter.id, outPlayerId)
    viewModel.castLoopVote(wrongVoter.id, correctVoter.id)

    viewModel.finalizeLoopVoting()
    assertEquals(LoopPhase.REVEAL, viewModel.uiState.value.loopGameState.phase)

    val awardedPoints = viewModel.uiState.value.loopGameState.awardedPoints
    assertEquals(1, awardedPoints[correctVoter.id])
    assertEquals(0, awardedPoints[wrongVoter.id] ?: 0)
  }

  @Test
  fun `individual player voting in spy game awards points correctly`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = PartyGameViewModel(app)

    viewModel.selectCategory(GameCategory.THE_SPY)
    val spyState = viewModel.uiState.value.spyGameState
    val spyId = spyState.spyPlayerId!!
    val players = viewModel.uiState.value.players

    repeat(spyState.assignments.size) { viewModel.advanceSpyPassPhone() }
    viewModel.proceedToSpyAccusation()

    val regularPlayers = players.filter { it.id != spyId }
    val correctVoter = regularPlayers[0]
    val wrongVoter = regularPlayers[1]

    viewModel.castSpyVote(correctVoter.id, spyId)
    viewModel.castSpyVote(wrongVoter.id, correctVoter.id)

    viewModel.finalizeSpyVoting()
    assertEquals(SpyPhase.REVEAL, viewModel.uiState.value.spyGameState.phase)

    val awardedPoints = viewModel.uiState.value.spyGameState.awardedPoints
    assertEquals(1, awardedPoints[correctVoter.id])
    assertEquals(0, awardedPoints[wrongVoter.id] ?: 0)
  }
}
