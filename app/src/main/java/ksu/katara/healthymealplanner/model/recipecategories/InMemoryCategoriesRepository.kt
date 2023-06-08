package ksu.katara.healthymealplanner.model.recipecategories

import ksu.katara.healthymealplanner.exceptions.RecipeCategoryNotFoundException
import ksu.katara.healthymealplanner.model.recipecategories.entities.Category
import ksu.katara.healthymealplanner.tasks.SimpleTask
import ksu.katara.healthymealplanner.tasks.Task
import java.util.concurrent.Callable

typealias RecipeCategoriesListener = (recipeCategories: List<Category>) -> Unit

class InMemoryCategoriesRepository : CategoriesRepository {

    private var recipeCategories = mutableListOf<Category>()
    private var loaded = false
    private val listeners = mutableSetOf<RecipeCategoriesListener>()

    init {
        RECIPE_CATEGORIES.forEach { (id, categoriesList) ->
            recipeCategories.add(
                Category(
                    id = id.toLong(),
                    photo = categoriesList[0],
                    name = categoriesList[1],
                )
            )
        }
    }

    override fun loadRecipeCategories(): Task<Unit> = SimpleTask {
        Thread.sleep(200L)
        recipeCategories = getRecipeCategories()
        loaded = true
        notifyChanges()
    }

    private fun getRecipeCategories(): MutableList<Category> {
        val recipeCategories = mutableListOf<Category>()
        RECIPE_CATEGORIES.forEach { (id, categoriesList) ->
            recipeCategories.add(
                Category(
                    id = id.toLong(),
                    photo = categoriesList[0],
                    name = categoriesList[1],
                )
            )
        }
        return recipeCategories
    }

    override fun getCategoryById(id: Long): Task<Category> = SimpleTask(Callable {
        Thread.sleep(200L)
        return@Callable recipeCategories.firstOrNull<Category> { it.id == id }
            ?: throw RecipeCategoryNotFoundException()
    })

    override fun addListener(listener: RecipeCategoriesListener) {
        listeners.add(listener)
        if (loaded) {
            listener.invoke(recipeCategories)
        }
    }

    override fun removeListener(listener: RecipeCategoriesListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        if (!loaded) return
        listeners.forEach { it.invoke(recipeCategories) }
    }

    companion object {
        private val RECIPE_CATEGORIES = mapOf(
            0 to listOf(
                "https://images.unsplash.com/photo-1611068120813-eca5a8cbf793?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "Первые блюда",
            ),
            1 to listOf(
                "https://images.unsplash.com/photo-1432139509613-5c4255815697?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=985&q=80",
                "Вторые блюда",
            ),
            2 to listOf(
                "https://images.unsplash.com/photo-1537785713284-0015ce8a145c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                "Салаты",
            ),
            3 to listOf(
                "https://images.unsplash.com/photo-1592415486689-125cbbfcbee2?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2050&q=80",
                "Закуски",
            ),
            4 to listOf(
                "https://images.unsplash.com/photo-1509440159596-0249088772ff?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2372&q=80",
                "Выпечка",
            ),
            5 to listOf(
                "https://images.unsplash.com/photo-1644882767097-2f1748d80ec9?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
                "Соусы и маринады",
            ),
            6 to listOf(
                "https://images.unsplash.com/photo-1582438936711-60b28a7195cd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2369&q=80",
                "Заготовки",
            ),
            7 to listOf(
                "https://images.unsplash.com/photo-1497534446932-c925b458314e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1372&q=80",
                "Напитки",
            ),
            8 to listOf(
                "https://images.unsplash.com/photo-1508737804141-4c3b688e2546?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=986&q=80",
                "Десерты",
            ),
            9 to listOf(
                "https://images.unsplash.com/photo-1596560548464-f010549b84d7?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                "Гарниры",
            ),
        )
    }
}