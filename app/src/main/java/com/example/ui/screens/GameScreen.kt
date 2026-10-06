package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameCategory
import com.example.model.PenaltyItem
import com.example.model.Player
import com.example.ui.components.PartyCard
import com.example.ui.components.PenaltyDialog
import com.example.ui.components.PlayerManagerModal
import com.example.ui.components.QuickAddQuestionDialog
import com.example.ui.components.RouletteDialog
import com.example.ui.theme.CoralPink
import com.example.ui.theme.CrimsonRed
import com.example.viewmodel.GameUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GameScreen(
  uiState: GameUiState,
  onNavigateHome: () -> Unit,
  onSelectCategory: (GameCategory) -> Unit,
  onFlipCard: () -> Unit,
  onNextCard: () -> Unit,
  onPreviousCard: () -> Unit,
  onShuffleDeck: () -> Unit,
  onTriggerPenalty: () -> Unit,
  onDismissPenalty: (applyToPlayer: Boolean) -> Unit,
  onSpinRoulette: () -> Unit,
  onDismissRoulette: () -> Unit,
  onToggleDarkMode: () -> Unit,
  onAddPlayer: (String) -> Unit,
  onRemovePlayer: (String) -> Unit,
  onAddQuestionToCategory: (categoryId: String, question: String) -> Unit = { _, _ -> },
  onVoteForPlayer: (String) -> Unit = {},
  onResetVotes: () -> Unit = {},
  onRerollTargetPlayer: () -> Unit = {},
  onNavigateSessionStats: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showPlayerModal by remember { mutableStateOf(false) }
  var showQuickAddQuestionDialog by remember { mutableStateOf(false) }

  val categories = uiState.allCategories

  if (showQuickAddQuestionDialog && uiState.selectedCategory.isCustom) {
    QuickAddQuestionDialog(
      category = uiState.selectedCategory,
      onAddQuestion = { qText ->
        onAddQuestionToCategory(uiState.selectedCategory.id, qText)
      },
      onDismiss = { showQuickAddQuestionDialog = false }
    )
  }

  // Penalty Dialog Popup
  if (uiState.activePenalty != null) {
    PenaltyDialog(
      penalty = uiState.activePenalty,
      targetPlayer = uiState.penaltyTargetPlayer,
      onDismiss = { onDismissPenalty(false) },
      onApplyPenalty = { onDismissPenalty(true) },
      onRerollPenalty = onTriggerPenalty
    )
  }

  // Roulette Picker Dialog Popup
  if (uiState.pickedRoulettePlayer != null) {
    RouletteDialog(
      pickedPlayer = uiState.pickedRoulettePlayer,
      players = uiState.players,
      onDismiss = onDismissRoulette,
      onSpinAgain = onSpinRoulette
    )
  }

  // Player Manager Modal
  if (showPlayerModal) {
    PlayerManagerModal(
      players = uiState.players,
      onAddPlayer = onAddPlayer,
      onRemovePlayer = onRemovePlayer,
      onDismiss = { showPlayerModal = false }
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .windowInsetsPadding(WindowInsets.statusBars)
  ) {
    // Top Bar matching Sleek Interface header (Compact & Clean)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          modifier = Modifier
            .size(36.dp)
            .clickable { onNavigateHome() }
            .testTag("back_to_home_button"),
          shape = CircleShape,
          color = Color(0xFF4A4458)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "العودة للرئيسية",
              tint = Color(0xFFD0BCFF),
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Text(
          text = "جلسة أصحاب",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFE6E1E5)
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(50),
          color = Color(0xFF2D2A33),
          border = BorderStroke(1.dp, Color(0xFF4A4458))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .background(Color(0xFFD0BCFF), CircleShape)
            )
            Text(
              text = uiState.selectedCategory.titleArabic,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFD0BCFF),
              fontSize = 11.sp
            )
          }
        }

        IconButton(
          onClick = onNavigateSessionStats,
          modifier = Modifier
            .size(32.dp)
            .testTag("game_session_stats_button")
        ) {
          Text(text = "🏆", fontSize = 16.sp)
        }

        IconButton(
          onClick = onToggleDarkMode,
          modifier = Modifier
            .size(32.dp)
            .testTag("game_dark_mode_toggle")
        ) {
          Icon(
            imageVector = if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = "الوضع الليلي",
            tint = Color(0xFFCAC4D0),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // Turn & Progress Info Header Strip (Ultra-Compact)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (uiState.players.isNotEmpty()) {
        val activePlayerIndex = uiState.currentCardIndex % uiState.players.size
        val activePlayer = uiState.players[activePlayerIndex]
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = uiState.selectedCategory.primaryColor.copy(alpha = 0.16f),
          border = BorderStroke(1.dp, uiState.selectedCategory.primaryColor.copy(alpha = 0.5f)),
          modifier = Modifier
            .clickable { onSpinRoulette() }
            .testTag("game_active_turn_player_banner")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "👑", fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "الدور: ${activePlayer.name}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFE6E1E5),
              fontSize = 11.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "🎲", fontSize = 11.sp)
          }
        }
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2D2A33),
        border = BorderStroke(1.dp, Color(0xFF4A4458).copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "الكرت ${uiState.currentCardIndex + 1} / ${uiState.currentDeck.size}",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFCAC4D0),
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          )
        }
      }
    }

    // Category Selector Strip (Quick Switching, Slim & Clean)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      categories.forEach { category ->
        val isSelected = category == uiState.selectedCategory
        Surface(
          shape = RoundedCornerShape(50),
          color = if (isSelected) Color(0xFFD0BCFF) else Color(0xFF2D2A33),
          border = BorderStroke(1.dp, if (isSelected) Color(0xFFD0BCFF) else Color(0xFF4A4458)),
          modifier = Modifier
            .clickable { onSelectCategory(category) }
            .testTag("category_tab_${category.id}")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = category.iconEmoji, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = category.titleArabic,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color(0xFF381E72) else Color(0xFFCAC4D0),
              fontSize = 10.sp
            )
          }
        }
      }

      if (uiState.selectedCategory.isCustom) {
        Surface(
          shape = RoundedCornerShape(50),
          color = uiState.selectedCategory.primaryColor.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, uiState.selectedCategory.primaryColor),
          modifier = Modifier
            .clickable { showQuickAddQuestionDialog = true }
            .testTag("game_quick_add_question_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              tint = uiState.selectedCategory.primaryColor,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "+ كرت",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = uiState.selectedCategory.primaryColor,
              fontSize = 10.sp
            )
          }
        }
      }
    }

    // Main Card Interactive Area (Naturally Fills Space without vertical scrolling)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = 14.dp, vertical = 2.dp),
      contentAlignment = Alignment.Center
    ) {
      if (uiState.currentCard != null) {
        PartyCard(
          cardItem = uiState.currentCard!!,
          isFlipped = uiState.isCardFlipped,
          currentIndex = uiState.currentCardIndex,
          totalCards = uiState.currentDeck.size,
          players = uiState.players,
          onFlipCard = {
            if (!uiState.isCardFlipped) {
              onFlipCard()
            }
          },
          onSwipeNext = onNextCard,
          modifier = Modifier.fillMaxSize()
        )
      } else {
        // Fallback / Empty Deck State
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          shape = RoundedCornerShape(24.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "📭", fontSize = 36.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "انتهت كروت هذه الفئة!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(12.dp))
              Button(onClick = onShuffleDeck) {
                Text("إعادة خربطة الكروت")
              }
            }
          }
        }
      }
    }

    // Category 1 ("مين أكثر واحد") - Compact Player Voting Strip
    if (uiState.selectedCategory.id == GameCategory.WHO_IS_MOST_LIKELY.id && uiState.players.isNotEmpty()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "صوتوا: 🗳️",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFD0BCFF),
          fontSize = 11.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          items(uiState.players) { player ->
            val votes = uiState.cardVotes[player.id] ?: 0
            val isVoted = votes > 0
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isVoted) Color(0xFFD0BCFF) else Color(0xFF2D2A33),
              border = BorderStroke(1.dp, if (isVoted) Color(0xFFD0BCFF) else Color(0xFF4A4458)),
              modifier = Modifier
                .clickable { onVoteForPlayer(player.id) }
                .testTag("vote_player_${player.id}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = player.name,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isVoted) FontWeight.Bold else FontWeight.Normal,
                  color = if (isVoted) Color(0xFF381E72) else Color(0xFFE6E1E5),
                  fontSize = 11.sp
                )
                if (votes > 0) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "($votes)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF381E72),
                    fontSize = 10.sp
                  )
                }
              }
            }
          }
        }
        if (uiState.cardVotes.isNotEmpty()) {
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "تصفير",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFFFB4AB),
            fontSize = 10.sp,
            modifier = Modifier.clickable { onResetVotes() }
          )
        }
      }
    }

    // Bottom Action Controls matching Sleek Interface footer (Completely Visible & Accessible in One Touch)
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.navigationBars),
      color = Color(0xFF1C1B1F),
      border = BorderStroke(1.dp, Color(0xFF4A4458).copy(alpha = 0.5f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        // Main Primary Action Buttons: Penalize, Flip & Next
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Penalize Button ("عقاب")
          Button(
            onClick = onTriggerPenalty,
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("penalize_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFFFB4AB),
              contentColor = Color(0xFF690005)
            ),
            contentPadding = PaddingValues(horizontal = 8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Gavel,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "عقاب",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          }

          // Flip Button ("اقلب الكرت")
          Button(
            onClick = onFlipCard,
            modifier = Modifier
              .weight(1.1f)
              .height(48.dp)
              .testTag("flip_card_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF4A4458),
              contentColor = Color(0xFFE6E1E5)
            ),
            border = BorderStroke(1.dp, Color(0xFFD0BCFF).copy(alpha = 0.4f)),
            contentPadding = PaddingValues(horizontal = 8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Cached,
              contentDescription = null,
              tint = Color(0xFFD0BCFF),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (uiState.isCardFlipped) "الغطاء" else "اقلب الكرت",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          // Next Card Button ("التالي")
          Button(
            onClick = onNextCard,
            modifier = Modifier
              .weight(1.4f)
              .height(48.dp)
              .testTag("next_card_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFD0BCFF),
              contentColor = Color(0xFF381E72)
            ),
            contentPadding = PaddingValues(horizontal = 8.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "التالي",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 16.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Secondary Utility Controls (Previous, Shuffle, Players, Titles)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF2D2A33),
            border = BorderStroke(1.dp, Color(0xFF4A4458)),
            modifier = Modifier
              .clickable { onPreviousCard() }
              .testTag("previous_card_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFCAC4D0),
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "السابق",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFCAC4D0),
                fontSize = 11.sp
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF2D2A33),
            border = BorderStroke(1.dp, Color(0xFF4A4458)),
            modifier = Modifier
              .clickable { onShuffleDeck() }
              .testTag("shuffle_deck_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color(0xFFCAC4D0),
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "خربطة",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFCAC4D0),
                fontSize = 11.sp
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF2D2A33),
            border = BorderStroke(1.dp, Color(0xFF4A4458)),
            modifier = Modifier
              .clickable { showPlayerModal = true }
              .testTag("game_players_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                tint = Color(0xFFD0BCFF),
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "اللاعبين (${uiState.players.size})",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFD0BCFF),
                fontSize = 11.sp
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF2D2A33),
            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
            modifier = Modifier
              .clickable { onNavigateSessionStats() }
              .testTag("game_session_stats_shortcut")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "🏆", fontSize = 12.sp)
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "الألقاب",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFFD700),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}
