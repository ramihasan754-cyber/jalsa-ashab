package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayerWithTitle
import com.example.ui.theme.CategoryMixColor
import com.example.ui.theme.CategoryWhoColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SessionStatsScreen(
  playersWithTitles: List<PlayerWithTitle>,
  totalSessionRounds: Int,
  isDarkMode: Boolean = true,
  onResetStats: () -> Unit,
  onNavigateBack: () -> Unit,
  onTriggerPenalty: () -> Unit = {}
) {
  BackHandler { onNavigateBack() }

  var showResetConfirmDialog by remember { mutableStateOf(false) }

  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🔄", fontSize = 22.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "تصفير إحصائيات الجلسة؟",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }
      },
      text = {
        Text(
          text = "هل أنت متأكد من رغبتك في مسح سجل الإحصائيات والألقاب وتصفير العدادات لبدء سهرة جديدة من الصفر؟",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showResetConfirmDialog = false
            onResetStats()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
          modifier = Modifier.testTag("confirm_reset_stats_button")
        ) {
          Text("تصفير وبدء جديد", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("إلغاء", fontWeight = FontWeight.Medium)
        }
      }
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "إحصائيات الجلسة والألقاب 🏆",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = "ألقاب مضحكة وتكريم لكل نشمي بالشلة",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("session_stats_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "رجوع"
            )
          }
        },
        actions = {
          IconButton(
            onClick = { showResetConfirmDialog = true },
            modifier = Modifier.testTag("session_stats_reset_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "تصفير الإحصائيات",
              tint = MaterialTheme.colorScheme.error
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("session_stats_screen_list"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Hero Overview Header
      item {
        StatsHeroBanner(
          playersCount = playersWithTitles.size,
          totalRounds = totalSessionRounds
        )
      }

      // 2. Section Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ألقاب الشلة الرسمية 🎭",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
          )
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
          ) {
            Text(
              text = "${playersWithTitles.size} لاعبين",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }
      }

      // 3. Player Cards with Titles & Detailed Metrics
      items(playersWithTitles, key = { it.player.id }) { item ->
        PlayerTitleCard(item = item)
      }

      // 4. Quick Group Action Cards
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Button(
            onClick = onNavigateBack,
            modifier = Modifier
              .weight(1f)
              .height(52.dp)
              .testTag("session_stats_continue_game_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("متابعة اللعب 🎮", fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = onTriggerPenalty,
            modifier = Modifier
              .weight(1f)
              .height(52.dp)
              .testTag("session_stats_trigger_penalty_button"),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary)
          ) {
            Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("حكم عشوائي ⚡", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun StatsHeroBanner(playersCount: Int, totalRounds: Int) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ),
    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              CategoryMixColor.copy(alpha = 0.15f),
              CategoryWhoColor.copy(alpha = 0.10f)
            )
          )
        )
        .padding(18.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = Color(0xFFFFD700).copy(alpha = 0.2f),
              modifier = Modifier.size(46.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = "👑", fontSize = 24.sp)
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "حصاد السهرة والضحك",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "تقييم أداء كل لاعب وجرأته في الجلسة",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          MetricCounterPill(label = "النشامى الحاضرين", value = "$playersCount 👥")
          MetricCounterPill(label = "كروت وجولات", value = "$totalRounds 🎴")
          MetricCounterPill(label = "أجواء الجلسة", value = "مشتعلة 🔥")
        }
      }
    }
  }
}

@Composable
private fun MetricCounterPill(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Black,
      color = MaterialTheme.colorScheme.primary
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlayerTitleCard(item: PlayerWithTitle) {
  val player = item.player
  val title = item.title
  val stats = item.stats
  val badgeColor = Color(title.badgeColorHex)

  val avatarColors = listOf(
    Color(0xFF6750A4),
    Color(0xFFB3261E),
    Color(0xFF006A60),
    Color(0xFF984061),
    Color(0xFF5A5D8B),
    Color(0xFF7D5260)
  )
  val playerColor = avatarColors[player.avatarColorIndex % avatarColors.size]

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("player_title_card_${player.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
    border = BorderStroke(1.5.dp, badgeColor.copy(alpha = 0.6f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Top Row: Player Avatar, Name & Funny Title
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          // Avatar
          Surface(
            shape = CircleShape,
            color = playerColor,
            modifier = Modifier.size(46.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = player.name.take(1),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = player.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            // Title Pill
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = badgeColor.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.6f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = title.emoji, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = title.title,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Black,
                  color = badgeColor
                )
              }
            }
          }
        }

        // Funny Badge Tag
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
          Text(
            text = title.funnyBadge,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Funny Description
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = title.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(10.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Detailed Metrics Chips
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (stats.challengesCompleted > 0) {
          StatChip(text = "تحديات منجزة: ${stats.challengesCompleted} ⚡", color = Color(0xFFFF5722))
        }
        if (stats.confessionsAnswered > 0) {
          StatChip(text = "اعترافات وصراحة: ${stats.confessionsAnswered} 🕊️", color = Color(0xFF00BCD4))
        }
        if (stats.wouldYouRatherAnswered > 0) {
          StatChip(text = "قرارات لو خيروك: ${stats.wouldYouRatherAnswered} ⚖️", color = Color(0xFF9C27B0))
        }
        if (stats.votesReceivedInWhoIsMost > 0) {
          StatChip(text = "أصوات مين أكثر: ${stats.votesReceivedInWhoIsMost} 🎯", color = Color(0xFFE91E63))
        }
        if (stats.charadesWordsGuessed > 0) {
          StatChip(text = "حزار ولا كلمة: ${stats.charadesWordsGuessed} 🎬", color = Color(0xFF4CAF50))
        }
        if (player.penaltiesCount > 0 || stats.penaltiesTaken > 0) {
          StatChip(text = "أحكام متلقاة: ${player.penaltiesCount + stats.penaltiesTaken} 🎭", color = MaterialTheme.colorScheme.error)
        }
        if (player.score > 0) {
          StatChip(text = "مجموع النقاط: ${player.score} 🏆", color = Color(0xFFFFB300))
        }
      }
    }
  }
}

@Composable
private fun StatChip(text: String, color: Color) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = color.copy(alpha = 0.12f),
    border = BorderStroke(0.5.dp, color.copy(alpha = 0.4f))
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.SemiBold,
      color = color,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
  }
}
