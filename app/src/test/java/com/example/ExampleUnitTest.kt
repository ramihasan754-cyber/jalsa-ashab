package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun whoIsMostLikely_hasExactly500UniqueCards() {
    val cards = com.example.data.PartyQuestionsData.whoIsMostLikely
    assertEquals(500, cards.size)
    val uniqueIds = cards.map { it.id }.toSet()
    assertEquals(500, uniqueIds.size)
  }

  @Test
  fun charadesGameState_roundHistoryItemWorks() {
    val round = com.example.model.CharadesRoundHistoryItem(
      roundNumber = 1,
      team = com.example.model.Team.TEAM_A,
      teamName = "فريق النشمي",
      actorName = "أحمد",
      correctCount = 2,
      correctWords = listOf("فيلم الكيف", "مسلسل الهيبة"),
      skippedWords = emptyList(),
      teamAScoreAfter = 2,
      teamBScoreAfter = 0
    )
    val state = com.example.model.CharadesGameState(
      teamAScore = 2,
      roundHistory = listOf(round)
    )
    assertEquals(1, state.roundHistory.size)
    assertEquals("فريق النشمي", state.roundHistory.first().teamName)
    assertEquals(2, state.roundHistory.first().correctCount)
  }

  @Test
  fun playerWithTitle_assignsFunnyTitlesBasedOnPerformance() {
    val p1 = com.example.model.Player(id = "p1", name = "رامي")
    val p2 = com.example.model.Player(id = "p2", name = "سارة")
    val p3 = com.example.model.Player(id = "p3", name = "خالد")

    val statsMap = mapOf(
      "p1" to com.example.model.PlayerSessionStats(playerId = "p1", challengesCompleted = 5),
      "p2" to com.example.model.PlayerSessionStats(playerId = "p2", charadesWordsGuessed = 8),
      "p3" to com.example.model.PlayerSessionStats(playerId = "p3", confessionsAnswered = 6)
    )

    val assignedTitles = mutableSetOf<String>()
    val result = listOf(p1, p2, p3).map { player ->
      val s = statsMap[player.id] ?: com.example.model.PlayerSessionStats(playerId = player.id)
      val title: com.example.model.PlayerTitle = when {
        s.challengesCompleted > 0 && s.challengesCompleted >= (statsMap.values.maxOfOrNull { it.challengesCompleted } ?: 0) && "ملك التحديات" !in assignedTitles -> {
          com.example.model.PlayerTitle("ملك التحديات", "⚡", "نفّذ أكبر عدد من الأحكام والتحديات الجريئة بدون ما يرمش له جفن!", "قلبه فولاذي")
        }
        s.charadesWordsGuessed > 0 && s.charadesWordsGuessed >= (statsMap.values.maxOfOrNull { it.charadesWordsGuessed } ?: 0) && "أسرع حزاز" !in assignedTitles -> {
          com.example.model.PlayerTitle("أسرع حزاز", "🚀", "أسرع بديهة وحزر أصعب الكلمات والأفلام من أول إشارة تمثيل!", "رادار إشارات")
        }
        s.confessionsAnswered > 0 && s.confessionsAnswered >= (statsMap.values.maxOfOrNull { it.confessionsAnswered } ?: 0) && "صاحب الصراحة المطلقة" !in assignedTitles -> {
          com.example.model.PlayerTitle("صاحب الصراحة المطلقة", "🕊️", "اعترف بكل أسراره ع المكشوف وما خبى ولا تفصيلة ع الشلة!", "كتاب مفتوح")
        }
        else -> com.example.model.PlayerTitle("مستشار القعدة", "🛋️", "مراقب هادئ", "حكيم الجلسة")
      }
      assignedTitles.add(title.title)
      com.example.model.PlayerWithTitle(player = player, stats = s, title = title)
    }

    assertEquals("ملك التحديات", result[0].title.title)
    assertEquals("أسرع حزاز", result[1].title.title)
    assertEquals("صاحب الصراحة المطلقة", result[2].title.title)
  }
}
