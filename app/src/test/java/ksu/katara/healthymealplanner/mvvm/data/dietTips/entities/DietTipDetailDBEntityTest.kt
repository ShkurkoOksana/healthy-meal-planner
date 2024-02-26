package ksu.katara.healthymealplanner.mvvm.data.dietTips.entities

import ksu.katara.healthymealplanner.mvvm.domain.dietTips.entities.DietTipDetails
import org.junit.Assert.assertEquals
import org.junit.Test

class DietTipDetailDBEntityTest {

    @Test
    fun toDietTipDetails() {
        val responseEntity = DietTipDetailDBEntity(
            id = 1,
            background = "https://images.unsplash.com/photo-1635361785871-412887d18ed4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=3387&q=80"
        )

        val inAppEntity = responseEntity.toDietTipDetails()

        val expectedInAppEntity = DietTipDetails(
            id = 1,
            background = "https://images.unsplash.com/photo-1635361785871-412887d18ed4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=3387&q=80"
        )
        assertEquals(inAppEntity, expectedInAppEntity)
    }
}