package ksu.katara.healthymealplanner.mvvm

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import ksu.katara.healthymealplanner.foundation.BaseApplication
import ksu.katara.healthymealplanner.foundation.model.Repository
import ksu.katara.healthymealplanner.foundation.model.coroutines.IoDispatcher
import ksu.katara.healthymealplanner.mvvm.model.addrecipes.AddRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.addrecipes.InMemoryAddRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.calendar.InMemoryCalendarRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.SQLiteDietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.InMemoryMealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.SQLiteRecipeCategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.SQLiteRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.SQLiteShoppingListRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.ShoppingListRepository
import ksu.katara.healthymealplanner.mvvm.model.sqlite.AppSQLiteHelper

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

    private lateinit var database: SQLiteDatabase

    private lateinit var dietTipsRepository : Repository
    private lateinit var recipeCategoriesRepository: Repository
    private lateinit var recipesRepository: SQLiteRecipesRepository
    private lateinit var mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository
    private lateinit var addRecipesRepository: AddRecipesRepository
    private lateinit var shoppingListRepository: ShoppingListRepository

    private val calendarRepository = InMemoryCalendarRepository()

    private val ioDispatcher = IoDispatcher(Dispatchers.IO)
    private val workerDispatcher = IoDispatcher(Dispatchers.Default)

    /**
     * Place your repositories here
     */
    override val singletonScopeDependencies: List<Any> by lazy {
        listOf(
            dietTipsRepository,
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
        recipeCategoriesRepository = SQLiteRecipeCategoriesRepository(database, ioDispatcher)
        recipesRepository = SQLiteRecipesRepository(database, ioDispatcher)
        mealPlanForDateRecipesRepository = InMemoryMealPlanForDateRecipesRepository(ioDispatcher)
        addRecipesRepository = InMemoryAddRecipesRepository(
            recipesRepository,
            mealPlanForDateRecipesRepository,
            ioDispatcher
        )
        shoppingListRepository =
            SQLiteShoppingListRepository(database, ioDispatcher)
    }

    private fun initDatabase() {
        database = AppSQLiteHelper(this).writableDatabase
    }

}