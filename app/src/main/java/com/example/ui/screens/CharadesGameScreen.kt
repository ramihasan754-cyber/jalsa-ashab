package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CharadesGameState
import com.example.model.CharadesPhase
import com.example.model.CharadesPlayMode
import com.example.model.CharadesWordItem
import com.example.model.PenaltyItem
import com.example.model.Player
import com.example.model.Team
import com.example.ui.components.CharadesScoreHistoryModal
import com.example.ui.components.PenaltyDialog
import com.example.ui.theme.CategoryCharadesColor

private val avatarColors = listOf(
  Color(0xFF6750A4),
  Color(0xFF9C27B0),
  Color(0xFF00897B),
  Color(0xFFE65100),
  Color(0xFF1E88E5),
  Color(0xFF43A047),
  Color(0xFFD81B60),
  Color(0xFFF4511E)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharadesGameScreen(
  charadesState: CharadesGameState,
  allPlayers: List<Player>,
  isDarkMode: Boolean,
  activePenalty: PenaltyItem? = null,
  penaltyTargetPlayer: Player? = null,
  onSelectSubCategory: (com.example.model.CharadesSubCategory) -> Unit = {},
  onChangeSubCategory: () -> Unit = {},
  onStartRound: () -> Unit,
  onPauseTimer: () -> Unit,
  onResumeTimer: () -> Unit,
  onCorrectGuess: () -> Unit,
  onSkipWord: () -> Unit,
  onNextTurn: () -> Unit,
  onSwitchPlayerTeam: (String) -> Unit,
  onShuffleTeams: () -> Unit,
  onUpdateTeamNames: (String, String) -> Unit = { _, _ -> },
  onSetPlayMode: (CharadesPlayMode) -> Unit = {},
  onResetGame: () -> Unit,
  onClearHistory: () -> Unit = {},
  onToggleDarkMode: () -> Unit,
  onNavigateHome: () -> Unit,
  onTriggerPenalty: () -> Unit = {},
  onDismissPenalty: () -> Unit = {},
  onApplyPenalty: () -> Unit = {},
  onRerollPenalty: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showRulesDialog by remember { mutableStateOf(false) }
  var showResetConfirmDialog by remember { mutableStateOf(false) }
  var showEditTeamsDialog by remember { mutableStateOf(false) }
  var showScoreHistoryModal by remember { mutableStateOf(false) }
  var editTeamAName by remember(charadesState.teamAName) { mutableStateOf(charadesState.teamAName) }
  var editTeamBName by remember(charadesState.teamBName) { mutableStateOf(charadesState.teamBName) }

  if (showEditTeamsDialog) {
    AlertDialog(
      onDismissRequest = { showEditTeamsDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("✏️", fontSize = 20.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text("تعديل أسماء الفرق", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "اكتبوا أسماء الفرق المناسبة لجلستكم (مثلاً: فريق النشامى، فريق النشمي...)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          OutlinedTextField(
            value = editTeamAName,
            onValueChange = { editTeamAName = it },
            label = { Text("اسم الفريق الأول (أ)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("edit_team_a_name_input")
          )
          OutlinedTextField(
            value = editTeamBName,
            onValueChange = { editTeamBName = it },
            label = { Text("اسم الفريق الثاني (ب)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("edit_team_b_name_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateTeamNames(editTeamAName, editTeamBName)
            showEditTeamsDialog = false
          },
          modifier = Modifier.testTag("save_team_names_button")
        ) {
          Text("حفظ الأسماء")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditTeamsDialog = false }) {
          Text("إلغاء")
        }
      }
    )
  }

  if (showRulesDialog) {
    CharadesRulesDialog(onDismiss = { showRulesDialog = false })
  }

  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = { Text("إعادة تصفير اللعبة؟", fontWeight = FontWeight.Bold) },
      text = { Text("رح تتصفر النقاط للجولات الحالية بين فريق أ وفريق ب!") },
      confirmButton = {
        TextButton(
          onClick = {
            onResetGame()
            showResetConfirmDialog = false
          },
          modifier = Modifier.testTag("confirm_reset_charades_dialog")
        ) {
          Text("تصفير النقاط", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("إلغاء")
        }
      }
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .windowInsetsPadding(WindowInsets.statusBars)
      .windowInsetsPadding(WindowInsets.navigationBars)
  ) {
    // Top Bar
    TopAppBar(
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🎬", fontSize = 22.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "ولا كلمة",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = "الجولة ${charadesState.roundNumber} - تحدي التمثيل",
              style = MaterialTheme.typography.labelSmall,
              color = CategoryCharadesColor
            )
          }
        }
      },
      navigationIcon = {
        IconButton(
          onClick = onNavigateHome,
          modifier = Modifier.testTag("charades_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "الرجوع للرئيسية",
            tint = MaterialTheme.colorScheme.onBackground
          )
        }
      },
      actions = {
        IconButton(
          onClick = { showScoreHistoryModal = true },
          modifier = Modifier.testTag("charades_score_history_button")
        ) {
          Text(text = "🏆", fontSize = 18.sp)
        }
        IconButton(
          onClick = { showRulesDialog = true },
          modifier = Modifier.testTag("charades_rules_button")
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "قواعد اللعبة",
            tint = MaterialTheme.colorScheme.primary
          )
        }
        IconButton(
          onClick = { showResetConfirmDialog = true },
          modifier = Modifier.testTag("charades_reset_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "إعادة ضبط",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(
          onClick = onToggleDarkMode,
          modifier = Modifier.testTag("charades_dark_mode_toggle")
        ) {
          Icon(
            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = "تبديل المظهر",
            tint = MaterialTheme.colorScheme.onBackground
          )
        }
      },
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background
      )
    )

    // Scoreboard Header: Team A vs Team B (Only shown outside category selection)
    if (charadesState.phase != CharadesPhase.CATEGORY_SELECTION) {
      ScoreboardHeader(
        charadesState = charadesState,
        onSwitchPlayerTeam = onSwitchPlayerTeam,
        onShuffleTeams = onShuffleTeams,
        onOpenEditTeams = {
          editTeamAName = charadesState.teamAName
          editTeamBName = charadesState.teamBName
          showEditTeamsDialog = true
        },
        canEditTeams = charadesState.phase == CharadesPhase.READY
      )
    }

    // Dynamic Phase View
    AnimatedContent(
      targetState = charadesState.phase,
      transitionSpec = {
        (slideInVertically { height -> height / 3 } + fadeIn(tween(300)))
          .togetherWith(slideOutVertically { height -> -height / 3 } + fadeOut(tween(200)))
      },
      label = "charades_phase_transition",
      modifier = Modifier
        .fillMaxSize()
        .weight(1f)
    ) { phase ->
      when (phase) {
        CharadesPhase.CATEGORY_SELECTION -> {
          CharadesCategorySelectionView(
            selectedCategory = charadesState.selectedSubCategory,
            onSelectCategory = onSelectSubCategory
          )
        }
        CharadesPhase.READY -> {
          CharadesReadyView(
            charadesState = charadesState,
            onChangeSubCategory = onChangeSubCategory,
            onSetPlayMode = onSetPlayMode,
            onStartRound = onStartRound
          )
        }
        CharadesPhase.PLAYING -> {
          CharadesPlayingView(
            charadesState = charadesState,
            onPauseTimer = onPauseTimer,
            onResumeTimer = onResumeTimer,
            onCorrectGuess = onCorrectGuess,
            onSkipWord = onSkipWord
          )
        }
        CharadesPhase.ROUND_ENDED, CharadesPhase.GAME_OVER -> {
          CharadesRoundEndedView(
            charadesState = charadesState,
            onNextTurn = onNextTurn,
            onTriggerPenalty = onTriggerPenalty,
            onOpenScoreHistory = { showScoreHistoryModal = true }
          )
        }
      }
    }

    if (showScoreHistoryModal) {
      CharadesScoreHistoryModal(
        charadesState = charadesState,
        onDismiss = { showScoreHistoryModal = false },
        onClearHistory = onClearHistory
      )
    }

    if (activePenalty != null) {
      PenaltyDialog(
        penalty = activePenalty,
        targetPlayer = penaltyTargetPlayer,
        onDismiss = onDismissPenalty,
        onApplyPenalty = onApplyPenalty,
        onRerollPenalty = onRerollPenalty
      )
    }
  }
}

