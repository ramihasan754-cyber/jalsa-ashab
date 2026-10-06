package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

val customCategoryEmojis = listOf("👥", "🤫", "🔥", "🍕", "🏖️", "🎮", "☕", "🚗", "🏆", "🎭", "💡", "🎲")

val customCategoryColors = listOf(
  Color(0xFFD0BCFF), // Lilac
  Color(0xFFFFB4AB), // Coral
  Color(0xFFFFB74D), // Amber
  Color(0xFF80DEEA), // Mint Teal
  Color(0xFF81C784), // Emerald Sage
  Color(0xFFFF80AB), // Electric Pink
  Color(0xFF8C9EFF), // Neon Indigo
  Color(0xFFFFAB91)  // Sunset Peach
)

val partyQuestionInspirations = listOf(
  "مين أكثر واحد في الشلة مستحيل يوصل على موعده ويقول أنا بالطريق؟",
  "مين أكثر شخص لو مسكنا جواله 5 دقائق بنشوف فضايح وميمز غريبة؟",
  "لو خيروك تسافر رحلة صيفية مع شخص من الجلسة بدون جوال أسبوع، مين تختار؟",
  "مين أكثر واحد دايم يقول 'أنا طفران' وهو أول واحد يصرف في المطعم؟",
  "تحدي: اتصل على شخص من الشلة مو موجود وقول له سر وهمي وسكر الخط!",
  "مين أكثر واحد يرسل فويس نوت مدتها 8 دقائق كأنها برنامج وثائقي؟",
  "مين أكثر واحد يختفي في نص السهرة وينام أول ما نوصل؟"
)

