package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PartyQuestionsData
import com.example.model.PenaltyItem
import com.example.model.Player
import com.example.model.SpyGameState
import com.example.model.SpyPhase
import com.example.ui.components.PenaltyDialog
import com.example.ui.components.getAvatarColor
import com.example.ui.theme.CategorySpyColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpyGameScreen(
  gameState: SpyGameState,
  players: List<Player>,
  currentRound: Int = 1,
  maxRounds: Int = 0,
  isGameFinished: Boolean = false,
  activePenalty: PenaltyItem? = null,
  penaltyTargetPlayer: Player? = null,
  onAdvancePassPhone: () -> Unit,
  onToggleTimer: () -> Unit,
  onNextQuestionTurn: () -> Unit,
  onProceedToAccusation: () -> Unit,
  onCastVote: (voterId: String, suspectId: String) -> Unit = { _, _ -> },
  onFinalizeVoting: () -> Unit = {},
  onAccuseSpy: (String) -> Unit,
  onSpyGuessLocation: (String) -> Unit,
  onNextRound: () -> Unit = {},
  onRestartGame: () -> Unit,
  onNavigateHome: () -> Unit,
  onTriggerPenalty: () -> Unit,
  onDismissPenalty: () -> Unit = {},
  onApplyPenalty: () -> Unit = {},
  onRerollPenalty: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var revealedPlayerIndices by remember(gameState.location?.id, gameState.spyPlayerId) { mutableStateOf(emptySet<Int>()) }
  var isCardRevealedOpen by remember(gameState.currentPassingPlayerIndex) { mutableStateOf(false) }
  var activeVoterIndex by remember(gameState.location?.id, gameState.spyPlayerId) { mutableIntStateOf(0) }

  val currentAssignment = gameState.assignments.getOrNull(gameState.currentPassingPlayerIndex)
  val isLastPlayer = gameState.currentPassingPlayerIndex == gameState.assignments.size - 1
  val playerIndex = gameState.currentPassingPlayerIndex
  val hasSeenCard = playerIndex in revealedPlayerIndices

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
            .testTag("spy_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "العودة للرئيسية",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }

        Surface(
          shape = RoundedCornerShape(20.dp),
          color = CategorySpyColor.copy(alpha = 0.2f),
          border = androidx.compose.foundation.BorderStroke(1.dp, CategorySpyColor.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🕵️‍♂️", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (maxRounds > 0) "الجاسوس • جولة $currentRound من $maxRounds" else "الجاسوس • جولة $currentRound (مفتوح)",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        IconButton(
          onClick = onRestartGame,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .testTag("spy_restart_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "إعادة جولة جديدة",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      when (gameState.phase) {
        SpyPhase.PASS_PHONE -> {
          // --- Phase 1: Pass Phone Securely & Secret Peek ---
          if (currentAssignment != null) {
            val currentPlayer = currentAssignment.player
            val nextPlayer = gameState.assignments.getOrNull(gameState.currentPassingPlayerIndex + 1)?.player

            Column(
              modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              // Header indicating current player turn or next handover
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = when {
                    hasSeenCard -> Color(0xFF81C784).copy(alpha = 0.2f)
                    isCardRevealedOpen -> Color(0xFFFFB74D).copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                  }
                ) {
                  Text(
                    text = when {
                      hasSeenCard -> if (isLastPlayer) "اكتملت جميع الأدوار! 🎉" else "تم قفل دور ${currentPlayer.name} بنجاح ✅"
                      isCardRevealedOpen -> "البطاقة مفتوحة • دورك يا ${currentPlayer.name} 👁️"
                      else -> "اللاعب ${gameState.currentPassingPlayerIndex + 1} من ${gameState.assignments.size}"
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = when {
                      hasSeenCard -> Color(0xFF81C784)
                      isCardRevealedOpen -> Color(0xFFFFB74D)
                      else -> MaterialTheme.colorScheme.primary
                    }
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val targetPlayer = if (hasSeenCard && !isLastPlayer && nextPlayer != null) nextPlayer else currentPlayer

                // Prominent Turn Indicator Header
                Surface(
                  shape = RoundedCornerShape(18.dp),
                  color = CategorySpyColor.copy(alpha = 0.16f),
                  border = BorderStroke(2.dp, CategorySpyColor),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                ) {
                  Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(
                      text = if (hasSeenCard && !isLastPlayer) "الدور القادم لتسليم الهاتف لـ 📱" else "سلّم الهاتف الآن لـ 📱",
                      style = MaterialTheme.typography.labelMedium,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFD0BCFF)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = targetPlayer.name,
                      style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp
                      ),
                      color = Color.White,
                      textAlign = TextAlign.Center
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Avatar shows current player when viewing/unopened, or NEXT player when locked
                Box(
                  modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(getAvatarColor(targetPlayer.avatarColorIndex)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = if (hasSeenCard && isLastPlayer) "🕵️‍♂️" else targetPlayer.name.firstOrNull()?.toString() ?: "👤",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (hasSeenCard && isLastPlayer) 26.sp else 22.sp
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = when {
                    hasSeenCard -> if (isLastPlayer) "الجميع جاهز لبدء اللعبة!" else "الآن سلّم الجوال إلى: ${nextPlayer?.name ?: "اللاعب التالي"} 📱"
                    isCardRevealedOpen -> "أهلاً بك يا ${currentPlayer.name} 👋"
                    else -> "تأكد من عدم رؤية الآخرين ثم افتح بطاقتك"
                  },
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = when {
                    hasSeenCard -> if (isLastPlayer) "كل اللاعبين حفظوا أدوارهم، حان وقت التحقيق!" else "انتهى دور ${currentPlayer.name}. سلّم الجوال لـ ${nextPlayer?.name ?: "التالي"} لرؤية دوره."
                    isCardRevealedOpen -> "احرص على سرية الشاشة! أنت فقط من يرى هذه البطاقة 🤫"
                    else -> "يا ${currentPlayer.name}، تأكد أن لا أحد ينظر إلى الشاشة ثم افتح بطاقتك"
                  },
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.Center
                )
              }

              // One-Time Peek Shield Card per Participant
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .wrapContentHeight()
                  .defaultMinSize(minHeight = 240.dp)
                  .clip(RoundedCornerShape(24.dp))
                  .then(
                    if (!hasSeenCard && !isCardRevealedOpen) {
                      Modifier.clickable { isCardRevealedOpen = true }
                    } else Modifier
                  )
                  .testTag("spy_secret_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                  containerColor = when {
                    hasSeenCard -> Color(0xFF26191D)
                    isCardRevealedOpen -> if (currentAssignment.isSpy) Color(0xFF2C1B1F) else Color(0xFF1E1E38)
                    else -> MaterialTheme.colorScheme.surface
                  }
                ),
                border = androidx.compose.foundation.BorderStroke(
                  2.dp,
                  when {
                    hasSeenCard -> Color(0xFFEF5350).copy(alpha = 0.7f)
                    isCardRevealedOpen -> if (currentAssignment.isSpy) Color(0xFFFF5252) else CategorySpyColor
                    else -> CategorySpyColor.copy(alpha = 0.4f)
                  }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                  contentAlignment = Alignment.Center
                ) {
                  when {
                    hasSeenCard -> {
                      // Already revealed once and permanently locked
                      Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                      ) {
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
                        Spacer(modifier = Modifier.height(10.dp))
                        Icon(
                          imageVector = Icons.Default.Lock,
                          contentDescription = "مقفل نهائياً",
                          modifier = Modifier.size(44.dp),
                          tint = Color(0xFFEF5350)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                          text = "تم قفل بطاقة ${currentPlayer.name}!",
                          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                          color = Color(0xFFFF8A80),
                          textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                          text = if (isLastPlayer) "اكتملت جميع البطاقات ويمكن الآن بدء اللعبة فوراً" else "تم حفظ الدور بنجاح ولا يمكن إعادة فتحه منعاً للغش ✋",
                          style = MaterialTheme.typography.bodySmall,
                          color = Color.White.copy(alpha = 0.85f),
                          textAlign = TextAlign.Center
                        )
                      }
                    }

                    isCardRevealedOpen -> {
                      // Card open for this participant
                      Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                      ) {
                        if (currentAssignment.isSpy) {
                          Text(text = "🕵️‍♂️", fontSize = 46.sp)
                          Spacer(modifier = Modifier.height(6.dp))
                          Text(
                            text = "أنت الجاسوس!",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFFFF5252),
                            textAlign = TextAlign.Center
                          )
                          Spacer(modifier = Modifier.height(6.dp))
                          Text(
                            text = "أنت لا تعرف المكان السري!\nاستمع لأسئلة الآخرين بتركيز دون إثارة الشبهات، وحاول تخمين الموقع!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                          )
                        } else {
                          Text(text = gameState.location?.iconEmoji ?: "🏢", fontSize = 42.sp)
                          Spacer(modifier = Modifier.height(4.dp))
                          Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CategorySpyColor.copy(alpha = 0.25f)
                          ) {
                            Text(
                              text = "المكان السري: ${gameState.location?.nameArabic ?: ""}",
                              modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                              color = Color.White
                            )
                          }
                          Spacer(modifier = Modifier.height(6.dp))
                          Text(
                            text = "دورك: ${currentAssignment.roleName}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFB388FF)
                          )
                          Spacer(modifier = Modifier.height(4.dp))
                          Text(
                            text = gameState.location?.descriptionArabic ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                          )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Single prominent close button inside card
                        Button(
                          onClick = {
                            isCardRevealedOpen = false
                            revealedPlayerIndices = revealedPlayerIndices + playerIndex
                          },
                          modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(48.dp)
                            .testTag("spy_close_card_button"),
                          shape = RoundedCornerShape(14.dp),
                          colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentAssignment.isSpy) Color(0xFFD32F2F) else CategorySpyColor
                          )
                        ) {
                          Text(
                            text = "فهمت دوري، اقفل البطاقة فوراً 🔒",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                          )
                        }
                      }
                    }

                    else -> {
                      // Closed / Unopened prompt
                      Column(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                      ) {
                        Icon(
                          imageVector = Icons.Default.Lock,
                          contentDescription = "مقفل للحماية",
                          modifier = Modifier.size(44.dp),
                          tint = CategorySpyColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                          text = "بطاقة ${currentPlayer.name} السرية",
                          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                          color = MaterialTheme.colorScheme.onSurface,
                          textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Open Card Action Button
                        Button(
                          onClick = { isCardRevealedOpen = true },
                          modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(48.dp)
                            .testTag("spy_open_card_button"),
                          shape = RoundedCornerShape(16.dp),
                          colors = ButtonDefaults.buttonColors(containerColor = CategorySpyColor)
                        ) {
                          Text(
                            text = "افتح بطاقتي يا ${currentPlayer.name} 👁️",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                          )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dedicated Instruction Text Container Below Button
                        Surface(
                          shape = RoundedCornerShape(12.dp),
                          color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
                          modifier = Modifier.fillMaxWidth(0.95f)
                        ) {
                          Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                          ) {
                            Icon(
                              imageVector = Icons.Default.Lock,
                              contentDescription = null,
                              tint = MaterialTheme.colorScheme.error,
                              modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                              text = "انتبه: تفتح لمرة واحدة فقط! بمجرد إغلاقها ستُقفل نهائياً منعاً للغش ✋",
                              style = MaterialTheme.typography.bodySmall.copy(
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                              ),
                              color = MaterialTheme.colorScheme.onSurface,
                              textAlign = TextAlign.Center
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }

              // Advance to Next Player Button (Only shown when current card has been viewed & locked)
              if (hasSeenCard) {
                Button(
                  onClick = { onAdvancePassPhone() },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("spy_next_player_button"),
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = CategorySpyColor
                  )
                ) {
                  Text(
                    text = if (isLastPlayer) "الجميع عرف دوره! ابدأ التحقيق 🕵️‍♂️" else "تم، سلّم الجوال لـ ${nextPlayer?.name ?: "التالي"} ➡️",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                  )
                }
              }

              Spacer(modifier = Modifier.height(16.dp))
            }
          }
        }

        SpyPhase.QUESTIONING -> {
          // --- Phase 2: Questioning Phase & Timer ---
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            item {
              // Timer Display
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  val minutes = gameState.timerSecondsRemaining / 60
                  val seconds = gameState.timerSecondsRemaining % 60
                  val timerColor = if (gameState.timerSecondsRemaining < 45) MaterialTheme.colorScheme.error else CategorySpyColor

                  Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontSize = 54.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = timerColor
                  )

                  Text(
                    text = "وقت التحقيق والأسئلة لكشف الجاسوس",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(14.dp))

                  Button(
                    onClick = onToggleTimer,
                    colors = ButtonDefaults.buttonColors(
                      containerColor = if (gameState.isTimerRunning) MaterialTheme.colorScheme.surfaceVariant else CategorySpyColor
                    ),
                    shape = RoundedCornerShape(14.dp)
                  ) {
                    Icon(
                      imageVector = if (gameState.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                      contentDescription = "تشغيل/إيقاف المؤقت",
                      tint = if (gameState.isTimerRunning) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (gameState.isTimerRunning) "إيقاف مؤقت" else "متابعة المؤقت",
                      fontWeight = FontWeight.Bold,
                      color = if (gameState.isTimerRunning) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                    )
                  }
                }
              }
            }

            // Question Turn Indicator Card
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CategorySpyColor.copy(alpha = 0.3f))
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "دور السؤال الحالي 🎯",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = CategorySpyColor
                  )

                  Spacer(modifier = Modifier.height(10.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ) {
                      Text(
                        text = gameState.currentAskerName,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                      )
                    }

                    Text(
                      text = "  يسأل  👉  ",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    ) {
                      Text(
                        text = gameState.currentAnswererName,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(12.dp))

                  OutlinedButton(
                    onClick = onNextQuestionTurn,
                    shape = RoundedCornerShape(12.dp)
                  ) {
                    Icon(imageVector = Icons.Default.Casino, contentDescription = "دور عشوائي")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "تبديل الدور عشوائياً 🎲")
                  }
                }
              }
            }

            // Sample Questions Guide
            val sampleQuestions = gameState.location?.sampleQuestions ?: emptyList()
            if (sampleQuestions.isNotEmpty()) {
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(20.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                  Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                      text = "أفكار أسئلة ذكية للتحقيق:",
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    sampleQuestions.forEach { q ->
                      Text(
                        text = "• $q",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 3.dp)
                      )
                    }
                  }
                }
              }
            }

            // Accusation Button
            item {
              Button(
                onClick = onProceedToAccusation,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(56.dp)
                  .testTag("spy_accuse_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
              ) {
                Text(
                  text = "انتهى الوقت أو حان وقت الاتهام! 🕵️‍♂️",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
              }
              Spacer(modifier = Modifier.height(36.dp))
            }
          }
        }

        SpyPhase.ACCUSATION -> {
          // --- Phase 3: Voting on the Spy (Individual Voting with Points) ---
          val safeVoterIndex = activeVoterIndex.coerceIn(0, (players.size - 1).coerceAtLeast(0))
          val currentVoter = players.getOrNull(safeVoterIndex) ?: players.firstOrNull()
          val currentVoterSelection = currentVoter?.let { gameState.playerVotes[it.id] }
          val totalVotesCast = gameState.playerVotes.size
          val allVoted = totalVotesCast >= players.size

          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            item {
              Text(
                text = "تصويت كشف الجاسوس 🗳️",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "كل لاعب يصوت على الشخص اللي شاكك فيه لكسب النقاط!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )
            }

            // Voters Selector Row
            item {
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "اختر دور المصوّت:",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (allVoted) Color(0xFF43A047).copy(alpha = 0.2f) else CategorySpyColor.copy(alpha = 0.2f)
                    ) {
                      Text(
                        text = "$totalVotesCast من ${players.size} صوّتوا",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (allVoted) Color(0xFF43A047) else CategorySpyColor
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    items(players.size) { idx ->
                      val p = players[idx]
                      val isSelected = idx == safeVoterIndex
                      val hasVoted = p.id in gameState.playerVotes

                      Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) CategorySpyColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                          width = if (isSelected) 2.dp else 1.dp,
                          color = if (isSelected) CategorySpyColor else if (hasVoted) Color(0xFF43A047).copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                          .clickable { activeVoterIndex = idx }
                          .testTag("spy_voter_tab_${p.id}")
                      ) {
                        Row(
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Box(
                            modifier = Modifier
                              .size(24.dp)
                              .clip(CircleShape)
                              .background(getAvatarColor(p.avatarColorIndex)),
                            contentAlignment = Alignment.Center
                          ) {
                            Text(
                              text = p.name.firstOrNull()?.toString() ?: "👤",
                              color = Color.White,
                              fontSize = 12.sp,
                              fontWeight = FontWeight.Bold
                            )
                          }
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(
                            text = p.name,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                            color = if (isSelected) CategorySpyColor else MaterialTheme.colorScheme.onSurface
                          )
                          if (hasVoted) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "✓", color = Color(0xFF43A047), fontWeight = FontWeight.Black, fontSize = 12.sp)
                          }
                        }
                      }
                    }
                  }
                }
              }
            }

            // Active Voter Prompt Card
            if (currentVoter != null) {
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(18.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  border = androidx.compose.foundation.BorderStroke(1.dp, CategorySpyColor.copy(alpha = 0.3f))
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(getAvatarColor(currentVoter.avatarColorIndex)),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = currentVoter.name.firstOrNull()?.toString() ?: "👤",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                      )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = "دور المصوّت: ${currentVoter.name} 👈",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CategorySpyColor
                      )
                      Text(
                        text = "مين بتشك إنه الجاسوس وما بيعرف المكان؟",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                }
              }

              // Suspects List for current voter
              val suspects = players.filter { it.id != currentVoter.id }

              items(suspects) { suspect ->
                val isCurrentSelection = currentVoterSelection == suspect.id

                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      onCastVote(currentVoter.id, suspect.id)
                      val unvotedIndex = players.indexOfFirst { it.id != currentVoter.id && it.id !in gameState.playerVotes }
                      if (unvotedIndex != -1) {
                        activeVoterIndex = unvotedIndex
                      }
                    }
                    .testTag("accuse_player_${suspect.id}"),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = if (isCurrentSelection) CategorySpyColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                  ),
                  border = androidx.compose.foundation.BorderStroke(
                    width = if (isCurrentSelection) 2.dp else 1.dp,
                    color = if (isCurrentSelection) CategorySpyColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                  )
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(38.dp)
                          .clip(CircleShape)
                          .background(getAvatarColor(suspect.avatarColorIndex)),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = suspect.name.firstOrNull()?.toString() ?: "👤",
                          color = Color.White,
                          fontWeight = FontWeight.Bold,
                          fontSize = 16.sp
                        )
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Text(
                        text = suspect.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                    }

                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = if (isCurrentSelection) CategorySpyColor else Color(0xFFFF5252).copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = if (isCurrentSelection) "اختيارك ✓" else "اتهمه 👈",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isCurrentSelection) Color.White else Color(0xFFFF5252)
                      )
                    }
                  }
                }
              }
            }

            // Finalize Voting Button
            item {
              Spacer(modifier = Modifier.height(4.dp))
              Button(
                onClick = onFinalizeVoting,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("finalize_spy_voting_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CategorySpyColor)
              ) {
                Text(
                  text = "فرز الأصوات وكشف الجاسوس 🕵️‍♂️",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
              }
            }

            item {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "أو إذا كان الجاسوس يريد كشف نفسه وتخمين المكان فوراً: 📍",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // Spy Guesses Location Chips
            items(PartyQuestionsData.spyLocations) { loc ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onSpyGuessLocation(loc.id) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = loc.iconEmoji, fontSize = 22.sp)
                  Spacer(modifier = Modifier.width(12.dp))
                  Text(
                    text = "الجاسوس يخمن: ${loc.nameArabic}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }

            item {
              Spacer(modifier = Modifier.height(24.dp))
            }
          }
        }

        SpyPhase.REVEAL -> {
          // --- Phase 4: Final Outcome & Result ---
          val accusedPlayer = players.firstOrNull { it.id == gameState.accusedPlayerId }
          val actualSpyPlayer = players.firstOrNull { it.id == gameState.spyPlayerId }
          val actualLocation = gameState.location

          val isSpyCaught = gameState.accusedPlayerId == gameState.spyPlayerId
          val isSpyLocationGuessCorrect = gameState.spyGuessedLocationId == actualLocation?.id

          val spyWon = (!isSpyCaught && gameState.accusedPlayerId != null) || isSpyLocationGuessCorrect

          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (spyWon) Color(0xFF2C1B1F) else Color(0xFF1B2C24)
                ),
                border = androidx.compose.foundation.BorderStroke(
                  2.dp,
                  if (spyWon) Color(0xFFFF5252) else Color(0xFF81C784)
                )
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = if (spyWon) "🕵️‍♂️ فاز الجاسوس!" else "🏆 فازت المجموعة!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (spyWon) Color(0xFFFF5252) else Color(0xFF81C784)
                  )

                  Spacer(modifier = Modifier.height(6.dp))

                  Text(
                    text = when {
                      isSpyLocationGuessCorrect -> "الجاسوس (${actualSpyPlayer?.name}) خمن المكان السري بنجاح وخدع الجميع!"
                      isSpyCaught -> "تم كشف الجاسوس بنجاح وهو: ${actualSpyPlayer?.name}!"
                      else -> "الجاسوس كان: ${actualSpyPlayer?.name} وليس: ${accusedPlayer?.name}! نجا الجاسوس وفاز!"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                  )

                  Spacer(modifier = Modifier.height(10.dp))

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.15f)
                  ) {
                    Text(
                      text = "المكان السري الحقيقي: ${actualLocation?.nameArabic ?: ""}",
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = Color.White
                    )
                  }
                }
              }
            }

            // Voting Predictions Breakdown Card
            item {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("spy_voting_results_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "نتائج وتوقعات تصويت اللاعبين 🗳️",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = Color(0xFF81C784).copy(alpha = 0.2f)
                    ) {
                      Text(
                        text = "+1 لكل توقع صحيح",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF43A047)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  players.forEach { voter ->
                    if (voter.id == actualSpyPlayer?.id) {
                      val escaped = !isSpyCaught && gameState.accusedPlayerId != null
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Box(
                            modifier = Modifier
                              .size(28.dp)
                              .clip(CircleShape)
                              .background(getAvatarColor(voter.avatarColorIndex)),
                            contentAlignment = Alignment.Center
                          ) {
                            Text(
                              text = voter.name.firstOrNull()?.toString() ?: "👤",
                              color = Color.White,
                              fontWeight = FontWeight.Bold,
                              fontSize = 12.sp
                            )
                          }
                          Spacer(modifier = Modifier.width(8.dp))
                          Text(
                            text = "${voter.name} (الجاسوس 🕵️‍♂️)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                          )
                        }

                        Surface(
                          shape = RoundedCornerShape(8.dp),
                          color = if (escaped || isSpyLocationGuessCorrect) Color(0xFF81C784).copy(alpha = 0.2f) else Color(0xFFFF5252).copy(alpha = 0.15f)
                        ) {
                          Text(
                            text = when {
                              isSpyLocationGuessCorrect -> "خمن المكان! (+2) 📍"
                              escaped -> "نجا بنجاح! (+2) 🕶️"
                              else -> "تم كشفه 🎯"
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (escaped || isSpyLocationGuessCorrect) Color(0xFF43A047) else Color(0xFFFF5252)
                          )
                        }
                      }
                    } else {
                      val votedSuspectId = gameState.playerVotes[voter.id]
                      val votedSuspect = players.firstOrNull { it.id == votedSuspectId }
                      val isGuessCorrect = votedSuspectId == actualSpyPlayer?.id

                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Box(
                            modifier = Modifier
                              .size(28.dp)
                              .clip(CircleShape)
                              .background(getAvatarColor(voter.avatarColorIndex)),
                            contentAlignment = Alignment.Center
                          ) {
                            Text(
                              text = voter.name.firstOrNull()?.toString() ?: "👤",
                              color = Color.White,
                              fontWeight = FontWeight.Bold,
                              fontSize = 12.sp
                            )
                          }
                          Spacer(modifier = Modifier.width(8.dp))
                          Column {
                            Text(
                              text = voter.name,
                              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                              color = MaterialTheme.colorScheme.onSurface
                            )
                            if (votedSuspect != null) {
                              Text(
                                text = "صوّت لـ: ${votedSuspect.name}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                              )
                            }
                          }
                        }

                        if (votedSuspectId != null) {
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isGuessCorrect) Color(0xFF81C784).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                          ) {
                            Text(
                              text = if (isGuessCorrect) "توقع صحيح! (+1) 🎯" else "توقع خاطئ (0) ❌",
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                              color = if (isGuessCorrect) Color(0xFF43A047) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                          }
                        } else {
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                          ) {
                            Text(
                              text = "لم يصوّت",
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                              style = MaterialTheme.typography.labelSmall,
                              color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }
            }

            // Scoring Breakdown Card
            item {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("spy_scoring_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "جدول نقاط الجولة 📊",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                      Text(
                        text = if (maxRounds > 0) "جولة $currentRound من $maxRounds" else "جولة $currentRound (مفتوح)",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  Text(
                    text = "قواعد النقاط: توقع صحيح لكشف الجاسوس = +1 نقطة • إفلات الجاسوس أو تخمين المكان = +2 نقطة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(12.dp))

                  players.sortedByDescending { it.score }.forEach { player ->
                    val roundPts = gameState.awardedPoints[player.id] ?: 0
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                          modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(getAvatarColor(player.avatarColorIndex)),
                          contentAlignment = Alignment.Center
                        ) {
                          Text(
                            text = player.name.firstOrNull()?.toString() ?: "👤",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                          )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                          text = player.name + if (player.id == gameState.spyPlayerId) " (الجاسوس)" else "",
                          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                          color = MaterialTheme.colorScheme.onSurface
                        )
                      }

                      Row(verticalAlignment = Alignment.CenterVertically) {
                        if (roundPts > 0) {
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF81C784).copy(alpha = 0.2f)
                          ) {
                            Text(
                              text = "+$roundPts",
                              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                              color = Color(0xFF81C784)
                            )
                          }
                          Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                          text = "${player.score} نقطة",
                          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                          color = MaterialTheme.colorScheme.primary
                        )
                      }
                    }
                  }
                }
              }
            }

            if (isGameFinished) {
              item {
                val winner = players.maxByOrNull { it.score }
                Card(
                  modifier = Modifier.fillMaxWidth().testTag("spy_game_over_card"),
                  shape = RoundedCornerShape(20.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)),
                  border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
                ) {
                  Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(text = "👑 انتهت كل الجولات! 🏆", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "الفائز بالمركز الأول: ${winner?.name ?: "الجميع"} برصيد ${winner?.score ?: 0} نقطة!",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      textAlign = TextAlign.Center
                    )
                  }
                }
              }
            }

            item {
              OutlinedButton(
                onClick = onTriggerPenalty,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(52.dp),
                shape = RoundedCornerShape(16.dp)
              ) {
                Text(text = "عقاب مضحك للخاسر 🎭", fontWeight = FontWeight.Bold)
              }
            }

            item {
              if (isGameFinished) {
                Button(
                  onClick = onRestartGame,
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("spy_play_again_button"),
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = CategorySpyColor)
                ) {
                  Text(
                    text = "بدء سهرة ولعبة جديدة 🔄",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                  )
                }
              } else {
                Button(
                  onClick = onNextRound,
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("spy_next_round_button"),
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = CategorySpyColor)
                ) {
                  Text(
                    text = if (maxRounds > 0) "الانتقال للجولة التالية (${currentRound + 1} من $maxRounds) ➡️" else "الانتقال للجولة التالية (${currentRound + 1}) ➡️",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                  )
                }
              }
            }

            item {
              OutlinedButton(
                onClick = onNavigateHome,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
              ) {
                Text(text = "العودة للقائمة الرئيسية 🏠")
              }
              Spacer(modifier = Modifier.height(36.dp))
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