@Composable
private fun ScoreboardHeader(
  charadesState: CharadesGameState,
  onSwitchPlayerTeam: (String) -> Unit,
  onShuffleTeams: () -> Unit,
  onOpenEditTeams: () -> Unit,
  canEditTeams: Boolean
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .shadow(4.dp, RoundedCornerShape(20.dp)),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Team A Box
        TeamScoreBox(
          team = Team.TEAM_A,
          teamCustomName = charadesState.getTeamName(Team.TEAM_A),
          score = charadesState.teamAScore,
          players = charadesState.teamAPlayers,
          isActiveTurn = charadesState.currentTurnTeam == Team.TEAM_A,
          accentColor = Color(0xFFFFB300),
          modifier = Modifier.weight(1f),
          onPlayerClick = if (canEditTeams) onSwitchPlayerTeam else null
        )

        // VS Badge
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "ضد",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          if (canEditTeams) {
            IconButton(
              onClick = onShuffleTeams,
              modifier = Modifier
                .size(32.dp)
                .testTag("charades_shuffle_teams_button")
            ) {
              Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "خلط الفرق عشوائياً",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        // Team B Box
        TeamScoreBox(
          team = Team.TEAM_B,
          teamCustomName = charadesState.getTeamName(Team.TEAM_B),
          score = charadesState.teamBScore,
          players = charadesState.teamBPlayers,
          isActiveTurn = charadesState.currentTurnTeam == Team.TEAM_B,
          accentColor = Color(0xFFAB47BC),
          modifier = Modifier.weight(1f),
          onPlayerClick = if (canEditTeams) onSwitchPlayerTeam else null
        )
      }

      if (canEditTeams) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            modifier = Modifier
              .clickable { onOpenEditTeams() }
              .testTag("open_edit_team_names_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "تعديل أسماء الفرق ✏️",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp
              )
            }
          }

          Text(
            text = "💡 اضغط على اسم لاعب لنقله",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
private fun TeamScoreBox(
  team: Team,
  teamCustomName: String,
  score: Int,
  players: List<Player>,
  isActiveTurn: Boolean,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onPlayerClick: ((String) -> Unit)? = null
) {
  val borderStroke = if (isActiveTurn) {
    BorderStroke(2.dp, accentColor)
  } else {
    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
  }

  val backgroundColor = if (isActiveTurn) {
    accentColor.copy(alpha = 0.12f)
  } else {
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
  }

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = backgroundColor,
    border = borderStroke,
    modifier = modifier.testTag("team_box_${team.name}")
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(text = team.iconEmoji, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = teamCustomName,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = if (isActiveTurn) accentColor else MaterialTheme.colorScheme.onSurface,
          maxLines = 1
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "$score",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.ExtraBold,
        color = accentColor
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Players Chips
      if (players.isNotEmpty()) {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          items(players) { player ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = accentColor.copy(alpha = 0.2f),
              modifier = Modifier.clickable(enabled = onPlayerClick != null) {
                onPlayerClick?.invoke(player.id)
              }
            ) {
              Text(
                text = player.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      } else {
        Text(
          text = "لا يوجد لاعبين",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 10.sp
        )
      }
    }
  }
}

@Composable
private fun CharadesReadyView(
  charadesState: CharadesGameState,
  onChangeSubCategory: () -> Unit = {},
  onSetPlayMode: (CharadesPlayMode) -> Unit = {},
  onStartRound: () -> Unit
) {
  val activeTeam = charadesState.currentTurnTeam
  val teamName = charadesState.getTeamName(activeTeam)
  val teamColor = if (activeTeam == Team.TEAM_A) Color(0xFFFFB300) else Color(0xFFAB47BC)
  val actorName = charadesState.currentActorPlayer?.name ?: "أحد لاعبي الفريق"

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    item {
      Spacer(modifier = Modifier.height(16.dp))

      // Active Subcategory Chip
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, CategoryCharadesColor.copy(alpha = 0.5f)),
        modifier = Modifier
          .clickable { onChangeSubCategory() }
          .testTag("charades_active_subcategory_badge")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = charadesState.selectedSubCategory.iconEmoji, fontSize = 16.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "التصنيف: ${charadesState.selectedSubCategory.titleArabic}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CategoryCharadesColor.copy(alpha = 0.2f)
          ) {
            Text(
              text = "تغيير 🔄",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = CategoryCharadesColor,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Active Team Turn Banner
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = teamColor.copy(alpha = 0.15f),
        border = BorderStroke(1.5.dp, teamColor),
        modifier = Modifier.testTag("charades_ready_turn_banner")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = activeTeam.iconEmoji, fontSize = 20.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "الدور الآن: $teamName",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = teamColor
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Play Mode Selection Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "⏱️ اختر نمط اللعب خلال الـ ٦٠ ثانية:",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Option A: Single Card
            val isSingle = charadesState.playMode == CharadesPlayMode.SINGLE_CARD_PER_ROUND
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = if (isSingle) CategoryCharadesColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface,
              border = BorderStroke(
                if (isSingle) 2.dp else 1.dp,
                if (isSingle) CategoryCharadesColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
              ),
              modifier = Modifier
                .weight(1f)
                .clickable { onSetPlayMode(CharadesPlayMode.SINGLE_CARD_PER_ROUND) }
                .testTag("charades_mode_single_card")
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "🎯", fontSize = 22.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "كرت واحد لكل دقيقة",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = if (isSingle) CategoryCharadesColor else MaterialTheme.colorScheme.onSurface,
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = "تنتهي الجولة فور حزر الكرت أو انتهاء الوقت",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.Center,
                  fontSize = 9.sp,
                  lineHeight = 12.sp
                )
              }
            }

            // Option B: Multiple Cards
            val isMulti = charadesState.playMode == CharadesPlayMode.MULTIPLE_CARDS_SPEED
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = if (isMulti) CategoryCharadesColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface,
              border = BorderStroke(
                if (isMulti) 2.dp else 1.dp,
                if (isMulti) CategoryCharadesColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
              ),
              modifier = Modifier
                .weight(1f)
                .clickable { onSetPlayMode(CharadesPlayMode.MULTIPLE_CARDS_SPEED) }
                .testTag("charades_mode_multiple_cards")
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "⚡", fontSize = 22.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "عدة كروت متتالية",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = if (isMulti) CategoryCharadesColor else MaterialTheme.colorScheme.onSurface,
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = "حزر أكثر من كرت وتجميع نقاط حتى ينتهي الوقت",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.Center,
                  fontSize = 9.sp,
                  lineHeight = 12.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Main Instruction Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, teamColor.copy(alpha = 0.4f))
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Surface(
            shape = CircleShape,
            color = teamColor.copy(alpha = 0.2f),
            modifier = Modifier.size(70.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(text = "🎭", fontSize = 36.sp)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "الممثل المختار:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = actorName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = teamColor
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "الممثل بس اللي بشوف الكرت على التلفون، وببدأ يمثل بالإشارات والحركات بدون ما يحكي ولا حرف!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Rule Bullet Points
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "⚡ معكم دقيقة كاملة (٦٠ ثانية) لأكبر عدد كلمات!",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "🚫 ممنوع الحكي، ممنوع الهمس، ممنوع الإشارة لأغراض بالغرفة!",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "✅ كل كرت بحزروه بياخد فريقكم عليه نقطة لايف.",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF43A047),
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))

      // Start Button
      Button(
        onClick = onStartRound,
        modifier = Modifier
          .fillMaxWidth()
          .height(58.dp)
          .testTag("start_charades_round_button"),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = teamColor,
          contentColor = Color(0xFF1C1B1F)
        )
      ) {
        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(26.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "ابدأ الجولة (٦٠ ثانية) ⏱️",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold
        )
      }

      Spacer(modifier = Modifier.height(36.dp))
    }
  }
}