@Composable
fun CreateCustomCategoryModal(
  onDismiss: () -> Unit,
  onCreateCategory: (title: String, subtitle: String, emoji: String, color: Color, questions: List<String>) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var subtitle by remember { mutableStateOf("") }
  var selectedEmoji by remember { mutableStateOf(customCategoryEmojis.first()) }
  var selectedColor by remember { mutableStateOf(customCategoryColors.first()) }

  var currentQuestionInput by remember { mutableStateOf("") }
  val questionsList = remember {
    mutableStateListOf(
      "مين أكثر واحد في شلتنا مستحيل يوصل على موعده؟",
      "مين أكثر واحد فينا عنده أسرار ومواقف تفضح محد يعرفها؟"
    )
  }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .fillMaxHeight(0.92f)
        .testTag("create_custom_category_modal"),
      shape = RoundedCornerShape(32.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1B1F)),
      border = BorderStroke(1.5.dp, Color(0xFF4A4458))
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "إغلاق",
              tint = Color(0xFFCAC4D0)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "إنشاء فئة خاصة بشلتكم ✨",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFFE6E1E5)
            )
            Text(
              text = "أضف أسئلتكم وسوالفكم الخاصة",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFFCAC4D0)
            )
          }

          Surface(
            shape = CircleShape,
            color = selectedColor.copy(alpha = 0.25f),
            border = BorderStroke(1.dp, selectedColor),
            modifier = Modifier.size(40.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(text = selectedEmoji, fontSize = 20.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Category Basic Info
          item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "١. اسم الفئة والوصف:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = selectedColor
              )

              OutlinedTextField(
                value = title,
                onValueChange = {
                  title = it
                  if (it.isNotBlank()) errorMessage = null
                },
                placeholder = { Text("مثال: شلة الاستراحة، أسرار الجمعة...", color = Color(0xFF938F99)) },
                label = { Text("اسم الفئة *") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = selectedColor,
                  unfocusedBorderColor = Color(0xFF4A4458),
                  focusedTextColor = Color(0xFFE6E1E5),
                  unfocusedTextColor = Color(0xFFE6E1E5),
                  focusedLabelColor = selectedColor,
                  unfocusedLabelColor = Color(0xFFCAC4D0),
                  focusedContainerColor = Color(0xFF2D2A33),
                  unfocusedContainerColor = Color(0xFF2D2A33)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("category_title_input")
              )

              OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                placeholder = { Text("مثال: سوالف وفضايح جمعاتنا الأسبوعية", color = Color(0xFF938F99)) },
                label = { Text("وصف مختصر (اختياري)") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = selectedColor,
                  unfocusedBorderColor = Color(0xFF4A4458),
                  focusedTextColor = Color(0xFFE6E1E5),
                  unfocusedTextColor = Color(0xFFE6E1E5),
                  focusedLabelColor = selectedColor,
                  unfocusedLabelColor = Color(0xFFCAC4D0),
                  focusedContainerColor = Color(0xFF2D2A33),
                  unfocusedContainerColor = Color(0xFF2D2A33)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("category_subtitle_input")
              )
            }
          }

          // Emoji & Color Selection
          item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "٢. اختر أيقونة ولون الفئة:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = selectedColor
              )

              // Emoji Picker Row
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                customCategoryEmojis.forEach { emoji ->
                  Surface(
                    shape = CircleShape,
                    color = if (selectedEmoji == emoji) selectedColor.copy(alpha = 0.35f) else Color(0xFF2D2A33),
                    border = BorderStroke(
                      if (selectedEmoji == emoji) 2.dp else 1.dp,
                      if (selectedEmoji == emoji) selectedColor else Color(0xFF4A4458)
                    ),
                    modifier = Modifier
                      .size(44.dp)
                      .clickable { selectedEmoji = emoji }
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(text = emoji, fontSize = 22.sp)
                    }
                  }
                }
              }

              // Color Palette Picker Row
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                customCategoryColors.forEach { color ->
                  val isSelected = selectedColor == color
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(color)
                      .clickable { selectedColor = color }
                      .border(
                        width = if (isSelected) 3.dp else 0.dp,
                        color = if (isSelected) Color.White else Color.Transparent,
                        shape = CircleShape
                      )
                  )
                }
              }
            }
          }

          // Questions Input Section
          item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "٣. كروت وأسئلة الشلة (${questionsList.size}):",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = selectedColor
                )

                Text(
                  text = "أضف أسئلة تضحك!",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFFCAC4D0)
                )
              }

              // Quick Input Box
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedTextField(
                  value = currentQuestionInput,
                  onValueChange = {
                    currentQuestionInput = it
                    if (it.isNotBlank()) errorMessage = null
                  },
                  placeholder = { Text("اكتب سؤالاً أو موقفاً عن الشلة...", color = Color(0xFF938F99), fontSize = 13.sp) },
                  shape = RoundedCornerShape(16.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = selectedColor,
                    unfocusedBorderColor = Color(0xFF4A4458),
                    focusedTextColor = Color(0xFFE6E1E5),
                    unfocusedTextColor = Color(0xFFE6E1E5),
                    focusedContainerColor = Color(0xFF2D2A33),
                    unfocusedContainerColor = Color(0xFF2D2A33)
                  ),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("new_question_input")
                )

                Button(
                  onClick = {
                    if (currentQuestionInput.isNotBlank()) {
                      questionsList.add(0, currentQuestionInput.trim())
                      currentQuestionInput = ""
                      errorMessage = null
                    }
                  },
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = selectedColor,
                    contentColor = Color(0xFF1C1B1F)
                  ),
                  modifier = Modifier.testTag("add_question_to_list_button")
                ) {
                  Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("أضف", fontWeight = FontWeight.Bold)
                }
              }

              // Inspirations / Suggestions Strip
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFF2D2A33), RoundedCornerShape(16.dp))
                  .border(1.dp, Color(0xFF4A4458).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFFFFB74D),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "أفكار مقترحة (اضغط للإضافة السريعة):",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFB74D),
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  partyQuestionInspirations.forEach { idea ->
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = Color(0xFF1C1B1F),
                      border = BorderStroke(1.dp, Color(0xFF4A4458)),
                      modifier = Modifier.clickable {
                        questionsList.add(0, idea)
                        errorMessage = null
                      }
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "+ $idea",
                          style = MaterialTheme.typography.labelSmall,
                          color = Color(0xFFE6E1E5)
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // Questions List Preview
          itemsIndexed(questionsList) { index, questionText ->
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = Color(0xFF2D2A33),
              border = BorderStroke(1.dp, Color(0xFF4A4458).copy(alpha = 0.6f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  modifier = Modifier.weight(1f),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = CircleShape,
                    color = selectedColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(26.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = selectedColor
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = questionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFE6E1E5),
                    lineHeight = 20.sp
                  )
                }

                IconButton(
                  onClick = { questionsList.removeAt(index) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف الكرت",
                    tint = Color(0xFFFFB4AB),
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = errorMessage ?: "",
            color = Color(0xFFFFB4AB),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Save and Start Playing Button
        Button(
          onClick = {
            if (title.isBlank()) {
              errorMessage = "يرجى كتابة اسم الفئة أولاً!"
              return@Button
            }
            if (questionsList.isEmpty()) {
              errorMessage = "يرجى إضافة سؤال واحد على الأقل للفئة!"
              return@Button
            }

            onCreateCategory(
              title.trim(),
              subtitle.trim().ifEmpty { "أسئلة وسوالف شلتنا" },
              selectedEmoji,
              selectedColor,
              questionsList.toList()
            )
            onDismiss()
          },
          shape = RoundedCornerShape(26.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = selectedColor,
            contentColor = Color(0xFF1C1B1F)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("submit_create_category_button")
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "حفظ الفئة والبدء باللعب 🚀",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }
    }
  }
}
