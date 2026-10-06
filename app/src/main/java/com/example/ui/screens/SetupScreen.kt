package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.ui.components.getAvatarColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
  players: List<Player>,
  isDarkMode: Boolean,
  maxRounds: Int = 0,
  onSelectMaxRounds: (Int) -> Unit = {},
  onToggleDarkMode: () -> Unit,
  onAddPlayer: (String) -> Unit,
  onAddPlayerAt: (name: String, insertIndex: Int) -> Unit = { name, _ -> onAddPlayer(name) },
  onMovePlayerUp: (String) -> Unit = {},
  onMovePlayerDown: (String) -> Unit = {},
  onRemovePlayer: (String) -> Unit,
  onStartGame: () -> Unit,
  onNavigateAdmin: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var nameInput by remember { mutableStateOf("") }
  var insertIndex by remember(players.size) { mutableStateOf(players.size) }
  var showPositionDropdown by remember { mutableStateOf(false) }
  val suggestedNames = listOf("أحمد", "سارة", "خالد", "نورة", "محمد", "ريم", "فهد", "دانة", "ياسر", "لمى")

  val doAddPlayer = {
    if (nameInput.isNotBlank()) {
      onAddPlayerAt(nameInput, insertIndex)
      nameInput = ""
      insertIndex = players.size + 1
    }
  }

  val canPlayCardGames = players.size >= 2
  val canPlayHiddenRoleGames = players.size >= 3

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding()
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Top Bar with Host Badge & Admin & Dark Mode toggle
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "👑", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "وضع المضيف والأدمن",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
              modifier = Modifier
                .clickable { onNavigateAdmin() }
                .testTag("setup_admin_btn")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "⚙️", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "لوحة الأدمن",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onTertiaryContainer
                )
              }
            }

            IconButton(
              onClick = onToggleDarkMode,
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .testTag("theme_toggle_setup_button")
            ) {
              Icon(
                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "تبديل المظهر",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Hero Header
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    listOf(
                      MaterialTheme.colorScheme.primary,
                      MaterialTheme.colorScheme.tertiary
                    )
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🎉", fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "تجهيز جلسة الأصحاب",
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "أضف أسماء جميع المشاركين في الجلسة لتفعيل الأسئلة الجماعية وتوزيع الأدوار السرية تلقائياً!",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }
      }

      // Round Management Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("setup_round_management_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎯", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "نظام وعدد الجولات",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
              ) {
                Text(
                  text = when (maxRounds) {
                    3 -> "3 جولات"
                    5 -> "5 جولات"
                    10 -> "10 جولات"
                    else -> "مفتوح / بدون حد"
                  },
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "اختر عدد الجولات قبل انتهاء اللعبة، أو اختر 'مفتوح' لجلسة سهرة مستمرة بدون قيود:",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              val roundOptions = listOf(
                0 to "مفتوح ♾️",
                3 to "3 جولات",
                5 to "5 جولات",
                10 to "10 جولات"
              )
              roundOptions.forEach { (count, label) ->
                val isSelected = maxRounds == count
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                  border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                  ),
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectMaxRounds(count) }
                    .testTag("round_option_$count")
                ) {
                  Text(
                    text = label,
                    modifier = Modifier.padding(vertical = 10.dp),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                  )
                }
              }
            }
          }
        }
      }

      // Input Section
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Text(
              text = "إضافة لاعب جديد",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                modifier = Modifier
                  .weight(1f)
                  .testTag("player_name_input"),
                placeholder = { Text("اسم اللاعب (مثلاً: رامي، سارة)...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = MaterialTheme.colorScheme.primary,
                  unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                  onDone = {
                    if (nameInput.isNotBlank()) {
                      onAddPlayer(nameInput)
                      nameInput = ""
                    }
                  }
                )
              )

              Spacer(modifier = Modifier.width(10.dp))

              Button(
                onClick = { doAddPlayer() },
                modifier = Modifier
                  .height(54.dp)
                  .testTag("add_player_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary
                )
              ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "إضافة", fontWeight = FontWeight.Bold)
              }
            }

            // Position Picker Selector
            if (players.isNotEmpty()) {
              Spacer(modifier = Modifier.height(10.dp))
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { showPositionDropdown = true }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.SwapVert,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val posLabel = when {
                      insertIndex <= 0 -> "في أول الجلسة (قبل ${players.first().name})"
                      insertIndex >= players.size -> "في آخر الجلسة (بعد ${players.last().name})"
                      else -> "بعد ${players[insertIndex - 1].name} وقبل ${players[insertIndex].name}"
                    }
                    Column {
                      Text(
                        text = "ترتيب الإضافة عند وصول لاعب جديد:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = posLabel,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                      )
                    }
                  }

                  Text(
                    text = "تغيير ▼",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                DropdownMenu(
                  expanded = showPositionDropdown,
                  onDismissRequest = { showPositionDropdown = false }
                ) {
                  DropdownMenuItem(
                    text = { Text("في أول الجلسة (قبل ${players.first().name})") },
                    onClick = {
                      insertIndex = 0
                      showPositionDropdown = false
                    }
                  )
                  for (i in 1 until players.size) {
                    DropdownMenuItem(
                      text = { Text("بين ${players[i - 1].name} و ${players[i].name}") },
                      onClick = {
                        insertIndex = i
                        showPositionDropdown = false
                      }
                    )
                  }
                  DropdownMenuItem(
                    text = { Text("في آخر الجلسة (بعد ${players.last().name})") },
                    onClick = {
                      insertIndex = players.size
                      showPositionDropdown = false
                    }
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Add Suggested Names
            Text(
              text = "اقتراحات سريعة:",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              suggestedNames.forEach { suggestedName ->
                val alreadyAdded = players.any { it.name.equals(suggestedName, ignoreCase = true) }
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = if (alreadyAdded) {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                  } else {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                  },
                  border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (alreadyAdded) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                  ),
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = !alreadyAdded) {
                      onAddPlayer(suggestedName)
                    }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = if (alreadyAdded) "✓ $suggestedName" else "+ $suggestedName",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                      color = if (alreadyAdded) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                      } else {
                        MaterialTheme.colorScheme.primary
                      }
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Players List Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "المشاركون في الجلسة",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary
            ) {
              Text(
                text = "${players.size}",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
              )
            }
          }

          // Requirement status indicator
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = when {
              canPlayHiddenRoleGames -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
              canPlayCardGames -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
              else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
            }
          ) {
            Text(
              text = when {
                canPlayHiddenRoleGames -> "✨ جاهز لجميع الألعاب"
                canPlayCardGames -> "🃏 جاهز للكروت (أضف 3+ للأدوار السرية)"
                else -> "⚠️ أضف لاعبين على الأقل"
              },
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = when {
                canPlayHiddenRoleGames -> MaterialTheme.colorScheme.primary
                canPlayCardGames -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.error
              }
            )
          }
        }
      }

      // Players Items
      if (players.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "👥", fontSize = 36.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "لم تقم بإضافة أي لاعبين بعد",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "استخدم حقل الإدخال أعلاه أو اضغط على الاقتراحات السريعة",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
              )
            }
          }
        }
      } else {
        itemsIndexed(players, key = { _, it -> it.id }) { idx, player ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("player_item_${player.id}"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(getAvatarColor(player.avatarColorIndex)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${idx + 1}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                  )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                  Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  if (player.penaltiesCount > 0) {
                    Text(
                      text = "أحكام سابقة: ${player.penaltiesCount} 🎭",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = { onMovePlayerUp(player.id) },
                  enabled = idx > 0,
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "تحريك للأعلى",
                    tint = if (idx > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    modifier = Modifier.size(18.dp)
                  )
                }

                IconButton(
                  onClick = { onMovePlayerDown(player.id) },
                  enabled = idx < players.size - 1,
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "تحريك للأسفل",
                    tint = if (idx < players.size - 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    modifier = Modifier.size(18.dp)
                  )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                  onClick = { onRemovePlayer(player.id) },
                  modifier = Modifier
                    .size(36.dp)
                    .testTag("remove_player_${player.id}")
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "حذف اللاعب",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }
    }

    // Floating Bottom Start Button
    Surface(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth(),
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
      tonalElevation = 8.dp,
      shadowElevation = 8.dp
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp)
      ) {
        Button(
          onClick = onStartGame,
          enabled = players.isNotEmpty(),
          modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .testTag("start_game_button"),
          shape = RoundedCornerShape(18.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = if (players.size < 2) "أضف لاعبين للبدء" else "ابدأ الجلسة واللعب 🎉",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "بدء اللعب"
            )
          }
        }
      }
    }
  }
}
