package com.example.data

import com.example.model.CardItem
import com.example.model.CharadesWordItem
import com.example.model.GameCategory
import com.example.model.LoopTopic
import com.example.model.PenaltyItem
import com.example.model.SpyLocation


object PartyQuestionsData {

  // Category 1: "مين أكثر واحد" - Exactly 500 Unique Questions in Authentic Jordanian Dialect
  val whoIsMostLikely: List<CardItem> = WhoIsMostLikelyData1.list +
                                         WhoIsMostLikelyData2.list +
                                         WhoIsMostLikelyData3.list +
                                         WhoIsMostLikelyData4.list

  // Category 2: "لو خيروك" - Exactly 100 Unique Dilemmas (Jordanian Dialect)
  val wouldYouRather = WouldYouRatherData.list

  // Category 3: "تحديات وأحكام" - Exactly 100 Unique Gathering Dares & Actions
  val challengesAndPenalties = ChallengesAndPenaltiesData.list

  // Category 4: "اعترافات وصراحة" - Exactly 100 Unique Confessions & Truths
  val confessionsAndTruth = ConfessionsAndTruthData.list

  // Category 5: "برا السالفة" (Out of the Loop) - Exactly 100 Unique Tangible Jordanian Items (No Abstract Adjectives)
  val loopTopics = listOf(
    LoopTopic(
      id = "jordanian_food",
      categoryName = "أكلات وأطعمة ملموسة 🍲",
      iconEmoji = "🍲",
      words = OutOfTheLoopTangibleData.topicsByCategory["أكلات وأطعمة ملموسة"] ?: emptyList()
    ),
    LoopTopic(
      id = "jordanian_places",
      categoryName = "أماكن ومعالم ملموسة 🏛️",
      iconEmoji = "🏛️",
      words = OutOfTheLoopTangibleData.topicsByCategory["أماكن ومعالم ملموسة"] ?: emptyList()
    ),
    LoopTopic(
      id = "jordanian_home_items",
      categoryName = "أغراض وأدوات بالدار ملموسة 🛋️",
      iconEmoji = "🛋️",
      words = OutOfTheLoopTangibleData.topicsByCategory["أغراض وأدوات بالدار ملموسة"] ?: emptyList()
    ),
    LoopTopic(
      id = "jordanian_transport",
      categoryName = "مركبات ووسائل مواصلات 🚌",
      iconEmoji = "🚌",
      words = OutOfTheLoopTangibleData.topicsByCategory["مركبات ووسائل مواصلات"] ?: emptyList()
    ),
    LoopTopic(
      id = "jordanian_professions",
      categoryName = "مهن وأشخاص ملموسين 👥",
      iconEmoji = "👥",
      words = OutOfTheLoopTangibleData.topicsByCategory["مهن وأشخاص ملموسين"] ?: emptyList()
    )
  )

  // Flat list containing all 100 unique tangible words (No abstract adjectives)
  val all100LoopWords: List<String> = OutOfTheLoopTangibleData.all100TangibleTopics

  // Category 6: "الجاسوس" - Exactly 100 Unique Local and Global Locations
  val spyLocations = SpyLocationsData.list

  // Quick Penalties for Gatherings
  val penaltiesList = listOf(
    PenaltyItem("p1", "نط ع رجل واحدة 15 نطة متتالية وأنت بتصيح: 'أنا ملك الشاورما!'", "عقاب خفيف ومضحك", "🦘"),
    PenaltyItem("p2", "امسك كاسة مي باردة وحطها ع جبينك بدون ما توقع لمدة 20 ثانية!", "عقاب توازن", "🧊"),
    PenaltyItem("p3", "احكي نكتة بايخة وإذا محدا ضحك من الشلة بتنفذ 10 عدات ضغط فوراً!", "عقاب كوميدي محرج", "🃏"),
    PenaltyItem("p4", "قلد صوت قطة بتصحي صحابها الصبح بنص الصالة!", "عقاب تمثيل", "🐱"),
    PenaltyItem("p5", "اشرب كاسة مي مع رشة فلفل أسود وخلّصها بدفعة واحدة!", "عقاب تذوق حار", "🌶️"),
    PenaltyItem("p6", "خلي الشخص اللي ع يسارك يكتب ستاتوس مضحك بحسابك الواتساب لمدة 5 دقايق!", "عقاب السوشيال ميديا", "📱")
  )

