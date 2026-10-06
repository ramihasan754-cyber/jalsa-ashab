package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Player
import com.example.ui.theme.CoralPink
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SunsetAmber

val avatarPalette = listOf(
  NeonPurple,
  CoralPink,
  ElectricCyan,
  SunsetAmber,
  EmeraldGreen,
  Color(0xFFE040FB)
)

fun getAvatarColor(index: Int): Color {
  if (avatarPalette.isEmpty()) return NeonPurple
  val safeIndex = if (index < 0) -index else index
  return avatarPalette[safeIndex % avatarPalette.size]
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayerManagerModal(
  players: List<Player>,
  onAddPlayer: (String) -> Unit,
  onAddPlayerAt: (name: String, insertIndex: Int) -> Unit = { name, _ -> onAddPlayer(name) },
  onMovePlayerUp: (String) -> Unit = {},
  onMovePlayerDown: (String) -> Unit = {},
  onRemovePlayer: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var playerNameInput by remember { mutableStateOf("") }
  var insertIndex by remember(players.size) { mutableStateOf(players.size) }
  var showPositionDropdown by remember { mutableStateOf(false) }
  val quickPresets = listOf("أحمد", "رامي", "سارة", "نور", "فهد", "ليان", "عمر", "ريم")

  val doAddPlayer = {
    if (playerNameInput.isNotBlank()) {
      onAddPlayerAt(playerNameInput, insertIndex)
      playerNameInput = ""
      insertIndex = players.size + 1
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("player_manager_card"),
      shape = RoundedCornerShape(32.dp),
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFF2D2A33)
      ),
      border = BorderStroke(1.5.dp, Color(0xFF4A4458))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(22.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Group,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ترتيب وإضافة اللاعبين",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "إغلاق",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "إذا وصل صديق جديد للجلسة، يمكنك تحديد ترتيبه بدقة (قبل مين أو بعد مين) لمنع لخبطة التمرير والدور!",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Field + Add Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = playerNameInput,
            onValueChange = { playerNameInput = it },
            placeholder = { Text("اسم الصديق الجديد...") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("player_name_input"),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
              onDone = { doAddPlayer() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
          )

          Button(
            onClick = { doAddPlayer() },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFD0BCFF),
              contentColor = Color(0xFF381E72)
            ),
            modifier = Modifier.testTag("add_player_button")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("إضافة", fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Position Picker Selector (قبل مين أو بعد مين)
        if (players.isNotEmpty()) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
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
                    text = "مكان الإضافة في الدور:",
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

        // Quick Preset Chips
        Text(
          text = "اقتراحات سريعة:",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          quickPresets.filter { preset -> players.none { it.name == preset } }.take(5).forEach { preset ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.clickable {
                onAddPlayerAt(preset, insertIndex)
                insertIndex = players.size + 1
              }
            ) {
              Text(
                text = "+ $preset",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Players List Section with Ordering Controls
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ترتيب الحاضرين حالياً (${players.size}):",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "استخدم الأسهم 🔼 🔽 لضبط الترتيب",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (players.isEmpty()) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "لم يتم إضافة أي لاعب بعد. أضف أسماء الأصدقاء للبدء!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(16.dp)
            )
          }
        } else {
          Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            players.forEachIndexed { idx, player ->
              val color = avatarPalette[player.avatarColorIndex % avatarPalette.size]
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = color.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      shape = CircleShape,
                      color = color,
                      modifier = Modifier.size(26.dp)
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Text(
                          text = "${idx + 1}",
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White
                        )
                      }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = player.name,
                      style = MaterialTheme.typography.titleSmall,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    // Move Up
                    IconButton(
                      onClick = { onMovePlayerUp(player.id) },
                      enabled = idx > 0,
                      modifier = Modifier.size(30.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "تحريك للأعلى",
                        tint = if (idx > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.size(16.dp)
                      )
                    }

                    // Move Down
                    IconButton(
                      onClick = { onMovePlayerDown(player.id) },
                      enabled = idx < players.size - 1,
                      modifier = Modifier.size(30.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "تحريك للأسفل",
                        tint = if (idx < players.size - 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.size(16.dp)
                      )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Remove
                    IconButton(
                      onClick = { onRemovePlayer(player.id) },
                      modifier = Modifier.size(30.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "حذف",
                        tint = CoralPink,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onDismiss,
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("dismiss_player_manager")
        ) {
          Text("حفظ الترتيب وإغلاق", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
