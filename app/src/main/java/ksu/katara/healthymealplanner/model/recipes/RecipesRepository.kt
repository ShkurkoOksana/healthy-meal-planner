package ksu.katara.healthymealplanner.model.recipes

import ksu.katara.healthymealplanner.model.recipes.entities.Recipe

interface RecipesRepository {

    fun getRecipeById(id: Long): Recipe

}