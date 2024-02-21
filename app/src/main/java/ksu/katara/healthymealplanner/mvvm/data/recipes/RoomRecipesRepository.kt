package ksu.katara.healthymealplanner.mvvm.data.recipes

import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.domain.recipes.RecipeDetailsListener
import ksu.katara.healthymealplanner.mvvm.domain.recipes.RecipeIngredientsListener
import ksu.katara.healthymealplanner.mvvm.domain.recipes.RecipesInCategoryListener
import ksu.katara.healthymealplanner.mvvm.domain.recipes.RecipesRepository
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.RecipeDetails
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.RecipePreparationStep
import javax.inject.Inject

/**
 * Simple in-memory implementation of [RecipesRepository]
 */
class RoomRecipesRepository @Inject constructor(
    private val recipesDao: RecipesDao,
    private val ioDispatcher: IoDispatcher
) : RecipesRepository {

    private lateinit var recipeDetails: RecipeDetails
    private var recipeDetailsLoaded = false
    private val recipeDetailsListeners = mutableSetOf<RecipeDetailsListener>()

    private var types = listOf<String>()
    private var typesLoaded = false

    private var recipesInCategory = listOf<Recipe>()
    private var recipesInCategoryLoaded = false
    private val recipesInCategoryListeners = mutableSetOf<RecipesInCategoryListener>()

    private var ingredients = listOf<RecipeIngredient>()
    private var ingredientsLoaded = false
    private val ingredientsListeners = mutableSetOf<RecipeIngredientsListener>()

    private var preparationSteps = listOf<RecipePreparationStep>()
    private var preparationStepsLoaded = false

    override suspend fun loadRecipesInCategory(recipeCategoryId: Long): List<Recipe> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            recipesInCategory =
                recipesDao.findRecipesInCategory(recipeCategoryId).map { it.toRecipe() }
            recipesInCategoryLoaded = true
            notifyRecipeInCategoryChanges()
            return@withContext recipesInCategory
        }

    override fun addRecipeInCategoryListener(listener: RecipesInCategoryListener) {
        recipesInCategoryListeners.add(listener)
        if (recipesInCategoryLoaded) {
            listener.invoke(recipesInCategory)
        }
    }

    override fun removeRecipeInCategoryListener(listener: RecipesInCategoryListener) {
        recipesInCategoryListeners.remove(listener)
    }

    private fun notifyRecipeInCategoryChanges() {
        if (!recipesInCategoryLoaded) return
        recipesInCategoryListeners.forEach { it.invoke(recipesInCategory) }
    }

    override suspend fun loadRecipeDetails(recipeId: Long): RecipeDetails =
        withContext(ioDispatcher.value) {
            delay(1000L)
            recipeDetails = findRecipeDetails(recipeId)
            recipeDetailsLoaded = true
            notifyRecipeDetailsChanges()
            return@withContext recipeDetails
        }

    private fun findRecipeDetails(recipeId: Long): RecipeDetails {
        val recipeDetails = recipesDao.findRecipeById(recipeId)
        return RecipeDetails(
            recipe = recipeDetails.toRecipe(),
            preparationTime = recipeDetails.preparationTime,
            cuisineType = findRecipeCuisineTypeByRecipeId(recipeId),
            types = findRecipeTypesByRecipeId(recipeId),
            energeticValue = recipeDetails.energeticValue,
            proteins = recipeDetails.proteins,
            fats = recipeDetails.fats,
            carbohydrates = recipeDetails.carbohydrates,
            ingredients = findRecipeIngredientsByRecipeId(recipeId),
            preparationSteps = findRecipePreparationSteps(recipeId),
            isFavorite = recipeDetails.isFavorite == 1
        )
    }

    private fun findRecipeCuisineTypeByRecipeId(id: Long): String {
        return recipesDao.findRecipeCuisineTypeByRecipeId(id).map { it.cuisineTypeDBEntity.name }[0]
    }

    override fun addRecipeDetailsListener(listener: RecipeDetailsListener) {
        recipeDetailsListeners.add(listener)
        if (recipeDetailsLoaded) {
            listener.invoke(recipeDetails)
        }
    }

    override fun removeRecipeDetailsListener(listener: RecipeDetailsListener) {
        recipeDetailsListeners.remove(listener)
    }

    private fun notifyRecipeDetailsChanges() {
        if (!recipeDetailsLoaded) return
        recipeDetailsListeners.forEach { it.invoke(recipeDetails) }
    }

    override suspend fun loadRecipeTypes(recipeId: Long): List<String> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            types = findRecipeTypesByRecipeId(recipeId)
            typesLoaded = true
            return@withContext types
        }

    private fun findRecipeTypesByRecipeId(id: Long): List<String> {
        return recipesDao.findRecipeTypesByRecipeId(id).map { it.recipeTypeDBEntity.name }
    }

    override suspend fun loadIngredients(recipeId: Long): List<RecipeIngredient> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            ingredients = findRecipeIngredientsByRecipeId(recipeId)
            ingredientsLoaded = true
            notifyIngredientsChanges()
            return@withContext ingredients
        }

    private fun findRecipeIngredientsByRecipeId(id: Long): List<RecipeIngredient> {
        val tupleList = recipesDao.findRecipeIngredientsByRecipeId(id)
        val list: MutableList<RecipeIngredient> = mutableListOf()
        tupleList.forEach {
            val recipeIngredient = RecipeIngredient(
                id = it.recipeIngredientDBEntity.id,
                product = it.recipeIngredientDBEntity.name,
                amount = it.recipesRecipeIngredientsJoinTableDBEntity.amount.ifEmpty { "0.0" }
                    .toDouble(),
                measure = it.ingredientMeasureDBEntity.name,
                isInShoppingList = it.recipesRecipeIngredientsJoinTableDBEntity.isInShoppingList == 1
            )
            list.add(recipeIngredient)
        }
        return list
    }

    override fun addIngredientListener(listener: RecipeIngredientsListener) {
        ingredientsListeners.add(listener)
        if (ingredientsLoaded) {
            listener.invoke(ingredients)
        }
    }

    override fun removeIngredientsListener(listener: RecipeIngredientsListener) {
        ingredientsListeners.remove(listener)
    }

    private fun notifyIngredientsChanges() {
        if (!ingredientsLoaded) return
        ingredientsListeners.forEach { it.invoke(ingredients) }
    }

    override suspend fun setIngredientSelected(
        recipeId: Long,
        ingredientId: Long,
        isSelected: Boolean
    ): Unit = withContext(ioDispatcher.value) {
        delay(1000L)
        updateRecipeIngredient(recipeId, ingredientId, isSelected)
        ingredients = findRecipeIngredientsByRecipeId(recipeId)
        ingredientsLoaded = true
        notifyIngredientsChanges()
    }

    private fun updateRecipeIngredient(recipeId: Long, ingredientId: Long, isSelected: Boolean) {
        val selected = if (isSelected) 1 else 0
        recipesDao.updateRecipeIngredient(recipeId, ingredientId, selected)
    }

    override suspend fun setAllIngredientsSelected(recipeId: Long, isSelected: Boolean) =
        withContext(ioDispatcher.value) {
            delay(1000L)
            updateAllRecipeIngredients(recipeId, isSelected)
            ingredients = findRecipeIngredientsByRecipeId(recipeId)
            ingredientsLoaded = true
            notifyIngredientsChanges()
        }

    private fun updateAllRecipeIngredients(recipeId: Long, isSelected: Boolean) {
        val selected = if (isSelected) 1 else 0
        recipesDao.updateAllRecipeIngredients(recipeId, selected)
    }

    override suspend fun isAllIngredientsSelected(recipeId: Long): Boolean =
        withContext(ioDispatcher.value) {
            delay(1000L)
            return@withContext recipesDao.isAllIngredientsSelected(recipeId) == ingredients.size
        }

    override suspend fun loadPreparationSteps(recipeId: Long): List<RecipePreparationStep> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            preparationSteps = findRecipePreparationSteps(recipeId)
            preparationStepsLoaded = true
            return@withContext preparationSteps
        }

    private fun findRecipePreparationSteps(recipeId: Long): List<RecipePreparationStep> {
        return recipesDao.findRecipePreparationSteps(recipeId)
            .map { it.preparationStepDBEntity.toRecipePreparationStep() }
    }

}