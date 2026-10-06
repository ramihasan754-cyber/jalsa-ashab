package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CardItem
import com.example.model.GameCategory
import com.example.model.Player
import kotlin.math.abs

@Composable
fun PartyCard(
  cardItem: CardItem,
  isFlipped: Boolean,
  currentIndex: Int,
  totalCards: Int,
  players: List<Player>,
  onFlipCard: () -> Unit,
  onSwipeNext: () -> Unit,
  modifier: Modifier = Modifier
) {
  // 3D rotation animation
  val rotation by animateFloatAsState(
    targetValue = if (isFlipped) 180f else 0f,
    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
    label = "cardFlipAnimation"
  )

  var totalDragDistance by remember { mutableFloatStateOf(0f) }

  Box(
    modifier = modifier
      .widthIn(min = 260.dp, max = 380.dp)
      .fillMaxWidth(0.95f)
      .fillMaxHeight()
      .pointerInput(cardItem.id) {
        detectHorizontalDragGestures(
          onDragEnd = {
            if (abs(totalDragDistance) > 100f) {
              onSwipeNext()
            }
            totalDragDistance = 0f
          },
          onDragCancel = { totalDragDistance = 0f },
          onHorizontalDrag = { _, dragAmount ->
            totalDragDistance += dragAmount
          }
        )
      }
      .clickable {
        if (!isFlipped) onFlipCard()
      }
      .testTag("party_card_container"),
    contentAlignment = Alignment.Center
  ) {
    // Ambient glowing backdrop behind the card matching the Sleek design
    Box(
      modifier = Modifier
        .fillMaxSize(0.92f)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              cardItem.category.primaryColor.copy(alpha = 0.20f),
              Color.Transparent
            )
          ),
          shape = RoundedCornerShape(28.dp)
        )
    )

    Card(
      modifier = Modifier
        .fillMaxSize()
        .shadow(
          elevation = 12.dp,
          shape = RoundedCornerShape(28.dp),
          spotColor = cardItem.category.primaryColor.copy(alpha = 0.40f)
        )
        .graphicsLayer {
          rotationY = rotation
          cameraDistance = 14f * density
        }
        .testTag("interactive_party_card"),
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFF2D2A33)
      ),
      border = BorderStroke(
        width = 1.5.dp,
        brush = Brush.verticalGradient(
          colors = listOf(
            cardItem.category.primaryColor.copy(alpha = 0.7f),
            Color(0xFF4A4458).copy(alpha = 0.4f),
            cardItem.category.primaryColor.copy(alpha = 0.3f)
          )
        )
      )
    ) {
      if (rotation <= 90f) {
        // FRONT COVER (Face Down - Tap to reveal)
        CardFrontCover(
          category = cardItem.category,
          currentIndex = currentIndex,
          totalCards = totalCards
        )
      } else {
        // BACK FACE (Face Up - Question Revealed)
        // Flip content horizontally so text is not mirrored
        Box(
          modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationY = 180f }
        ) {
          CardRevealedContent(
            cardItem = cardItem,
            currentIndex = currentIndex,
            totalCards = totalCards,
            players = players
          )
        }
      }
    }
  }
}

@Composable
private fun CardFrontCover(
  category: GameCategory,
  currentIndex: Int,
  totalCards: Int
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            category.primaryColor.copy(alpha = 0.3f),
            Color(0xFF381E72).copy(alpha = 0.4f),
            Color(0xFF1C1B1F)
          )
        )
      )
      .padding(18.dp),
    contentAlignment = Alignment.Center
  ) {
    // Faint sleek watermark in corner
    Text(
      text = category.iconEmoji,
      fontSize = 60.sp,
      modifier = Modifier
        .align(Alignment.TopEnd)
        .graphicsLayer {
          rotationZ = 12f
          alpha = 0.10f
        }
    )

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Big Emoji Avatar in sleek container
      Surface(
        modifier = Modifier.size(72.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF4A4458).copy(alpha = 0.6f),
        border = BorderStroke(1.5.dp, category.primaryColor.copy(alpha = 0.5f))
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = category.iconEmoji,
            fontSize = 36.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Category Title
      Text(
        text = category.titleArabic,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFE6E1E5),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = category.subtitleArabic,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFFCAC4D0),
        textAlign = TextAlign.Center,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Tap hint sleek pill
      Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFF2D2A33),
        border = BorderStroke(1.dp, Color(0xFF938F99).copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.TouchApp,
            contentDescription = null,
            tint = category.primaryColor,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "المس لقلب الكرت وكشف السؤال",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFE6E1E5),
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "كرت ${currentIndex + 1} من $totalCards",
        style = MaterialTheme.typography.labelSmall,
        color = Color(0xFFCAC4D0).copy(alpha = 0.7f),
        fontSize = 10.sp
      )
    }
  }
}

