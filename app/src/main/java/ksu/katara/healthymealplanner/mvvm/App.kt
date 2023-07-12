package ksu.katara.healthymealplanner.mvvm

import android.app.Application
import ksu.katara.healthymealplanner.foundation.BaseApplication
import ksu.katara.healthymealplanner.foundation.tasks.ThreadUtils
import ksu.katara.healthymealplanner.foundation.tasks.dispatchers.MainThreadDispatcher
import ksu.katara.healthymealplanner.foundation.tasks.factories.ExecutorServiceTasksFactory
import ksu.katara.healthymealplanner.mvvm.model.addrecipes.InMemoryAddRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.calendar.InMemoryCalendarRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.InMemoryDietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.InMemoryMealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.product.InMemoryProductsRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.InMemoryCategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.InMemoryRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.InMemoryShoppingListRepository
import java.util.concurrent.Executors

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {
    private val singleThreadExecutorTasksFactory = ExecutorServiceTasksFactory(Executors.newSingleThreadExecutor())
    private val cachedThreadPoolExecutorTasksFactory = ExecutorServiceTasksFactory(Executors.newCachedThreadPool())

    private val threadUtils = ThreadUtils.Default()
    private val dispatcher = MainThreadDispatcher()

    private val dietTipsRepository = InMemoryDietTipsRepository(singleThreadExecutorTasksFactory)
    private val productsRepository = InMemoryProductsRepository()
    private val recipeCategoriesRepository = InMemoryCategoriesRepository(singleThreadExecutorTasksFactory)
    private val recipesRepository = InMemoryRecipesRepository(productsRepository, singleThreadExecutorTasksFactory)
    private val mealPlanForDateRecipesRepository = InMemoryMealPlanForDateRecipesRepository(singleThreadExecutorTasksFactory)
    private val addRecipesRepository = InMemoryAddRecipesRepository(recipesRepository, mealPlanForDateRecipesRepository, singleThreadExecutorTasksFactory, threadUtils)
    private val shoppingListRepository = InMemoryShoppingListRepository(recipesRepository, singleThreadExecutorTasksFactory)
    private val calendarRepository = InMemoryCalendarRepository()

    /**
     * Place your repositories here
     */
    override val singletonScopeDependencies: List<Any> = listOf(
        cachedThreadPoolExecutorTasksFactory,
        dispatcher,
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