@Composable
private fun CharadesPlayingView(
  charadesState: CharadesGameState,
  onPauseTimer: () -> Unit,
  onResumeTimer: () -> Unit,
  onCorrectGuess: () -> Unit,
  onSkipWord: () -> Unit
) {
  val secondsRemaining = charadesState.timerSecondsRemaining
  val progress = (secondsRemaining.toFloat() / 60f).coerceIn(0f, 1f)
  val isLowTime = secondsRemaining <= 10

  val timerColor by animateColorAsState(
    targetValue = if (isLowTime) MaterialTheme.colorScheme.error else CategoryCharadesColor,
    animationSpec = tween(300),
    label = "charades_timer_color"
  )

  // Infinite scale animation for urgent time (< 10 seconds)
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_charades")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isLowTime && charadesState.isTimerRunning) 1.08f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  val wordItem = charadesState.currentWordItem

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 18.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Timer & Status Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Round Correct Badges Counter
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF43A047).copy(alpha = 0.15f),
        border = BorderStroke(1.dp, Color(0xFF43A047).copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF43A047),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${charadesState.wordsCorrectThisRound.size} صحيح",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF43A047)
          )
        }
      }

      // Circular Animated Timer
      Box(
        modifier = Modifier
          .scale(pulseScale)
          .size(80.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxSize(),
          color = timerColor,
          strokeWidth = 6.dp,
          trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "$secondsRemaining",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = timerColor
          )
          Text(
            text = "ثانية",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp
          )
        }
      }

      // Pause/Resume Button
      IconButton(
        onClick = {
          if (charadesState.isTimerRunning) onPauseTimer() else onResumeTimer()
        },
        modifier = Modifier
          .size(44.dp)
          .testTag("charades_timer_toggle")
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.size(40.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = if (charadesState.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (charadesState.isTimerRunning) "إيقاف مؤقت" else "استئناف",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Active Play Mode Chip
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
      Text(
        text = if (charadesState.playMode == CharadesPlayMode.SINGLE_CARD_PER_ROUND) {
          "🎯 نمط: كرت واحد لكل دقيقة (تنتهي الجولة فور الحزر)"
        } else {
          "⚡ نمط: عدة كروت متتالية حتى انتهاء الـ ٦٠ ثانية"
        },
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        fontSize = 11.sp
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Big Word Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .shadow(12.dp, RoundedCornerShape(26.dp))
        .testTag("charades_word_card"),
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(
        2.dp,
        Brush.verticalGradient(
          listOf(
            CategoryCharadesColor,
            CategoryCharadesColor.copy(alpha = 0.3f)
          )
        )
      )
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(
                CategoryCharadesColor.copy(alpha = 0.08f),
                Color.Transparent
              )
            )
          )
          .padding(20.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          // Category Tag Chip
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CategoryCharadesColor.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, CategoryCharadesColor.copy(alpha = 0.6f))
          ) {
            Text(
              text = wordItem?.categoryTag ?: "عام 🇯🇴",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
          }

          Spacer(modifier = Modifier.height(26.dp))

          // Secret Word Display
          Text(
            text = wordItem?.word ?: "جاهز؟",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 38.sp,
            modifier = Modifier
              .padding(horizontal = 8.dp)
              .testTag("charades_secret_word_text")
          )

          Spacer(modifier = Modifier.height(26.dp))

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          ) {
            Text(
              text = "🤐 تمثيل وإشارات فقط بدون كلام!",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Action Buttons: Correct (+1) & Skip
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 32.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Skip Button
      OutlinedButton(
        onClick = onSkipWord,
        modifier = Modifier
          .weight(1f)
          .height(56.dp)
          .testTag("charades_skip_button"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
      ) {
        Icon(
          imageVector = Icons.Default.SkipNext,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "تخطي ⏭️",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold
        )
      }

      // Correct Button (+1 point)
      Button(
        onClick = onCorrectGuess,
        modifier = Modifier
          .weight(1.5f)
          .height(56.dp)
          .testTag("charades_correct_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF43A047),
          contentColor = Color.White
        )
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "صحيح (+1) ✅",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
private fun CharadesRoundEndedView(
  charadesState: CharadesGameState,
  onNextTurn: () -> Unit,
  onTriggerPenalty: () -> Unit = {},
  onOpenScoreHistory: () -> Unit = {}
) {
  val activeTeam = charadesState.currentTurnTeam
  val teamName = charadesState.getTeamName(activeTeam)
  val correctCount = charadesState.wordsCorrectThisRound.size
  val skippedCount = charadesState.wordsSkippedThisRound.size

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    item {
      Spacer(modifier = Modifier.height(16.dp))

      // Time's Up / Goal Reached Banner
      val isSingleAndGuessed = charadesState.playMode == CharadesPlayMode.SINGLE_CARD_PER_ROUND && correctCount > 0
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSingleAndGuessed) Color(0xFF43A047).copy(alpha = 0.2f) else MaterialTheme.colorScheme.errorContainer,
        border = BorderStroke(1.5.dp, if (isSingleAndGuessed) Color(0xFF43A047) else MaterialTheme.colorScheme.error)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = if (isSingleAndGuessed) "🎉" else "⏰", fontSize = 24.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isSingleAndGuessed) {
              "أبدعتم! تم حزر الكرت بنجاح 🎉"
            } else {
              "خلصت الدقيقة! (انتهت الجولة) ⏱️"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = if (isSingleAndGuessed) Color(0xFF43A047) else MaterialTheme.colorScheme.onErrorContainer
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Round Summary Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "نتائج جولة $teamName 🎉",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Points Gained Chip
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF43A047).copy(alpha = 0.18f),
            border = BorderStroke(1.5.dp, Color(0xFF43A047))
          ) {
            Text(
              text = "+$correctCount نقاط بالجولة!",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Black,
              color = Color(0xFF43A047),
              modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Correct words list
          if (charadesState.wordsCorrectThisRound.isNotEmpty()) {
            Text(
              text = "الكلمات اللي حزرتوها بنجاح:",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
              verticalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              charadesState.wordsCorrectThisRound.forEach { word ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = Color(0xFF43A047).copy(alpha = 0.12f),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      tint = Color(0xFF43A047),
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = word,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Medium,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }
                }
              }
            }
          }

          if (charadesState.wordsSkippedThisRound.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "كلمات تم تخطيها ($skippedCount):",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(
              verticalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              charadesState.wordsSkippedThisRound.take(3).forEach { word ->
                Text(
                  text = "• $word",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              if (charadesState.wordsSkippedThisRound.size > 3) {
                Text(
                  text = "و ${charadesState.wordsSkippedThisRound.size - 3} أخرى...",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))

      // Button to view Score History & Results
      Button(
        onClick = onOpenScoreHistory,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("charades_open_history_from_round_ended"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = CategoryCharadesColor.copy(alpha = 0.25f),
          contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.5.dp, CategoryCharadesColor)
      ) {
        Text(
          text = "🏆 عرض سجل النقاط ونتائج الجولات (${charadesState.roundHistory.size})",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedButton(
        onClick = onTriggerPenalty,
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("charades_trigger_penalty_button"),
        shape = RoundedCornerShape(16.dp)
      ) {
        Text(text = "عقاب مضحك للفريق الخاسر 🎭", fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Next Team Turn Button
      val nextTeam = if (activeTeam == Team.TEAM_A) Team.TEAM_B else Team.TEAM_A
      val nextButtonColor = if (nextTeam == Team.TEAM_A) Color(0xFFFFB300) else Color(0xFFAB47BC)

      Button(
        onClick = onNextTurn,
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("charades_next_turn_button"),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = nextButtonColor,
          contentColor = Color(0xFF1C1B1F)
        )
      ) {
        Text(
          text = "دور ${charadesState.getTeamName(nextTeam)} الآن ⬅️",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold
        )
      }

      Spacer(modifier = Modifier.height(36.dp))
    }
  }
}

@Composable
private fun CharadesRulesDialog(onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("🎬", fontSize = 24.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text("قواعد لعبة ولا كلمة", fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "١. بنقسم الجلسة لفريقين: فريق النشمي (أ) وفريق النشامى (ب).",
          style = MaterialTheme.typography.bodyMedium
        )
        Text(
          text = "٢. كل جولة ممثل واحد من الفريق بيمسك التلفون بدون ما حدا من فريقه يشوف الشاشة.",
          style = MaterialTheme.typography.bodyMedium
        )
        Text(
          text = "٣. معكم ٦٠ ثانية بالضبط لتمثل أكبر عدد من الكلمات والمصطلحات الأردنية والعربية!",
          style = MaterialTheme.typography.bodyMedium
        )
        Text(
          text = "٤. ممنوع الحكي أو الهمس أو الإشارة لأشياء ملموسة بالغرفة نهائياً!",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.error
        )
        Text(
          text = "٥. كل كرت فريقه بحزره بضغط 'صحيح' وبياخد نقطة لايف، وإذا صعب عليه بضغط 'تخطي'.",
          style = MaterialTheme.typography.bodyMedium
        )
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss, modifier = Modifier.testTag("dismiss_charades_rules_button")) {
        Text("فهمت، يلا نلعب! 🥳")
      }
    }
  )
}

@Composable
private fun CharadesCategorySelectionView(
  selectedCategory: com.example.model.CharadesSubCategory,
  onSelectCategory: (com.example.model.CharadesSubCategory) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Spacer(modifier = Modifier.height(16.dp))

      // Header Icon
      Surface(
        shape = CircleShape,
        color = CategoryCharadesColor.copy(alpha = 0.15f),
        modifier = Modifier.size(76.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(text = "🎬", fontSize = 38.sp)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "اختر تصنيف التحدي",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "حدد نوع الكلمات اللي حابين تمثلوها في هذه الجولة بين الفريقين:",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp
      )

      Spacer(modifier = Modifier.height(24.dp))
    }

    // Option A: Movies & TV Shows
    item {
      SubCategorySelectionCard(
        title = "مسلسلات وأفلام",
        subtitle = "أشهر المسلسلات والأفلام والمسرحيات العربية (١٥٠ خيار)",
        iconEmoji = "🎬",
        badgeText = "كلاسيكيات الشاشة 📺",
        accentColor = Color(0xFFFFB300),
        samples = listOf("باب الحارة", "مدرسة المشاغبين", "اللمبي", "الهيبة"),
        isSelected = selectedCategory == com.example.model.CharadesSubCategory.MOVIES_AND_SERIES,
        testTag = "charades_option_movies",
        onClick = { onSelectCategory(com.example.model.CharadesSubCategory.MOVIES_AND_SERIES) }
      )

      Spacer(modifier = Modifier.height(16.dp))
    }

    // Option B: Specific Details & Objects
    item {
      SubCategorySelectionCard(
        title = "تفاصيل وأشياء دقيقة",
        subtitle = "١٠٠ غرض وتفصيلة دقيقة وعفوية من حياتنا اليومية",
        iconEmoji = "🔍",
        badgeText = "١٠٠ غرض جديد 🔥",
        accentColor = Color(0xFF00B0FF),
        samples = listOf("سلك شاحن معقد", "فردة جرابات ضايعة", "زر قميص مقطوع", "قشرة بصل"),
        isSelected = selectedCategory == com.example.model.CharadesSubCategory.SPECIFIC_DETAILS,
        testTag = "charades_option_details",
        onClick = { onSelectCategory(com.example.model.CharadesSubCategory.SPECIFIC_DETAILS) }
      )

      Spacer(modifier = Modifier.height(36.dp))
    }
  }
}

@Composable
private fun SubCategorySelectionCard(
  title: String,
  subtitle: String,
  iconEmoji: String,
  badgeText: String,
  accentColor: Color,
  samples: List<String>,
  isSelected: Boolean,
  testTag: String,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag(testTag)
      .shadow(6.dp, RoundedCornerShape(22.dp)),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(
      width = if (isSelected) 2.dp else 1.dp,
      color = if (isSelected) accentColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Surface(
          shape = CircleShape,
          color = accentColor.copy(alpha = 0.18f),
          modifier = Modifier.size(54.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(text = iconEmoji, fontSize = 28.sp)
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = accentColor.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
        ) {
          Text(
            text = badgeText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Samples flow
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        samples.take(3).forEach { sample ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          ) {
            Text(
              text = sample,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              fontSize = 11.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Action button
      Button(
        onClick = onClick,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = accentColor,
          contentColor = Color(0xFF1C1B1F)
        )
      ) {
        Text(
          text = "اختر $title 👈",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

