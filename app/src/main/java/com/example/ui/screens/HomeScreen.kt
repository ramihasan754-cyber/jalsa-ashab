package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PartyQuestionsData
import com.example.model.CardItem
import com.example.model.GameCategory
import com.example.model.PenaltyItem
import com.example.model.Player
import com.example.ui.components.CreateCustomCategoryModal
import com.example.ui.components.ManageCustomCategoryModal
import com.example.ui.components.PenaltyDialog
import com.example.ui.components.PlayerManagerModal
import com.example.ui.components.avatarPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  players: List<Player>,
  isDarkMode: Boolean,
  customCategories: List<GameCategory> = emptyList(),
  allCustomCards: List<CardItem> = emptyList(),
  customQuestionsCountMap: Map<String, Int> = emptyMap(),
  onToggleDarkMode: () -> Unit,
  onSelectCategory: (GameCategory) -> Unit,
  onAddPlayer: (String) -> Unit,
  onAddPlayerAt: (name: String, insertIndex: Int) -> Unit = { name, _ -> onAddPlayer(name) },
  onMovePlayerUp: (String) -> Unit = {},
  onMovePlayerDown: (String) -> Unit = {},
  onRemovePlayer: (String) -> Unit,
  onCreateCustomCategory: (title: String, subtitle: String, emoji: String, color: Color, questions: List<String>) -> Unit = { _, _, _, _, _ -> },
  onAddQuestionToCategory: (categoryId: String, question: String) -> Unit = { _, _ -> },
  onDeleteQuestion: (questionId: String) -> Unit = {},
  onDeleteCustomCategory: (categoryId: String) -> Unit = {},
  onNavigateSetup: () -> Unit = {},
  onNavigateAdmin: () -> Unit = {},
  onNavigateSessionStats: () -> Unit = {},
  activePenalty: PenaltyItem? = null,
  penaltyTargetPlayer: Player? = null,
  onTriggerPenalty: () -> Unit = {},
  onDismissPenalty: () -> Unit = {},
  onApplyPenalty: () -> Unit = {},
  onRerollPenalty: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showPlayerModal by remember { mutableStateOf(false) }
  var showCreateCategoryModal by remember { mutableStateOf(false) }
  var selectedManageCategory by remember { mutableStateOf<GameCategory?>(null) }

  val defaultCategories = GameCategory.defaultCategories

  if (showPlayerModal) {
    PlayerManagerModal(
      players = players,
      onAddPlayer = onAddPlayer,
      onAddPlayerAt = onAddPlayerAt,
      onMovePlayerUp = onMovePlayerUp,
      onMovePlayerDown = onMovePlayerDown,
      onRemovePlayer = onRemovePlayer,
      onDismiss = { showPlayerModal = false }
    )
  }

  if (showCreateCategoryModal) {
    CreateCustomCategoryModal(
      onDismiss = { showCreateCategoryModal = false },
      onCreateCategory = { title, subtitle, emoji, color, questions ->
        onCreateCustomCategory(title, subtitle, emoji, color, questions)
      }
    )
  }

  if (selectedManageCategory != null) {
    val categoryToManage = selectedManageCategory!!
    val questionsForCat = allCustomCards.filter { it.category.id == categoryToManage.id }
    ManageCustomCategoryModal(
      category = categoryToManage,
      questions = questionsForCat,
      onAddQuestion = { qText -> onAddQuestionToCategory(categoryToManage.id, qText) },
      onDeleteQuestion = onDeleteQuestion,
      onDeleteCategory = { onDeleteCustomCategory(categoryToManage.id) },
      onPlayCategory = { onSelectCategory(categoryToManage) },
      onDismiss = { selectedManageCategory = null }
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .windowInsetsPadding(WindowInsets.statusBars)
  ) {
    // Top Bar
    TopAppBar(
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🎉", fontSize = 24.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "جلسة أصحاب",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
          )
        }
      },
      actions = {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onNavigateAdmin() }
            .testTag("admin_dashboard_nav_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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

        Spacer(modifier = Modifier.width(4.dp))

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFFFFB4AB).copy(alpha = 0.25f),
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onTriggerPenalty() }
            .testTag("home_penalty_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🎭", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "عقاب",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFFFFB4AB)
            )
          }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFFFFD700).copy(alpha = 0.18f),
          border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onNavigateSessionStats() }
            .testTag("session_stats_top_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🏆", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "الألقاب",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFFFFD700)
            )
          }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onNavigateSetup() }
            .testTag("host_setup_nav_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "👑", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "المضيف",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.width(4.dp))

        IconButton(
          onClick = onToggleDarkMode,
          modifier = Modifier.testTag("dark_mode_toggle")
        ) {
          Icon(
            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = if (isDarkMode) "تفعيل الوضع النهاري" else "تفعيل الوضع الليلي",
            tint = MaterialTheme.colorScheme.onBackground
          )
        }
      },
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent
      )
    )

    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .weight(1f),
      contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 120.dp + navBarBottom),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Hero Greeting Card
      item {
        HeroGatheringBanner(
          playersCount = players.size,
          onManagePlayers = { showPlayerModal = true }
        )
      }

      // Players Quick Bar
      item {
        PlayersQuickStrip(
          players = players,
          onOpenModal = { showPlayerModal = true }
        )
      }

      // Session Stats & Funny Titles Banner
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2B2533)
          ),
          border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateSessionStats() }
            .testTag("session_stats_home_banner")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = CircleShape,
              color = Color(0xFFFFD700).copy(alpha = 0.2f),
              modifier = Modifier.size(46.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = "🏆", fontSize = 24.sp)
              }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "ألقاب الشلة وإحصائيات الجلسة",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "مين ملك التحديات؟ مين أسرع حزاز؟ شاهد التكريم الآن!",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCAC4D0)
              )
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFFFD700).copy(alpha = 0.25f)
            ) {
              Text(
                text = "عرض 👑",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD700),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      // Create Custom Category Callout Banner
      item {
        CreateCustomCategoryBanner(
          onClick = { showCreateCategoryModal = true }
        )
      }

      // Admin Dashboard Dynamic Management Banner
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateAdmin() }
            .testTag("admin_dashboard_home_banner")
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "⚙️", fontSize = 22.sp)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "لوحة تحكم الأدمن والمحتوى 🇯🇴",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                  text = "إضافة وتعديل وحذف أي سؤال أو كلمة أو موقع بالعامية",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
              }
            }
            Text(
              text = "دخول ⬅️",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.tertiary
            )
          }
        }
      }

      // Custom Categories Section (if any exist)
      if (customCategories.isNotEmpty()) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "فئات شلتكم الخاصة ✨",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
              )
            }
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              modifier = Modifier
                .clickable { showCreateCategoryModal = true }
                .testTag("header_add_custom_category_button")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "فئة جديدة",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }

        items(customCategories) { customCategory ->
          val count = customQuestionsCountMap[customCategory.id] ?: 0
          CustomCategoryCardItem(
            category = customCategory,
            questionCount = count,
            onPlay = { onSelectCategory(customCategory) },
            onManage = { selectedManageCategory = customCategory }
          )
        }
      }

      // Default Categories Section Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "الفئات الأساسية للعب:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "٧ ألعاب وفئات للجلسة",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Category Cards List
      items(defaultCategories) { category ->
        val countText = when (category.id) {
          GameCategory.OUT_OF_THE_LOOP.id -> "${PartyQuestionsData.loopTopics.flatMap { it.words }.size} كلمة أردنية"
          GameCategory.THE_SPY.id -> "${PartyQuestionsData.spyLocations.size} أماكن سرية"
          GameCategory.CHARADES.id -> "${PartyQuestionsData.charadesWordsList.size} كلمة وتمثيل"
          GameCategory.MAFIA.id -> "أدوار سرية وتصويت"
          else -> "${PartyQuestionsData.getAllQuestionsForCategory(category).size} كرت"
        }
        CategoryCardItem(
          category = category,
          countText = countText,
          onClick = { onSelectCategory(category) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(28.dp))
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

@Composable
private fun HeroGatheringBanner(
  playersCount: Int,
  onManagePlayers: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(12.dp, RoundedCornerShape(24.dp)),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(
      1.5.dp,
      Brush.horizontalGradient(
        listOf(
          MaterialTheme.colorScheme.primary,
          MaterialTheme.colorScheme.secondary
        )
      )
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            listOf(
              MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              Color.Transparent
            )
          )
        )
        .padding(20.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "جاهزين للضحك والسوالف؟ 🥳",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "لعبة كروت تفاعلية مصممة لجمعات الأصدقاء، اختاروا الفئة واقلبوا الكروت!",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 22.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "$playersCount لاعبين بالجلسة",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }

          Button(
            onClick = onManagePlayers,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("manage_players_button")
          ) {
            Text("إدارة الأصدقاء", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun PlayersQuickStrip(
  players: List<Player>,
  onOpenModal: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onOpenModal() },
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        // Player Avatar bubbles
        players.take(4).forEachIndexed { index, player ->
          val color = avatarPalette[player.avatarColorIndex % avatarPalette.size]
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(color)
              .border(BorderStroke(1.5.dp, MaterialTheme.colorScheme.surface), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = player.name.take(1),
              style = MaterialTheme.typography.labelSmall,
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
        }

        if (players.size > 4) {
          Text(
            text = "+${players.size - 4}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
          text = if (players.isNotEmpty()) players.joinToString("، ") { it.name } else "اضغط لإضافة الأصدقاء",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1
        )
      }

      Text(
        text = "تعديل ✏️",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
private fun CreateCustomCategoryBanner(
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("create_custom_category_banner"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(
      1.5.dp,
      Brush.horizontalGradient(
        listOf(
          Color(0xFFD0BCFF),
          Color(0xFFFFB74D)
        )
      )
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            listOf(
              Color(0xFFD0BCFF).copy(alpha = 0.15f),
              Color(0xFFFFB74D).copy(alpha = 0.08f)
            )
          )
        )
        .padding(18.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFD0BCFF).copy(alpha = 0.2f),
            border = BorderStroke(1.dp, Color(0xFFD0BCFF).copy(alpha = 0.5f))
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(text = "✨", fontSize = 26.sp)
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Text(
              text = "أنشئ فئة خاصة بشلتكم 🎉",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = "سمّ الفئة واكتب أسئلتكم ومواقفكم المشتركة!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
          onClick = onClick,
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD0BCFF),
            contentColor = Color(0xFF1C1B1F)
          ),
          modifier = Modifier.testTag("create_custom_category_button")
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("أنشئ الآن", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
private fun CustomCategoryCardItem(
  category: GameCategory,
  questionCount: Int,
  onPlay: () -> Unit,
  onManage: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("custom_category_card_${category.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.5.dp, category.primaryColor.copy(alpha = 0.5f))
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              category.primaryColor.copy(alpha = 0.16f),
              Color.Transparent
            )
          )
        )
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Emoji avatar
        Surface(
          modifier = Modifier
            .size(56.dp)
            .clickable { onPlay() },
          shape = RoundedCornerShape(18.dp),
          color = category.primaryColor.copy(alpha = 0.22f),
          border = BorderStroke(1.dp, category.primaryColor.copy(alpha = 0.6f))
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(text = category.iconEmoji, fontSize = 28.sp)
          }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
          modifier = Modifier
            .weight(1f)
            .clickable { onPlay() }
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = category.titleArabic,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = category.primaryColor.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, category.primaryColor.copy(alpha = 0.4f))
            ) {
              Text(
                text = "مخصصة ✨",
                style = MaterialTheme.typography.labelSmall,
                color = category.primaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(3.dp))

          Text(
            text = category.subtitleArabic,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "$questionCount كرت مضاف",
            style = MaterialTheme.typography.labelSmall,
            color = category.primaryColor,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onManage,
            modifier = Modifier.testTag("manage_category_${category.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "إدارة الأسئلة",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Surface(
            modifier = Modifier
              .size(40.dp)
              .clickable { onPlay() },
            shape = CircleShape,
            color = category.primaryColor
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "بدء اللعب",
                tint = Color(0xFF1C1B1F),
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CategoryCardItem(
  category: GameCategory,
  countText: String,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("category_card_${category.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.5.dp, category.primaryColor.copy(alpha = 0.35f))
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              category.primaryColor.copy(alpha = 0.12f),
              Color.Transparent
            )
          )
        )
        .padding(18.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Big Emoji Avatar Box
        Surface(
          modifier = Modifier.size(60.dp),
          shape = RoundedCornerShape(18.dp),
          color = category.primaryColor.copy(alpha = 0.18f),
          border = BorderStroke(1.dp, category.primaryColor.copy(alpha = 0.4f))
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(text = category.iconEmoji, fontSize = 30.sp)
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = category.titleArabic,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = category.primaryColor.copy(alpha = 0.15f)
            ) {
              Text(
                text = countText,
                style = MaterialTheme.typography.labelSmall,
                color = category.primaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }


          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = category.subtitleArabic,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
          modifier = Modifier.size(40.dp),
          shape = CircleShape,
          color = category.primaryColor
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "بدء",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }
    }
  }
}

// Small helper extension for Box border
private fun Modifier.border(border: BorderStroke, shape: androidx.compose.ui.graphics.Shape): Modifier {
  return this.then(Modifier.background(Color.Transparent, shape))
}
