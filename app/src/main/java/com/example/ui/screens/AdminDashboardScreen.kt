package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CardItem
import com.example.model.GameCategory
import com.example.model.CharadesWordItem
import com.example.model.Player
import com.example.model.SpyLocation
import com.example.ui.theme.CategoryCharadesColor
import com.example.ui.theme.CategoryMixColor
import com.example.ui.theme.CategoryTruthColor
import com.example.ui.theme.CategoryWhoColor
import com.example.viewmodel.GameUiState
import com.example.viewmodel.PartyGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  viewModel: PartyGameViewModel,
  uiState: GameUiState,
  onNavigateBack: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf(
    "🗂️ كروت الأسئلة",
    "🤫 كلمات برا السالفة",
    "🕵️‍♂️ مواقع الجاسوس",
    "🎬 كلمات ولا كلمة",
    "👥 إدارة اللاعبين"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "لوحة تحكم الأدمن ⚙️",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "إدارة وتعديل المحتوى بالعامية الأردنية مباشرة",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("admin_back_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "رجوع"
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      ScrollableTabRow(
        selectedTabIndex = selectedTab,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        contentColor = MaterialTheme.colorScheme.primary
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = {
              Text(
                text = title,
                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
              )
            },
            modifier = Modifier.testTag("admin_tab_$index")
          )
        }
      }

      when (selectedTab) {
        0 -> AdminQuestionsTab(viewModel = viewModel, uiState = uiState)
        1 -> AdminLoopWordsTab(viewModel = viewModel, uiState = uiState)
        2 -> AdminSpyLocationsTab(viewModel = viewModel, uiState = uiState)
        3 -> AdminCharadesWordsTab(viewModel = viewModel, uiState = uiState)
        4 -> AdminPlayersTab(viewModel = viewModel, uiState = uiState)
      }
    }
  }
}