@Composable
private fun CardRevealedContent(
  cardItem: CardItem,
  currentIndex: Int,
  totalCards: Int,
  players: List<Player>
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            cardItem.category.primaryColor.copy(alpha = 0.30f),
            Color(0xFF381E72).copy(alpha = 0.45f),
            Color(0xFF1C1B1F)
          )
        )
      )
      .padding(16.dp)
  ) {
    // Watermark icon in corner matching the Sleek design HTML
    Text(
      text = cardItem.category.iconEmoji,
      fontSize = 60.sp,
      modifier = Modifier
        .align(Alignment.TopEnd)
        .graphicsLayer {
          rotationZ = 12f
          alpha = 0.12f
        }
    )

    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Row: Category Badge + Card Counter
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(50),
          color = Color(0xFF2D2A33).copy(alpha = 0.85f),
          border = BorderStroke(1.dp, Color(0xFF4A4458))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .background(cardItem.category.primaryColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = cardItem.category.titleArabic,
              style = MaterialTheme.typography.labelSmall,
              color = cardItem.category.primaryColor,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(50),
          color = Color(0xFF2D2A33).copy(alpha = 0.6f),
          border = BorderStroke(1.dp, Color(0xFF4A4458).copy(alpha = 0.5f))
        ) {
          Text(
            text = "${currentIndex + 1} / $totalCards",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFCAC4D0),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Middle Content: Question / Challenge / Options
      Column(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Player Turn Pill Badge matching the Sleek design HTML
        val assignedPlayer = if (players.isNotEmpty()) {
          remember(cardItem.id) { players.random() }
        } else null

        if (assignedPlayer != null) {
          Surface(
            shape = RoundedCornerShape(50),
            color = Color.Black.copy(alpha = 0.25f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier.padding(bottom = 10.dp)
          ) {
            Text(
              text = "الدور على: ${assignedPlayer.name}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color.White.copy(alpha = 0.85f),
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
              fontSize = 11.sp
            )
          }
        }

        // Main Question Text with dynamic responsive typography based on text length
        val textLength = cardItem.questionArabic.length
        val (textStyle, lineHeight) = when {
          cardItem.optionA != null -> Pair(MaterialTheme.typography.titleSmall, 22.sp)
          textLength > 90 -> Pair(MaterialTheme.typography.bodyLarge, 24.sp)
          textLength > 55 -> Pair(MaterialTheme.typography.titleMedium, 26.sp)
          else -> Pair(MaterialTheme.typography.titleLarge, 28.sp)
        }

        Text(
          text = cardItem.questionArabic,
          style = textStyle,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          textAlign = TextAlign.Center,
          lineHeight = lineHeight,
          modifier = Modifier.padding(horizontal = 6.dp)
        )

        // For "لو خيروك" (Would You Rather): Display Option A and Option B
        if (cardItem.optionA != null && cardItem.optionB != null) {
          Spacer(modifier = Modifier.height(10.dp))

          OptionChoiceCard(
            badge = "خيار ١",
            text = cardItem.optionA,
            borderColor = cardItem.category.primaryColor
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "⚡ أَوْ ⚡",
            style = MaterialTheme.typography.labelMedium,
            color = cardItem.category.primaryColor,
            fontWeight = FontWeight.ExtraBold
          )

          Spacer(modifier = Modifier.height(6.dp))

          OptionChoiceCard(
            badge = "خيار ٢",
            text = cardItem.optionB,
            borderColor = Color(0xFFFFB4AB)
          )
        }

        // Hint / Instruction Text matching the Sleek design HTML
        val instructionText = if (!cardItem.hintArabic.isNullOrBlank()) {
          cardItem.hintArabic
        } else {
          "اتفقوا على الشخص اللي يستحق هذا السؤال!"
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = instructionText,
          style = MaterialTheme.typography.bodySmall,
          color = Color.White.copy(alpha = 0.7f),
          textAlign = TextAlign.Center,
          fontSize = 11.sp
        )
      }

      // Bottom Row: Card unlocked message
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = Color(0xFF81C784),
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "تم كشف الكرت • اضغط التالي للمتابعة ➡️",
          style = MaterialTheme.typography.labelSmall,
          color = Color.White.copy(alpha = 0.65f),
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
private fun OptionChoiceCard(
  badge: String,
  text: String,
  borderColor: Color
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF2D2A33),
    border = BorderStroke(1.dp, borderColor.copy(alpha = 0.6f))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = borderColor,
        modifier = Modifier.padding(end = 8.dp)
      ) {
        Text(
          text = badge,
          style = MaterialTheme.typography.labelSmall,
          color = Color(0xFF381E72),
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
          fontSize = 11.sp
        )
      }
      Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFFE6E1E5),
        modifier = Modifier.weight(1f),
        lineHeight = 18.sp
      )
    }
  }
}