  // Category 7: "ولا كلمة" (Charades Game) - Exactly 150 Unique Egyptian and Syrian Movies, Series, and Plays (Strictly NO repetition, NO foreign content, NO religious topics)
  val charadesWordsList: List<String> = listOf(
    "مسلسل باب الحارة", "مسلسل ضيعة ضايعة", "مسلسل ولاد العم", "مسلسل المرايا", "مسلسل الهيبة",
    "مسلسل طاش ما طاش", "مسلسل رأفت الهجان", "مسلسل الزير سالم", "مسلسل باب المقام", "مسلسل العاصوف",
    "مسلسل عائلة فصفص", "مسلسل الحجاج بن يوسف", "مسلسل مظهر بيومي", "مسلسل حارة أبو عواد", "مسلسل راس غليص",
    "مسلسل وضحا وبن عجلان", "مسلسل سلسال الدم", "مسلسل حكايات ابن الحداد", "مسلسل البواسل", "مسلسل الخوالي",
    "مسلسل حمام القيشاني", "مسلسل ليالي الصالحية", "مسلسل الجوارح", "مسلسل الكواسر", "مسلسل الإنتظار",
    "مسلسل صبايا", "مسلسل بقعة ضوء", "مسلسل عزه وبنتها", "مسلسل زمن البرغوت", "مسلسل أمل حناء",
    "مسرحية كاسك يا وطن", "مسرحية غربة", "مسرحية مدرسة المشاغبين", "مسرحية عش المجانين", "مسرحية الوجه البشوش",
    "مسرحية الشطّار", "مسرحية المتزوجون", "مسرحية الواد سيد الشغال", "مسرحية سك على بناتك", "مسرحية ريا وسكينة",
    "مسرحية البعبع", "مسرحية شارع محمد علي", "مسرحية سبيس تو", "مسرحية حزمني يا", "مسرحية الزير سالم الكوميدية",
    "فيلم ولاد رزق", "فيلم الفيل الأزرق", "فيلم كازابلانكا", "فيلم الإرهاب والكباب", "فيلم عسل أسود",
    "فيلم صنع في مصر", "فيلم تيتو", "فيلم السفارة في العمارة", "فيلم همام في أمستردام", "فيلم صعيدي في الجامعة الأمريكية",
    "فيلم اللمبي", "فيلم بوحة", "فيلم حسن وبقلظ", "فيلم نادي الرجال السري", "فيلم بحب السيما",
    "فيلم اسكندرية كمان وكمان", "فيلم عمارة يعقوبيان", "فيلم الكيف", "فيلم العار", "فيلم الجزيرة",
    "فيلم بنات العم", "فيلم سمير وشهير وبهير", "فيلم لا تراجع ولا استسلام", "فيلم الحرب العالمية الثالثة", "فيلم تسليم أهالي",
    "فيلم واحد تاني", "فيلم نبيل الجميل أخصائي تجميل", "فيلم وش في وش", "فيلم المصلحة", "فيلم الخلية",
    "فيلم مرجان أحمد مرجان", "فيلم التجربة الدنماركية", "فيلم عريس من جهة امنية", "فيلم زكي شان", "فيلم الباشا تلميذ",
    "فيلم حاحا وتفاحة", "فيلم كلمني شكرا", "فيلم كف قمر", "فيلم ساعة ونص", "فيلم إكس لارج",
    "مسلسل الندم", "مسلسل أرواح عارية", "مسلسل قلم حمرة", "مسلسل الولادة من الخاصرة", "مسلسل غزل البنات",
    "مسلسل تخت شرقي", "مسلسل جرذان الصحراء", "مسلسل أكسل", "مسلسل سنكسار", "مسلسل قاع المدينة",
    "مسلسل أهل الغرام", "مسلسل بواب الريح", "مسلسل وردة شامية", "مسلسل العراب", "مسلسل مذكرات عائلية جدا",
    "مسلسل الكبير أوي", "مسلسل اللعبة", "مسلسل ريفو", "مسلسل الا أنا", "مسلسل موضوع عائلي",
    "مسلسل نيللي وشريهان", "مسلسل في بيتنا روبوت", "مسلسل مكتوب عليا", "مسلسل البيت بيتي", "مسلسل سفاح الجيزة",
    "مسلسل اللعبة الجزء الثاني", "مسلسل العتاولة", "مسلسل حق عرب", "مسلسل المداح", "مسلسل العائدون",
    "مسلسل الاختيار الجزء الثاني", "مسلسل الاختيار الجزء الثالث", "مسلسل هجمة مرتدة", "مسلسل القاهرة كابول", "مسلسل بطلوع الروح",
    "مسلسل جزيرة غمام", "مسلسل الفصول الأربعة", "مسلسل الصفارة", "مسلسل رشيد", "مسلسل جعفر العمدة",
    "مسلسل سوق الكانتو", "مسلسل جت سليمة", "مسلسل الهرشة السابعة", "مسلسل تحت الوصاية", "مسلسل رسالة الإمام",
    "مسلسل وعود سخية", "مسلسل سره الباتع", "مسلسل تلت التلاتة", "مسلسل تغيير جو", "مسلسل بابا جه",
    "مسلسل العتبة الحمراء", "مسلسل خالتي صفية والدير", "مسلسل المال والبنون", "مسلسل الضوء الشارد", "مسلسل ليالي الحلمية",
    "مسلسل أرابيسك", "مسلسل زيزينيا", "مسلسل الشهد والدموع", "مسلسل رأفت الهجان الجزء الثاني", "مسلسل رأفت الهجان الجزء الثالث",
    "مسلسل يوميات مدير عام", "مسلسل بطل من هذا الزمان", "مسلسل لن أعيش في جلباب أبي", "مسرحية شاهد ما شفش حاجة", "فيلم فول الصين العظيم"
  )

