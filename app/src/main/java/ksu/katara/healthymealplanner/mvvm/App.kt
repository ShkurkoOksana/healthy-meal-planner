package ksu.katara.healthymealplanner.mvvm

import android.app.Application
import ksu.katara.healthymealplanner.foundation.BaseApplication
import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.mvvm.model.addrecipes.InMemoryAddRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.calendar.InMemoryCalendarRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.InMemoryDietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.InMemoryMealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.product.InMemoryProductsRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.InMemoryCategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.InMemoryRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.InMemoryShoppingListRepository

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {
    private val dietTipsRepository = InMemoryDietTipsRepository()
    private val productsRepository = InMemoryProductsRepository()
    private val recipeCategoriesRepository = InMemoryCategoriesRepository()
    private val recipesRepository = InMemoryRecipesRepository(productsRepository,)
    private val mealPlanForDateRecipesRepository = InMemoryMealPlanForDateRecipesRepository()
    private val addRecipesRepository = InMemoryAddRecipesRepository(recipesRepository, mealPlanForDateRecipesRepository,)
    private val shoppingListRepository = InMemoryShoppingListRepository(recipesRepository)
    private val calendarRepository = InMemoryCalendarRepository()

    /**
     * Place your repositories here, now we have only 1 repository
     */
    override val repositories: List<Repository> = listOf(
        dietTipsRepository,
        productsRepository,
        recipeCategoriesRepository,
        recipesRepository,
        mealPlanForDateRecipesRepository,
        addRecipesRepository,
        shoppingListRepository,
        calendarRepository,
    )

}