// -------------------------------------------------------------
// TAB 0: QUESTIONS MANAGEMENT
// -------------------------------------------------------------
@Composable
private fun AdminQuestionsTab(
  viewModel: PartyGameViewModel,
  uiState: GameUiState
) {
  val categories = remember(uiState.customCategories) {
    listOf(
      GameCategory.WHO_IS_MOST_LIKELY,
      GameCategory.WOULD_YOU_RATHER,
      GameCategory.CHALLENGES_PENALTIES,
      GameCategory.CONFESSIONS_TRUTH
    ) + uiState.customCategories
  }

  var selectedCategory by remember { mutableStateOf(categories.first()) }
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }
  var editingCard by remember { mutableStateOf<CardItem?>(null) }
  var cardToDelete by remember { mutableStateOf<CardItem?>(null) }

  val allCards = remember(selectedCategory, uiState.allCustomCards, uiState.deletedItemIds) {
    viewModel.getQuestionsForCategoryFiltered(selectedCategory)
  }

  val filteredCards = remember(allCards, searchQuery) {
    if (searchQuery.isBlank()) allCards
    else allCards.filter {
      it.questionArabic.contains(searchQuery, ignoreCase = true) ||
        (it.optionA?.contains(searchQuery, ignoreCase = true) == true) ||
        (it.optionB?.contains(searchQuery, ignoreCase = true) == true)
    }
  }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    // Categories Chip Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      categories.forEach { cat ->
        FilterChip(
          selected = selectedCategory.id == cat.id,
          onClick = { selectedCategory = cat },
          label = { Text("${cat.iconEmoji} ${cat.titleArabic}") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = cat.primaryColor.copy(alpha = 0.25f),
            selectedLabelColor = MaterialTheme.colorScheme.onSurface
          ),
          modifier = Modifier.testTag("admin_cat_chip_${cat.id}")
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Action Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "${filteredCards.size} كرت متوفر",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )

      Button(
        onClick = { showAddDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = selectedCategory.primaryColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("admin_add_question_btn")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("أضف كرت جديد", fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Search Box
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("ابحث في الأسئلة بالعامية...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(Icons.Default.Clear, contentDescription = "مسح")
          }
        }
      },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("admin_question_search"),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Questions List
    if (filteredCards.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "لا توجد أسئلة تطابق البحث أو في هذه الفئة.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
      ) {
        itemsIndexed(filteredCards, key = { _, item -> item.id }) { index, card ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth().animateContentSize()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(28.dp)
                      .clip(CircleShape)
                      .background(selectedCategory.primaryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "${index + 1}",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = selectedCategory.primaryColor
                    )
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = card.category.titleArabic,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                Row {
                  IconButton(
                    onClick = { editingCard = card },
                    modifier = Modifier.size(32.dp).testTag("edit_q_${card.id}")
                  ) {
                    Icon(
                      Icons.Default.Edit,
                      contentDescription = "تعديل",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                  IconButton(
                    onClick = { cardToDelete = card },
                    modifier = Modifier.size(32.dp).testTag("delete_q_${card.id}")
                  ) {
                    Icon(
                      Icons.Default.Delete,
                      contentDescription = "حذف",
                      tint = MaterialTheme.colorScheme.error,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = card.questionArabic,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
              )

              if (card.optionA != null && card.optionB != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                  verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Text("🅰️ ${card.optionA}", style = MaterialTheme.typography.bodyMedium)
                  Text("🅱️ ${card.optionB}", style = MaterialTheme.typography.bodyMedium)
                }
              }

              if (!card.hintArabic.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "💡 ${card.hintArabic}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }
  }

  // Add Question Dialog
  if (showAddDialog) {
    QuestionEditorDialog(
      title = "إضافة كرت جديد لـ ${selectedCategory.titleArabic}",
      initialQuestion = "",
      initialOptionA = "",
      initialOptionB = "",
      initialHint = "",
      isRather = selectedCategory.id == GameCategory.WOULD_YOU_RATHER.id,
      onDismiss = { showAddDialog = false },
      onSave = { text, optA, optB, hint ->
        viewModel.addQuestion(
          categoryId = selectedCategory.id,
          questionText = text,
          optionA = optA,
          optionB = optB,
          hint = hint
        )
        showAddDialog = false
      }
    )
  }

  // Edit Question Dialog
  editingCard?.let { card ->
    QuestionEditorDialog(
      title = "تعديل الكرت",
      initialQuestion = card.questionArabic,
      initialOptionA = card.optionA.orEmpty(),
      initialOptionB = card.optionB.orEmpty(),
      initialHint = card.hintArabic.orEmpty(),
      isRather = selectedCategory.id == GameCategory.WOULD_YOU_RATHER.id,
      onDismiss = { editingCard = null },
      onSave = { text, optA, optB, hint ->
        viewModel.editQuestion(
          questionId = card.id,
          categoryId = selectedCategory.id,
          questionText = text,
          optionA = optA,
          optionB = optB,
          hint = hint
        )
        editingCard = null
      }
    )
  }

  // Delete Confirmation Dialog
  cardToDelete?.let { card ->
    AlertDialog(
      onDismissRequest = { cardToDelete = null },
      title = { Text("تأكيد الحذف") },
      text = { Text("هل أنت متأكد من رغبتك بحذف هذا الكرت من الجولة نهائياً؟\n\n\"${card.questionArabic}\"") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteQuestion(card.id, selectedCategory.id)
            cardToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("حذف")
        }
      },
      dismissButton = {
        TextButton(onClick = { cardToDelete = null }) {
          Text("إلغاء")
        }
      }
    )
  }
}

// -------------------------------------------------------------
// TAB 1: OUT OF THE LOOP WORDS MANAGEMENT
// -------------------------------------------------------------
@Composable
private fun AdminLoopWordsTab(
  viewModel: PartyGameViewModel,
  uiState: GameUiState
) {
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }
  var wordToDelete by remember { mutableStateOf<String?>(null) }

  val groupedWords = remember(uiState.deletedItemIds, uiState.customLoopWords) {
    viewModel.getAllActiveLoopWordsGrouped()
  }

  val totalWordsCount = remember(groupedWords) {
    groupedWords.values.sumOf { it.size }
  }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "كلمات برا السالفة الأردنية 🇯🇴",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "$totalWordsCount كلمة أردنية نشطة",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary
        )
      }

      Button(
        onClick = { showAddDialog = true },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CategoryWhoColor),
        modifier = Modifier.testTag("admin_add_loop_word_btn")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("أضف كلمة", fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("ابحث عن كلمة أردنية (منسف، جميد، صويفية...)...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("admin_loop_word_search"),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {
      groupedWords.forEach { (catName, words) ->
        val filteredWords = if (searchQuery.isBlank()) words
        else words.filter { it.contains(searchQuery, ignoreCase = true) }

        if (filteredWords.isNotEmpty()) {
          item(key = catName) {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = catName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                  Text(
                    text = "${filteredWords.size} كلمة",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  filteredWords.forEach { word ->
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "• $word",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                      )
                      IconButton(
                        onClick = { wordToDelete = word },
                        modifier = Modifier.size(28.dp).testTag("delete_loop_word_$word")
                      ) {
                        Icon(
                          Icons.Default.Delete,
                          contentDescription = "حذف الكلمة",
                          tint = MaterialTheme.colorScheme.error,
                          modifier = Modifier.size(16.dp)
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Add Word Dialog
  if (showAddDialog) {
    var categoryInput by remember { mutableStateOf("أكلات ومشروبات أردنية 🍲") }
    var wordInput by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      title = { Text("إضافة كلمة أردنية لـ 'برا السالفة'") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = categoryInput,
            onValueChange = { categoryInput = it },
            label = { Text("اسم التصنيف أو الفئة") },
            placeholder = { Text("مثال: مناطق أردنية، أكلات...") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = wordInput,
            onValueChange = { wordInput = it },
            label = { Text("الكلمة السرية الأردنية") },
            placeholder = { Text("مثال: قلاية بندورة، دابوق...") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (wordInput.isNotBlank()) {
              viewModel.addLoopWord(categoryInput, wordInput)
              showAddDialog = false
            }
          },
          enabled = wordInput.isNotBlank()
        ) {
          Text("حفظ الكلمة")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
      }
    )
  }

  // Delete Word Confirmation Dialog
  wordToDelete?.let { word ->
    AlertDialog(
      onDismissRequest = { wordToDelete = null },
      title = { Text("حذف كلمة من برا السالفة") },
      text = { Text("هل أنت متأكد من حذف الكلمة الأردنية \"$word\" من اللعبة؟") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteLoopWord(word)
            wordToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("حذف")
        }
      },
      dismissButton = {
        TextButton(onClick = { wordToDelete = null }) { Text("إلغاء") }
      }
    )
  }
}

// -------------------------------------------------------------
// TAB 2: SPY LOCATIONS MANAGEMENT
// -------------------------------------------------------------
@Composable
private fun AdminSpyLocationsTab(
  viewModel: PartyGameViewModel,
  uiState: GameUiState
) {
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }
  var locationToDelete by remember { mutableStateOf<SpyLocation?>(null) }

  val locations = remember(uiState.deletedItemIds, uiState.customSpyLocations) {
    viewModel.getAllActiveSpyLocations()
  }

  val filteredLocations = remember(locations, searchQuery) {
    if (searchQuery.isBlank()) locations
    else locations.filter {
      it.nameArabic.contains(searchQuery, ignoreCase = true) ||
        it.descriptionArabic.contains(searchQuery, ignoreCase = true)
    }
  }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "مواقع الجاسوس الأردنية 🕵️‍♂️",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${filteredLocations.size} موقع أردني نشط",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary
        )
      }

      Button(
        onClick = { showAddDialog = true },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CategoryTruthColor),
        modifier = Modifier.testTag("admin_add_spy_location_btn")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("أضف موقع", fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("ابحث عن موقع أردني (بوليفارد، قلعة عمان...)...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("admin_spy_search"),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {
      items(filteredLocations, key = { it.id }) { loc ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${loc.iconEmoji} ${loc.nameArabic}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              IconButton(
                onClick = { locationToDelete = loc },
                modifier = Modifier.size(28.dp).testTag("delete_spy_loc_${loc.id}")
              ) {
                Icon(
                  Icons.Default.Delete,
                  contentDescription = "حذف الموقع",
                  tint = MaterialTheme.colorScheme.error,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = loc.descriptionArabic,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "الأدوار: ${loc.roles.joinToString(" • ")}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }
  }

  // Add Spy Location Dialog
  if (showAddDialog) {
    var nameInput by remember { mutableStateOf("") }
    var emojiInput by remember { mutableStateOf("📍") }
    var descInput by remember { mutableStateOf("") }
    var rolesInput by remember { mutableStateOf("زبون، موظف، سائح، حارس") }
    var questionsInput by remember { mutableStateOf("هل المكان قديم؟\nشو بتشم ريحة هون؟") }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      title = { Text("إضافة موقع أردني جديد لـ 'الجاسوس'") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("اسم الموقع الأردني") },
            placeholder = { Text("مثال: وادي الموجب") },
            modifier = Modifier.fillMaxWidth()
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = emojiInput,
              onValueChange = { emojiInput = it },
              label = { Text("الإيموجي") },
              modifier = Modifier.width(90.dp)
            )
            OutlinedTextField(
              value = descInput,
              onValueChange = { descInput = it },
              label = { Text("وصف المكان") },
              placeholder = { Text("طبيعة، شلالات ومغامرة...") },
              modifier = Modifier.weight(1f)
            )
          }
          OutlinedTextField(
            value = rolesInput,
            onValueChange = { rolesInput = it },
            label = { Text("الأدوار (مفصولة بفواصل)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = questionsInput,
            onValueChange = { questionsInput = it },
            label = { Text("أسئلة مساعدة (سطر لكل سؤال)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (nameInput.isNotBlank()) {
              val roles = rolesInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
              val questions = questionsInput.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
              viewModel.addSpyLocation(
                nameArabic = nameInput,
                iconEmoji = emojiInput,
                descriptionArabic = descInput,
                roles = roles,
                sampleQuestions = questions
              )
              showAddDialog = false
            }
          },
          enabled = nameInput.isNotBlank()
        ) {
          Text("حفظ الموقع")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
      }
    )
  }

  // Delete Location Confirmation Dialog
  locationToDelete?.let { loc ->
    AlertDialog(
      onDismissRequest = { locationToDelete = null },
      title = { Text("حذف موقع من الجاسوس") },
      text = { Text("هل أنت متأكد من حذف موقع \"${loc.nameArabic}\" من جولات الجاسوس؟") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteSpyLocation(loc.id)
            locationToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("حذف")
        }
      },
      dismissButton = {
        TextButton(onClick = { locationToDelete = null }) { Text("إلغاء") }
      }
    )
  }
}

// -------------------------------------------------------------
// TAB 3: PLAYERS MANAGEMENT
// -------------------------------------------------------------
@Composable
private fun AdminPlayersTab(
  viewModel: PartyGameViewModel,
  uiState: GameUiState
) {
  var newPlayerName by remember { mutableStateOf("") }
  var playerToEdit by remember { mutableStateOf<Player?>(null) }
  var editedNameInput by remember { mutableStateOf("") }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text(
      text = "إدارة اللاعبين الحاضرين في الجلسة 👥",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = "${uiState.players.size} لاعبين مسجلين",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Add Player Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = newPlayerName,
        onValueChange = { newPlayerName = it },
        placeholder = { Text("اكتب اسم اللاعب...") },
        singleLine = true,
        modifier = Modifier.weight(1f).testTag("admin_add_player_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Button(
        onClick = {
          if (newPlayerName.isNotBlank()) {
            viewModel.addPlayer(newPlayerName)
            newPlayerName = ""
          }
        },
        enabled = newPlayerName.isNotBlank(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("admin_add_player_btn")
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text("إضافة")
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {
      items(uiState.players, key = { it.id }) { player ->
        val avatarColors = listOf(
          Color(0xFF6750A4), Color(0xFF006A60), Color(0xFF984061),
          Color(0xFFB52706), Color(0xFF00639B), Color(0xFF705D00)
        )
        val color = avatarColors.getOrElse(player.avatarColorIndex) { MaterialTheme.colorScheme.primary }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(color),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = player.name.take(1),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = player.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
              )
              if (player.penaltiesCount > 0) {
                Text(
                  text = "العقوبات: ${player.penaltiesCount}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.error
                )
              }
            }
          }

          Row {
            IconButton(
              onClick = {
                playerToEdit = player
                editedNameInput = player.name
              },
              modifier = Modifier.size(32.dp).testTag("edit_player_${player.id}")
            ) {
              Icon(
                Icons.Default.Edit,
                contentDescription = "تعديل الاسم",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
            IconButton(
              onClick = { viewModel.removePlayer(player.id) },
              modifier = Modifier.size(32.dp).testTag("delete_player_${player.id}")
            ) {
              Icon(
                Icons.Default.Delete,
                contentDescription = "حذف اللاعب",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }

  // Edit Player Dialog
  playerToEdit?.let { player ->
    AlertDialog(
      onDismissRequest = { playerToEdit = null },
      title = { Text("تعديل اسم اللاعب") },
      text = {
        OutlinedTextField(
          value = editedNameInput,
          onValueChange = { editedNameInput = it },
          label = { Text("الاسم الجديد") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      },
      confirmButton = {
        Button(
          onClick = {
            if (editedNameInput.isNotBlank()) {
              viewModel.updatePlayerName(player.id, editedNameInput)
              playerToEdit = null
            }
          },
          enabled = editedNameInput.isNotBlank()
        ) {
          Text("حفظ")
        }
      },
      dismissButton = {
        TextButton(onClick = { playerToEdit = null }) { Text("إلغاء") }
      }
    )
  }
}

// -------------------------------------------------------------
// HELPER DIALOG: QUESTION EDITOR
// -------------------------------------------------------------
@Composable
private fun QuestionEditorDialog(
  title: String,
  initialQuestion: String,
  initialOptionA: String,
  initialOptionB: String,
  initialHint: String,
  isRather: Boolean,
  onDismiss: () -> Unit,
  onSave: (question: String, optionA: String?, optionB: String?, hint: String?) -> Unit
) {
  var questionText by remember { mutableStateOf(initialQuestion) }
  var optionA by remember { mutableStateOf(initialOptionA) }
  var optionB by remember { mutableStateOf(initialOptionB) }
  var hintText by remember { mutableStateOf(initialHint) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title, fontWeight = FontWeight.Bold) },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = questionText,
          onValueChange = { questionText = it },
          label = { Text("نص السؤال أو التحدي بالعامية الأردنية *") },
          placeholder = { Text("مثال: مين أكتر واحد بطلب شاورما بنص الليل؟") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth()
        )

        if (isRather) {
          OutlinedTextField(
            value = optionA,
            onValueChange = { optionA = it },
            label = { Text("الخيار (أ)") },
            placeholder = { Text("الخيار الأول...") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = optionB,
            onValueChange = { optionB = it },
            label = { Text("الخيار (ب)") },
            placeholder = { Text("الخيار الثاني...") },
            modifier = Modifier.fillMaxWidth()
          )
        }

        OutlinedTextField(
          value = hintText,
          onValueChange = { hintText = it },
          label = { Text("تلميح أو تعليق مضحك (اختياري)") },
          placeholder = { Text("مثال: صوتوا كلكم بنفس اللحظة!") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (questionText.isNotBlank()) {
            onSave(
              questionText.trim(),
              optionA.trim().ifEmpty { null },
              optionB.trim().ifEmpty { null },
              hintText.trim().ifEmpty { null }
            )
          }
        },
        enabled = questionText.isNotBlank()
      ) {
        Text("حفظ")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

// -------------------------------------------------------------
// TAB 3: CHARADES WORDS MANAGEMENT
// -------------------------------------------------------------
@Composable
private fun AdminCharadesWordsTab(
  viewModel: PartyGameViewModel,
  uiState: GameUiState
) {
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }
  var wordToDelete by remember { mutableStateOf<CharadesWordItem?>(null) }

  val allWords = remember(uiState.deletedItemIds, uiState.customCharadesWords) {
    viewModel.getAllActiveCharadesWords()
  }

  val groupedWords = remember(allWords) {
    allWords.groupBy { it.categoryTag }
  }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "كلمات وتمثيل ولا كلمة 🎬",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${allWords.size} كلمة وعبارة نشطة",
          style = MaterialTheme.typography.bodySmall,
          color = CategoryCharadesColor
        )
      }

      Button(
        onClick = { showAddDialog = true },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CategoryCharadesColor),
        modifier = Modifier.testTag("admin_add_charades_word_btn")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("أضف كلمة", fontWeight = FontWeight.Bold, color = Color(0xFF1C1B1F))
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("ابحث عن كلمة أو تصنيف (منسف، غوار، عجلون...)...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("admin_charades_word_search"),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {
      groupedWords.forEach { (catTag, words) ->
        val filteredWords = if (searchQuery.isBlank()) words
        else words.filter {
          it.word.contains(searchQuery, ignoreCase = true) ||
          it.categoryTag.contains(searchQuery, ignoreCase = true)
        }

        if (filteredWords.isNotEmpty()) {
          item(key = catTag) {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = catTag,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CategoryCharadesColor
                  )
                  Text(
                    text = "${filteredWords.size} كلمة",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  filteredWords.forEach { wordItem ->
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                      ) {
                        Text(
                          text = wordItem.word,
                          style = MaterialTheme.typography.bodyMedium,
                          fontWeight = FontWeight.Medium
                        )
                        if (wordItem.isCustom) {
                          Spacer(modifier = Modifier.width(6.dp))
                          Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                          ) {
                            Text(
                              text = "مضاف",
                              style = MaterialTheme.typography.labelSmall,
                              color = MaterialTheme.colorScheme.primary,
                              fontSize = 10.sp,
                              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                          }
                        }
                      }

                      IconButton(
                        onClick = { wordToDelete = wordItem },
                        modifier = Modifier.size(32.dp).testTag("delete_charades_${wordItem.id}")
                      ) {
                        Icon(
                          Icons.Default.Delete,
                          contentDescription = "حذف الكلمة",
                          tint = MaterialTheme.colorScheme.error,
                          modifier = Modifier.size(18.dp)
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Delete Confirmation Dialog
  if (wordToDelete != null) {
    val target = wordToDelete!!
    AlertDialog(
      onDismissRequest = { wordToDelete = null },
      title = { Text("حذف الكلمة؟", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت متأكد من حذف \"${target.word}\" من لعبة ولا كلمة؟ لن تظهر بالجولات القادمة.") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteCharadesWord(target.id, target.word)
            wordToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
          modifier = Modifier.testTag("confirm_delete_charades_dialog_btn")
        ) {
          Text("حذف")
        }
      },
      dismissButton = {
        TextButton(onClick = { wordToDelete = null }) { Text("إلغاء") }
      }
    )
  }

  // Add Word Dialog
  if (showAddDialog) {
    AddCharadesWordDialog(
      onDismiss = { showAddDialog = false },
      onAdd = { word, tag ->
        viewModel.addCharadesWord(word, tag)
        showAddDialog = false
      }
    )
  }
}

@Composable
private fun AddCharadesWordDialog(
  onDismiss: () -> Unit,
  onAdd: (String, String) -> Unit
) {
  var wordText by remember { mutableStateOf("") }
  var selectedTag by remember { mutableStateOf("مسلسلات سورية ومصرية 📺") }

  val commonTags = listOf(
    "مسلسلات سورية ومصرية 📺",
    "أفلام سينما مصرية 🎬",
    "مسرحيات كوميدية 🎭",
    "أفلام ومسلسلات 🎬"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("إضافة عمل جديد لـ ولا كلمة 🎬", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = wordText,
          onValueChange = { wordText = it },
          label = { Text("اسم المسلسل، الفيلم أو المسرحية") },
          placeholder = { Text("مثال: فيلم الفيل الأزرق، مسرحية المتزوجون...") },
          modifier = Modifier.fillMaxWidth().testTag("add_charades_word_input")
        )

        Text(
          text = "اختر التصنيف:",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(commonTags) { tag ->
            FilterChip(
              selected = selectedTag == tag,
              onClick = { selectedTag = tag },
              label = { Text(tag, fontSize = 11.sp) }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (wordText.isNotBlank()) {
            onAdd(wordText.trim(), selectedTag)
          }
        },
        enabled = wordText.isNotBlank(),
        modifier = Modifier.testTag("confirm_add_charades_dialog_btn")
      ) {
        Text("إضافة")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("إلغاء") }
    }
  )
}

