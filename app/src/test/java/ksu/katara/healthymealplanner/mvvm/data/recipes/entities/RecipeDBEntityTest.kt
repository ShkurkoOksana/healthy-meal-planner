package ksu.katara.healthymealplanner.mvvm.data.recipes.entities

import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.Recipe
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeDBEntityTest {

    @Test
    fun toRecipe() {
        val responseEntity = RecipeDBEntity(
            id = 1,
            categoryId = 2,
            name = "Глазунья",
            photo = "https://images.unsplash.com/photo-1612487439139-c2d7bac13577?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
            cuisineTypeId = 1,
            energeticValue = 199,
            proteins = 3,
            fats = 13,
            carbohydrates = 10,
            isFavorite = 1,
            preparationTime = 15,
        )

        val inAppEntity = responseEntity.toRecipe()

        val expectedInAppEntity = Recipe(
            id = 1,
            categoryId = 2,
            name = "Глазунья",
            photo = "https://images.unsplash.com/photo-1612487439139-c2d7bac13577?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80"
        )
        assertEquals(inAppEntity, expectedInAppEntity)
    }
}
