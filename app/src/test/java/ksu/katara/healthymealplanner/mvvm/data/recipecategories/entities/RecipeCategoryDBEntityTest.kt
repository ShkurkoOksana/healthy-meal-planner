package ksu.katara.healthymealplanner.mvvm.data.recipecategories.entities

import ksu.katara.healthymealplanner.mvvm.domain.recipecategories.entities.RecipeCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeCategoryDBEntityTest {

    @Test
    fun toRecipeCategory() {
        val responseEntity = RecipeCategoryDBEntity(
            id = 1,
            photo = "https://images.unsplash.com/photo-1611068120813-eca5a8cbf793?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            name = "Первые блюда"
        )

        val inAppEntity = responseEntity.toRecipeCategory()

        val expectedInAppEntity = RecipeCategory(
            id = 1,
            photo = "https://images.unsplash.com/photo-1611068120813-eca5a8cbf793?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
            name = "Первые блюда"
        )
        assertEquals(inAppEntity, expectedInAppEntity)
    }
}