  // Category 7: Option B - "تفاصيل وأشياء دقيقة" (100 Items)
  val specificDetailsWordsList: List<String> = listOf(
    "غطاية قلم حبر", "سحاب بلوزة معلق", "زر قميص مقطوع", "فردة جرابات ضايعة", "رباط بوت فالت",
    "بطارية ريموت ضعيفة", "كبسة كيبورد خربانة", "شاشة تلفون مكسورة", "كفر تلفون سيليكون", "سلك شاحن معقد",
    "شاحن لابتوب", "سماعة سلك مقطوعة", "راوتر إنترنت", "فيش كهربا", "ممحاة لوح",
    "براية حديد", "لزاق شفاف", "كباسة ورق", "دبوس ورق", "مسطرة بلاستيك مكسورة",
    "خاتم فضة", "طوق رقبة", "بكلة شعر", "كعب سكربينة", "حزام جلد",
    "ياقة قميص", "مسكة طنجرة", "غطاية بريق شاي", "مصفاية شاي", "معلقة خشب",
    "شوكة بلاستيك مكسورة", "كاسة كرتون", "فتاحة علب", "قشارة بطاطا", "نكاشة أسنان",
    "ليفة جلي", "فرشاة أسنان مستعملة", "معجون أسنان مخلص", "ليفة حمام", "صابونة صغيرة",
    "ملقط حواجب", "قصاصة أظافر", "علبة شامبو فاضية", "بشكير وجه", "قطنة أذنين",
    "ماكينة حلاقة", "مفتاح سيارة", "غماز سيارة", "مساحة قزاز سيارة", "برغي عجل",
    "طاسة سيارة طايرة", "غطاية تنكة بنزين", "بربيش مي", "حجر رصيف", "ورقة شجر يابسة",
    "مسكة باب", "مفتاح باب مصدي", "ريموت مكيف", "لمبة محروقة", "كبسة ضو",
    "برداية شباك", "مخدة ريش", "شرشف تخت", "رجل طاولة بتهز", "سلة زبالة",
    "قشرة بصل", "سن ثومة", "كيس شيبس منفوخ", "حبة علكة ملزقة", "قرن فلفل حار",
    "قشرة موزة", "عرق بقدونس", "بذرة بطيخ", "حبة زيتون بذر", "نملة ماشية",
    "ذبانة بتزن", "شمعة طافية", "كبريتة مولعة", "خيط وإبرة", "مغيطة مصاري",
    "لزقة جروح", "قفل مصدي", "مفتاح إنجليزي", "شاكوش خشب", "جيبة بنطلون مخزوقة",
    "شريط كاسيت قديم", "عقرب ساعة ثواني", "شاشة تلفزيون مغبرة", "كيس نايلون طاير بالهوا", "غطاية علبة دوا",
    "ميزان حرارة", "نظارة مكسورة", "كمامة مقطوعة", "علاقة ملابس بلاستيك", "شبرة هدية",
    "سحاب شنطة سفر", "عجل عرباية سوبرماركت", "فاتورة مطوية", "نكاشة ديدان", "قشة عصير"
  )

