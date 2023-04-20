package ksu.katara.healthymealplanner.model.dietTips

import ksu.katara.healthymealplanner.exceptions.DietTipsNotFoundException
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTipDetails
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTipsChapter
import ksu.katara.healthymealplanner.tasks.SimpleTask
import ksu.katara.healthymealplanner.tasks.Task
import java.util.concurrent.Callable
import kotlin.properties.Delegates

typealias DietTipsChaptersListener = (dietTipsChapters: List<DietTipsChapter>) -> Unit
typealias DietTipsListener = (dietTips: List<DietTip>) -> Unit

class InMemoryDietTipsRepository : DietTipsRepository {

    private lateinit var dietTipsChapters: MutableList<DietTipsChapter>
    private var dietTipsChaptersLoaded = false
    private val dietTipsChaptersListeners = mutableSetOf<DietTipsChaptersListener>()

    private lateinit var dietTips: MutableList<DietTip>
    private var dietTipsLoaded = false
    private val dietTipsListeners = mutableSetOf<DietTipsListener>()

    private var dietTipsSize by Delegates.notNull<Int>()

    override fun loadDietTipsChapters(): Task<Unit> = SimpleTask {
        Thread.sleep(2000)

        dietTipsChapters = getDietTipsChapters()

        dietTipsChaptersLoaded = true
        notifyDietTipsChaptersChanges()
    }

    private fun getDietTipsChapters(): MutableList<DietTipsChapter> {
        dietTipsChapters = mutableListOf()

        var dietTipId = 0

        for ((dietTipChapterId, dietTipChapterName) in DIET_TIPS_CHAPTER_NAME.withIndex()) {
            val dietTipsList = mutableListOf<DietTip>()

            dietTipsSize = DIET_TIPS_IMAGES.getValue(dietTipChapterName).size
            val dietTipsPhotos = DIET_TIPS_IMAGES.getValue(dietTipChapterName)
            val dietTipsNames = DIET_TIPS_NAMES.getValue(dietTipChapterName)

            (0 until dietTipsSize).map { dietTipIndex ->
                val dietTip = DietTip(
                    id = dietTipId.toLong(),
                    photo = dietTipsPhotos[dietTipIndex],
                    name = dietTipsNames[dietTipIndex],
                )

                dietTipsList.add(dietTip)
                dietTipId++
            }

            val dietTipsChapter = DietTipsChapter(
                id = dietTipChapterId.toLong(),
                name = dietTipChapterName,
                dietTipsList = dietTipsList
            )

            dietTipsChapters.add(dietTipsChapter)
        }

        return dietTipsChapters
    }

    override fun loadDietTips(): Task<Unit> = SimpleTask {
        Thread.sleep(2000)

        dietTips = getDietTips()

        dietTipsLoaded = true
        notifyDietTipsChanges()
    }

    private fun getDietTips(): MutableList<DietTip> {
        dietTips = mutableListOf()

        var dietTipId = 0

        for (key in DIET_TIPS_IMAGES.keys) {
            for (dietTipIndex in DIET_TIPS_IMAGES.getValue(key).indices) {
                val dietTip = DietTip(
                    id = dietTipId.toLong(),
                    name = DIET_TIPS_NAMES.getValue(key)[dietTipIndex],
                    photo = DIET_TIPS_IMAGES.getValue(key)[dietTipIndex],
                )

                dietTips.add(dietTip)
                dietTipId++
            }
        }


        return dietTips
    }

    override fun getDietTipDetailsById(id: Long): Task<DietTipDetails> = SimpleTask(Callable {
        Thread.sleep(2000)

        val dietTip = dietTips.firstOrNull { it.id == id } ?: throw DietTipsNotFoundException()

        return@Callable DietTipDetails(
            dietTip = dietTip,
            background = DIET_TIPS_DETAILS_BACKGROUND.getValue(dietTip.name),
            titlesList = DIET_TIPS_DETAILS_TITLES.getValue(dietTip.name),
            descriptionsList = DIET_TIPS_DETAILS_DESCRIPTIONS.getValue(dietTip.name),
        )
    })

    override fun addDietTipsChaptersListener(listener: DietTipsChaptersListener) {
        dietTipsChaptersListeners.add(listener)
        if (dietTipsChaptersLoaded) {
            listener.invoke(dietTipsChapters)
        }
    }

    override fun addDietTipsListener(listener: DietTipsListener) {
        dietTipsListeners.add(listener)
        if (dietTipsLoaded) {
            listener.invoke(dietTips)
        }
    }

    override fun removeDietTipsChaptersListener(listener: DietTipsChaptersListener) {
        dietTipsChaptersListeners.remove(listener)
    }

    override fun removeDietTipsListener(listener: DietTipsListener) {
        dietTipsListeners.remove(listener)
    }

    private fun notifyDietTipsChaptersChanges() {
        if (!dietTipsChaptersLoaded) return
        dietTipsChaptersListeners.forEach { it.invoke(dietTipsChapters) }
    }

    private fun notifyDietTipsChanges() {
        if (!dietTipsLoaded) return
        dietTipsListeners.forEach { it.invoke(dietTips) }
    }

