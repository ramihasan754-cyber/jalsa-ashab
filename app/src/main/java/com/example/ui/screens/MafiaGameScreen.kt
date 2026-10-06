package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MafiaGamePhase
import com.example.model.MafiaGameState
import com.example.model.MafiaPlayerRole
import com.example.model.MafiaRoleType
import com.example.model.PenaltyItem
import com.example.model.Player
import com.example.ui.components.PenaltyDialog
import com.example.ui.components.getAvatarColor
import com.example.ui.theme.CategoryMafiaColor
import com.example.ui.theme.CoralPink
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SunsetAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MafiaGameScreen(
  gameState: MafiaGameState,
  players: List<Player>,
  activePenalty: PenaltyItem? = null,
  penaltyTargetPlayer: Player? = null,
  onAddPlayer: (String) -> Unit = {},
  onRemovePlayer: (String) -> Unit = {},
  onAdvancePassPhone: () -> Unit,
  onProceedToNightMafia: () -> Unit,
  onChooseMafiaTarget: (victimPlayerId: String) -> Unit,
  onChooseDoctorProtected: (protectedPlayerId: String) -> Unit,
  onChooseDetectiveInvestigation: (investigatedPlayerId: String) -> Unit,
  onProceedToDayDiscussion: () -> Unit,
  onToggleTimer: () -> Unit,
  onProceedToDayVoting: () -> Unit,
  onCastDayVote: (voterId: String, suspectId: String) -> Unit,
  onFinalizeDayVoting: (eliminatedPlayerId: String?) -> Unit,
  onAdvanceToNextRound: () -> Unit,
  onRestartGame: () -> Unit,
  onNavigateHome: () -> Unit,
  onTriggerPenalty: () -> Unit = {},
  onDismissPenalty: () -> Unit = {},
  onApplyPenalty: () -> Unit = {},
  onRerollPenalty: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var activeVoterIndex by remember(gameState.phase, gameState.roundNumber) { mutableIntStateOf(0) }
  var selectedEliminationTargetId by remember(gameState.phase) { mutableStateOf<String?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Navigation Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onNavigateHome,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .testTag("mafia_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "العودة للرئيسية",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }

        Surface(
          shape = RoundedCornerShape(20.dp),
          color = CategoryMafiaColor.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, CategoryMafiaColor.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🕵️‍♂️", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "المافيا • جولة ${gameState.roundNumber}",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = CategoryMafiaColor
            )
          }
        }

        IconButton(
          onClick = onRestartGame,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .testTag("mafia_restart_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "إعادة تشغيل",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      // Main Game Content by Phase (or Setup if players < 5)
      if (players.size < 5) {
        MafiaPlayerSetupView(
          players = players,
          onAddPlayer = onAddPlayer,
          onRemovePlayer = onRemovePlayer,
          onStart = onRestartGame
        )
      } else {
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.TopCenter
        ) {
          when (gameState.phase) {
          MafiaGamePhase.ROLE_DISTRIBUTION -> {
            RoleDistributionView(
              gameState = gameState,
              onNextPlayer = onAdvancePassPhone
            )
          }

          MafiaGamePhase.NIGHT_INTRO -> {
            NightIntroView(
              roundNumber = gameState.roundNumber,
              onStartNight = onProceedToNightMafia
            )
          }

          MafiaGamePhase.NIGHT_MAFIA -> {
            NightMafiaView(
              assignments = gameState.assignments,
              onSelectVictim = onChooseMafiaTarget
            )
          }

          MafiaGamePhase.NIGHT_DOCTOR -> {
            NightDoctorView(
              assignments = gameState.assignments,
              onSelectProtected = onChooseDoctorProtected
            )
          }

          MafiaGamePhase.NIGHT_DETECTIVE -> {
            NightDetectiveView(
              assignments = gameState.assignments,
              onSelectInvestigate = onChooseDetectiveInvestigation
            )
          }

          MafiaGamePhase.NIGHT_RECAP -> {
            NightRecapView(
              gameState = gameState,
              onProceedToDay = onProceedToDayDiscussion
            )
          }

          MafiaGamePhase.DAY_DISCUSSION -> {
            DayDiscussionView(
              gameState = gameState,
              onToggleTimer = onToggleTimer,
              onProceedToVoting = onProceedToDayVoting
            )
          }

          MafiaGamePhase.DAY_VOTING -> {
            DayVotingView(
              gameState = gameState,
              activeVoterIndex = activeVoterIndex,
              onSelectVote = { voterId, targetId ->
                onCastDayVote(voterId, targetId)
                val alivePlayers = gameState.assignments.filter { it.isAlive }
                if (activeVoterIndex < alivePlayers.size - 1) {
                  activeVoterIndex++
                }
              },
              onFinalizeVoting = onFinalizeDayVoting
            )
          }

          MafiaGamePhase.DAY_ELIMINATION -> {
            DayEliminationView(
              gameState = gameState,
              onNextNight = onAdvanceToNextRound
            )
          }

          MafiaGamePhase.GAME_OVER -> {
            MafiaGameOverView(
              gameState = gameState,
              onRestartGame = onRestartGame,
              onNavigateHome = onNavigateHome
            )
          }
        }
      }
    }
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

// -------------------------------------------------------------
// Phase 1: Role Distribution
// -------------------------------------------------------------
@Composable
private fun RoleDistributionView(
  gameState: MafiaGameState,
  onNextPlayer: () -> Unit
) {
  val currentAssignment = gameState.assignments.getOrNull(gameState.currentPassingIndex) ?: return
  val isLastPlayer = gameState.currentPassingIndex == gameState.assignments.size - 1
  val nextAssignment = gameState.assignments.getOrNull(gameState.currentPassingIndex + 1)
  val nextPlayer = nextAssignment?.player

  var hasSeenRole by remember(gameState.currentPassingIndex) { mutableStateOf(false) }
  var isCardRevealedOpen by remember(gameState.currentPassingIndex) { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(6.dp))

    // Prominent Turn Indicator Banner
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = if (hasSeenRole && !isLastPlayer) EmeraldGreen.copy(alpha = 0.18f) else CategoryMafiaColor.copy(alpha = 0.20f),
      border = BorderStroke(2.dp, if (hasSeenRole && !isLastPlayer) EmeraldGreen else CategoryMafiaColor),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("mafia_turn_banner")
    ) {
      Column(
        modifier = Modifier.padding(vertical = 14.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = if (hasSeenRole && !isLastPlayer) "الدور القادم لتسليم الهاتف لـ 📱" else "سلّم الهاتف الآن لـ 📱",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFD0BCFF)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (hasSeenRole && !isLastPlayer && nextPlayer != null) nextPlayer.name else currentAssignment.player.name,
          style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Black,
            fontSize = 30.sp
          ),
          color = Color.White,
          textAlign = TextAlign.Center
        )
      }
    }

    // Role Card with One-Time Reveal Protection
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 320.dp)
        .clickable(enabled = !hasSeenRole && !isCardRevealedOpen) {
          isCardRevealedOpen = true
        }
        .testTag("mafia_role_card"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(
        containerColor = when {
          hasSeenRole -> Color(0xFF26191D)
          isCardRevealedOpen -> when (currentAssignment.role) {
            MafiaRoleType.MAFIA -> CategoryMafiaColor.copy(alpha = 0.25f)
            MafiaRoleType.DOCTOR -> EmeraldGreen.copy(alpha = 0.25f)
            MafiaRoleType.DETECTIVE -> ElectricCyan.copy(alpha = 0.25f)
            MafiaRoleType.VILLAGER -> SunsetAmber.copy(alpha = 0.25f)
          }
          else -> MaterialTheme.colorScheme.surface
        }
      ),
      border = BorderStroke(
        2.dp,
        when {
          hasSeenRole -> Color(0xFFEF5350).copy(alpha = 0.8f)
          isCardRevealedOpen -> when (currentAssignment.role) {
            MafiaRoleType.MAFIA -> CategoryMafiaColor
            MafiaRoleType.DOCTOR -> EmeraldGreen
            MafiaRoleType.DETECTIVE -> ElectricCyan
            MafiaRoleType.VILLAGER -> SunsetAmber
          }
          else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        }
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        when {
          hasSeenRole -> {
            // Permanently locked view preventing any cheating
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFFEF5350).copy(alpha = 0.15f)
            ) {
              Text(
                text = "🚫 مقفلة نهائياً منعاً للغش",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFEF5350)
              )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "مقفل نهائياً",
              modifier = Modifier.size(54.dp),
              tint = Color(0xFFEF5350)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "تم قفل دور ${currentAssignment.player.name} بنجاح!",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFFFF8A80),
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = if (isLastPlayer) "اكتملت جميع الأدوار! الجميع يغمض عيونه وسلّم الهاتف لإعلان بدء الليل 🌙" else "تم حفظ الدور السري بنجاح ولا يمكن إعادة فتحه منعاً للغش ✋",
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White.copy(alpha = 0.85f),
              textAlign = TextAlign.Center,
              lineHeight = 22.sp
            )
          }

          !isCardRevealedOpen -> {
            // Unopened state
            Box(
              modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(CategoryMafiaColor.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "مغلق",
                tint = CategoryMafiaColor,
                modifier = Modifier.size(40.dp)
              )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "يا ${currentAssignment.player.name}!",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "تأكد ألا أحد ينظر إلى الشاشة، ثم المس هنا لكشف دورك السري لمرة واحدة فقط!",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
              lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = CategoryMafiaColor.copy(alpha = 0.2f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Visibility,
                  contentDescription = null,
                  tint = CategoryMafiaColor,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "انقر لكشف دورك",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = CategoryMafiaColor
                )
              }
            }
          }

          else -> {
            // Open state: secret role displayed
            Text(
              text = currentAssignment.role.iconEmoji,
              fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = currentAssignment.role.displayNameArabic,
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
              color = when (currentAssignment.role) {
                MafiaRoleType.MAFIA -> CoralPink
                MafiaRoleType.DOCTOR -> EmeraldGreen
                MafiaRoleType.DETECTIVE -> ElectricCyan
                MafiaRoleType.VILLAGER -> SunsetAmber
              }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = currentAssignment.role.descriptionArabic,
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.VisibilityOff,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "احفظ دورك جيداً ثم اضغط الزر أدناه للقفل",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // Action Button below card
    when {
      !hasSeenRole && !isCardRevealedOpen -> {
        Button(
          onClick = { isCardRevealedOpen = true },
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("mafia_reveal_role_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = CategoryMafiaColor)
        ) {
          Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "اكشف دوري 👁️",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }
      }

      !hasSeenRole && isCardRevealedOpen -> {
        Button(
          onClick = {
            hasSeenRole = true
            isCardRevealedOpen = false
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("mafia_hide_and_lock_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
        ) {
          Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "أخفِ واقفل بطاقتي نهائياً 🔒",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }
      }

      else -> {
        Button(
          onClick = onNextPlayer,
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("mafia_next_player_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = if (isLastPlayer) CategoryMafiaColor else EmeraldGreen)
        ) {
          Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isLastPlayer) "الجميع يغمض عيونه.. ابدأ مرحلة الليل 🌙" else "سلّم الهاتف إلى: ${nextPlayer?.name ?: "اللاعب التالي"} ➡️",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }
      }
    }

    Text(
      text = "لاعب ${gameState.currentPassingIndex + 1} من ${gameState.assignments.size}",
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

// -------------------------------------------------------------
// Phase 2: Night Intro
// -------------------------------------------------------------
@Composable
private fun NightIntroView(
  roundNumber: Int,
  onStartNight: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(vertical = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(100.dp)
        .clip(CircleShape)
        .background(CategoryMafiaColor.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Nightlight,
        contentDescription = "الليل",
        tint = CategoryMafiaColor,
        modifier = Modifier.size(54.dp)
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "نامت المدينة.. 🌙",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
      color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "الجميع يغمضون أعينهم الآن في الجلسة!\nسيبدأ الموجه (الراوي) بإيقاظ الأدوار تباعاً.",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(36.dp))

    Button(
      onClick = onStartNight,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("start_night_phases_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = CategoryMafiaColor
      )
    ) {
      Text(
        text = "استيقظت المافيا 🔪",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
    }
  }
}

// -------------------------------------------------------------
// Phase 3: Night Mafia
// -------------------------------------------------------------
@Composable
private fun NightMafiaView(
  assignments: List<MafiaPlayerRole>,
  onSelectVictim: (String) -> Unit
) {
  var selectedId by remember { mutableStateOf<String?>(null) }
  val alivePlayers = assignments.filter { it.isAlive }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "🔪 دور المافيا",
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
      color = CoralPink
    )

    Text(
      text = "تستيقظ المافيا في صمت وتتفق بالإشارة على اختيار الضحية الليلة:",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    // Player Grid/List
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      alivePlayers.forEach { rolePlayer ->
        val isSelected = selectedId == rolePlayer.player.id
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { selectedId = rolePlayer.player.id }
            .testTag("mafia_victim_${rolePlayer.player.id}"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CoralPink.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
          ),
          border = BorderStroke(
            1.5.dp,
            if (isSelected) CoralPink else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(getAvatarColor(rolePlayer.player.avatarColorIndex)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = rolePlayer.player.name.firstOrNull()?.toString() ?: "👤",
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = rolePlayer.player.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            if (isSelected) {
              Text(text = "🎯 الضحية", color = CoralPink, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Button(
      onClick = { selectedId?.let { onSelectVictim(it) } },
      enabled = selectedId != null,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("confirm_mafia_victim_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = CoralPink)
    ) {
      Text(
        text = "تأكيد اختيار الضحية 🔪",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
    }
  }
}

// -------------------------------------------------------------
// Phase 4: Night Doctor
// -------------------------------------------------------------
@Composable
private fun NightDoctorView(
  assignments: List<MafiaPlayerRole>,
  onSelectProtected: (String) -> Unit
) {
  var selectedId by remember { mutableStateOf<String?>(null) }
  val alivePlayers = assignments.filter { it.isAlive }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "🩺 دور الطبيب",
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
      color = EmeraldGreen
    )

    Text(
      text = "تنام المافيا ويستيقظ الطبيب.. اختر شخصاً لإنقاذه وحمايته من القتل الليلة:",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      alivePlayers.forEach { rolePlayer ->
        val isSelected = selectedId == rolePlayer.player.id
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { selectedId = rolePlayer.player.id }
            .testTag("doctor_protect_${rolePlayer.player.id}"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EmeraldGreen.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
          ),
          border = BorderStroke(
            1.5.dp,
            if (isSelected) EmeraldGreen else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(getAvatarColor(rolePlayer.player.avatarColorIndex)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = rolePlayer.player.name.firstOrNull()?.toString() ?: "👤",
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = rolePlayer.player.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            if (isSelected) {
              Text(text = "🛡️ محمي", color = EmeraldGreen, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Button(
      onClick = { selectedId?.let { onSelectProtected(it) } },
      enabled = selectedId != null,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("confirm_doctor_protect_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
    ) {
      Text(
        text = "تأكيد الحماية 🛡️",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
    }
  }
}

// -------------------------------------------------------------
// Phase 5: Night Detective
// -------------------------------------------------------------
@Composable
private fun NightDetectiveView(
  assignments: List<MafiaPlayerRole>,
  onSelectInvestigate: (String) -> Unit
) {
  var selectedId by remember { mutableStateOf<String?>(null) }
  var showResultDialog by remember { mutableStateOf(false) }
  val alivePlayers = assignments.filter { it.isAlive }
  val selectedRolePlayer = alivePlayers.firstOrNull { it.player.id == selectedId }

  if (showResultDialog && selectedRolePlayer != null) {
    val isMafia = selectedRolePlayer.role == MafiaRoleType.MAFIA
    AlertDialog(
      onDismissRequest = { /* Require action */ },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("🔍", fontSize = 22.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text("نتيجة التحقيق السري", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "اللاعب المستجوب: ${selectedRolePlayer.player.name}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(14.dp))
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isMafia) CoralPink.copy(alpha = 0.2f) else EmeraldGreen.copy(alpha = 0.2f),
            border = BorderStroke(1.5.dp, if (isMafia) CoralPink else EmeraldGreen)
          ) {
            Text(
              text = if (isMafia) "⚠️ مافيا 🔪" else "🛡️ مواطن بريء",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Black,
              color = if (isMafia) CoralPink else EmeraldGreen,
              modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "هذه النتيجة للشرطي / المحقق فقط! احفظها سراً ولا تخبر أحداً حتى يبدأ نقاش الصباح.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showResultDialog = false
            onSelectInvestigate(selectedRolePlayer.player.id)
          },
          colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color.Black),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("confirm_detective_popup_button")
        ) {
          Text("فهمت النتيجة، أخفِ الجهاز للجميع 🤫", fontWeight = FontWeight.Bold)
        }
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "🕵️‍♂️ دور المحقق",
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
      color = ElectricCyan
    )

    Text(
      text = "ينام الطبيب ويستيقظ المحقق.. أشر على لاعب لمعرفة هل هو من المافيا أم مواطن:",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      alivePlayers.forEach { rolePlayer ->
        val isSelected = selectedId == rolePlayer.player.id
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { selectedId = rolePlayer.player.id }
            .testTag("detective_investigate_${rolePlayer.player.id}"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ElectricCyan.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
          ),
          border = BorderStroke(
            1.5.dp,
            if (isSelected) ElectricCyan else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(getAvatarColor(rolePlayer.player.avatarColorIndex)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = rolePlayer.player.name.firstOrNull()?.toString() ?: "👤",
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = rolePlayer.player.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            if (isSelected) {
              Text(text = "🔍 فحص", color = ElectricCyan, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Button(
      onClick = {
        if (selectedId != null) {
          showResultDialog = true
        }
      },
      enabled = selectedId != null,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("confirm_detective_investigate_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
    ) {
      Text(
        text = "كشف هوية اللاعب للمحقق 🔍",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.Black
      )
    }
  }
}

// -------------------------------------------------------------
// Phase 6: Night Recap
// -------------------------------------------------------------
@Composable
private fun NightRecapView(
  gameState: MafiaGameState,
  onProceedToDay: () -> Unit
) {
  val victim = gameState.assignments.firstOrNull { it.player.id == gameState.lastNightEliminatedPlayerId }
  val wasSaved = gameState.wasSavedByDoctor
  val investigatedPlayer = gameState.assignments.firstOrNull { it.player.id == gameState.nightInvestigatedPlayerId }
  val isInvestigatedMafia = gameState.lastInvestigationResultIsMafia

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    // Sunrise Header
    Box(
      modifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .background(SunsetAmber.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.WbSunny,
        contentDescription = "شروق الشمس",
        tint = SunsetAmber,
        modifier = Modifier.size(44.dp)
      )
    }

    Text(
      text = "أشرقت شمس النهار! ☀️",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
      color = MaterialTheme.colorScheme.onSurface
    )

    Text(
      text = "يستيقظ الجميع ويفتحون أعينهم لسماع ما حدث في ظلام الليل...",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    // Outcome Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (wasSaved) EmeraldGreen.copy(alpha = 0.15f) else CoralPink.copy(alpha = 0.15f)
      ),
      border = BorderStroke(
        1.5.dp,
        if (wasSaved) EmeraldGreen else CoralPink
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        if (wasSaved) {
          Text(text = "🛡️ نجاح الطبيب!", style = MaterialTheme.typography.titleLarge, color = EmeraldGreen, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "حاولت المافيا الغدر بأحد الأشخاص، لكن الطبيب تدخل في الوقت المناسب وأنقذ حياته!\nلم يمت أحد هذه الليلة.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
          )
        } else if (victim != null) {
          Text(text = "⚰️ حادثة اغتيال!", style = MaterialTheme.typography.titleLarge, color = CoralPink, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "استيقظت المدينة على خبر مفجع.. قُتل ${victim.player.name} غدراً في الليل وخرج من اللعبة!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "دوره كان: ${victim.role.displayNameArabic} ${victim.role.iconEmoji}",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        } else {
          Text(
            text = "مرت الليلة بهدوء وسلام!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Button(
      onClick = onProceedToDay,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("start_day_discussion_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber)
    ) {
      Text(
        text = "بدء نقاش النهار والمحاكمة 🗣️",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.Black
      )
    }
  }
}

// -------------------------------------------------------------
// Phase 7: Day Discussion
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DayDiscussionView(
  gameState: MafiaGameState,
  onToggleTimer: () -> Unit,
  onProceedToVoting: () -> Unit
) {
  val minutes = gameState.timerSecondsRemaining / 60
  val seconds = gameState.timerSecondsRemaining % 60
  val timerText = String.format("%02d:%02d", minutes, seconds)
  val isTimerRunning = gameState.isTimerRunning

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "🗣️ وقت النقاش والاتهام",
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
      color = MaterialTheme.colorScheme.onSurface
    )

    Text(
      text = "الجميع يناقش ويشك في بعضه البعض.. من يبدو متوتراً؟ من يدافع عن من؟",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    // Timer Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.HourglassBottom,
            contentDescription = "عداد النقاش",
            tint = SunsetAmber
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = timerText,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = if (gameState.timerSecondsRemaining <= 20) CoralPink else MaterialTheme.colorScheme.onSurface
          )
        }

        IconButton(
          onClick = onToggleTimer,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(SunsetAmber.copy(alpha = 0.2f))
        ) {
          Icon(
            imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = "تشغيل/إيقاف العداد",
            tint = SunsetAmber
          )
        }
      }
    }

    // Surviving Players Summary
    Text(
      text = "الأحياء في الجلسة حالياً (${gameState.assignments.count { it.isAlive }}):",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.align(Alignment.Start)
    )

    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      gameState.assignments.forEach { rolePlayer ->
        val isAlive = rolePlayer.isAlive
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isAlive) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
          border = BorderStroke(
            1.dp,
            if (isAlive) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f) else Color.Transparent
          )
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isAlive) "🟢 " else "💀 ",
              fontSize = 12.sp
            )
            Text(
              text = rolePlayer.player.name,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = if (isAlive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Button(
      onClick = onProceedToVoting,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("proceed_to_voting_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = CategoryMafiaColor)
    ) {
      Text(
        text = "الانتقال للتصويت والإعدام ⚖️",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
    }
  }
}

// -------------------------------------------------------------
// Phase 8: Day Voting
// -------------------------------------------------------------
@Composable
private fun DayVotingView(
  gameState: MafiaGameState,
  activeVoterIndex: Int,
  onSelectVote: (voterId: String, targetId: String) -> Unit,
  onFinalizeVoting: (eliminatedPlayerId: String?) -> Unit
) {
  val alivePlayers = gameState.assignments.filter { it.isAlive }
  val currentVoter = alivePlayers.getOrNull(activeVoterIndex)
  val allVoted = alivePlayers.all { gameState.dayVotes.containsKey(it.player.id) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "⚖️ تصويت الحارة",
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
      color = MaterialTheme.colorScheme.onSurface
    )

    if (!allVoted && currentVoter != null) {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = CategoryMafiaColor.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, CategoryMafiaColor.copy(alpha = 0.4f))
      ) {
        Text(
          text = "دور اللاعب: ${currentVoter.player.name} للتصويت ضد مشتبه به!",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = CategoryMafiaColor,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
      }

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        alivePlayers.filter { it.player.id != currentVoter.player.id }.forEach { candidate ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onSelectVote(currentVoter.player.id, candidate.player.id) }
              .testTag("vote_suspect_${candidate.player.id}"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = candidate.player.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "صوّت لإعدامه 👈",
                style = MaterialTheme.typography.labelMedium,
                color = CoralPink
              )
            }
          }
        }
      }
    } else {
      // Show voting tally
      val voteCounts = mutableMapOf<String, Int>()
      gameState.dayVotes.values.forEach { targetId ->
        voteCounts[targetId] = (voteCounts[targetId] ?: 0) + 1
      }
      val highestVote = voteCounts.maxByOrNull { it.value }
      val mostVotedPlayer = gameState.assignments.firstOrNull { it.player.id == highestVote?.key }

      Text(
        text = "نتائج تصويت الحارة:",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        alivePlayers.forEach { playerRole ->
          val votes = voteCounts[playerRole.player.id] ?: 0
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (playerRole.player.id == mostVotedPlayer?.player?.id) CoralPink.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
              1.dp,
              if (playerRole.player.id == mostVotedPlayer?.player?.id) CoralPink else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = playerRole.player.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "$votes أصوات",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = if (votes > 0) CoralPink else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = { onFinalizeVoting(mostVotedPlayer?.player?.id) },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("finalize_execution_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CoralPink)
      ) {
        Text(
          text = if (mostVotedPlayer != null) "تنفيذ حكم الإعدام على ${mostVotedPlayer.player.name} ⚖️" else "تخطي بدون إعدام 🕊️",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
      }
    }
  }
}

// -------------------------------------------------------------
// Phase 9: Day Elimination
// -------------------------------------------------------------
@Composable
private fun DayEliminationView(
  gameState: MafiaGameState,
  onNextNight: () -> Unit
) {
  val executed = gameState.assignments.firstOrNull { it.player.id == gameState.dayEliminatedPlayerId }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    if (executed != null) {
      Text(text = "⚖️", fontSize = 60.sp)
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "تم إعدام ${executed.player.name}!",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
        color = CoralPink
      )
      Spacer(modifier = Modifier.height(8.dp))
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
      ) {
        Text(
          text = "دوره كان: ${executed.role.displayNameArabic} ${executed.role.iconEmoji}",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = if (executed.role == MafiaRoleType.MAFIA) "🎉 أحسنتم! تخلصت الحارة من أحد أفراد المافيا!" else "😱 يا للأسف! تم إعدام مواطن بريء!",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )
    } else {
      Text(
        text = "لم يتم إعدام أي شخص اليوم!",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    Spacer(modifier = Modifier.height(30.dp))

    Button(
      onClick = onNextNight,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("next_night_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = CategoryMafiaColor)
    ) {
      Text(
        text = "دخول الليل التالي 🌙",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
    }
  }
}

// -------------------------------------------------------------
// Phase 10: Game Over
// -------------------------------------------------------------
@Composable
private fun MafiaGameOverView(
  gameState: MafiaGameState,
  onRestartGame: () -> Unit,
  onNavigateHome: () -> Unit
) {
  val winner = gameState.winningTeamArabic ?: "انتهاء اللعبة"

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(20.dp))

    Text(text = "🏆", fontSize = 64.sp)

    Text(
      text = "انتهت اللعبة!",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
      color = MaterialTheme.colorScheme.onSurface
    )

    Surface(
      shape = RoundedCornerShape(16.dp),
      color = CategoryMafiaColor.copy(alpha = 0.2f),
      border = BorderStroke(1.dp, CategoryMafiaColor.copy(alpha = 0.5f))
    ) {
      Text(
        text = "الفائز: $winner",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
        color = CategoryMafiaColor,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "كشف جميع أدوار اللاعبين في الجلسة:",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      gameState.assignments.forEach { rolePlayer ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (rolePlayer.isAlive) "🟢 " else "💀 ",
                fontSize = 14.sp
              )
              Text(
                text = rolePlayer.player.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = "${rolePlayer.role.displayNameArabic} ${rolePlayer.role.iconEmoji}",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = when (rolePlayer.role) {
                MafiaRoleType.MAFIA -> CoralPink
                MafiaRoleType.DOCTOR -> EmeraldGreen
                MafiaRoleType.DETECTIVE -> ElectricCyan
                MafiaRoleType.VILLAGER -> SunsetAmber
              }
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
      onClick = onRestartGame,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("restart_mafia_game_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = CategoryMafiaColor)
    ) {
      Text(
        text = "لعب جولة جديدة 🔄",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
    }

    OutlinedButton(
      onClick = onNavigateHome,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp),
      shape = RoundedCornerShape(16.dp)
    ) {
      Text(
        text = "العودة للرئيسية 🏠",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
private fun MafiaPlayerSetupView(
  players: List<Player>,
  onAddPlayer: (String) -> Unit,
  onRemovePlayer: (String) -> Unit,
  onStart: () -> Unit
) {
  var newPlayerName by remember { mutableStateOf("") }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Spacer(modifier = Modifier.height(16.dp))

      Surface(
        shape = CircleShape,
        color = CategoryMafiaColor.copy(alpha = 0.2f),
        modifier = Modifier.size(76.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(text = "🕵️‍♂️", fontSize = 38.sp)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "إعداد لاعبي المافيا",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "لعبة المافيا تحتاج إلى ٥ لاعبين على الأقل لتوزيع الأدوار السري (المافيا، الطبيب، الشرطي، والمواطنون)!",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Input Field + Add Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = newPlayerName,
          onValueChange = { newPlayerName = it },
          placeholder = { Text("أدخل اسم اللاعب...") },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("mafia_add_player_input")
        )

        Spacer(modifier = Modifier.width(10.dp))

        Button(
          onClick = {
            if (newPlayerName.isNotBlank()) {
              onAddPlayer(newPlayerName.trim())
              newPlayerName = ""
            }
          },
          enabled = newPlayerName.isNotBlank(),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = CategoryMafiaColor),
          modifier = Modifier
            .height(56.dp)
            .testTag("mafia_add_player_button")
        ) {
          Text("إضافة ➕", fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Player counter
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (players.size >= 5) EmeraldGreen.copy(alpha = 0.15f) else CoralPink.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, if (players.size >= 5) EmeraldGreen else CoralPink)
      ) {
        Text(
          text = if (players.size >= 5) "✅ عدد اللاعبين: ${players.size} (جاهزون للبدء)" else "⚠️ عدد اللاعبين: ${players.size} / ٥ (متبقي ${5 - players.size} لاعبين)",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = if (players.size >= 5) EmeraldGreen else CoralPink,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // Players List
    items(players) { player ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(getAvatarColor(player.avatarColorIndex)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = player.name.firstOrNull()?.toString() ?: "👤", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = player.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          }

          IconButton(
            onClick = { onRemovePlayer(player.id) },
            modifier = Modifier.testTag("remove_mafia_player_${player.id}")
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = onStart,
        enabled = players.size >= 5,
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("start_mafia_distribution_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CategoryMafiaColor)
      ) {
        Text(
          text = if (players.size >= 5) "بدء توزيع الأدوار السري 🎭" else "أضف ${5 - players.size} لاعبين للبدء",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(36.dp))
    }
  }
}

