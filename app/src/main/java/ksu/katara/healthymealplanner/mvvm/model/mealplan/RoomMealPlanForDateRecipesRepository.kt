package ksu.katara.healthymealplanner.mvvm.model.mealplan

import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.mealplan.entities.MealPlanRecipes
import ksu.katara.healthymealplanner.mvvm.model.mealplan.room.MealPlanDao
import ksu.katara.healthymealplanner.mvvm.model.mealplan.room.entities.MealPlanDBEntity
import ksu.katara.healthymealplanner.mvvm.model.mealplan.room.entities.MealPlanRecipesJoinTableDBEntity
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.model.recipes.room.RecipesDao
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.MealTypes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Simple in-memory implementation of [MealPlanForDateRecipesRepository]
 */
class RoomMealPlanForDateRecipesRepository @Inject constructor(
    private val mealPlanDao: MealPlanDao,
    private val recipesDao: RecipesDao,
    private val ioDispatcher: IoDispatcher
) : MealPlanForDateRecipesRepository {

    private var mealPlanForDateRecipes: MealPlanRecipes? = null
    private var mealPlanForDateRecipesLoaded = false
    private val mealPlanForDateRecipesListeners = mutableSetOf<MealPlanForDateRecipesListener>()

    private lateinit var addRecipes: MutableList<Recipe>
    private var loadedAddRecipes = false
    private val addRecipesListeners = mutableListOf<AddRecipesListener>()

    private val dataSDF = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)

    override suspend fun loadMealPlanForDate(
        selectedDate: Date,
        mealType: MealTypes
    ): MealPlanRecipes? = withContext(ioDispatcher.value) {
        delay(1000L)
        mealPlanForDateRecipes = findMealPlanRecipes(selectedDate, mealType)
        mealPlanForDateRecipesLoaded = true
        notifyMealPlanForDateChanges()
        return@withContext mealPlanForDateRecipes
    }

    private fun findMealPlanRecipes(selectedDate: Date, mealType: MealTypes): MealPlanRecipes? {
        val date = dataSDF.format(
            selectedDate
        )
        val type = mealType.mealName
        val tupleList = mealPlanDao.findMealPlanRecipes(date, type)
        val recipesList = tupleList.map { it.recipeDBEntity.toRecipe() }.toMutableList()
        return if (recipesList.isNotEmpty()) MealPlanRecipes(mealType, recipesList) else null
    }

    override fun addMealPlanForDateListener(listener: MealPlanForDateRecipesListener) {
        mealPlanForDateRecipesListeners.add(listener)
        if (mealPlanForDateRecipesLoaded) {
            listener.invoke(mealPlanForDateRecipes)
        }
    }

    override fun removeMealPlanForDateListener(listener: MealPlanForDateRecipesListener) {
        mealPlanForDateRecipesListeners.remove(listener)
    }

    override suspend fun addRecipeToMealPlanForDate(
        selectedDate: Date,
        mealType: MealTypes,
        recipe: Recipe
    ) = withContext(ioDispatcher.value) {
        delay(1000L)
        addRecipeToMealPlanForDateRecipes(selectedDate, mealType, recipe)
        notifyMealPlanForDateChanges()
    }

    private fun addRecipeToMealPlanForDateRecipes(
        selectedDate: Date,
        mealType: MealTypes,
        recipe: Recipe
    ) {
        insertRecipeToMealPlanForDateRecipes(selectedDate, mealType, recipe)
        mealPlanForDateRecipes = findMealPlanRecipes(selectedDate, mealType)
        mealPlanForDateRecipesLoaded = true
        notifyMealPlanForDateChanges()
    }

    private fun insertRecipeToMealPlanForDateRecipes(
        selectedDate: Date,
        mealType: MealTypes,
        recipe: Recipe
    ) {
        val date = dataSDF.format(selectedDate)
        val mealPlanDBEntity = MealPlanDBEntity.fromMealPlan(date)
        val mealPlanId = mealPlanDao.insertMealPlan(mealPlanDBEntity)
        val mealTypeId = mealType.ordinal.toLong() + 1
        val mealPlanRecipesJoinTableDBEntity =
            MealPlanRecipesJoinTableDBEntity.fromMealPlanAndRecipe(
                mealPlanId,
                recipe.id,
                mealTypeId
            )
        mealPlanDao.insertRecipeToMealPlan(mealPlanRecipesJoinTableDBEntity)
    }

    override suspend fun deleteRecipeFromMealPlanForDate(
        selectedDate: Date,
        mealType: MealTypes,
        recipe: Recipe
    ): MealPlanRecipes? = withContext(ioDispatcher.value) {
        delay(1000L)
        deleteRecipeFromMealPlanForDateRecipes(selectedDate, mealType, recipe)
        mealPlanForDateRecipes = findMealPlanRecipes(selectedDate, mealType)
        mealPlanForDateRecipesLoaded = true
        notifyMealPlanForDateChanges()
        return@withContext mealPlanForDateRecipes
    }

    private fun deleteRecipeFromMealPlanForDateRecipes(
        selectedDate: Date,
        mealType: MealTypes,
        recipe: Recipe
    ) {
        val date = dataSDF.format(selectedDate)
        mealPlanDao.deleteRecipeFromMealPlanForDateRecipes(date, mealType.mealName, recipe.id)
    }

    private fun notifyMealPlanForDateChanges() {
        if (!mealPlanForDateRecipesLoaded) return
        mealPlanForDateRecipesListeners.forEach { it.invoke(mealPlanForDateRecipes) }
    }

    override suspend fun loadAddRecipes(selectedDate: Date, mealType: MealTypes): List<Recipe> =
        withContext(ioDispatcher.value) {
            delay(1000L)
            addRecipes = findAddRecipes()
            loadedAddRecipes = true
            notifyAddRecipeChanges()
            return@withContext addRecipes
        }

    private fun findAddRecipes(): MutableList<Recipe> {
        val allRecipes = recipesDao.findRecipes().map { it.toRecipe() }.toMutableList()
        val mealPlanRecipes = mealPlanForDateRecipes?.recipes
        return if (mealPlanRecipes != null) {
            allRecipes.removeAll(mealPlanRecipes)
            allRecipes
        } else {
            allRecipes
        }
    }

    override fun addAddRecipesListener(listener: AddRecipesListener) {
        addRecipesListeners.add(listener)
        if (loadedAddRecipes) {
            listener.invoke(addRecipes)
        }
    }

    override fun removeAddRecipesListener(listener: AddRecipesListener) {
        addRecipesListeners.remove(listener)
    }

    override suspend fun deleteRecipeFromAddRecipes(recipe: Recipe) =
        withContext(ioDispatcher.value) {
            delay(1000L)
            val indexToDelete = addRecipes.indexOfFirst { it == recipe }
            if (indexToDelete != -1) {
                addRecipes.removeAt(indexToDelete)
            }
            notifyAddRecipeChanges()
        }

    private fun notifyAddRecipeChanges() {
        if (!loadedAddRecipes) return
        addRecipesListeners.forEach { it.invoke(addRecipes) }
    }
}