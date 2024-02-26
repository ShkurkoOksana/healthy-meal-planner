package ksu.katara.healthymealplanner.mvvm.data.dietTips.entities

import ksu.katara.healthymealplanner.mvvm.domain.dietTips.entities.DietTip
import org.junit.Assert.assertEquals
import org.junit.Test

class DietTipDBEntityTest {

    @Test
    fun toDietTip() {
        val responseEntity = DietTipDBEntity(
            id = 1,
            photo = "https://images.unsplash.com/photo-1490645935967-10de6ba17061?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2353&q=80",
            name = "Режим питания",
            dietTipDetailsId = 1,
            chapterId = 1
        )

        val inAppEntity = responseEntity.toDietTip()

        val expectedInAppEntity = DietTip(
            id = 1,
            photo = "https://images.unsplash.com/photo-1490645935967-10de6ba17061?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2353&q=80",
            name = "Режим питания",
            dietTipDetailsId = 1,
            chapterId = 1
        )
        assertEquals(inAppEntity, expectedInAppEntity)
    }
}