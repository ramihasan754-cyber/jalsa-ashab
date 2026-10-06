package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Player
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SunsetAmber
import kotlinx.coroutines.delay

@Composable
fun RouletteDialog(
  pickedPlayer: Player,
  players: List<Player>,
  onDismiss: () -> Unit,
  onSpinAgain: () -> Unit
) {
  var displayPlayer by remember { mutableStateOf(pickedPlayer) }
  var isAnimating by remember { mutableStateOf(true) }

  val scaleAnim by animateFloatAsState(
    targetValue = if (isAnimating) 0.85f else 1.05f,
    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
    label = "rouletteScale"
  )

  LaunchedEffect(pickedPlayer) {
    isAnimating = true
    // Fun slot machine shuffle cycle
    repeat(8) {
      if (players.isNotEmpty()) {
        displayPlayer = players.random()
      }
      delay(70)
    }
    displayPlayer = pickedPlayer
    isAnimating = false
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("roulette_dialog_card"),
      shape = RoundedCornerShape(32.dp),
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFF2D2A33)
      ),
      border = BorderStroke(
        1.5.dp,
        Brush.verticalGradient(
          colors = listOf(Color(0xFFD0BCFF), Color(0xFF4A4458))
        )
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(modifier = Modifier.fillMaxWidth()) {
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.TopEnd)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "إغلاق",
              tint = Color(0xFFCAC4D0)
            )
          }

          Text(
            text = "🎲 روليت الاختيار العشوائي",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD0BCFF),
            modifier = Modifier.align(Alignment.Center)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Chosen Player Highlight Circle
        Surface(
          modifier = Modifier
            .size(110.dp)
            .graphicsLayer {
              scaleX = scaleAnim
              scaleY = scaleAnim
            },
          shape = CircleShape,
          color = Color(0xFF4A4458),
          border = BorderStroke(2.dp, Color(0xFFD0BCFF))
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = if (isAnimating) "🎲" else "👑",
              fontSize = 44.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = if (isAnimating) "جاري الاختيار..." else "وقع الاختيار على:",
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFFCAC4D0)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = displayPlayer.name,
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.ExtraBold,
          color = Color(0xFFE6E1E5),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onSpinAgain() },
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF4A4458),
            border = BorderStroke(1.dp, Color(0xFF938F99).copy(alpha = 0.3f))
          ) {
            Row(
              modifier = Modifier.padding(vertical = 12.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(imageVector = Icons.Default.Casino, contentDescription = null, tint = Color(0xFFE6E1E5))
              Spacer(modifier = Modifier.size(8.dp))
              Text("اختيار لاعب آخر", color = Color(0xFFE6E1E5))
            }
          }

          Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFD0BCFF),
              contentColor = Color(0xFF381E72)
            )
          ) {
            Text("يلا نبدأ الدور!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
          }
        }
      }
    }
  }
}
