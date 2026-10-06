package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.PenaltyItem
import com.example.model.Player
import com.example.ui.theme.CoralPink
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen

@Composable
fun PenaltyDialog(
  penalty: PenaltyItem,
  targetPlayer: Player?,
  onDismiss: () -> Unit,
  onApplyPenalty: () -> Unit,
  onRerollPenalty: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    AnimatedVisibility(
      visible = true,
      enter = scaleIn(animationSpec = spring(dampingRatio = 0.7f)) + fadeIn()
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("penalty_dialog_card"),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
          containerColor = Color(0xFF2D2A33)
        ),
        border = BorderStroke(
          1.5.dp,
          Brush.verticalGradient(
            colors = listOf(Color(0xFFFFB4AB), Color(0xFF4A4458))
          )
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Top row with close
          Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .align(Alignment.TopEnd)
                .size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "إغلاق",
                tint = Color(0xFFCAC4D0)
              )
            }

            // Big Emoji badge in center
            Surface(
              modifier = Modifier
                .align(Alignment.Center)
                .size(76.dp),
              shape = CircleShape,
              color = Color(0xFFFFB4AB).copy(alpha = 0.2f),
              border = BorderStroke(1.5.dp, Color(0xFFFFB4AB))
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = penalty.iconEmoji, fontSize = 38.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "🚨 حُكْم الجلسة! 🚨",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFFFB4AB),
            textAlign = TextAlign.Center
          )

          if (targetPlayer != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              shape = RoundedCornerShape(50),
              color = Color(0xFF1C1B1F),
              border = BorderStroke(1.dp, Color(0xFF4A4458))
            ) {
              Text(
                text = "المحكوم عليه: ${targetPlayer.name} ⚖️",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE6E1E5),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Penalty Text Box
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1C1B1F),
            border = BorderStroke(1.dp, Color(0xFF4A4458))
          ) {
            Text(
              text = penalty.textArabic,
              style = MaterialTheme.typography.bodyLarge,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFE6E1E5),
              textAlign = TextAlign.Center,
              lineHeight = 26.sp,
              modifier = Modifier.padding(18.dp)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = penalty.severity,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFCAC4D0)
          )

          Spacer(modifier = Modifier.height(22.dp))

          // Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              onClick = onRerollPenalty,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(18.dp),
              color = Color(0xFF4A4458),
              border = BorderStroke(1.dp, Color(0xFF938F99).copy(alpha = 0.3f))
            ) {
              Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = null,
                  tint = Color(0xFFE6E1E5),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("حكم آخر", fontSize = 13.sp, color = Color(0xFFE6E1E5))
              }
            }

            Button(
              onClick = onApplyPenalty,
              modifier = Modifier.weight(1.2f),
              shape = RoundedCornerShape(18.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFB4AB),
                contentColor = Color(0xFF690005)
              )
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("تم التنفيذ (+1)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
