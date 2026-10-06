package com.example.model

enum class MafiaRoleType(
  val displayNameArabic: String,
  val teamNameArabic: String,
  val iconEmoji: String,
  val descriptionArabic: String,
  val instructionArabic: String
) {
  MAFIA(
    displayNameArabic = "مافيا",
    teamNameArabic = "فريق المافيا 🔪",
    iconEmoji = "🕵️‍♂️",
    descriptionArabic = "أنت من عصابة المافيا! هدفك التخلص من أهل الحارة والمواطنين وتضليلهم بالنهار بدون ما ينكشف أمرك.",
    instructionArabic = "في طور الليل، اختر الضحية التي تريد العصابة اغتيالها."
  ),
  DETECTIVE(
    displayNameArabic = "الشرطي / المحقق",
    teamNameArabic = "فريق الحارة 🛡️",
    iconEmoji = "👮‍♂️",
    descriptionArabic = "أنت عين القانون والمحقق السري للحارة! مهمتك كشف هوية المافيا وإقناع الناس بالتصويت ضدهم بالنهار.",
    instructionArabic = "في طور الليل، اختر أي لاعب للتحقيق في هويته والتأكد إذا كان مافيا أو شريف."
  ),
  DOCTOR(
    displayNameArabic = "الطبيب / المسعف",
    teamNameArabic = "فريق الحارة 🛡️",
    iconEmoji = "🩺",
    descriptionArabic = "أنت ملاك الإنقاذ وطبيب الحارة! تمتلك الدواء لإنقاذ شخص واحد كل ليلة من هجوم المافيا.",
    instructionArabic = "في طور الليل، اختر لاعباً لحمايته وإنقاذه إذا هاجمته المافيا (يمكنك حماية نفسك أيضاً)."
  ),
  VILLAGER(
    displayNameArabic = "مواطن شريف",
    teamNameArabic = "فريق الحارة 🛡️",
    iconEmoji = "🧑‍🌾",
    descriptionArabic = "أنت مواطن شريف من أهل الحارة الطيبين! ما عندك قدرات خاصة بالليل لكن صوتك ونباهتك بالنقاش بالنهار هم سلاحك لطرد المافيا.",
    instructionArabic = "نام بأمان وراقب تصرفات الجميع في الصباح لكشف الكذابين!"
  )
}

data class MafiaPlayerRole(
  val player: Player,
  val role: MafiaRoleType,
  val isAlive: Boolean = true,
  val notes: String = ""
)

enum class MafiaGamePhase {
  ROLE_DISTRIBUTION,   // تمرير الهاتف لمعرفة الأدوار
  NIGHT_INTRO,         // الحارة تنام، تجهيز الليل
  NIGHT_MAFIA,         // المافيا تختار الضحية
  NIGHT_DOCTOR,        // الطبيب يختار من يحمي
  NIGHT_DETECTIVE,     // الشرطي يختار لاعب للتحقيق
  NIGHT_RECAP,         // كشف ما حدث أثناء الليل
  DAY_DISCUSSION,      // نقاش الصباح وطرح الشبهات (مؤقت نقاش)
  DAY_VOTING,          // تصويت الحارة على استبعاد المشتبه به
  DAY_ELIMINATION,     // إعلان نتيجة التصويت وطرد المشتبه به
  GAME_OVER            // فوز المافيا أو فوز الحارة
}

data class MafiaGameState(
  val assignments: List<MafiaPlayerRole> = emptyList(),
  val currentPassingIndex: Int = 0,
  val phase: MafiaGamePhase = MafiaGamePhase.ROLE_DISTRIBUTION,
  val roundNumber: Int = 1,
  
  // Night choices
  val nightTargetVictimId: String? = null,
  val nightProtectedPlayerId: String? = null,
  val nightInvestigatedPlayerId: String? = null,
  val lastInvestigationResultIsMafia: Boolean? = null,
  
  // Night outcome
  val lastNightEliminatedPlayerId: String? = null,
  val wasSavedByDoctor: Boolean = false,
  
  // Day phase
  val timerSecondsRemaining: Int = 120,
  val isTimerRunning: Boolean = false,
  val dayVotes: Map<String, String> = emptyMap(), // voterId -> suspectId
  val dayEliminatedPlayerId: String? = null,
  
  // Game outcome
  val winningTeamArabic: String? = null, // "الحارة الشريفة" or "عصابة المافيا"
  val isGameOver: Boolean = false
) {
  val alivePlayers: List<MafiaPlayerRole>
    get() = assignments.filter { it.isAlive }

  val aliveMafiaCount: Int
    get() = assignments.count { it.isAlive && it.role == MafiaRoleType.MAFIA }

  val aliveInnocentsCount: Int
    get() = assignments.count { it.isAlive && it.role != MafiaRoleType.MAFIA }
}
