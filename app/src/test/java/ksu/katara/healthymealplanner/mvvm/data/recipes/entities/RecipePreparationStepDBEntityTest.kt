package ksu.katara.healthymealplanner.mvvm.data.recipes.entities

import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.RecipePreparationStep
import org.junit.Assert
import org.junit.Test

class RecipePreparationStepDBEntityTest {

    @Test
    fun toRecipePreparationStep() {
        val responseEntity = RecipePreparationStepDBEntity(
            id = 1,
            photo = "https://images.unsplash.com/photo-1498654077810-12c21d4d6dc3?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
            description = "Яйца берите крупные отборные. Чтобы яичница получилась не только вкусной, но и красивой, лучше используйте деревенские яйца с крупным желтком насыщенного желтого цвета. Яйца обязательно берите свежие. Перед началом приготовления их необходимо помыть",
            preparationStep = 1
        )

        val inAppEntity = responseEntity.toRecipePreparationStep()

        val expectedInAppEntity = RecipePreparationStep(
            id = 1,
            step = 1,
            photo = "https://images.unsplash.com/photo-1498654077810-12c21d4d6dc3?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
            description = "Яйца берите крупные отборные. Чтобы яичница получилась не только вкусной, но и красивой, лучше используйте деревенские яйца с крупным желтком насыщенного желтого цвета. Яйца обязательно берите свежие. Перед началом приготовления их необходимо помыть"
        )
        Assert.assertEquals(inAppEntity, expectedInAppEntity)
    }
}