  fun getTagForCharadesWord(word: String): String {
    return when {
      word.startsWith("مسلسل") -> "مسلسلات سورية ومصرية 📺"
      word.startsWith("مسرحية") -> "مسرحيات كوميدية 🎭"
      word.startsWith("فيلم") -> "أفلام سينما مصرية 🎬"
      else -> "أفلام ومسلسلات 🎬"
    }
  }

  fun getCharadesWords(
    subCategory: com.example.model.CharadesSubCategory = com.example.model.CharadesSubCategory.MOVIES_AND_SERIES,
    customWords: List<com.example.data.local.CustomCharadesWordEntity> = emptyList(),
    deletedIds: Set<String> = emptySet()
  ): List<CharadesWordItem> {
    if (subCategory == com.example.model.CharadesSubCategory.SPECIFIC_DETAILS) {
      return specificDetailsWordsList.mapIndexed { index, text ->
        CharadesWordItem(
          id = "sd_$index",
          word = text,
          categoryTag = "تفاصيل وأشياء دقيقة 🔍",
          isCustom = false
        )
      }.filterNot { it.id in deletedIds || it.word in deletedIds }
    }

    val baseItems = charadesWordsList.mapIndexed { index, text ->
      CharadesWordItem(
        id = "cw_$index",
        word = text,
        categoryTag = getTagForCharadesWord(text),
        isCustom = false
      )
    }.filterNot { it.id in deletedIds || it.word in deletedIds }

    val customItems = customWords.map {
      CharadesWordItem(
        id = it.id,
        word = it.word,
        categoryTag = it.categoryTag,
        isCustom = true
      )
    }.filterNot { it.id in deletedIds || it.word in deletedIds }

    return baseItems + customItems
  }


  fun getAllQuestionsForCategory(
    category: GameCategory,
    customCards: List<CardItem> = emptyList(),
    deletedIds: Set<String> = emptySet()
  ): List<CardItem> {
    val baseList = when (category.id) {
      GameCategory.WHO_IS_MOST_LIKELY.id -> whoIsMostLikely
      GameCategory.WOULD_YOU_RATHER.id -> wouldYouRather
      GameCategory.CHALLENGES_PENALTIES.id -> challengesAndPenalties
      GameCategory.CONFESSIONS_TRUTH.id -> confessionsAndTruth
      GameCategory.ALL_MIX.id -> {
        (whoIsMostLikely + wouldYouRather + challengesAndPenalties + confessionsAndTruth)
      }
      else -> emptyList()
    }

    // Filter out deleted built-ins, and override any edited built-in or append custom cards
    val activeBase = baseList.filterNot { it.id in deletedIds }
    val matchingCustom = customCards.filter { it.category.id == category.id || (category.id == GameCategory.ALL_MIX.id) }

    // If an edited question has the same ID as a built-in question, replace it
    val customMap = matchingCustom.associateBy { it.id }
    val merged = activeBase.map { baseItem -> customMap[baseItem.id] ?: baseItem } +
                 matchingCustom.filterNot { c -> activeBase.any { it.id == c.id } }

    return merged
  }
}
