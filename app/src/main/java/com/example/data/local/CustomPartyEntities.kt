package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "custom_categories")
data class CustomCategoryEntity(
  @PrimaryKey val id: String,
  val title: String,
  val subtitle: String,
  val iconEmoji: String,
  val colorHex: Long,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "custom_questions",
  indices = [Index("categoryId")]
)
data class CustomQuestionEntity(
  @PrimaryKey val id: String,
  val categoryId: String,
  val questionArabic: String,
  val optionA: String? = null,
  val optionB: String? = null,
  val hintArabic: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "deleted_items")
data class DeletedItemEntity(
  @PrimaryKey val itemId: String,
  val itemType: String, // "QUESTION", "LOOP_WORD", "SPY_LOCATION", "CATEGORY"
  val deletedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_loop_words")
data class CustomLoopWordEntity(
  @PrimaryKey val id: String,
  val categoryName: String,
  val word: String,
  val iconEmoji: String = "🇯🇴",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_spy_locations")
data class CustomSpyLocationEntity(
  @PrimaryKey val id: String,
  val nameArabic: String,
  val iconEmoji: String = "📍",
  val descriptionArabic: String,
  val rolesListRaw: String,
  val sampleQuestionsRaw: String,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_charades_words")
data class CustomCharadesWordEntity(
  @PrimaryKey val id: String,
  val word: String,
  val categoryTag: String = "عام",
  val createdAt: Long = System.currentTimeMillis()
)

