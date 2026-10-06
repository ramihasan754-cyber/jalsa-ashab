package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LoopGameState
import com.example.model.LoopPhase
import com.example.model.PenaltyItem
import com.example.model.Player
import com.example.ui.components.PenaltyDialog
import com.example.ui.components.getAvatarColor
import com.example.ui.theme.CategoryLoopColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OutOfTheLoopScreen(
  gameState: LoopGameState,
  players: List<Player>,
  currentRound: Int = 1,
  maxRounds: Int = 0,
  isGameFinished: Boolean = false,
  activePenalty: PenaltyItem? = null,
  penaltyTargetPlayer: Player? = null,
  onAdvancePassPhone: () -> Unit,
  onToggleTimer: () -> Unit,
  onAdvanceTurnOrder: () -> Unit = {},
  onSwitchToFreeAsking: () -> Unit = {},
  onProceedToVoting: () -> Unit,
  onCastVote: (voterId: String, suspectId: String) -> Unit = { _, _ -> },
  onFinalizeVoting: () -> Unit = {},
  onSelectSuspect: (String) -> Unit,
  onGuessWord: (String) -> Unit,
  onNextRound: () -> Unit = {},
  onRestartGame: () -> Unit,
  onNavigateHome: () -> Unit,
  onTriggerPenalty: () -> Unit,
  onDismissPenalty: () -> Unit = {},
  onApplyPenalty: () -> Unit = {},
  onRerollPenalty: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var revealedPlayerIndices by remember(gameState.secretWord) { mutableStateOf(emptySet<Int>()) }
  var isCardRevealedOpen by remember(gameState.currentPassingPlayerIndex) { mutableStateOf(false) }
  var guessInputText by remember { mutableStateOf("") }
  var activeVoterIndex by remember(gameState.secretWord) { mutableIntStateOf(0) }

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
            .testTag("loop_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "العودة للرئيسية",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }

        Surface(
          shape = RoundedCornerShape(20.dp),
          color = CategoryLoopColor.copy(alpha = 0.2f),
          border = androidx.compose.foundation.BorderStroke(1.dp, CategoryLoopColor.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🤫", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (maxRounds > 0) "برا السالفة • جولة $currentRound من $maxRounds" else "برا السالفة • جولة $currentRound (مفتوح)",
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
            .testTag("loop_restart_button")
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
        LoopPhase.PASS_PHONE -> {
          // --- Phase 1: Pass Phone Securely & Reveal Secret ---
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
              // Passing Instructions
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
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                  border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
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
                    text = if (hasSeenCard && isLastPlayer) "🎉" else targetPlayer.name.firstOrNull()?.toString() ?: "👤",
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
                    hasSeenCard -> if (isLastPlayer) "كل اللاعبين استلموا الجوال وحفظوا أدوارهم، حان وقت الأسئلة!" else "انتهى دور ${currentPlayer.name}. سلّم الجوال لـ ${nextPlayer?.name ?: "التالي"} لرؤية دوره."
                    isCardRevealedOpen -> "احرص على سرية الشاشة! أنت فقط من يرى هذه البطاقة 🤫"
                    else -> "يا ${currentPlayer.name}، تأكد أن لا أحد ينظر للشاشة ثم افتح بطاقتك"
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
                  .testTag("loop_secret_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                  containerColor = when {
                    hasSeenCard -> Color(0xFF26191D)
                    isCardRevealedOpen -> if (currentAssignment.isOutOfTheLoop) Color(0xFF2C1B1F) else Color(0xFF1B2C24)
                    else -> MaterialTheme.colorScheme.surface
                  }
                ),
                border = androidx.compose.foundation.BorderStroke(
                  2.dp,
                  when {
                    hasSeenCard -> Color(0xFFEF5350).copy(alpha = 0.7f)
                    isCardRevealedOpen -> if (currentAssignment.isOutOfTheLoop) CategoryLoopColor else Color(0xFF81C784)
                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
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
                      // Secret is open for this participant to view
                      Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                      ) {
                        if (currentAssignment.isOutOfTheLoop) {
                          Text(text = "🤫", fontSize = 46.sp)
                          Spacer(modifier = Modifier.height(6.dp))
                          Text(
                            text = "أنت برا السالفة!",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = CategoryLoopColor,
                            textAlign = TextAlign.Center
                          )
                          Spacer(modifier = Modifier.height(6.dp))
                          Text(
                            text = "أنت لا تعرف الكلمة السرية!\nاستمع لتلميحات أصحابك بتركيز وحاول التظاهر بأنك تعرفها لتفادي الشبهات.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                          )
                        } else {
                          Text(text = "🔑", fontSize = 42.sp)
                          Spacer(modifier = Modifier.height(4.dp))
                          Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.15f)
                          ) {
                            Text(
                              text = "الموضوع: ${gameState.categoryName}",
                              modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                              color = Color.White
                            )
                          }
                          Spacer(modifier = Modifier.height(6.dp))
                          Text(
                            text = gameState.secretWord,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF81C784),
                            textAlign = TextAlign.Center
                          )
                          Spacer(modifier = Modifier.height(4.dp))
                          Text(
                            text = "أعط تلميحاً ذكياً دون حرق الكلمة لبرا السالفة!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                          )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                          onClick = {
                            isCardRevealedOpen = false
                            revealedPlayerIndices = revealedPlayerIndices + playerIndex
                          },
                          modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(48.dp)
                            .testTag("loop_close_card_button"),
                          shape = RoundedCornerShape(14.dp),
                          colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentAssignment.isOutOfTheLoop) CategoryLoopColor else Color(0xFF2E7D32)
                          )
                        ) {
                          Text(
                            text = "فهمت وحفظت، اقفل البطاقة فوراً 🔒",
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
                          tint = MaterialTheme.colorScheme.primary
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
                            .testTag("loop_open_card_button"),
                          shape = RoundedCornerShape(16.dp),
                          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                          Text(
                            text = "افتح بطاقتي يا ${currentPlayer.name} 👁️",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
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
                              text = "انتبه: تفتح لمرة واحدة فقط! بمجرد إغلاقها ستُقفل نهائياً ولن تتمكن من فتحها مرة أخرى منعاً للغش ✋",
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

              // Advance Button (Only shown when current card is seen and locked)
              if (hasSeenCard) {
                Button(
                  onClick = { onAdvancePassPhone() },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("loop_next_player_button"),
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                  )
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                  ) {
                    Text(
                      text = if (isLastPlayer) "الجميع رأى دوره! ابدأ الجولة 🚀" else "تم، سلّم الجوال لـ ${nextPlayer?.name ?: "التالي"} ➡️",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(16.dp))
            }
          }
        }

        LoopPhase.DISCUSSION -> {
          // --- Phase 2: Discussion & Clues with Timer ---
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
                  val timerColor = if (gameState.timerSecondsRemaining < 30) MaterialTheme.colorScheme.error else CategoryLoopColor

                  Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = timerColor
                  )

                  Text(
                    text = "وقت النقاش وإعطاء التلميحات",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(14.dp))

                  Button(
                    onClick = onToggleTimer,
                    colors = ButtonDefaults.buttonColors(
                      containerColor = if (gameState.isTimerRunning) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(14.dp)
                  ) {
                    Icon(
                      imageVector = if (gameState.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                      contentDescription = "تشغيل/إيقاف المؤقت",
                      tint = if (gameState.isTimerRunning) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (gameState.isTimerRunning) "إيقاف مؤقت" else "متابعة الوقت",
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }

            item {
              // General Subject Topic Banner (Known to everyone!)
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                  containerColor = CategoryLoopColor.copy(alpha = 0.15f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CategoryLoopColor.copy(alpha = 0.3f))
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = "💡", fontSize = 32.sp)
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(
                      text = "تصنيف السالفة العام:",
                      style = MaterialTheme.typography.labelMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = gameState.categoryName,
                      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }
                }
              }
            }

            item {
              // Sequential turn (Round 1) vs Free asking (Round 2) Card
              if (gameState.turnPhase == 1) {
                val speakingPlayer = players.getOrNull(gameState.currentTurnOrderIndex) ?: players.firstOrNull()
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("loop_turn_phase_card"),
                  shape = RoundedCornerShape(20.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                  border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ) {
                      Text(
                        text = "المرحلة 1: تلميحات بالترتيب (${gameState.currentTurnOrderIndex + 1} من ${players.size})",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                      )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.Center
                    ) {
                      if (speakingPlayer != null) {
                        Box(
                          modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(getAvatarColor(speakingPlayer.avatarColorIndex)),
                          contentAlignment = Alignment.Center
                        ) {
                          Text(
                            text = speakingPlayer.name.firstOrNull()?.toString() ?: "👤",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                          )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                          text = "دور اللاعب: ${speakingPlayer.name} 🗣️",
                          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                          color = MaterialTheme.colorScheme.onSurface
                        )
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                      text = "أعط تلميحك الآن بكلمة أو كلمتين ذكيتين، ثم اضغط التالي لتسليم الدور لمن بعدك!",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      OutlinedButton(
                        onClick = onSwitchToFreeAsking,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                      ) {
                        Text(text = "تخطي للنقاش الحر ⚡", style = MaterialTheme.typography.bodySmall)
                      }

                      Button(
                        onClick = onAdvanceTurnOrder,
                        modifier = Modifier
                          .weight(1f)
                          .testTag("loop_advance_turn_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                      ) {
                        Text(
                          text = if (gameState.currentTurnOrderIndex + 1 >= players.size) "بدء النقاش الحر ➡️" else "التالي ➡️",
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                  }
                }
              } else {
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("loop_free_asking_card"),
                  shape = RoundedCornerShape(20.dp),
                  colors = CardDefaults.cardColors(containerColor = CategoryLoopColor.copy(alpha = 0.15f)),
                  border = androidx.compose.foundation.BorderStroke(1.5.dp, CategoryLoopColor.copy(alpha = 0.5f))
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = CategoryLoopColor.copy(alpha = 0.3f)
                    ) {
                      Text(
                        text = "المرحلة 2: أسئلة ونقاش حر مفتوح 💬",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                      text = "الآن انتهت التلميحات بالترتيب! يحق لأي لاعب توجيه أسئلة لأي شخص لاكتشاف التناقضات.",
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurface,
                      textAlign = TextAlign.Center
                    )
                  }
                }
              }
            }

            item {
              // Instructions Card
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
              ) {
                Column(modifier = Modifier.padding(18.dp)) {
                  Text(
                    text = "كيف تلعبون هذه الجولة؟",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "1. يبدأ أحد اللاعبين بإعطاء تلميح من كلمة أو كلمتين يثبت أنه يعرف الكلمة دون حرقها.\n" +
                      "2. 'برا السالفة' يحاول إعطاء تلميح عام أو تقليد تلميحات الآخرين!\n" +
                      "3. ناقشوا تلميحات كل شخص وشكوككم حتى انتهاء الوقت.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            item {
              Button(
                onClick = onProceedToVoting,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(56.dp)
                  .testTag("loop_vote_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CategoryLoopColor)
              ) {
                Text(
                  text = "انتهى الوقت! صوتوا على المشتبه به 🗳️",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
              }
              Spacer(modifier = Modifier.height(36.dp))
            }
          }
        }

        LoopPhase.VOTING -> {
          // --- Phase 3: Voting on the Suspect (Individual Voting with Points) ---
          val safeVoterIndex = activeVoterIndex.coerceIn(0, (players.size - 1).coerceAtLeast(0))
          val currentVoter = players.getOrNull(safeVoterIndex) ?: players.firstOrNull()
          val currentVoterSelection = currentVoter?.let { gameState.playerVotes[it.id] }
          val totalVotesCast = gameState.playerVotes.size
          val allVoted = totalVotesCast >= players.size

          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Voting Header
            Text(
              text = "تصويت كشف برا السالفة 🗳️",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "كل لاعب يصوت على الشخص اللي شاكك فيه ليحصل على نقاط!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Voters Selector Row
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
                    color = if (allVoted) Color(0xFF43A047).copy(alpha = 0.2f) else CategoryLoopColor.copy(alpha = 0.2f)
                  ) {
                    Text(
                      text = "$totalVotesCast من ${players.size} صوّتوا",
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = if (allVoted) Color(0xFF43A047) else CategoryLoopColor
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
                      color = if (isSelected) CategoryLoopColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                      border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) CategoryLoopColor else if (hasVoted) Color(0xFF43A047).copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                      ),
                      modifier = Modifier
                        .clickable { activeVoterIndex = idx }
                        .testTag("voter_tab_${p.id}")
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
                          color = if (isSelected) CategoryLoopColor else MaterialTheme.colorScheme.onSurface
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

            Spacer(modifier = Modifier.height(14.dp))

            // Active Voter Prompt
            if (currentVoter != null) {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CategoryLoopColor.copy(alpha = 0.3f))
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
                      text = "دور: ${currentVoter.name} 👈",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = CategoryLoopColor
                    )
                    Text(
                      text = "مين بتشك إنه برا السالفة ومضيع الموضوع؟",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Suspects List (all players except current voter)
              val suspects = players.filter { it.id != currentVoter.id }

              LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                items(suspects.size) { sIdx ->
                  val suspect = suspects[sIdx]
                  val isCurrentSelection = currentVoterSelection == suspect.id

                  Card(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable {
                        onCastVote(currentVoter.id, suspect.id)
                        // Auto-advance to next player who hasn't voted
                        val unvotedIndex = players.indexOfFirst { it.id != currentVoter.id && it.id !in gameState.playerVotes }
                        if (unvotedIndex != -1) {
                          activeVoterIndex = unvotedIndex
                        }
                      }
                      .testTag("loop_suspect_${suspect.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                      containerColor = if (isCurrentSelection) CategoryLoopColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                      width = if (isCurrentSelection) 2.dp else 1.dp,
                      color = if (isCurrentSelection) CategoryLoopColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
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
                        color = if (isCurrentSelection) CategoryLoopColor else CategoryLoopColor.copy(alpha = 0.15f)
                      ) {
                        Text(
                          text = if (isCurrentSelection) "اختيارك ✓" else "صوت له 👈",
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                          color = if (isCurrentSelection) Color.White else CategoryLoopColor
                        )
                      }
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = onFinalizeVoting,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("finalize_loop_voting_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CategoryLoopColor)
              ) {
                Text(
                  text = "فرز الأصوات وكشف برا السالفة 🎯",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
              }
            }
          }
        }

        LoopPhase.REVEAL -> {
          // --- Phase 4: Final Reveal & Word Guess ---
          val suspectedPlayer = players.firstOrNull { it.id == gameState.suspectedPlayerId }
          val actualOutPlayer = players.firstOrNull { it.id == gameState.outPlayerId }
          val isSuspectCorrect = gameState.suspectedPlayerId == gameState.outPlayerId

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
                  containerColor = if (isSuspectCorrect) Color(0xFF1B2C24) else Color(0xFF2C1B1F)
                ),
                border = androidx.compose.foundation.BorderStroke(
                  2.dp,
                  if (isSuspectCorrect) Color(0xFF81C784) else CategoryLoopColor
                )
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = if (isSuspectCorrect) "🎯 صادوه!" else "😈 فلت منها!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSuspectCorrect) Color(0xFF81C784) else CategoryLoopColor
                  )

                  Spacer(modifier = Modifier.height(6.dp))

                  Text(
                    text = if (isSuspectCorrect) {
                      "المجموعة كشفت برا السالفة بنجاح وهو: ${actualOutPlayer?.name}"
                    } else {
                      "برا السالفة الحقيقي كان: ${actualOutPlayer?.name}\nوليس: ${suspectedPlayer?.name}!"
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
                      text = "الكلمة السرية كانت: ${gameState.secretWord}",
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
                  .testTag("loop_voting_results_card"),
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
                    if (voter.id == actualOutPlayer?.id) {
                      val escaped = suspectedPlayer?.id != actualOutPlayer?.id
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
                            text = "${voter.name} (برا السالفة)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                          )
                        }

                        Surface(
                          shape = RoundedCornerShape(8.dp),
                          color = if (escaped) Color(0xFF81C784).copy(alpha = 0.2f) else Color(0xFFFF5252).copy(alpha = 0.15f)
                        ) {
                          Text(
                            text = if (escaped) "أفلت بنجاح! (+2) 😈" else "تم كشفه 🎯",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (escaped) Color(0xFF43A047) else Color(0xFFFF5252)
                          )
                        }
                      }
                    } else {
                      val votedSuspectId = gameState.playerVotes[voter.id]
                      val votedSuspect = players.firstOrNull { it.id == votedSuspectId }
                      val isGuessCorrect = votedSuspectId == actualOutPlayer?.id

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

            // Word Guessing by Out of the Loop Player
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                ) {
                  Text(
                    text = "فرصة سرقة الفوز 🏆",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "إذا استطاع ${actualOutPlayer?.name} تخمين الكلمة السرية، يسرق الفوز بالكامل!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(12.dp))

                  if (gameState.isWordGuessedCorrectly != null) {
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = if (gameState.isWordGuessedCorrectly == true) Color(0xFF1B2C24) else Color(0xFF2C1B1F),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Text(
                        text = if (gameState.isWordGuessedCorrectly == true) {
                          "🎉 رائع! خمن الكلمة صح وسرق الفوز!"
                        } else {
                          "❌ تخمين خاطئ! بقيت النتيجة لصالح المجموعة."
                        },
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (gameState.isWordGuessedCorrectly == true) Color(0xFF81C784) else CategoryLoopColor,
                        textAlign = TextAlign.Center
                      )
                    }
                  } else {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      OutlinedTextField(
                        value = guessInputText,
                        onValueChange = { guessInputText = it },
                        modifier = Modifier
                          .weight(1f)
                          .testTag("loop_guess_input"),
                        placeholder = { Text("تخمين الكلمة السرية...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Button(
                        onClick = {
                          if (guessInputText.isNotBlank()) {
                            onGuessWord(guessInputText)
                          }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CategoryLoopColor)
                      ) {
                        Text(text = "تخمين", fontWeight = FontWeight.Bold, color = Color.White)
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
                  .testTag("loop_scoring_card"),
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
                    text = "قواعد النقاط: كشف برا السالفة = +1 نقطة • برا السالفة يخمن الكلمة = +2 نقطة",
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
                          text = player.name + if (player.id == gameState.outPlayerId) " (برا السالفة)" else "",
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
                  modifier = Modifier.fillMaxWidth().testTag("loop_game_over_card"),
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

            // Action Buttons
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
                    .testTag("loop_play_again_button"),
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                  Text(
                    text = "بدء سهرة ولعبة جديدة 🔄",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                  )
                }
              } else {
                Button(
                  onClick = onNextRound,
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("loop_next_round_button"),
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                  Text(
                    text = if (maxRounds > 0) "الانتقال للجولة التالية (${currentRound + 1} من $maxRounds) ➡️" else "الانتقال للجولة التالية (${currentRound + 1}) ➡️",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
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
