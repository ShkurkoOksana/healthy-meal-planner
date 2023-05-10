package ksu.katara.healthymealplanner.model.recipecategories

import ksu.katara.healthymealplanner.model.recipecategories.entities.Category
import ksu.katara.healthymealplanner.tasks.Task

interface CategoriesRepository {

    fun loadRecipeCategories(): Task<Unit>

    fun getCategoryById(id: Long): Category

    fun getById(id: Long): Task<Category>

    fun addListener(listener: RecipeCategoriesListener)

    fun removeListener(listener: RecipeCategoriesListener)
}