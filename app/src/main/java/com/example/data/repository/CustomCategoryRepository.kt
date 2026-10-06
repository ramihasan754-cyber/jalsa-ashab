package com.example.data.repository

import com.example.data.local.CustomCategoryEntity
import com.example.data.local.CustomCharadesWordEntity
import com.example.data.local.CustomLoopWordEntity
import com.example.data.local.CustomPartyDao
import com.example.data.local.CustomQuestionEntity
import com.example.data.local.CustomSpyLocationEntity
import com.example.data.local.DeletedItemEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CustomCategoryRepository(private val dao: CustomPartyDao) {
  val customCategories: Flow<List<CustomCategoryEntity>> = dao.getAllCustomCategories()
  val allCustomQuestions: Flow<List<CustomQuestionEntity>> = dao.getAllCustomQuestions()
  val allDeletedItems: Flow<List<DeletedItemEntity>> = dao.getAllDeletedItems()
  val allCustomLoopWords: Flow<List<CustomLoopWordEntity>> = dao.getAllCustomLoopWords()
  val allCustomSpyLocations: Flow<List<CustomSpyLocationEntity>> = dao.getAllCustomSpyLocations()
  val allCustomCharadesWords: Flow<List<CustomCharadesWordEntity>> = dao.getAllCustomCharadesWords()

  fun getQuestionsForCategory(categoryId: String): Flow<List<CustomQuestionEntity>> =
    dao.getQuestionsForCategory(categoryId)

  suspend fun saveCategory(category: CustomCategoryEntity) = dao.insertCategory(category)

  suspend fun deleteCategory(categoryId: String) = dao.deleteCategoryById(categoryId)

  suspend fun saveQuestion(question: CustomQuestionEntity) = dao.insertQuestion(question)

  suspend fun deleteQuestion(questionId: String) = dao.deleteQuestionById(questionId)

  suspend fun markItemDeleted(itemId: String, itemType: String) =
    dao.insertDeletedItem(DeletedItemEntity(itemId = itemId, itemType = itemType))

  suspend fun unmarkItemDeleted(itemId: String) = dao.removeDeletedItem(itemId)

  suspend fun saveCustomLoopWord(word: CustomLoopWordEntity) = dao.insertCustomLoopWord(word)

  suspend fun deleteCustomLoopWord(id: String) = dao.deleteCustomLoopWordById(id)

  suspend fun saveCustomSpyLocation(location: CustomSpyLocationEntity) =
    dao.insertCustomSpyLocation(location)

  suspend fun deleteCustomSpyLocation(id: String) = dao.deleteCustomSpyLocationById(id)

  suspend fun saveCustomCharadesWord(word: CustomCharadesWordEntity) =
    dao.insertCustomCharadesWord(word)

  suspend fun deleteCustomCharadesWord(id: String) = dao.deleteCustomCharadesWordById(id)


  suspend fun seedSampleCategoryIfEmpty(currentCategoriesCount: Int) {
    if (currentCategoriesCount > 0) return

    val sampleCategoryId = "sample_friends_group"
    val sampleCategory = CustomCategoryEntity(
      id = sampleCategoryId,
      title = "شلة أصحابنا 🇯🇴",
      subtitle = "أسئلة وسوالف ومواقف خاصة بجمعتنا!",
      iconEmoji = "👥",
      colorHex = 0xFFD0BCFF.toLong()
    )
    dao.insertCategory(sampleCategory)

    val sampleQuestions = listOf(
      CustomQuestionEntity(
        id = UUID.randomUUID().toString(),
        categoryId = sampleCategoryId,
        questionArabic = "مين أكتر واحد بشلتنا مستحيل يوصل ع موعده وبحكي 'أنا عند الإشارة' وهو لسه بالدار بيلبس؟",
        hintArabic = "صوتوا كلكم عليه هسا!"
      ),
      CustomQuestionEntity(
        id = UUID.randomUUID().toString(),
        categoryId = sampleCategoryId,
        questionArabic = "مين أكتر شخص بالقعدة لو مسكنا تلفونه 5 دقايق بنشوف ميمز وفضائح بتقلب القعدة؟",
        hintArabic = "الاعتراف نص الفضيلة!"
      ),
      CustomQuestionEntity(
        id = UUID.randomUUID().toString(),
        categoryId = sampleCategoryId,
        questionArabic = "لو خيروك تطلع رحلة ع وادي رم مع اتنين من الشلة مين بتختار ومين بتستبعد؟",
        optionA = "الأول يسوق طول الطريق",
        optionB = "التاني يختار الأكل والمطاعم",
        hintArabic = "بدون زعل، اختار بالاسم!"
      ),
      CustomQuestionEntity(
        id = UUID.randomUUID().toString(),
        categoryId = sampleCategoryId,
        questionArabic = "مين أكتر واحد فينا دايماً بحكي 'أنا طفران وممعيش كاش' وهو أول واحد بطلب دليفري وشاورما؟",
        hintArabic = "كشف الحساب البنكي الفوري!"
      )
    )

    for (q in sampleQuestions) {
      dao.insertQuestion(q)
    }
  }
}
