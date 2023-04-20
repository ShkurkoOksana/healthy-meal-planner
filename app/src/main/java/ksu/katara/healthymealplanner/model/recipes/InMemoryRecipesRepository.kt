package ksu.katara.healthymealplanner.model.recipes

import ksu.katara.healthymealplanner.model.recipes.entities.Recipe

const val TAG = "InMemoryRecipesItemListRepository"

class InMemoryRecipesRepository : RecipesRepository {
    private lateinit var recipes: MutableList<Recipe>

    private val recipesSize = 5

    init {
        recipes = (0 until recipesSize).map {
            Recipe(
                id = it.toLong(),
                name = RECIPES_NAMES[it],
                photo = RECIPES_IMAGES[it],
            )
        }.toMutableList()
    }

    override fun getRecipeById(id: Long): Recipe {
        return recipes[id.toInt()]
    }

    companion object {
        private val RECIPES_IMAGES = listOf(
            "https://images.unsplash.com/photo-1612487439139-c2d7bac13577?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
            "https://images.unsplash.com/photo-1625944230945-1b7dd3b949ab?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1760&q=80",
            "https://images.unsplash.com/photo-1527976746453-f363eac4d889?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3432&q=80",
            "https://images.unsplash.com/photo-1551248429-40975aa4de74?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1980&q=80",
            "https://images.unsplash.com/photo-1485921325833-c519f76c4927?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1364&q=80",
        )

        private val RECIPES_NAMES = listOf(
            "Глазунья",
            "Греческий салат",
            "Борщ",
            "Салат цезарь с креветками",
            "Минтай в сливочном соусе",
        )
    }
}