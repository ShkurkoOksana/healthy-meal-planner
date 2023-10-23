package ksu.katara.healthymealplanner.mvvm

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import ksu.katara.healthymealplanner.foundation.BaseApplication
import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.addrecipes.InMemoryAddRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.calendar.InMemoryCalendarRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.SQLiteDietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.InMemoryMealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.product.InMemoryProductsRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.InMemoryCategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.InMemoryRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.InMemoryShoppingListRepository
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteHelper

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

    private lateinit var database: SQLiteDatabase

    private val ioDispatcher = IoDispatcher(Dispatchers.IO)
    private val workerDispatcher = IoDispatcher(Dispatchers.Default)

    private lateinit var dietTipsRepository : Repository
    private val productsRepository = InMemoryProductsRepository()
    private val recipeCategoriesRepository = InMemoryCategoriesRepository(ioDispatcher)
    private val recipesRepository = InMemoryRecipesRepository(productsRepository, ioDispatcher)
    private val mealPlanForDateRecipesRepository =
        InMemoryMealPlanForDateRecipesRepository(ioDispatcher)
    private val addRecipesRepository = InMemoryAddRecipesRepository(
        recipesRepository,
        mealPlanForDateRecipesRepository,
        ioDispatcher
    )
    private val shoppingListRepository =
        InMemoryShoppingListRepository(recipesRepository, ioDispatcher)
    private val calendarRepository = InMemoryCalendarRepository()

    /**
     * Place your repositories here
     */
    override val singletonScopeDependencies: List<Any> by lazy {
        listOf(
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
    override fun onCreate() {
        super.onCreate()
        initDatabase()
        dietTipsRepository = SQLiteDietTipsRepository(database, ioDispatcher)
    }

    private fun initDatabase() {
        database = AppSQLiteHelper(this).writableDatabase
    }

}