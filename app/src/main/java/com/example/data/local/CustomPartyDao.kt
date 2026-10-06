package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomPartyDao {
  // Categories
  @Query("SELECT * FROM custom_categories ORDER BY createdAt ASC")
  fun getAllCustomCategories(): Flow<List<CustomCategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CustomCategoryEntity)

  @Query("DELETE FROM custom_categories WHERE id = :categoryId")
  suspend fun deleteCategoryById(categoryId: String)

  // Questions (For both custom categories and custom/edited questions on built-in categories)
  @Query("SELECT * FROM custom_questions WHERE categoryId = :categoryId ORDER BY createdAt ASC")
  fun getQuestionsForCategory(categoryId: String): Flow<List<CustomQuestionEntity>>

  @Query("SELECT * FROM custom_questions ORDER BY createdAt ASC")
  fun getAllCustomQuestions(): Flow<List<CustomQuestionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertQuestion(question: CustomQuestionEntity)

  @Query("DELETE FROM custom_questions WHERE id = :questionId")
  suspend fun deleteQuestionById(questionId: String)

  // Deleted Items Tracking (Allows hiding/deleting any built-in question, word, or location)
  @Query("SELECT * FROM deleted_items")
  fun getAllDeletedItems(): Flow<List<DeletedItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDeletedItem(item: DeletedItemEntity)

  @Query("DELETE FROM deleted_items WHERE itemId = :itemId")
  suspend fun removeDeletedItem(itemId: String)

  // Custom Loop Words ("برا السالفة")
  @Query("SELECT * FROM custom_loop_words ORDER BY createdAt ASC")
  fun getAllCustomLoopWords(): Flow<List<CustomLoopWordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomLoopWord(word: CustomLoopWordEntity)

  @Query("DELETE FROM custom_loop_words WHERE id = :id")
  suspend fun deleteCustomLoopWordById(id: String)

  // Custom Spy Locations ("الجاسوس")
  @Query("SELECT * FROM custom_spy_locations ORDER BY createdAt ASC")
  fun getAllCustomSpyLocations(): Flow<List<CustomSpyLocationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomSpyLocation(location: CustomSpyLocationEntity)

  @Query("DELETE FROM custom_spy_locations WHERE id = :id")
  suspend fun deleteCustomSpyLocationById(id: String)

  // Custom Charades Words ("ولا كلمة")
  @Query("SELECT * FROM custom_charades_words ORDER BY createdAt ASC")
  fun getAllCustomCharadesWords(): Flow<List<CustomCharadesWordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomCharadesWord(word: CustomCharadesWordEntity)

  @Query("DELETE FROM custom_charades_words WHERE id = :id")
  suspend fun deleteCustomCharadesWordById(id: String)
}

