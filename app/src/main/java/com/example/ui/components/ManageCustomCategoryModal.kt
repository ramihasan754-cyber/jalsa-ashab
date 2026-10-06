package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CardItem
import com.example.model.GameCategory

@Composable
fun ManageCustomCategoryModal(
  category: GameCategory,
  questions: List<CardItem>,
  onAddQuestion: (String) -> Unit,
  onDeleteQuestion: (String) -> Unit,
  onDeleteCategory: () -> Unit,
  onPlayCategory: () -> Unit,
  onDismiss: () -> Unit
) {
  var newQuestionText by remember { mutableStateOf("") }
  var showDeleteConfirmation by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .fillMaxHeight(0.88f)
        .testTag("manage_custom_category_modal"),
      shape = RoundedCornerShape(32.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1B1F)),
      border = BorderStroke(1.5.dp, Color(0xFF4A4458))
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = Color(0xFFCAC4D0))
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = category.primaryColor.copy(alpha = 0.25f),
              border = BorderStroke(1.dp, category.primaryColor),
              modifier = Modifier.size(38.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = category.iconEmoji, fontSize = 20.sp)
              }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = category.titleArabic,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFE6E1E5)
              )
              Text(
                text = "${questions.size} كروت مضافة",
                style = MaterialTheme.typography.labelSmall,
                color = category.primaryColor
              )
            }
          }

          IconButton(onClick = { showDeleteConfirmation = true }) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "حذف الفئة",
              tint = Color(0xFFFFB4AB)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Add Question Box
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = newQuestionText,
            onValueChange = { newQuestionText = it },
            placeholder = { Text("أضف سؤال جديد للشلة...", color = Color(0xFF938F99), fontSize = 13.sp) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = category.primaryColor,
              unfocusedBorderColor = Color(0xFF4A4458),
              focusedTextColor = Color(0xFFE6E1E5),
              unfocusedTextColor = Color(0xFFE6E1E5),
              focusedContainerColor = Color(0xFF2D2A33),
              unfocusedContainerColor = Color(0xFF2D2A33)
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("manage_new_question_input")
          )

          Button(
            onClick = {
              if (newQuestionText.isNotBlank()) {
                onAddQuestion(newQuestionText.trim())
                newQuestionText = ""
              }
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = category.primaryColor,
              contentColor = Color(0xFF1C1B1F)
            ),
            modifier = Modifier.testTag("manage_add_question_button")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("أضف", fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Questions List
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (questions.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("📭", fontSize = 42.sp)
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "لا توجد أسئلة بعد في هذه الفئة!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCAC4D0)
                  )
                  Text(
                    text = "اكتب أول كرت واضغط 'أضف' لبدء التحدي",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF938F99)
                  )
                }
              }
            }
          } else {
            itemsIndexed(questions) { index, card ->
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF2D2A33),
                border = BorderStroke(1.dp, Color(0xFF4A4458).copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Surface(
                      shape = CircleShape,
                      color = category.primaryColor.copy(alpha = 0.2f),
                      modifier = Modifier.size(28.dp)
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Text(
                          text = "${index + 1}",
                          style = MaterialTheme.typography.labelSmall,
                          fontWeight = FontWeight.Bold,
                          color = category.primaryColor
                        )
                      }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = card.questionArabic,
                      style = MaterialTheme.typography.bodyMedium,
                      color = Color(0xFFE6E1E5),
                      lineHeight = 22.sp
                    )
                  }

                  IconButton(
                    onClick = { onDeleteQuestion(card.id) },
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
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Play Button
        Button(
          onClick = {
            onPlayCategory()
            onDismiss()
          },
          shape = RoundedCornerShape(26.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = category.primaryColor,
            contentColor = Color(0xFF1C1B1F)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("manage_play_category_button")
        ) {
          Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "العب بهذه الفئة الحين! 🎮",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }
    }

    if (showDeleteConfirmation) {
      Dialog(onDismissRequest = { showDeleteConfirmation = false }) {
        Card(
          shape = RoundedCornerShape(26.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2A33)),
          border = BorderStroke(1.dp, Color(0xFFFFB4AB)),
          modifier = Modifier.padding(16.dp)
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("⚠️", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "حذف فئة '${category.titleArabic}'؟",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFE6E1E5),
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "سيتم حذف جميع الكروت والأسئلة الخاصة بهذه الفئة.",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFFCAC4D0),
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = { showDeleteConfirmation = false },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
              ) {
                Text("إلغاء", color = Color(0xFFE6E1E5))
              }
              Button(
                onClick = {
                  showDeleteConfirmation = false
                  onDeleteCategory()
                  onDismiss()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFFFFB4AB),
                  contentColor = Color(0xFF690005)
                )
              ) {
                Text("حذف", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
