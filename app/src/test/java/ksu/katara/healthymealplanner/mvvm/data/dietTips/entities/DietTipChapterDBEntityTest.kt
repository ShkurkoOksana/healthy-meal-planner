package ksu.katara.healthymealplanner.mvvm.data.dietTips.entities

import ksu.katara.healthymealplanner.mvvm.domain.dietTips.entities.DietTipChapter
import org.junit.Assert.assertEquals
import org.junit.Test

class DietTipChapterDBEntityTest {

    @Test
    fun toDietTipChapter() {
        val responseEntity = DietTipChapterDBEntity(
            id = 1,
            name = "Фундамент здорового образа жизни"
        )

        val inAppEntity = responseEntity.toDietTipChapter()

        val expectedInAppEntity = DietTipChapter(
            id = 1,
            name = "Фундамент здорового образа жизни"
        )
        assertEquals(inAppEntity, expectedInAppEntity)
    }
}