    companion object {
        private val DIET_TIPS_CHAPTER_NAME = mutableListOf(
            "Фундамент здорового образа жизни",
            "Питание как фактор риска в развитии заболеваний",
            "Восстановление работы ЖКТ",
        )

        private val DIET_TIPS_IMAGES = mutableMapOf(
            DIET_TIPS_CHAPTER_NAME[0] to mutableListOf(
                "https://images.unsplash.com/photo-1490645935967-10de6ba17061?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2353&q=80",
                "https://images.unsplash.com/photo-1614887065001-06c958a7cddd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1656218257936-8384471a0258?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2274&q=80",
                "https://images.unsplash.com/photo-1518611012118-696072aa579a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                "https://images.unsplash.com/photo-1545389336-cf090694435e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1364&q=80",
            ),
            DIET_TIPS_CHAPTER_NAME[1] to mutableListOf(
                "https://images.unsplash.com/photo-1559757148-5c350d0d3c56?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3431&q=80",
                "https://plus.unsplash.com/premium_photo-1671718110418-d25ac8e87627?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1649073586104-2ac3fab175ea?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2074&q=80",
                "https://plus.unsplash.com/premium_photo-1661780215564-b3a919366e6b?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                "https://images.unsplash.com/photo-1559757175-053139280de2?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3431&q=80",
            ),
            DIET_TIPS_CHAPTER_NAME[2] to mutableListOf(
                "https://images.unsplash.com/photo-1543362906-acfc16c67564?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1365&q=80",
                "https://images.unsplash.com/photo-1543362906-acfc16c67564?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1365&q=80",
                "https://images.unsplash.com/photo-1543362906-acfc16c67564?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1365&q=80",
            ),
        )

        private val DIET_TIPS_NAMES = mutableMapOf(
            DIET_TIPS_CHAPTER_NAME[0] to mutableListOf(
                "Режим питания",
                "Вода",
                "Сон",
                "Спорт",
                "Медитация",
            ),
            DIET_TIPS_CHAPTER_NAME[1] to mutableListOf(
                "Мозг",
                "Желудок",
                "Печень и желчный пузырь",
                "Поджелудочная железа и кишечник",
                "Почки",
            ),
            DIET_TIPS_CHAPTER_NAME[2] to mutableListOf(
                "Система пищеварения: ротовая полость, пищевод и желудок",
                "Система пищеварения: печень и желчный пузырь",
                "Система пищеварения: поджелудочная железа и кишечник",
            ),
        )

        private val DIET_TIPS_DETAILS_BACKGROUND = mutableMapOf(
            "Режим питания" to mutableListOf(
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            ),
            "Вода" to mutableListOf(
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            ),
            "Сон" to mutableListOf(
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            ),
            "Спорт" to mutableListOf(
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            ),
            "Медитация" to mutableListOf(
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1487147264018-f937fba0c817?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            ),
            "Мозг" to mutableListOf(
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            ),
            "Желудок" to mutableListOf(
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            ),
            "Печень и желчный пузырь" to mutableListOf(
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            ),
            "Поджелудочная железа и кишечник" to mutableListOf(
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            ),
            "Почки" to mutableListOf(
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "https://images.unsplash.com/photo-1556262298-e85892643712?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            ),
            "Система пищеварения: ротовая полость, пищевод и желудок" to mutableListOf(
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
            ),
            "Система пищеварения: печень и желчный пузырь" to mutableListOf(
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
            ),
            "Система пищеварения: поджелудочная железа и кишечник" to mutableListOf(
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
                "https://images.unsplash.com/photo-1523766775147-152d0d6e2adb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
            )
        )

        private val DIET_TIPS_DETAILS_TITLES = mutableMapOf(
            "Режим питания" to mutableListOf(
                "Подготовка к приему пищи",
                "Приемущества тщательного пережевывания",
                "Во время приема пищи",
                "Дробное питание: за и против?",
                "Базовые принципы здорового питания",
                "Формируем корзину из цельных продуктов",
                "Планируем рацион",
                "Как облегчить приготовление пищи",
            ),
            "Вода" to mutableListOf(
                "Признаки того, что вы пьете мало воды",
                "Характеристики воды",
                "pH воды",
                "Плюсы воды с отрицательным ОВП",
                "Средства для изменения качества воды",
            ),
            "Сон" to mutableListOf(
                "Сон - лучшее лекарство",
                "Мелатонин - anti-age гормон",
                "Рекомендации при проблемах со сном",
            ),
            "Спорт" to mutableListOf(
                "Рекомендации",
                "Дыхательные практики",
            ),
            "Медитация" to mutableListOf(
                "Рекомендации",
                "Виды медитаций",
            ),
            "Мозг" to mutableListOf(
                "Связь питания и заболеваний",
                "Заболевания, связанные с нейровоспалением",
                "Последствия нейровоспаления",
                "Сахар и продукты гликации",
                "Способы коррекции с помощью питания",
            ),
            "Желудок" to mutableListOf(
                "Последствия низкой кислотности желудочного сока",
                "Причины пониженой кислотности желудочного сока",
                "Способы коррекции с помощью питания и привычек",
            ),
            "Печень и желчный пузырь" to mutableListOf(
                "Важно",
                "Основные функции печени",
                "Токсины",
                "Для поддержания и восстановления печени",
                "Заболевания, возникающие из-за нарушения работы желчного пузыря",
                "Заболевания, возникающие из-за нарушения усвоения жиров и жирорастворимых витаминов",
                "Главные функции желчи",
                "Полипы в желчном пузыре",
                "Причины образования камней в желчном пузыре",
                "Коррекция с помощью питания",
            ),
            "Поджелудочная железа и кишечник" to mutableListOf(
                "Панкреатит",
                "Инсулинорезистентность",
                "Причины инсулинорезистентности",
                "Коррекция с помощью питания",
                "Тонкий кишечник",
                "Работа с дисбиозом",
            ),
            "Почки" to mutableListOf(
                "Причины заболевания почек и образования камней",
                "Коррекция с помощью питания",
            ),
            "Система пищеварения: ротовая полость, пищевод и желудок" to mutableListOf(
                "Основная задача пищеварения",
                "Процесс пищеварения",
                "Важно",
                "Функции соляной кислоты",
                "Как определить уровень соляной кислоты",
                "Симптоматика низкой кислотности",
                "Дополнительные способы определения кислотности",
                "Признаки гиперацидности",
                "Рекомендации при повышенной кислотности и ГЭРБ",
                "Рекомендации при низкой кислотности",
            ),
            "Система пищеварения: печень и желчный пузырь" to mutableListOf(
                "Функции печени",
                "Синдром Жильбера",
                "Симптомы синдрома Жюльбера",
                "Основная задача при синдроме Жюльюера",
                "Вещества(добавки и питание), способствующие активации глюкоронозилтрансферазы",
                "Диагностика работы печени по лабораторным анализам",
                "Детокс питание для улучшения работы печени",
                "Исключаем на детоксе",
                "Что употреблять на детоксе",
                "Фазы детокса",
                "Продукты, активизирующие первую фазу детокса",
                "Микроэлементы, необходимые для работы первой фазы детокса",
                "Микроэлементы, необходимые для работы второй фазы детокса",
                "Печень синтезирует желчь",
                "Функции желчи",
                "Симптоматика проблем с желчным пузырем",
                "Причины нарушения желчеоттока",
                "Рекомендации для нормализации желчеоттока",
            ),
            "Система пищеварения: поджелудочная железа и кишечник" to mutableListOf(
                "Поджелудочная железа",
                "Панкреатические ферменты для пищеварения",
                "Восстановление выработки ферментов поджелудочной железы",
                "Функции тонкого кишечника",
                "Типы тищеварения в тонком кишечнике",
                "Рекомендации для восстановления слизистой кишечника и микрофлоры",
                "Выработка ферментов различнымт органами ЖКТ",
            ),
        )

        private val DIET_TIPS_DETAILS_DESCRIPTIONS = mutableMapOf(
            "Режим питания" to mutableListOf(
                "- Пищеварение начинается с мысли о еде\n" +
                        "- Едим по голоду, не заедаем стресс\n" +
                        "- Внешний вид еды играет важную роль\n" +
                        "- Не есть на ходу, стоя\n" +
                        "- Есть за столом в спокойном состоянии, не испытывая стресс" +
                        "- Не использовать гаджены во время приема пищи\n" +
                        "- Для лучшего контроля тщытельного пережевывания:" +
                        "откусите кусочек, положите вилку на стол. Тщательно пережуйте." +
                        "Возьмите вилку и положите следующую порцию еды в рот",
                "- В процессе тщательного пережевывания выделяется слюна, в которой содержатся" +
                        "ферментыб помогающие расщеплять углеводы(особенно важно тем, у кого" +
                        "есть проблемы с поджелудочной железой)\n" +
                        "- В слюне содержатся антибактериальные вещества, помогающие боротся" +
                        "с вирусами и бактериями\n" +
                        "- Чем больше выделяется слюны, тем больше выделяется соляной кислоты и " +
                        "желчи для лучшего пищеварения\n" +
                        "- Происходит более быстрое чувство насыщения",
                "- Во время еды не пьем никакие напитки, кроме теплой воды(не более 100-150 мл," +
                        " 38-40 градусов)\n" +
                        "- В каждый прием пищи включаем овощи и зелень",
                "- Дробное питание может приводить к заболеваниям\n" +
                        "- В зависимости от состояния ЖКТ и сопутствующих заболевания, фрукты необходимо " +
                        "употреблять либо за 15-20 минут до еды, либо после еды\n" +
                        "- Промышленные сладости и сахар исключаются\n" +
                        "- Любые десерты едим после основного приема пищи, не на перекус\n" +
                        "- Можно между приемами пищи оставить чай, травяной чай, цикорий, какао, кофе, если " +
                        "разрешены эти напитки на лечебном протоколе. Этот напиток не должен быть с каким-то " +
                        "десертом, иначе это уже будет прием пищи\n",
                "- Исключить добавленный сахар\n" +
                        "- Читать составы\n" +
                        "- Минимизировать/исключить обработанные проодукты(фаст-фуд, полуфабрикаты, пельмени, " +
                        "колбасы, сосиски и другое)\n" +
                        "- Использовать цельные продукты\n" +
                        "- Не жарить блюда до коричневой корочки(лучше тушить, готовить на пару, запекать)\n" +
                        "- Убрать все продукты воспаления из дома, чтобы не было соблазнов\n" +
                        "- Выбирать простые блюда на первом этапе\n" +
                        "- Делать упор на разнообразие рациона, а не на сложность блюда\n",
                "- Мясо, субпродукты, птица, рыба, морепродукты, яйца\n" +
                        "- Овощи, зелень, ягоды, фрукты\n" +
                        "- Бобовые: нут, маш, чечевица\n" +
                        "- Злаки: гречка, рис, амарант, кеноа\n" +
                        "- Нерафинированные масла, семена, орехи\n",
                "- Яйца могут быть через день\n" +
                        "- Красное мясо 2 раза в неделю\n" +
                        "- Птица 3 раза в неделю\n" +
                        "- Рыба и морепродукты 2 раза в неделю(можно чаще)\n" +
                        "- Обязательно включайте в рацион субпродукты(печень, сердечки и другое)\n" +
                        "- Каджый день включайте разные злаки и бобовые(если разрешены на лечебном протоколе)\n" +
                        "- Каждый день новый овощ, зелень: различные виды капусты(белокочанная, цветная, " +
                        "брюссельская, кольраби), свекла, морковь и другое\n",
                "- Заранее сварить крупу и хранить в стеклянном контейнере в холодильнике\n" +
                        "- Купить консервы без сахара и масла(сардины, тунец, скумбрия, кукуруза, горох)\n" +
                        "- Купить замороженные овощи\n" +
                        "- Сварить заранее яйца\n" +
                        "- Заранее приготовить мясо, субпродукты, паштеты и другое\n" +
                        "- Заморозить костные и овощниые бульоны\n" +
                        "- Пользоваться мультиваркой, пароваркой\n",
            ),
            "Вода" to mutableListOf(
                "- Жажда и сухость во рту\n" +
                        "- Моча темно-желтого цвета\n" +
                        "- Нерегулярный стул\n" +
                        "- Сухая кожа, поменялся цвет лица\n" +
                        "- Боли в суставах\n" +
                        "- Образование целлюлита\n" +
                        "- Неприятный запах пота\n" +
                        "! Норма воды в день: 30 мл на 1 кг идеального веса\n",
                "- Температура воды:\n" +
                        "Пейте теплую воду(38-40 градусов) или хотя бы комнатной температуры." +
                        "Начинайти пить постепенно, небольшими глоточками\n" +
                        "- Ph воды:\n" +
                        "В среднем pH организма варируется от 7-7.5, поэтому вода не должна" +
                        "быть ниже 7.5(а лучше 7.5-8.5). Воду с pH 9-10 не пьем на постоянной основе, " +
                        "она показана только во время болезни, при острых инфекционных заболеваниях\n" +
                        "- Окислительно-восстановительный потенциал(ОВП)\n" +
                        "ОВП - это активность электронов, участвующих в окислительно-восстановительной " +
                        "реакции. ОВП организма 100-200милливольт, а питьевая вода имеет ОВП от " +
                        "+200 до +300 милливольт. Чтобы организм не тратил энергию на изменение ОВП, " +
                        "нужно использовать качественную воду\n",
                "Для определния pH воды можно использовать pH-полоски и pH-метры. Для измерения pH характиристик" +
                        "можно брать кипяченную, фильтрованную, диистиллированную воду",
                "- Стабилизация уровня глюкозы в крови\n" +
                        "- Восстановление эластичности сосудов\n" +
                        "- Восстановление давления\n" +
                        "- Выводит свободные радикалы из организма\n" +
                        "- Стимулирует функции пищеварительной системы\n" +
                        "- Улучшает обмен веществ\n",
                "- Эфирные масла цитрусовых(1 капля на стакан воды) - " +
                        "лимон, грейпфрут, апельсин, мандарие и другое\n" +
                        "- Минеральные концентраты\n" +
                        "- Электролитическая минеральная добавка Coral mine\n" +
                        "- Водородные бутылки, кувшины\n" +
                        "- Ионизаторы\n",
            ),
            "Сон" to mutableListOf(
                "Совы - это не выспавшиеся жаворонки.\n" +
                        "Мелатонин - гормон сна и его выработка начинается в 19 часов и " +
                        "достигает пика к 23. Восстановление организма происходит с 23 до 1 ночи, " +
                        "поэтому важно спать в это время!\n" +
                        "Готовимся ко сну с 22, в 23 уже спим",
                "- Обладает антивозрастным эффектом\n" +
                        "- Улучшает иммунитет\n" +
                        "- Восстанавливает уровень лептина(гормона насыщения)\n" +
                        "- Улучшает работу клеток головного мозга\n" +
                        "- Предотвращает развитие опухолей\n",
                "- Обязательно завтракаем\n" +
                        "- Не едим за 3-4 часа до сна(не позднее 7 часов вечера)\n" +
                        "- Если есть проблемы с храническим стрессом, можно сделать легкий " +
                        "перекус без углеводов за 1 час до сна\n" +
                        "- Прием магниевых ванн за 40 минут до сна(вода не выше 40 градусов)\n" +
                        "- Температура в комнате в районе 21 градуса\n" +
                        "- Беруши\n" +
                        "- Маска для сна\n" +
                        "- Плотные шторы\n" +
                        "- Ставим ночной режим на гаджеты\n" +
                        "- Специальные очки для изменения спектра света\n" +
                        "- Медитации\n",
            ),
            "Спорт" to mutableListOf(
                "- Проходите в день минимум 10000 шагов\n" +
                        "- Подбирайте физическую активность по силам и по самочуствию\n" +
                        "- Спорт влияет на работу блуждающего нерва\n" +
                        "- Тонус мышц тазового дна зависит от диафрагмального дыхания\n" +
                        "- Нет правил по приему пищи при занятиях спортом\n",
                "- Стрельниковой\n" +
                        "- Бутейко\n" +
                        "- Пранаяма\n",
            ),
            "Медитация" to mutableListOf(
                "- Выберите удобное время\n" +
                        "- Выберите тихое место\n" +
                        "- Сядьте удобно\n" +
                        "- Сохраняйте желудок полупустым\n" +
                        "- Начните с разминки\n" +
                        "- Сделайте несколько глубоких вдохов и выдохов\n" +
                        "- Сохраняйте на лице мягкую улыбку\n" +
                        "- Открывайте глаза медленно и постепенно\n",
                "- Медитация Випассана\n" +
                        "- Медитация Шаматха\n" +
                        "- Трансцендентальная медитация\n" +
                        "- Медитация на чакры\n" +
                        "- Медитация в йоге\n" +
                        "- Дзен-медитация\n",
            ),
            "Мозг" to mutableListOf(
                "Еда напрямую влияет на ваше здоровье.\n" +
                        "Развитие заболеваний зависит от\n" +
                        "- Качества потребляемой еды\n" +
                        "- Ее усвоения\n" +
                        "- Количества консервантов, канцерогенов и токсинов в еде" +
                        "- Дефицитов из-за неполноценного и рационального питания",
                "- Болезнь Паркинсона\n" +
                        "- Рассеяный склероз\n" +
                        "- Болезнь Альцгеймера\n" +
                        "- Болезнь Хантингтона\n",
                "- Потеря памяти\n" +
                        "- Деменция\n" +
                        "- Нарушение когнитивных способностей\n" +
                        "- Усталость\n" +
                        "- Депрессия\n" +
                        "-Тремор\n" +
                        "- Скованность мышц и суставов\n" +
                        "- Трудность с речью\n",
                "Сахар и продукты гликации нарпямую влияют на развитие нейродегенеративных заболеваний!\n\n" +
                        "Продукты гликации: выпечка, промышленные сладости, газировка, пиво, алкоголь, " +
                        "глазурь, жаренное до черной корки и другое.\n\n" +
                        "Сахар\n" +
                        "- Повышает уровень глюкозы и инсулина\n" +
                        "- Способствует накоплению жировых отложений\n" +
                        "- Способствует гликации белков в организме(катаракта, атеросклероз)\n" +
                        "- Вызывает мигрени, депрессии, ПМС\n\n" +
                        "На переработку глюкозы тратятся:\n" +
                        "- Витамины группы В\n" +
                        "- Магний\n" +
                        "- Витамин С\n" +
                        "- другие витамины и минералы\n",
                "- Исключить простые углеводы и добавленные сахара\n" +
                        "- Включить полезные омега-3 жиры(форель, скумбрия, сельдь, печень трески, " +
                        "яйца, субпродукты), растительные источники омега-3(льняное, коноплянное, оливковое масло)\n" +
                        "- Включить некрахмалистые овощи(все виды капусты, зелень и другое)\n" +
                        "- Включить качественный белок(домашние яйца, фермерское мясо и субпродукты, дикая рыба), " +
                        "кроме молочной продукции\n",
            ),
            "Желудок" to mutableListOf(
                "- Снижение уровня общего белка, аминокислот\n" +
                        "- Камни, полипы в желчном, застойжелчи\n" +
                        "- Железодефицит\n" +
                        "- Анемия\n" +
                        "- Гипо и гипертиреоз(гормоны щитовидной железы - белковые структуры)\n" +
                        "- Заболевания кожи(розация, купероз, экзема, атопический дерматит, акне и другое)\n" +
                        "- Различные дефициты(кальций, цинк, медь, РР, В, магний и другие)\n" +
                        "- Дисбиоз кишечника\n\n" +
                        "По ФГДС уровень кислотности определнить нельзя, измерять кислотность необходимо с " +
                        "помощью суточной рН-метрии",
                "- Неправильные пищевые привычки(не качественное пережевывание пищи, прием пищи в состоянии " +
                        "стресса и прочее)\n" +
                        "- Нарушение работы блуждающего нерва\n" +
                        "- Прием лекарственных препаратов(антибиотики, КОК, НПВС, ИПП)\n" +
                        "- Употребление сахара\n" +
                        "- Употребление молочной продукции\n" +
                        "- Дефициты витаминов и минералов(хром, марганец, цинк, медь, В и другие)\n\n" +
                        "При низкой кислотности и дефиците белка сфинктеры не смыкаются из-за сниженной " +
                        "мышечной моторики. Поэтому при ГЭРБ и изжоге кислотность, зачастую, снижена.\n",
                "- Медленное и тщательное пережевывание пищи\n" +
                        "- Употребление теплой воды\n" +
                        "- Заваривание горьких трав(полынь, одуванчик, лопух, расторопша и другое)\n" +
                        "- Заживление слизистых\n" +
                        "- Употребление минеральной воды, выпуская газы(Есентуки 17)\n" +
                        "- Подсаливать пищу(2/3 чайной ложки) натуральной морской, розовой, гималайской солью\n" +
                        "- Включение клетчаткиЖ овощи и зелень\n" +
                        "- Добавление белка(животного и растительного) - соляная кислота вырабатывается на белок\n" +
                        "- Прием сока квашенной капусты/сока сильдерея на тощак(не более 50мл за 15-30 минут до еды)\n\n" +
                        "Сляная кислота не допускает разрастания хеликобактер пилори, поэтому нельзя допускать ее снижения\n",
            ),
            "Печень и желчный пузырь" to mutableListOf(
                "Холестерин на 70% синтезируется в печени и лишь на 30% мы получаем извне\n\n" +
                        "1. Детоксикация\n" +
                        "Печень переводит токсины из жирорастворимой формы в водорастворимую\n\n" +
                        "2. Метаболизм стероидных гормонов\n" +
                        "3. Синтез Холестерина(с помощью холестерина синтезируется желчь)\n",
                "- Консерванты и канцерогены\n" +
                        "- Тяжелые металлы\n" +
                        "- Лекарственные препараты\n" +
                        "- Алкоголь\n",
                "Убираем:\n" +
                        "- Сахар\n" +
                        "- Молоко\n" +
                        "- Глютен\n" +
                        "Включаем:\n" +
                        "- Овощи и зелень\n" +
                        "- Ягоды\n" +
                        "- Орехи, семена\n",
                "- Билиарный сланж\n" +
                        "- Взвесь\n" +
                        "- Застой желчи\n" +
                        "- Полипы и камни\n" +
                        "- Холецистит\n" +
                        "- ДЖВП\n",
                "- СИБР, СИГР\n" +
                        "- Кандидоз\n" +
                        "- Цистит\n" +
                        "- Геморой, варикоз\n",
                "- Мигрени\n",
                "- Заболевания кожи(сухость, фоликулярный кератоз, акне, экзема, дерматит)\n" +
                        "- Ухудшение зрения\n" +
                        "- Остеопороз\n" +
                        "- Слабый иммунитет\n",
                "- Усвоение жиров и жирорастворимых витаминов\n" +
                        "- Санация и обеззараживание\n" +
                        "- Вывод токсинов из организма\n" +
                        "- Формирование каловых масс\n" +
                        "- Регуляция моторики кишечника\n",
                "Если полип больше 1см, то его рекомендуется удалять\n\n" +
                        "Причины возникновения:\n" +
                        "- Воспаление слизистой жулчного пузыря(из-за сгущения желчи, " +
                        "неправильной ее консистенции)\n" +
                        "- Вирусная и бактериальная инфекция\n" +
                        "- Нарушение обмена холестерина\n" +
                        "- Дефицит витаминов группы В и йода\n" +
                        "- Прием КОК\n",
                "- Заболевания печени и желчного пузыря\n" +
                        "- Нарушение консистенции и качества желчи\n" +
                        "- Избыток простых углеводов и сахаров\n" +
                        "- Инсулинорезистентность, ожирение, сахарный диабет, подагра\n" +
                        "- Дефицит магния, витаминов группы В\n" +
                        "- Малоподвижный образ жизни\n" +
                        "- Нарушенный водный режим\n\n" +
                        "! Если камни занимают более 50% желчного пузыря, то желчный удаляется\n",
                "- Прием пищи в расслабленном состоянии\n" +
                        "- Употретребление теплой воды\n" +
                        "- При запорах прием воды Донат магния(в теплом виде без газов, если нет камней)\n" +
                        "- Прием трав - горечи\n" +
                        "- Включение в рацион имбиря, цитрусовых(лимон, лайм), нефильтрованного яблочного " +
                        "уксуса, сырой свеклы\n" +
                        "- Употребление ферментированных продуктов(квашенной капусты, редьки). Если " +
                        "вздутие, то употреблять сок\n" +
                        "- Добавление клетчатки в каждый прием пищи\n" +
                        "- Добавление полезных жиров в каждый прием пищи(нерафинированные масла, семена, МСТ масло)\n",
            ),
            "Поджелудочная железа и кишечник" to mutableListOf(
                "Панкреатит - это воспаление поджелудочной железы\n\n" +
                        "Причины:\n" +
                        "- Воспалительные продукты(сахар, простые углеводы, острая, жареная, жирная пища)\n" +
                        "- Алкоголь\n" +
                        "- Стресс\n" +
                        "- Патологии желчевыводящих путей\n" +
                        "- Токсичные вещества, канцерогены\n" +
                        "- Инсулинорезистентность\n",
                "Инсулинорезистентность - это предшественник сахарного диабета 2 типа\n" +
                        "Последствия:\n" +
                        "- Лишний вес\n" +
                        "- Сердечно-сосудистые заболевания\n" +
                        "- Метаболический синдром\n" +
                        "- Атеросклероз, инфаркт, инсульт\n" +
                        "- СПКЯ\n" +
                        "- Хроническое воспаление\n" +
                        "- Акне, жирный блеск кожи\n" +
                        "- Выпадение волос\n" +
                        "- Хроническая усталость\n",
                "- Избыток простых углеводов и сахара\n" +
                        "- Дробное питание\n" +
                        "- Переизбыток фруктозы(более 30 грамм в сутки)\n" +
                        "- Гиподинамия\n" +
                        "- Поздний ужин\n",
                "- Исключение сахара и простых углеводов\n" +
                        "- Трехразовое питание\n" +
                        "- Ужин за 3-4 часа до сна\n" +
                        "- Физическая активность\n" +
                        "- Добавление некрахмалистых овощей и зелени\n" +
                        "- Добавление жиров\n" +
                        "Увеличение промежутков между ужином и завтраком(14-16 часов)",
                "Если у вас низкая кислотность, нарушение желчеоттокати низкая ферментативная активность, " +
                        "то патогенная микрофлора быстро размножается\n\n" +
                        "Симптомы и заболевания, вссоциированные с нарушением микрофлоры:\n" +
                        "- Запоры, вздутия, гозообразования\n" +
                        "- Акне, шелушение, экзема, атопический дерматит, аллергические реакции\n" +
                        "- Аутоиммунные заболевания\n" +
                        "- Заболевания мочеполовой системы(цистит, кандидоз, простатит и другое)\n" +
                        "- Депрессия, повышенное чувство тревоги и страха\n\n" +
                        "! 95% нейромедиаторов берут свое начало в кишечнике(серотонин, ГАМК и прочее)\n" +
                        "80% иммунитета содержится в кишечнике\n",
                "- Восстановление кислотности, желчеоттока, работы поджелудочной железы\n" +
                        "- Исключение продуктов воспаления(сахар, молочная и кисломолочная продукция, глютен)\n" +
                        "- Включение в рацион костного бульона\n" +
                        "- Употребление овощей и зелени\n",
            ),
            "Почки" to mutableListOf(
                "- Нарушение работы печени и желчного пузыря\n" +
                        "- Дефицит витамина Д, витамина К\n" +
                        "- Переизбыток красного мяса и фруктов\n" +
                        "- Дефицит витаминов группы В и повышенный уровень гомоцистеина\n" +
                        "- Заболевания ЖКТ\n" +
                        "- Гиподинамия\n" +
                        "- Дефицит воды\n" +
                        "- Нарушение кислотно-щелочного баланса",
                "- Теплая вода\n" +
                        "- Овощи, зелень\n" +
                        "- Исключение сахара, рафинированных углеводов, копченого, острого, жареного, соусов, кофе, чая\n",
            ),
            "Система пищеварения: ротовая полость, пищевод и желудок" to mutableListOf(
                "- усвоение белков, жиров и углеводов, витаминов и минералов\n\n" +
                        "В пищеварительном тракте происходит:\n" +
                        "- переваривание\n" +
                        "- извлечение питательных веществ\n" +
                        "- всасывание в кровь\n" +
                        "- выведение непереваренных остатков\n",
                "Пищевод\n" +
                        "Стенки пищевода вырабатывают слизь для проталкивания пищевого комка. " +
                        "Для того, чтобы выделилась слизь, необходимо тщательно жевать пищу\n\n" +
                        "Желудок\n" +
                        "Функции жеудка:\n" +
                        "- Механическое переваривание еды\n" +
                        "- Химическое переваривание пищи\n" +
                        "- Под действием соляной кислоты, а также фермнта пепсин и липаза, " +
                        "пищевой комок превращается в химус\n\n" +
                        "Образование химуса происходит:\n" +
                        "углеводная пища - 2 часа\n" +
                        "белковая пища - 4 часа\n" +
                        "жированя пища - 5-6 часов\n",
                "В верхней части желудка происходит образование соляной кислоты, в нижней части вырабатывается " +
                        "щелочь для нейтролизации кислоты\n\n" +
                        "В желудке кислая среда, pH должен быть от 1,5 до 2",
                "- Переваривание белков\n" +
                        "Белки являются переносчиками витаминов, гемоглобин и ферритин являются белковыми " +
                        "молекулами, без достаточного уровня белка не повысить железа\n\n" +
                        "- Усвоение витаминов и минералов\n" +
                        "К ним относится железо, витамин С, медь, цинк, витамин В12. В желудке вырабатывается слизь, " +
                        "которая содержит фактор Касла, который обеспечивает усволение витамина В12\n\n" +
                        "Защитная фцнкция\n" +
                        "В желудке вырабатывается лизоцим вместе с соляной кислотой, который является сильным " +
                        "противовирусным и бактерицидным барьером, защищает от личинок гельминтов и паразитов\n",
                "- ФГДС не является информативным методом, так как определяет уровень соляной кислоты в моменте\n" +
                        "- Суточная pH-метрия самый достоверный способ, где в течении суток специальным прибором " +
                        "измеряется уровень кислотности с шагом в час\n" +
                        "- Гастропанель по крови. Определяется уровень пепсина 1,2 уровень гастрина 17 и антител к " +
                        "хеликобактов пилори. Гастропанель всегда должна оцениваться комплексно с копрограммой, " +
                        "уровнем общего белка и симптоматикой\n" +
                        "- Капсульная эндоскопия. Происходит оценка всей пищеварительной системы, но в России не распространена\n",
                "- Изжога\n" +
                        "- Отрыжка\n" +
                        "- Запах изо рта\n" +
                        "- Отрыжка с серным привкусом\n" +
                        "- Аллергия\n" +
                        "- Розацея\n" +
                        "- Купероз\n" +
                        "- Выпадение волос\n" +
                        "- Ломкость ногтей\n" +
                        "- Железодефицит\n",
                "- Тест с содой\n" +
                        "Утром на тощак 0,5 чайной ложки растворить в 100мл воды и выпить, смотреть, через " +
                        "какое время произойдет отрыжка. Через 2-3 минуты - соляной кислоты достаточно. " +
                        "Если отрыжка возникает через 3 минуты и более - недостаточная выработка соляной кислоты." +
                        "Выполнить тест в динамике 3-4 дня. При дискомфорте в желудке не повторять.\n\n" +
                        "- Копрограмма. Показатели низкой кислотности:\n" +
                        "- Если показатель щелочной среды pH выше 7,5-8\n" +
                        "- Остатки непереваренной пищи\n" +
                        "- Мышечные волокна с исчерченностью и без исчерченности\n" +
                        "- Соединительная ткань\n\n" +
                        "Показатели низкой кислотности в лабораторныз анализах\n" +
                        "- Уровень общего белка ниже 73-75 г/л\n" +
                        "- Уровень ферритина ниже 50нг/мл\n" +
                        "- Дефицит витамина В12\n",
                "- Изжога\n" +
                        "- Кислота во рту\n" +
                        "- Сопоставлять симптомы с лабораторной диагностикой\n\n" +
                        "Хеликобактер пилори\n" +
                        "Бактерия, которая живет с нами миллионы лет. Важно, чтобы она не разрасталась и не " +
                        "повреждала слизистую желудка. Один из факторов избыточного размножения Хеликобактор пилори - " +
                        "снижение соляной кислоты. Коррекция подбирается строго индивидуально\n",
                "Убрать из рациона на 2-3 недели:\n" +
                        "- Кофеин\n" +
                        "- Кислые ягоды\n" +
                        "- Цитрусовые\n" +
                        "- Пасленовые(томаты, перец, баклажан, картофель)\n\n" +
                        "Нутрицевтики для восстановления слизистой желудка(4-6 недель)\n" +
                        "- Алоэ вера\n" +
                        "- Мастиковая смола\n" +
                        "- Ржавый вяз\n" +
                        "- Цинка карнозин\n" +
                        "- Комплексные препараты\n",
                "Исключить из рациона\n" +
                        "- сахар\n" +
                        "- рафинированные углеводы\n" +
                        "- молочные и кисломолочные продукты\n\n" +
                        "Тщательно пережевывать\n\n" +
                        "Концентрироваться на процессе приема пищи\n\n" +
                        "Включить в рацион\n" +
                        "- Сок сельдерея или квашенной капусты за 10-15 минут до приема пищи\n" +
                        "- Воды с лимоном или яблочным нефольтрованным уксусом за 30 минут до еды. " +
                        "Яблочный нефильтрованный уксус хорошо работает с патогенной флорой, в том числе с " +
                        "кандидозом. Начинать дозировку необходимо с 1 чайной ложки на один стакан воды, постепенно " +
                        "увеличивая дозировку до 4 чайных ложек, пока не начнете испытывать дискомфорт. Яблочный уксус " +
                        "в капсулах подходит при чувствительности эмали зубов, их удобно брать с собой. Пить по " +
                        "1-2 капсулы во время еды, в которой присутвтвует белок\n" +
                        "- Горечи - одуванчик, тысячилистник, лопух, ромашка. Заваривать как травяные чаи.\n\n" +
                        "Бетаин пепсин относится к сильным нутрицевтикам. Помогает усвоению белка. При неправильном применении " +
                        "может повредить слизистую желудка. Применение строго обсуждать с врачом\n",
            ),
            "Система пищеварения: печень и желчный пузырь" to mutableListOf(
                "1. Детоксификационная\n" +
                        "Печень - один из главных органов детоксикации. Все поступающие токсичные вещества " +
                        "печень переводит из жирорастворимых в водорастворимую. Дальше с помощью почек и мочи, " +
                        "желчи и кала, воды и пота выводит из организма. К токсинам относятся: пары краски, лекарства, " +
                        "алкоголь, кофеин, консерванты и канцерогены из пищи, тяжелые металлы из воздуха, пестициды\n\n" +
                        "Защитная\n" +
                        "Холестерин на 70% производит печень. Повышенный уровень холестерина - повод обратить внимание на " +
                        "печень. При стрессе перень будет вырабатывать повышенное количество холестерина для обеспесения " +
                        "потребности организма в ситнезе картизола\n\n" +
                        "Синтез желчи\n" +
                        "Желчь участвует в расщеплении жиров и жирорастворимых витаминов. Для их усвоения надо обратить " +
                        "внимание на работу печени, чтобы печень могла давать нужное количество холестерина для синтеза желчи\n\n" +
                        "Регулирование пигментного обмена\n" +
                        "Печень отвечает за выведение повышенного количества билирубина. Пигментация, пожелтение кожи, желтые " +
                        "склеры - признак нарушения работы печени\n\n" +
                        "Синтез белков и нрмализация углеводного обмена\n" +
                        "Синтез глюкозы и глюкогена\n",
                "Синдром Жильбера - дисфункция печени, которая связана с мутацией в UGT1A1 гене и характерезуется " +
                        "хронически повышенным общим и непрямым билирубином при отсутствии других патологий печени.\n\n" +
                        "Непрямой билирубин - довольно токсичный продукт распада гемоглобина, который с помощью присоединения " +
                        "глюкароновой кислоты должен превращаться в нетоксичный прямой и выводится с желчью. Мутация связана с " +
                        "подавлением фермента, который и переводит непрямой билирубин в прямой",
                "- Желтизна склер, заметная только вам(особенно при недосыпах, переутомлении, голодании)\n" +
                        "- Периодические боли в правом подреберье, тошнота\n" +
                        "- Усталость, раздражительность\n" +
                        "- Сухость кожи, зуд, крапивница\n" +
                        "- Непереносимость определенных лекарств и трав\n" +
                        "- Гастроэнтерологические проблемы\n\n" +
                        "К чему приводит постоянно повышенный непрямой билирубин:\n" +
                        "- Нарушению второй фазы детоксификации печени: неспособность справится с токсинами, пестицидами, " +
                        "стероидами, консервантами, фенолами и другими токсичными веществами\n" +
                        "- Накопление токсинов в печени, а следовательно их переход в глубокие кеани и другие органы",
                "Исключить вещества, подавляющие действие фермента UDP-глюкуронозилтрансферазы, а также максимально " +
                        "его стимулировать\n\n" +
                        "Блокаторы фермента(все, чего нужно избенать):\n" +
                        "- ФармацевтиткаЖ амфетамин, аспирин, ибупрофен(все НСПВП)б катоконазол, мотилиум\n" +
                        "- Гепабене\n" +
                        "- Оральные контрацептивы\n" +
                        "- Витамин А(ретинол)\n" +
                        "- растения: расторопша, Saw Palmetto(сереноа-пальма ползучая, основа Простамола), " +
                        "эхинацей, женьшень, галлат эпигаллокатехин(зеленый чай), чеснок, солодка\n" +
                        "- Fo-ti(горец многоцветковый)\n" +
                        "- ингибиторы протонной помпы(омез, омепрозол, рабепразол и прочее)\n" +
                        "- рафинированные углеводы\n" +
                        "- алкоголь\n",
                "- Феруловая кислота: кофе в зернах, спаржа, ягоды, горох, цитрусовые\n" +
                        "- Глюкоза(необходима для экспресии гена UGT1A1, поэтому кетодиета противопоказана)\n" +
                        "- Сульфарофан - все крестоцветные(проростки брокколи идеально, можно в виде добавки), " +
                        "брюсельская капуста, кейл, цветная капуста, зелень\n" +
                        "- Кверцетин(каперсы, гречка, красный лук, листовые овощи, томаты)\n" +
                        "- Куркумин(с жирной пищей), ресвератрол\n" +
                        "- Одуванчик\n" +
                        "- Таурин(аминокислота для желчных кислот)\n",
                "1. Повышение билирубина выше 15 мл/л\n" +
                        "2. Повышены АЛТ и АСТ\n" +
                        "- Синдром Жильбера",
                "- 3-х разовое питание\n" +
                        "- Ужин за 4-5 часов до сна\n" +
                        "- Утром на тощак выпить 2 стакана воды\n" +
                        "- Между приемами пищи травяные чаи\n" +
                        "- Выпивать не менее 2-х литров воды\n",
                "- Животный белок\n" +
                        "- Молочную и кисломолочную продукцию\n" +
                        "- Глютеносодержащие злаки: пшеница, рожь, ячмень\n" +
                        "- Сахар и рафинированные углеводы\n" +
                        "- Кофеин, черный чай, зеленый чай, алкоголь\n" +
                        "- Картофель\n" +
                        "- Не более 30гр орехов в день\n",
                "- Максимальное количество овощей, особенно зеленых - брокколи, кейл, бок-чой, " +
                        "белокочанная капуста, " +
                        "цветная капуста, кольраби, шпинат, руколла, базилик, кинза, петрушка, " +
                        "кабачки, спаржа, " +
                        "редис, артишоки, огурцы, морковь, свекла в сыром виде\n" +
                        "- Специи(куркума, размарин, тимьян, аригано, артишок, имбирь)\n" +
                        "- Фрукты и ягоды - цитрусовые, авокадо, зеленые яблоки, ягоды\n" +
                        "- Крупы - гречка, киноа, амарант\n" +
                        "- Ферментированные продукты - квашенная капуста, чайный гриб, кокосовый или " +
                        "тибетский кефир, квашеная редька\n",
                "1 Фаза защиты от токсинов\n" +
                        "Защита от токсинов через цитохромы семейства Р450. Данные ферменты защищают " +
                        "клетки от повреждений, превращают токсины в водорастворимую форму\n\n" +
                        "2 Фаза конъюгации\n" +
                        "Включает 6 путей для выведения водорастворимых метаболиков: через желчь, " +
                        "мочу, стул, слюшу, легкие и молоко. 2 фаза часто проходит вяло. При нарушении 2 " +
                        "метаболиты выводятся не полностью, а попадают в кровоток, что провоцирует заболевания\n\n" +
                        "3 Фаза налаживания работы кишечника\n" +
                        "Здесь происходит транспортировка метаболитов и выведение из организма. Без " +
                        "налаженной 3 фазы детокс начинать нельзя. Для поддержки 3 фазы нужно адекватное употребление " +
                        "воды",
                "- Зверобой\n" +
                        "- Тмин\n" +
                        "- Семена укропа\n" +
                        "- Крестоцветные\n" +
                        "- Цитрусовые(кроме грейпфрута)\n" +
                        "- Белковая пища\n" +
                        "- Витамины В3\n" +
                        "Субпродукты(печень, сердце, почки), ципленок, говядина, рыба(тунец, лосось). Листовые овощи, " +
                        "морковь, томаты, спаржа, проросшее зерно, люцерна, кайенский перец, петрушка, ромашка, " +
                        "моченные орехи\n" +
                        "- Витамин В1\n" +
                        "Кедровые орехи, бурый рис, фисташки, семена подсолнечника. Рыба, печень, говядина, свинина, " +
                        "яичнй желток, проросшее зерно, спаржа, брюсельская капуста, замоченные орехи, бобовые продукты\n" +
                        "- Витамин С\n" +
                        "Болгарский перец, ягоды, цитрусовые, фрукты, манго, папайя, авокадо, зеленые овощи, свекольные " +
                        "листья, листовая капуста, трава одуванчика, листья горчицы, черная смородина, брокколи, " +
                        "брюсельская капуста, мускусная дыня",
                "- Витамин В2\n" +
                        "Субпродукты(печень, язык), яйцо, рыба, спаржа, бананы\n" +
                        "- Витамины В3\n" +
                        "Субпродукты(печень, сердце, почки), ципленок, говядина, рыба(тунец, лосось). Листовые овощи, " +
                        "морковь, томаты, спаржа, проросшее зерно, люцерна, кайенский перец, петрушка, ромашка, " +
                        "моченные орехи\n" +
                        "- Витамин В6\n" +
                        "Мясо, цыпленок, яйцо, рыба, печень, морковь, шпинат, авокадо, проростки пшеницы, " +
                        "бананы, брокколи, капуста, замоченные орехи\n" +
                        "- Витамин В12\n" +
                        "Мясо(говядина), субпродукты(печень и почки), морепродукты(моллючки, сардина, тунец), " +
                        "рыба(тунец, лосось), яйца\n" +
                        "- Фолиевая кислота\n" +
                        "Печень, лосось, устрицы, яйцо. Зеленые листовые овощи, томаты, спаржа, проросшая пшеница, " +
                        "проросшие цельные зерна\n" +
                        "- Витамины А, С, Е\n" +
                        "- Селен, цинк, магний\n" +
                        "- Флавоноиды, которые содержатся в овощах, ягодах и зелени\n" +
                        "- Глутатион\n" +
                        "Авокадо, брокколи, белокочанная капуста, цветная капуста, чеснок, листовая капуста, лук\n",
                "- Аминокислоты\n" +
                        "Глицин, глютамин, таурин, цистеин, метионин. Мясо, рыба, птица. Шпинат, петрушка, " +
                        "капуста, соя, фасоль, горох\n" +
                        "- Витамины группы В\n" +
                        "Субпродукты(печень, сердце, почки), ципленок, говядина, рыба(тунец, лосось), яйца, " +
                        "морепродукты. Листовые овощи, морковь, томаты, , спаржа, проросшее зерно. Шпинат, " +
                        "авокадо, бананы, брокколи, капуста, замоченные орехи\n" +
                        "- Витамин С\n" +
                        "Болгарский перец, ягоды, цитрусовые, фрукты, манго, папайя, авокадо, зеленые овощи, свекольные " +
                        "листья, листовая капуста, трава одуванчика, листья горчицы, черная смородина, брокколи, " +
                        "брюсельская капуста, мускусная дыня" +
                        "- Селен\n" +
                        "Бразильский орех, тунец, говядина, цыпленок, индейка, рыба(треска). Спаржа, " +
                        "чеснок, проросшая пшеница\n" +
                        "- Глутатион\n" +
                        "Авокадо, брокколи, белокочанная капуста, цветная капуста, чеснок, листовая капуста, лук\n",
                "Желчь:\n" +
                        "- нормализует микрофлору кишечника\n" +
                        "- отвечает за усвоение жиров и витаминов\n" +
                        "- синтезируется в гепатоцитах, далее попадает в желчные капиляры и по желчным протокам " +
                        "скапливается в желчном пузыре\n\n" +
                        "Желчь выделаяется в первую очередь на прием жиров, потом белков, потом углеводов." +
                        "Желчь активирует липазу поджелудочной железы\n",
                "- Нейтролизация соляной кислоты в двенадцатиперстной кишке\n" +
                        "- Античептическая функция\n" +
                        "- Выведение метаболитов токсичных веществ и метаболитов гормонов\n" +
                        "- Активизация ферментов поджелудочной железы и кишечника\n" +
                        "- Формирование каловых масс и моторики кишечника\n" +
                        "- Усвоение жиров и жирорастворимых витаминов\n",
                "- Наличие нейтрального жира, мыла, непереваренных жирных кислот в копрограмме\n" +
                        "- Твердый стул, жирный стул, запор, обесцвеченный кал, жидкий стул\n" +
                        "- Горечь во рту, тошнота на жиры, боли в правом подреберье, отрыжка на омега-3 жиры, " +
                        "боль в эпигастрии, газообразовнаие\n" +
                        "- Спецфический запах пота\n" +
                        "- Мигрени и головные боли\n" +
                        "- Проблемы со зрением\n" +
                        "- Экзема\n" +
                        "- Акне\n" +
                        "- Фолликулярный кератоз\n" +
                        "- Бессоница\n",
                "- Инфекционные и вирусные заболевания\n" +
                        "- Недостаточный питьевой режим\n" +
                        "- Высокая токсическая нагрузка на печень\n" +
                        "- Дефицит витаминов, минералов и аминокислот\n" +
                        "- Недостаточная двигательная активность\n" +
                        "- Нерациональное питание с исключением жиров и клетчатки\n" +
                        "- Изюыток сахара и трансжиров в рационе",
                "- Минимальная суточная норма употребления воды - 2 литра, температура воды 38-40 градусов. " +
                        "До завтрака необходимо выпить 2 стакана воды\n" +
                        "- При запорах подключать воду Donat за 30 минут до еды. Начинать с 50мл и плавно " +
                        "доводить до 200мл. Предварительно из воды необходимо выпустить газы, оставив на ночь\n" +
                        "- В каждый прием пищи добавлять жиры - яйца, жирную рыбу, субпродукты, авокадо, масло " +
                        "авокадо, печень трески, масло МСТ, масло ГХИ, льняное масло, оливковое масло, семена " +
                        "и орехи\n" +
                        "- Употреблять качественный белок в каждый прием пищи - яйца, рыба, морепродукты, мясо, " +
                        "супродукты, птица, гречка, киноа, амарант, бобовые, проростки злаков и бобовых\n" +
                        "- Добавлять клетчатку - разнообразные овощи и зелень\n" +
                        "- Употредлять травы-горечи\n" +
                        "- Добавлять в рацион сырую свеклу и черную редьку",
            ),
            "Система пищеварения: поджелудочная железа и кишечник" to mutableListOf(
                "Проблемы с поджелудочной железой связаны с нерациональным питанием, употреблением алкоголя и " +
                        "с проблемами верхних вышележыщих органов пищеварения\n\n" +
                        "Поджелудочная железа - место выработки панкриатических ферментов и панкриатического сока. " +
                        "На работу поджелудочной железы влияет желчный пузырь, чем больше желчи вырабатывает " +
                        "желчный пузырь, тем больше вырабатывается ферментов\n\n" +
                        "Панкреатический сок имеет щелочную среду, содержит ферменты для расщепления белков, " +
                        "жиров и углеводов\n",
                "Белки - крепсин и химотрипсин\n" +
                        "Углеводы - амилаза\n" +
                        "Этры - липаза\n\n" +
                        "Вставить таблицу с ферментами\n",
                "- Восстановить кислотность\n" +
                        "- Восстановить функции желчного пузыря\n" +
                        "- Убрать сахар, глютен, рафинированные углеводы\n" +
                        "- Исключить алкоголь\n" +
                        "- Поддержка ЖКТ, если есть аутоиммунные заболевания(красная волчанка, аутоиммуный тиреодит, " +
                        "витилиго, псориаз, рассеяный склероз)\n" +
                        "- Исключаем жирное и жареное. Приоритетная готовка - тушение, запекание, варка, " +
                        "допустимо приготовление в мультиварке\n",
                "- Секреторная\n" +
                        "Продолжается выработка ферментов для переваривания белков, жиров и углеводов\n\n" +
                        "- Моторная \n\n" +
                        "- Всасывательная\n" +
                        "Всасывание питательных веществ сквозь слизистую кишечнка\n",
                "- Полостное пищеварение\n" +
                        "- Пристеночное пищеварение\n\n" +
                        "Дефициты микроэлементов, бродильные процессы возникают в следствии нарушения пристеночного " +
                        "пищеварения\n\n" +
                        "При запорах происходит хроническая интоксикация, нарушается слизистая кишечника и пристеночное " +
                        "пищеварение\n\n" +
                        "В толстый кишечник пища попадает почти полностью переваренной за исключением растительной " +
                        "клетчатки. Здесь происходит всасывание воды, остатки пищи уплотняются, склеиваются слизью и " +
                        "формируются каловые массы\n\n" +
                        "! На перистальтику кишечника большое значение оказывает клетчатка",
                "- Восстановление функции и кислотности желудка\n" +
                        "- Восстановление структуры желчи\n" +
                        "- Достаточное количество панкреатических ферментов\n" +
                        "- Выбрать лечебную систему питания\n" +
                        "- Время восстановления слизистой 3-6 месяцев",
                "- Ротовая полость - амилаза\n" +
                        "- Желудок - пепсин и химозин\n" +
                        "- Тонкий кишечник - мальтоза, лактоза, сахароза, трипсин, химотрипсин\n\n" +
                        "Вставить таблицу",
            ),
        )
    }
}