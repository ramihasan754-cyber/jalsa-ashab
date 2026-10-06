package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameCategory

@Composable
fun QuickAddQuestionDialog(
  category: GameCategory,
  onAddQuestion: (questionText: String) -> Unit,
  onDismiss: () -> Unit
) {
  var questionText by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("quick_add_question_card"),
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2A33)),
      border = BorderStroke(1.5.dp, category.primaryColor)
    ) {
      Column(
        modifier = Modifier.padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = Color(0xFFCAC4D0))
          }

          Text(
            text = "أضف كرت لـ ${category.iconEmoji} ${category.titleArabic}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE6E1E5)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = questionText,
          onValueChange = {
            questionText = it
            if (it.isNotBlank()) isError = false
          },
          placeholder = {
            Text("اكتب سؤال أو موقف جديد للشلة...", color = Color(0xFF938F99), fontSize = 13.sp)
          },
          label = { Text("نص السؤال أو التحدي") },
          minLines = 3,
          maxLines = 5,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = category.primaryColor,
            unfocusedBorderColor = Color(0xFF4A4458),
            focusedTextColor = Color(0xFFE6E1E5),
            unfocusedTextColor = Color(0xFFE6E1E5),
            focusedContainerColor = Color(0xFF1C1B1F),
            unfocusedContainerColor = Color(0xFF1C1B1F)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("quick_question_text_input")
        )

        if (isError) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "يرجى كتابة نص الكرت!",
            color = Color(0xFFFFB4AB),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            if (questionText.isBlank()) {
              isError = true
              return@Button
            }
            onAddQuestion(questionText.trim())
            onDismiss()
          },
          shape = RoundedCornerShape(18.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = category.primaryColor,
            contentColor = Color(0xFF1C1B1F)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("quick_submit_question_button")
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("إضافة الكرت للجلسة", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
